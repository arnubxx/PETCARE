package com.petcare.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.petcare.util.DatabaseConnection;
import com.petcare.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/reset-password")
public class PasswordResetServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String username = req.getParameter("username");
        String newPassword = req.getParameter("password");
        
        if (username == null || username.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            resp.setStatus(400);
            try (PrintWriter out = resp.getWriter()) {
                out.print("{\"status\":\"error\",\"message\":\"Username and password required\"}");
            }
            return;
        }
        
        try (PrintWriter out = resp.getWriter();
             Connection conn = DatabaseConnection.getConnection()) {
            
            String hashedPassword = PasswordUtil.hash(newPassword);
            String sql = "UPDATE users SET password_hash = ? WHERE username = LOWER(?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, hashedPassword);
                stmt.setString(2, username);
                
                int updated = stmt.executeUpdate();
                
                if (updated > 0) {
                    out.print("{\"status\":\"success\",\"message\":\"Password reset successfully for user: " + username + "\"}");
                } else {
                    resp.setStatus(404);
                    out.print("{\"status\":\"error\",\"message\":\"User not found: " + username + "\"}");
                }
            }
            
        } catch (Exception e) {
            resp.setStatus(500);
            try (PrintWriter out = resp.getWriter()) {
                String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Unknown error";
                out.print("{\"status\":\"error\",\"message\":\"" + errorMsg + "\"}");
            }
        }
    }
}
