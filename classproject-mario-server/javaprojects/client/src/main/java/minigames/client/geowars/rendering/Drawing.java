package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;

/**
 * A superclass for all common drawings in GeoWars.
 */
public abstract class Drawing {

  // The GameObject that this Drawing is drawing.
  protected GameObject parent;
  protected Color defaultColor;

  // Constants for the colors used in GeoWars.
  // UI Colors
  public static final Color GEOWARS_COLOR = new Color(0, 148, 212); // Soft Blue
  public static final Color TITLE_COLOR = new Color(0, 170, 242); // Brighter Blue
  public static final Color OPTIONS_COLOR = new Color(158, 181, 184); // Grey
  public static final Color QUIT_COLOR = new Color(209, 17, 0); // Darkish Red
  public static final Color START_COLOR = new Color(0, 235, 78); // Bright Green

  // Entity Colors
  public static final Color PLAYER_COLOR = new Color(83, 227, 227); // Bright Cyan
  public static final Color PURPLE_ENEMY_COLOR = new Color(156, 5, 250); // Purple
  public static final Color GREEN_ENEMY_COLOR = new Color(89, 255, 0); // Bright Green
  public static final Color PINK_ENEMY_COLOR = new Color(227, 77, 227); // Pink
  public static final Color LIGHT_BLUE_ENEMY_COLOR = new Color(110, 207, 255); // Sky Blue

  // Other Colors
  public static final Color BACKGROUND_COLOR = new Color(4, 0, 25); // Very Dark Blue-Purple
  public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0); // Transparent

  // Constants for the possible Drawings in GeoWars.
  public static final int PLAYER = 0;
  public static final int WALL = 1;
  public static final int PROJECTILE = 2;
  public static final int PURPLE_ENEMY = 3;
  public static final int GREEN_ENEMY = 4;
  public static final int PINK_ENEMY = 5;
  public static final int SMALL_PINK_ENEMY = 6;
  public static final int LIGHT_BLUE_ENEMY = 7;
  public static final int PLAYER_EXPLOSION = 8;
  public static final int OPTIONS = 9;
  public static final int LEADERBOARD = 10;
  public static final int PAUSE = 11;
  public static final int ARROW = 12;
  public static final int SLIDER = 13;
  public static final int PLUS = 14;
  public static final int MINUS = 15;
  public static final int MUTE = 16;

  /**
   * Constructor for the Drawing class.
   * 
   * @param parent The GameObject that this Drawing is drawing.
   */
  public Drawing(GameObject parent) {
    this.parent = parent;
  }

  /**
   * Abstract method for drawing the Drawing.
   * 
   * @param g           The Graphics object to draw with.
   * @param scale       The scale of the Drawing.
   * @param rotation    The rotation of the Drawing.
   * @param customColor The color to draw the Drawing with.
   */
  public abstract void draw(Graphics g, double scale, double rotation, Color customColor);

  /**
   * Method for getting a {@code Drawing} object based on the given drawing type.
   * 
   * @param drawingType The type of {@code Drawing} to get.
   * @param parent      The {@code GameObject} that the {@code Drawing} will be
   *                    drawing.
   * @return The {@code Drawing} object based on the given drawing type.
   */
  public static Drawing getDrawing(int drawingType, GameObject parent) {
    switch (drawingType) {
      case PLAYER:
        return new PlayerDrawing(parent);
      case WALL:
        return new WallDrawing(parent);
      case PROJECTILE:
        return new ProjectileDrawing(parent);
      case PURPLE_ENEMY:
        return new PurpleEnemyDrawing(parent);
      case GREEN_ENEMY:
        return new GreenEnemyDrawing(parent);
      case PINK_ENEMY:
        return new PinkEnemyDrawing(parent);
      case SMALL_PINK_ENEMY:
        return new SmallPinkEnemyDrawing(parent);
      case LIGHT_BLUE_ENEMY:
        return new LightBlueEnemyDrawing(parent);
      case PLAYER_EXPLOSION:
        return new PlayerExplosionDrawing(parent);
      case OPTIONS:
        return new OptionsDrawing(parent);
      case LEADERBOARD:
        return new LeaderboardDrawing(parent);
      case PAUSE:
        return new PauseDrawing(parent);
      case ARROW:
        return new ArrowDrawing(parent);
      case SLIDER:
        return new SliderDrawing(parent);
      case PLUS:
        return new PlusDrawing(parent);
      case MINUS:
        return new MinusDrawing(parent);
      case MUTE:
        return new MuteDrawing(parent);
      default:
        return null;
    }
  }

  /**
   * Clamps a given rotation value between the legal range of -PI to PI.
   * 
   * @param rotation The rotation value to clamp.
   * @return The clamped rotation value.
   */
  public static double clampRotation(double rotation) {
    while (rotation > Math.PI) {
      rotation -= 2 * Math.PI;
    }
    while (rotation < -Math.PI) {
      rotation += 2 * Math.PI;
    }
    return rotation;
  }

}
