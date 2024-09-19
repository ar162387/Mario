package minigames.client.geowars.scenes;

import java.awt.Color;

import minigames.client.geowars.*;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.ui.*;
import minigames.client.geowars.util.Sound;
import minigames.client.geowars.util.Vector2D;

/**
 * This class represents the main menu of the game.
 * It contains the instructions to build the main menu.
 */
public class MainMenu extends Menu {

  /**
   * Constructor for MainMenu.
   * 
   * @param engine The GeoWars client.
   */
  public MainMenu(GeoWars engine) {
    super(engine);
  }

  /**
   * Load the main menu.
   * This method contains the instructions to build the main menu.
   * The main menu should contain the Title, the player's name, and 4 buttons:
   * - Play
   * - Options
   * - Leaderboard
   * - Quit
   */
  @Override
  public void load() {
    // Call the superclass load method
    super.load();

    // Title
    this.addUIElement(generateTitleText());

    // Player Name
    this.addUIElement(generatePlayerNameText());

    // Buttons
    // Start Button
    this.addUIElement(generateStartButton());

    // Options
    this.addUIElement(generateOptionsButton());

    // Leaderboard
    this.addUIElement(generateLeaderboardButton());

    // Quit
    this.addUIElement(generateQuitButton());

    // Starts the background music when level is loaded.
    Sound.getInstance().playMusic(Sound.Type.MENU);
  }

  /**
   * Recreate the MainMenu.
   * 
   * @return A new MainMenu object.
   */
  @Override
  public Scene recreate() {
    return new MainMenu(engine);
  }

  private Text generateTitleText() {
    Color titleColor = Drawing.TITLE_COLOR;

    Text title = new Text(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, GeoWars.SCREEN_HEIGHT / 2 - 100));
    title.setAlignment(Text.ALIGN_CENTER);
    title.setContent("GeoWars");
    title.setFontSize(120);
    title.setColor(titleColor);

    return title;
  }

  private Text generatePlayerNameText() {
    Text playerName = new Text(engine, new Vector2D(80, GeoWars.SCREEN_HEIGHT - 50));
    playerName.setContent("Captain - " + gm.getPlayerName());
    playerName.setAlignment(Text.ALIGN_LEFT);
    playerName.setFontSize(30);

    return playerName;
  }

  private Button generateStartButton() {
    Color startColor = Drawing.START_COLOR;

    Button startButton = new Button(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, GeoWars.SCREEN_HEIGHT / 2 + 90),
        new Vector2D(350, 90));
    startButton.setColor(startColor);

    startButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        sm.loadScene(new ArcadeLevel(engine));
      }
    });

    Text startText = new Text(engine, new Vector2D(0, 0), true);
    startText.setContent("Start");
    startText.setAlignment(Text.ALIGN_CENTER);
    startText.setFontSize(60);
    startText.setColor(startColor);

    startButton.addElement(startText, new Vector2D(0, -15));

    return startButton;
  }

  private Button generateOptionsButton() {
    Color optionsColor = Drawing.OPTIONS_COLOR;

    Button optionsButton = new Button(engine, new Vector2D(cornerButtonOffset, cornerButtonOffset),
        new Vector2D(cornerButtonSize, cornerButtonSize));
    optionsButton.setColor(Drawing.TRANSPARENT_COLOR);

    optionsButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        sm.loadScene(new OptionsMenu(engine));
      }
    });

    Image optionsImage = new Image(engine, new Vector2D(0, 0), true, 1, -Math.PI / 2, Drawing.OPTIONS);
    optionsImage.setColor(optionsColor);

    optionsButton.addElement(optionsImage, new Vector2D(0, 0));

    return optionsButton;
  }

  private Button generateLeaderboardButton() {
    Button leaderboardButton = new Button(engine,
        new Vector2D(cornerButtonOffset, GeoWars.SCREEN_HEIGHT - cornerButtonOffset),
        new Vector2D(cornerButtonSize, cornerButtonSize));
    leaderboardButton.setColor(Drawing.TRANSPARENT_COLOR);

    leaderboardButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        sm.loadScene(new Leaderboard(engine));
      }
    });

    Image leaderboardImage = new Image(engine, new Vector2D(0, 0), true, 1, 0, Drawing.LEADERBOARD);

    leaderboardButton.addElement(leaderboardImage, new Vector2D(0, -0));

    return leaderboardButton;
  }

  private Button generateQuitButton() {
    int quitButtonWidth = 90;
    int quitButtonHeight = 50;
    Color quitColor = Drawing.QUIT_COLOR;

    Button quitButton = new Button(engine,
        new Vector2D(GeoWars.SCREEN_WIDTH - cornerButtonOffset - (quitButtonWidth - cornerButtonSize) / 2,
            GeoWars.SCREEN_HEIGHT - cornerButtonOffset),
        new Vector2D(quitButtonWidth, quitButtonHeight));
    quitButton.setColor(quitColor);

    quitButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        gm.quitGame();
      }
    });

    Text quitText = new Text(engine, new Vector2D(0, 0), true);
    quitText.setContent("Quit");
    quitText.setAlignment(Text.ALIGN_CENTER);
    quitText.setColor(quitColor);

    quitButton.addElement(quitText, new Vector2D(0, -5));

    return quitButton;
  }

}