package com.bank.client;

import java.rmi.Naming;

import com.bank.client.gui.LoginFrame;
import com.bank.rmi.BankService;

public class BankClient {
    private BankService bankService;
    private String sessionId;
    private String currentAccountNumber;

    public BankClient() {
        // Tạo unique session ID
        sessionId = java.util.UUID.randomUUID().toString();
        try {
            // Look up the remote service
            bankService = (BankService) Naming.lookup("rmi://localhost:1099/BankService");
            System.out.println("Connected to Bank Service");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public BankService getBankService() {
        return bankService;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getCurrentAccountNumber() {
        return currentAccountNumber;
    }

    public void setCurrentAccountNumber(String accountNumber) {
        this.currentAccountNumber = accountNumber;
        try {
            // Đăng ký session khi login
            if (bankService != null && accountNumber != null) {
                bankService.registerActiveSession(accountNumber, sessionId);
            }
        } catch (Exception e) {
            System.err.println("Lỗi đăng ký session: " + e.getMessage());
        }
    }

    public void logout() {
        try {
            // Hủy đăng ký session khi logout
            if (bankService != null && currentAccountNumber != null && sessionId != null) {
                bankService.unregisterActiveSession(currentAccountNumber, sessionId);
            }
        } catch (Exception e) {
            System.err.println("Lỗi hủy đăng ký session: " + e.getMessage());
        }
        currentAccountNumber = null;
    }

    // Method để kiểm tra account status định kỳ
    public boolean isCurrentAccountActive() {
        try {
            if (bankService != null && currentAccountNumber != null) {
                return bankService.isAccountActive(currentAccountNumber);
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra trạng thái tài khoản: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        BankClient client = new BankClient();
        if (client.getBankService() != null) {
            new LoginFrame(client);
        }
    }
}