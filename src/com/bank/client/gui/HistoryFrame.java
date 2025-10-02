package com.bank.client.gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;

import com.bank.client.BankClient;
import com.bank.common.Account;
import com.bank.common.Transaction;

public class HistoryFrame extends JFrame {
    private BankClient client;
    private Account account;
    private DecimalFormat currencyFormat;

    private JTable table;
    private DefaultTableModel tableModel;

    public HistoryFrame(BankClient client, Account account) {
        this.client = client;
        this.account = account;
        this.currencyFormat = new DecimalFormat("#,### VND");
        initializeUI();
        loadTransactionHistory();
    }

    private void initializeUI() {
        setTitle("Lịch sử giao dịch - MiniBank");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(true);

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
        headerPanel.setPreferredSize(new Dimension(800, 80));

        JLabel lblTitle = new JLabel("LỊCH SỬ GIAO DỊCH", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblAccount = new JLabel("Tài khoản: " + account.getAccountNumber(), JLabel.CENTER);
        lblAccount.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
        lblAccount.setForeground(new Color(220, 235, 255));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.add(lblAccount, BorderLayout.SOUTH);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // Content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 248, 252));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Table with improved styling
        String[] columns = { "Mã GD", "Loại", "Số tiền", "Thời gian", "Mô tả" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(64, 150, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Mã GD
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Loại
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Số tiền
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Thời gian
        table.getColumnModel().getColumn(4).setPreferredWidth(200); // Mô tả

        // Alternating row colors
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel với styling đẹp hơn
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        buttonPanel.setBackground(new Color(245, 248, 252));

        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Thêm hover effect
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnClose.setBackground(new Color(90, 98, 104));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnClose.setBackground(new Color(108, 117, 125));
            }
        });
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnClose);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void loadTransactionHistory() {
        try {
            List<Transaction> transactions = client.getBankService().getTransactionHistory(account.getAccountNumber());

            tableModel.setRowCount(0); // Clear existing data

            for (Transaction transaction : transactions) {
                Object[] row = {
                        transaction.getTransactionId(),
                        transaction.getType(),
                        currencyFormat.format(transaction.getAmount()),
                        transaction.getTimestamp(),
                        transaction.getDescription()
                };
                tableModel.addRow(row);
            }

            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chưa có giao dịch nào!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải lịch sử: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}