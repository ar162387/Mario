package minigames.client.connectfour;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConnectFourHighScoresScreen extends JPanel {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 800;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_PANEL_WIDTH = 200;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 27);
    private static final Color BUTTON_COLOR = new Color(0, 100, 200);
    private static final Color BUTTON_HOVER_COLOR = new Color(0, 150, 255);
    private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 24);
    private static final Font CONTENT_FONT = new Font("Monospaced", Font.PLAIN, 14);

    private JButton backButton;

    public ConnectFourHighScoresScreen(ActionListener actionListener) {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Button Panel
        JPanel buttonPanel = createButtonPanel(actionListener);
        add(buttonPanel, BorderLayout.WEST);

        // Content Panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createButtonPanel(ActionListener actionListener) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setPreferredSize(new Dimension(BUTTON_PANEL_WIDTH, SCREEN_HEIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 25, 50, 25));

        backButton = createStyledButton("Back", actionListener);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("High Scores", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 0, 20, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // High Scores Table
        String[] columnNames = {"Rank", "Player", "Score"};
        Object[][] data = {
                {"1", "Player1", "1000"},
                {"2", "Player2", "900"},
                {"3", "Player3", "800"},
                {"4", "Player4", "700"},
                {"5", "Player5", "600"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(CONTENT_FONT);
        table.setForeground(Color.WHITE);
        table.setBackground(BACKGROUND_COLOR);
        table.setRowHeight(30);
        table.setGridColor(new Color(100, 100, 100));
        table.setSelectionBackground(new Color(0, 50, 100));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(BUTTON_FONT);
        header.setForeground(Color.WHITE);
        header.setBackground(BUTTON_COLOR);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        scrollPane.setBorder(new EmptyBorder(4, 4, 4, 4));
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        return contentPanel;
    }


    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(listener);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    public JButton getBackButton() {
        return backButton;
    }
}