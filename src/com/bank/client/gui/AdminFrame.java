package com.bank.client.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.DecimalFormat;

import com.bank.client.BankClient;
import com.bank.common.Account;
import com.bank.common.Transaction;

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
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Main panel with modern background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                // Subtle gradient background
                Color color1 = new Color(248, 250, 252);
                Color color2 = new Color(241, 245, 249);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setOpaque(false);

        // Modern header with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int arc = 20;

                // Admin gradient - darker blue
                Color color1 = new Color(21, 101, 192);
                Color color2 = new Color(69, 90, 100);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, arc, arc);

                // Subtle shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(2, 2, w - 4, h - 4, arc, arc);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setOpaque(false);

        // Header content
        JLabel lblTitle = new JLabel("⚙️ QUẢN TRỊ HỆ THỐNG", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblSubtitle = new JLabel("Ngân hàng Mini - Quản lý tài khoản", JLabel.RIGHT);
        lblSubtitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(220, 220, 220));
        headerPanel.add(lblSubtitle, BorderLayout.EAST);

        // Tạo table để hiển thị accounts
        String[] columns = { "Số TK", "Tên chủ TK", "Số dư", "Mật khẩu", "Hành động" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4; // Cho phép edit cột mật khẩu và hành động
            }
        };

        accountsTable = new JTable(tableModel);
        accountsTable.getColumnModel().getColumn(2).setCellRenderer(new RightAlignRenderer());

        // Thêm custom renderer cho cột mật khẩu với biểu tượng con mắt
        accountsTable.getColumnModel().getColumn(3).setCellRenderer(new PasswordRenderer());
        accountsTable.getColumnModel().getColumn(3).setCellEditor(new PasswordEditor());

        // Thêm 6 button vào cột hành động (bỏ nút xem mật khẩu vì đã chuyển vào cột mật
        // khẩu)
        accountsTable.getColumn("Hành động").setCellRenderer(new ButtonRenderer());
        accountsTable.getColumn("Hành động").setCellEditor(new ButtonEditor(new JCheckBox(), client));

        // Modern table styling
        accountsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountsTable.setRowHeight(60);
        accountsTable.setGridColor(new Color(224, 224, 224));
        accountsTable.setSelectionBackground(new Color(25, 118, 210, 50));
        accountsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        accountsTable.getTableHeader().setBackground(new Color(245, 245, 245));
        accountsTable.getTableHeader().setForeground(new Color(33, 33, 33));

        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Modern button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);

        JButton btnRefresh = createModernButton("🔄 Làm mới", new Color(76, 175, 80), Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(150, 45));
        btnRefresh.addActionListener(e -> loadAccountsData());

        JButton btnLogout = createModernButton("🚪 Đăng xuất", new Color(244, 67, 54), Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(150, 45));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame(client);
        });

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnLogout);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
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

    // Custom renderer cho cột mật khẩu với biểu tượng con mắt
    class PasswordRenderer extends JPanel implements TableCellRenderer {
        private JLabel passwordLabel;
        private JLabel eyeLabel;

        public PasswordRenderer() {
            setLayout(new BorderLayout(5, 0));
            setOpaque(true);

            passwordLabel = new JLabel("••••••••");
            passwordLabel.setHorizontalAlignment(JLabel.LEFT);

            eyeLabel = new JLabel("👁");
            eyeLabel.setHorizontalAlignment(JLabel.CENTER);
            eyeLabel.setPreferredSize(new Dimension(30, 20));
            eyeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            add(passwordLabel, BorderLayout.CENTER);
            add(eyeLabel, BorderLayout.EAST);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
                passwordLabel.setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                passwordLabel.setForeground(table.getForeground());
            }

            return this;
        }
    }

    // Custom editor cho cột mật khẩu
    class PasswordEditor extends DefaultCellEditor {
        private JPanel panel;
        private JLabel passwordLabel;
        private JLabel eyeLabel;
        private String accountNumber;
        private boolean passwordVisible = false;

        public PasswordEditor() {
            super(new JCheckBox());

            panel = new JPanel(new BorderLayout(5, 0));
            panel.setOpaque(true);

            passwordLabel = new JLabel("••••••••");
            passwordLabel.setHorizontalAlignment(JLabel.LEFT);

            eyeLabel = new JLabel("👁");
            eyeLabel.setHorizontalAlignment(JLabel.CENTER);
            eyeLabel.setPreferredSize(new Dimension(30, 20));
            eyeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            eyeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    togglePasswordVisibility();
                }
            });

            panel.add(passwordLabel, BorderLayout.CENTER);
            panel.add(eyeLabel, BorderLayout.EAST);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            accountNumber = (String) table.getValueAt(row, 0);
            passwordVisible = false;
            passwordLabel.setText("••••••••");
            eyeLabel.setText("👁");
            return panel;
        }

        public Object getCellEditorValue() {
            return "••••••••";
        }

        private void togglePasswordVisibility() {
            try {
                if (!passwordVisible) {
                    // Hiển thị mật khẩu
                    String password = client.getBankService().getUserPassword(accountNumber);
                    if (password != null) {
                        passwordLabel.setText(password);
                        eyeLabel.setText("🙈");
                        passwordVisible = true;
                    }
                } else {
                    // Ẩn mật khẩu
                    passwordLabel.setText("••••••••");
                    eyeLabel.setText("👁");
                    passwordVisible = false;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(panel, "Lỗi: " + e.getMessage());
            }
        }
    }

    // Custom renderer cho panel 6 buttons
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton depositBtn, withdrawBtn, changePasswordBtn, historyBtn, lockBtn, deleteBtn;

        public ButtonRenderer() {
            setLayout(new GridLayout(2, 3, 2, 2));
            setOpaque(true);

            // Nút Nạp tiền
            depositBtn = new JButton("Add");
            depositBtn.setBackground(new Color(40, 167, 69));
            depositBtn.setForeground(Color.WHITE);
            depositBtn.setFont(new Font("Arial", Font.BOLD, 8));
            depositBtn.setToolTipText("Nạp tiền vào tài khoản");
            depositBtn.setFocusPainted(false);
            depositBtn.setBorderPainted(false);

            // Nút Rút tiền
            withdrawBtn = new JButton("Sub");
            withdrawBtn.setBackground(new Color(220, 53, 69));
            withdrawBtn.setForeground(Color.WHITE);
            withdrawBtn.setFont(new Font("Arial", Font.BOLD, 8));
            withdrawBtn.setToolTipText("Rút tiền từ tài khoản");
            withdrawBtn.setFocusPainted(false);
            withdrawBtn.setBorderPainted(false);

            // Nút Đổi mật khẩu
            changePasswordBtn = new JButton("Pwd");
            changePasswordBtn.setBackground(new Color(0, 123, 255));
            changePasswordBtn.setForeground(Color.WHITE);
            changePasswordBtn.setFont(new Font("Arial", Font.BOLD, 8));
            changePasswordBtn.setToolTipText("Đổi mật khẩu");
            changePasswordBtn.setFocusPainted(false);
            changePasswordBtn.setBorderPainted(false);

            // Nút Xem lịch sử
            historyBtn = new JButton("His");
            historyBtn.setBackground(new Color(108, 117, 125));
            historyBtn.setForeground(Color.WHITE);
            historyBtn.setFont(new Font("Arial", Font.BOLD, 10));
            historyBtn.setToolTipText("Xem lịch sử giao dịch");
            historyBtn.setFocusPainted(false);
            historyBtn.setBorderPainted(false);

            // Nút Khóa/Mở khóa
            lockBtn = new JButton("Lock");
            lockBtn.setBackground(new Color(255, 193, 7));
            lockBtn.setForeground(Color.WHITE);
            lockBtn.setFont(new Font("Arial", Font.BOLD, 10));
            lockBtn.setToolTipText("Khóa tài khoản");
            lockBtn.setFocusPainted(false);
            lockBtn.setBorderPainted(false);

            // Nút Xóa
            deleteBtn = new JButton("Del");
            deleteBtn.setBackground(new Color(220, 53, 69));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 10));
            deleteBtn.setToolTipText("Xóa tài khoản");
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);

            add(depositBtn);
            add(withdrawBtn);
            add(changePasswordBtn);
            add(historyBtn);
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
                lockBtn.setText("Unlock");
                lockBtn.setBackground(new Color(40, 167, 69)); // Green color
                lockBtn.setToolTipText("Mở khóa tài khoản");
            } else {
                // Account is not locked - show LOCK button
                lockBtn.setText("Lock");
                lockBtn.setBackground(new Color(255, 193, 7)); // Yellow color
                lockBtn.setToolTipText("Khóa tài khoản");
            }

            return this;
        }
    }

    // Custom editor cho panel 6 buttons
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton depositBtn, withdrawBtn, changePasswordBtn, historyBtn, lockBtn, deleteBtn;
        private String accountNumber;
        private BankClient client;

        public ButtonEditor(JCheckBox checkBox, BankClient client) {
            super(checkBox);
            this.client = client;

            panel = new JPanel(new GridLayout(2, 3, 2, 2));
            panel.setOpaque(true);

            // Nút Nạp tiền
            depositBtn = new JButton("Add");
            depositBtn.setBackground(new Color(40, 167, 69));
            depositBtn.setForeground(Color.WHITE);
            depositBtn.setFont(new Font("Arial", Font.BOLD, 8));
            depositBtn.setToolTipText("Nạp tiền vào tài khoản");
            depositBtn.setFocusPainted(false);
            depositBtn.setBorderPainted(false);
            depositBtn.addActionListener(e -> {
                fireEditingStopped();
                showDepositDialog(accountNumber);
            });

            // Nút Rút tiền
            withdrawBtn = new JButton("Sub");
            withdrawBtn.setBackground(new Color(220, 53, 69));
            withdrawBtn.setForeground(Color.WHITE);
            withdrawBtn.setFont(new Font("Arial", Font.BOLD, 8));
            withdrawBtn.setToolTipText("Rút tiền từ tài khoản");
            withdrawBtn.setFocusPainted(false);
            withdrawBtn.setBorderPainted(false);
            withdrawBtn.addActionListener(e -> {
                fireEditingStopped();
                showWithdrawDialog(accountNumber);
            });

            // Nút Đổi mật khẩu
            changePasswordBtn = new JButton("Pwd");
            changePasswordBtn.setBackground(new Color(0, 123, 255));
            changePasswordBtn.setForeground(Color.WHITE);
            changePasswordBtn.setFont(new Font("Arial", Font.BOLD, 8));
            changePasswordBtn.setToolTipText("Đổi mật khẩu");
            changePasswordBtn.setFocusPainted(false);
            changePasswordBtn.setBorderPainted(false);
            changePasswordBtn.addActionListener(e -> {
                fireEditingStopped();
                showChangePasswordDialog(accountNumber);
            });

            // Nút Xem lịch sử
            historyBtn = new JButton("His");
            historyBtn.setBackground(new Color(108, 117, 125));
            historyBtn.setForeground(Color.WHITE);
            historyBtn.setFont(new Font("Arial", Font.BOLD, 8));
            historyBtn.setToolTipText("Xem lịch sử giao dịch");
            historyBtn.setFocusPainted(false);
            historyBtn.setBorderPainted(false);
            historyBtn.addActionListener(e -> {
                fireEditingStopped();
                showUserHistoryDialog(accountNumber);
            });

            // Nút Khóa/Mở khóa
            lockBtn = new JButton("Lock");
            lockBtn.setBackground(new Color(255, 193, 7));
            lockBtn.setForeground(Color.WHITE);
            lockBtn.setFont(new Font("Arial", Font.BOLD, 8));
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
            deleteBtn = new JButton("Del");
            deleteBtn.setBackground(new Color(220, 53, 69));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 8));
            deleteBtn.setToolTipText("Xóa tài khoản");
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                showDeleteAccountDialog(accountNumber);
            });

            panel.add(depositBtn);
            panel.add(withdrawBtn);
            panel.add(changePasswordBtn);
            panel.add(historyBtn);
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
                lockBtn.setText("Unlock");
                lockBtn.setBackground(new Color(40, 167, 69)); // Green color
                lockBtn.setToolTipText("Mở khóa tài khoản");
            } else {
                // Account is not locked - show LOCK button
                lockBtn.setText("Lock");
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

        private void showAdjustBalanceDialog(String accountNumber) {
            try {
                Account account = client.getBankService().getAccountInfo(accountNumber);
                if (account == null) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "Không tìm thấy tài khoản!");
                    return;
                }

                JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
                panel.add(new JLabel("Số tài khoản:"));
                JTextField txtAccount = new JTextField(account.getAccountNumber());
                txtAccount.setEditable(false);
                panel.add(txtAccount);

                panel.add(new JLabel("Tên chủ tài khoản:"));
                JTextField txtName = new JTextField(account.getAccountHolder());
                txtName.setEditable(false);
                panel.add(txtName);

                panel.add(new JLabel("Số dư hiện tại:"));
                JTextField txtCurrentBalance = new JTextField(currencyFormat.format(account.getBalance()));
                txtCurrentBalance.setEditable(false);
                panel.add(txtCurrentBalance);

                panel.add(new JLabel("Số tiền điều chỉnh (+/-):"));
                JTextField txtAmount = new JTextField();
                panel.add(txtAmount);

                JPanel reasonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
                reasonPanel.add(new JLabel("Lý do điều chỉnh:"));
                JTextField txtReason = new JTextField();
                reasonPanel.add(txtReason);

                JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                mainPanel.add(panel, BorderLayout.CENTER);
                mainPanel.add(reasonPanel, BorderLayout.SOUTH);

                int result = JOptionPane.showConfirmDialog(AdminFrame.this, mainPanel,
                        "Điều chỉnh số dư tài khoản", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        double amount = Double.parseDouble(txtAmount.getText());
                        String reason = txtReason.getText().trim();

                        if (reason.isEmpty()) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Vui lòng nhập lý do điều chỉnh!");
                            return;
                        }

                        boolean success = client.getBankService().adjustUserBalance(accountNumber, amount, reason);
                        if (success) {
                            double newBalance = account.getBalance() + amount;
                            String operation = amount > 0 ? "+" : "";
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                    "Điều chỉnh số dư thành công!\n" +
                                            "Tài khoản: " + accountNumber + "\n" +
                                            "Số dư cũ: " + currencyFormat.format(account.getBalance()) + "\n" +
                                            "Điều chỉnh: " + operation + currencyFormat.format(amount) + "\n" +
                                            "Số dư mới: " + currencyFormat.format(newBalance) + "\n" +
                                            "Lý do: " + reason);
                            loadAccountsData(); // Refresh data
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Điều chỉnh số dư thất bại!");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Số tiền không hợp lệ!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage());
            }
        }

        private void showChangePasswordDialog(String accountNumber) {
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

                panel.add(new JLabel("Mật khẩu mới:"));
                JPasswordField txtNewPassword = new JPasswordField();
                panel.add(txtNewPassword);

                int result = JOptionPane.showConfirmDialog(AdminFrame.this, panel,
                        "Đổi mật khẩu cho tài khoản", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String newPassword = new String(txtNewPassword.getPassword());
                    if (newPassword.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Mật khẩu không được để trống!");
                        return;
                    }

                    boolean success = client.getBankService().changeUserPassword(accountNumber, newPassword);
                    if (success) {
                        JOptionPane.showMessageDialog(AdminFrame.this,
                                "Đổi mật khẩu thành công!\n" +
                                        "Tài khoản: " + accountNumber + "\n" +
                                        "Mật khẩu mới đã được cập nhật.");
                    } else {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Đổi mật khẩu thất bại!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage());
            }
        }

        private void showUserHistoryDialog(String accountNumber) {
            try {
                List<Transaction> transactions = client.getBankService().getUserTransactionHistory(accountNumber);

                if (transactions == null || transactions.isEmpty()) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "Không có lịch sử giao dịch!");
                    return;
                }

                // Tạo history window mới
                JFrame historyFrame = new JFrame("Lịch sử giao dịch - " + accountNumber);
                historyFrame.setSize(800, 600);
                historyFrame.setLocationRelativeTo(AdminFrame.this);

                String[] columns = { "ID Giao dịch", "Loại", "Số tiền", "Thời gian", "Mô tả" };
                DefaultTableModel historyTableModel = new DefaultTableModel(columns, 0);

                for (Transaction trans : transactions) {
                    Object[] row = {
                            trans.getTransactionId(),
                            trans.getType(),
                            currencyFormat.format(trans.getAmount()),
                            trans.getTimestamp(),
                            trans.getDescription()
                    };
                    historyTableModel.addRow(row);
                }

                JTable historyTable = new JTable(historyTableModel);
                historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                JScrollPane scrollPane = new JScrollPane(historyTable);

                JButton closeBtn = new JButton("Đóng");
                closeBtn.addActionListener(e -> historyFrame.dispose());

                JPanel buttonPanel = new JPanel(new FlowLayout());
                buttonPanel.add(closeBtn);

                historyFrame.add(scrollPane, BorderLayout.CENTER);
                historyFrame.add(buttonPanel, BorderLayout.SOUTH);
                historyFrame.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage());
            }
        }

        private void showDepositDialog(String accountNumber) {
            try {
                Account account = client.getBankService().getAccountInfo(accountNumber);
                if (account == null) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "Không tìm thấy tài khoản!");
                    return;
                }

                JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
                panel.add(new JLabel("Số tài khoản:"));
                JTextField txtAccount = new JTextField(account.getAccountNumber());
                txtAccount.setEditable(false);
                panel.add(txtAccount);

                panel.add(new JLabel("Tên chủ tài khoản:"));
                JTextField txtName = new JTextField(account.getAccountHolder());
                txtName.setEditable(false);
                panel.add(txtName);

                panel.add(new JLabel("Số dư hiện tại:"));
                JTextField txtCurrentBalance = new JTextField(currencyFormat.format(account.getBalance()));
                txtCurrentBalance.setEditable(false);
                panel.add(txtCurrentBalance);

                panel.add(new JLabel("Số tiền nạp (+):"));
                JTextField txtAmount = new JTextField();
                panel.add(txtAmount);

                JPanel reasonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
                reasonPanel.add(new JLabel("Lý do nạp tiền:"));
                JTextField txtReason = new JTextField();
                reasonPanel.add(txtReason);

                JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                mainPanel.add(panel, BorderLayout.CENTER);
                mainPanel.add(reasonPanel, BorderLayout.SOUTH);

                int result = JOptionPane.showConfirmDialog(AdminFrame.this, mainPanel,
                        "Nạp tiền vào tài khoản", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        double amount = Double.parseDouble(txtAmount.getText());
                        String reason = txtReason.getText().trim();

                        if (amount <= 0) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Số tiền nạp phải lớn hơn 0!");
                            return;
                        }

                        if (reason.isEmpty()) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Vui lòng nhập lý do nạp tiền!");
                            return;
                        }

                        boolean success = client.getBankService().adjustUserBalance(accountNumber, amount, reason);
                        if (success) {
                            double newBalance = account.getBalance() + amount;
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                    "Nạp tiền thành công!\n" +
                                            "Tài khoản: " + accountNumber + "\n" +
                                            "Số dư cũ: " + currencyFormat.format(account.getBalance()) + "\n" +
                                            "Số tiền nạp: +" + currencyFormat.format(amount) + "\n" +
                                            "Số dư mới: " + currencyFormat.format(newBalance) + "\n" +
                                            "Lý do: " + reason);
                            loadAccountsData(); // Refresh data
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Nạp tiền thất bại!");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Số tiền không hợp lệ!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage());
            }
        }

        private void showWithdrawDialog(String accountNumber) {
            try {
                Account account = client.getBankService().getAccountInfo(accountNumber);
                if (account == null) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "Không tìm thấy tài khoản!");
                    return;
                }

                JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
                panel.add(new JLabel("Số tài khoản:"));
                JTextField txtAccount = new JTextField(account.getAccountNumber());
                txtAccount.setEditable(false);
                panel.add(txtAccount);

                panel.add(new JLabel("Tên chủ tài khoản:"));
                JTextField txtName = new JTextField(account.getAccountHolder());
                txtName.setEditable(false);
                panel.add(txtName);

                panel.add(new JLabel("Số dư hiện tại:"));
                JTextField txtCurrentBalance = new JTextField(currencyFormat.format(account.getBalance()));
                txtCurrentBalance.setEditable(false);
                panel.add(txtCurrentBalance);

                panel.add(new JLabel("Số tiền rút (-):"));
                JTextField txtAmount = new JTextField();
                panel.add(txtAmount);

                JPanel reasonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
                reasonPanel.add(new JLabel("Lý do rút tiền:"));
                JTextField txtReason = new JTextField();
                reasonPanel.add(txtReason);

                JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                mainPanel.add(panel, BorderLayout.CENTER);
                mainPanel.add(reasonPanel, BorderLayout.SOUTH);

                int result = JOptionPane.showConfirmDialog(AdminFrame.this, mainPanel,
                        "Rút tiền từ tài khoản", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        double amount = Double.parseDouble(txtAmount.getText());
                        String reason = txtReason.getText().trim();

                        if (amount <= 0) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Số tiền rút phải lớn hơn 0!");
                            return;
                        }

                        if (reason.isEmpty()) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Vui lòng nhập lý do rút tiền!");
                            return;
                        }

                        if (amount > account.getBalance()) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Số tiền rút vượt quá số dư hiện tại!");
                            return;
                        }

                        boolean success = client.getBankService().adjustUserBalance(accountNumber, -amount, reason);
                        if (success) {
                            double newBalance = account.getBalance() - amount;
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                    "Rút tiền thành công!\n" +
                                            "Tài khoản: " + accountNumber + "\n" +
                                            "Số dư cũ: " + currencyFormat.format(account.getBalance()) + "\n" +
                                            "Số tiền rút: -" + currencyFormat.format(amount) + "\n" +
                                            "Số dư mới: " + currencyFormat.format(newBalance) + "\n" +
                                            "Lý do: " + reason);
                            loadAccountsData(); // Refresh data
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Rút tiền thất bại!");
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "Số tiền không hợp lệ!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage());
            }
        }

        private void showViewPasswordDialog(String accountNumber) {
            try {
                String password = client.getBankService().getUserPassword(accountNumber);
                if (password == null) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "Không tìm thấy tài khoản!");
                    return;
                }

                Account account = client.getBankService().getAccountInfo(accountNumber);

                JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
                panel.add(new JLabel("Số tài khoản:"));
                JTextField txtAccount = new JTextField(accountNumber);
                txtAccount.setEditable(false);
                panel.add(txtAccount);

                panel.add(new JLabel("Tên chủ tài khoản:"));
                JTextField txtName = new JTextField(account != null ? account.getAccountHolder() : "N/A");
                txtName.setEditable(false);
                panel.add(txtName);

                panel.add(new JLabel("Mật khẩu:"));
                JTextField txtPassword = new JTextField(password);
                txtPassword.setEditable(false);
                txtPassword.setBackground(Color.YELLOW);
                panel.add(txtPassword);

                JOptionPane.showMessageDialog(AdminFrame.this, panel, "Thông tin mật khẩu tài khoản",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(AdminFrame.this, "Lỗi: " + e.getMessage());
            }
        }
    }

    // Phương thức tạo nút modern với rounded corners và hover effect
    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int arc = 15; // Rounded corner radius

                // Background color with hover effect
                boolean hovered = Boolean.TRUE.equals(getClientProperty("hovered"));
                Color background = hovered ? bgColor.darker() : bgColor;
                g2d.setColor(background);
                g2d.fillRoundRect(0, 0, w, h, arc, arc);

                // Subtle border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                // Soft shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, w - 4, h - 4, arc, arc);

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.putClientProperty("hovered", true);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.putClientProperty("hovered", false);
                btn.repaint();
            }
        });

        return button;
    }
}