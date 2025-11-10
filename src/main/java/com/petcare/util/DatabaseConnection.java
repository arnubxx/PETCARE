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
        String defaultUrl = "jdbc:mysql://localhost:3306/petcare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String jdbcUrl = System.getenv().getOrDefault("PETCARE_DB_URL", defaultUrl);
        String dbUser = System.getenv().getOrDefault("PETCARE_DB_USER", "root");
        String dbPassword = System.getenv().getOrDefault("PETCARE_DB_PASSWORD", "Arnubdatta");
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
