package minigames.client.geowars.rendering;

import java.awt.*;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.ui.Slider;

public class SliderDrawing extends Drawing {

  private int defaultWidth = 500;
  private int defaultHeight = 30;

  public SliderDrawing(GameObject parent) {
    super(parent);
    this.defaultColor = Drawing.GEOWARS_COLOR;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    if (parent == null || !(parent instanceof Slider)) {
      return;
    }

    Slider slider = (Slider) parent;
    Graphics2D g2d = (Graphics2D) g;

    // Set the scale
    int width = (int) (defaultWidth * scale);
    int height = (int) (defaultHeight * scale);
    int x = (int) parent.getCurrentX();
    int y = (int) parent.getCurrentY();

    int numTicks = slider.getMaxValue() / slider.getStep();
    int tickWidth = width / numTicks;

    // Set the color
    Color color = customColor != null ? customColor : defaultColor;

    // Draw the image.
    g2d.setStroke(new BasicStroke((int) (6 * scale)));

    int currX = x - width / 2;
    for (int i = 1; i <= numTicks; i++) {
      if (slider.getValue() >= i * slider.getStep()) {
        g2d.setColor(color.brighter());
      } else {
        g2d.setColor(color.darker());
      }
      g2d.fillRect(currX, y - height / 2, tickWidth, height);
      g2d.setColor(color);
      g2d.drawRect(currX, y - height / 2, tickWidth, height);
      currX += tickWidth;
    }

  }

}
