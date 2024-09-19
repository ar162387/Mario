package minigames.client.deepfried.UI;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GameOVerUI extends JPanel{

    private JButton newButton;
    private JButton returnButton;

    //layout and panel
    private CardLayout cardLayout;
    private JPanel gameOverPanel;

    public GameOVerUI() {

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Initialize panel
        gameOverPanel = initialiseGameOverPanel();
        
        // Add panel this panel managed by CardLayout
        add(gameOverPanel, "gameOver");
        
        showGameOverPanel();
    }

    private JLabel loadImage(String path){
        JLabel gameOverImage = null;
        try {
            BufferedImage chefImage = ImageIO.read(MainMenuUI.class.getResource(path));
            ImageIcon imageIcon = new ImageIcon(chefImage);

            gameOverImage = new JLabel(imageIcon);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameOverImage;
    }

     private JButton createJButton(String label, String fontName, int fontSize, String bgColour, String textColour) {
        newButton = new JButton(label);
        newButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
        newButton.setBackground(Color.decode(bgColour));
        newButton.setForeground(Color.decode(textColour));

        return newButton;
    }

    private JPanel initialiseGameOverPanel(){
       // JPanel backgroundPanel = new JPanel();
        //backgroundPanel.setBounds(0, 0, 1140, 680);
        //JLabel backGroundImage = loadImage("/deepFried/gameover-image.png");
        //backgroundPanel.setLayout();
        //backGroundImage.setBounds(0,0,1140,680);
        //backgroundPanel.add(backGroundImage);

        JPanel gameOverImage = new JPanel(new GridBagLayout());
        gameOverImage.setBounds(0, 0, 1400, 900);
        gameOverImage.setBackground(Color.decode("#97faff"));
        JLabel gameOverBackground = loadImage("/deepFried/gameover-image.png");
        gameOverImage.add(gameOverBackground);

        // Add the components to the overall panel using the grid bag layout manager and return it
        JPanel gameOverScreen = new JPanel(new GridBagLayout());
        gameOverScreen.setMaximumSize(new Dimension(1400, 900));
        gameOverScreen.setBackground(Color.decode("#97FAFF"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
            
        // Get screen size to calculate padding
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        
        // Calculate padding and apply it
        int paddingHeight = (int) (screenHeight * 0.02);
        int paddingWidth = (int) (screenWidth * 0.02);
        constraints.insets = new Insets(paddingHeight, paddingWidth, paddingHeight, paddingWidth);
        
        // Game over image
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        gameOverScreen.add(gameOverImage, constraints);


        constraints.ipadx = 125;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        constraints.insets = new Insets(10, 50, 10, 25);
        returnButton = createJButton("Return to Main Menu", "Helvetica", 20, "#f09c4a", "#FFFFFF");
        gameOverScreen.add(returnButton, constraints);
        //returnButton.setBounds(100, 550, 300, 30);
        
        //backgroundPanel.add(returnButton);
        //backgroundPanel.setComponentZOrder(returnButton, 0);

        constraints.ipadx = 125;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.insets = new Insets(10, 0, 10, 0);
        JLabel score = new JLabel("100");
        score.setOpaque(true);
        score.setBackground(Color.WHITE);
        score.setForeground(Color.decode("#f09c4a"));
        score.setBounds(250,240,100,100);
        score.setFont(new Font("Helvetica", Font.BOLD, 44));
        gameOverScreen.add(score, constraints);
        //backgroundPanel.add(score);
        //backgroundPanel.setComponentZOrder(score, 0);


        return gameOverScreen;
    }

    public JButton getReturnButton() {
        return returnButton;
    }
    public void showGameOverPanel() {
        cardLayout.show(this, "gameOver");
    }

}