package minigames.client.geowars.ui;

import minigames.client.geowars.*;
import minigames.client.geowars.rendering.*;
import minigames.client.geowars.util.Vector2D;

import java.awt.*;

public class Slider extends Image {

  private int value = 0;
  private int maxValue;
  private int step;

  /**
   * Constructor for the Slider class.
   * 
   * @param engine   The GeoWars engine that the slider is running on.
   * @param position The position of the centre of the slider.
   * @param scale    The scale of the slider relative to the source Drawing.
   * @param rotation The rotation of the slider in radians relative to the source
   *                 Drawing.
   * @param maxValue The maximum value of the slider.
   * @param step     The step size of the slider.
   */
  public Slider(GeoWars engine, Vector2D position, double scale, double rotation, int maxValue, int step) {
    super(engine, position, scale, rotation, Drawing.SLIDER);
    this.maxValue = maxValue;
    this.step = step;
  }

  /**
   * Constructor for the Slider class.
   * 
   * @param engine        The GeoWars engine that the slider is running on.
   * @param position      The position of the centre of the slider.
   * @param drawnByParent Whether the slider is drawn by its parent
   * @param scale         The scale of the slider relative to the source Drawing.
   * @param rotation      The rotation of the slider in radians relative to the
   *                      source Drawing.
   * @param maxValue      The maximum value of the slider.
   * @param step          The step size of the slider.
   */
  public Slider(GeoWars engine, Vector2D position, boolean drawnByParent, double scale, double rotation, int maxValue,
      int step) {
    super(engine, position, drawnByParent, scale, rotation, Drawing.SLIDER);
    this.maxValue = maxValue;
    this.step = step;
  }

  /**
   * Get the value of the slider.
   * 
   * @return The value of the slider.
   */
  public int getMaxValue() {
    return maxValue;
  }

  /**
   * Get the value of the slider.
   * 
   * @return The value of the slider.
   */
  public int getStep() {
    return step;
  }

  /**
   * Get the value of the slider.
   * 
   * @return The value of the slider.
   */
  public int getValue() {
    return value;
  }

  /**
   * Set the value of the slider.
   * 
   * @param value The value to set the slider to.
   */
  public void setValue(int value) {
    if (value < 0) {
      this.value = 0;
    } else if (value > maxValue) {
      this.value = maxValue;
    } else {
      this.value = value;
    }
  }

  @Override
  public boolean draw(Graphics g) {
    if (g != null && drawing != null) {
      drawing.draw(g, scale, rotation, color);
    }

    return true;
  }

}
