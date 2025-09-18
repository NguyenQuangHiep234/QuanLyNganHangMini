package com.bank.common;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String accountHolder;
    private double balance;
    private String password;
    private boolean isLocked;
    private String lockReason;

    public Account() {
    }

    public Account(String accountNumber, String accountHolder, double balance, String password) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.password = password;
        this.isLocked = false;
        this.lockReason = null;
    }

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    @Override
    public String toString() {
        return "Account [accountNumber=" + accountNumber + ", accountHolder=" + accountHolder + ", balance=" + balance
                + ", isLocked=" + isLocked + "]";
    }
}