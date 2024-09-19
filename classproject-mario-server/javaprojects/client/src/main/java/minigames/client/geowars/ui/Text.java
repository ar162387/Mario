package minigames.client.geowars.ui;

import java.awt.*;

import minigames.client.geowars.*;
import minigames.client.geowars.util.Vector2D;

/**
 * Class for representing text in GeoWars.
 * Extends UIElement.
 * Will be used for displaying text on the screen.
 * Has a font, size, and color.
 */
public class Text extends UIElement {

  // Text Alignment
  public static final int ALIGN_LEFT = 0;
  public static final int ALIGN_CENTER = 1;
  public static final int ALIGN_RIGHT = 2;

  private String content;

  // To be used if the defaults are not desired.
  private float fontSize = 0;
  private Font customFont;
  private int alignment = ALIGN_CENTER;

  /**
   * Constructor for the Text class.
   * 
   * @param engine   The GeoWars engine that the text is running on.
   * @param position The position of the top-left corner of the text by default.
   *                 Changes with alignment.
   */
  public Text(GeoWars engine, Vector2D position) {
    super(engine, position);
  }

  /**
   * Constructor for the Text class.
   * 
   * @param engine        The GeoWars engine that the text is running on.
   * @param position      The position of the top-left corner of the text by
   *                      default. Changes with alignment.
   * @param drawnByParent Whether the text is drawn by its parent and should be
   *                      skipped by the
   *                      Renderer.
   */
  public Text(GeoWars engine, Vector2D position, boolean drawnByParent) {
    super(engine, position, drawnByParent);
  }

  /**
   * Get the content of the Text object.
   * 
   * @return The text to display.
   */
  public String getContent() {
    return content;
  }

  /**
   * Set the content of the text.
   * 
   * @param content The text to display.
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Set the font size of the text.
   * 
   * @param fontSize The size of the font.
   */
  public void setFontSize(float fontSize) {
    this.fontSize = fontSize;
  }

  /**
   * Set the font of the text.
   * 
   * @param font The font to use.
   */
  public void setFont(Font font) {
    this.customFont = font;
  }

  /**
   * Set the alignment of the text.
   * 
   * @param alignment The alignment of the text.
   */
  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

  @Override
  public boolean draw(Graphics g) {
    if (g != null && this.color != null && this.content != null) {
      if (customFont != null) {
        g.setFont(customFont);
      } else {
        if (fontSize != 0) {
          g.setFont(g.getFont().deriveFont(fontSize));
        }
      }

      g.setColor(this.color);

      int xDraw;
      switch (alignment) {
        case ALIGN_CENTER:
          xDraw = (int) position.x - (g.getFontMetrics().stringWidth(content) / 2);
          break;
        case ALIGN_RIGHT:
          xDraw = (int) position.x - g.getFontMetrics().stringWidth(content);
          break;
        default:
          xDraw = (int) position.x;
          break;
      }

      int yDraw = (int) position.y + g.getFontMetrics().getHeight() / 2;
      g.drawString(content, xDraw, yDraw);
    }

    return true;
  }

}
