package minigames.client.gwent.ui;

import minigames.client.gwent.GameController;
import minigames.gwent.GameState;
import javax.swing.*;
import java.awt.*;

public class LeftSidePanel {
    private final JPanel sidePanel;
    PlayerPanel player1Panel;
    PlayerPanel player2Panel;

    WeatherPanel weatherPanel;

    public LeftSidePanel(GameState gameState, GameController gameController) {
        this.sidePanel = new JPanel(new GridLayout(3,1));
        this.player1Panel = new PlayerPanel(gameState.getPlayerOne());
        this.player2Panel = new PlayerPanel(gameState.getPlayerTwo());
        this.weatherPanel = new WeatherPanel(gameState, gameController);

        initialiseSidePanel();
    }

    private void initialiseSidePanel() {
        sidePanel.setBorder(BorderFactory.createTitledBorder("Side Panel"));

        sidePanel.add(player2Panel.getPanel());
        sidePanel.add(weatherPanel.getPanel());
        sidePanel.add(player1Panel.getPanel());
    }

    public JPanel getPanel() {
        return sidePanel;
    }

    public void update() {
        sidePanel.revalidate();
        sidePanel.repaint();

        player2Panel.update();
        weatherPanel.update();
        player1Panel.update();
    }
}
