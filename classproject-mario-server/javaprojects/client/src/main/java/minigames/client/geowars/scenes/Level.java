package minigames.client.geowars.scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.geowars.*;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.ui.*;
import minigames.client.geowars.util.*;

import java.awt.Color;
import java.util.ArrayList;

/**
 * The {@code Level} class represents a playable level in the GeoWars game.
 * It defines the starting position of the player, the layout of walls, and
 * common UI elements
 * like score, timer, and pause functionality. This abstract class is extended
 * by specific level types.
 */
public abstract class Level extends Scene {

  // Level Data
  protected Vector2D playerStartPosition;
  protected int playerStartingLives = 0;
  protected ArrayList<Wall> walls;

  protected int borderThickness = 15;
  protected int borderMargin = 50 - borderThickness;

  // The top-left and bottom-right corners of the playable area
  protected Vector2D playableAreaTL;
  protected Vector2D playableAreaBR;

  // Common UI Elements
  private Text score;
  private Text timer;
  private Button pauseButton;
  private Panel pausePanel;
  private Panel gameOverPanel;
  private Panel optionsPanel;
  private Text statsText;
  private ArrayList<Image> playerLives;

  /**
   * Constructor for the {@code Level} class.
   * Initialises the playable area and prepares the list of walls for the level.
   * 
   * @param engine     The GeoWars game engine.
   * @param playerName The name of the player.
   */
  public Level(GeoWars engine, String playerName) {
    super(engine);
    this.walls = new ArrayList<>();
    this.playableAreaTL = new Vector2D(borderMargin + borderThickness, borderMargin + borderThickness);
    this.playableAreaBR = new Vector2D(GeoWars.SCREEN_WIDTH - borderMargin - borderThickness,
        GeoWars.SCREEN_HEIGHT - borderMargin - borderThickness);

    this.playerLives = new ArrayList<>();
  }

  /**
   * Retrieves the list of walls in the level.
   * 
   * @return A list of {@code Wall} objects in the level.
   */
  public ArrayList<Wall> getWalls() {
    return walls;
  }

  /**
   * Retrieves the player's starting position in the level.
   * 
   * @return The {@code Vector2D} representing the player's starting position.
   */
  public Vector2D getPlayerStartPosition() {
    return playerStartPosition;
  }

  /**
   * Retrieves the number of lives the player starts with in the level.
   * 
   * @return The number of lives the player starts with.
   */
  public int getPlayerStartingLives() {
    return playerStartingLives;
  }

  /**
   * Retrieves the playable area of the level.
   * 
   * @return An array containing the top-left and bottom-right corners of the
   *         playable area.
   */
  public Vector2D[] getPlayableArea() {
    return new Vector2D[] { playableAreaTL, playableAreaBR };
  }

  /**
   * Adds a wall to the level.
   * 
   * @param wall The {@code Wall} object to add to the level.
   */
  public void addWall(Wall wall) {
    walls.add(wall);
  }

  /**
   * Sets the player's starting position for the level.
   * 
   * @param playerStartPosition The {@code Vector2D} representing the player's
   *                            starting position.
   */
  public void setPlayerStartPosition(Vector2D playerStartPosition) {
    this.playerStartPosition = playerStartPosition;
  }

  /**
   * Sets the score displayed on the UI during the level.
   * 
   * @param score The player's score.
   */
  public void setScore(int score) {
    String scoreString = String.format("Score: %,d", score);
    this.score.setContent(scoreString);
  }

  /**
   * Sets the timer displayed on the UI during the level.
   * 
   * @param time The time of the level in seconds.
   */
  public void setTimer(double time) {
    timer.setContent(DeltaTime.levelTimeToString(time));
  }

  public void setPlayerLives(int lives) {
    for (Image life : playerLives) {
      life.destroy();
    }
    playerLives.clear();

    for (int i = 0; i < lives; i++) {
      Image life = new Image(engine, new Vector2D(50 + i * 35, GeoWars.SCREEN_HEIGHT - 18), 1.2, -Math.PI / 2,
          Drawing.PLAYER);
      playerLives.add(life);
      addUIElement(life);
    }
  }

