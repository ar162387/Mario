package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.Player;

public class PlayerDrawing extends Drawing {

  // Default values for the PlayerDrawing.
  private int defaultSize = (int) (2 * Player.PLAYER_SIZE) / 3;
  // Size of the points arrays.
  private int nPoints = 8;

  /**
   * Constructor for the PlayerDrawing class.
   * 
   * @param parent The GameObject that this Drawing is drawing.
   */
  public PlayerDrawing(GameObject parent) {
    super(parent);
    this.defaultColor = Drawing.PLAYER_COLOR;
  }

  /**
   * Method for drawing the PlayerDrawing.
   * 
   * @param g           The Graphics object to draw with.
   * @param scale       The scale of the Drawing.
   * @param rotation    The rotation of the Drawing.
   * @param customColor The color to draw the Drawing with.
   */
  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    if (parent == null) {
      return;
    }

    // Set the scale
    int rOuter = (int) (defaultSize * scale);
    int rInner = (int) (defaultSize * scale * 0.5);
    int x = (int) parent.getPosition().x;
    int y = (int) parent.getPosition().y;

    // Set the color
    Color color = defaultColor;
    if (customColor != null) {
      color = customColor;
    }

    g.setColor(color);

    // Set the points.
    // x = r * cos(theta)
    int[] xPoints = {
        (int) (x + rOuter * Math.cos(clampRotation(rotation + Math.PI))),
        (int) (x + rOuter * Math.cos(clampRotation(rotation + 4 * Math.PI / 3))),
        (int) (x + rOuter * Math.cos(clampRotation(rotation + 11 * Math.PI / 6))),
        (int) (x + rInner * Math.cos(clampRotation(rotation + 4 * Math.PI / 3))),
        (int) (x + rInner * Math.cos(clampRotation(rotation + Math.PI))),
        (int) (x + rInner * Math.cos(clampRotation(rotation + 2 * Math.PI / 3))),
        (int) (x + rOuter * Math.cos(clampRotation(rotation + Math.PI / 6))),
        (int) (x + rOuter * Math.cos(clampRotation(rotation + 2 * Math.PI / 3))),
    };

    // y = r * sin(theta)
    int[] yPoints = {
        (int) (y + rOuter * Math.sin(clampRotation(rotation + Math.PI))),
        (int) (y + rOuter * Math.sin(clampRotation(rotation + 4 * Math.PI / 3))),
        (int) (y + rOuter * Math.sin(clampRotation(rotation + 11 * Math.PI / 6))),
        (int) (y + rInner * Math.sin(clampRotation(rotation + 4 * Math.PI / 3))),
        (int) (y + rInner * Math.sin(clampRotation(rotation + Math.PI))),
        (int) (y + rInner * Math.sin(clampRotation(rotation + 2 * Math.PI / 3))),
        (int) (y + rOuter * Math.sin(clampRotation(rotation + Math.PI / 6))),
        (int) (y + rOuter * Math.sin(clampRotation(rotation + 2 * Math.PI / 3))),
    };

    g.fillPolygon(xPoints, yPoints, nPoints);

  }

}
