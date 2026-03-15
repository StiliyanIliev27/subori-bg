package bg.sabori.ui;

import javax.swing.*;
import java.awt.*;

/**
 * STUB — за имплементация от Човек 2.
 *
 * Панелът трябва да визуализира статистики:
 *   - Брой събития по регион
 *   - Ежегодни vs еднократни събития
 *   - Топ организатори по брой организирани събития
 * Използва: EventDAO, RegionDAO, OrganizerDAO.
 */
public class StatisticsPanel extends JPanel {

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Панел Статистики — очаква се имплементация", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        add(label, BorderLayout.CENTER);
    }
}
