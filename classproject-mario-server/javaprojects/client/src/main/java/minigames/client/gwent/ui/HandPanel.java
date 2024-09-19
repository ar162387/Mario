package minigames.client.gwent.ui;

import minigames.client.gwent.GameController;
import minigames.gwent.Hand;
import minigames.gwent.Player;
import minigames.gwent.cards.Card;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;

public class HandPanel extends JLayeredPane {

    private static final Logger logger = LogManager.getLogger(HandPanel.class);
    private GameController gameController;
    private Player player;
    private JLayeredPane handPanel;

    public HandPanel(GameController gameController, Player player) {
        this.gameController = gameController;
        this.player = player;
        this.handPanel = new JLayeredPane();

        initialiseHandPanel();
    }

    private void initialiseHandPanel() {
        handPanel.setLayout(null);
        handPanel.setPreferredSize(new Dimension(600, 220));
        logger.info("Attempt to populate {} hand", player.getName());


        populateHand(player.getHand());
    }

    private void populateHand(Hand hand) {
        int offset = 0;

        for (int i = 0; i < hand.getCards().size(); i++) {
            Card card = hand.getCards().get(i);
            CardPanel cardPanel = new CardPanel(card, i, offset, true, gameController);
            offset += 70;
            cardPanel.putClientProperty("originalY", 20);
            cardPanel.putClientProperty("cardIndex", i);
            handPanel.setLayer(cardPanel.getPanel(), i);
            handPanel.add(cardPanel.getPanel());
            addCardHoverListeners(cardPanel.getPanel(), handPanel, card);
        }
    }

    /**
     * Method to add hover listeners for cards, used in Hand.
     *
     * @param cardPanel JPanel of the cards
     * @param handPanel JLayeredPane of the hand
     */
    private void addCardHoverListeners(JPanel cardPanel, JLayeredPane handPanel, Card card) {
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                handPanel.moveToFront(cardPanel);
                cardPanel.setBorder(new LineBorder(Color.YELLOW, 2));
                animateCardPosition(cardPanel, cardPanel.getY() - 20);
                handPanel.setLayer(cardPanel, JLayeredPane.DRAG_LAYER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                Integer originalY = (Integer) cardPanel.getClientProperty("originalY");
                Integer cardIndex = (Integer) cardPanel.getClientProperty("cardIndex");
                if (originalY != null && cardIndex != null) {
                    animateCardPosition(cardPanel, originalY);
                    cardPanel.setBorder(new LineBorder(Color.BLACK, 1));

                    handPanel.setLayer(cardPanel, JLayeredPane.DEFAULT_LAYER);

                    Timer resetZOrderTimer = new Timer(5, e1 -> {
                        handPanel.setLayer(cardPanel, cardIndex);
                        ((Timer) e1.getSource()).stop();
                    });
                    resetZOrderTimer.start();
                }
            }
        });
    }

    /**
     * Method to animate the card when hovering/clicking
     *
     * @param cardPanel JPanel of the card we want to add the animation too
     * @param targetY   int How far the panel "raises"
     */
    private void animateCardPosition(JPanel cardPanel, int targetY) {
        // Cancel any existing animation timer if it exists
        Timer existingTimer = (Timer) cardPanel.getClientProperty("animationTimer");
        if (existingTimer != null) {
            existingTimer.stop();
        }

        Timer timer = new Timer(5, e -> {
            int currentY = cardPanel.getY();
            if (currentY != targetY) {
                int step = Math.min(Math.abs(targetY - currentY), 1);
                step = currentY < targetY ? step : -step;

                cardPanel.setLocation(cardPanel.getX(), currentY + step);
                cardPanel.repaint();
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        timer.start();

        cardPanel.putClientProperty("animationTimer", timer);
    }

    public void update() {
        handPanel.removeAll(); // Clear all existing components

        populateHand(player.getHand());

        // Workaround timer, fixes the cards blending together on startup
        javax.swing.Timer repaintTimer = new javax.swing.Timer(100, e -> {
            handPanel.revalidate();
            handPanel.repaint();
            ((javax.swing.Timer) e.getSource()).stop();
        });
        repaintTimer.start();
    }

    public JLayeredPane getPanel() {
        return handPanel;
    }
}