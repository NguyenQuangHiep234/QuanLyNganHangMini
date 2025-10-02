package com.bank.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.bank.client.BankClient;
import com.bank.common.Account;

public class LoginFrame extends JFrame {
    private BankClient client;
    private JTextField txtAccountNumber;
    private JPasswordField txtPassword;

    public LoginFrame(BankClient client) {
        this.client = client;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Đăng nhập - Ngân hàng Mini");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with modern background
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20)) {
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 30, 30));
        mainPanel.setOpaque(false);

        // Modern header with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int arc = 30;

                // Modern banking gradient
                Color color1 = new Color(25, 118, 210);
                Color color2 = new Color(100, 181, 246);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, arc, arc);

                // Subtle shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(2, 2, w - 4, h - 4, arc, arc);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        headerPanel.setPreferredSize(new Dimension(400, 150));
        headerPanel.setOpaque(false);

        // Header content
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setOpaque(false);

        JLabel lblTitle = new JLabel("NGÂN HÀNG MINI", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Đăng nhập tài khoản của bạn", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(220, 220, 220));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerContent.add(lblTitle);
        headerContent.add(Box.createRigidArea(new Dimension(0, 10)));
        headerContent.add(lblSubtitle);

        headerPanel.add(headerContent, BorderLayout.CENTER);

        // Modern form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblAccount = new JLabel("Số tài khoản:");
        lblAccount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAccount.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lblAccount, gbc);

        txtAccountNumber = new JTextField();
        txtAccountNumber.setPreferredSize(new Dimension(340, 50));
        txtAccountNumber.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAccountNumber.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 189, 189), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        txtAccountNumber.setBackground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 10, 20);
        formPanel.add(txtAccountNumber, gbc);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPassword.setForeground(new Color(33, 33, 33));
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 20, 5, 20);
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(340, 50));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 189, 189), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        txtPassword.setBackground(Color.WHITE);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 20, 20, 20);
        formPanel.add(txtPassword, gbc);

        // Modern button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        // Login button with modern design
        JButton btnLogin = createModernButton("Đăng nhập", new Color(76, 175, 80), Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(340, 55));
        btnLogin.setPreferredSize(new Dimension(340, 55));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Create account button
        JButton btnCreateAccount = createModernButton("Tạo tài khoản mới", new Color(25, 118, 210), Color.WHITE);
        btnCreateAccount.setMaximumSize(new Dimension(340, 50));
        btnCreateAccount.setPreferredSize(new Dimension(340, 50));
        btnCreateAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCreateAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CreateAccountFrame(client);
                dispose();
            }
        });

        // Admin button
        JButton btnAdmin = createModernButton("Quản trị hệ thống", new Color(255, 152, 0), Color.WHITE);
        btnAdmin.setMaximumSize(new Dimension(340, 50));
        btnAdmin.setPreferredSize(new Dimension(340, 50));
        btnAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String password = JOptionPane.showInputDialog("Nhập mật khẩu quản trị:");
                if ("admin123".equals(password)) {
                    new AdminFrame(client);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Mật khẩu không đúng!");
                }
            }
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(btnCreateAccount);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(btnAdmin);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
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

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
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

    private void login() {
        String accountNumber = txtAccountNumber.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (accountNumber.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Account account = client.getBankService().login(accountNumber, password);
            if (account != null) {
                // Kiểm tra tài khoản có bị khóa không
                if (account.isLocked()) {
                    String lockMessage;
                    if (account.getLockReason() == null || account.getLockReason().isEmpty()) {
                        // Tự khóa
                        lockMessage = "Tài khoản của bạn đã bị khóa, hãy liên hệ admin để được hỗ trợ sớm nhất.";
                    } else {
                        // Admin khóa với lý do
                        lockMessage = "Tài khoản của bạn đã bị admin khóa với lý do: '" + account.getLockReason()
                                + "'. Hãy liên hệ admin để được hỗ trợ sớm nhất.";
                    }
                    JOptionPane.showMessageDialog(this, lockMessage, "Tài khoản bị khóa", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                // Đăng ký session cho tài khoản
                client.setCurrentAccountNumber(account.getAccountNumber());

                new MainFrame(client, account);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Số tài khoản hoặc mật khẩu không đúng!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}