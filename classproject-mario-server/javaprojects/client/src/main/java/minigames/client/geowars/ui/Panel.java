package minigames.client.geowars.ui;

import java.awt.*;
import java.util.ArrayList;

import minigames.client.geowars.GeoWars;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.util.Vector2D;

public class Panel extends UIElement {

  protected Vector2D size;
  protected ArrayList<UIElement> elements;
  protected int border = 6;

  /**
   * Constructor for the Panel class.
   * 
   * @param engine   The GeoWars engine that the panel is running on.
   * @param position The position of the top-left corner of the panel.
   * @param size     The size of the panel.
   */
  public Panel(GeoWars engine, Vector2D position, Vector2D size) {
    super(engine, position);
    this.size = size;
  }

  /**
   * Constructor for the Panel class.
   * 
   * @param engine        The GeoWars engine that the panel is running on.
   * @param position      The position of the top-left corner of the panel.
   * @param size          The size of the panel.
   * @param drawnByParent Whether the panel is drawn by its parent and should be
   *                      skipped by the
   *                      Renderer.
   */
  public Panel(GeoWars engine, Vector2D position, boolean drawnByParent, Vector2D size) {
    super(engine, position, drawnByParent);
    this.size = size;
  }

  /**
   * Add a UI element to the panel.
   * 
   * @param element The element to add to the panel.
   */
  public void addElement(UIElement element, Vector2D offset) {
    if (elements == null) {
      elements = new ArrayList<UIElement>();
    }
    elements.add(element);

    element.getPosition().set(this.getPosition());
    element.getPosition().add(offset);
  }

  /**
   * Remove a UI element from the panel.
   * 
   * @param element The element to remove from the panel.
   */
  public void removeElement(UIElement element) {
    if (elements != null) {
      element.destroy();
      elements.remove(element);
    }
  }

  /**
   * Set the border thickness of the panel.
   * 
   * @param border The border thickness of the panel.
   */
  public void setBorder(int border) {
    this.border = border;
  }

  @Override
  public void enable() {
    super.enable();
    if (elements != null) {
      for (UIElement element : elements) {
        element.enable();
      }
    }
  }

  @Override
  public void disable() {
    super.disable();
    if (elements != null) {
      for (UIElement element : elements) {
        element.disable();
      }
    }
  }

  @Override
  public void destroy() {
    for (UIElement element : elements) {
      element.destroy();
    }
    super.destroy();
  }

  @Override
  public boolean draw(Graphics g) {
    if (g != null && this.color != null) {

      if (this.color != Drawing.TRANSPARENT_COLOR) {

        int rColor = color.getRed();
        int gColor = color.getGreen();
        int bColor = color.getBlue();
        Color centreColor = new Color(rColor, gColor, bColor, 70);

        g.setColor(centreColor);
      } else {
        g.setColor(color);
      }

      g.fillRect((int) position.x - (int) size.x / 2 + border, (int) position.y - (int) size.y / 2 + border,
          (int) size.x - 2 * border, (int) size.y - 2 * border);

      g.setColor(color);

      // Draw the border
      // Left
      g.fillRect((int) position.x - (int) size.x / 2, (int) position.y - (int) size.y / 2, border, (int) size.y);
      // Right
      g.fillRect((int) position.x + (int) size.x / 2 - border, (int) position.y - (int) size.y / 2, border,
          (int) size.y);
      // Top
      g.fillRect((int) position.x - (int) size.x / 2, (int) position.y - (int) size.y / 2, (int) size.x, border);
      // Bottom
      g.fillRect((int) position.x - (int) size.x / 2, (int) position.y + (int) size.y / 2 - border, (int) size.x,
          border);

      if (elements != null) {
        for (UIElement element : elements) {
          if (element.isDrawnByParent()) {
            element.draw(g);
          }
        }
      }

    }
    return true;
  }
}
