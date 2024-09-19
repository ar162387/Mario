package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;

public class MuteDrawing extends Drawing {

  private int defaultSize = 50;

  public MuteDrawing(GameObject parent) {
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

    // Drawing consists of 4 parts:
    // 1. Speaker cone: A triangle facing right.
    // 2. Speaker body: A rectangle covering the back of the triangle.
    // 3. Speaker mute: A semi-circle extruding from the front of the triangle.
    // 4. Sound wave: A hollow semi-circle extruding from the front of the triangle.

    // Outlines first.
    g2d.setColor(color.darker());
    g2d.setStroke(new BasicStroke((int) (6 * scale)));

    // Speaker cone
    int nPoints = 3;
    int[] xPoints = { x - size / 2 + size / 12, x + size / 6, x + size / 6 };
    int[] yPoints = { y, y - size / 2, y + size / 2 };
    g2d.drawPolygon(xPoints, yPoints, nPoints);

    // Speaker body
    g2d.drawRect(x - size / 2, y - size / 6, size / 3, size / 3);

    // Speaker mute
    g2d.drawOval(x - size / 8 + size / 8, y - size / 8, size / 4, size / 4);

    // Sound wave
    g2d.drawOval(x - size / 3 + size / 8, y - size / 3, 2 * size / 3, 2 * size / 3);

    // Fill in the drawing.
    g2d.setColor(color);
    g2d.fillPolygon(xPoints, yPoints, nPoints);
    g2d.fillRect(x - size / 2, y - size / 6, size / 3, size / 3);
    g2d.fillOval(x - size / 8 + size / 8, y - size / 8, size / 4, size / 4);
  }
}
