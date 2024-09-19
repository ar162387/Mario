package minigames.client.EightBall.UI;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Custom button class for the EightBall game UI.
 * This class extends the JavaFX Button class and provides custom styles
 * and behavior specific to the EightBall game.
 */
public class EightBallButton extends Button {
    private final String FONT_PATH = "/kenvector_future.ttf";
    private final String BUTTON_FREE_STYLE;
    private final String BUTTON_PRESSED_STYLE;

    /**
     * Constructor for the EightBallButton.
     * Initializes the button with the provided text, sets the font, styles, and event listeners.
     *
     * @param text The text to be displayed on the button.
     */
    public EightBallButton(String text) {
        BUTTON_FREE_STYLE = generateButtonStyle("/EightBall/yellow_button.png");
        BUTTON_PRESSED_STYLE = generateButtonStyle("/EightBall/yellow_button_pressed.png");

        setText(text);
        setButtonFont();

        setPrefWidth(190);
        setPrefHeight(49);

        setStyle(BUTTON_FREE_STYLE);
        initialiseButtonListener();
    }

    /**
     * Generates a CSS style string for the button using the provided image path.
     *
     * @param imagePath The path to the image to be used for the button's background.
     * @return The generated CSS style string.
     */
    private String generateButtonStyle(String imagePath) {
        try {
            return "-fx-background-color: transparent; -fx-background-image: url('" + getClass().getResource(imagePath).toExternalForm() + "')";
        } catch (Exception e) {
            e.printStackTrace();
            return "-fx-background-color: transparent;";
        }
    }

    /**
     * Sets the font for the button text.
     * Attempts to load a custom font. If loading fails, it falls back to a default font.
     */
    private void setButtonFont() {
        try {
            Text buttonText = new Text();
            buttonText.setFont(Font.loadFont("file:/kenvector_future.ttf", 23)); // not working
        } catch (Exception e) {
            setFont(Font.font("Verdana", 23));
        }
    }

    /**
     * Sets the style for the button when it is pressed.
     * This includes changing the style and adjusting the button's layout.
     */
    private void setButtonPressedStyle() {
        setStyle(BUTTON_PRESSED_STYLE);
        setPrefHeight(45);
        setLayoutY(getLayoutY() + 4); // Move the button down slightly to simulate pressing.
    }

    /**
     * Sets the style for the button when it is released.
     * This includes resetting the style and layout to the default state.
     */
    private void setButtonReleasedStyle() {
        setStyle(BUTTON_FREE_STYLE);
        setPrefHeight(49);
        setLayoutY(getLayoutY() - 4); // Move the button up slightly to simulate release.
    }

    /**
     * Initializes the event listeners for the button.
     * These listeners handle mouse interactions such as pressing, releasing, entering, and exiting.
     */
    private void initialiseButtonListener() {
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    setButtonPressedStyle(); // Apply pressed style when the primary mouse button is pressed.
                }
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    setButtonReleasedStyle(); // Revert to released style when the primary mouse button is released.
                }
            }
        });

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Handle mouse enter event (currently does nothing).
            }
        });

        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setEffect(null); // Clear any effects when the mouse exits the button area.
            }
        });
    }
}
