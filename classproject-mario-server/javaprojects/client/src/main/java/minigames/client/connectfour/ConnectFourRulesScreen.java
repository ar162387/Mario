package minigames.client.connectfour;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConnectFourRulesScreen extends JPanel {
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

    public ConnectFourRulesScreen(ActionListener actionListener) {
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

        // Title
        JLabel titleLabel = new JLabel("Connect Four Rules", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 0, 20, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Rules text
        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setWrapStyleWord(true);
        rulesText.setLineWrap(true);
        rulesText.setFont(CONTENT_FONT);
        rulesText.setForeground(Color.WHITE);
        rulesText.setBackground(BACKGROUND_COLOR);
        rulesText.setBorder(new EmptyBorder(20, 40, 20, 40));
        rulesText.setText(
                "1. The game is played on a vertical board with 6 rows and 7 columns.\n\n" +
                        "2. Two players take turns dropping their colored discs into the columns.\n\n" +
                        "3. The discs fall to the lowest available space in the chosen column.\n\n" +
                        "4. The objective is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs.\n\n" +
                        "5. The game ends when a player connects four discs or when the board is full (a draw).\n\n" +
                        "6. Players must block their opponent while trying to create their own winning line."
        );

        JScrollPane scrollPane = new JScrollPane(rulesText);
        scrollPane.setBorder(null);
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