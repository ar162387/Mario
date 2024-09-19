package minigames.client.geowars.rendering;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.enemies.GreenEnemy;

import java.awt.*;

public class GreenEnemyDrawing extends Drawing {

  public GreenEnemyDrawing(GameObject parent) {
    super(parent);
    defaultColor = Drawing.GREEN_ENEMY_COLOR;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    // Potentially cast the parent.
    if (parent == null) {
      return;
    }

    // Set the scale
    int width = (int) (GreenEnemy.DEFAULT_WIDTH * scale);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;

    // Draw the enemy
    // Convert g to Graphics2D for more control
    Graphics2D g2d = (Graphics2D) g;

    // Draw outline rectangle
    g2d.setColor(color);
    g2d.fillRect(x - width / 2, y - width / 2, width, width);

    // Set color to black inner rectangle
    g2d.setColor(Color.BLACK);
    g2d.fillRect(x - width / 2 + 2, y - width / 2 + 2, width - 4,
        width - 4);

    // Set color back to purpel for diagonal lines
    g2d.setColor(color);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawLine(x - width / 2 + 3, y - width / 2 + 3, x + width / 2 - 3, y + width / 2 - 3);
    g2d.drawLine(x - width / 2 + 3, y + width / 2 - 3, x + width / 2 - 3, y - width / 2 + 3);
  }

}
