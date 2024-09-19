package minigames.client.geowars.gameobjects;

import java.awt.*;
import java.util.ArrayList;

import minigames.client.geowars.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.colliders.Collision;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.util.*;

/*
 * It seems everything except walls has a velocity, so we should probably store that in here as
 * well as a Vector2D, and refactor those classes to use it.
 */

/**
 * Superclass for all game objects in GeoWars.
 * Constructor should add itself to GeoWar's GameObject list.
 * Has 4 functions that all GameObjects could implement.
 * Start() - is called when the object is created.
 * Update() - is called every game loop tick.
 * Destroy() - is called when the object is removed from the game.
 * Draw() - is called every animator tick.
 * Contains "tranform" data, which is a position and rotation.
 */
public class GameObject {

  // Game Engine
  protected GeoWars engine;
  protected boolean enabled = true;
  protected int cullBuffer = 20;

  // Transform Data
  protected Vector2D position;
  protected Vector2D pastPosition;
  protected double rotation;

  // Colliders
  protected ArrayList<Collider> colliders = new ArrayList<>();

  // Bounding box
  protected double width, height;

  // Drawing
  protected Drawing drawing;

  // Constructors
  // Constructs and initialises a GameObject at (0,0) with rotation 0.
  public GameObject(GeoWars engine) {
    this.position = new Vector2D();
    this.pastPosition = new Vector2D(position);
    this.rotation = 0;
    this.engine = engine;
    engine.registerGameObject(this);
  }

  // Constructs and initialises a GameObject at the specified position with
  // rotation 0.
  public GameObject(GeoWars engine, Vector2D position) {
    this.position = new Vector2D(position);
    this.pastPosition = new Vector2D(position);
    this.rotation = 0;
    this.engine = engine;
    engine.registerGameObject(this);
  }

  // Constructs and initialises a GameObject at the specified position with the
  // specified rotation.
  public GameObject(GeoWars engine, Vector2D position, double rotation) {
    this.position = new Vector2D(position);
    this.pastPosition = new Vector2D(position);
    this.rotation = rotation;
    this.engine = engine;
    engine.registerGameObject(this);
  }

  // Getters and Setters
  public GeoWars getEngine() {
    return engine;
  }

  public ArrayList<Collider> getColliders() {
    return this.colliders;
  }

  public Vector2D getPosition() {
    return position;
  }

  public Vector2D getPastPosition() {
    return pastPosition;
  }

  public double getCurrentX() {
    return position.x;
  }

  public double getCurrentY() {
    return position.y;
  }

  public double getRotation() {
    return rotation;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public void setPosition(Vector2D position) {
    this.position = new Vector2D(position);
  }

  public void setX(double x) {
    this.position.x = x;
  }

  public void setY(double y) {
    this.position.y = y;
  }

  public void setRotation(double rotation) {
    this.rotation = rotation;
  }

  protected void addCollider(Collider collider) {
    this.colliders.add(collider);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void enable() {
    this.enabled = true;
    for (Collider c : colliders) {
      c.setEnabled(true);
    }
  }

  public void disable() {
    this.enabled = false;
    for (Collider c : colliders) {
      c.setEnabled(false);
    }
  }

  /* --------------- Collision Detection --------------- */

  public void onCollision(Collision c) {
    // DO NOTHING: Subclasses will override.
  }

  // Called when the object after the object is created and in the list of
  // GameObjects.
  public void start() {
    // Nothing required here at this stage. Subclasses can implement this if they
    // need.
    // Should be used to initialise things that require the object to be in the game
    // already.
  }

  // Called every game loop tick.
  public void update() {

  }

  // Called when the object is removed from the game.
  public void destroy() {
    for (Collider c : colliders) {
      c.destroy();
    }
    this.engine.removeGameObject(this);
    // Should be used to clean up any resources that the object has created,
    // especially if there are "children" GameObjects that need to be removed from
    // the Engine as well.
  }

  // Called every animator tick.
  public boolean draw(Graphics g) {
    // Right now this returns false by default so that classes that do not draw to
    // the screen will not be registered to the renderer.
    // Override this method in subclasses to return true if the object should be
    // drawn.
    return false;
  }

  /**
   * Checks if the GameObject is off the screen, and if it is, it will call
   * destroy on itself.
   */
  public void checkOffScreen() {
    if (this.position.x < -cullBuffer || this.position.x > GeoWars.SCREEN_WIDTH + cullBuffer
        || this.position.y < -cullBuffer || this.position.y > GeoWars.SCREEN_HEIGHT + cullBuffer) {
      if (!(this instanceof Player)) {
        this.destroy();
      }
    }
  }
}
