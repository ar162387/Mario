package minigames.client.gwent.ui;

import minigames.gwent.GameState;
import minigames.gwent.cards.CardType;
import minigames.gwent.Player;
import minigames.client.gwent.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BattlefieldPanel extends JPanel {
    private final GameController gameController;
    private final GameState gameState;
    private final JPanel battlefieldPanel;
    private final Map<String, LanePanel> lanePanels;

    public BattlefieldPanel(GameController gameController, GameState gameState) {
        this.gameController = gameController;
        this.gameState = gameState;
        this.battlefieldPanel = new JPanel(new GridLayout(6, 1));
        this.lanePanels = new HashMap<>();

        initialiseBattlefieldPanel();
    }

    public void initialiseBattlefieldPanel() {
        addLanePanel("Player 2 Siege", CardType.SIEGE, gameState.getPlayerTwo());
        addLanePanel("Player 2 Range", CardType.RANGE, gameState.getPlayerTwo());
        addLanePanel("Player 2 Melee", CardType.MELEE, gameState.getPlayerTwo());
        addLanePanel("Player 1 Melee", CardType.MELEE, gameState.getPlayerOne());
        addLanePanel("Player 1 Range", CardType.RANGE, gameState.getPlayerOne());
        addLanePanel("Player 1 Siege", CardType.SIEGE, gameState.getPlayerOne());
    }

    private void addLanePanel(String title, CardType zone, Player player) {
        LanePanel lanePanel = new LanePanel(title, zone, player, gameController);
        battlefieldPanel.add(lanePanel.getPanel());
        lanePanels.put(player.getName() + "_" + zone.name(), lanePanel);
    }

    public void update() {
        for (LanePanel lanePanel : lanePanels.values()) {
            lanePanel.update();
        }
        revalidate();
        repaint();
    }

    public JPanel getPanel() {
        return battlefieldPanel;
    }
}
