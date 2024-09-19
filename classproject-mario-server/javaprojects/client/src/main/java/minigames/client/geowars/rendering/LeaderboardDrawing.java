package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;

public class LeaderboardDrawing extends Drawing {

  private int podiumWidth = 12;
  private int podiumHeight = 30;
  private int baseHeight = 8;

  public LeaderboardDrawing(GameObject parent) {
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
    int pWidth = (int) (podiumWidth * scale);
    int pHeight = (int) (podiumHeight * scale);
    int bHeight = (int) (baseHeight * scale);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;
    g2d.setColor(color);

    // Draw the image.
    // 1st Place Podium
    g2d.fillRect(x - pWidth / 2, y - pHeight / 2, pWidth, pHeight);

    // 2nd Place Podium
    int p2Height = (int) (podiumHeight * 0.6);
    int p2YStart = y + (pHeight / 2) - (p2Height);
    g2d.fillRect(x - 3 * pWidth / 2, p2YStart, pWidth, p2Height);

    // 3rd Place Podium
    int p3Height = (int) (podiumHeight * 0.3);
    int p3YStart = y + (pHeight / 2) - (p3Height);
    g2d.fillRect(x + pWidth / 2, p3YStart, pWidth, p3Height);

    // Draw outlines
    g2d.setColor(color.darker());
    g2d.setStroke(new BasicStroke((int) (2 * scale)));
    g2d.drawRect(x - pWidth / 2, y - pHeight / 2, pWidth, pHeight);
    g2d.drawRect(x - 3 * pWidth / 2, p2YStart, pWidth, p2Height);
    g2d.drawRect(x + pWidth / 2, p3YStart, pWidth, p3Height);

    // Podium Base
    int baseYStart = y + (pHeight / 2);
    g2d.fillRect(x - (2 * pWidth), baseYStart, 4 * pWidth, bHeight);

  }

}
