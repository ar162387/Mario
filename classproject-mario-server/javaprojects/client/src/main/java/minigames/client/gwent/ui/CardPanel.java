package minigames.client.gwent.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.gwent.GameController;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.PowerCard;
import minigames.gwent.cards.WeatherCard;

public class CardPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(CardPanel.class);

    private static final Dimension PANEL_SIZE = new Dimension(120, 180);
    private static final Dimension POWER_LABEL_SIZE = new Dimension(20, 20);
    private static final Dimension NAME_LABEL_SIZE = new Dimension(90, 20);
    private static final Dimension TYPE_LABEL_SIZE = new Dimension(20, 20);
    private JPanel cardPanel;
    private Card card;
    private int index;
    private int offset;
    private boolean active;
    private GameController gameController;

    public CardPanel(Card card, int index, int offset, boolean active, GameController gameController) {
        this.cardPanel = new JPanel();
        this.card = card;
        this.index = index;
        this.offset = offset;
        this.active = active;
        this.gameController = gameController;

        initialiseCardPanel();
    }

    private void initialiseCardPanel() {
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(new LineBorder(Color.BLACK, 1, true));
        cardPanel.setPreferredSize(PANEL_SIZE);
        cardPanel.setBounds(offset, 20, 120,  180);
        cardPanel.putClientProperty("originalY", 20);
        cardPanel.putClientProperty("cardIndex", index);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.BOTH;

        addPowerLabel(card, gbc);
        addNameLabel(card, gbc);
        addDescriptionLabel(card, gbc);
        addCardTypeLabel(card, gbc);
        addCardToolTip(cardPanel, card);

        cardPanel.setEnabled(active);

        // Card Played
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (active) {
                    gameController.playCard(0, card);
                }
            }
        });

        setToolTipText(card.getName());
    }

    private void addPowerLabel(Card card, GridBagConstraints gbc) {
        JLabel powerLabel = new JLabel("0", JLabel.CENTER);
        if (card instanceof WeatherCard) {
            powerLabel.setText("\uD83C\uDF2B"); // Unicode for cloud emoji
        } else if (card instanceof PowerCard powerCard) {
            int combinedPower = powerCard.getCardBasePowerRating() + powerCard.getCardWeatherModifier() + powerCard.getCardMoraleModifier() + powerCard.getCardBondModifier();;
            powerLabel.setText(Integer.toString(combinedPower));
        }
        powerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        powerLabel.setMinimumSize(POWER_LABEL_SIZE);
        cardPanel.add(powerLabel, gbc);
    }

    private void addNameLabel(Card card, GridBagConstraints gbc) {
        JLabel nameLabel = new JLabel(card.getName(), JLabel.CENTER);
        nameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        nameLabel.setMaximumSize(NAME_LABEL_SIZE);
        cardPanel.add(nameLabel, gbc);
    }

    private void addDescriptionLabel(Card card, GridBagConstraints gbc) {
        JLabel descLabel = new JLabel("<html>" + card.getDescription() + "</html>", JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        cardPanel.add(descLabel, gbc);
    }

    private void addCardTypeLabel(Card card, GridBagConstraints gbc) {
        JLabel cardTypeLabel = new JLabel(String.valueOf(card.getCardType().getInitial()), JLabel.CENTER);
        cardTypeLabel.setOpaque(true); // Allow background coloring
        cardTypeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cardTypeLabel.setPreferredSize(TYPE_LABEL_SIZE);
        cardTypeLabel.setMinimumSize(TYPE_LABEL_SIZE);
        cardTypeLabel.setMaximumSize(TYPE_LABEL_SIZE);

        // Set background color based on card type
        cardTypeLabel.setBackground(card.getCardType().getColor());

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        cardPanel.add(cardTypeLabel, gbc);
    }

    /**
     * Creates a tooltip for a card, and makes visibile on card (button) hover.
     */

    public void addCardToolTip(JPanel cardPanel, Card card) {
        // Initialise a Map to store attributes
        Map<String, String> cardAttributes = new LinkedHashMap<>();

        // Store attributes
        cardAttributes.put("Name", card.getName());
        cardAttributes.put("Faction", card.getFaction());
        cardAttributes.put("Type", String.valueOf(card.getCardType()));
        cardAttributes.put("Ability", String.valueOf(card.getAbility().getName())); 
        cardAttributes.put("Description", card.getDescription());

        // Coniditional - if card is a PowerCard, store additional attributes.
        if (card instanceof PowerCard) {
            PowerCard castedPowerCard = (PowerCard) card;
            cardAttributes.put("Power", String.valueOf(castedPowerCard.getCardBasePowerRating()));
            cardAttributes.put("Power Modifier", String.valueOf(castedPowerCard.getCardWeatherModifier()));
            cardAttributes.put("Morale Modifier", String.valueOf(castedPowerCard.getCardMoraleModifier()));
            cardAttributes.put("Bond Modifier", String.valueOf(castedPowerCard.getCardBondModifier()));
        }

        // Initialise a StringBuilder object
        StringBuilder toolTip = new StringBuilder("<html>");

        // Loop, appending attributes from the Map to the StringBuilder object.
        for (Map.Entry<String, String> cardEntry : cardAttributes.entrySet()) {
            toolTip.append(cardEntry.getKey()).append(": ").append(cardEntry.getValue()).append("<br>");
        }

        toolTip.append("</html>");
        cardPanel.setToolTipText(toolTip.toString());

        // Add mouse listener - enables 'on-hover' functionality.
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(cardPanel, 0, 0, 0, e.getX(), e.getY(), 0, false));
            }
        });
    }
    public JPanel getPanel() {
        return cardPanel;
    }
}