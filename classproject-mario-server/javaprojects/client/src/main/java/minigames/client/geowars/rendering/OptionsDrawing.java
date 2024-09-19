package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;

public class OptionsDrawing extends Drawing {

  private int defaultSize = 24;

  private int nPoints = 24;

  public OptionsDrawing(GameObject parent) {
    super(parent);
    this.defaultColor = Drawing.OPTIONS_COLOR;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    if (parent == null) {
      return;
    }

    Graphics2D g2d = (Graphics2D) g;

    // Set the scale
    int rOuter = (int) (defaultSize * scale);
    int rInner = 0;
    int rMid = (int) (defaultSize * scale * 0.7);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;
    g2d.setColor(color);

    // Set the points.
    double offset = Math.PI / 12;
    int radius;
    // x = r * cos(theta)
    int[] xPoints = new int[nPoints];
    // y = r * sin(theta)
    int[] yPoints = new int[nPoints];

    for (int i = 0; i < nPoints; i++) {

      int angle = Math.floorDiv(i, 2);

      if (i % 4 == 0 || i % 4 == 3) {
        radius = rInner;
      } else {
        radius = rOuter;
      }

      xPoints[i] = (int) (x + radius * Math.cos(clampRotation(rotation + offset + (angle * Math.PI / 6))));

      yPoints[i] = (int) (y + radius * Math.sin(clampRotation(rotation + offset + (angle * Math.PI / 6))));
    }

    g2d.fillPolygon(xPoints, yPoints, nPoints);
    g2d.fillOval(x - rMid - 1, y - rMid, rMid * 2, rMid * 2);
  }

}
