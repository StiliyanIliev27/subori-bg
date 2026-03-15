package bg.sabori.ui;

import javax.swing.*;
import java.awt.*;

/**
 * STUB — за имплементация от Човек 2.
 *
 * Панелът трябва да предоставя CRUD операции за таблица events.
 * Използва: EventDAO, SettlementDAO, CategoryDAO, OrganizerDAO (за dropdown-и).
 */
public class EventsPanel extends JPanel {

    public EventsPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Панел Събития — очаква се имплементация", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        add(label, BorderLayout.CENTER);
    }
}
