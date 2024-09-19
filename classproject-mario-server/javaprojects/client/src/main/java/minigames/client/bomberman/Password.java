// imports
package minigames.client.bomberman;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.layout.Region;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

/**
 * The password class is responsible for receiving user entered cheat codes. 
 * 
 * Contributors: Jacob Derbyshire - jderbysh@myune.edu.au
 */
public class Password {
    private final Stage stage;
    private final Game game;
    private VBox passwordLayout;
    private Scene passwordScene;
    private TextField passcodeField;
    private Text title;
    private Node[] pageNodes;
    private int boom = 0; // set original value aka cheat code off
    private int fast = 0; // set original value aka cheat code off

    /**
     * Constructor
     * @param stage - the JavaFX stage
     * @param game - Game object
     */
    public Password(Stage stage, Game game){
        this.stage = stage;
        this.game = game;
        buildPassword();
    }

    /**
     * private function that creates the password page
     */
    private void buildPassword() {
        passwordLayout = new VBox(20);
        passwordLayout.setAlignment(javafx.geometry.Pos.CENTER); // center the layout for aesthetic reasons
        passwordLayout.setStyle("-fx-background-color: black;");

        // Create the scene with the passwordLayout so I can pass it through to the stage
        passwordScene = new Scene(passwordLayout, 640, 480);

        // set title for password page
        title = new Text("Password");
        title.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 40px; -fx-fill: white;");
        passwordLayout.getChildren().add(title); 

        // Spacer between title and password entries
        Region titleSpacer = new Region();
        titleSpacer.setMinHeight(10); // Minimum height for the spacer
        passwordLayout.getChildren().add(titleSpacer);

        // Create the TextField for password input
        passcodeField = new TextField();
        passcodeField.setMaxWidth(220); // Set the max width for the text field so it's centered and also for aesthetic reasons
        passcodeField.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 40px; -fx-text-fill: white; -fx-background-color: #333;");

        // Limit the TextField to 4 characters (not limited to numeric input)
        passcodeField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > 4) {
                passcodeField.setText(newValue.substring(0, 4));
            }
        });

        // Add the TextField to the layout
        passwordLayout.getChildren().add(passcodeField); 

        pageNodes = new Node[]{title, passcodeField}; // add page nodes to a set so that I can loop through them

        // Handle enter key for validation
        passcodeField.setOnAction(event -> validatePasscode()); // setOnAction listens for the "enter" key press

        // escape to go back to the main menu
        passwordLayout.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // Create a FadeTransition
                for (int i=0; i<pageNodes.length; i++){
                    MenuController.transition(pageNodes[i], 250, 1, 0); // loop through the page nodes to produce fade out effect
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
     * function to check if the passcode entered matches one of the valid passcodes.
     */
    private void validatePasscode() {
        String passcode = passcodeField.getText();
        if (passcode.length() == 4) {
            System.out.println("Passcode entered: " + passcode);
            passcodeField.setText("");
            switch (passcode) {
                case "test" -> DebugManager.getInstance().toggleDebugMode(); // turns on and off debug mode
                // checks if the cheat code is already active or not and sets the value accordingly
                case "fast" -> {
                    if (fast == 0) {
                        game.setSpeed(3.5);
                        fast = 1;
                    }
                    else {
                        game.setSpeed(1.5);
                        fast = 0;
                    }
                }
                // checks if the cheat code is already active or not and sets the value accordingly
                case "boom" -> {
                    if (boom == 0) {
                        game.setPower(2);
                        boom = 1;
                    }
                    else {
                        game.setPower(1);
                        boom = 0;
                    }
                }
            }
        }
    }

    /**
     * shows the password page on the stage
     */
    public void show(){                    
        for (int i=0; i<pageNodes.length; i++){
            pageNodes[i].setOpacity(0); // set the opacity to 0 for the fade in effect
        }
        stage.setScene(passwordScene);
        for (int i=0; i<pageNodes.length; i++){
            MenuController.transition(pageNodes[i], 250, 0, 1); // loop through the page nodes to produce fade in effect
        }
        passcodeField.requestFocus(); // focus on the password field so user can enter immediately
    }
}
