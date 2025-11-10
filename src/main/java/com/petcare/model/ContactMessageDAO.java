package com.petcare.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.petcare.util.DatabaseConnection;

public class ContactMessageDAO {
    private static final String INSERT_SQL = "INSERT INTO contact_messages (name, email, subject, message) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT id, name, email, subject, message, created_at FROM contact_messages ORDER BY created_at DESC";

    public long create(ContactMessage msg) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, msg.getName());
            ps.setString(2, msg.getEmail());
            ps.setString(3, msg.getSubject());
            ps.setString(4, msg.getMessage());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1;
    }

    public List<ContactMessage> findAll() throws SQLException {
        List<ContactMessage> messages = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ContactMessage msg = new ContactMessage();
                msg.setId(rs.getLong("id"));
                msg.setName(rs.getString("name"));
                msg.setEmail(rs.getString("email"));
                msg.setSubject(rs.getString("subject"));
                msg.setMessage(rs.getString("message"));
                msg.setCreatedAt(rs.getTimestamp("created_at"));
                messages.add(msg);
            }
        }
        return messages;
    }
}
