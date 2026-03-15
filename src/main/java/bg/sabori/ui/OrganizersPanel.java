package bg.sabori.ui;

import javax.swing.*;
import java.awt.*;

/**
 * STUB — за имплементация от Човек 2.
 *
 * Панелът трябва да предоставя CRUD операции за таблица organizers.
 * Използва: OrganizerDAO, SettlementDAO (за dropdown на населено място).
 */
public class OrganizersPanel extends JPanel {

    public OrganizersPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Панел Организатори — очаква се имплементация", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        add(label, BorderLayout.CENTER);
    }
}
