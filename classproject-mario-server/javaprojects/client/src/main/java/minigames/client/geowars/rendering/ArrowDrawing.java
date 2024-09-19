package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;
// import minigames.client.geowars.util.Vector2D;

public class ArrowDrawing extends Drawing {

  private int defaultSize = 40;

  // private Vector2D defaultRectRight = new Vector2D(5, 10);
  // private Vector2D defaultRectLeft = new Vector2D(-20, 10);
  // private Vector2D defaultTriTip = new Vector2D(20, 0);
  // private Vector2D defaultTriSide = new Vector2D(5, 20);

  // private double rectRightRadius;
  // private double rectLeftRadius;
  // private double triTipRadius;
  // private double triSideRadius;

  // private double rectRightTheta;
  // private double rectLeftTheta;
  // private double triTipTheta;
  // private double triSideTheta;

  public ArrowDrawing(GameObject parent) {
    super(parent);
    this.defaultColor = Drawing.GEOWARS_COLOR;

    // calcDefaultRadii();
    // calcDefaultThetas();
  }

  // private void calcDefaultRadii() {
  // rectRightRadius = defaultRectRight.length();
  // rectLeftRadius = defaultRectLeft.length();
  // triTipRadius = defaultTriTip.length();
  // triSideRadius = defaultTriSide.length();
  // }

  // private void calcDefaultThetas() {
  // rectRightTheta = Math.atan2(defaultRectRight.y, defaultRectRight.x);
  // rectLeftTheta = Math.atan(defaultRectLeft.y / defaultRectLeft.x);
  // triTipTheta = Math.atan2(defaultTriTip.y, defaultTriTip.x);
  // triSideTheta = Math.atan2(defaultTriSide.y, defaultTriSide.x);
  // }

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

    // Might come back to this later, was an attempt to have the arrow be able to be
    // rotated.

    // // Scale Radii
    // double rectRightRadiusScaled = rectRightRadius * scale;
    // double rectLeftRadiusScaled = rectLeftRadius * scale;
    // double triTipRadiusScaled = triTipRadius * scale;
    // double triSideRadiusScaled = triSideRadius * scale;

    // // Set the points
    // int nPoints = 7;
    // // x = r * cos(theta)
    // int xPoints[] = {
    // (int) (x + rectRightRadiusScaled * Math.cos(clampRotation(rectRightTheta +
    // rotation))),
    // (int) (x + triSideRadiusScaled * Math.cos(clampRotation(triSideTheta +
    // rotation))),
    // (int) (x + triTipRadiusScaled * Math.cos(clampRotation(triTipTheta +
    // rotation))),
    // (int) (x + triSideRadiusScaled * Math.cos(clampRotation(-triSideTheta +
    // rotation))),
    // (int) (x + rectRightRadiusScaled * Math.cos(clampRotation(-rectRightTheta +
    // rotation))),
    // (int) (x + rectLeftRadiusScaled * Math.cos(clampRotation(-rectLeftTheta +
    // rotation))),
    // (int) (x + rectLeftRadiusScaled * Math.cos(clampRotation(rectLeftTheta +
    // rotation))),
    // };
    // // y = r * sin(theta)
    // int yPoints[] = {
    // (int) (y + rectRightRadiusScaled * Math.sin(clampRotation(rectRightTheta +
    // rotation))),
    // (int) (y + triSideRadiusScaled * Math.sin(clampRotation(triSideTheta +
    // rotation))),
    // (int) (y + triTipRadiusScaled * Math.sin(clampRotation(triTipTheta +
    // rotation))),
    // (int) (y + triSideRadiusScaled * Math.sin(clampRotation(-triSideTheta +
    // rotation))),
    // (int) (y + rectRightRadiusScaled * Math.sin(clampRotation(-rectRightTheta +
    // rotation))),
    // (int) (y + rectLeftRadiusScaled * Math.sin(clampRotation(-rectLeftTheta +
    // rotation))),
    // (int) (y + rectLeftRadiusScaled * Math.sin(clampRotation(rectLeftTheta +
    // rotation))),
    // };

    // // Draw Outline first
    // g2d.setColor(color.darker());
    // g2d.setStroke(new BasicStroke((int) (3 * scale)));

    // g2d.drawPolygon(xPoints, yPoints, nPoints);

    // // Fill in the arrow
    // g2d.setColor(color);
    // g2d.fillPolygon(xPoints, yPoints, nPoints);

    // Draw the outlines first
    g2d.setColor(color.darker());
    g2d.setStroke(new BasicStroke((int) (5 * scale)));

    // Draw the rectangle on the right
    g2d.drawRect(x - size / 8, y - size / 5, 5 * size / 8, 2 * size / 5);
    // Draw the triangle on the left
    int numPoints = 3;
    int[] xPoints = { x - size / 2, x - size / 8, x - size / 8 };
    int[] yPoints = { y, y + size / 3, y - size / 3 };
    g2d.drawPolygon(xPoints, yPoints, numPoints);

    // Fill in the arrow
    g2d.setColor(color);
    // Fill in the rectangle on the right
    g2d.fillRect(x - size / 8, y - size / 4, 5 * size / 8, size / 2);
    // Fill in the triangle on the left
    g2d.fillPolygon(xPoints, yPoints, numPoints);
  }

}
