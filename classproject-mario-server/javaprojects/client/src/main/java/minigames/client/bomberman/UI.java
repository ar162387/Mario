package minigames.client.bomberman;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UI {
    private int lives = 3;
    private int level = 1;
    private int score = 0;
    private Text hearts = new Text("♥♥♥");
    Text scoreText = new Text("Score: " + this.score);
    Text levelText = new Text("Level: " + this.level);
    VBox UIMenu = new VBox();
    Region columnSpacer2 = new Region(); // region to offset the hearts disappearing
    
    public UI(){
        updateUIMenu();
    };

    private void updateUIMenu() {
        levelText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 18px; -fx-fill: white; font-weight: 400;");
        Text livesText = new Text("Lives: ");
        livesText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 18px; -fx-fill: white;");
        hearts.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 24px; -fx-fill: crimson;");
        // hearts.setTextAlignment(Pos.CENTER);
        scoreText.setStyle("-fx-font-family: 'Press Start 2p'; -fx-font-size: 18px; -fx-fill: white;");
                // Spacer between the columns
        Region columnSpacer = new Region();
        columnSpacer.setMinWidth(50); // Minimum width for the spacer
        // Spacer between the columns
        columnSpacer2 = new Region();
        columnSpacer2.setMinWidth(50); // Minimum width for the spacer
        Region columnSpacer3 = new Region();
        columnSpacer3.setMinWidth(15); // Minimum width for the spacer
        Region rowSpacer = new Region();
        rowSpacer.setMinHeight(10);
        HBox UIColumns = new HBox(columnSpacer3, levelText, columnSpacer, livesText, this.hearts, columnSpacer2, scoreText);
        UIMenu = new VBox(rowSpacer, UIColumns);
        UIMenu.setAlignment(Pos.CENTER);
    }

    public void setLives (int lives){
        this.lives = lives;
        setHearts(lives);
    }

    private void setHearts(int lives){
        StringBuilder heartsBuilder = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            heartsBuilder.append("♥");
        }
        String heartsString = heartsBuilder.toString();
        hearts.setText(heartsString);
        columnSpacer2.setMinWidth(116 - 24*lives); // offset the hearts text width
    }

    private void setScoreText(int score){
        scoreText.setText("Score: " + score);
    }

    private void setLevelText(int level){
        levelText.setText("Level: " + this.level);
    }

    public void setScore (int score){
        this.score = this.score + this.level;
        setScoreText(this.score);
    }

    public void setLevel(int level){
        this.level = level;
        setLevelText(this.level);
    }

    public VBox getUIMenu(){
        return this.UIMenu;
    }

    public int getLives(){
        return this.lives;
    }

    public int getScore(){
        return this.score;
    }

    public int getLevel(){
        return this.level;
    }
}


