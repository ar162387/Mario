package minigames.client.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * This class is used to handle the leaderboard associated with debugging the Minesweeper game.
 * This class is used to determine and set the users rank on the leader board
 * Author: Matt Hayes
 */

public class Leaderboard {
    // Initialize variables
    private user[] position;
    private int userCount;
    private JLabel[] labels;
    private JLabel hiScoreLabel;
    private GameController gameController;
    
    //background image variables
    private BufferedImage background;
    private static String dir = "/images/minesweeper/";
    private static String background_img = dir + "minesweeperMushroomCloud.png";
    
    //Constructor
    public Leaderboard(user[] position,int userCount, GameController gameController) {
        this.position = position;
        this.userCount = userCount;
        labels = new JLabel[userCount];
        this.gameController = gameController;
        
        //Loads in the background image
        try {
            background = ImageIO.read(getClass().getResource(background_img));
            if (background == null) {
					System.out.println("Image not found");            
            }
        } catch (IOException e) {
            System.out.println("Error loading background image. \nError message: " + e.getMessage());
        }
    }
    
    // Getters
    public user[] getPosition() {
        return position;
    }

    // Setters  
    public void setRank(int rank, user user) {
        if (rank >= 0 && rank < userCount) {
            this.position[rank] = user;
        }
    }
    
    //Inserts the user into the leaderboard based on score
    public void getRank(user user) {
    	for (int i = 0; i < userCount; i++) {
    			if ( user.getScore() > position[i].getScore() ) {
    				//Move the following ranks up one
    				for (int r = userCount - 1; r > i; r--) {
						position[r] = position[r-1];    				
    				}
    				//insert user
    				position[i] = user;   
					//exit
    				break;
    			}
       }  
    }


    // Create the information panel that lists the user and their position on the leaderboard
    public JPanel showRanking() {
        // Initialize the JPanel and JLabels
        JPanel ranking = new JPanel(new GridLayout(userCount, 1, 0, 20));
        for (int i = 0; i < userCount; i++) {
            labels[i] = new JLabel((i + 1) + ". " + position[i].getUserName() + " - " + position[i].getScore());
            labels[i].setForeground(Color.WHITE); // Set font color 
            labels[i].setBackground(new Color(0, 0, 0, 180)); // Set font back ground color 
            labels[i].setOpaque(true);
            Font currentFont = labels[i].getFont(); // Get the current font of the label
            Font newFont = currentFont.deriveFont(20f); // Set the font size to 20
            labels[i].setFont(newFont); // Apply the new font to the label
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(labels[i], BorderLayout.CENTER);
            panel.setLayout(new FlowLayout(FlowLayout.CENTER));
            panel.setOpaque(false);
            ranking.add(panel);
        }
        return ranking;
    }
    

    // Create a panel that has the leaderboard and a main menu button
    public JPanel createLeaderboard() {
    	  JPanel leaderboardPanel = new JPanel(new BorderLayout());
    	  
        // Initialize the leaderboard panel
        JLayeredPane backgroundPanel = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int imageWidth = background.getWidth();
                    int imageHeight = background.getHeight();
    
                    // Calculate the scaling factor to maintain the aspect ratio
                    double aspectRatio = (double) imageWidth / imageHeight;
                    int newWidth = panelWidth;
                    int newHeight = (int) (panelWidth / aspectRatio);
    
                    if (newHeight > panelHeight) {
                        newHeight = panelHeight;
                        newWidth = (int) (panelHeight * aspectRatio);
                    }
    
                    // Calculate the top-left corner for centering the image
                    int x = (panelWidth - newWidth) / 2;
                    int y = (panelHeight - newHeight) / 2;
    
                    // Draw the image with the calculated dimensions
                    g.drawImage(background, x, y, newWidth, newHeight, this);
                }
            }
        };
        backgroundPanel.setPreferredSize(new Dimension(800, 600));
		  backgroundPanel.setLayout(new BorderLayout());

			//Creates a label to display the title
        JLabel label = new JLabel("Leaderboard", SwingConstants.CENTER);
        label.setForeground(Color.WHITE); // Set font color to white
        Font currentFont = label.getFont(); // Get the current font of the label
        Font newFont = currentFont.deriveFont(45f); // Set the font size to 45
        label.setFont(newFont); // Apply the new font to the label
        backgroundPanel.add(label, BorderLayout.NORTH);

        // Get the rank panel
        JPanel rankPanel = showRanking();
        rankPanel.setOpaque(false);
        backgroundPanel.add(rankPanel, BorderLayout.CENTER);
        
        //Listener for the main menu button to return the user after viewing highscores
        JButton mainMenu = new JButton("Menu");
        mainMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.navMainMenu();
            }
        });
        JPanel buttonPanel = new JPanel();
		  buttonPanel.setOpaque(false);
		  buttonPanel.add(mainMenu, BorderLayout.CENTER);
		      
        //construct the panel 
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
        
		  leaderboardPanel.add(backgroundPanel, BorderLayout.CENTER);

        return leaderboardPanel;
    }
}
    

