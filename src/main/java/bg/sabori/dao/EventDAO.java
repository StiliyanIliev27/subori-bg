package bg.sabori.dao;

import bg.sabori.db.DatabaseConnection;
import bg.sabori.model.Event;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private static final String SELECT_WITH_JOINS =
        "SELECT e.id, e.name, e.event_date, e.is_recurring, e.description, " +
        "e.settlement_id, e.category_id, e.organizer_id, " +
        "s.name AS settlement_name, c.name AS category_name, o.name AS organizer_name " +
        "FROM events e " +
        "JOIN settlements s ON e.settlement_id = s.id " +
        "JOIN categories c ON e.category_id = c.id " +
        "JOIN organizers o ON e.organizer_id = o.id ";

    public List<Event> getAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS + "ORDER BY e.event_date DESC, e.name";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapEvent(rs));
            }
        }
        return list;
    }

    public List<Event> searchByDate(LocalDate date) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS + "WHERE e.event_date = ? ORDER BY e.name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvent(rs));
                }
            }
        }
        return list;
    }

    public List<Event> searchByMonth(int month) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS + "WHERE MONTH(e.event_date) = ? ORDER BY DAY(e.event_date), e.name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvent(rs));
                }
            }
        }
        return list;
    }

    public void insert(String name, LocalDate eventDate, boolean isRecurring, String description,
                       int settlementId, int categoryId, int organizerId) throws SQLException {
        String sql = "INSERT INTO events (name, event_date, is_recurring, description, settlement_id, category_id, organizer_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setDate(2, Date.valueOf(eventDate));
            ps.setBoolean(3, isRecurring);
            ps.setString(4, emptyToNull(description));
            ps.setInt(5, settlementId);
            ps.setInt(6, categoryId);
            ps.setInt(7, organizerId);
            ps.executeUpdate();
        }
    }

    public void update(int id, String name, LocalDate eventDate, boolean isRecurring, String description,
                       int settlementId, int categoryId, int organizerId) throws SQLException {
        String sql = "UPDATE events SET name = ?, event_date = ?, is_recurring = ?, description = ?, " +
                     "settlement_id = ?, category_id = ?, organizer_id = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setDate(2, Date.valueOf(eventDate));
            ps.setBoolean(3, isRecurring);
            ps.setString(4, emptyToNull(description));
            ps.setInt(5, settlementId);
            ps.setInt(6, categoryId);
            ps.setInt(7, organizerId);
            ps.setInt(8, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Event> findByRegionAndMonth(int regionId, int month) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS +
            "JOIN regions r ON s.region_id = r.id " +
            "WHERE r.id = ? AND MONTH(e.event_date) = ? " +
            "ORDER BY e.event_date, e.name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, regionId);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvent(rs));
                }
            }
        }
        return list;
    }

    public List<Event> findByCategoryAndSettlementType(int categoryId, String settlementType) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS +
            "WHERE e.category_id = ? AND s.type = ? " +
            "ORDER BY e.event_date, e.name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, settlementType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvent(rs));
                }
            }
        }
        return list;
    }

    public List<Event> findByOrganizerTypeAndRegion(String organizerType, int regionId) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS +
            "JOIN regions r ON s.region_id = r.id " +
            "WHERE o.type = ? AND r.id = ? " +
            "ORDER BY e.event_date, e.name";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, organizerType);
            ps.setInt(2, regionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvent(rs));
                }
            }
        }
        return list;
    }

    public List<Object[]> countByRegion() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT r.name AS region_name, COUNT(e.id) AS event_count " +
                     "FROM regions r " +
                     "LEFT JOIN settlements s ON s.region_id = r.id " +
                     "LEFT JOIN events e ON e.settlement_id = s.id " +
                     "GROUP BY r.id, r.name " +
                     "ORDER BY event_count DESC, r.name";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("region_name"), rs.getInt("event_count")});
            }
        }
        return list;
    }

    public List<Object[]> countRecurringVsSingle() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT is_recurring, COUNT(*) AS event_count FROM events GROUP BY is_recurring";
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                boolean recurring = rs.getBoolean("is_recurring");
                String label = recurring ? "Ежегодни" : "Еднократни";
                list.add(new Object[]{label, rs.getInt("event_count")});
            }
        }
        return list;
    }

    public List<Object[]> topOrganizersByEventCount(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT o.name, COUNT(e.id) AS event_count " +
                     "FROM organizers o " +
                     "LEFT JOIN events e ON e.organizer_id = o.id " +
                     "GROUP BY o.id, o.name " +
                     "ORDER BY event_count DESC, o.name " +
                     "LIMIT ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString("name"), rs.getInt("event_count")});
                }
            }
        }
        return list;
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        return new Event(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getDate("event_date").toLocalDate(),
            rs.getBoolean("is_recurring"),
            rs.getString("description"),
            rs.getInt("settlement_id"),
            rs.getInt("category_id"),
            rs.getInt("organizer_id"),
            rs.getString("settlement_name"),
            rs.getString("category_name"),
            rs.getString("organizer_name")
        );
    }

    private String emptyToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

