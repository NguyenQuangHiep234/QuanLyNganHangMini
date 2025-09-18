package com.bank.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.DecimalFormat;

import com.bank.client.BankClient;
import com.bank.common.Account;

public class TransferFrame extends JFrame {
    private BankClient client;
    private Account fromAccount;
    private DecimalFormat currencyFormat;

    private JTextField txtToAccount, txtAmount, txtReceiverName;
    private JLabel lblReceiverName;
    private Timer accountCheckTimer;

    public TransferFrame(BankClient client, Account fromAccount) {
        this.client = client;
        this.fromAccount = fromAccount;
        this.currencyFormat = new DecimalFormat("#,###");
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chuyển khoản - MiniBank");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with light blue-gray background
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 248, 252));

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
        headerPanel.setPreferredSize(new Dimension(450, 80));

        JLabel lblTitle = new JLabel("CHUYỂN KHOẢN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // Content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 248, 252));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Form panel với styling đẹp hơn
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Đến số tài khoản
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblToAccount = new JLabel("Đến số tài khoản:");
        lblToAccount.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblToAccount, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtToAccount = new JTextField();
        txtToAccount.setPreferredSize(new Dimension(200, 30));
        txtToAccount.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(txtToAccount, gbc);

        // Tên người nhận
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblReceiver = new JLabel("Tên người nhận:");
        lblReceiver.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblReceiver, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel receiverPanel = new JPanel(new BorderLayout());
        receiverPanel.setBackground(Color.WHITE);
        txtReceiverName = new JTextField();
        txtReceiverName.setEditable(false);
        txtReceiverName.setBackground(new Color(248, 248, 248));
        txtReceiverName.setPreferredSize(new Dimension(200, 30));
        txtReceiverName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        receiverPanel.add(txtReceiverName, BorderLayout.CENTER);

        lblReceiverName = new JLabel(" Chưa kiểm tra ");
        lblReceiverName.setForeground(Color.GRAY);
        lblReceiverName.setFont(new Font("Arial", Font.ITALIC, 10));
        receiverPanel.add(lblReceiverName, BorderLayout.EAST);
        formPanel.add(receiverPanel, gbc);

        // Số tiền
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblAmount = new JLabel("Số tiền (VND):");
        lblAmount.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblAmount, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtAmount = new JTextField();
        txtAmount.setPreferredSize(new Dimension(200, 30));
        txtAmount.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(txtAmount, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Thêm sự kiện tự động kiểm tra tài khoản
        setupAccountNumberListener();

        // Button panel với styling đẹp hơn
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 248, 252));

        JButton btnTransfer = new JButton("Chuyển khoản");
        btnTransfer.setPreferredSize(new Dimension(140, 40));
        btnTransfer.setBackground(new Color(64, 150, 255));
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setFont(new Font("Arial", Font.BOLD, 14));
        btnTransfer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnTransfer.setFocusPainted(false);
        btnTransfer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Thêm hover effect
        btnTransfer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnTransfer.setBackground(new Color(50, 130, 230));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnTransfer.setBackground(new Color(64, 150, 255));
            }
        });
        btnTransfer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transferMoney();
            }
        });

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Thêm hover effect
        btnCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancel.setBackground(new Color(90, 98, 104));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCancel.setBackground(new Color(108, 117, 125));
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(btnTransfer);
        buttonPanel.add(btnCancel);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void setupAccountNumberListener() {
        // Sử dụng Timer để tránh kiểm tra liên tục khi đang gõ
        accountCheckTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkAccountNumber();
            }
        });
        accountCheckTimer.setRepeats(false);

        txtToAccount.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                scheduleCheck();
            }

            public void removeUpdate(DocumentEvent e) {
                scheduleCheck();
            }

            public void changedUpdate(DocumentEvent e) {
                scheduleCheck();
            }

            private void scheduleCheck() {
                accountCheckTimer.restart();
            }
        });
    }

    private void checkAccountNumber() {
        String toAccount = txtToAccount.getText().trim();

        if (toAccount.isEmpty()) {
            txtReceiverName.setText("");
            lblReceiverName.setText(" Chưa nhập số TK ");
            lblReceiverName.setForeground(Color.GRAY);
            return;
        }

        if (toAccount.equals(fromAccount.getAccountNumber())) {
            txtReceiverName.setText("Không thể chuyển cho chính mình");
            lblReceiverName.setText(" Số TK không hợp lệ ");
            lblReceiverName.setForeground(Color.RED);
            return;
        }

        // Hiển thị loading
        txtReceiverName.setText("Đang kiểm tra...");
        lblReceiverName.setText(" Đang tải ");
        lblReceiverName.setForeground(Color.BLUE);

        // Kiểm tra tài khoản tồn tại
        SwingWorker<Account, Void> worker = new SwingWorker<Account, Void>() {
            @Override
            protected Account doInBackground() throws Exception {
                return client.getBankService().getAccountInfo(toAccount);
            }

            @Override
            protected void done() {
                try {
                    Account receiverAccount = get();
                    if (receiverAccount != null) {
                        txtReceiverName.setText(receiverAccount.getAccountHolder());
                        lblReceiverName.setText(" Hợp lệ ");
                        lblReceiverName.setForeground(new Color(0, 128, 0));
                    } else {
                        txtReceiverName.setText("Không tìm thấy tài khoản");
                        lblReceiverName.setText(" Không tồn tại ");
                        lblReceiverName.setForeground(Color.RED);
                    }
                } catch (Exception ex) {
                    txtReceiverName.setText("Lỗi kiểm tra");
                    lblReceiverName.setText(" Lỗi kết nối ");
                    lblReceiverName.setForeground(Color.RED);
                }
            }
        };

        worker.execute();
    }

    private void transferMoney() {
        try {
            String toAccount = txtToAccount.getText().trim();
            String amountText = txtAmount.getText().replaceAll("[.,]", "");
            double amount = Double.parseDouble(amountText);

            if (toAccount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tài khoản nhận!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (toAccount.equals(fromAccount.getAccountNumber())) {
                JOptionPane.showMessageDialog(this, "Không thể chuyển cho chính mình!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra lại tài khoản đích trước khi chuyển
            Account receiverAccount = client.getBankService().getAccountInfo(toAccount);
            if (receiverAccount == null) {
                JOptionPane.showMessageDialog(this, "Số tài khoản nhận không tồn tại!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = client.getBankService().transfer(fromAccount.getAccountNumber(), toAccount, amount);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Chuyển khoản thành công!\n" +
                                "Số tiền: " + currencyFormat.format(amount) + " VND\n" +
                                "Đến: " + receiverAccount.getAccountHolder() + " (" + toAccount + ")",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển khoản thất bại! Số dư không đủ.", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}