package bg.sabori.ui;

import bg.sabori.dao.CategoryDAO;
import bg.sabori.dao.EventDAO;
import bg.sabori.dao.RegionDAO;
import bg.sabori.model.Category;
import bg.sabori.model.Event;
import bg.sabori.model.Region;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SearchPanel extends JPanel {

    private final EventDAO    eventDao    = new EventDAO();
    private final RegionDAO   regionDao   = new RegionDAO();
    private final CategoryDAO categoryDao = new CategoryDAO();

    private JComboBox<Region> regionMonthRegionCombo;
    private JComboBox<String> regionMonthCombo;
    private JButton           btnRegionMonth;

    private JComboBox<Category> categoryCombo;
    private JComboBox<String>   settlementTypeCombo;
    private JButton             btnCategorySettlementType;

    private JComboBox<String> organizerTypeCombo;
    private JComboBox<Region> organizerTypeRegionCombo;
    private JButton           btnOrganizerTypeRegion;

    private final DefaultTableModel tableModel;
    private final JTable            table;

    private List<Region>   regions;
    private List<Category> categories;

    public SearchPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtersPanel = new JPanel(new GridLayout(3, 1, 0, 6));
        filtersPanel.add(buildRegionMonthBlock());
        filtersPanel.add(buildCategorySettlementTypeBlock());
        filtersPanel.add(buildOrganizerTypeRegionBlock());
        add(filtersPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new String[]{"Събитие", "Дата", "Ежегодно", "Категория", "Населено място", "Организатор"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadLookups();
        clearResults();
    }

    private JPanel buildRegionMonthBlock() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Справка: Регион + Месец"));

        regionMonthRegionCombo = new JComboBox<>();
        regionMonthCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        btnRegionMonth = new JButton("Покажи");

        panel.add(new JLabel("Регион:"));
        panel.add(regionMonthRegionCombo);
        panel.add(new JLabel("Месец:"));
        panel.add(regionMonthCombo);
        panel.add(btnRegionMonth);

        btnRegionMonth.addActionListener(e -> onSearchRegionMonth());
        return panel;
    }

    private JPanel buildCategorySettlementTypeBlock() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Справка: Категория + Тип населено място"));

        categoryCombo = new JComboBox<>();
        settlementTypeCombo = new JComboBox<>(new String[]{"село", "град"});
        btnCategorySettlementType = new JButton("Покажи");

        panel.add(new JLabel("Категория:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Тип населено място:"));
        panel.add(settlementTypeCombo);
        panel.add(btnCategorySettlementType);

        btnCategorySettlementType.addActionListener(e -> onSearchCategorySettlementType());
        return panel;
    }

    private JPanel buildOrganizerTypeRegionBlock() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Справка: Тип организатор + Регион"));

        organizerTypeCombo = new JComboBox<>(new String[]{"Община", "Читалище", "Частен"});
        organizerTypeRegionCombo = new JComboBox<>();
        btnOrganizerTypeRegion = new JButton("Покажи");

        panel.add(new JLabel("Тип организатор:"));
        panel.add(organizerTypeCombo);
        panel.add(new JLabel("Регион:"));
        panel.add(organizerTypeRegionCombo);
        panel.add(btnOrganizerTypeRegion);

        btnOrganizerTypeRegion.addActionListener(e -> onSearchOrganizerTypeRegion());
        return panel;
    }

    private void loadLookups() {
        try {
            regions = regionDao.getAll();
            categories = categoryDao.getAll();

            regionMonthRegionCombo.removeAllItems();
            organizerTypeRegionCombo.removeAllItems();
            for (Region region : regions) {
                regionMonthRegionCombo.addItem(region);
                organizerTypeRegionCombo.addItem(region);
            }

            categoryCombo.removeAllItems();
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearchRegionMonth() {
        Region region = (Region) regionMonthRegionCombo.getSelectedItem();
        if (region == null) { showWarn("Изберете регион."); return; }

        int month = regionMonthCombo.getSelectedIndex() + 1;
        try {
            refreshResults(eventDao.findByRegionAndMonth(region.getId(), month));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearchCategorySettlementType() {
        Category category = (Category) categoryCombo.getSelectedItem();
        if (category == null) { showWarn("Изберете категория."); return; }

        String settlementType = (String) settlementTypeCombo.getSelectedItem();
        try {
            refreshResults(eventDao.findByCategoryAndSettlementType(category.getId(), settlementType));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearchOrganizerTypeRegion() {
        String organizerType = (String) organizerTypeCombo.getSelectedItem();
        Region region = (Region) organizerTypeRegionCombo.getSelectedItem();
        if (region == null) { showWarn("Изберете регион."); return; }

        try {
            refreshResults(eventDao.findByOrganizerTypeAndRegion(organizerType, region.getId()));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refreshResults(List<Event> data) {
        tableModel.setRowCount(0);
        for (Event event : data) {
            tableModel.addRow(new Object[]{
                event.getName(),
                event.getEventDate(),
                event.isRecurring() ? "Да" : "Не",
                event.getCategoryName(),
                event.getSettlementName(),
                event.getOrganizerName()
            });
        }
    }

    private void clearResults() {
        tableModel.setRowCount(0);
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Внимание", JOptionPane.WARNING_MESSAGE);
    }
}
