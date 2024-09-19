package minigames.client.connectfour;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ConnectFourGameLobbyScreen extends JPanel {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 800;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_PANEL_WIDTH = 200;

    private static final Color BACKGROUND_COLOR = new Color(0, 0, 27);
    private static final Color BUTTON_COLOR = new Color(0, 100, 200);
    private static final Color BUTTON_HOVER_COLOR = new Color(0, 150, 255);
    private static final Color TABLE_BACKGROUND_COLOR = new Color(0, 0, 27);
    private static final Color TABLE_GRID_COLOR = new Color(100, 100, 100);
    private static final Color TABLE_SELECTION_BACKGROUND_COLOR = new Color(0, 50, 100);
    private static final Color TABLE_SELECTION_FOREGROUND_COLOR = Color.WHITE;
    private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 24);
    private static final Font CONTENT_FONT = new Font("Monospaced", Font.PLAIN, 14);

    private JButton backButton;
    private JButton startGameButton;
    private JTable playerTable;
    private DefaultTableModel tableModel;

    public ConnectFourGameLobbyScreen(ActionListener actionListener) {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create the button panel on the left
        JPanel buttonPanel = createButtonPanel(actionListener);
        add(buttonPanel, BorderLayout.WEST);

        // Create the content panel
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
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        startGameButton = createStyledButton("Start Game", actionListener);
        buttonPanel.add(startGameButton);

        return buttonPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Game Lobby", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 0, 20, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Create a table for players in the lobby
        String[] columnNames = {"Player", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0);
        playerTable = new JTable(tableModel);
        styleTable(playerTable);

        // Set up the "Join Game" button in the table
        playerTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        playerTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(playerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4)); // 4px white border
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Wrap the scroll pane in a panel to add padding
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20)); // Add padding
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tablePanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private void styleTable(JTable table) {
        table.setFont(CONTENT_FONT);
        table.setForeground(Color.WHITE);
        table.setBackground(TABLE_BACKGROUND_COLOR);
        table.setRowHeight(40);
        table.setGridColor(TABLE_GRID_COLOR);
        table.setSelectionBackground(TABLE_SELECTION_BACKGROUND_COLOR);
        table.setSelectionForeground(TABLE_SELECTION_FOREGROUND_COLOR);
        table.setTableHeader(null); // Remove table header
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
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

    public void updatePlayerList(List<String> players) {
        tableModel.setRowCount(0);
        for (String player : players) {
            tableModel.addRow(new Object[]{player, "Is awaiting a new challenger", "Join Game"});
        }
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getStartGameButton() {
        return startGameButton;
    }

    // Custom button renderer for the table
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setForeground(Color.WHITE);
            setBackground(BUTTON_COLOR);
            return this;
        }
    }

    // Custom button editor for the table
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setForeground(Color.WHITE);
            button.setBackground(BUTTON_COLOR);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                System.out.println("Join Game clicked for player: " + playerTable.getValueAt(playerTable.getSelectedRow(), 0));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}