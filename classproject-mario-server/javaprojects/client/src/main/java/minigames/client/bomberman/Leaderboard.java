package minigames.client.bomberman;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.ArrayList;
import javafx.application.Platform;

/**
 * The Leaderboard class is responsible for displaying the leaderboard of the game.
 * It fetches the data from the server using DataSingleton and updates the UI dynamically.
 */
public class Leaderboard {
    private final Stage stage;
    private final Game game;
    private VBox leaderboardLayout;
    private Scene leaderboardScene;
    private List<Node> pageNodes;

    // Reference to the Singleton that handles data fetching
    private DataSingleton dataSingleton;

    /**
     * Constructor
     * @param stage - the JavaFX stage
     * @param game - Game object
     */
    public Leaderboard(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        this.dataSingleton = DataSingleton.getInstance(); // Get Singleton instance
        buildLeaderboard();
    }

    /**
     * Creates the leaderboard visual and fetches the leaderboard data.
     */
    private void buildLeaderboard() {
        leaderboardLayout = new VBox(20); // Increased spacing between elements
        leaderboardLayout.setPadding(new Insets(20)); // Added padding around the VBox
        leaderboardLayout.setAlignment(Pos.CENTER); // Center the leaderboard layout
        leaderboardLayout.setStyle("-fx-background-color: black;");
        VBox.setVgrow(leaderboardLayout, Priority.ALWAYS);

        // Create the scene with the leaderboard layout
        leaderboardScene = new Scene(leaderboardLayout, 640, 480);

        Text title = new Text("Leaderboard");
        title.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 40px; -fx-fill: white;");
        leaderboardLayout.getChildren().add(title);

        // Spacer between title and leaderboard entries
        Region titleSpacer = new Region();
        titleSpacer.setMinHeight(20); // Minimum height for the spacer
        leaderboardLayout.getChildren().add(titleSpacer);

        // Empty placeholder for leaderboard data (this will be updated once data is fetched)
        Text loadingText = new Text("Loading leaderboard...");
        loadingText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;");
        leaderboardLayout.getChildren().add(loadingText);

        // Fetch leaderboard data from the server
        fetchLeaderboardData();

        // escape to go back to the main menu
        leaderboardLayout.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // Create a FadeTransition
                for (Node node : pageNodes) {
                    MenuController.transition(node, 250, 1, 0); // loop through the page nodes to produce fade out effect
                }
                // Create a PauseTransition to wait for the animation to complete
                PauseTransition pauseTransition = new PauseTransition(Duration.millis(250));
                pauseTransition.setOnFinished(e -> {
                    // Update the game state after the pause
                    if (game.getPaused() == 0) {
                        game.setState(GameState.MAIN_MENU);
                    }
                    if (game.getPaused() == 1) {
                        game.setState(GameState.PAUSE);
                    }
                });
                // Play the pause transition after the translation
                pauseTransition.play();
            }
        });
    }

    /**
     * Fetches the leaderboard data from the server and updates the UI.
     */
    private void fetchLeaderboardData() {
        // Fetch data using the Singleton
        dataSingleton.fetchData("/api/leaderboard/top/bomberman/5", "bombermanLeaderboard", this::displayLeaderboard);
    }

    /**
     * Updates the leaderboard display with the fetched data.
     * @param leaderboardData - the leaderboard data fetched from the server
     */
    private void displayLeaderboard(JsonArray leaderboardData) {
        // Clear existing nodes (if any) before updating with new data
        leaderboardLayout.getChildren().clear();

        // Add the title back after clearing the layout
        Text title = new Text("Leaderboard");
        title.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 40px; -fx-fill: white;");
        leaderboardLayout.getChildren().add(title);

        // Spacer between title and leaderboard entries
        Region titleSpacer = new Region();
        titleSpacer.setMinHeight(20); // Minimum height for the spacer
        leaderboardLayout.getChildren().add(titleSpacer);

        // Set up the headers for the leaderboard
        Text positionHeader = new Text("Pos");
        positionHeader.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;");
        Text nameHeader = new Text("Name");
        nameHeader.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;");
        Text scoreHeader = new Text("Score");
        scoreHeader.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;");

        VBox positionColumn = new VBox();
        positionColumn.getChildren().add(positionHeader);
        Region positionSpacer = new Region();
        positionSpacer.setMinHeight(20);
        positionColumn.getChildren().add(positionSpacer);

        VBox nameColumn = new VBox();
        nameColumn.getChildren().add(nameHeader);
        Region nameSpacer = new Region();
        nameSpacer.setMinHeight(20);
        nameColumn.getChildren().add(nameSpacer);

        VBox scoreColumn = new VBox();
        scoreColumn.getChildren().add(scoreHeader);
        Region scoreSpacer = new Region();
        scoreSpacer.setMinHeight(20);
        scoreColumn.getChildren().add(scoreSpacer);

        // Loop through leaderboard data and populate columns
        for (int i = 0; i < leaderboardData.size(); i++) {
            JsonObject row = leaderboardData.getJsonObject(i);
            String position = Integer.toString(i + 1);
            String name = row.getString("name");
            String score = row.getString("score");

            // Add position, name, and score to respective columns
            Text positionText = new Text(position);
            positionText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;");
            positionColumn.getChildren().add(positionText);
            Region spacer1 = new Region();
            spacer1.setMinHeight(10);
            positionColumn.getChildren().add(spacer1);

            Text nameText = new Text(name);
            nameText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;");
            nameColumn.getChildren().add(nameText);
            Region spacer2 = new Region();
            spacer2.setMinHeight(10);
            nameColumn.getChildren().add(spacer2);

            Text scoreText = new Text(score);
            scoreText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;");
            scoreColumn.getChildren().add(scoreText);
            Region spacer3 = new Region();
            spacer3.setMinHeight(10);
            scoreColumn.getChildren().add(spacer3);
        }

        // Spacer between the columns
        Region columnSpacer = new Region();
        columnSpacer.setMinWidth(50);

        Region columnSpacer2 = new Region();
        columnSpacer2.setMinWidth(50);

        // Add the columns to an HBox for display
        HBox dataColumns = new HBox(positionColumn, columnSpacer, nameColumn, columnSpacer2, scoreColumn);
        dataColumns.setAlignment(Pos.CENTER);

        leaderboardLayout.getChildren().add(dataColumns);

        // Store the nodes for future animation use (fade in/out effects)
        pageNodes = new ArrayList<>(leaderboardLayout.getChildren());

        leaderboardLayout.setAlignment(Pos.CENTER);
    }

    /**
     * Shows the leaderboard by fading in the content.
     */
    public void show() {
        fetchLeaderboardData();
        for (Node node : pageNodes) {
            node.setOpacity(0); // set the opacity to 0 for the fade in effect
        }
        stage.setScene(leaderboardScene);
        for (Node node : pageNodes) {
            MenuController.transition(node, 250, 0, 1); // loop through the page nodes to produce fade in effect
        }
        leaderboardLayout.requestFocus(); // Ensure the leaderboard can capture key events
    }
}
