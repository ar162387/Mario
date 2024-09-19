package minigames.client.geowars.rendering;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.PlayerExplosion;

import java.awt.*;

public class PlayerExplosionDrawing extends Drawing {

  public PlayerExplosionDrawing(GameObject parent) {
    super(parent);
    defaultColor = Color.WHITE;
  }

  @Override
  public void draw(Graphics g, double scale, double rotation, Color customColor) {
    // Cast the parent to a PlayerExplosion object
    if (parent == null || !(parent instanceof PlayerExplosion)) {
      return;
    }

    PlayerExplosion explosion = (PlayerExplosion) parent;

    // Set the scale
    int size = (int) (explosion.getSize() * scale);
    int x = (int) explosion.getPosition().x;
    int y = (int) explosion.getPosition().y;

    // Set the color
    Color color = defaultColor;
    if (customColor != null) {
      color = customColor;
    }

    g.setColor(color);

    // Draw the explosion
    g.drawRect(x - size / 2, y - size / 2, size, size);
  }
}
