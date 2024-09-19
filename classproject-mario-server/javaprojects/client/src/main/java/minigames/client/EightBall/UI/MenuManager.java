package minigames.client.EightBall.UI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import minigames.client.EightBall.EightBallCommands;
import minigames.client.EightBall.GameViewManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the main menu of the EightBall game, including navigation between subScenes and handling button actions.
 * This class creates and manages the main menu layout, background, and buttons, as well as the subScenes for different menu options.
 */
public class MenuManager {

    // Constants for the width and height of the main window.
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    // The main pane and scene that hold all the UI elements.
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    // Constants for the position of the menu buttons.
    private final static int MENU_BUTTONS_X = 130;
    private final static int MENU_BUTTONS_Y = 225;

    // Subscenes for different sections of the menu.
    private EightBallSubScene creditsSubScene;
    private EightBallSubScene helpSubScene;
    private EightBallSubScene scoreSubScene;
    private EightBallSubScene choosePlayerSubScene;

    // Keeps track of the currently visible subScene.
    private EightBallSubScene sceneToHide;

    // List to hold all the menu buttons.
    List<EightBallButton> menuButtons;

    // List to hold all the player picker components.
    List<PlayerPicker> playerList;

    // The player that is currently chosen.
    private PLAYER chosenPlayer;

    GameViewManager gameViewManager;
    EightBallCommands commandServer;

    /**
     * Constructor for the MenuManager.
     * Initializes the main menu with buttons, background, and subScenes.
     */
    public MenuManager(EightBallCommands commandServer) throws IOException {
        this.commandServer = commandServer;
        menuButtons = new ArrayList<>();
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();
        mainStage.setScene(mainScene);
        createSubScenes();
        createButtons();
        createBackground();
        displayLogo();
    }

    /**
     * Displays the selected subScene and hides the previous one if necessary.
     *
     * @param subScene The subScene to be displayed.
     */
    private void showSubScene(EightBallSubScene subScene) {
        if (sceneToHide != null) {
            sceneToHide.moveSubScene();
        }
        subScene.moveSubScene();
        sceneToHide = subScene;
    }

    /**
     * Creates various subScenes (credits, help, scores, choose player) and adds them to the main pane.
     */
    private void createSubScenes() {
        creditsSubScene = new EightBallSubScene();
        mainPane.getChildren().add(creditsSubScene);

        helpSubScene = new EightBallSubScene();
        mainPane.getChildren().add(helpSubScene);

        scoreSubScene = new EightBallSubScene();
        mainPane.getChildren().add(scoreSubScene);

        createChoosePlayerSubScene();
        createScoreSubScene();
        createHelpSubScene();
        createCreditsSubScene();
    }

    /**
     * Creates a subScene for choosing a player.
     */
    private void createChoosePlayerSubScene() {
        choosePlayerSubScene = new EightBallSubScene();
        mainPane.getChildren().add(choosePlayerSubScene);

        // Label for the choose player subScene.
        InfoLabel choosePlayerLabel = new InfoLabel("CHOOSE GAME MODE");
        choosePlayerLabel.setLayoutX(110);
        choosePlayerLabel.setLayoutY(25);
        choosePlayerSubScene.getPane().getChildren().add(choosePlayerLabel);
        choosePlayerSubScene.getPane().getChildren().add(createPlayerToChoose());
        choosePlayerSubScene.getPane().getChildren().add(createButtonToStart());
    }

