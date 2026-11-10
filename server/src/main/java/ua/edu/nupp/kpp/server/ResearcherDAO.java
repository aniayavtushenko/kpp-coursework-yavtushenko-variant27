package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResearcherDAO {

    public void add(Researcher researcher) throws SQLException {
        String sql = "INSERT INTO researcher (full_name, department, email) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, researcher.getFullName());
            ps.setString(2, researcher.getDepartment());
            ps.setString(3, researcher.getEmail());
            ps.executeUpdate();
        }
    }

    public List<Researcher> getAll() throws SQLException {
        String sql = "SELECT id, full_name, department, email FROM researcher ORDER BY id";
        List<Researcher> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Researcher r = new Researcher(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("email")
                );
                list.add(r);
            }
        }
        return list;
    }

    public Researcher getById(long id) throws SQLException {
        String sql = "SELECT id, full_name, department, email FROM researcher WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Researcher(
                            rs.getLong("id"),
                            rs.getString("full_name"),
                            rs.getString("department"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public void update(Researcher researcher) throws SQLException {
        String sql = "UPDATE researcher SET full_name = ?, department = ?, email = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, researcher.getFullName());
            ps.setString(2, researcher.getDepartment());
            ps.setString(3, researcher.getEmail());
            ps.setLong(4, researcher.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM researcher WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}