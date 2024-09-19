package minigames.client.EightBall.UI;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PlayerPicker extends VBox {
    private ImageView circleImage;
    private ImageView playerImage;

    private String notChosenCircle = "/EightBall/grey_circle_notchosen.png";
    private String chosenCircle = "EightBall/yellow_circle_chosen.png";

    private PLAYER player;

    private boolean isPlayerChosen;

    public PlayerPicker(PLAYER player) {
        circleImage = new ImageView(notChosenCircle);
        playerImage = new ImageView(player.getUrl());
        this.player = player;
        isPlayerChosen = false;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.getChildren().add(circleImage);
        this.getChildren().add(playerImage);
    }

    public PLAYER getPlayer() {
        return player;
    }

    public boolean isPlayerChosen() {
        return isPlayerChosen;
    }

    public void setIsPlayerChosen(boolean isPlayerChosen) {
        this.isPlayerChosen = isPlayerChosen;
        String imageToSet = this.isPlayerChosen ? chosenCircle : notChosenCircle;
        circleImage.setImage(new Image(imageToSet));
    }
}
