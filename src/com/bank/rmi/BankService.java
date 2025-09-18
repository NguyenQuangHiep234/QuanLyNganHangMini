package com.bank.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.bank.common.Account;
import com.bank.common.Transaction;

public interface BankService extends Remote {
    // Account operations
    boolean createAccount(Account account) throws RemoteException;

    Account login(String accountNumber, String password) throws RemoteException;

    double checkBalance(String accountNumber) throws RemoteException;

    // Transfer operations
    boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException;

    // Account information
    Account getAccountInfo(String accountNumber) throws RemoteException;

    // Transaction history
    List<Transaction> getTransactionHistory(String accountNumber) throws RemoteException;

    // Account management
    boolean updateAccount(Account account) throws RemoteException;

    boolean deleteAccount(String accountNumber) throws RemoteException;

    List<Account> getAllAccounts() throws RemoteException;

    // Account lock/unlock management
    boolean lockAccount(String accountNumber, String reason) throws RemoteException;

    boolean unlockAccount(String accountNumber) throws RemoteException;

    // Utility methods
    boolean accountExists(String accountNumber) throws RemoteException;

    // 🔥 THÊM PHƯƠNG THỨC MỚI: Admin set tiền
    boolean setAccountBalance(String accountNumber, double newBalance) throws RemoteException;
}