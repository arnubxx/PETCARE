package com.petcare.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.petcare.util.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/view-user")
public class ViewUserServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String username = req.getParameter("username");
        
        if (username == null || username.isEmpty()) {
            resp.setStatus(400);
            try (PrintWriter out = resp.getWriter()) {
                out.print("{\"status\":\"error\",\"message\":\"Username required\"}");
            }
            return;
        }
        
        try (PrintWriter out = resp.getWriter();
             Connection conn = DatabaseConnection.getConnection()) {
            
            String sql = "SELECT username, email, created_at FROM users WHERE username = LOWER(?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String foundUsername = rs.getString("username");
                        String email = rs.getString("email");
                        String createdAt = rs.getString("created_at");
                        
                        out.print("{");
                        out.print("\"status\":\"success\",");
                        out.print("\"username\":\"" + foundUsername + "\",");
                        out.print("\"email\":\"" + (email != null ? email : "") + "\",");
                        out.print("\"created_at\":\"" + (createdAt != null ? createdAt : "") + "\",");
                        out.print("\"note\":\"Password is hashed with BCrypt for security - cannot be displayed\"");
                        out.print("}");
                    } else {
                        resp.setStatus(404);
                        out.print("{\"status\":\"error\",\"message\":\"User not found: " + username + "\"}");
                    }
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
