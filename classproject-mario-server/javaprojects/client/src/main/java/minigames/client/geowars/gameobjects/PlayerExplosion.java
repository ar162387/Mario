package minigames.client.geowars.gameobjects;

import minigames.client.geowars.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.util.*;

import java.awt.*;

public class PlayerExplosion extends GameObject {

  private double explosionTime;

  private double size;
  private double maxSize;

  private Collider exploder;

  private Drawing drawing;

  /**
   * Create a new PlayerExplosion.
   * 
   * @param engine        The engine that the explosion is in.
   * @param position      The position of the explosion.
   * @param explosionTime The time it takes for the explosion to reach its maximum
   *                      size.
   */
  public PlayerExplosion(GeoWars engine, Vector2D position, double explosionTime) {
    super(engine, position);
    this.explosionTime = explosionTime;
    this.size = 0;
    this.maxSize = GeoWars.SCREEN_WIDTH * 2;
    exploder = new Collider(this, size, size, false);
    this.addCollider(exploder);

    drawing = Drawing.getDrawing(Drawing.PLAYER_EXPLOSION, this);
  }

  /**
   * Get the size of the explosion.
   * 
   * @return The size of the explosion.
   */
  public double getSize() {
    return size;
  }

  @Override
  public void update() {
    // Incease the size of the explosion such that it reaches maxSize in
    // explosionTime seconds.
    size += maxSize * (1 / explosionTime) * DeltaTime.delta();
    exploder.setWidth(size);
    exploder.setHeight(size);
    // Destroy the explosion if it reaches maxSize.
    if (size > maxSize) {
      this.destroy();
    }
  }

  @Override
  public boolean draw(Graphics g) {
    if (g != null && drawing != null) {
      drawing.draw(g, 1, 0, null);
    }

    return true;
  }

}
