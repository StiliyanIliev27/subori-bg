package bg.sabori.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Събори и Местни Събития в България");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));

        tabs.addTab("Региони",        new RegionsPanel());
        tabs.addTab("Населени места", new SettlementsPanel());
        tabs.addTab("Категории",      new CategoriesPanel());
        tabs.addTab("Организатори",   new OrganizersPanel());
        tabs.addTab("Събития",        new EventsPanel());
        tabs.addTab("Справки",        new SearchPanel());
        tabs.addTab("Статистики",     new StatisticsPanel());

        add(tabs);
    }
}
