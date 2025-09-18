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
        setSize(400, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("TẠO TÀI KHOẢN MỚI", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Số tài khoản
        JLabel lblAccountNumber = new JLabel("Số tài khoản:");
        lblAccountNumber.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lblAccountNumber, gbc);

        txtAccountNumber = new JTextField();
        txtAccountNumber.setPreferredSize(new Dimension(340, 45));
        txtAccountNumber.setFont(new Font("Arial", Font.PLAIN, 16));
        txtAccountNumber.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 8, 20);
        formPanel.add(txtAccountNumber, gbc);

        // Tên chủ tài khoản
        JLabel lblAccountHolder = new JLabel("Tên chủ tài khoản:");
        lblAccountHolder.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 2;
        gbc.insets = new Insets(12, 20, 5, 20);
        formPanel.add(lblAccountHolder, gbc);

        txtAccountHolder = new JTextField();
        txtAccountHolder.setPreferredSize(new Dimension(340, 45));
        txtAccountHolder.setFont(new Font("Arial", Font.PLAIN, 16));
        txtAccountHolder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 20, 8, 20);
        formPanel.add(txtAccountHolder, gbc);

        // Mật khẩu
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 20, 5, 20);
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(340, 45));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 20, 20, 20);
        formPanel.add(txtPassword, gbc);

        // Số tiền ban đầu (ẩn, set mặc định = 0)
        txtInitialBalance = new JTextField("0");

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(30, 20, 20, 20);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.weightx = 1.0;

        JButton btnCreate = new JButton("Tạo tài khoản");
        btnCreate.setPreferredSize(new Dimension(340, 55));
        btnCreate.setBackground(new Color(0, 153, 0));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFont(new Font("Arial", Font.BOLD, 18));
        btnCreate.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btnCreate.setFocusPainted(false);
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonPanel.add(btnCreate, btnGbc);

        JButton btnBack = new JButton("Quay lại");
        btnBack.setPreferredSize(new Dimension(340, 50));
        btnBack.setBackground(new Color(108, 117, 125));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 16));
        btnBack.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginFrame(client);
                dispose();
            }
        });
        btnGbc.gridy = 1;
        btnGbc.insets = new Insets(15, 20, 20, 20);
        buttonPanel.add(btnBack, btnGbc);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
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
}