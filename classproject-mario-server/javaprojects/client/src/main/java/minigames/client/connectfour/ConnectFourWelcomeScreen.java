package minigames.client.connectfour;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;

public class ConnectFourWelcomeScreen extends JPanel {
    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 800;
    public static final int BUTTON_WIDTH = 150;
    public static final int BUTTON_HEIGHT = 50;
    public static final int BUTTON_PANEL_WIDTH = 200;
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 27);
    public static final Color BUTTON_COLOR = new Color(0, 100, 200);
    public static final Color BUTTON_HOVER_COLOR = new Color(0, 150, 255);
    public static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 16);

    private JButton startButton;
    private JButton rulesButton;
    private JButton highScoresButton;
    private JButton backToMenuButton;

    private Image backgroundImage;

    public ConnectFourWelcomeScreen(ActionListener listener) {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        loadBackgroundImage();

        JPanel buttonPanel = createButtonPanel(listener);
        add(buttonPanel, BorderLayout.WEST);
        add(new ImagePanel(), BorderLayout.CENTER);
    }

    private JPanel createButtonPanel(ActionListener listener) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setPreferredSize(new Dimension(BUTTON_PANEL_WIDTH, SCREEN_HEIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 25, 50, 25));

        startButton = createStyledButton("Play", listener);
        rulesButton = createStyledButton("Game Rules", listener);
        highScoresButton = createStyledButton("High Scores", listener);
        backToMenuButton = createStyledButton("Back to Menu", listener);

        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(rulesButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(highScoresButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(backToMenuButton);

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

    private void loadBackgroundImage() {
        String imagePath = "/ConnectFour/ConnectFourImages/TitleImage.png";
        try {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                backgroundImage = ImageIO.read(imageStream);
                System.out.println("Image loaded successfully from resources");
            } else {
                File file = new File("src/main/resources" + imagePath);
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                    System.out.println("Image loaded successfully from file system");
                } else {
                    System.err.println("Image file not found: " + imagePath);
                    System.err.println("Attempted file path: " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class ImagePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.drawString("Image not found", 10, 20);
            }
        }
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getRulesButton() {
        return rulesButton;
    }

    public JButton getHighScoresButton() {
        return highScoresButton;
    }

    public JButton getBackToMenuButton() {
        return backToMenuButton;
    }
}