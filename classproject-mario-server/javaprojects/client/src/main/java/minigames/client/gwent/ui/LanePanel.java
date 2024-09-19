package minigames.client.gwent.ui;

import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.Player;
import minigames.client.gwent.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LanePanel extends JPanel {
    private final String title;
    private final CardType zone;
    private final Player player;
    private final GameController gameController;

    private final JPanel lanePanel;
    private JPanel laneContentPanel;
    private JLabel powerLabel;

    public LanePanel(String title, CardType zone, Player player, GameController gameController) {
        this.title = title;
        this.zone = zone;
        this.player = player;
        this.gameController = gameController;

        this.lanePanel = new JPanel(new BorderLayout());
        initialiseLanePanel();
    }

    public void initialiseLanePanel() {
        lanePanel.setPreferredSize(new Dimension(800, 150)); // Panel size

        // Power Label
        powerLabel = createPowerLabel();
        lanePanel.add(powerLabel, BorderLayout.WEST);

        // Lane content panel holds the cards
        laneContentPanel = new JPanel();
        laneContentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        laneContentPanel.setBorder(BorderFactory.createTitledBorder(title));

        lanePanel.add(laneContentPanel);
    }

    private JLabel createPowerLabel() {
        // TODO: Make this label look better
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(30, 30));
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);

        return label;
    }

    private void populateLane() {
        laneContentPanel.removeAll();

        List<Card> cardsInZone = player.getCardsInZone(zone);
        int offset = 0;

        for (int i = 0; i < cardsInZone.size(); i++) {
            Card card = cardsInZone.get(i);
            CardPanel cardPanel = new CardPanel(card, i, offset, true, gameController);
            offset += cardPanel.getWidth() + 10; // Adjust offset for the next card

            laneContentPanel.add(cardPanel.getPanel());
        }

        powerLabel.setText(String.valueOf(player.calculateZoneStrength(zone)));
    }

    public void update() {
        populateLane();

        lanePanel.revalidate();
        lanePanel.repaint();
    }

    public JPanel getPanel() {
        return lanePanel;
    }
}
