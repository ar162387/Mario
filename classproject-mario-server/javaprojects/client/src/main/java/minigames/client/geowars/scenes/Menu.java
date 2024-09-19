package minigames.client.geowars.scenes;

import minigames.client.geowars.*;

/**
 * Menu class for GeoWars.
 * A Menu is a scene that only has UI elements, and no gameplay elements.
 * 
 * Since all menus are quite different, there is no real implementation here.
 */
public abstract class Menu extends Scene {

  protected int cornerButtonOffset = 43;
  protected int cornerButtonSize = 50;

  /**
   * Constructor for the Menu class.
   * 
   * @param engine The GeoWars engine that the menu is running on.
   */
  public Menu(GeoWars engine) {
    super(engine);
  }

  /**
   * Load the menu.
   * This method should contain the instructions to build the menu.
   */
  @Override
  public void load() {
    // Updates the game state, this changes how InputManager behaves.
    engine.setGameState(0);
  }
}
