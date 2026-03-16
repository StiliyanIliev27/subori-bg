package bg.sabori.dao;

import bg.sabori.db.DatabaseConnection;
import bg.sabori.model.Organizer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizerDAO {

    private static final String SELECT_WITH_SETTLEMENT =
        "SELECT o.id, o.name, o.type, o.contact, o.settlement_id, s.name AS settlement_name " +
        "FROM organizers o " +
        "JOIN settlements s ON o.settlement_id = s.id ";

    public List<Organizer> getAll() throws SQLException {
        List<Organizer> list = new ArrayList<>();
        String sql = SELECT_WITH_SETTLEMENT + "ORDER BY o.name";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Organizer> searchByName(String keyword) throws SQLException {
        List<Organizer> list = new ArrayList<>();
        String sql = SELECT_WITH_SETTLEMENT + "WHERE o.name LIKE ? ORDER BY o.name";
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

    public List<Organizer> searchByType(String type) throws SQLException {
        List<Organizer> list = new ArrayList<>();
        String sql = SELECT_WITH_SETTLEMENT + "WHERE o.type = ? ORDER BY o.name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public void insert(String name, String type, String contact, int settlementId) throws SQLException {
        String sql = "INSERT INTO organizers (name, type, contact, settlement_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, type);
            ps.setString(3, emptyToNull(contact));
            ps.setInt(4, settlementId);
            ps.executeUpdate();
        }
    }

    public void update(int id, String name, String type, String contact, int settlementId) throws SQLException {
        String sql = "UPDATE organizers SET name = ?, type = ?, contact = ?, settlement_id = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, type);
            ps.setString(3, emptyToNull(contact));
            ps.setInt(4, settlementId);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM organizers WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Organizer map(ResultSet rs) throws SQLException {
        return new Organizer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("type"),
            rs.getString("contact"),
            rs.getInt("settlement_id"),
            rs.getString("settlement_name")
        );
    }

    private String emptyToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

