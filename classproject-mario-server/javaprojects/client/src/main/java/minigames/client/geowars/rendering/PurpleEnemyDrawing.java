package minigames.client.geowars.rendering;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.enemies.PurpleEnemy;

import java.awt.*;

public class PurpleEnemyDrawing extends Drawing {

  public PurpleEnemyDrawing(GameObject parent) {
    super(parent);
    defaultColor = Drawing.PURPLE_ENEMY_COLOR;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    // Potentially cast the parent.
    if (parent == null) {
      return;
    }

    // Set the scale
    int width = (int) (PurpleEnemy.DEFAULT_WIDTH * scale);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;

    // Draw purple oval
    g.setColor(color);
    g.fillOval(x - width / 2, y - width / 2, width, width);

    // Draw inner black oval
    g.setColor(Color.BLACK);
    g.fillOval(x - width / 2 + 3, y - width / 2 + 3, width - 6, width - 6);
  }

}
