package minigames.client.tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.IOException;
import javax.sound.sampled.*;

public class TicTacToeUI extends JFrame {
    public static final int SIZE = 3; // Make SIZE public
    private JButton[][] buttons = new JButton[SIZE][SIZE];
    private TicTacToe gameClient;
    private String currentPlayer = "X"; // Track the current player
    private Clip clip; // For background music
    private BufferedImage backgroundImage; // To store the background image

    public TicTacToeUI(TicTacToe gameClient) {
        this.gameClient = gameClient;
        setSize(800, 800); // Set window size to 800x800
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            showWelcomeScreen(); // Initialize the UI
        } catch (IOException e) {
            e.printStackTrace();
        }

        playBackgroundMusic("/tictactoe/sounds/bgmusic.wav"); // Ensure this path is correct
    }

    // Method to create and show the welcome screen
    private void showWelcomeScreen() throws IOException {
        // Load background image
        backgroundImage = ImageIO.read(getClass().getResourceAsStream("/tictactoe/images/tictactoebg.jpg"));

        // Set background image
        JPanel welcomePanel = new BackgroundPanel(backgroundImage);
        welcomePanel.setLayout(new BorderLayout());
        setContentPane(welcomePanel);

        // Add title label
        JLabel titleLabel = new JLabel("Tic Tac Toe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        welcomePanel.add(titleLabel, BorderLayout.CENTER);

        // Add buttons panel
        JPanel buttonPanel = createButtonPanel();
        welcomePanel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper method to create the button panel
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false); // Make panel transparent

        JButton playButton = new JButton("Play");
        JButton exitButton = new JButton("Exit");

        playButton.addActionListener(e -> {
            getContentPane().removeAll(); // Clear the welcome screen components
            initializeGameUI(); // Start game in the same window
            revalidate(); // Refresh the window
            repaint();
        });

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(playButton);
        buttonPanel.add(exitButton);

        return buttonPanel;
    }

    // Method to initialize the game UI
   // Method to initialize the game UI
private void initializeGameUI() {
    // Load background image
    try {
        backgroundImage = ImageIO.read(getClass().getResourceAsStream("/tictactoe/images/tictactoebg.jpg"));
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Set layout and background image
    JPanel gamePanel = new BackgroundPanel(backgroundImage);
    gamePanel.setLayout(null); // Use null layout for absolute positioning
    setContentPane(gamePanel);

    // Create and add grid panel
    JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
    gridPanel.setBounds(250, 250, 300, 300); // Center the grid within 800x800 panel
    gridPanel.setOpaque(false); // Make panel transparent
    gamePanel.add(gridPanel);

    // Customize the game grid
    for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
            buttons[row][col] = new JButton();
            buttons[row][col].setFont(new Font("Arial", Font.PLAIN, 40));
            buttons[row][col].setFocusPainted(false);
            buttons[row][col].addActionListener(new ButtonClickListener(row, col));
            gridPanel.add(buttons[row][col]);
        }
    }

    setVisible(true);
}


    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isCellEmpty(row, col)) {
                setCellText(row, col, currentPlayer);
                System.out.println("Button clicked at: " + row + "," + col + " by " + currentPlayer); // Debugging
            }
        }
    }

    // Method to check if a cell is empty
    public boolean isCellEmpty(int row, int col) {
        return buttons[row][col].getText().isEmpty();
    }

    // Method to set text in a cell
    public void setCellText(int row, int col, String text) {
        buttons[row][col].setText(text);
    }


    // BackgroundPanel class to handle background images
    private class BackgroundPanel extends JPanel {
        private BufferedImage image;

        // Constructor to accept BufferedImage
        public BackgroundPanel(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Method to play background music
    private void playBackgroundMusic(String musicPath) {
        try {
            // Load the audio file as an InputStream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(musicPath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
