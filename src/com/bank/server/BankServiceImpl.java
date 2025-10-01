package com.bank.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bank.common.Account;
import com.bank.common.Transaction;
import com.bank.rmi.BankService;

public class BankServiceImpl extends UnicastRemoteObject implements BankService {
    private static final long serialVersionUID = 1L;

    // Cache for account information
    private Map<String, Account> accountCache = new HashMap<>();
    private Map<String, Long> cacheTimestamps = new HashMap<>();
    private static final long CACHE_TIMEOUT = 30000; // 30 seconds

    // Session management for tracking active clients
    private Map<String, List<String>> activeSessions = new HashMap<>(); // accountNumber -> list of sessionIds

    protected BankServiceImpl() throws RemoteException {
        super();
        testDatabaseConnection();
        DatabaseConnection.updateSchemaForAccountLocking(); // Update database schema
        initializeAdminAccount();
    }

    private void testDatabaseConnection() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Kết nối database thành công");

                // Kiểm tra tables tồn tại
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet tables = meta.getTables(null, null, "accounts", new String[] { "TABLE" });
                if (tables.next()) {
                    System.out.println("Bảng accounts đã tồn tại");
                } else {
                    System.out.println("Bảng accounts không tồn tại");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
        }
    }

    private void initializeAdminAccount() {
        try {
            // Chỉ tạo admin account nếu chưa tồn tại
            if (!accountExists("admin")) {
                Account adminAccount = new Account("admin", "Administrator", 0, "admin123");
                createAccount(adminAccount);
                System.out.println("Đã tạo tài khoản admin");
            }

            System.out.println("Khởi tạo admin account thành công");

        } catch (RemoteException e) {
            System.err.println("Lỗi khởi tạo admin account: " + e.getMessage());
        }
    }

    @Override
    public boolean createAccount(Account account) throws RemoteException {
        String sql = "INSERT INTO accounts (account_number, account_holder, balance, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getAccountHolder());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setString(4, account.getPassword());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi tạo tài khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Account login(String accountNumber, String password) throws RemoteException {
        String sql = "SELECT * FROM accounts WHERE account_number = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Account account = new Account(
                        rs.getString("account_number"),
                        rs.getString("account_holder"),
                        rs.getDouble("balance"),
                        rs.getString("password"));

                // Check if account columns exist and set lock status
                try {
                    boolean isLocked = rs.getBoolean("is_locked");
                    String lockReason = rs.getString("lock_reason");
                    account.setLocked(isLocked);
                    account.setLockReason(lockReason);
                } catch (SQLException e) {
                    // Columns don't exist yet, set defaults
                    account.setLocked(false);
                    account.setLockReason(null);
                }

                return account;
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
            return null;
        }
    }

    @Override
    public double checkBalance(String accountNumber) throws RemoteException {
        // Kiểm tra trạng thái tài khoản trước khi cho phép xem số dư
        if (!validateAccountStatus(accountNumber)) {
            System.out.println("CheckBalance bị từ chối: Tài khoản " + accountNumber + " đã bị khóa");
            throw new RemoteException("Tài khoản đã bị khóa. Vui lòng liên hệ ngân hàng.");
        }

        String sql = "SELECT balance FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra số dư: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public Account getAccountInfo(String accountNumber) throws RemoteException {
        // Kiểm tra cache trước
        Long timestamp = cacheTimestamps.get(accountNumber);
        if (timestamp != null && System.currentTimeMillis() - timestamp < CACHE_TIMEOUT) {
            return accountCache.get(accountNumber);
        }

        String sql = "SELECT account_number, account_holder, balance FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Chỉ trả về thông tin cơ bản, không trả về password
                Account account = new Account();
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountHolder(rs.getString("account_holder"));
                account.setBalance(rs.getDouble("balance"));

                // Lưu vào cache
                accountCache.put(accountNumber, account);
                cacheTimestamps.put(accountNumber, System.currentTimeMillis());

                return account;
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Lỗi lấy thông tin tài khoản: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException {
        // Kiểm tra trạng thái tài khoản trước khi thực hiện giao dịch
        if (!validateAccountStatus(fromAccount)) {
            System.out.println("Transfer bị từ chối: Tài khoản " + fromAccount + " đã bị khóa");
            return false;
        }

        if (amount <= 0)
            return false;
        if (fromAccount.equals(toAccount))
            return false;

        String checkAccountsSQL = "SELECT account_number FROM accounts WHERE account_number IN (?, ?)";
        String checkBalanceSQL = "SELECT balance FROM accounts WHERE account_number = ? FOR UPDATE";
        String updateBalanceSQL = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String insertTransactionSQL = "INSERT INTO transactions (transaction_id, account_number, type, amount, timestamp, description) VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Kiểm tra tài khoản tồn tại
            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkAccountsSQL)) {
                pstmtCheck.setString(1, fromAccount);
                pstmtCheck.setString(2, toAccount);
                ResultSet rs = pstmtCheck.executeQuery();

                int count = 0;
                while (rs.next())
                    count++;
                if (count != 2) {
                    System.err.println("Một trong hai tài khoản không tồn tại: " + fromAccount + ", " + toAccount);
                    conn.rollback();
                    return false;
                }
            }

            // Kiểm tra số dư TRONG TRANSACTION với FOR UPDATE
            double currentBalance = 0;
            try (PreparedStatement pstmtBalance = conn.prepareStatement(checkBalanceSQL)) {
                pstmtBalance.setString(1, fromAccount);
                ResultSet rs = pstmtBalance.executeQuery();

                if (rs.next()) {
                    currentBalance = rs.getDouble("balance");
                    System.out.println("Số dư thực tế của " + fromAccount + ": " + currentBalance);
                    System.out.println("Số tiền muốn chuyển: " + amount);
                }

                if (currentBalance < amount) {
                    System.err.println("Số dư không đủ: " + currentBalance + " < " + amount);
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement pstmtWithdraw = conn.prepareStatement(updateBalanceSQL);
                    PreparedStatement pstmtDeposit = conn.prepareStatement(updateBalanceSQL);
                    PreparedStatement pstmtTrans1 = conn.prepareStatement(insertTransactionSQL);
                    PreparedStatement pstmtTrans2 = conn.prepareStatement(insertTransactionSQL)) {

                // Rút tiền từ tài khoản nguồn (số âm)
                pstmtWithdraw.setDouble(1, -amount);
                pstmtWithdraw.setString(2, fromAccount);
                int withdrawResult = pstmtWithdraw.executeUpdate();

                if (withdrawResult == 0) {
                    System.err.println("Lỗi khi rút tiền từ tài khoản " + fromAccount);
                    conn.rollback();
                    return false;
                }

                // Gửi tiền vào tài khoản đích (số dương)
                pstmtDeposit.setDouble(1, amount);
                pstmtDeposit.setString(2, toAccount);
                int depositResult = pstmtDeposit.executeUpdate();

                if (depositResult == 0) {
                    System.err.println("Lỗi khi gửi tiền vào tài khoản " + toAccount);
                    conn.rollback();
                    return false;
                }

                // Ghi giao dịch cho tài khoản nguồn
                String transId1 = UUID.randomUUID().toString();
                pstmtTrans1.setString(1, transId1);
                pstmtTrans1.setString(2, fromAccount);
                pstmtTrans1.setString(3, "TRANSFER_OUT");
                pstmtTrans1.setDouble(4, amount);
                pstmtTrans1.setString(5, "Chuyển tiền đến tài khoản " + toAccount);
                pstmtTrans1.executeUpdate();

                // Ghi giao dịch cho tài khoản đích
                String transId2 = UUID.randomUUID().toString();
                pstmtTrans2.setString(1, transId2);
                pstmtTrans2.setString(2, toAccount);
                pstmtTrans2.setString(3, "TRANSFER_IN");
                pstmtTrans2.setDouble(4, amount);
                pstmtTrans2.setString(5, "Chuyển tiền từ tài khoản " + fromAccount);
                pstmtTrans2.executeUpdate();

                conn.commit();
                System.out.println("Chuyển khoản thành công: " + fromAccount + " -> " + toAccount + " : " + amount);

                // Clear cache sau khi chuyển khoản thành công
                accountCache.remove(fromAccount);
                accountCache.remove(toAccount);
                cacheTimestamps.remove(fromAccount);
                cacheTimestamps.remove(toAccount);

                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi trong quá trình chuyển khoản: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi chuyển khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) throws RemoteException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("transaction_id"),
                        rs.getString("account_number"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("description"));
                transaction.setTimestamp(rs.getTimestamp("timestamp"));
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy lịch sử giao dịch: " + e.getMessage());
        }

        return transactions;
    }

    @Override
    public boolean updateAccount(Account account) throws RemoteException {
        String sql = "UPDATE accounts SET account_holder = ?, password = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountHolder());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getAccountNumber());

            int rowsAffected = pstmt.executeUpdate();

            // Clear cache sau khi cập nhật
            accountCache.remove(account.getAccountNumber());
            cacheTimestamps.remove(account.getAccountNumber());

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật tài khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAccount(String accountNumber) throws RemoteException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            int rowsAffected = pstmt.executeUpdate();

            // Clear cache sau khi xóa
            accountCache.remove(accountNumber);
            cacheTimestamps.remove(accountNumber);

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi xóa tài khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Account> getAllAccounts() throws RemoteException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Account account = new Account(
                        rs.getString("account_number"),
                        rs.getString("account_holder"),
                        rs.getDouble("balance"),
                        rs.getString("password"));

                // Check if account lock columns exist and set lock status
                try {
                    boolean isLocked = rs.getBoolean("is_locked");
                    String lockReason = rs.getString("lock_reason");
                    account.setLocked(isLocked);
                    account.setLockReason(lockReason);
                } catch (SQLException e) {
                    // Columns don't exist yet, set defaults
                    account.setLocked(false);
                    account.setLockReason(null);
                }

                accounts.add(account);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách tài khoản: " + e.getMessage());
        }

        return accounts;
    }

    @Override
    public boolean accountExists(String accountNumber) throws RemoteException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra tài khoản tồn tại: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setAccountBalance(String accountNumber, double newBalance) throws RemoteException {
        if (newBalance < 0) {
            System.err.println("Số dư không thể âm: " + newBalance);
            return false;
        }

        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        String insertTransactionSQL = "INSERT INTO transactions (transaction_id, account_number, type, amount, timestamp, description) VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                    PreparedStatement pstmtTrans = conn.prepareStatement(insertTransactionSQL)) {

                // Cập nhật số dư
                pstmt.setDouble(1, newBalance);
                pstmt.setString(2, accountNumber);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    System.err.println("Không tìm thấy tài khoản: " + accountNumber);
                    conn.rollback();
                    return false;
                }

                // Ghi log giao dịch admin
                String transactionId = UUID.randomUUID().toString();
                pstmtTrans.setString(1, transactionId);
                pstmtTrans.setString(2, accountNumber);
                pstmtTrans.setString(3, "ADMIN_SET_BALANCE");
                pstmtTrans.setDouble(4, newBalance);
                pstmtTrans.setString(5, "Admin set số dư mới");
                pstmtTrans.executeUpdate();

                conn.commit();
                System.out.println("Admin đã set số dư tài khoản " + accountNumber + " thành: " + newBalance);

                // Clear cache
                accountCache.remove(accountNumber);
                cacheTimestamps.remove(accountNumber);

                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi set số dư: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database khi set số dư: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean lockAccount(String accountNumber, String reason) throws RemoteException {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Kiểm tra tài khoản có tồn tại không
            if (!accountExists(accountNumber)) {
                return false;
            }

            conn.setAutoCommit(false);

            try {
                String sql = "UPDATE accounts SET is_locked = 1, lock_reason = ? WHERE account_number = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, reason);
                pstmt.setString(2, accountNumber);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();

                    // Clear cache
                    accountCache.remove(accountNumber);
                    cacheTimestamps.remove(accountNumber);

                    System.out.println("Đã khóa tài khoản: " + accountNumber + " với lý do: " + reason);
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi khóa tài khoản: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database khi khóa tài khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unlockAccount(String accountNumber) throws RemoteException {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Kiểm tra tài khoản có tồn tại không
            if (!accountExists(accountNumber)) {
                return false;
            }

            conn.setAutoCommit(false);

            try {
                String sql = "UPDATE accounts SET is_locked = 0, lock_reason = NULL WHERE account_number = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, accountNumber);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();

                    // Clear cache
                    accountCache.remove(accountNumber);
                    cacheTimestamps.remove(accountNumber);

                    System.out.println("Đã mở khóa tài khoản: " + accountNumber);
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi mở khóa tài khoản: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database khi mở khóa tài khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean changeUserPassword(String accountNumber, String newPassword) throws RemoteException {
        String sql = "UPDATE accounts SET password = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, accountNumber);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Admin đã đổi mật khẩu cho tài khoản: " + accountNumber);
                return true;
            } else {
                System.err.println("Không tìm thấy tài khoản: " + accountNumber);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi đổi mật khẩu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Transaction> getUserTransactionHistory(String accountNumber) throws RemoteException {
        return getTransactionHistory(accountNumber);
    }

    @Override
    public boolean adjustUserBalance(String accountNumber, double amount, String reason) throws RemoteException {
        String checkBalanceSQL = "SELECT balance FROM accounts WHERE account_number = ?";
        String updateBalanceSQL = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String insertTransactionSQL = "INSERT INTO transactions (transaction_id, account_number, type, amount, timestamp, description) VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Kiểm tra tài khoản tồn tại và lấy số dư hiện tại
                PreparedStatement checkStmt = conn.prepareStatement(checkBalanceSQL);
                checkStmt.setString(1, accountNumber);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.err.println("Không tìm thấy tài khoản: " + accountNumber);
                    conn.rollback();
                    return false;
                }

                double currentBalance = rs.getDouble("balance");
                double newBalance = currentBalance + amount;

                // Kiểm tra số dư sau khi điều chỉnh không được âm
                if (newBalance < 0) {
                    System.err.println("Không thể điều chỉnh: số dư sẽ âm (" + newBalance + ")");
                    conn.rollback();
                    return false;
                }

                // Cập nhật số dư
                PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL);
                updateStmt.setDouble(1, amount);
                updateStmt.setString(2, accountNumber);
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }

                // Ghi log giao dịch
                String transactionId = UUID.randomUUID().toString();
                PreparedStatement transStmt = conn.prepareStatement(insertTransactionSQL);
                transStmt.setString(1, transactionId);
                transStmt.setString(2, accountNumber);
                transStmt.setString(3, amount > 0 ? "ADMIN_CREDIT" : "ADMIN_DEBIT");
                transStmt.setDouble(4, Math.abs(amount));
                transStmt.setString(5, "Admin điều chỉnh số dư: " + reason);
                transStmt.executeUpdate();

                conn.commit();

                String operation = amount > 0 ? "+" : "";
                System.out.println("Admin đã điều chỉnh tài khoản " + accountNumber + ": " + operation + amount
                        + " VND. Lý do: " + reason);

                // Clear cache
                accountCache.remove(accountNumber);
                cacheTimestamps.remove(accountNumber);

                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi điều chỉnh số dư: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getUserPassword(String accountNumber) throws RemoteException {
        String sql = "SELECT password FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy mật khẩu: " + e.getMessage());
            return null;
        }
    }

    // ===== SESSION MANAGEMENT AND ACCOUNT STATUS METHODS =====

    @Override
    public boolean isAccountActive(String accountNumber) throws RemoteException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT is_locked FROM accounts WHERE account_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, accountNumber);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return !rs.getBoolean("is_locked"); // Active nếu không bị khóa
                }
                return false; // Tài khoản không tồn tại
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra trạng thái tài khoản: " + e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized void registerActiveSession(String accountNumber, String sessionId) throws RemoteException {
        activeSessions.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(sessionId);
        System.out.println("Đã đăng ký session " + sessionId + " cho tài khoản " + accountNumber);
    }

    @Override
    public synchronized void unregisterActiveSession(String accountNumber, String sessionId) throws RemoteException {
        List<String> sessions = activeSessions.get(accountNumber);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                activeSessions.remove(accountNumber);
            }
        }
        System.out.println("Đã hủy đăng ký session " + sessionId + " cho tài khoản " + accountNumber);
    }

    // Utility method để kiểm tra account status trước mọi operation quan trọng
    private boolean validateAccountStatus(String accountNumber) throws RemoteException {
        if (!isAccountActive(accountNumber)) {
            System.out.println("Tài khoản " + accountNumber + " đã bị khóa, từ chối thao tác");
            return false;
        }
        return true;
    }

    // Phương thức để đóng kết nối khi tắt server
    public void shutdown() {
        System.out.println("Đang tắt server...");
        DatabaseConnection.closeConnection();
    }
}