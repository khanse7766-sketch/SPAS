package com.spas.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton connection manager for database access.
 */
public class DBManager {
    private static DBManager instance;
    private Connection connection;

    private DBManager() {
        try {
            // Load driver just in case
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DBConfig.DB_URL, DBConfig.User, DBConfig.Password);
            runLightweightMigrations();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            // Do not throw runtime error here to allow UI to show gracefully
        }
    }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new DBManager();
                }
            } catch (SQLException e) {
                instance = new DBManager();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void runLightweightMigrations() {
        if (connection == null) return;
        try (var stmt = connection.createStatement()) {
            stmt.executeUpdate("ALTER TABLE attendance MODIFY status ENUM('PRESENT','ABSENT','LATE','LEAVE','HOLIDAY') NOT NULL");
        } catch (SQLException ignored) {
            // Keep startup resilient if the table is not created yet or the user has limited privileges.
        }
    }
}
