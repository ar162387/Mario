package minigames.client.gwent.ui;

import minigames.client.gwent.GameController;
import minigames.gwent.GameState;
import minigames.gwent.cards.Card;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WeatherPanel {
    private static final Logger logger = LogManager.getLogger(WeatherPanel.class);

    private GameState gameState;
    private GameController gameController;
    private final JPanel weatherPanel;

    public WeatherPanel(GameState gameState, GameController gameController) {
        this.gameState = gameState;
        this.gameController = gameController;
        this.weatherPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        initialiseWeatherPanel();
    }

    private void initialiseWeatherPanel() {
        weatherPanel.setBorder(BorderFactory.createTitledBorder("Weather Panel"));
        weatherPanel.add(new JPanel(), BorderLayout.CENTER);
    }

    private void populateWeather() {
        weatherPanel.removeAll();
        List<Card> weatherCards = gameState.getWeatherCards();
        logger.info("Weather cards active: {}", weatherCards);
        int offset = 0;

        for (int i = 0; i < weatherCards.size(); i++) {
            Card card = weatherCards.get(i);
            CardPanel cardPanel = new CardPanel(card, i, offset, true, gameController);
            offset += cardPanel.getWidth() + 10; // Adjust offset for the next card

            weatherPanel.add(cardPanel.getPanel());
        }
    }

    public void update() {
        weatherPanel.removeAll();

        populateWeather();

        weatherPanel.revalidate();
        weatherPanel.repaint();
    }

    public JPanel getPanel() {
        return weatherPanel;
    }
}
