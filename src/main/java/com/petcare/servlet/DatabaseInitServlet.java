package com.petcare.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import com.petcare.util.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/init-db")
public class DatabaseInitServlet extends HttpServlet {
    
    private static final String[] CREATE_TABLES = {
        "CREATE TABLE IF NOT EXISTS users (" +
        "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
        "username VARCHAR(255) UNIQUE NOT NULL," +
        "email VARCHAR(255) UNIQUE NOT NULL," +
        "password_hash VARCHAR(255) NOT NULL," +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",
        
        "CREATE TABLE IF NOT EXISTS booking (" +
        "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
        "name VARCHAR(255) NOT NULL," +
        "email VARCHAR(255) NOT NULL," +
        "number VARCHAR(20) NOT NULL," +
        "pet_type VARCHAR(50) NOT NULL," +
        "service_id BIGINT DEFAULT 0," +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci",
        
        "CREATE TABLE IF NOT EXISTS contact_messages (" +
        "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
        "name VARCHAR(255) NOT NULL," +
        "email VARCHAR(255) NOT NULL," +
        "subject VARCHAR(500)," +
        "message TEXT NOT NULL," +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = resp.getWriter();
             Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            StringBuilder result = new StringBuilder();
            result.append("{\"status\":\"success\",\"tables\":[");
            
            for (int i = 0; i < CREATE_TABLES.length; i++) {
                stmt.executeUpdate(CREATE_TABLES[i]);
                if (i > 0) result.append(",");
                result.append("\"").append(i == 0 ? "users" : i == 1 ? "booking" : "contact_messages").append("\"");
            }
            
            result.append("],\"message\":\"Database initialized successfully! All tables created.\"}");
            out.print(result.toString());
            
        } catch (Exception e) {
            resp.setStatus(500);
            try (PrintWriter out = resp.getWriter()) {
                String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Unknown error";
                out.print("{\"status\":\"error\",\"message\":\"Database initialization failed: " + errorMsg + "\"}");
            }
            e.printStackTrace();
        }
    }
}
