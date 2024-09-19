package minigames.client.geowars.scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import minigames.client.geowars.GameManager;
import minigames.client.geowars.GeoWars;
import minigames.client.geowars.SceneManager;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.ui.*;

/**
 * Scene class for GeoWars.
 * Abstract class that specific scene types should extend.
 * Scenes contain instructions for what to load to the screen initially.
 */
public abstract class Scene {

  protected GameManager gm;
  protected SceneManager sm;

  protected GeoWars engine;
  protected ArrayList<UIElement> uiElements;

  /**
   * Constructor for the Scene class.
   * 
   * @param engine The GeoWars engine that the scene is running on.
   */
  public Scene(GeoWars engine) {
    this.engine = engine;
    this.uiElements = new ArrayList<UIElement>();

    if (GameManager.isInstanceNull()) {
      System.err.println("GameManager instance is null in Scene constructor.");
    } else {
      gm = GameManager.getInstance(null, null);
    }

    if (SceneManager.isInstanceNull()) {
      System.err.println("SceneManager instance is null in Scene constructor.");
    } else {
      sm = SceneManager.getInstance(null);
    }
  }

  /**
   * Get the engine that the scene is running on.
   * 
   * @return The GeoWars engine that the scene is running on.
   */
  public GeoWars getEngine() {
    return engine;
  }

  /**
   * Get the list of UIElements in the scene.
   * 
   * @return The list of UIElements in the scene.
   */
  public ArrayList<UIElement> getUIElements() {
    return uiElements;
  }

  /**
   * Add a UIElement to the scene.
   * 
   * @param element The UIElement to add to the scene.
   */
  public void addUIElement(UIElement element) {
    uiElements.add(element);
  }

  /**
   * Called by the SceneManager once the scene has been constucted.
   * This method should be used to initialise game objects, load resources, etc.
   * This method should be overwritten by subclasses, as the default
   * implementation does nothing.
   */
  public void load() {

  }

  /**
   * Called by the SceneManager when the scene is being unloaded.
   * This method should be used to clean up any resources that were loaded in the
   * load method.
   * All Scenes have a list of UIElements, so they can be cleaned up by default.
   */
  public void cleanup() {
    for (UIElement element : uiElements) {
      element.destroy();
    }
    uiElements.clear();
  }

  /**
   * Draw the background of the scene.
   * This method should be overwritten by subclasses if they want something other
   * than a simple
   * solid black background to be drawn.
   * 
   * @param g The Graphics object provided by the Game Renderer to draw with.
   */
  public void drawBackground(Graphics g) {
    g.setColor(Drawing.BACKGROUND_COLOR);
    g.fillRect(0, 0, GeoWars.SCREEN_WIDTH, GeoWars.SCREEN_HEIGHT);
  };

  public abstract Scene recreate();
}
