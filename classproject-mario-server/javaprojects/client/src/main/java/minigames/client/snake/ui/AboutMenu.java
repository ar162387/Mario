package minigames.client.snake.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.InputStream;


import java.io.IOException;

import minigames.client.snake.Sound;
import minigames.client.snake.util.ButtonCreator;
import minigames.client.snake.util.ImageLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.MinigameNetworkClient;


/**
 * The AboutMenu class represents a UI panel that displays information about how to play
 * the game and provides a button to return to the main menu.
 *
 * The background image and button are loaded dynamically, and this menu
 * also plays the menu background music. When the "Back to Menu" button is clicked,
 * the listener triggers an action to go back to the main menu.
 *
 * This class uses the {@link ImageLoader} to load the background image and
 * {@link ButtonCreator} to create the button. The sound is managed by {@link Sound}.
 *
 *  @see ImageLoader
 *  @see ButtonCreator
 *  @see Sound
 */
public class AboutMenu {

    private JPanel aboutPanel;
    private Image backgroundImage;

    public AboutMenu(ActionListener backToMenuListener) {

        // Load the background image from the resources directory
        backgroundImage = ImageLoader.loadImage("snake/aboutBg2.png");

        // Create the about panel with a custom paintComponent to draw the background
        aboutPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight()-150, this);
                }
            }
        };

        // Add "Back to Menu" button
        JButton backButton = ButtonCreator.createButton("Back to Menu", "#4287f5", "#6495ed", backToMenuListener);
        backButton.setBounds(300, 600, 200, 50); // Position the button
        aboutPanel.add(backButton);


        // play the Menu music
        Sound.getInstance().loop(Sound.Type.MENU);

        // Revalidate and repaint to ensure visibility
        aboutPanel.revalidate();
        aboutPanel.repaint();
    }


    public JPanel getAboutPanel() {
        return aboutPanel;
    }
}


