package com.bank.client.gui;

import java.awt.*;
import javax.swing.*;
import java.text.DecimalFormat;

import com.bank.client.BankClient;
import com.bank.common.Account;

public class MainFrame extends JFrame {
    private BankClient client;
    private Account currentAccount;
    private JLabel lblBalance;
    private boolean balanceVisible = false;
    private DecimalFormat currencyFormat;
    private JButton menuButton; // Field để lưu reference nút hamburger
    private JPanel sidePanel; // Panel trượt từ bên trái
    private JPanel overlayPanel; // Panel overlay để chặn tương tác background
    private boolean sideMenuOpen = false; // Trạng thái menu
    private Timer accountStatusTimer; // Timer để kiểm tra trạng thái tài khoản

    public MainFrame(BankClient client, Account account) {
        this.client = client;
        this.currentAccount = account;
        this.currencyFormat = new DecimalFormat("#,### VND");
        initializeUI();
        startAccountStatusMonitoring();
    }

    private void initializeUI() {
        setTitle("MiniBank - " + currentAccount.getAccountHolder());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(400, 700);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 248, 252)); // Light blue-gray background

        // ===== HEADER với gradient xanh ngân hàng =====
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(64, 150, 255); // Blue nhẹ hơn
                Color color2 = new Color(30, 120, 220); // Dark blue nhẹ hơn
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        headerPanel.setPreferredSize(new Dimension(400, 250)); // Tăng chiều cao để chứa balance card

        // Top section với hamburger menu
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        // Menu hamburger ở góc trái trên header
        menuButton = new JButton("⚙"); // Icon bánh răng
        menuButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18)); // Font hỗ trợ symbol tốt
        menuButton.setPreferredSize(new Dimension(45, 35));
        menuButton.setBackground(new Color(64, 150, 255)); // Nền xanh nhẹ hơn
        menuButton.setForeground(Color.WHITE); // Icon màu trắng để nổi bật trên nền xanh
        menuButton.setFocusPainted(false);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.setBorder(null); // Bỏ hoàn toàn border để chỉ có icon
        menuButton.setContentAreaFilled(false); // Bỏ background default
        menuButton.setOpaque(false); // Trong suốt để hòa với header
        menuButton.addActionListener(e -> {
            if (sideMenuOpen) {
                closeSideMenu(); // Nếu menu đang mở thì đóng lại
            } else {
                showMenu(); // Nếu menu đang đóng thì mở ra
            }
        });

        topSection.add(menuButton, BorderLayout.WEST);

        // Center content panel
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        // Welcome label với font hiện đại
        JLabel lblWelcome = new JLabel("Xin chào", JLabel.CENTER);
        lblWelcome.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên người dùng nổi bật
        JLabel lblUserName = new JLabel(currentAccount.getAccountHolder(), JLabel.CENTER);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Số tài khoản đầy đủ (không che)
        JLabel lblAccountNumber = new JLabel("Số tài khoản: " + currentAccount.getAccountNumber(), JLabel.CENTER);
        lblAccountNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAccountNumber.setForeground(new Color(220, 220, 220));
        lblAccountNumber.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Balance panel trong suốt hòa với header
        JPanel balanceCard = new JPanel();
        balanceCard.setOpaque(false); // Trong suốt để hòa với header gradient
        balanceCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Chỉ có padding, không có border
        balanceCard.setMaximumSize(new Dimension(350, 70)); // Tăng kích thước
        balanceCard.setPreferredSize(new Dimension(350, 70)); // Thêm preferred size
        balanceCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceCard.setLayout(new BorderLayout());

        lblBalance = new JLabel("Số dư: *******", JLabel.CENTER);
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Tăng font size
        lblBalance.setForeground(Color.WHITE); // Chữ trắng để nổi bật trên nền xanh gradient

        JButton btnToggleBalance = new JButton("👁️"); // Thay bằng kính lúp để tránh lỗi font
        btnToggleBalance.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); // Tăng font size
        btnToggleBalance.setPreferredSize(new Dimension(40, 35)); // Tăng kích thước
        btnToggleBalance.setBackground(new Color(255, 255, 255, 40)); // Nền trắng nhẹ trong suốt
        btnToggleBalance.setForeground(Color.WHITE);
        btnToggleBalance.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1)); // Border trắng nhẹ
        btnToggleBalance.setFocusPainted(false);
        btnToggleBalance.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleBalance.addActionListener(e -> toggleBalanceVisibility());

        balanceCard.add(lblBalance, BorderLayout.CENTER);
        balanceCard.add(btnToggleBalance, BorderLayout.EAST);

        // Thêm components vào center content
        centerContent.add(lblWelcome);
        centerContent.add(Box.createRigidArea(new Dimension(0, 8)));
        centerContent.add(lblUserName);
        centerContent.add(Box.createRigidArea(new Dimension(0, 12)));
        centerContent.add(lblAccountNumber);
        centerContent.add(Box.createRigidArea(new Dimension(0, 20)));
        centerContent.add(balanceCard);

        // Assembly header
        headerPanel.add(topSection, BorderLayout.NORTH);
        headerPanel.add(centerContent, BorderLayout.CENTER);

        // ===== BUTTON PANEL với thiết kế hiện đại (không có hamburger) =====
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main buttons với card design
        JPanel mainButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        mainButtonsPanel.setOpaque(false);

        // Chuyển khoản button - Banking blue theme
        JButton btnTransfer = createModernButton("💸", "Chuyển khoản", new Color(64, 150, 255), Color.WHITE);
        btnTransfer.addActionListener(e -> handleButtonClick(0));

        // Lịch sử button - Teal theme
        JButton btnHistory = createModernButton("📊", "Lịch sử giao dịch", new Color(0, 150, 136), Color.WHITE);
        btnHistory.addActionListener(e -> handleButtonClick(1));

        mainButtonsPanel.add(btnTransfer);
        mainButtonsPanel.add(btnHistory);

        // Placeholder area với modern card design
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setOpaque(true);
        placeholderPanel.setBackground(Color.WHITE);
        placeholderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(40, 20, 40, 20)));
        placeholderPanel.setLayout(new BorderLayout());

        JLabel placeholderLabel = new JLabel(
                "<html><center><span style='color: #9E9E9E;'>🔧</span><br><br><span style='color: #757575;'>Các tính năng khác<br>đang được phát triển</span></center></html>");
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderPanel.add(placeholderLabel, BorderLayout.CENTER);

        // Layout assembly (chỉ có main buttons, không có top panel)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(mainButtonsPanel, BorderLayout.NORTH);
        centerPanel.add(placeholderPanel, BorderLayout.CENTER);

        buttonPanel.add(centerPanel, BorderLayout.CENTER); // ===== FOOTER với modern design =====
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(250, 250, 250));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        footerPanel.setLayout(new BorderLayout());

        JLabel lblFooter = new JLabel("🔒 MiniBank - An toàn & Bảo mật", JLabel.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(120, 120, 120));

        footerPanel.add(lblFooter, BorderLayout.CENTER);

        // ===== ASSEMBLY =====
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Phương thức tạo nút modern với shadow và hover effect
    private JButton createModernButton(String icon, String text, Color bgColor, Color textColor) {
        JButton button = new JButton("<html><center><div style='margin-top: 8px;'><span style='font-size: 20px;'>"
                + icon
                + "</span></div><div style='margin-top: 8px; margin-bottom: 8px;'><span style='font-size: 11px;'>"
                + text + "</span></div></center></html>");
        button.setPreferredSize(new Dimension(140, 80));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15))); // Giảm padding trên/dưới

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void toggleBalanceVisibility() {
        balanceVisible = !balanceVisible;
        if (balanceVisible) {
            try {
                double balance = client.getBankService().checkBalance(currentAccount.getAccountNumber());
                lblBalance.setText("Số dư: " + formatCurrency(balance));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi lấy số dư: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            lblBalance.setText("Số dư: *******");
        }
    }

    private String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    private void showMenu() {
        if (sideMenuOpen) {
            closeSideMenu();
            return;
        }

        // Tạo side panel chiếm 4/5 width
        int panelWidth = (int) (getWidth() * 0.8); // 80% = 4/5 màn hình

        // Tạo overlay chỉ che phần bên phải (không che side panel)
        overlayPanel = new JPanel();
        overlayPanel.setBackground(new Color(0, 0, 0, 80)); // Màu đen trong suốt
        overlayPanel.setBounds(panelWidth, 0, getWidth() - panelWidth, getHeight()); // Chỉ che phần bên phải
        overlayPanel.setOpaque(false); // Cho phép trong suốt

        // Click overlay để đóng menu
        overlayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeSideMenu();
            }
        });

        sidePanel = new JPanel();
        sidePanel.setBackground(Color.WHITE);
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setBounds(0, 0, panelWidth, getHeight());
        sidePanel.setOpaque(true); // Đảm bảo hoàn toàn opaque

        // Thêm mouse listener để chặn hoàn toàn tương tác xuyên qua
        sidePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            // Chặn tất cả mouse events, không làm gì cả
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Consume event để không bị pass through
                evt.consume();
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                evt.consume();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                evt.consume();
            }
        }); // Header của side panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(64, 150, 255));
        headerPanel.setPreferredSize(new Dimension(panelWidth, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Menu", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Content panel với 2 nút
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // BoxLayout để xếp dọc từ trên xuống
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setOpaque(true); // Đảm bảo opaque

        // Chặn mouse events cho content panel
        contentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                evt.consume(); // Chặn click xuyên qua
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                evt.consume();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                evt.consume();
            }
        });

        // Nút Cập nhật thông tin - hình chữ nhật ngang
        JButton updateButton = createSidePanelButton("👤", "Cập nhật thông tin", panelWidth);
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Align về bên trái
        updateButton.addActionListener(e -> {
            closeSideMenu();
            updateAccountInfo();
        });

        // Nút Khóa tài khoản - hình chữ nhật ngang
        JButton lockButton = createSidePanelButton("🔒", "Khóa tài khoản", panelWidth);
        lockButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        lockButton.addActionListener(e -> {
            closeSideMenu();
            lockAccount();
        });

        // Nút Xóa tài khoản - hình chữ nhật ngang
        JButton deleteButton = createSidePanelButton("🗑️", "Xóa tài khoản", panelWidth);
        deleteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteButton.addActionListener(e -> {
            closeSideMenu();
            deleteAccount();
        });

        // Nút Đăng xuất - hình chữ nhật ngang
        JButton logoutButton = createSidePanelButton("🚪", "Đăng xuất", panelWidth);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Align về bên trái
        logoutButton.addActionListener(e -> {
            closeSideMenu();
            int result = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất khỏi tài khoản?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                new LoginFrame(client);
                dispose();
            }
        });

        // Thêm các nút theo thứ tự từ trên xuống
        contentPanel.add(updateButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Khoảng cách 15px
        contentPanel.add(lockButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Khoảng cách 15px
        contentPanel.add(deleteButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Khoảng cách 15px
        contentPanel.add(logoutButton);
        contentPanel.add(Box.createVerticalGlue()); // Đẩy các nút lên trên, để trống phía dưới cho nút mới

        sidePanel.add(headerPanel, BorderLayout.NORTH);
        sidePanel.add(contentPanel, BorderLayout.CENTER);

        // Thêm overlay trước, side panel sau (để side panel nằm trên overlay)
        getLayeredPane().add(overlayPanel, JLayeredPane.POPUP_LAYER);
        getLayeredPane().add(sidePanel, JLayeredPane.MODAL_LAYER); // MODAL_LAYER cao hơn POPUP_LAYER

        // Đổi icon bánh răng thành X
        menuButton.setText("✕");
        menuButton.revalidate(); // Force UI update
        menuButton.repaint(); // Force repaint
        sideMenuOpen = true; // Animation slide in (đơn giản)
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    private void closeSideMenu() {
        if (sidePanel != null) {
            getLayeredPane().remove(sidePanel);
            sidePanel = null;
        }
        if (overlayPanel != null) {
            getLayeredPane().remove(overlayPanel);
            overlayPanel = null;
        }
        menuButton.setText("⚙"); // Đổi lại thành bánh răng
        menuButton.revalidate(); // Force UI update
        menuButton.repaint(); // Force repaint
        sideMenuOpen = false;
        repaint();
    }

    // Tạo nút dạng hình chữ nhật cho side panel
    private JButton createSidePanelButton(String icon, String text, int panelWidth) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(15, 0));

        // Icon panel
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        // Text label
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        button.add(iconLabel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        // Kích thước cố định
        button.setPreferredSize(new Dimension(panelWidth - 80, 65)); // Chiều cao cố định 65px
        button.setMinimumSize(new Dimension(panelWidth - 80, 65));
        button.setMaximumSize(new Dimension(panelWidth - 80, 65));

        button.setBackground(new Color(248, 249, 250));
        button.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        button.setFocusPainted(false);
        button.setOpaque(true); // Đảm bảo button hoàn toàn opaque
        button.setContentAreaFilled(true); // Đảm bảo fill background hoàn toàn
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(64, 150, 255, 30));
                button.repaint(); // Force repaint khi hover
                // Repaint toàn bộ content panel để tránh overlap
                if (button.getParent() != null) {
                    button.getParent().repaint();
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 249, 250));
                button.repaint(); // Force repaint khi exit hover
                // Repaint toàn bộ content panel để clear overlap
                if (button.getParent() != null) {
                    button.getParent().repaint();
                }
                // Repaint toàn bộ side panel để đảm bảo
                if (button.getParent() != null && button.getParent().getParent() != null) {
                    button.getParent().getParent().repaint();
                }
            }
        });

        return button;
    }

    private void handleButtonClick(int index) {
        switch (index) {
            case 0: // Chuyển khoản
                new TransferFrame(client, currentAccount);
                break;
            case 1: // Lịch sử giao dịch
                new HistoryFrame(client, currentAccount);
                break;
        }
    }

    private void updateAccountInfo() {
        // Tên hiển thị nhưng không cho sửa
        JTextField txtName = new JTextField(currentAccount.getAccountHolder());
        txtName.setEditable(false);

        // Mật khẩu cho phép nhập mới
        JPasswordField txtPassword = new JPasswordField(currentAccount.getPassword());

        Object[] message = {
                "Tên chủ tài khoản:", txtName,
                "Mật khẩu mới:", txtPassword
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Cập nhật thông tin", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newPassword = new String(txtPassword.getPassword()).trim();

            if (!newPassword.isEmpty()) {
                currentAccount.setPassword(newPassword);

                try {
                    if (client.getBankService().updateAccount(currentAccount)) {
                        JOptionPane.showMessageDialog(this, "Cập nhật mật khẩu thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật thất bại!",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Khóa tài khoản của chính user
    private void lockAccount() {
        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn khóa tài khoản của mình?\n" +
                        "Sau khi khóa, bạn sẽ không thể đăng nhập được!",
                "Xác nhận khóa tài khoản",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                // User tự khóa thì không có lý do (null)
                if (client.getBankService().lockAccount(currentAccount.getAccountNumber(), null)) {
                    JOptionPane.showMessageDialog(this,
                            "Tài khoản đã được khóa thành công!\nBạn sẽ được đăng xuất.",
                            "Khóa tài khoản thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    new LoginFrame(client);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Khóa tài khoản thất bại!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Xóa tài khoản của chính user
    private void deleteAccount() {
        int result = JOptionPane.showConfirmDialog(this,
                "⚠️ CẢNH BÁO: Bạn có chắc muốn XÓA VĨNH VIỄN tài khoản của mình?\n" +
                        "Hành động này KHÔNG THỂ HOÀN TÁC!\n" +
                        "Tất cả dữ liệu và số dư sẽ bị mất!",
                "Xác nhận xóa tài khoản",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Xác nhận lần 2
            String confirm = JOptionPane.showInputDialog(this,
                    "Để xác nhận xóa tài khoản, vui lòng nhập: DELETE",
                    "Xác nhận cuối cùng",
                    JOptionPane.WARNING_MESSAGE);

            if ("DELETE".equals(confirm)) {
                try {
                    if (client.getBankService().deleteAccount(currentAccount.getAccountNumber())) {
                        JOptionPane.showMessageDialog(this,
                                "Tài khoản đã được xóa thành công!\nTạm biệt!",
                                "Xóa tài khoản thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame(client);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa tài khoản thất bại!", "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else if (confirm != null) {
                JOptionPane.showMessageDialog(this, "Xác nhận không đúng. Hủy bỏ xóa tài khoản.", "Hủy bỏ",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // ===== ACCOUNT STATUS MONITORING =====

    private void startAccountStatusMonitoring() {
        // Kiểm tra trạng thái tài khoản mỗi 5 giây
        accountStatusTimer = new Timer(5000, e -> checkAccountStatus());
        accountStatusTimer.start();
    }

    private void checkAccountStatus() {
        try {
            if (!client.isCurrentAccountActive()) {
                // Tài khoản đã bị khóa
                accountStatusTimer.stop();

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Tài khoản của bạn đã bị khóa bởi quản trị viên.\n" +
                                    "Bạn sẽ được đăng xuất ngay lập tức.\n" +
                                    "Vui lòng liên hệ ngân hàng để được hỗ trợ.",
                            "Tài khoản bị khóa",
                            JOptionPane.WARNING_MESSAGE);

                    // Logout và quay về login screen
                    client.logout();
                    new LoginFrame(client);
                    dispose();
                });
            }
        } catch (Exception ex) {
            System.err.println("Lỗi kiểm tra trạng thái tài khoản: " + ex.getMessage());
        }
    }

    // Override để đảm bảo cleanup khi đóng window
    @Override
    public void dispose() {
        if (accountStatusTimer != null) {
            accountStatusTimer.stop();
        }
        client.logout();
        super.dispose();
    }
}