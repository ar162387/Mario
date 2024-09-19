package minigames.client.geowars.rendering;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.enemies.LightBlueEnemy;

import java.awt.*;

public class LightBlueEnemyDrawing extends Drawing {

  private int nPoints = 4;

  public LightBlueEnemyDrawing(GameObject parent) {
    super(parent);
    defaultColor = Drawing.LIGHT_BLUE_ENEMY_COLOR;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    // Potentially cast the parent.
    if (parent == null) {
      return;
    }

    // Set the scale
    int width = LightBlueEnemy.DEFAULT_WIDTH;
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;

    // Draw the enemy
    Graphics2D g2d = (Graphics2D) g;
    g.setColor(color);

    // Set the points
    int[] xPoints = {
        (x),
        (x + width / 2),
        (x),
        (x - width / 2)
    };
    int[] yPoints = {
        (y - width / 2),
        (y),
        (y + width / 2),
        (y)
    };

    // Draw the polygon
    g2d.setStroke(new BasicStroke(3));
    g.drawPolygon(xPoints, yPoints, nPoints);
  }

}
