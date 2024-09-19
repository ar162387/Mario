package minigames.client.gwent.ui;

import minigames.gwent.Player;

import javax.swing.*;

import java.awt.*;

public class PlayerPanel {
    private final JPanel playerPanel;
    private final JLabel pictureLabel;
    private final JLabel nameLabel;
    private final JLabel factionLabel;
    private final JLabel livesLabel;
    private final JLabel powerLabel;

    Player player;

    public PlayerPanel(Player player) {
        this.player = player;

        this.pictureLabel = new JLabel("picture"); // TODO: Implement getPicture to Player class and return an ImageIcon
        this.nameLabel = new JLabel(player.getName());
        this.factionLabel = new JLabel("temp faction");
        this.livesLabel = new JLabel("Lives: " + player.getHealth()); // TODO: Replace with an image for each life
        this.powerLabel = createPowerLabel(player.calculatePlayerStrength());

        this.playerPanel = new JPanel(new GridBagLayout());

        initialiseProfilePanel();
    }

    private JLabel createPowerLabel(int power) {
        // TODO: Make this look better
        JLabel label = new JLabel(String.valueOf(power), SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(30, 30)); // Circular label size
        label.setOpaque(true);
        label.setBackground(Color.BLACK); // Background color for the power indicator
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void initialiseProfilePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);

        // Picture on the left
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        playerPanel.add(pictureLabel, gbc);

        // Name and faction in the center
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        factionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textPanel.add(nameLabel);
        textPanel.add(factionLabel);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        playerPanel.add(textPanel, gbc);

        // Lives on the right
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        playerPanel.add(livesLabel, gbc);

        // Power label on the far right
        gbc.gridx = 3;
        gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        playerPanel.add(powerLabel, gbc);

        playerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void update() {
        livesLabel.setText("Lives: " + player.getHealth());
        powerLabel.setText(String.valueOf(player.calculatePlayerStrength()));

        playerPanel.revalidate();
        playerPanel.repaint();
    }

    public JPanel getPanel() {
        return playerPanel;
    }
}
