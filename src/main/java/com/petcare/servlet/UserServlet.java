package com.petcare.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import com.petcare.model.User;
import com.petcare.model.UserDAO;
import com.petcare.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "UserServlet", urlPatterns = {"/signin", "/signup", "/whoami", "/logout"})
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            if ("/signup".equals(path)) {
                String username = req.getParameter("username");
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                    resp.setStatus(400);
                    out.print("{\"status\":\"error\",\"message\":\"username and password required\"}");
                    return;
                }
                if (password.length() < 6) {
                    resp.setStatus(400);
                    out.print("{\"status\":\"error\",\"message\":\"password must be at least 6 characters\"}");
                    return;
                }
                if (email != null && !email.isEmpty() && !email.contains("@")) {
                    resp.setStatus(400);
                    out.print("{\"status\":\"error\",\"message\":\"invalid email\"}");
                    return;
                }
                // Convert username to lowercase for case-insensitive storage
                username = username.toLowerCase();
                // prevent duplicate usernames
                User existing = userDAO.findByUsername(username);
                if (existing != null) {
                    resp.setStatus(409);
                    out.print("{\"status\":\"exists\"}");
                    return;
                }
                User u = new User();
                u.setUsername(username);
                u.setEmail(email);
                u.setPasswordHash(PasswordUtil.hash(password));
                long id = userDAO.create(u);
                if (id > 0) {
                    out.print("{\"status\":\"ok\",\"id\":"+id+"}");
                } else {
                    resp.setStatus(500);
                    out.print("{\"status\":\"error\",\"message\":\"failed to create user\"}");
                }
            } else if ("/signin".equals(path)) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                    resp.setStatus(400);
                    out.print("{\"status\":\"error\",\"message\":\"username and password required\"}");
                    return;
                }
                // Convert username to lowercase for case-insensitive lookup
                username = username.toLowerCase();
                User u = userDAO.findByUsername(username);
                if (u != null && PasswordUtil.verify(password, u.getPasswordHash())) {
                    HttpSession session = req.getSession(true);
                    session.setAttribute("user", u.getUsername());
                    out.print("{\"status\":\"ok\"}");
                } else {
                    resp.setStatus(401);
                    out.print("{\"status\":\"unauthorized\",\"message\":\"invalid credentials\"}");
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            if ("/whoami".equals(path)) {
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(200);
                    out.print("{\"loggedIn\":false}");
                } else {
                    String user = (String) session.getAttribute("user");
                    out.print("{\"loggedIn\":true,\"user\":\"" + user + "\"}");
                }
            } else if ("/logout".equals(path)) {
                HttpSession session = req.getSession(false);
                if (session != null) session.invalidate();
                out.print("{\"status\":\"ok\"}");
            } else {
                resp.setStatus(404);
                out.print("{\"status\":\"error\",\"message\":\"not found\"}");
            }
        }
    }
}
