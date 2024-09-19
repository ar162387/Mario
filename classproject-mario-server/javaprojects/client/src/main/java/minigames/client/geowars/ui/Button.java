package minigames.client.geowars.ui;

import java.awt.*;

import javax.swing.*;

import minigames.client.geowars.*;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.util.Vector2D;

/**
 * Class for representing Buttons in GeoWars.
 * Extends UIElement.
 * Will be used for clickable buttons on the screen.
 * Has a size and can support a Text object.
 */
public class Button extends Panel {

  private boolean isHighlighted = false;
  private boolean isClicked = false;
  private boolean readyForClick = false;

  private ButtonAction action = new ButtonAction() {
    @Override
    public void execute() {
      System.err.println("Button was clicked but no action has been set!");
    }
  };

  private JPanel panel;
  private InputManager inputManager;

  /**
   * Constructor for the Button class.
   * 
   * @param engine   The GeoWars engine that the button is running on.
   * @param position The position of the centre of the button.
   * @param size     The size of the button (given in pixels).
   */
  public Button(GeoWars engine, Vector2D position, Vector2D size) {
    super(engine, position, size);
    this.size = size;
    this.panel = engine.getMainPanel();

    if (InputManager.isInstanceNull()) {
      System.err.println("InputManager instance is null in Button constructor.");
    } else {
      this.inputManager = InputManager.getInstance(null, null);
    }
  }

  /**
   * Constructor for the Button class.
   * 
   * @param engine        The GeoWars engine that the button is running on.
   * @param position      The position of the centre of the button.
   * @param size          The size of the button (given in pixels).
   * @param drawnByParent Whether the button is drawn by its parent and should be
   *                      skipped by the
   *                      Renderer.
   */
  public Button(GeoWars engine, Vector2D position, boolean drawnByParent, Vector2D size) {
    super(engine, position, drawnByParent, size);
    this.size = size;
    this.panel = engine.getMainPanel();

    if (InputManager.isInstanceNull()) {
      System.err.println("InputManager instance is null in Button constructor.");
    } else {
      this.inputManager = InputManager.getInstance(null, null);
    }
  }

  /**
   * Set the action to be executed when the button is clicked.
   * 
   * @param action The action to be executed when the button is clicked.
   */
  public void setAction(ButtonAction action) {
    this.action = action;
  }

  @Override
  public void update() {
    // Check if the mouse is over the button.
    boolean pastHighlighted = isHighlighted;
    isHighlighted = checkHighlight();

    if (!isHighlighted) {
      // The mouse is not over the button, so it is not ready for a click.
      readyForClick = false;
    }

    // Check if the button is being clicked this frame.
    boolean pastClicked = isClicked;
    isClicked = inputManager.getLeftMousePressed();

    if (isClicked && !pastClicked && isHighlighted) {
      // The mouse button was pressed over the button.
      readyForClick = true;
    }

    if (!isClicked && pastClicked && isHighlighted && readyForClick) {
      // The mouse button was pressed over the button, and has been released over the
      // button.
      action.execute();
      readyForClick = false;
      inputManager.setMouseOverButton(false);
    }

    // If the button has been highlighted this frame, but the mouse is not clicked,
    // we need to tell the InputManager that the user is hovering over a button and
    // probably would like to click on it, so don't trigger other mouse actions.
    if (isHighlighted && !isClicked && !pastHighlighted) {
      inputManager.setMouseOverButton(true);
    }

    // If the button stops being highlighted, click or not, we need to tell the
    // InputManager that the user is no longer hovering over a button, and probably
    // wants other mouse actions to work.
    if (!isHighlighted && pastHighlighted) {
      inputManager.setMouseOverButton(false);
    }

    // Change the color of the button based on the state.
    if (baseColor != Drawing.TRANSPARENT_COLOR) {
      if (isClicked && readyForClick) {
        color = baseColor.darker();
      } else if (isHighlighted) {
        color = baseColor.brighter();
      } else {
        color = baseColor;
      }
    }

    for (UIElement element : elements) {
      if (isClicked && readyForClick) {
        element.setDrawColor(element.getColor().darker());
      } else if (isHighlighted) {
        element.setDrawColor(element.getColor().brighter());
      } else {
        element.setDrawColor(element.getColor());
      }
    }
  }

  /**
   * Check if the mouse is over the button.
   * 
   * @return Whether the mouse is over the button.
   */
  private boolean checkHighlight() {
    Point mousePosAbs = MouseInfo.getPointerInfo().getLocation();
    Point windowLocation = panel.getLocationOnScreen();
    Point mousePos = new Point(mousePosAbs.x - windowLocation.x, mousePosAbs.y - windowLocation.y);

    double left = position.x - size.x / 2;
    double right = position.x + size.x / 2;
    double top = position.y - size.y / 2;
    double bottom = position.y + size.y / 2;

    if (mousePos.x > left && mousePos.x < right && mousePos.y > top && mousePos.y < bottom) {
      return true;
    }

    return false;
  }
}