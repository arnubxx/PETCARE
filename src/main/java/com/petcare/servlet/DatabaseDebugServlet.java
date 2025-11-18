package com.petcare.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import com.petcare.util.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/debug-db")
public class DatabaseDebugServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = resp.getWriter()) {
            String mysqlHost = System.getenv("MYSQLHOST");
            String mysqlPort = System.getenv("MYSQLPORT");
            String dbHost = System.getenv("DB_HOST");
            String dbPort = System.getenv("DB_PORT");
            String dbName = System.getenv("DB_NAME");
            String dbUser = System.getenv("DB_USER");
            String dbPass = System.getenv("DB_PASSWORD");
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"environment\":{");
            json.append("\"MYSQLHOST\":\"").append(mysqlHost != null ? mysqlHost : "NOT_SET").append("\",");
            json.append("\"MYSQLPORT\":\"").append(mysqlPort != null ? mysqlPort : "NOT_SET").append("\",");
            json.append("\"DB_HOST\":\"").append(dbHost != null ? dbHost : "NOT_SET").append("\",");
            json.append("\"DB_PORT\":\"").append(dbPort != null ? dbPort : "NOT_SET").append("\",");
            json.append("\"DB_NAME\":\"").append(dbName != null ? dbName : "NOT_SET").append("\",");
            json.append("\"DB_USER\":\"").append(dbUser != null ? dbUser : "NOT_SET").append("\",");
            json.append("\"DB_PASSWORD\":\"").append(dbPass != null ? "***SET***" : "NOT_SET").append("\"");
            json.append("},");
            
            json.append("\"connection\":{");
            try {
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null && !conn.isClosed()) {
                    json.append("\"status\":\"SUCCESS\",");
                    json.append("\"message\":\"Database connected successfully\"");
                    conn.close();
                } else {
                    json.append("\"status\":\"FAILED\",");
                    json.append("\"message\":\"Connection is null or closed\"");
                }
            } catch (Exception e) {
                json.append("\"status\":\"ERROR\",");
                json.append("\"message\":\"").append(e.getMessage().replace("\"", "'")).append("\"");
            }
            json.append("}");
            json.append("}");
            
            out.print(json.toString());
            
        } catch (Exception e) {
            resp.setStatus(500);
            try (PrintWriter out = resp.getWriter()) {
                out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
            }
        }
    }
}
