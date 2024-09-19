package minigames.client.gwent.ui;

import minigames.client.gwent.GameController;
import minigames.gwent.Player;

import javax.swing.*;
import java.awt.*;

public class RightSidePanel {
    private final JPanel sidePanel;
    private final JLabel player1Label;
    private final JLabel player2Label;
    GameController gameController;
    PassTurnPanel passTurnPanel;

    public RightSidePanel(Player player1, Player player2, GameController gameController) {
        this.gameController = gameController;
        this.sidePanel = new JPanel(new BorderLayout());
        this.passTurnPanel = new PassTurnPanel(gameController);
        this.player2Label = new JLabel("Player 2 Deck/Graveyard", SwingConstants.CENTER);
        this.player1Label = new JLabel("Player 1 Deck/Graveyard", SwingConstants.CENTER);
        initialiseSidePanel();
    }

    private void initialiseSidePanel() {
        sidePanel.setBorder(BorderFactory.createTitledBorder("Side Panel"));
        sidePanel.add(player2Label, BorderLayout.NORTH);
        sidePanel.add(player1Label, BorderLayout.SOUTH);
        sidePanel.add(passTurnPanel.getPanel(), BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return sidePanel;
    }

    public void update() {
        sidePanel.revalidate();
        sidePanel.repaint();
    }
}