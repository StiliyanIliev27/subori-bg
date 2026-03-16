package bg.sabori.ui;

import bg.sabori.dao.OrganizerDAO;
import bg.sabori.dao.SettlementDAO;
import bg.sabori.model.Organizer;
import bg.sabori.model.Settlement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Панелът предоставя CRUD операции за таблица organizers.
 * Използва: OrganizerDAO, SettlementDAO (за dropdown на населено място).
 */
public class OrganizersPanel extends JPanel {

    private static final String[] ORGANIZER_TYPES = {"Община", "Читалище", "Частен"};

    private final OrganizerDAO  dao           = new OrganizerDAO();
    private final SettlementDAO settlementDao = new SettlementDAO();

    private final DefaultTableModel tableModel;
    private final JTable            table;
    private final JTextField        searchField;
    private final JComboBox<String> searchTypeCombo;
    private final JTextField        nameField;
    private final JComboBox<String> typeCombo;
    private final JTextField        contactField;
    private final JComboBox<Settlement> settlementCombo;
    private final JButton           btnAdd;
    private final JButton           btnUpdate;
    private final JButton           btnDelete;
    private final JButton           btnSearch;
    private final JButton           btnShowAll;

    private List<Organizer>  currentData;
    private List<Settlement> settlements;

    public OrganizersPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
            new String[]{"Име", "Тип", "Контакт", "Населено място"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchField = new JTextField(16);
        searchTypeCombo = new JComboBox<>(new String[]{"Всички типове", "Община", "Читалище", "Частен"});
        btnSearch = new JButton("Търси");
        btnShowAll = new JButton("Покажи всички");
        searchPanel.add(new JLabel("Търсене по име:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Тип:"));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Данни за организатор"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Име:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        nameField = new JTextField(24);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Тип:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        typeCombo = new JComboBox<>(ORGANIZER_TYPES);
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Контакт:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contactField = new JTextField(24);
        formPanel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Населено място:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        settlementCombo = new JComboBox<>();
        formPanel.add(settlementCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnAdd = new JButton("Добави");
        btnUpdate = new JButton("Редактирай");
        btnDelete = new JButton("Изтрий");
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        formPanel.add(btnPanel, gbc);

        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.add(searchPanel, BorderLayout.NORTH);
        south.add(formPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnSearch.addActionListener(e -> onSearch());
        btnShowAll.addActionListener(e -> loadAll());

        loadSettlements();
        loadAll();
    }

    private void loadSettlements() {
        try {
            settlements = settlementDao.getAll();
            settlementCombo.removeAllItems();
            for (Settlement s : settlements) {
                settlementCombo.addItem(s);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadAll() {
        try {
            currentData = dao.getAll();
            refresh(currentData);
            searchField.setText("");
            searchTypeCombo.setSelectedIndex(0);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refresh(List<Organizer> data) {
        tableModel.setRowCount(0);
        for (Organizer o : data) {
            tableModel.addRow(new Object[]{o.getName(), o.getType(), o.getContact(), o.getSettlementName()});
        }
        clearForm();
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Organizer organizer = currentData.get(row);
        nameField.setText(organizer.getName());
        typeCombo.setSelectedItem(organizer.getType());
        contactField.setText(organizer.getContact() == null ? "" : organizer.getContact());

        for (int i = 0; i < settlements.size(); i++) {
            if (settlements.get(i).getId() == organizer.getSettlementId()) {
                settlementCombo.setSelectedIndex(i);
                break;
            }
        }

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void onAdd() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { showWarn("Въведете име."); return; }

        Settlement settlement = (Settlement) settlementCombo.getSelectedItem();
        if (settlement == null) { showWarn("Изберете населено място."); return; }

        try {
            dao.insert(name, (String) typeCombo.getSelectedItem(), contactField.getText(), settlement.getId());
            loadSettlements();
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String name = nameField.getText().trim();
        if (name.isEmpty()) { showWarn("Въведете име."); return; }

        Settlement settlement = (Settlement) settlementCombo.getSelectedItem();
        if (settlement == null) { showWarn("Изберете населено място."); return; }

        try {
            dao.update(currentData.get(row).getId(), name, (String) typeCombo.getSelectedItem(),
                contactField.getText(), settlement.getId());
            loadSettlements();
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Organizer organizer = currentData.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Изтриване на организатор \"" + organizer.getName() + "\"?",
            "Потвърждение", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.delete(organizer.getId());
            loadSettlements();
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearch() {
        String keyword = searchField.getText().trim();
        String selectedType = (String) searchTypeCombo.getSelectedItem();

        try {
            if (!keyword.isEmpty()) {
                currentData = dao.searchByName(keyword);
                if (selectedType != null && !"Всички типове".equals(selectedType)) {
                    currentData.removeIf(o -> !selectedType.equals(o.getType()));
                }
            } else if (selectedType != null && !"Всички типове".equals(selectedType)) {
                currentData = dao.searchByType(selectedType);
            } else {
                loadAll();
                return;
            }
            refresh(currentData);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        nameField.setText("");
        typeCombo.setSelectedIndex(0);
        contactField.setText("");
        if (settlementCombo.getItemCount() > 0) settlementCombo.setSelectedIndex(0);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Внимание", JOptionPane.WARNING_MESSAGE);
    }
}
