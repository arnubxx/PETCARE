package com.petcare;

import java.sql.Connection;

import com.petcare.util.DatabaseConnection;

public class TestDB {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("✅ Connected to MySQL successfully!");
            System.out.println("Database URL: " + conn.getMetaData().getURL());
            System.out.println("Database User: " + conn.getMetaData().getUserName());
            System.out.println("Connection is valid: " + conn.isValid(5));
        } catch (Exception e) {
            System.out.println("❌ Database connection failed:");
            e.printStackTrace();
        }
    }
}