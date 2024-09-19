package minigames.client.geowars.ui;

import minigames.client.geowars.*;
import minigames.client.geowars.rendering.*;
import minigames.client.geowars.util.Vector2D;

import java.awt.*;

/**
 * The class defining a simple drawable image in GeoWars.
 * Extends UIElement, and is drawn on top of the game world.
 * Will be used to draw images rather than text as a UIElement.
 */
public class Image extends UIElement {

  protected double scale; // Value larger than 0.
  protected double rotation; // Between -PI and PI.
  protected Drawing drawing;

  /**
   * Constructor for the Image class.
   * This constructor should be used if the image is drawn on its own with no
   * parent UI element.
   * 
   * @param engine   The GeoWars engine that the image is running on.
   * @param position The position of the centre of the image.
   * @param scale    The scale of the image relative to the source Drawing.
   * @param rotation The rotation of the image in radians relative to the source
   *                 Drawing.
   * @param color    The color of the image.
   * @param drawing  The Drawing object that the image will be based on.
   */
  public Image(GeoWars engine, Vector2D position, double scale, double rotation, int drawingType) {
    super(engine, position);
    this.scale = Math.max(0, scale);
    this.rotation = Drawing.clampRotation(rotation);
    this.drawing = Drawing.getDrawing(drawingType, this);
  }

  /**
   * Constructor for the Image class.
   * This constructor should be used if the image is drawn as a child of a parent
   * UI element.
   * 
   * @param engine        The {@code GeoWars} engine that the image is running on.
   * @param position      The position of the centre of the image.
   * @param drawnByParent Whether the image is drawn by its parent
   * @param scale         The scale of the image relative to the source Drawing.
   * @param rotation      The rotation of the image in radians relative to the
   *                      source Drawing.
   * @param color         The color of the image.
   * @param drawing       The Drawing object that the image will be based on.
   */
  public Image(GeoWars engine, Vector2D position, boolean drawnByParent, double scale, double rotation,
      int drawingType) {
    super(engine, position, drawnByParent);
    this.scale = Math.max(0, scale);
    this.rotation = Drawing.clampRotation(rotation);
    this.drawing = Drawing.getDrawing(drawingType, this);
  }

  @Override
  public boolean draw(Graphics g) {
    if (g != null && drawing != null) {
      drawing.draw(g, scale, rotation, color);
    }

    return true;
  }
}
