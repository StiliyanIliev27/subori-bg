package bg.sabori.ui;

import bg.sabori.dao.EventDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StatisticsPanel extends JPanel {

    private final EventDAO eventDao = new EventDAO();

    private final DefaultTableModel byRegionModel;
    private final DefaultTableModel recurringModel;
    private final DefaultTableModel topOrganizersModel;

    public StatisticsPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton refreshButton = new JButton("Обнови статистиките");
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        byRegionModel = new DefaultTableModel(new String[]{"Регион", "Брой събития"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        recurringModel = new DefaultTableModel(new String[]{"Тип", "Брой"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        topOrganizersModel = new DefaultTableModel(new String[]{"Организатор", "Брой събития"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JPanel center = new JPanel(new GridLayout(1, 3, 8, 8));
        center.add(wrapTable("Брой събития по регион",   byRegionModel,      200, 100));
        center.add(wrapTable("Ежегодни vs еднократни",   recurringModel,     150,  80));
        center.add(wrapTable("Топ организатори",          topOrganizersModel, 220, 100));
        add(center, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadStatistics());

        loadStatistics();
    }

    private JPanel wrapTable(String title, DefaultTableModel model, int col0Width, int col1Width) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setRowSorter(new TableRowSorter<>(model));
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(col0Width);
        table.getColumnModel().getColumn(1).setPreferredWidth(col1Width);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadStatistics() {
        try {
            fillModel(byRegionModel, eventDao.countByRegion());
            fillModel(recurringModel, eventDao.countRecurringVsSingle());
            fillModel(topOrganizersModel, eventDao.topOrganizersByEventCount(10));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillModel(DefaultTableModel model, List<Object[]> data) {
        model.setRowCount(0);
        for (Object[] row : data) {
            model.addRow(row);
        }
    }
}
