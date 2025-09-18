package com.bank.client.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.DecimalFormat;

import com.bank.client.BankClient;
import com.bank.common.Account;

public class AdminFrame extends JFrame {
    private BankClient client;
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private DecimalFormat currencyFormat;
    private Map<String, Account> accountsMap; // Map để lưu Account objects

    public AdminFrame(BankClient client) {
        this.client = client;
        this.currencyFormat = new DecimalFormat("#,### VND");
        this.accountsMap = new HashMap<>();
        initializeUI();
        loadAccountsData();
    }

    private void initializeUI() {
        setTitle("Quản trị hệ thống - Ngân hàng Mini");
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel lblTitle = new JLabel("QUẢN TRỊ HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));

        // Tạo table để hiển thị accounts
        String[] columns = { "Số TK", "Tên chủ TK", "Số dư", "Mật khẩu", "Hành động" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Chỉ cho phép edit cột hành động
            }
        };

        accountsTable = new JTable(tableModel);
        accountsTable.getColumnModel().getColumn(2).setCellRenderer(new RightAlignRenderer());

        // Thêm 3 button vào cột hành động
        accountsTable.getColumn("Hành động").setCellRenderer(new ButtonRenderer());
        accountsTable.getColumn("Hành động").setCellEditor(new ButtonEditor(new JCheckBox(), client));

        // Tăng chiều rộng cột hành động để chứa 3 nút
        accountsTable.getColumn("Hành động").setPreferredWidth(150);
        accountsTable.getColumn("Hành động").setMinWidth(150);

        // Tăng chiều cao row để chứa 3 nút
        accountsTable.setRowHeight(50);

        JScrollPane scrollPane = new JScrollPane(accountsTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadAccountsData());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame(client);
        });

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnLogout);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void loadAccountsData() {
        try {
            List<Account> accounts = client.getBankService().getAllAccounts();
            tableModel.setRowCount(0);
            accountsMap.clear(); // Clear existing map

            for (Account account : accounts) {
                // Lọc bỏ tài khoản admin khỏi danh sách
                if ("admin".equals(account.getAccountNumber())) {
                    continue; // Bỏ qua tài khoản admin
                }

                // Store account in map for later reference
                accountsMap.put(account.getAccountNumber(), account);

                Object[] row = {
                        account.getAccountNumber(),
                        account.getAccountHolder(),
                        currencyFormat.format(account.getBalance()),
                        "••••••••", // Ẩn mật khẩu
                        "Set tiền" // Button text
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // Custom renderer để căn phải số dư
    class RightAlignRenderer extends DefaultTableCellRenderer {
        public RightAlignRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }
    }

    // Custom renderer cho panel 3 buttons
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton setMoneyBtn, lockBtn, deleteBtn;

        public ButtonRenderer() {
            setLayout(new GridLayout(1, 3, 2, 0));
            setOpaque(true);

            // Nút Set tiền với icon đô
            setMoneyBtn = new JButton("$");
            setMoneyBtn.setBackground(new Color(40, 167, 69));
            setMoneyBtn.setForeground(Color.WHITE);
            setMoneyBtn.setFont(new Font("Arial", Font.BOLD, 16));
            setMoneyBtn.setToolTipText("Set tiền");
            setMoneyBtn.setFocusPainted(false);
            setMoneyBtn.setBorderPainted(false);

            // Nút Khóa
            lockBtn = new JButton("LOCK");
            lockBtn.setBackground(new Color(255, 193, 7));
            lockBtn.setForeground(Color.WHITE);
            lockBtn.setFont(new Font("Arial", Font.BOLD, 10));
            lockBtn.setToolTipText("Khóa tài khoản");
            lockBtn.setFocusPainted(false);
            lockBtn.setBorderPainted(false);

            // Nút Xóa
            deleteBtn = new JButton("DEL");
            deleteBtn.setBackground(new Color(220, 53, 69));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 10));
            deleteBtn.setToolTipText("Xóa tài khoản");
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);

            add(setMoneyBtn);
            add(lockBtn);
            add(deleteBtn);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // Get account number from the first column
            String accountNumber = (String) table.getValueAt(row, 0);
            Account account = accountsMap.get(accountNumber);

            if (account != null && account.isLocked()) {
                // Account is locked - show UNLOCK button
                lockBtn.setText("UNLOCK");
                lockBtn.setBackground(new Color(40, 167, 69)); // Green color
                lockBtn.setToolTipText("Mở khóa tài khoản");
            } else {
                // Account is not locked - show LOCK button
                lockBtn.setText("LOCK");
                lockBtn.setBackground(new Color(255, 193, 7)); // Yellow color
                lockBtn.setToolTipText("Khóa tài khoản");
            }

            return this;
        }
    }

    // Custom editor cho panel 3 buttons
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton setMoneyBtn, lockBtn, deleteBtn;
        private String accountNumber;
        private BankClient client;

        public ButtonEditor(JCheckBox checkBox, BankClient client) {
            super(checkBox);
            this.client = client;

            panel = new JPanel(new GridLayout(1, 3, 2, 0));
            panel.setOpaque(true);

            // Nút Set tiền với icon đô
            setMoneyBtn = new JButton("$");
            setMoneyBtn.setBackground(new Color(40, 167, 69));
            setMoneyBtn.setForeground(Color.WHITE);
            setMoneyBtn.setFont(new Font("Arial", Font.BOLD, 16));
            setMoneyBtn.setToolTipText("Set tiền");
            setMoneyBtn.setFocusPainted(false);
            setMoneyBtn.setBorderPainted(false);
            setMoneyBtn.addActionListener(e -> {
                fireEditingStopped();
                showSetBalanceDialog(accountNumber);
            });

            // Nút Khóa
            lockBtn = new JButton("LOCK");
            lockBtn.setBackground(new Color(255, 193, 7));
            lockBtn.setForeground(Color.WHITE);
            lockBtn.setFont(new Font("Arial", Font.BOLD, 10));
            lockBtn.setToolTipText("Khóa tài khoản");
            lockBtn.setFocusPainted(false);
            lockBtn.setBorderPainted(false);
            lockBtn.addActionListener(e -> {
                fireEditingStopped();
                Account account = accountsMap.get(accountNumber);
                if (account != null && account.isLocked()) {
                    // Account is locked - unlock it
                    showUnlockAccountDialog(accountNumber);
                } else {
                    // Account is not locked - lock it
                    showLockAccountDialog(accountNumber);
                }
            });

            // Nút Xóa
            deleteBtn = new JButton("DEL");
            deleteBtn.setBackground(new Color(220, 53, 69));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 10));
            deleteBtn.setToolTipText("Xóa tài khoản");
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                showDeleteAccountDialog(accountNumber);
            });

            panel.add(setMoneyBtn);
            panel.add(lockBtn);
            panel.add(deleteBtn);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            accountNumber = (String) table.getValueAt(row, 0);

            // Update lock button based on account status
            Account account = accountsMap.get(accountNumber);
            if (account != null && account.isLocked()) {
                // Account is locked - show UNLOCK button
                lockBtn.setText("UNLOCK");
                lockBtn.setBackground(new Color(40, 167, 69)); // Green color
                lockBtn.setToolTipText("Mở khóa tài khoản");
            } else {
                // Account is not locked - show LOCK button
                lockBtn.setText("LOCK");
                lockBtn.setBackground(new Color(255, 193, 7)); // Yellow color
                lockBtn.setToolTipText("Khóa tài khoản");
            }

            return panel;
        }

        public Object getCellEditorValue() {
            return "Actions";
        }

        private void showLockAccountDialog(String accountNumber) {
            // Yêu cầu lý do khóa
            String reason = JOptionPane.showInputDialog(AdminFrame.this,
                    "Nhập lý do khóa tài khoản " + accountNumber + ":",
                    "Khóa tài khoản",
                    JOptionPane.WARNING_MESSAGE);

            if (reason != null && !reason.trim().isEmpty()) {
                int result = JOptionPane.showConfirmDialog(AdminFrame.this,
                        "Bạn có chắc muốn khóa tài khoản " + accountNumber + "?\n" +
                                "Lý do: " + reason,
                        "Xác nhận khóa tài khoản",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    try {
                        if (client.getBankService().lockAccount(accountNumber, reason)) {
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                    "Tài khoản " + accountNumber + " đã được khóa thành công!\nLý do: " + reason,
                                    "Khóa tài khoản thành công",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadAccountsData(); // Refresh table
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                    "Khóa tài khoản thất bại!",
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage(), "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        private void showUnlockAccountDialog(String accountNumber) {
            int result = JOptionPane.showConfirmDialog(AdminFrame.this,
                    "Bạn có chắc muốn mở khóa tài khoản " + accountNumber + "?",
                    "Xác nhận mở khóa tài khoản",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                try {
                    if (client.getBankService().unlockAccount(accountNumber)) {
                        JOptionPane.showMessageDialog(AdminFrame.this,
                                "Tài khoản " + accountNumber + " đã được mở khóa thành công!",
                                "Mở khóa tài khoản thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadAccountsData(); // Refresh table
                    } else {
                        JOptionPane.showMessageDialog(AdminFrame.this,
                                "Mở khóa tài khoản thất bại!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void showDeleteAccountDialog(String accountNumber) {
            // Yêu cầu lý do xóa
            String reason = JOptionPane.showInputDialog(AdminFrame.this,
                    "Nhập lý do xóa tài khoản " + accountNumber + ":",
                    "Xóa tài khoản",
                    JOptionPane.ERROR_MESSAGE);

            if (reason != null && !reason.trim().isEmpty()) {
                int result = JOptionPane.showConfirmDialog(AdminFrame.this,
                        "⚠️ CẢNH BÁO: Bạn có chắc muốn XÓA VĨNH VIỄN tài khoản " + accountNumber + "?\n" +
                                "Hành động này KHÔNG THỂ HOÀN TÁC!\n" +
                                "Lý do: " + reason,
                        "Xác nhận xóa tài khoản",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    // Xác nhận lần 2
                    String confirm = JOptionPane.showInputDialog(AdminFrame.this,
                            "Để xác nhận xóa tài khoản, vui lòng nhập: DELETE",
                            "Xác nhận cuối cùng",
                            JOptionPane.WARNING_MESSAGE);

                    if ("DELETE".equals(confirm)) {
                        try {
                            if (client.getBankService().deleteAccount(accountNumber)) {
                                JOptionPane.showMessageDialog(AdminFrame.this,
                                        "Tài khoản " + accountNumber + " đã được xóa thành công!\nLý do: " + reason,
                                        "Xóa tài khoản thành công",
                                        JOptionPane.INFORMATION_MESSAGE);
                                loadAccountsData(); // Refresh table
                            } else {
                                JOptionPane.showMessageDialog(AdminFrame.this,
                                        "Xóa tài khoản thất bại!",
                                        "Lỗi",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage(), "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (confirm != null) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Xác nhận không đúng. Hủy bỏ xóa tài khoản.",
                                "Hủy bỏ", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }

        private void showSetBalanceDialog(String accountNumber) {
            try {
                Account account = client.getBankService().getAccountInfo(accountNumber);
                if (account == null) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "Không tìm thấy tài khoản!");
                    return;
                }

                JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
                panel.add(new JLabel("Số tài khoản:"));
                JTextField txtAccount = new JTextField(account.getAccountNumber());
                txtAccount.setEditable(false);
                panel.add(txtAccount);

                panel.add(new JLabel("Tên chủ tài khoản:"));
                JTextField txtName = new JTextField(account.getAccountHolder());
                txtName.setEditable(false);
                panel.add(txtName);

                panel.add(new JLabel("Số dư mới (VND):"));
                JTextField txtBalance = new JTextField(String.valueOf(account.getBalance()));
                panel.add(txtBalance);

                int result = JOptionPane.showConfirmDialog(AdminFrame.this, panel,
                        "Set số dư cho tài khoản", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        double newBalance = Double.parseDouble(txtBalance.getText());
                        if (newBalance < 0) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Số dư không thể âm!");
                            return;
                        }

                        boolean success = client.getBankService().setAccountBalance(accountNumber, newBalance);
                        if (success) {
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                    "Đã set số dư thành công!\n" +
                                            "Tài khoản: " + accountNumber + "\n" +
                                            "Số dư mới: " + currencyFormat.format(newBalance));
                            loadAccountsData(); // Refresh data
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Set số dư thất bại!");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Số dư không hợp lệ!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage());
            }
        }
    }
}