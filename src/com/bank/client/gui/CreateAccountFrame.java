package com.bank.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.bank.client.BankClient;
import com.bank.common.Account;

public class CreateAccountFrame extends JFrame {
    private BankClient client;
    private JTextField txtAccountNumber, txtAccountHolder;
    private JPasswordField txtPassword;
    private JTextField txtInitialBalance;

    public CreateAccountFrame(BankClient client) {
        this.client = client;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Tạo tài khoản mới");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 750);
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 30));
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
                int arc = 30;

                // Create account gradient - green theme
                Color color1 = new Color(76, 175, 80);
                Color color2 = new Color(56, 142, 60);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, arc, arc);

                // Subtle shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(2, 2, w - 4, h - 4, arc, arc);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        headerPanel.setPreferredSize(new Dimension(400, 120));
        headerPanel.setOpaque(false);

        // Header content
        JPanel headerContent = new JPanel(new GridBagLayout());
        headerContent.setOpaque(false);
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.gridx = 0;
        hgbc.anchor = GridBagConstraints.CENTER;

        JLabel lblTitle = new JLabel("✨ TẠO TÀI KHOẢN MỚI", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        hgbc.gridy = 0;
        hgbc.insets = new Insets(0, 0, 5, 0);
        headerContent.add(lblTitle, hgbc);

        JLabel lblSubtitle = new JLabel("Đăng ký tài khoản ngân hàng của bạn", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(220, 220, 220));
        hgbc.gridy = 1;
        hgbc.insets = new Insets(0, 0, 0, 0);
        headerContent.add(lblSubtitle, hgbc);

        headerPanel.add(headerContent, BorderLayout.CENTER);

        // Modern form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Số tài khoản
        JLabel lblAccountNumber = new JLabel("Số tài khoản:");
        lblAccountNumber.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAccountNumber.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lblAccountNumber, gbc);

        txtAccountNumber = new JTextField();
        txtAccountNumber.setPreferredSize(new Dimension(340, 45));
        txtAccountNumber.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAccountNumber.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 189, 189), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        txtAccountNumber.setBackground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 8, 20);
        formPanel.add(txtAccountNumber, gbc);

        // Tên chủ tài khoản
        JLabel lblAccountHolder = new JLabel("Tên chủ tài khoản:");
        lblAccountHolder.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAccountHolder.setForeground(new Color(33, 33, 33));
        gbc.gridy = 2;
        gbc.insets = new Insets(12, 20, 5, 20);
        formPanel.add(lblAccountHolder, gbc);

        txtAccountHolder = new JTextField();
        txtAccountHolder.setPreferredSize(new Dimension(340, 45));
        txtAccountHolder.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAccountHolder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 189, 189), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        txtAccountHolder.setBackground(Color.WHITE);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 20, 8, 20);
        formPanel.add(txtAccountHolder, gbc);

        // Mật khẩu
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPassword.setForeground(new Color(33, 33, 33));
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 20, 5, 20);
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(340, 45));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 189, 189), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        txtPassword.setBackground(Color.WHITE);
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 20, 20, 20);
        formPanel.add(txtPassword, gbc);

        // Số tiền ban đầu (ẩn, set mặc định = 0)
        txtInitialBalance = new JTextField("0");

        // Modern button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnCreate = createModernButton("✨ Tạo tài khoản", new Color(76, 175, 80), Color.WHITE);
        btnCreate.setMaximumSize(new Dimension(340, 50));
        btnCreate.setPreferredSize(new Dimension(340, 50));
        btnCreate.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCreate.addActionListener(e -> createAccount());

        JButton btnCancel = createModernButton("❌ Hủy", new Color(158, 158, 158), Color.WHITE);
        btnCancel.setMaximumSize(new Dimension(340, 45));
        btnCancel.setPreferredSize(new Dimension(340, 45));
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCreate);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(btnCancel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Trong CreateAccountFrame.java, sửa phần createAccount()
    private void createAccount() {
        try {
            String accountNumber = txtAccountNumber.getText().trim();
            String accountHolder = txtAccountHolder.getText().trim();
            String password = new String(txtPassword.getPassword());

            // 🚫 BỎ PHẦN NHẬP SỐ TIỀN BAN ĐẦU
            double initialBalance = 0; // Mặc định số dư = 0

            if (accountNumber.isEmpty() || accountHolder.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (client.getBankService().accountExists(accountNumber)) {
                JOptionPane.showMessageDialog(this, "Số tài khoản đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account newAccount = new Account(accountNumber, accountHolder, initialBalance, password);

            if (client.getBankService().createAccount(newAccount)) {
                JOptionPane.showMessageDialog(this,
                        "Tạo tài khoản thành công!\n" +
                                "Số TK: " + accountNumber + "\n" +
                                "Tên: " + accountHolder + "\n" +
                                "Số dư ban đầu: 0 VND\n\n" +
                                "Liên hệ admin để được set số dư.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame(client);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tạo tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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