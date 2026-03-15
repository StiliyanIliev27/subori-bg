package bg.sabori.dao;

import bg.sabori.db.DatabaseConnection;
import bg.sabori.model.Settlement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SettlementDAO {

    private static final String SELECT_WITH_REGION =
        "SELECT s.id, s.name, s.type, s.region_id, r.name AS region_name " +
        "FROM settlements s " +
        "JOIN regions r ON s.region_id = r.id ";

    public List<Settlement> getAll() throws SQLException {
        List<Settlement> list = new ArrayList<>();
        String sql = SELECT_WITH_REGION + "ORDER BY r.name, s.name";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Settlement> searchByName(String keyword) throws SQLException {
        List<Settlement> list = new ArrayList<>();
        String sql = SELECT_WITH_REGION + "WHERE s.name LIKE ? ORDER BY s.name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public void insert(String name, String type, int regionId) throws SQLException {
        String sql = "INSERT INTO settlements (name, type, region_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, type);
            ps.setInt(3, regionId);
            ps.executeUpdate();
        }
    }

    public void update(int id, String newName, String newType, int newRegionId) throws SQLException {
        String sql = "UPDATE settlements SET name = ?, type = ?, region_id = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, newName.trim());
            ps.setString(2, newType);
            ps.setInt(3, newRegionId);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM settlements WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Settlement map(ResultSet rs) throws SQLException {
        return new Settlement(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("type"),
            rs.getInt("region_id"),
            rs.getString("region_name")
        );
    }
}
