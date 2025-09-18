package com.bank.common;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String accountNumber;
    private String type; // DEPOSIT, WITHDRAW, TRANSFER
    private double amount;
    private Date timestamp;
    private String description;
    
    public Transaction() {}
    
    public Transaction(String transactionId, String accountNumber, String type, double amount, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = new Date();
        this.description = description;
    }
    
    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return "Transaction [transactionId=" + transactionId + ", accountNumber=" + accountNumber + ", type=" + type
                + ", amount=" + amount + ", timestamp=" + timestamp + ", description=" + description + "]";
    }
}