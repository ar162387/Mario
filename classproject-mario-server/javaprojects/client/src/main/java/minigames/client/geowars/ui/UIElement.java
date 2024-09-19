package minigames.client.geowars.ui;

import java.awt.*;
import minigames.client.geowars.*;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.util.Vector2D;

/**
 * The superclass defining all UIElements in GeoWars.
 * Currently will include Buttons and Text.
 * Extends GameObject, and is drawn on top of the game world.
 */
public class UIElement extends GameObject {

  boolean drawnByParent = false;
  protected Color baseColor = Drawing.GEOWARS_COLOR;
  protected Color color = baseColor;

  /**
   * Constructor for the UIElement class.
   * 
   * @param engine   The GeoWars engine that the UIElement is running on.
   * @param position The position of the top-left corner of the UIElement.
   */
  public UIElement(GeoWars engine, Vector2D position) {
    super(engine, position);
  }

  /**
   * Constructor for the UIElement class.
   * 
   * @param engine        The GeoWars engine that the UIElement is running on.
   * @param position      The position of the top-left corner of the UIElement.
   * @param drawnByParent Whether the UIElement is drawn by its parent and should
   *                      be skipped by the
   *                      Renderer.
   */
  public UIElement(GeoWars engine, Vector2D position, boolean drawnByParent) {
    super(engine, position);
    this.drawnByParent = drawnByParent;
  }

  /**
   * Get whether the UIElement is drawn by its parent and should be skipped by the
   * Renderer.
   * 
   * @return Whether the UIElement is drawn by its parent and should be skipped by
   *         the Renderer.
   */
  public boolean isDrawnByParent() {
    return drawnByParent;
  }

  /**
   * Get the base color of the UIElement.
   * 
   * @return The base color of the UIElement.
   */
  public Color getColor() {
    return baseColor;
  }

  /**
   * Set the base color of the UIElement.
   * Also resets the draw color to the base color.
   * 
   * @param color The color to set the base color to.
   */
  public void setColor(Color color) {
    this.baseColor = color;
    this.color = baseColor;
  }

  /**
   * Set the draw color of the UIElement.
   * This is the actual colour that will be drawn by the renderer.
   * Usually will be set based off some aspect of the base color.
   * 
   * @param color The color to set the draw color to.
   */
  public void setDrawColor(Color color) {
    this.color = color;
  }
}
