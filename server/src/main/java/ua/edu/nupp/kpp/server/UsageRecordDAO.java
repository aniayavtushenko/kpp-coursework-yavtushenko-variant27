package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsageRecordDAO {

    public void add(UsageRecord record) throws SQLException {
        String sql = "INSERT INTO usage_record (booking_id, description, recorded_date) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, record.getBookingId());
            ps.setString(2, record.getDescription());
            ps.setString(3, record.getRecordedDate());
            ps.executeUpdate();
        }
    }

    public List<UsageRecord> getAll() throws SQLException {
        String sql = "SELECT id, booking_id, description, recorded_date FROM usage_record ORDER BY id";
        List<UsageRecord> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                UsageRecord ur = new UsageRecord(
                        rs.getLong("id"),
                        rs.getLong("booking_id"),
                        rs.getString("description"),
                        rs.getString("recorded_date")
                );
                list.add(ur);
            }
        }
        return list;
    }

    public UsageRecord getById(long id) throws SQLException {
        String sql = "SELECT id, booking_id, description, recorded_date FROM usage_record WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UsageRecord(
                            rs.getLong("id"),
                            rs.getLong("booking_id"),
                            rs.getString("description"),
                            rs.getString("recorded_date")
                    );
                }
            }
        }
        return null;
    }

    public void update(UsageRecord record) throws SQLException {
        String sql = "UPDATE usage_record SET booking_id = ?, description = ?, recorded_date = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, record.getBookingId());
            ps.setString(2, record.getDescription());
            ps.setString(3, record.getRecordedDate());
            ps.setLong(4, record.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM usage_record WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}