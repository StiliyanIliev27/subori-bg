package bg.sabori.dao;

import bg.sabori.db.DatabaseConnection;
import bg.sabori.model.Region;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegionDAO {

    public List<Region> getAll() throws SQLException {
        List<Region> list = new ArrayList<>();
        String sql = "SELECT id, name FROM regions ORDER BY name";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Region(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }

    public List<Region> searchByName(String keyword) throws SQLException {
        List<Region> list = new ArrayList<>();
        String sql = "SELECT id, name FROM regions WHERE name LIKE ? ORDER BY name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Region(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return list;
    }

    public void insert(String name) throws SQLException {
        String sql = "INSERT INTO regions (name) VALUES (?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
        }
    }

    public void update(int id, String newName) throws SQLException {
        String sql = "UPDATE regions SET name = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, newName.trim());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM regions WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
