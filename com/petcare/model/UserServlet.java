package com.petcare.model;
import com.petcare.model.DatabaseConnection;
import com.petcare.model.User;
import com.petcare.model.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/signin".equals(path)) {
            req.getRequestDispatcher("/signin.jsp").forward(req, resp);
        } else if ("/signup".equals(path)) {
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if ("/signin".equals(path)) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");

                String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    req.getSession().setAttribute("user", new User(
                        (int) rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                    ));
                    resp.sendRedirect("/petcare");
                } else {
                    req.setAttribute("error", "Invalid username or password");
                    req.getRequestDispatcher("/signin.jsp").forward(req, resp);
                }
            } else if ("/signup".equals(path)) {
                String username = req.getParameter("username");
                String email = req.getParameter("email");
                String password = req.getParameter("password");

                String sql = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.executeUpdate();

                resp.sendRedirect("/petcare/signin");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "An error occurred");
            req.getRequestDispatcher(path + ".jsp").forward(req, resp);
        }
    }
}
