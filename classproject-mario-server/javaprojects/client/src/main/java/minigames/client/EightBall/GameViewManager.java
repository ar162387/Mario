package minigames.client.EightBall;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import minigames.client.EightBall.UI.PLAYER;

import java.awt.*;

import static java.awt.SystemColor.menu;

public class GameViewManager {

    private AnchorPane gamePane;
    private EightBallController gameScene;
    private Stage gameStage;

    private javafx.scene.canvas.Canvas canvas;

    private Stage menuStage;
    private ImageView player;
    private ImageView pool_table;
    private ImageView backButton;

    private ImageView playerOne;

    private ImageView playerTwo;


    public static final String POOL_TABLE = "/EightBall/pool-table.png";

    public static final String BACK_BUTTON = "/EightBall/back-button.png";

    public static final String PLAYER_ONE = "/EightBall/player-one.png";
    public static final String PLAYER_TWO = "/EightBall/player-two.png";

    private static final int GAME_WIDTH = 1024;
    private static final int GAME_HEIGHT = 768;


    public GameViewManager() {
        initStage();
    }

    private void createMouseListeners(EightBallController _scene) {
        _scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    _scene.mousePrimary();

                } else if(event.getButton() == MouseButton.SECONDARY) {
                    _scene.mouseSecondary();

                }
            }
        });

        _scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _scene.mouseMove((float) event.getX(), (float) event.getY());
            }
        });


        _scene.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {

                } else if(event.getButton() == MouseButton.SECONDARY) {

                }
            }
        });
    }

    public void initStage() {
        gamePane = new AnchorPane();
        canvas = new Canvas(GAME_WIDTH,GAME_HEIGHT);
        gameScene =new EightBallController(gamePane,GAME_WIDTH,GAME_HEIGHT,canvas);
        createMouseListeners(gameScene);
        gameStage = new Stage();
        gameStage.setTitle("EightBall");
        gameStage.setScene(gameScene);
        gameStage.setResizable(false);

        //Prepares canvas that game will be drawn on.


    }

    public void startNewGame(Stage menuStage, PLAYER chosenPlayer) {
        this.menuStage = menuStage;
        this.menuStage.hide();


        displayBackground();
        createGameElements(chosenPlayer);




        gameStage.show();
    }

    private void displayBackground() {

		Image bgImage =  new Image("EightBall/game-bg.png", GAME_WIDTH, GAME_HEIGHT, true, true);
		BackgroundImage bg = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,new BackgroundSize(GAME_WIDTH, GAME_HEIGHT, false, false, false, false));
		gamePane.setBackground(new Background(bg));
    }

    private void createGameElements(PLAYER chosenPlayer) {
        //Create table image
        pool_table = new ImageView(POOL_TABLE);
        pool_table.setFitWidth(900);
        pool_table.setFitHeight(700);
        pool_table.setLayoutX(50);
        pool_table.setLayoutY(50);

        // Create back button
        backButton = new ImageView(BACK_BUTTON);
        backButton.setFitWidth(50);
        backButton.setFitHeight(50);
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);

        // Create player one icon
        playerOne = new ImageView(PLAYER_ONE);
        playerOne.setFitWidth(100);
        playerOne.setFitHeight(100);
        playerOne.setLayoutX(350);
        playerOne.setLayoutY(20);

        // Player 1 name
        Label playerOneLabel = new Label("Player 1");
        playerOneLabel.setFont(Font.font("Verdana",20));
        playerOneLabel.setLayoutX(250);
        playerOneLabel.setLayoutY(40);
        playerOneLabel.setStyle("-fx-text-fill: white;");

        // Create player two icon
        playerTwo = new ImageView(PLAYER_TWO);
        playerTwo.setFitWidth(100);
        playerTwo.setFitHeight(100);
        playerTwo.setLayoutX(550);
        playerTwo.setLayoutY(20);

        // Player 2 name
        Label playerTwoLabel = new Label("Player 2");
        playerTwoLabel.setFont(Font.font("Verdana",20));
        playerTwoLabel.setLayoutX(650);
        playerTwoLabel.setLayoutY(40);
        playerTwoLabel.setStyle("-fx-text-fill: white;");



        // Handle back button click
//        backButton.setOnMousePressed(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                // Assuming you have a method to switch to the main menu scene
//                goToMainMenu(menuStage);
//            }
//        });


        gamePane.getChildren().addAll(pool_table, playerOne, playerOneLabel, playerTwo, playerTwoLabel, backButton,canvas); //backButton
    }

    public EightBallController getController() {
        return gameScene;
    }

    // Method to switch to the main menu scene
//    private void goToMainMenu(Stage menuStage) {
//        this.menuStage = menuStage;
//        menuStage.show();    }
}
