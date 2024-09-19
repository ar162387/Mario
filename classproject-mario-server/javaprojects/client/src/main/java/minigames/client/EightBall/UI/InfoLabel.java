package minigames.client.EightBall.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Custom label class for displaying information in the EightBall game UI.
 */
public class InfoLabel extends Label {
    public final static String FONT_PATH = "/EightBall/fonts/kenvector_future.ttf";
    public final static String BG_IMAGE = "/EightBall/infolabel_bg.png";

    /**
     * Initialises the label with specific dimensions, padding, text, and background.
     *
     * @param text The text to be displayed on the label.
     */
    public InfoLabel(String text) {
        setPrefWidth(600);
        setPrefHeight(50);
        setText(text);
        setPadding(new Insets(10, 40, 40, 50));
        setWrapText(true);
        setLabelFont();
        //setAlignment(Pos.CENTER);

        BackgroundImage bgImage = new BackgroundImage(new Image(BG_IMAGE, 380, 50, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);
        setBackground(new Background(bgImage));
    }


    /**
     * Sets the font for the label text.
     */
    private void setLabelFont() {
        try {
            setFont(Font.loadFont(new FileInputStream(new File(FONT_PATH)), 23)); // Load custom font from file.
        } catch (FileNotFoundException e) {
            System.out.println("Could not load font. Using defaults...");
            setFont(Font.font("Verdana", 23)); // Fallback to a default font if the custom font fails to load.
        }
    }
}
