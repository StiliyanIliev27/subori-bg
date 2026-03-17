package bg.sabori.ui;

import bg.sabori.dao.CategoryDAO;
import bg.sabori.dao.EventDAO;
import bg.sabori.dao.OrganizerDAO;
import bg.sabori.dao.SettlementDAO;
import bg.sabori.model.Category;
import bg.sabori.model.Event;
import bg.sabori.model.Organizer;
import bg.sabori.model.Settlement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class EventsPanel extends JPanel {

    private final EventDAO      dao          = new EventDAO();
    private final SettlementDAO settlementDao = new SettlementDAO();
    private final CategoryDAO   categoryDao  = new CategoryDAO();
    private final OrganizerDAO  organizerDao = new OrganizerDAO();

    private final DefaultTableModel           tableModel;
    private final JTable                      table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField                  searchDateField;
    private final JComboBox<String>           searchMonthCombo;
    private final JTextField                  nameField;
    private final JSpinner                    eventDateSpinner;
    private final JCheckBox                   recurringCheck;
    private final JTextArea                   descriptionArea;
    private final JComboBox<Settlement>       settlementCombo;
    private final JComboBox<Category>         categoryCombo;
    private final JComboBox<Organizer>        organizerCombo;
    private final JButton                     btnAdd;
    private final JButton                     btnUpdate;
    private final JButton                     btnDelete;
    private final JButton                     btnSearch;
    private final JButton                     btnShowAll;
    private final JLabel                      statusLabel;

    private List<Event>      currentData;
    private List<Settlement> settlements;
    private List<Category>   categories;
    private List<Organizer>  organizers;

    public EventsPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
            new String[]{"Име", "Дата", "Ежегодно", "Категория", "Населено място", "Организатор"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(75);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(170);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        searchDateField = new JTextField(10);
        searchMonthCombo = new JComboBox<>(new String[]{
            "Всички месеци", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
        });
        btnSearch = new JButton("Търси");
        btnShowAll = new JButton("Покажи всички");
        searchPanel.add(new JLabel("Дата (yyyy-MM-dd):"));
        searchPanel.add(searchDateField);
        searchPanel.add(new JLabel("или месец:"));
        searchPanel.add(searchMonthCombo);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);
        searchDateField.addActionListener(e -> onSearch());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Данни за събитие"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Иeме:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        nameField = new JTextField(24);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Дата:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        eventDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(eventDateSpinner, "yyyy-MM-dd");
        eventDateSpinner.setEditor(dateEditor);
        formPanel.add(eventDateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Ежегодно:"), gbc);
        gbc.gridx = 1;
        recurringCheck = new JCheckBox("Да");
        recurringCheck.setSelected(true);
        formPanel.add(recurringCheck, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Описание:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        descriptionArea = new JTextArea(4, 24);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weighty = 0;
        formPanel.add(new JLabel("Населено място:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        settlementCombo = new JComboBox<>();
        formPanel.add(settlementCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Категория:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        categoryCombo = new JComboBox<>();
        formPanel.add(categoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Организатор:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        organizerCombo = new JComboBox<>();
        formPanel.add(organizerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnAdd = new JButton("Добави");
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
        south.add(formPanel, BorderLayout.CENTER);
        south.add(statusLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnSearch.addActionListener(e -> onSearch());
        btnShowAll.addActionListener(e -> loadAll());
        btnClear.addActionListener(e  -> clearForm());

        loadDropdownData();
        loadAll();
    }

    private void loadDropdownData() {
        try {
            settlements = settlementDao.getAll();
            categories = categoryDao.getAll();
            organizers = organizerDao.getAll();

            settlementCombo.removeAllItems();
            for (Settlement s : settlements) settlementCombo.addItem(s);

            categoryCombo.removeAllItems();
            for (Category c : categories) categoryCombo.addItem(c);

            organizerCombo.removeAllItems();
            for (Organizer o : organizers) organizerCombo.addItem(o);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadAll() {
        try {
            currentData = dao.getAll();
            refresh(currentData);
            searchDateField.setText("");
            searchMonthCombo.setSelectedIndex(0);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refresh(List<Event> data) {
        tableModel.setRowCount(0);
        for (Event e : data) {
            tableModel.addRow(new Object[]{
                e.getName(),
                e.getEventDate(),
                e.isRecurring() ? "Да" : "Не",
                e.getCategoryName(),
                e.getSettlementName(),
                e.getOrganizerName()
            });
        }
        statusLabel.setText("Показани: " + data.size() + " записа");
        clearForm();
    }

    private void onRowSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);

        Event event = currentData.get(row);
        nameField.setText(event.getName());
        eventDateSpinner.setValue(Date.from(
            event.getEventDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        recurringCheck.setSelected(event.isRecurring());
        descriptionArea.setText(event.getDescription() == null ? "" : event.getDescription());

        selectById(settlementCombo, settlements, event.getSettlementId());
        selectById(categoryCombo, categories, event.getCategoryId());
        selectById(organizerCombo, organizers, event.getOrganizerId());

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void onAdd() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { showWarn("Въведете име."); return; }

        LocalDate date = spinnerDate();
        Settlement settlement = (Settlement) settlementCombo.getSelectedItem();
        Category category = (Category) categoryCombo.getSelectedItem();
        Organizer organizer = (Organizer) organizerCombo.getSelectedItem();
        if (settlement == null || category == null || organizer == null) {
            showWarn("Изберете населено място, категория и организатор.");
            return;
        }

        try {
            dao.insert(name, date, recurringCheck.isSelected(), descriptionArea.getText(),
                settlement.getId(), category.getId(), organizer.getId());
            loadDropdownData();
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
        if (name.isEmpty()) { showWarn("Въведете име."); return; }

        LocalDate date = spinnerDate();
        Settlement settlement = (Settlement) settlementCombo.getSelectedItem();
        Category category = (Category) categoryCombo.getSelectedItem();
        Organizer organizer = (Organizer) organizerCombo.getSelectedItem();
        if (settlement == null || category == null || organizer == null) {
            showWarn("Изберете населено място, категория и организатор.");
            return;
        }

        try {
            dao.update(currentData.get(row).getId(), name, date, recurringCheck.isSelected(),
                descriptionArea.getText(), settlement.getId(), category.getId(), organizer.getId());
            loadDropdownData();
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);

        Event event = currentData.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Изтриване на събитие \"" + event.getName() + "\"?",
            "Потвърждение", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.delete(event.getId());
            loadAll();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onSearch() {
        String dateText = searchDateField.getText().trim();
        int monthIndex = searchMonthCombo.getSelectedIndex();

        try {
            if (!dateText.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(dateText);
                    currentData = dao.searchByDate(date);
                } catch (Exception ex) {
                    showWarn("Невалидна дата. Използвайте формат yyyy-MM-dd.");
                    return;
                }
            } else if (monthIndex > 0) {
                currentData = dao.searchByMonth(monthIndex);
            } else {
                loadAll();
                return;
            }
            refresh(currentData);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private LocalDate spinnerDate() {
        Date d = (Date) eventDateSpinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void selectById(JComboBox<?> combo, List<?> source, int id) {
        for (int i = 0; i < source.size(); i++) {
            Object item = source.get(i);
            if (item instanceof Settlement s && s.getId() == id) { combo.setSelectedIndex(i); return; }
            if (item instanceof Category c  && c.getId() == id)  { combo.setSelectedIndex(i); return; }
            if (item instanceof Organizer o && o.getId() == id)  { combo.setSelectedIndex(i); return; }
        }
    }

    private void clearForm() {
        nameField.setText("");
        eventDateSpinner.setValue(new Date());
        recurringCheck.setSelected(true);
        descriptionArea.setText("");
        if (settlementCombo.getItemCount() > 0) settlementCombo.setSelectedIndex(0);
        if (categoryCombo.getItemCount() > 0) categoryCombo.setSelectedIndex(0);
        if (organizerCombo.getItemCount() > 0) organizerCombo.setSelectedIndex(0);
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
