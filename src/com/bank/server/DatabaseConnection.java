package com.bank.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bank_management";
    private static final String USER = "root";
    private static final String PASSWORD = "230404";

    static {
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Created new MySQL database connection");
            return newConnection;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection() {
        // Không cần làm gì vì mỗi connection được tự động đóng trong try-with-resources
        System.out.println("Database connections will be closed automatically");
    }

    // Test connection
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Database connection test: SUCCESS");
                System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test: FAILED");
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Update database schema for account locking feature
    public static void updateSchemaForAccountLocking() {
        try (Connection conn = getConnection()) {
            // Check if is_locked column exists
            var metaData = conn.getMetaData();
            var columns = metaData.getColumns(null, null, "accounts", "is_locked");

            if (!columns.next()) {
                // Add is_locked column
                String addIsLockedColumn = "ALTER TABLE accounts ADD COLUMN is_locked BOOLEAN DEFAULT FALSE";
                conn.createStatement().execute(addIsLockedColumn);
                System.out.println("✅ Added is_locked column to accounts table");
            }

            // Check if lock_reason column exists
            columns = metaData.getColumns(null, null, "accounts", "lock_reason");

            if (!columns.next()) {
                // Add lock_reason column
                String addLockReasonColumn = "ALTER TABLE accounts ADD COLUMN lock_reason VARCHAR(255) DEFAULT NULL";
                conn.createStatement().execute(addLockReasonColumn);
                System.out.println("✅ Added lock_reason column to accounts table");
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to update database schema: " + e.getMessage());
        }
    }
}