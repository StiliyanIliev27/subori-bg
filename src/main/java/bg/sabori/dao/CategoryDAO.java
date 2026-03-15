package bg.sabori.dao;

import bg.sabori.db.DatabaseConnection;
import bg.sabori.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }

    public List<Category> searchByName(String keyword) throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM categories WHERE name LIKE ? ORDER BY name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return list;
    }

    public void insert(String name) throws SQLException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
        }
    }

    public void update(int id, String newName) throws SQLException {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, newName.trim());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
