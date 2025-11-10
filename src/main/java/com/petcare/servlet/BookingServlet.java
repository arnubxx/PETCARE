package com.petcare.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import com.petcare.model.Booking;
import com.petcare.model.BookingDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "BookingServlet", urlPatterns = {"/booking"})
public class BookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.setStatus(401);
                out.print("{\"status\":\"unauthorized\",\"message\":\"signin required\"}");
                return;
            }

            // Fetch all bookings for the user
            List<Booking> bookings = bookingDAO.findAll();
            
            // Build JSON response
            StringBuilder json = new StringBuilder("{\"status\":\"ok\",\"bookings\":[");
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                if (i > 0) json.append(",");
                json.append("{\"id\":").append(b.getId())
                    .append(",\"name\":\"").append(escapeJson(b.getName())).append("\"")
                    .append(",\"email\":\"").append(escapeJson(b.getEmail())).append("\"")
                    .append(",\"number\":\"").append(escapeJson(b.getNumber())).append("\"")
                    .append(",\"petType\":\"").append(escapeJson(b.getPetType())).append("\"")
                    .append(",\"serviceId\":").append(b.getServiceId())
                    .append("}");
            }
            json.append("]}");
            out.print(json.toString());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.setStatus(401);
                out.print("{\"status\":\"unauthorized\",\"message\":\"signin required\"}");
                return;
            }

            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String number = req.getParameter("number");
            String petType = req.getParameter("petType");
            String serviceIdStr = req.getParameter("serviceId");
            long serviceId = 0;
            try { serviceId = Long.parseLong(serviceIdStr); } catch (Exception ignored) {}

            if (name == null || name.isEmpty()) {
                resp.setStatus(400);
                out.print("{\"status\":\"error\",\"message\":\"name required\"}");
                return;
            }

            Booking b = new Booking(0, name, email, number, petType, serviceId);
            long id = bookingDAO.create(b);
            if (id > 0) {
                out.print("{\"status\":\"ok\",\"id\":"+id+"}");
            } else {
                resp.setStatus(500);
                out.print("{\"status\":\"error\"}");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        
        // Read body parameters for PUT request
        String body = new String(req.getInputStream().readAllBytes());
        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (String param : body.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                params.put(java.net.URLDecoder.decode(pair[0], "UTF-8"), 
                          java.net.URLDecoder.decode(pair[1], "UTF-8"));
            }
        }
        
        try (PrintWriter out = resp.getWriter()) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.setStatus(401);
                out.print("{\"status\":\"unauthorized\",\"message\":\"signin required\"}");
                return;
            }

            String idStr = params.get("id");
            String name = params.get("name");
            String email = params.get("email");
            String number = params.get("number");
            String petType = params.get("petType");
            String serviceIdStr = params.get("serviceId");
            
            long id = 0;
            long serviceId = 0;
            try { id = Long.parseLong(idStr); } catch (Exception e) {
                resp.setStatus(400);
                out.print("{\"status\":\"error\",\"message\":\"invalid id\"}");
                return;
            }
            try { serviceId = Long.parseLong(serviceIdStr); } catch (Exception ignored) {}

            if (name == null || name.isEmpty()) {
                resp.setStatus(400);
                out.print("{\"status\":\"error\",\"message\":\"name required\"}");
                return;
            }

            Booking b = new Booking(id, name, email, number, petType, serviceId);
            boolean success = bookingDAO.update(b);
            if (success) {
                out.print("{\"status\":\"ok\"}");
            } else {
                resp.setStatus(500);
                out.print("{\"status\":\"error\",\"message\":\"update failed\"}");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        
        // Read body parameters for DELETE request
        String body = new String(req.getInputStream().readAllBytes());
        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (String param : body.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                params.put(java.net.URLDecoder.decode(pair[0], "UTF-8"), 
                          java.net.URLDecoder.decode(pair[1], "UTF-8"));
            }
        }
        
        try (PrintWriter out = resp.getWriter()) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.setStatus(401);
                out.print("{\"status\":\"unauthorized\",\"message\":\"signin required\"}");
                return;
            }

            String idStr = params.get("id");
            long id = 0;
            try { id = Long.parseLong(idStr); } catch (Exception e) {
                resp.setStatus(400);
                out.print("{\"status\":\"error\",\"message\":\"invalid id\"}");
                return;
            }

            boolean success = bookingDAO.delete(id);
            if (success) {
                out.print("{\"status\":\"ok\"}");
            } else {
                resp.setStatus(500);
                out.print("{\"status\":\"error\",\"message\":\"delete failed\"}");
            }
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
