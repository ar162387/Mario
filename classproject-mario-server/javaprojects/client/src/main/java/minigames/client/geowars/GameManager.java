package minigames.client.geowars;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.geowars.scenes.*;
import minigames.client.geowars.util.*;

/**
 * Game Manager for GeoWars.
 * Keeps track of game metadata like user-defined settings, player data, etc.
 * Is responsible for launching sub-managers that handle specific game logic.
 */
public class GameManager {

  // Misc util.
  private static final Logger logger = LogManager.getLogger(GameManager.class);

  // Singleton implementation
  private static GameManager instance;

  // Game Engine references
  private GeoWars engine;

  // Game Data
  private String playerName;
  private GeoWarsOptions options;
  private ArrayList<GeoWarsScore> arcadeScores;

  // Game Managers
  private SceneManager sceneManager;
  private LevelManager levelManager;

  // Private constructor to enforce singleton pattern.
  private GameManager(GeoWars engine, String playerName) {
    this.engine = engine;
    this.playerName = playerName;
    // At the moment we are creating default options every time we launch the game.
    // Ideally these would be saved per user.
    this.options = new GeoWarsOptions();
    this.arcadeScores = new ArrayList<>();
    this.sceneManager = SceneManager.getInstance(engine);
    this.levelManager = null;
  }

  public static boolean isInstanceNull() {
    return instance == null;
  }

  // Singleton getInstance method.
  public static GameManager getInstance(GeoWars engine, String playerName) {
    if (instance == null) {
      instance = new GameManager(engine, playerName);
    }
    return instance;
  }

  public String getPlayerName() {
    return playerName;
  }

  public GeoWarsOptions getOptions() {
    return options;
  }

  public ArrayList<GeoWarsScore> getArcadeScores() {
    return arcadeScores;
  }

  public void addArcadeScore(int score, double time) {
    arcadeScores.add(new GeoWarsScore(score, time, playerName, "Arcade"));
  }

  public void startGame() {
    // Have the Scene Manager load the main menu.
    sceneManager.loadScene(new MainMenu(engine));
  }

  public void startLevel(Scene scene) {

    // If the levelManager currently exists, that is from an old level, so we should
    // clean it up.
    if (!LevelManager.isInstanceNull()) {
      endLevel();
    }

    // Create a new levelManager for the new level and start the level.
    levelManager = LevelManager.getInstance(engine, (Level) scene);
    levelManager.startLevel();

  }

  public void endLevel() {
    // Make sure the game state is 'unpaused'.
    pauseLevel(false);
    // Clean up the levelManager.
    levelManager.cleanup();
    levelManager = null;

    System.out.println(arcadeScores);
  }

  public void updateLevel() {
    if (levelManager != null) {
      levelManager.updateLevel();
    }
  }

  public void pauseLevel(boolean pause) {
    // We only want to pause if we are actually inside a level.
    if (levelManager != null) {
      levelManager.setPause(pause);
      engine.setPause(pause);
      DeltaTime.setPause(pause);
    }
  }

  /**
   * Clean up the internal components of the game.
   * This is the method that is called by the quitGame button in the main menu.
   */
  public void quitGame() {
    // Stops sounds and releases them.
    Sound.getInstance().stopMusic();
    Sound.getInstance().releaseAllClips();

    // Clean up the levelManager if it exists.
    if (levelManager != null) {
      endLevel();
    }
    // Clean up the sceneManager.
    if (sceneManager != null) {
      sceneManager.cleanup();
      sceneManager = null;
    }
    // Clean up the GameManager.
    instance = null;

    // Close the engine.
    engine.quit();
  }
}