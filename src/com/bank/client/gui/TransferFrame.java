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
    private JPanel amountSuggestionsPanel;
    private Timer amountInputTimer;

    public TransferFrame(BankClient client, Account fromAccount) {
        this.client = client;
        this.fromAccount = fromAccount;
        this.currencyFormat = new DecimalFormat("#,###");
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chuyển khoản - MiniBank");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 700);
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
        headerPanel.setPreferredSize(new Dimension(400, 80));

        JLabel lblTitle = new JLabel("CHUYỂN KHOẢN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // Content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 248, 252));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

        // Form panel với styling đẹp hơn, giống MainFrame
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Đến số tài khoản - Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel lblToAccount = new JLabel("Đến số tài khoản:");
        lblToAccount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblToAccount, gbc);

        // Đến số tài khoản - TextField
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 15, 10, 15);
        txtToAccount = new JTextField();
        txtToAccount.setPreferredSize(new Dimension(300, 32));
        txtToAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtToAccount.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        formPanel.add(txtToAccount, gbc);

        // Tên người nhận - Label
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 15, 5, 15);
        JLabel lblReceiver = new JLabel("Tên người nhận:");
        lblReceiver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblReceiver, gbc);

        // Tên người nhận - TextField với status
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 15, 10, 15);
        JPanel receiverPanel = new JPanel(new BorderLayout());
        receiverPanel.setBackground(Color.WHITE);
        txtReceiverName = new JTextField();
        txtReceiverName.setEditable(false);
        txtReceiverName.setBackground(new Color(248, 248, 248));
        txtReceiverName.setPreferredSize(new Dimension(300, 32));
        txtReceiverName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtReceiverName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        receiverPanel.add(txtReceiverName, BorderLayout.CENTER);

        lblReceiverName = new JLabel(" Chưa kiểm tra ");
        lblReceiverName.setForeground(Color.GRAY);
        lblReceiverName.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        receiverPanel.add(lblReceiverName, BorderLayout.EAST);
        formPanel.add(receiverPanel, gbc);

        // Số tiền - Label
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 15, 5, 15);
        JLabel lblAmount = new JLabel("Số tiền (VND):");
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblAmount, gbc);

        // Số tiền - TextField
        gbc.gridy = 5;
        gbc.insets = new Insets(2, 15, 8, 15);
        txtAmount = new JTextField();
        txtAmount.setPreferredSize(new Dimension(300, 32));
        txtAmount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAmount.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        formPanel.add(txtAmount, gbc);

        // Panel gợi ý số tiền
        gbc.gridy = 6;
        gbc.insets = new Insets(2, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        amountSuggestionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        amountSuggestionsPanel.setBackground(Color.WHITE);
        amountSuggestionsPanel.setPreferredSize(new Dimension(320, 50));
        amountSuggestionsPanel.setMinimumSize(new Dimension(320, 50));
        amountSuggestionsPanel.setVisible(false); // Ẩn ban đầu
        formPanel.add(amountSuggestionsPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Thêm sự kiện tự động kiểm tra tài khoản
        setupAccountNumberListener();

        // Thêm sự kiện cho ô nhập số tiền để hiển thị gợi ý
        setupAmountInputListener();

        // Button panel với styling hiện đại giống MainFrame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 248, 252));

        JButton btnTransfer = createModernButton("", "Chuyển khoản", new Color(64, 150, 255));
        btnTransfer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transferMoney();
            }
        });

        JButton btnCancel = createModernButton("", "Hủy", new Color(108, 117, 125));
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(btnTransfer);
        buttonPanel.add(btnCancel);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== FOOTER với design giống MainFrame =====
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(250, 250, 250));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        footerPanel.setLayout(new BorderLayout());

        JLabel lblFooter = new JLabel("🔒 MiniBank - An toàn & Bảo mật", JLabel.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(120, 120, 120));

        footerPanel.add(lblFooter, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

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

    private void setupAmountInputListener() {
        // Sử dụng Timer để tránh tạo gợi ý liên tục khi đang gõ
        amountInputTimer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAmountSuggestions();
            }
        });
        amountInputTimer.setRepeats(false);

        txtAmount.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                scheduleAmountSuggestion();
            }

            public void removeUpdate(DocumentEvent e) {
                scheduleAmountSuggestion();
            }

            public void changedUpdate(DocumentEvent e) {
                scheduleAmountSuggestion();
            }

            private void scheduleAmountSuggestion() {
                amountInputTimer.restart();
            }
        });
    }

    private void updateAmountSuggestions() {
        String amountText = txtAmount.getText().trim();
        amountSuggestionsPanel.removeAll();

        if (amountText.isEmpty()) {
            amountSuggestionsPanel.setVisible(false);
            amountSuggestionsPanel.revalidate();
            amountSuggestionsPanel.repaint();
            return;
        }

        // Lấy chữ số đầu tiên
        char firstDigit = 0;
        for (char c : amountText.toCharArray()) {
            if (Character.isDigit(c)) {
                firstDigit = c;
                break;
            }
        }

        if (firstDigit == 0) {
            amountSuggestionsPanel.setVisible(false);
            amountSuggestionsPanel.revalidate();
            amountSuggestionsPanel.repaint();
            return;
        }

        // Tạo gợi ý dựa trên chữ số đầu tiên
        int digit = Character.getNumericValue(firstDigit);
        long[] suggestions = generateAmountSuggestions(digit);

        // Chỉ hiển thị các nút gợi ý, không có label "Gợi ý:"
        for (long amount : suggestions) {
            JButton btnSuggestion = createSuggestionButton(amount);
            amountSuggestionsPanel.add(btnSuggestion);
        }

        amountSuggestionsPanel.setVisible(true);
        amountSuggestionsPanel.revalidate();
        amountSuggestionsPanel.repaint();

        // Đảm bảo parent container cũng được revalidate
        if (amountSuggestionsPanel.getParent() != null) {
            amountSuggestionsPanel.getParent().revalidate();
            amountSuggestionsPanel.getParent().repaint();
        }
    }

    private long[] generateAmountSuggestions(int firstDigit) {
        // Tạo gợi ý: chữ số * 10k, 100k, 1tr, 10tr
        return new long[] {
                firstDigit * 10000L, // 10 nghìn
                firstDigit * 100000L, // 100 nghìn
                firstDigit * 1000000L, // 1 triệu
                firstDigit * 10000000L // 10 triệu
        };
    }

    private JButton createSuggestionButton(long amount) {
        String formattedAmount = currencyFormat.format(amount);
        String displayText;

        if (amount >= 1000000) {
            if (amount % 1000000 == 0) {
                displayText = (amount / 1000000) + "tr";
            } else {
                displayText = String.format("%.1ftr", amount / 1000000.0);
            }
        } else {
            displayText = (amount / 1000) + "k";
        }

        JButton btnSuggestion = new JButton(displayText);
        btnSuggestion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSuggestion.setPreferredSize(new Dimension(65, 35));
        btnSuggestion.setBackground(new Color(240, 248, 255));
        btnSuggestion.setForeground(new Color(64, 150, 255));
        btnSuggestion.setBorder(BorderFactory.createLineBorder(new Color(64, 150, 255), 1));
        btnSuggestion.setFocusPainted(false);
        btnSuggestion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btnSuggestion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSuggestion.setBackground(new Color(64, 150, 255));
                btnSuggestion.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSuggestion.setBackground(new Color(240, 248, 255));
                btnSuggestion.setForeground(new Color(64, 150, 255));
            }
        });

        // Click event để điền vào ô số tiền
        btnSuggestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtAmount.setText(formattedAmount);
                amountSuggestionsPanel.setVisible(false);
                amountSuggestionsPanel.revalidate();
                amountSuggestionsPanel.repaint();
            }
        });

        return btnSuggestion;
    }

    private void transferMoney() {
        try {
            // Kiểm tra trạng thái tài khoản trước khi chuyển khoản
            if (!client.isCurrentAccountActive()) {
                JOptionPane.showMessageDialog(this,
                        "Tài khoản của bạn đã bị khóa.\nVui lòng liên hệ ngân hàng để được hỗ trợ.",
                        "Tài khoản bị khóa",
                        JOptionPane.WARNING_MESSAGE);
                dispose();
                return;
            }

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
        } catch (java.rmi.RemoteException ex) {
            // Xử lý đặc biệt cho RemoteException (có thể là account bị khóa)
            if (ex.getMessage().contains("khóa")) {
                JOptionPane.showMessageDialog(this,
                        "Tài khoản của bạn đã bị khóa.\nBạn sẽ được đăng xuất.",
                        "Tài khoản bị khóa",
                        JOptionPane.WARNING_MESSAGE);
                // Quay về login
                client.logout();
                new LoginFrame(client);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức tạo nút compact cho TransferFrame
    private JButton createModernButton(String icon, String text, Color bgColor) {
        String buttonText = (icon != null && !icon.trim().isEmpty())
                ? "<html><center><span style='font-size: 14px;'>" + icon + "</span> " +
                        "<span style='font-size: 13px;'>" + text + "</span></center></html>"
                : text;
        JButton button = new JButton(buttonText);
        button.setPreferredSize(new Dimension(130, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = bgColor;

            public void mouseEntered(MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }
}