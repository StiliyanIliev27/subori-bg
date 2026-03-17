package bg.sabori.ui;

import bg.sabori.dao.CategoryDAO;
import bg.sabori.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CategoriesPanel extends JPanel {

    private final CategoryDAO dao = new CategoryDAO();

    private final DefaultTableModel           tableModel;
    private final JTable                      table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField                  searchField;
    private final JTextField                  nameField;
    private final JButton                     btnAdd;
    private final JButton                     btnUpdate;
    private final JButton                     btnDelete;
    private final JButton                     btnSearch;
    private final JButton                     btnShowAll;
    private final JLabel                      statusLabel;

    private List<Category> currentData;

    public CategoriesPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table ---
        tableModel = new DefaultTableModel(new String[]{"Наименование"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Search bar ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        searchField = new JTextField(20);
        btnSearch   = new JButton("Търси");
        btnShowAll  = new JButton("Покажи всички");
        searchPanel.add(new JLabel("Търсене по наименование:"));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);
        searchField.addActionListener(e -> onSearch());

        // --- Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Данни за категория"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Наименование:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        nameField = new JTextField(25);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnAdd    = new JButton("Добави");
        btnUpdate = new JButton("Редактирай");
        btnDelete = new JButton("Изтрий");
        JButton btnClear = new JButton("Изчисти");
        styleAddButton(btnAdd);
        styleDeleteButton(btnDelete);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel, gbc);

        statusLabel = new JLabel("Показани: 0 записа");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        statusLabel.setForeground(Color.GRAY);

        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.add(searchPanel, BorderLayout.NORTH);
        south.add(formPanel,   BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        // --- Listeners ---
        btnAdd.addActionListener(e    -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnSearch.addActionListener(e -> onSearch());
        btnShowAll.addActionListener(e -> loadAll());
        btnClear.addActionListener(e  -> clearForm());

        loadAll();
    }

    private void loadAll() {
        try {
            currentData = dao.getAll();
            refresh(currentData);
            searchField.setText("");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refresh(List<Category> data) {
        tableModel.setRowCount(0);
        for (Category c : data) {
            tableModel.addRow(new Object[]{c.getName()});
        }
        statusLabel.setText("Показани: " + data.size() + " записа");
        clearForm();
    }

    private void onRowSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        nameField.setText(currentData.get(row).getName());
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void onAdd() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { showWarn("Въведете наименование."); return; }
        try {
            dao.insert(name);
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onUpdate() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        String name = nameField.getText().trim();
        if (name.isEmpty()) { showWarn("Въведете наименование."); return; }
        try {
            dao.update(currentData.get(row).getId(), name);
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Изтриване на категория \"" + currentData.get(row).getName() + "\"?",
            "Потвърждение", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(currentData.get(row).getId());
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) { loadAll(); return; }
        try {
            currentData = dao.searchByName(keyword);
            refresh(currentData);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        nameField.setText("");
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        table.clearSelection();
    }

    private static void styleAddButton(JButton btn) {
        btn.setBackground(new Color(40, 167, 69));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private static void styleDeleteButton(JButton btn) {
        btn.setBackground(new Color(220, 53, 69));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Внимание", JOptionPane.WARNING_MESSAGE);
    }
}
