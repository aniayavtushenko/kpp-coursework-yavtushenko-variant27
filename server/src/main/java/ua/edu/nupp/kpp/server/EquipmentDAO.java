package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    public void add(Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipment " +
                "(inventory_code, name, category, utilization_percent) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipment.getInventoryCode());
            ps.setString(2, equipment.getName());
            ps.setString(3, equipment.getCategory());
            ps.setDouble(4, equipment.getUtilizationPercent());

            ps.executeUpdate();
        }
    }

    public List<Equipment> getAll() throws SQLException {
        String sql = "SELECT id, inventory_code, name, category, " +
                "utilization_percent FROM equipment ORDER BY id";

        List<Equipment> list = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getLong("id"),
                        rs.getString("inventory_code"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("utilization_percent")
                );

                list.add(e);
            }
        }

        return list;
    }

    public Equipment getById(long id) throws SQLException {
        String sql = "SELECT id, inventory_code, name, category, " +
                "utilization_percent FROM equipment WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Equipment(
                            rs.getLong("id"),
                            rs.getString("inventory_code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("utilization_percent")
                    );
                }
            }
        }

        return null;
    }

    public void update(Equipment equipment) throws SQLException {
        String sql = "UPDATE equipment SET inventory_code = ?, " +
                "name = ?, category = ?, utilization_percent = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipment.getInventoryCode());
            ps.setString(2, equipment.getName());
            ps.setString(3, equipment.getCategory());
            ps.setDouble(4, equipment.getUtilizationPercent());
            ps.setLong(5, equipment.getId());

            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM equipment WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}