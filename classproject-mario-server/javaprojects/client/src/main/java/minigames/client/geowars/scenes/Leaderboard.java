package minigames.client.geowars.scenes;

import java.util.ArrayList;

import minigames.client.geowars.*;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.ui.*;
import minigames.client.geowars.util.DeltaTime;
import minigames.client.geowars.util.Vector2D;

/**
 * Leaderboard scene for GeoWars.
 * Displays the top 10 scores in the game.
 * 
 * This scene is accessible from the main menu.
 * 
 * Right now, this is not dynamic at all, though with a second level type, it
 * should be, and should swap between displaying the top 10 scores for each
 * level on a button press.
 */
public class Leaderboard extends Menu {

  private final int displayScores = 10;
  private final int scoreFontSize = 30;

  /**
   * Constructor for the Leaderboard scene.
   * 
   * @param engine The GeoWars engine that the scene is running on.
   */
  public Leaderboard(GeoWars engine) {
    super(engine);
  }

  /**
   * Load the Leaderboard scene.
   * This method contains the instructions to build the leaderboard.
   * The leaderboard contains:
   * - A Title text that says "Leaderboard"
   * - A list of the top 10 scores
   * - A 'Back to Menu' button
   */
  @Override
  public void load() {
    super.load();

    // Title
    this.addUIElement(generateTitleText());

    // Score list
    generateScoreList();

    // Back button
    this.addUIElement(generateBackButton());
  }

  @Override
  public Scene recreate() {
    return new Leaderboard(engine);
  }

  private Text generateTitleText() {
    Text title = new Text(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, 70));
    title.setContent("Leaderboard");
    title.setFontSize(70);
    title.setAlignment(Text.ALIGN_CENTER);
    title.setColor(Drawing.TITLE_COLOR);

    return title;
  }

  private void generateScoreList() {
    ArrayList<GeoWarsScore> scores = gm.getArcadeScores();
    ArrayList<GeoWarsScore> topScores = GeoWarsScore.getTopScores(scores, displayScores);

    if (topScores.size() == 0) {
      Text noScores = new Text(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, 200));
      noScores.setContent("No scores yet!");
      noScores.setFontSize(40);
      noScores.setAlignment(Text.ALIGN_CENTER);

      this.addUIElement(noScores);
    } else {
      int y = 150;

      this.addUIElement(generateScoreTitlePanel(y));

      for (int i = 1; i <= topScores.size(); i++) {

        this.addUIElement(generateScorePanel(y + 20 + i * 50, topScores.get(i - 1)));
      }
    }
  }

  private Panel generateScoreTitlePanel(int y) {
    Panel scorePanel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, y), new Vector2D(1000, 50));
    scorePanel.setColor(Drawing.TRANSPARENT_COLOR);

    Text playerName = new Text(engine, new Vector2D());
    playerName.setContent("Captain");
    playerName.setFontSize(scoreFontSize);
    playerName.setAlignment(Text.ALIGN_LEFT);
    playerName.setColor(Drawing.TITLE_COLOR);

    scorePanel.addElement(playerName, new Vector2D(-400, 0));

    Text scoreText = new Text(engine, new Vector2D());
    scoreText.setContent("Score");
    scoreText.setFontSize(scoreFontSize);
    scoreText.setAlignment(Text.ALIGN_RIGHT);
    scoreText.setColor(Drawing.TITLE_COLOR);

    scorePanel.addElement(scoreText, new Vector2D(350, 0));

    Text timeText = new Text(engine, new Vector2D());
    timeText.setContent("Time");
    timeText.setFontSize(scoreFontSize);
    timeText.setAlignment(Text.ALIGN_LEFT);
    timeText.setColor(Drawing.TITLE_COLOR);

    scorePanel.addElement(timeText, new Vector2D(375, 0));

    return scorePanel;
  }

  private Panel generateScorePanel(int y, GeoWarsScore score) {
    Panel scorePanel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, y), new Vector2D(1000, 50));
    scorePanel.setColor(Drawing.TRANSPARENT_COLOR);

    Text playerName = new Text(engine, new Vector2D());
    playerName.setContent(score.getPlayerName());
    playerName.setFontSize(scoreFontSize);
    playerName.setAlignment(Text.ALIGN_LEFT);

    scorePanel.addElement(playerName, new Vector2D(-400, 0));

    Text scoreText = new Text(engine, new Vector2D());
    scoreText.setContent(Integer.toString(score.getScore()));
    scoreText.setFontSize(scoreFontSize);
    scoreText.setAlignment(Text.ALIGN_RIGHT);

    scorePanel.addElement(scoreText, new Vector2D(350, 0));

    Text timeText = new Text(engine, new Vector2D());
    timeText.setContent(DeltaTime.levelTimeToString(score.getTime()));
    timeText.setFontSize(scoreFontSize);
    timeText.setAlignment(Text.ALIGN_LEFT);

    scorePanel.addElement(timeText, new Vector2D(375, 0));

    return scorePanel;
  }

  private Button generateBackButton() {
    Button btm = new Button(engine, new Vector2D(cornerButtonOffset, cornerButtonOffset),
        new Vector2D(cornerButtonSize, cornerButtonSize));
    btm.setColor(Drawing.TRANSPARENT_COLOR);

    btm.setAction(new ButtonAction() {
      @Override
      public void execute() {
        sm.loadScene(new MainMenu(engine));
      }
    });

    Image btmImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.ARROW);
    btmImage.setColor(Drawing.QUIT_COLOR);

    btm.addElement(btmImage, new Vector2D());

    return btm;
  }

}
