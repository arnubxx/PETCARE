package com.petcare.util;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection implements Closeable {
    private static HikariDataSource ds;
    
    static {
        HikariConfig cfg = new HikariConfig();
        
        // Support Render.com, Railway, and custom environment variables
        String jdbcUrl;
        String dbUser;
        String dbPassword;
        
        // Check if Render.com variables exist (DB_HOST is common for Render)
        String dbHost = System.getenv("DB_HOST");
        if (dbHost != null && !dbHost.isEmpty()) {
            // Render.com deployment - use DB_HOST, DB_PORT, DB_NAME
            String dbPort = System.getenv().getOrDefault("DB_PORT", "3306");
            String dbName = System.getenv().getOrDefault("DB_NAME", "petcare");
            jdbcUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + 
                      "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            dbUser = System.getenv().getOrDefault("DB_USER", "root");
            dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "");
        } else {
            // Check if Railway variables exist
            String mysqlHost = System.getenv("MYSQLHOST");
            if (mysqlHost != null && !mysqlHost.isEmpty()) {
                // Railway deployment - use their auto-provided variables
                String mysqlPort = System.getenv().getOrDefault("MYSQLPORT", "3306");
                String mysqlDatabase = System.getenv().getOrDefault("MYSQLDATABASE", "railway");
                jdbcUrl = "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDatabase + 
                          "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                dbUser = System.getenv().getOrDefault("MYSQLUSER", "root");
                dbPassword = System.getenv().getOrDefault("MYSQLPASSWORD", "");
            } else {
                // Local development
                String defaultUrl = "jdbc:mysql://localhost:3306/petcare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                jdbcUrl = System.getenv().getOrDefault("PETCARE_DB_URL", defaultUrl);
                dbUser = System.getenv().getOrDefault("PETCARE_DB_USER", "root");
                dbPassword = System.getenv().getOrDefault("PETCARE_DB_PASSWORD", "Arnubdatta");
            }
        }
        
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(dbUser);
        cfg.setPassword(dbPassword);
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setPoolName("petcare-pool");
        ds = new HikariDataSource(cfg);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static DataSource getDataSource() { return ds; }

    @Override
    public void close() {
        if (ds != null) ds.close();
    }
}
