package com.petcare.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import com.petcare.model.ContactMessage;
import com.petcare.model.ContactMessageDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ContactServlet", urlPatterns = {"/contact"})
public class ContactServlet extends HttpServlet {

    private final ContactMessageDAO contactDAO = new ContactMessageDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String subject = req.getParameter("subject");
            String message = req.getParameter("message");

            if (name == null || name.isEmpty() || email == null || email.isEmpty() || message == null || message.isEmpty()) {
                resp.setStatus(400);
                out.print("{\"status\":\"error\",\"message\":\"Name, email, and message are required\"}");
                return;
            }

            ContactMessage msg = new ContactMessage();
            msg.setName(name);
            msg.setEmail(email);
            msg.setSubject(subject);
            msg.setMessage(message);

            long id = contactDAO.create(msg);
            if (id > 0) {
                out.print("{\"status\":\"ok\",\"message\":\"Thank you for contacting us! We will get back to you soon.\"}");
            } else {
                resp.setStatus(500);
                out.print("{\"status\":\"error\",\"message\":\"Failed to send message\"}");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            // Check if user is logged in
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.setStatus(403);
                out.print("{\"status\":\"error\",\"message\":\"Login required\"}");
                return;
            }

            List<ContactMessage> messages = contactDAO.findAll();
            out.print("{\"status\":\"ok\",\"messages\":[");
            for (int i = 0; i < messages.size(); i++) {
                ContactMessage msg = messages.get(i);
                out.print("{\"id\":" + msg.getId() + 
                         ",\"name\":\"" + escapeJson(msg.getName()) + "\"" +
                         ",\"email\":\"" + escapeJson(msg.getEmail()) + "\"" +
                         ",\"subject\":\"" + escapeJson(msg.getSubject()) + "\"" +
                         ",\"message\":\"" + escapeJson(msg.getMessage()) + "\"" +
                         ",\"createdAt\":\"" + msg.getCreatedAt() + "\"}");
                if (i < messages.size() - 1) out.print(",");
            }
            out.print("]}");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
}
