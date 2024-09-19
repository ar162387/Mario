package minigames.client.RodentsRevenge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MouseSelectionPanel extends JPanel {

    private final RodentsRevenge game;

    public MouseSelectionPanel(RodentsRevenge game) {
        this.game = game;
        this.setLayout(new BorderLayout());

        //Label for header label
        JLabel headerLabel = new JLabel("SELECT YOUR MOUSE COLOR", JLabel.CENTER);
        headerLabel.setFont(new Font("Monospaced", Font.BOLD, 24)); // Retro monospace font
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Add padding around the label
        headerLabel.setForeground(Color.GREEN); // Retro green color
        this.add(headerLabel, BorderLayout.NORTH);

        //Panel for mouse avatars with GridBagLayout for centering and spacing
        JPanel avatarPanel = new JPanel(new GridBagLayout());
        avatarPanel.setBackground(Color.BLACK); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.gridx = GridBagConstraints.RELATIVE; // Auto-increment column position
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.CENTER; // Center the images

        // Avater Panel
        avatarPanel.add(createMouseAvatar("gray", "/RodentsRevenge/Map/mouse.png"), gbc);  // Default gray mouse
        avatarPanel.add(createMouseAvatar("blue", "/RodentsRevenge/MouseColors/mouse_blue2.png"), gbc);
        avatarPanel.add(createMouseAvatar("red", "/RodentsRevenge/MouseColors/mouse_red2.png"), gbc);
        avatarPanel.add(createMouseAvatar("green", "/RodentsRevenge/MouseColors/mouse_green.png"), gbc);

        this.add(avatarPanel, BorderLayout.CENTER);

        // Sets background of the selection panel to black for a retro look
        this.setBackground(Color.BLACK);
    }

    private JLabel createMouseAvatar(String colorName, String imagePath) {
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Resize image
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        // Store the original image and create a brighter version for the hover effect
        Image originalImage = scaledImage;
        ImageIcon hoverIcon = new ImageIcon(createBrightenedImage(scaledImage));

        //mouse hover and click effects
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                imageLabel.setIcon(hoverIcon);   // Show brightened image on hover
                Sound.getInstance().playSFX(Sound.Type.SELECTION);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                imageLabel.setIcon(new ImageIcon(originalImage)); // Revert to the original image
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                game.setMouseColor(colorName.toLowerCase()); 
            }
        });

        return imageLabel;
    }

    private Image createBrightenedImage(Image image) {
        BufferedImage brightenedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = brightenedImage.createGraphics();
        
        // original image
        g2.drawImage(image, 0, 0, null);
        
        //brightening effect
        RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null); // 1.2f for brightness, 15 for contrast
        rescaleOp.filter(brightenedImage, brightenedImage);
        
        g2.dispose();
        
        return brightenedImage;
    }
}
