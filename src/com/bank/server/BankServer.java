package com.bank.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class BankServer {
    public static void main(String[] args) {
        try {
            // Create RMI registry on port 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry created on port 1099");
            
            // Create the service instance
            BankServiceImpl bankService = new BankServiceImpl();
            
            // Bind the service to the registry
            Naming.rebind("rmi://localhost:1099/BankService", bankService);
            System.out.println("BankService is ready and bound to 'BankService'");
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}