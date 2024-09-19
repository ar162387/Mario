package minigames.client.geowars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.geowars.scenes.*;

/**
 * Scene Manager for GeoWars.
 * Is responsible for loading and cleaning up Scenes that are used in the game.
 */
public class SceneManager {

  // Misc util.
  private static final Logger logger = LogManager.getLogger(GeoWarsRenderer.class);

  // Singleton implementation
  private static SceneManager instance;

  // Game Engine references
  private GeoWars engine;

  // Scene Manager Data
  private Scene currentScene;

  // Private constructor to enforce singleton pattern.
  private SceneManager(GeoWars engine) {
    this.engine = engine;
    this.currentScene = null;
  }

  public static boolean isInstanceNull() {
    return instance == null;
  }

  // Singleton getInstance method.
  public static SceneManager getInstance(GeoWars engine) {
    if (instance == null) {
      instance = new SceneManager(engine);
    }
    return instance;
  }

  public void loadScene(Scene scene) {
    // Clean up the current scene
    engine.sceneChanged();
    cleanScene();
    currentScene = scene;

    // Send new Scene to the renderer
    if (!GeoWarsRenderer.isInstanceNull()) {
      GeoWarsRenderer.getInstance(null).setNewScene(currentScene);
    } else {
      logger.error("GeoWarsRenderer instance is null. Cannot set new scene.");
    }

    currentScene.load();

    // If new scene is a level, have the GameManager start the level.
    if (currentScene instanceof Level) {
      if (!GameManager.isInstanceNull()) {
        GameManager.getInstance(null, null).startLevel((Level) currentScene);
      } else {
        logger.error("GameManager instance is null. Cannot start level.");
      }
    }
  }

  public void reloadScene() {
    if (currentScene != null) {
      loadScene(currentScene.recreate());
    }
  }

  public void cleanScene() {
    if (currentScene != null) {
      currentScene.cleanup();
      currentScene = null;
    }
    engine.cleanEngine();
  }

  public void cleanup() {
    cleanScene();
    instance = null;
  }
}
