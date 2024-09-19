package minigames.client.geowars.rendering;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.projectiles.Projectile;

import java.awt.*;

public class ProjectileDrawing extends Drawing {

  public ProjectileDrawing(GameObject parent) {
    super(parent);
    defaultColor = Color.YELLOW;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    // Potentially cast the parent.
    if (parent == null) {
      return;
    }

    // Set the scale
    int width = (int) (Projectile.DEFAULT_WIDTH * scale);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;
    g.setColor(color);

    // Draw the projectile
    g.fillOval(x - width / 2, y - width / 2, width, width);
  }

}
