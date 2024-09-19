package minigames.client.EightBall.UI;

import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * Custom subScene class for the EightBall game UI.
 * This class extends the JavaFX SubScene class and provides a custom appearance and animation for a sliding panel.
 */
public class EightBallSubScene extends SubScene {
    private final static String FONT_PATH = "/EightBall/fonts/kenvector_future.ttf";
    private boolean isHidden = true;

    /**
     * Constructor for the EightBallSubScene.
     */
    public EightBallSubScene() {
        super(new AnchorPane(), 600, 400);
        prefWidth(600);
        prefHeight(400);

        BackgroundImage image = new BackgroundImage(new Image(getClass().getResource("/EightBall/yellow_panel.png").toExternalForm(),
                600, 400, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);

        AnchorPane root2 = (AnchorPane) this.getRoot();
        root2.setBackground(new Background(image));

        setLayoutX(1024);
        setLayoutY(225);
    }

    /**
     * Moves the subScene in and out of view with a sliding animation.
     */
    public void moveSubScene() {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.3));
        transition.setNode(this);

        if (isHidden) {
            transition.setToX(-676);
            isHidden = false;
        } else {
            transition.setToX(0);
            isHidden = true;
        }
        transition.play();
    }

    /**
     * Returns the root pane of the subScene, allowing for further customisation or adding children.
     *
     * @return The AnchorPane that serves as the root of this subScene.
     */
    public AnchorPane getPane() {
        return (AnchorPane) this.getRoot();
    }
}