    /**
     * Creates a subScene for score display.
     */
    private void  createScoreSubScene() {
        scoreSubScene = new EightBallSubScene();
        mainPane.getChildren().add(scoreSubScene);

        InfoLabel scoresLabel = new InfoLabel("LEADERBOARD");
        scoresLabel.setLayoutX(110);
        scoresLabel.setLayoutY(25);

        VBox scoreList = new VBox(10);
        scoreList.setLayoutX(150);
        scoreList.setLayoutY(100);

        Label scoreHeading = new Label("     Name			Score   ");
        //scoreHeading.setUnderline(true);
        Label score1 = new Label("Player 1		  100");
        Label score2 = new Label("Player 2		  100");
        Label score3 = new Label("Player 3		  100");
        scoreHeading.setFont(Font.font("Verdana",20));
        score1.setFont(Font.font("Verdana",20));
        score2.setFont(Font.font("Verdana",20));
        score3.setFont(Font.font("Verdana",20));

        scoreList.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(20), new Insets(-20,-20,-20,-20))));
        scoreList.getChildren().addAll(scoreHeading, score1, score2, score3);
        scoreSubScene.getPane().getChildren().addAll(scoresLabel, scoreList);
    }
    /**
     * Creates a subScene for how to play.
     */
    private void  createHelpSubScene() {
        helpSubScene = new EightBallSubScene();
        mainPane.getChildren().add(helpSubScene);

        InfoLabel helpLabel = new InfoLabel("HOW TO PLAY");
        helpLabel.setLayoutX(110);
        helpLabel.setLayoutY(25);

        VBox instructionList = new VBox(10);
        instructionList.setLayoutX(30);
        instructionList.setLayoutY(70);

        Label instructions = new Label( "\n -There are 7 solid, and 7 striped balls, "
                + "a black 8-ball, \n and a white cue-ball. The first player to sink a ball gets to \n play for the ball he sunk "
                + "ie. if player 1 sinks a striped ball first, then player 1 is stripes, and player 2 is solids"
                + " \n -A player is randomly chosen to break\n -If a ball is sunk, the player keeps playing until they miss "
                + "\n -Once they miss, it's the next player's turn \n -Sink all of the designated balls, and then shoot"
                + " at the 8-ball last to win \n-The 8-ball must be sunk last-sinking it before then will result"
                + " in an automatic loss \n -If the cue ball is sunk, the next player gets their turn with the ball in hand \n"
                + " -The cue ball must touch that player's type of ball (striped or solid), and the coloured ball that was hit"
                + " or the cue ball must touch a side of the table\n" //Use the left and right arrow keys to rotate the cue
                + "Click and hold the mouse to power up the cue. The longer you hold, the more power in your shot\nLet go to release the cue");

        instructions.setFont(Font.font("Verdana",16));


        instructionList.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(20), new Insets(-20,-20,-20,-20))));
        instructionList.getChildren().add(instructions);

        helpSubScene.getPane().getChildren().addAll(helpLabel, instructionList);

    }
    /**
     * Creates a subScene for credits.
     */
    private void  createCreditsSubScene() {
        creditsSubScene = new EightBallSubScene();
        mainPane.getChildren().add(creditsSubScene);

        InfoLabel creditsLabel = new InfoLabel("CREDITS");
        creditsLabel.setLayoutX(110);
        creditsLabel.setLayoutY(25);
        // bg: https://www.freepik.com/free-vector/billiards-club-template-design_40465790.htm#fromView=search&page=2&position=1&uuid=450b8569-fd38-4c32-8e9a-15f9f59e1316

        VBox creditList = new VBox(10);
        creditList.setLayoutX(150);
        creditList.setLayoutY(100);

        Label creditHeading = new Label("Sources");
        //scoreHeading.setUnderline(true);
        Label credit = new Label("Source link placeholder");
        creditHeading.setFont(Font.font("Verdana",20));
        credit.setFont(Font.font("Verdana",20));


        creditList.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(20), new Insets(-20,-20,-20,-20))));
        creditList.getChildren().addAll(creditHeading, credit);
        creditsSubScene.getPane().getChildren().addAll(creditsLabel, creditList);

    }

    /**
     * Creates a horizontal box with player pickers for selecting a player.
     *
     * @return The HBox containing player pickers.
     */
    private HBox createPlayerToChoose() {
        HBox box = new HBox();
        box.setSpacing(20);
        playerList = new ArrayList<>();

        // Loop through all available players and create a player picker for each.
        for (PLAYER player : PLAYER.values()) {
            PlayerPicker playerPicker = new PlayerPicker(player);
            playerList.add(playerPicker);
            box.getChildren().add(playerPicker);
            playerPicker.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for (PlayerPicker player : playerList) {
                        player.setIsPlayerChosen(false);
                    }
                    playerPicker.setIsPlayerChosen(true);
                    chosenPlayer = playerPicker.getPlayer();
                }
            });
        }
        box.setLayoutX(140);
        box.setLayoutY(100);
        return box;
    }

    private EightBallButton createButtonToStart() {
        EightBallButton startButton = new EightBallButton("Start");
        startButton.setLayoutX(350);
        startButton.setLayoutY(320);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                try {
//                    //SoundEffects.playSound(new URI(BUTTON_SFX));
//                } catch (URISyntaxException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                if (chosenPlayer != null) {
                    mainStage.hide();
                    gameViewManager = new GameViewManager();
                    gameViewManager.startNewGame(mainStage, chosenPlayer);
                    commandServer.sendSimpleCommand("clientReady");
                }
            }
        });
        return startButton;
    }
    /**
     * Returns the main stage containing the menu scene.
     *
     * @return The main stage for the menu.
     */
    public Stage getMainStage() {
        return mainStage;
    }

    /**
     * Adds a menu button to the main pane at a specific position and manages its layout.
     *
     * @param button The button to be added to the menu.
     */
    private void addMenuButton(EightBallButton button) {
        button.setLayoutX(MENU_BUTTONS_X);
        button.setLayoutY(MENU_BUTTONS_Y + menuButtons.size() * 100);
        button.setCursor(Cursor.HAND);
        menuButtons.add(button);
        mainPane.getChildren().add(button);
    }

    /**
     * Creates all the menu buttons and assigns them actions.
     */
    private void createButtons() throws IOException {
        createStartButton();
        createLeaderboardButton();
        createHelpButton();
        createCreditsButton();
        createExitButton();
    }

    /**
     * Creates the Start button and adds an action to show the player selection subScene.
     */
    private void createStartButton() {
        EightBallButton startButton = new EightBallButton("PLAY");
        addMenuButton(startButton);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSubScene(choosePlayerSubScene);
            }
        });
    }

    /**
     * Creates the Scores button and adds an action to show the scores subScene.
     */
    private void createLeaderboardButton() {
        EightBallButton leaderboardButton = new EightBallButton("LEADERBOARD");
        addMenuButton(leaderboardButton);

        leaderboardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSubScene(scoreSubScene);
            }
        });
    }

    /**
     * Creates the Help button and adds an action to show the help subScene.
     */
    private void createHelpButton() {
        EightBallButton helpButton = new EightBallButton("HOW TO PLAY");
        addMenuButton(helpButton);

        helpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSubScene(helpSubScene);
            }
        });
    }

    /**
     * Creates the Credits button and adds an action to show the credits subScene.
     */
    private void createCreditsButton() {
        EightBallButton creditsButton = new EightBallButton("CREDITS");
        addMenuButton(creditsButton);

        creditsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSubScene(creditsSubScene);
            }
        });
    }

    /**
     * Creates the Exit button and adds an action to close the application.
     */
    private void createExitButton() {
        EightBallButton exitButton = new EightBallButton("EXIT");
        addMenuButton(exitButton);

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainStage.close();
            }
        });
    }

    /**
     * Sets the background image for the main menu pane.
     */
    private void createBackground() {
        Image bgImage = new Image(
                getClass().getResource("/EightBall/menu-bg.png").toExternalForm(),
                WIDTH, HEIGHT, false, true
        );
        BackgroundImage bg = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, null);
        mainPane.setBackground(new Background(bg));
    }

    /**
     * Displays the game logo on the main menu.
     */
    private void displayLogo() {
        ImageView logo = new ImageView(
                getClass().getResource("/EightBall/pool-logo.png").toExternalForm()); // Load the logo image.
        logo.setFitWidth(300); // Set the width of the logo.
        logo.setFitHeight(150); // Set the height of the logo.

        // Set the position of the logo on the pane.
        logo.setLayoutX(80);
        logo.setLayoutY(50);

        mainPane.getChildren().add(logo); // Add the logo to the main pane.
    }

    public GameViewManager getGameViewManager() {
        return gameViewManager;
    }
}