  /**
   * Loads the level and its elements, including score, timer, pause button, pause
   * panel, options panel, and game over panel.
   * By default, every level has these UI elements and a generated border of
   * walls.
   */
  @Override
  public void load() {
    engine.setGameState(1);

    // Score
    addUIElement(generateScoreText());

    // Timer
    addUIElement(generateTimerText());

    // Pause Button
    addUIElement(generatePauseButton());

    // Pause Panel
    addUIElement(generatePausePanel());

    // Options Panel
    addUIElement(generateOptionsPanel());

    // Game Over Panel
    addUIElement(generateGameOverPanel());

    // Walls
    generateLevelBorder();
  }

  /**
   * Cleans up the level by destroying walls and resetting player position.
   * This method ensures that all generic level elements are properly removed.
   */
  @Override
  public void cleanup() {
    super.cleanup();
    for (Wall wall : walls) {
      wall.destroy();
    }
    walls.clear();
    playerStartPosition = null;
  }

  public void setPause(boolean pause) {
    if (pause) {
      pauseButton.disable();
      pausePanel.enable();
    } else {
      pauseButton.enable();
      pausePanel.disable();
    }
  }

  public void gameOver() {
    pauseButton.disable();
    pausePanel.disable();
    statsText.setContent(score.getContent() + "   -   Time: " + timer.getContent());
    timer.disable();
    score.disable();
    gameOverPanel.enable();
  }

  /**
   * Generates the text element for displaying the score.
   * 
   * @return A {@code Text} object representing the score display.
   */
  private Text generateScoreText() {
    score = new Text(engine, new Vector2D(10, 15));
    score.setContent("Score: 0");
    score.setAlignment(Text.ALIGN_LEFT);
    return score;
  }

