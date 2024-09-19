package minigames.client.gwent.ui;

import minigames.gwent.GameState;
import minigames.client.gwent.GameController;

import javax.swing.*;
import java.awt.*;

public class CenterPanel extends JPanel {
    private final JPanel centerPanel;

    GameController gameController;
    GameState gameState;
    HandPanel player1HandPanel;
    HandPanel player2HandPanel;
    BattlefieldPanel battlefieldPanel;

    public CenterPanel(GameController gameController, GameState gameState) {
        this.gameController = gameController;
        this.gameState = gameState;
        this.player1HandPanel = new HandPanel(gameController, gameState.getPlayerOne());
        this.player2HandPanel = new HandPanel(gameController, gameState.getPlayerTwo());
        this.battlefieldPanel = new BattlefieldPanel(gameController, gameState);

        this.centerPanel = new JPanel(new BorderLayout());

        initialiseCenterPanel();
    }

    public void initialiseCenterPanel() {
        centerPanel.add(player2HandPanel.getPanel(), BorderLayout.NORTH);
        centerPanel.add(battlefieldPanel.getPanel(), BorderLayout.CENTER);
        centerPanel.add(player1HandPanel.getPanel(), BorderLayout.SOUTH);
    }

    public void update() {
        player1HandPanel.update();
        player2HandPanel.update();
        battlefieldPanel.update();
    }

    public JPanel getPanel() {
        return centerPanel;
    }
}
