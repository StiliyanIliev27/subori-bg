package bg.sabori.ui;

import javax.swing.*;
import java.awt.*;

/**
 * STUB — за имплементация от Човек 2.
 *
 * Панелът трябва да предоставя многокритериална справка (минимум 2 критерия
 * от различни таблици). Използва: EventDAO и всички останали DAO-та.
 */
public class SearchPanel extends JPanel {

    public SearchPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Панел Справки — очаква се имплементация", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        add(label, BorderLayout.CENTER);
    }
}
