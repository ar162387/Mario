package minigames.client.bomberman;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;

public class Pause {
    private final Stage stage;
    private final Game game;
    private int selectedIndex = 0;
    private VBox pauseLayout;
    private Text[] pauseOptions;
    private Scene pauseScene;  
    private SequentialTransition sequentialTransition;

    public Pause(Stage stage, Game game){
        this.stage = stage;
        this.game = game;

        buildPause();
    }

    private void buildPause() {
        pauseLayout = new VBox(10);
        pauseLayout.setStyle("-fx-background-color: black");
        pauseLayout.setAlignment(javafx.geometry.Pos.CENTER); // center the menu for aesthetic reasons

        // Spacer between title pause menu
        Region titleSpacer1 = new Region();
        titleSpacer1.setMinHeight(40); // Minimum height for the spacer
        pauseLayout.getChildren().add(titleSpacer1);    

        Text title = new Text("Game Paused");
        title.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 50px; -fx-fill: white;");
        pauseLayout.getChildren().add(title);

        // Spacer between title pause menu
        Region titleSpacer2 = new Region();
        titleSpacer2.setMinHeight(30); // Minimum height for the spacer
        pauseLayout.getChildren().add(titleSpacer2);        

        // create menu options
        Text resumeGame = new Text("Resume");
        Text leaderBoard = new Text("Leaderboard");
        Text password = new Text("Password");
        Text controls = new Text("Controls");
        Text mainMenu = new Text("Menu");

        pauseOptions = new Text[]{resumeGame, leaderBoard, password, controls, mainMenu};
        pauseLayout.getChildren().addAll(resumeGame, leaderBoard, password, controls, mainMenu);

        // cycle through menu using the arrow keys, enter to select
        pauseLayout.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    moveSelection(-1);
                    break;
                case DOWN:
                    moveSelection(1);
                    break;
                case ENTER:
                    executeSelection();
                    break;
                case ESCAPE:
                    game.setState(GameState.PLAYING); // escape will return to the game
                    break;
                default:
                    break;
            }
        });
        // initially select the first option
        updateSelection();
        
        // create the pause scene
        pauseScene = new Scene(pauseLayout, 624, 566);
    }

    /**
     * shows the menu on the stage
     */
    public void show() {
        stage.setScene(pauseScene);
        pauseLayout.requestFocus();
    }

    /**
     * Helper function for moving around in the menu
     * @param direction
     */
    private void moveSelection(int direction) {
        // Update the selected index based on direction
        sequentialTransition.stop(); // stop the animation for the old selected item
        pauseOptions[selectedIndex].setOpacity(1); // set the opacity of the old selected item back to normal in case it's mid transition
        selectedIndex = (selectedIndex + direction + pauseOptions.length) % pauseOptions.length; // Wrap around
        updateSelection();
    }

    /**
     * Helper function to highlight the selected text
     */
    private void updateSelection() {
        // highlight the selected option by utilising styles
        for (int i = 0; i < pauseOptions.length; i++) {
            Text text = pauseOptions[i];
            if (i == selectedIndex) {
                text.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: yellow;"); // Highlight
                sequentialTransition = animateSelection(pauseOptions[selectedIndex]); // animate the newly selected menu item
            } else {
                text.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 20px; -fx-fill: white;"); // Ensure everything else is not highlighted
            }
        }
    }

    /**
     * Helper function to run the selected option
     */
    private void executeSelection() {
        switch (selectedIndex) {
            case 0:
                game.setState(GameState.PLAYING); // calling the setState method in game also runs the show method in the selected game state / class
                break;
            case 1:
                game.setState(GameState.LEADERBOARD);
                break;
            case 2:
                game.setState(GameState.PASSWORD);
                break;
            case 3:
                game.setState(GameState.CONTROLS);
                break;
            case 4:
                game.setPaused(0);
                moveSelection(1);
                game.setState(GameState.MAIN_MENU);
                break;
        }
    }
    public SequentialTransition animateSelection(Node javaFXNode){
        // Create a FadeTransition for fading in
        FadeTransition fadeIn = new FadeTransition();
        fadeIn.setDuration(Duration.millis(250));
        fadeIn.setNode(javaFXNode);
        fadeIn.setFromValue(0.0); // Fully transparent
        fadeIn.setToValue(1.0);   // Fully opaque

        // Create a FadeTransition for fading out
        FadeTransition fadeOut = new FadeTransition();
        fadeOut.setDuration(Duration.millis(250));
        fadeOut.setNode(javaFXNode);
        fadeOut.setFromValue(1.0); // Fully opaque
        fadeOut.setToValue(0.0);   // Fully transparent

        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(fadeIn, fadeOut);

        // Set the SequentialTransition to repeat indefinitely
        sequentialTransition.setCycleCount(SequentialTransition.INDEFINITE);

        // Start the transition
        sequentialTransition.play();
        return sequentialTransition;
    }
}
