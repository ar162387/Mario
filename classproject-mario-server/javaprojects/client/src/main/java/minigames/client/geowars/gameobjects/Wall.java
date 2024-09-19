package minigames.client.geowars.gameobjects;

import java.awt.*;

import minigames.client.geowars.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.util.*;

/**
 * Wall class for GeoWars.
 * A Wall is a GameObject that cannot move and is used to define the
 * boundaries of the playable area.
 * Has a Collider, which should interact with projectiles, enemies, and the
 * player.
 */
public class Wall extends GameObject {

  // Constructor
  public Wall(GeoWars engine, Vector2D position, double rotation, int width, int height) {
    // Call the GameObject constructor
    // A Wall's Position is in the centre of the wall.
    super(engine, position, rotation);
    // Wall Collider
    // Width of the wall in pixels.
    this.width = width;
    // Height of the wall in pixels.
    this.height = height;

    Collider mainCollider = new Collider(this, width, height, false);
    addCollider(mainCollider);

    drawing = Drawing.getDrawing(Drawing.WALL, this);
  }

  @Override
  public boolean draw(Graphics g) {
    if (g != null && drawing != null) {
      drawing.draw(g, 1, 0, null);
    }
    return true;
  }

}
