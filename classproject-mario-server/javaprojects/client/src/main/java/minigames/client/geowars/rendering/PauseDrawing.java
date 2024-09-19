package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;

public class PauseDrawing extends Drawing {

  private int defaultWidth = 5;
  private int defaultHeight = 18;

  public PauseDrawing(GameObject parent) {
    super(parent);
    this.defaultColor = Drawing.GEOWARS_COLOR;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    if (parent == null) {
      return;
    }

    Graphics2D g2d = (Graphics2D) g;

    // Set the scale
    int width = (int) (defaultWidth * scale);
    int height = (int) (defaultHeight * scale);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;
    g2d.setColor(color);

    // Draw the image.
    // Left Bar
    g2d.fillRect(x - (3 * width / 2), y - height / 2, width, height);

    // Right Bar
    g2d.fillRect(x + (width / 2), y - height / 2, width, height);

  }

}
