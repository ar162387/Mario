package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;

public class MinusDrawing extends Drawing {

  private int defaultSize = 50;

  public MinusDrawing(GameObject parent) {
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
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();
    int size = (int) (defaultSize * scale);

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;

    // Outline first
    g2d.setColor(color.darker());
    g2d.setStroke(new BasicStroke((int) (6 * scale)));

    // Horizontal Rectangle
    g2d.drawRect(x - size / 2, y - size / 8, size, size / 4);

    // Fill in the minus
    g2d.setColor(color);
    g2d.fillRect(x - size / 2, y - size / 8, size, size / 4);
  }

}
