package minigames.client.snake.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ButtonCreator {

    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 20);
    private static final Color FOREGROUND_COLOUR = Color.WHITE; // text colour
    private static final Color BORDER_COLOUR = Color.DARK_GRAY;

    /**
     * Creates a customised button with the specified properties.
     * @param text A String  to display on the button
     * @param bgColour String The background colour of the button
     * @param hoverColour The colour of the button when hovered
     * @param actionListener The action listener to handle button clicks
     * @return the customised JButton
     */
    public static JButton createButton(
            String text, String bgColour, String hoverColour, ActionListener actionListener) {

        JButton button = new JButton(text);
        button.setFont(DEFAULT_FONT);
        button.setBackground(Color.decode(bgColour));
        button.setForeground(FOREGROUND_COLOUR);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOUR, 2)); // Dark gray border

        button.setOpaque(true); //this is required to see the button on a mac
        //button.setContentAreaFilled(true);
        button.setBorderPainted(true); // this is required to see the border on a mac

        button.setPreferredSize(new Dimension(150, 45));
        // Add mouse hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(hoverColour));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(bgColour));
            }
        });

        button.setFocusable(false);

        button.addActionListener(actionListener);

        return button;
    }
}