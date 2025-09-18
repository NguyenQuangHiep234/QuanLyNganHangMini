package com.bank.client;

import java.rmi.Naming;

import com.bank.client.gui.LoginFrame;
import com.bank.rmi.BankService;

public class BankClient {
    private BankService bankService;
    
    public BankClient() {
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
    
    public static void main(String[] args) {
        BankClient client = new BankClient();
        if (client.getBankService() != null) {
            new LoginFrame(client);
        }
    }
}