  /**
   * Generates the text element for displaying the timer.
   * 
   * @return A {@code Text} object representing the timer display.
   */
  private Text generateTimerText() {
    timer = new Text(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2 - 50, 15));
    timer.setContent("0");
    timer.setAlignment(Text.ALIGN_LEFT);
    return timer;
  }

  /**
   * Generates the pause button that pauses the game when clicked.
   * 
   * @return A {@code Button} object representing the pause button.
   */
  private Button generatePauseButton() {
    pauseButton = new Button(engine, new Vector2D(GeoWars.SCREEN_WIDTH - 17, 17), new Vector2D(30, 30));
    pauseButton.setBorder(2);

    Image pauseIcon = new Image(engine, new Vector2D(0, 0), true, 1, 0, Drawing.PAUSE);
    pauseButton.addElement(pauseIcon, new Vector2D(0, 0));

    // Button Actions
    pauseButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        gm.pauseLevel(true);
      }
    });

    return pauseButton;
  }

  /**
   * Generates the pause panel that appears when the game is paused.
   * Contains buttons to resume, access options, or quit the game.
   * 
   * @return A {@code Panel} object representing the pause panel.
   */
  private Panel generatePausePanel() {
    pausePanel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, GeoWars.SCREEN_HEIGHT / 2),
        new Vector2D(400, 500));

    Color greyColor = Drawing.OPTIONS_COLOR;
    Color redColor = Drawing.QUIT_COLOR;
    Color resumeColor = Drawing.START_COLOR;

    // Pause Title
    Text pauseTitle = new Text(engine, new Vector2D(0, 0), true);
    pausePanel.addElement(pauseTitle, new Vector2D(0, -200));
    pauseTitle.setContent("Game Paused");
    pauseTitle.setFontSize(40);
    pauseTitle.setColor(greyColor);

    // Resume Button
    Button resumeButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(200, 75));
    pausePanel.addElement(resumeButton, new Vector2D(0, -75));
    resumeButton.setColor(resumeColor);
    Text resumeText = new Text(engine, new Vector2D(0, 0), true);
    resumeText.setContent("Resume");
    resumeText.setFontSize(30);
    resumeText.setColor(resumeColor);
    resumeButton.addElement(resumeText, new Vector2D(0, -8));

    // Options Button
    Button optionsButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(200, 75));
    pausePanel.addElement(optionsButton, new Vector2D(0, 50));
    optionsButton.setColor(greyColor);
    Text optionsText = new Text(engine, new Vector2D(0, 0), true);
    optionsText.setContent("Options");
    optionsText.setFontSize(30);
    optionsText.setColor(greyColor);
    optionsButton.addElement(optionsText, new Vector2D(0, -8));

    // Quit Button
    Button quitButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(200, 75));
    pausePanel.addElement(quitButton, new Vector2D(0, 175));
    quitButton.setColor(redColor);
    Text quitText = new Text(engine, new Vector2D(0, 0), true);
    quitText.setContent("Quit");
    quitText.setFontSize(30);
    quitText.setColor(redColor);
    quitButton.addElement(quitText, new Vector2D(0, -8));

    // Button Actions
    resumeButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        gm.pauseLevel(false);
      }
    });

    optionsButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        pausePanel.disable();
        optionsPanel.enable();
      }
    });

    quitButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        quitLevel();
      }
    });

    pausePanel.disable();

    return pausePanel;
  }

  /**
   * Generates the options panel that appears when the options button is clicked.
   * Contains sliders for adjusting volume and a button to return to the pause
   * panel.
   * 
   * @return A {@code Panel} object representing the options panel.
   */
  private Panel generateOptionsPanel() {
    optionsPanel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, GeoWars.SCREEN_HEIGHT / 2),
        new Vector2D(400, 500));

    // Pause Title
    Text optionsTitle = new Text(engine, new Vector2D(0, 0), true);
    optionsPanel.addElement(optionsTitle, new Vector2D(0, -200));
    optionsTitle.setContent("Options");
    optionsTitle.setFontSize(40);
    optionsTitle.setColor(Drawing.OPTIONS_COLOR);

    // Music Panel
    Panel musicPanel = new Panel(engine, new Vector2D(0, 0), true, new Vector2D(400, 100));
    musicPanel.setColor(Drawing.TRANSPARENT_COLOR);

    optionsPanel.addElement(musicPanel, new Vector2D(0, -80));

    // Music Title
    Text musicTitle = new Text(engine, new Vector2D(0, 0), true);
    musicPanel.addElement(musicTitle, new Vector2D(-60, -25));
    musicTitle.setContent("Music");
    musicTitle.setFontSize(25);
    musicTitle.setAlignment(Text.ALIGN_CENTER);

    // Music Slider
    Slider musicSlider = new Slider(engine, new Vector2D(0, 0), true, 0.4, 0, 10, 1);
    musicSlider.setValue(gm.getOptions().getMusicVolume());
    musicPanel.addElement(musicSlider, new Vector2D(0, 15));

    // Music Buttons
    // Plus Button
    Button musicPlusButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(40, 40));
    musicPanel.addElement(musicPlusButton, new Vector2D(123, 14));
    musicPlusButton.setColor(Drawing.TRANSPARENT_COLOR);

    Image musicPlusIcon = new Image(engine, new Vector2D(0, 0), true, 0.5, 0, Drawing.PLUS);
    musicPlusButton.addElement(musicPlusIcon, new Vector2D(0, 0));

    musicPlusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        musicSlider.setValue(gm.getOptions().getMusicVolume() + 1);
        gm.getOptions().setMusicVolume(musicSlider.getValue());
      }
    });

    // Minus Button
    Button musicMinusButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(40, 40));
    musicPanel.addElement(musicMinusButton, new Vector2D(-125, 14));
    musicMinusButton.setColor(Drawing.TRANSPARENT_COLOR);

    Image musicMinusIcon = new Image(engine, new Vector2D(0, 0), true, 0.6, 0, Drawing.MINUS);
    musicMinusButton.addElement(musicMinusIcon, new Vector2D(0, 0));

    musicMinusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        musicSlider.setValue(gm.getOptions().getMusicVolume() - 1);
        gm.getOptions().setMusicVolume(musicSlider.getValue());
      }
    });

    // Mute Button
    Button musicMuteButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(40, 40));
    musicPanel.addElement(musicMuteButton, new Vector2D(10, -18));
    musicMuteButton.setColor(Drawing.TRANSPARENT_COLOR);

    Image musicMuteIcon = new Image(engine, new Vector2D(0, 0), true, 0.5, 0, Drawing.MUTE);
    musicMuteButton.addElement(musicMuteIcon, new Vector2D(0, 0));
    musicMuteIcon.setColor(gm.getOptions().isMusicMuted() ? Drawing.QUIT_COLOR : Drawing.START_COLOR);

    musicMuteButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        gm.getOptions().setMusicMuted(!gm.getOptions().isMusicMuted());
        musicMuteIcon.setColor(gm.getOptions().isMusicMuted() ? Drawing.QUIT_COLOR : Drawing.START_COLOR);
      }
    });

    // SFX Panel
    Panel sfxPanel = new Panel(engine, new Vector2D(0, 0), true, new Vector2D(400, 100));
    sfxPanel.setColor(Drawing.TRANSPARENT_COLOR);

    optionsPanel.addElement(sfxPanel, new Vector2D(0, 30));

    // SFX Title
    Text sfxTitle = new Text(engine, new Vector2D(0, 0), true);
    sfxPanel.addElement(sfxTitle, new Vector2D(-60, -25));
    sfxTitle.setContent("Sound");
    sfxTitle.setFontSize(25);
    sfxTitle.setAlignment(Text.ALIGN_CENTER);

    // SFX Slider
    Slider sfxSlider = new Slider(engine, new Vector2D(0, 0), true, 0.4, 0, 10, 1);
    sfxSlider.setValue(gm.getOptions().getSfxVolume());
    sfxPanel.addElement(sfxSlider, new Vector2D(0, 15));

    // SFX Buttons
    // Plus Button
    Button sfxPlusButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(40, 40));
    sfxPanel.addElement(sfxPlusButton, new Vector2D(123, 14));
    sfxPlusButton.setColor(Drawing.TRANSPARENT_COLOR);

    Image sfxPlusIcon = new Image(engine, new Vector2D(0, 0), true, 0.5, 0, Drawing.PLUS);
    sfxPlusButton.addElement(sfxPlusIcon, new Vector2D(0, 0));

    sfxPlusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        sfxSlider.setValue(gm.getOptions().getSfxVolume() + 1);
        gm.getOptions().setSfxVolume(sfxSlider.getValue());
      }
    });

    // Minus Button
    Button sfxMinusButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(40, 40));
    sfxPanel.addElement(sfxMinusButton, new Vector2D(-125, 14));
    sfxMinusButton.setColor(Drawing.TRANSPARENT_COLOR);

    Image sfxMinusIcon = new Image(engine, new Vector2D(0, 0), true, 0.6, 0, Drawing.MINUS);
    sfxMinusButton.addElement(sfxMinusIcon, new Vector2D(0, 0));

    sfxMinusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        sfxSlider.setValue(gm.getOptions().getSfxVolume() - 1);
        gm.getOptions().setSfxVolume(sfxSlider.getValue());
      }
    });

    // Mute Button
    Button sfxMuteButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(40, 40));
    sfxPanel.addElement(sfxMuteButton, new Vector2D(10, -18));
    sfxMuteButton.setColor(Drawing.TRANSPARENT_COLOR);

    Image sfxMuteIcon = new Image(engine, new Vector2D(0, 0), true, 0.5, 0, Drawing.MUTE);
    sfxMuteButton.addElement(sfxMuteIcon, new Vector2D(0, 0));
    sfxMuteIcon.setColor(gm.getOptions().isSfxMuted() ? Drawing.QUIT_COLOR : Drawing.START_COLOR);

    sfxMuteButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        gm.getOptions().setSfxMuted(!gm.getOptions().isSfxMuted());
        sfxMuteIcon.setColor(gm.getOptions().isSfxMuted() ? Drawing.QUIT_COLOR : Drawing.START_COLOR);
      }
    });

    // Back Button
    Button backButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(50, 50));
    optionsPanel.addElement(backButton, new Vector2D(-160, 215));
    backButton.setColor(Drawing.TRANSPARENT_COLOR);

    Image backIcon = new Image(engine, new Vector2D(0, 0), true, 1, 0, Drawing.ARROW);
    backButton.addElement(backIcon, new Vector2D(0, 0));
    backIcon.setColor(Drawing.QUIT_COLOR);

    backButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        pausePanel.enable();
        optionsPanel.disable();
      }
    });

    optionsPanel.disable();

    return optionsPanel;
  }

  /**
   * Generates the game over panel that appears when the player dies.
   * Contains buttons to retry the level or return to the main menu.
   * 
   * @return A {@code Panel} object representing the game over panel.
   */
  private Panel generateGameOverPanel() {
    gameOverPanel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, GeoWars.SCREEN_HEIGHT / 2),
        new Vector2D(GeoWars.SCREEN_WIDTH, GeoWars.SCREEN_HEIGHT));
    gameOverPanel.setColor(Color.BLACK);

    Color redColor = Drawing.QUIT_COLOR;
    Color retryColor = Drawing.START_COLOR;

    // Game Over Title
    Text gameOverTitle = new Text(engine, new Vector2D(0, 0), true);
    gameOverPanel.addElement(gameOverTitle, new Vector2D(0, -200));
    gameOverTitle.setContent("Game Over");
    gameOverTitle.setColor(Drawing.TITLE_COLOR);
    gameOverTitle.setFontSize(120);

    // Stats Text
    statsText = new Text(engine, new Vector2D(0, 0), true);
    gameOverPanel.addElement(statsText, new Vector2D(0, -50));
    statsText.setContent("Score: 0 - Time: 0");
    statsText.setFontSize(40);

    // Retry Button
    Button retryButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(200, 75));
    gameOverPanel.addElement(retryButton, new Vector2D(0, 75));
    retryButton.setColor(retryColor);
    Text retryText = new Text(engine, new Vector2D(0, 0), true);
    retryText.setContent("Retry");
    retryText.setFontSize(30);
    retryText.setColor(retryColor);
    retryButton.addElement(retryText, new Vector2D(0, -8));

    // Main Menu Button
    Button mainMenuButton = new Button(engine, new Vector2D(0, 0), true, new Vector2D(200, 75));
    gameOverPanel.addElement(mainMenuButton, new Vector2D(0, 200));
    mainMenuButton.setColor(redColor);
    Text mainMenuText = new Text(engine, new Vector2D(0, 0), true);
    mainMenuText.setContent("Quit");
    mainMenuText.setFontSize(30);
    mainMenuText.setColor(redColor);
    mainMenuButton.addElement(mainMenuText, new Vector2D(0, -8));

    // Button Actions
    retryButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        gm.endLevel();
        sm.reloadScene();
      }
    });

    mainMenuButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        quitLevel();
      }
    });

    gameOverPanel.disable();

    return gameOverPanel;
  }

  /**
   * Generates the level's border by adding walls around the playable area.
   */
  private void generateLevelBorder() {
    // Top Wall
    addWall(new Wall(
        engine,
        new Vector2D((double) GeoWars.SCREEN_WIDTH / 2, (double) borderMargin + borderThickness / 2),
        0,
        GeoWars.SCREEN_WIDTH - borderMargin * 2,
        borderThickness));

    // Bottom Wall
    addWall(new Wall(
        engine,
        new Vector2D((double) GeoWars.SCREEN_WIDTH / 2,
            (double) GeoWars.SCREEN_HEIGHT - (borderMargin + borderThickness / 2)),
        0,
        GeoWars.SCREEN_WIDTH - borderMargin * 2,
        borderThickness));

    // Left Wall
    addWall(new Wall(
        engine,
        new Vector2D((double) borderMargin + borderThickness / 2, (double) GeoWars.SCREEN_HEIGHT / 2),
        0,
        borderThickness,
        GeoWars.SCREEN_HEIGHT - borderMargin * 2));

    // Right Wall
    addWall(new Wall(
        engine,
        new Vector2D((double) GeoWars.SCREEN_WIDTH - (borderMargin + borderThickness / 2),
            (double) GeoWars.SCREEN_HEIGHT / 2),
        0,
        borderThickness,
        GeoWars.SCREEN_HEIGHT - borderMargin * 2));
  }

  private void quitLevel() {
    gm.endLevel();
    sm.loadScene(new MainMenu(engine));
  }

  /**
   * Abstract method to get the next spawn event for the level.
   * 
   * @param lastSpawnTime  The time of the last spawn event.
   * @param levelTime      The current time in the level.
   * @param lastWaveNumber The number of the last wave.
   * @param playerDead     {@code true} if the player is dead, otherwise
   *                       {@code false}.
   * @return The next {@code SpawnEvent} for the level.
   */
  public abstract SpawnEvent getNextSpawnEvent(double lastSpawnTime, double levelTime, int lastWaveNumber,
      boolean playerDead);
}