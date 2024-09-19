package minigames.client.connectfour;

import io.vertx.core.json.JsonArray;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;

public class ConnectFourClient implements ActionListener, GameClient {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 800;

    private JFrame frame;
    private ConnectFourWelcomeScreen welcomeScreen;
    private ConnectFourRulesScreen rulesScreen;
    private ConnectFourHighScoresScreen highScoresScreen;
    private ConnectFourGameLobbyScreen gameLobbyScreen;
    private ConnectFourGameBoardScreen gameBoardScreen;
    private MinigameNetworkClient networkClient;
    private GameMetadata gameMetadata;
    private String player;

    public ConnectFourClient() {
    }
    public String getPlayerName() {
        return player;
    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.networkClient = mnClient;
        this.gameMetadata = game;
        this.player = player;

        // Initialize the game UI here
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Connect Four");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

            welcomeScreen = new ConnectFourWelcomeScreen(this);
            rulesScreen = new ConnectFourRulesScreen(this);
            highScoresScreen = new ConnectFourHighScoresScreen(this);
            gameLobbyScreen = new ConnectFourGameLobbyScreen(this);
            gameBoardScreen = new ConnectFourGameBoardScreen(this);

            frame.add(welcomeScreen);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == welcomeScreen.getStartButton()) {
            showGameLobbyScreen();
        } else if (e.getSource() == welcomeScreen.getRulesButton()) {
            showRulesScreen();
        } else if (e.getSource() == welcomeScreen.getHighScoresButton()) {
            showHighScoresScreen();
        } else if (e.getSource() == welcomeScreen.getBackToMenuButton()) {
            closeGame();
        } else if (e.getSource() == rulesScreen.getBackButton()) {
            showWelcomeScreen();
        } else if (e.getSource() == highScoresScreen.getBackButton()) {
            showWelcomeScreen();
        } else if (e.getSource() == gameLobbyScreen.getBackButton()) {
            showWelcomeScreen();
        } else if (e.getSource() == gameLobbyScreen.getStartGameButton()) {
            startGame();
        }
    }

    private void showRulesScreen() {
        switchScreen(rulesScreen);
    }

    private void showWelcomeScreen() {
        switchScreen(welcomeScreen);
    }

    private void showHighScoresScreen() {
        switchScreen(highScoresScreen);
    }

    public void showGameLobbyScreen() {
        switchScreen(gameLobbyScreen);
        // For demonstration purposes, update the player list with some dummy data
        gameLobbyScreen.updatePlayerList(Arrays.asList("Player1", "Player2", "Player3"));
    }

    public void startGame() {
        JsonObject startCommand = new JsonObject().put("command", "startGame");
        networkClient.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), player, Collections.singletonList(startCommand)));
        switchScreen(gameBoardScreen);
    }

    public void quitGame() {
        // Implement the logic to quit the game and return to the welcome screen
        switchScreen(welcomeScreen);
    }

    public void goBack() {
        // Implement the logic to go back to the game lobby screen
        switchScreen(gameLobbyScreen);
    }

    private void switchScreen(JPanel screen) {
        frame.getContentPane().removeAll();
        frame.add(screen);
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        if (command.containsKey("error")) {
            JOptionPane.showMessageDialog(frame, "Error: " + command.getString("error"));
            return;
        }

        if (command.containsKey("grid")) {
            updateGameBoard(command.getJsonArray("grid"));
        }

        if (command.containsKey("activePlayer")) {
            updateActivePlayer(command.getString("activePlayer"));
        }

        if (command.containsKey("winState")) {
            handleWinState(command.getString("winState"));
        }
    }

    private void updateGameBoard(JsonArray grid) {
        gameBoardScreen.updateBoard(grid);
    }

    private void updateActivePlayer(String activePlayer) {
        gameBoardScreen.setActivePlayer(activePlayer);
    }

    private void handleWinState(String winState) {
        String message;
        switch (winState) {
            case "WIN":
                message = "Game over. " + gameBoardScreen.getActivePlayer() + " wins!";
                break;
            case "DRAW":
                message = "Game over. It's a draw!";
                break;
            default:
                return;
        }
        JOptionPane.showMessageDialog(frame, message);
        showWelcomeScreen();
    }

    public void makeMove(int column) {
        JsonObject moveCommand = new JsonObject()
                .put("command", "putDisk")
                .put("column", column);
        networkClient.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), player, Collections.singletonList(moveCommand)));
    }

    @Override
    public void closeGame() {
        if (frame != null) {
            frame.dispose();
        }
    }
}