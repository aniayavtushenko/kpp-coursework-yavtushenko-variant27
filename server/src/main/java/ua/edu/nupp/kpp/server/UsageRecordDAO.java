
package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsageRecordDAO {

    public void add(UsageRecord record) throws SQLException {
        String sql = "INSERT INTO usage_record "
                + "(equipment_id, booking_id, record_date, v1, v2, v3, utilization_score) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, record.getEquipmentId());
            ps.setLong(2, record.getBookingId());
            ps.setString(3, record.getRecordDate());
            ps.setDouble(4, record.getV1());
            ps.setDouble(5, record.getV2());
            ps.setDouble(6, record.getV3());
            ps.setDouble(7, record.getUtilizationScore());

            ps.executeUpdate();
        }
    }

    public List<UsageRecord> getAll() throws SQLException {
        String sql = "SELECT id, equipment_id, booking_id, record_date, "
                + "v1, v2, v3, utilization_score "
                + "FROM usage_record ORDER BY id";

        List<UsageRecord> list = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UsageRecord ur = new UsageRecord(
                        rs.getLong("id"),
                        rs.getLong("equipment_id"),
                        rs.getLong("booking_id"),
                        rs.getString("record_date"),
                        rs.getDouble("v1"),
                        rs.getDouble("v2"),
                        rs.getDouble("v3"),
                        rs.getDouble("utilization_score")
                );

                list.add(ur);
            }
        }

        return list;
    }

    public UsageRecord getById(long id) throws SQLException {
        String sql = "SELECT id, equipment_id, booking_id, record_date, "
                + "v1, v2, v3, utilization_score "
                + "FROM usage_record WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UsageRecord(
                            rs.getLong("id"),
                            rs.getLong("equipment_id"),
                            rs.getLong("booking_id"),
                            rs.getString("record_date"),
                            rs.getDouble("v1"),
                            rs.getDouble("v2"),
                            rs.getDouble("v3"),
                            rs.getDouble("utilization_score")
                    );
                }
            }
        }

        return null;
    }

    public void update(UsageRecord record) throws SQLException {
        String sql = "UPDATE usage_record SET "
                + "equipment_id = ?, booking_id = ?, record_date = ?, "
                + "v1 = ?, v2 = ?, v3 = ?, utilization_score = ? "
                + "WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, record.getEquipmentId());
            ps.setLong(2, record.getBookingId());
            ps.setString(3, record.getRecordDate());
            ps.setDouble(4, record.getV1());
            ps.setDouble(5, record.getV2());
            ps.setDouble(6, record.getV3());
            ps.setDouble(7, record.getUtilizationScore());
            ps.setLong(8, record.getId());

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
