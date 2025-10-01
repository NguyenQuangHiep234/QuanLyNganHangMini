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
        setSize(400, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel lblTitle = new JLabel("NGÂN HÀNG MINI", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0, 102, 204));

        // Form panel với layout cải thiện
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblAccount = new JLabel("Số tài khoản:");
        lblAccount.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lblAccount, gbc);

        txtAccountNumber = new JTextField();
        txtAccountNumber.setPreferredSize(new Dimension(340, 50));
        txtAccountNumber.setFont(new Font("Arial", Font.PLAIN, 16));
        txtAccountNumber.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 10, 20);
        formPanel.add(txtAccountNumber, gbc);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 20, 5, 20);
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(340, 50));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 20, 20, 20);
        formPanel.add(txtPassword, gbc);

        // Buttons với layout dọc
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(30, 20, 20, 20);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.weightx = 1.0;

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(340, 55));
        btnLogin.setBackground(new Color(0, 153, 0));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonPanel.add(btnLogin, btnGbc);

        JButton btnCreateAccount = new JButton("Tạo tài khoản mới");
        btnCreateAccount.setPreferredSize(new Dimension(340, 50));
        btnCreateAccount.setBackground(new Color(0, 102, 204));
        btnCreateAccount.setForeground(Color.WHITE);
        btnCreateAccount.setFont(new Font("Arial", Font.BOLD, 16));
        btnCreateAccount.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnCreateAccount.setFocusPainted(false);
        btnCreateAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CreateAccountFrame(client);
                dispose();
            }
        });
        btnGbc.gridy = 1;
        btnGbc.insets = new Insets(15, 20, 20, 20);
        buttonPanel.add(btnCreateAccount, btnGbc);

        // Trong LoginFrame.java, thêm nút Admin
        JButton btnAdmin = new JButton("Quản trị hệ thống");
        btnAdmin.setPreferredSize(new Dimension(340, 50));
        btnAdmin.setBackground(new Color(255, 153, 0));
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.setFont(new Font("Arial", Font.BOLD, 16));
        btnAdmin.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnAdmin.setFocusPainted(false);
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
        btnGbc.gridy = 2;
        btnGbc.insets = new Insets(15, 20, 20, 20);
        buttonPanel.add(btnAdmin, btnGbc);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
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