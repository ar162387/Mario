package minigames.client.geowars.rendering;

import minigames.client.geowars.gameobjects.*;

import java.awt.*;

public class WallDrawing extends Drawing {

  public WallDrawing(GameObject parent) {
    super(parent);
    defaultColor = Color.WHITE;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    if (parent == null) {
      return;
    }

    // Set the scale
    int width = (int) (parent.getWidth() * scale);
    int height = (int) (parent.getHeight() * scale);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    g.setColor(customColor != null ? customColor : defaultColor);

    // Draw the wall
    g.fillRect(x - width / 2, y - height / 2, width, height);
  }

}
