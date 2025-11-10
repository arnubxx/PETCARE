package com.petcare.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.petcare.util.DatabaseConnection;

public class BookingDAO {
    private static final String INSERT_SQL = "INSERT INTO booking (name, email, number, pet_type, service_id) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT id, name, email, number, pet_type, service_id FROM booking";
    private static final String UPDATE_SQL = "UPDATE booking SET name = ?, email = ?, number = ?, pet_type = ?, service_id = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM booking WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT id, name, email, number, pet_type, service_id FROM booking WHERE id = ?";

    public long create(Booking b) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getName());
            ps.setString(2, b.getEmail());
            ps.setString(3, b.getNumber());
            ps.setString(4, b.getPetType());
            ps.setLong(5, b.getServiceId());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1;
    }

    public List<Booking> findAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Booking b = new Booking(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("number"),
                    rs.getString("pet_type"),
                    rs.getLong("service_id")
                );
                bookings.add(b);
            }
        }
        return bookings;
    }

    public Booking findById(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Booking(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("number"),
                        rs.getString("pet_type"),
                        rs.getLong("service_id")
                    );
                }
            }
        }
        return null;
    }

    public boolean update(Booking b) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, b.getName());
            ps.setString(2, b.getEmail());
            ps.setString(3, b.getNumber());
            ps.setString(4, b.getPetType());
            ps.setLong(5, b.getServiceId());
            ps.setLong(6, b.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }
}
