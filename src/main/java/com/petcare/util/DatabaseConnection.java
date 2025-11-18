package com.petcare.util;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection implements Closeable {

    private static HikariDataSource dataSource;
    private static boolean initialized = false;
    private static Exception initException = null;

    private static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            HikariConfig config = new HikariConfig();
            
            String dbHost = System.getenv("MYSQLHOST");
            String dbPort = System.getenv("MYSQLPORT");
            String dbName = System.getenv("MYSQLDATABASE");
            String dbUser = System.getenv("MYSQLUSER");
            String dbPass = System.getenv("MYSQLPASSWORD");

            if (dbHost == null || dbHost.isEmpty()) {
                dbHost = System.getenv("DB_HOST");
                dbPort = System.getenv("DB_PORT");
                dbName = System.getenv("DB_NAME");
                dbUser = System.getenv("DB_USER");
                dbPass = System.getenv("DB_PASSWORD");
            }

            if (dbHost == null || dbHost.isEmpty()) {
                dbHost = "localhost";
                dbPort = "3306";
                dbName = "petcare";
                dbUser = "root";
                dbPass = "Arnubdatta";
            }

            String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    dbHost, dbPort, dbName);

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPass);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
            initialized = true;
            
            System.out.println("Database connection pool initialized successfully");
            System.out.println("Connected to: " + jdbcUrl);
            
        } catch (Exception e) {
            initException = e;
            System.err.println("Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        
        if (dataSource == null) {
            if (initException != null) {
                throw new SQLException("Database initialization failed: " + initException.getMessage(), initException);
            }
            throw new SQLException("DataSource not initialized");
        }
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void close() {
        closeDataSource();
    }
}