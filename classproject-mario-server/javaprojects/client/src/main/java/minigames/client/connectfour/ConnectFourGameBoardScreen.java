package minigames.client.connectfour;

import io.vertx.core.json.JsonArray;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;

public class ConnectFourGameBoardScreen extends JPanel {
    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 800;
    public static final int BUTTON_WIDTH = 150;
    public static final int BUTTON_HEIGHT = 50;
    public static final int BUTTON_PANEL_WIDTH = 200;
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 27);
    public static final Color BUTTON_COLOR = new Color(0, 100, 200);
    public static final Color BUTTON_HOVER_COLOR = new Color(0, 150, 255);
    public static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 16);

    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int CELL_SIZE = 80;
    private static final int BORDER_THICKNESS = 10;
    private static final int BORDER_RADIUS = 20;

    private ConnectFourClient client;
    private String[][] board;
    private String activePlayer;

    private JButton playAgainButton;
    private JButton quitGameButton;
    private JButton backButton;
    private JLabel notificationLabel;
    private JPanel gameBoardPanel;

    public ConnectFourGameBoardScreen(ConnectFourClient client) {
        this.client = client;
        this.board = new String[ROWS][COLS];
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initializeComponents();
    }

    private void initializeComponents() {
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.WEST);

        gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGameBoard(g);
            }
        };
        gameBoardPanel.setOpaque(false);
        gameBoardPanel.setPreferredSize(new Dimension(
                COLS * CELL_SIZE + 4 * BORDER_THICKNESS,
                ROWS * CELL_SIZE + 4 * BORDER_THICKNESS
        ));
        gameBoardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = (e.getX() - 2 * BORDER_THICKNESS) / CELL_SIZE;
                if (column >= 0 && column < COLS) {
                    client.makeMove(column);
                }
            }
        });

        // Create a custom border
        Border roundedBorder = new RoundedBorder(BORDER_RADIUS, new Color(255, 40, 185), BORDER_THICKNESS);
        gameBoardPanel.setBorder(roundedBorder);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(gameBoardPanel);
        add(centerPanel, BorderLayout.CENTER);

        notificationLabel = new JLabel("Game notifications will appear here");
        notificationLabel.setForeground(Color.WHITE);
        notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
        add(notificationLabel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setPreferredSize(new Dimension(BUTTON_PANEL_WIDTH, SCREEN_HEIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 25, 50, 25));

        playAgainButton = createStyledButton("Play Again", e -> client.startGame());
        quitGameButton = createStyledButton("Quit Game", e -> client.quitGame());
        backButton = createStyledButton("Back", e -> client.goBack());

        buttonPanel.add(playAgainButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(quitGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(backButton);

        return buttonPanel;
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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    public void updateBoard(JsonArray grid) {
        for (int i = 0; i < ROWS; i++) {
            JsonArray row = grid.getJsonArray(i);
            for (int j = 0; j < COLS; j++) {
                board[i][j] = row.getString(j);
            }
        }
        repaint();
    }

    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
        repaint();
    }

    public String getActivePlayer() {
        return activePlayer;
    }

    private void drawGameBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startX = 2 * BORDER_THICKNESS;
        int startY = 2 * BORDER_THICKNESS;

        // Draw game board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = startX + col * CELL_SIZE;
                int y = startY + (ROWS - 1 - row) * CELL_SIZE;
                g2d.setColor(new Color(20, 20, 255));
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                if (board[row][col] != null && !board[row][col].isEmpty()) {
                    g2d.setColor(board[row][col].equals(client.getPlayerName()) ? Color.RED : Color.YELLOW);
                    g2d.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }
            }
        }

        if (activePlayer != null) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(BUTTON_FONT);
            g2d.drawString("Current turn: " + activePlayer, startX + 10, getHeight() - BORDER_THICKNESS - 10);
        }
    }

    public void setNotification(String message) {
        notificationLabel.setText(message);
    }

    // Custom RoundedBorder class
    private static class RoundedBorder implements Border {
        private int radius;
        private Color color;
        private int thickness;

        RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.thickness, this.thickness, this.thickness, this.thickness);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x + thickness / 2, y + thickness / 2,
                    width - thickness, height - thickness,
                    radius, radius);
            g2d.dispose();
        }
    }
}