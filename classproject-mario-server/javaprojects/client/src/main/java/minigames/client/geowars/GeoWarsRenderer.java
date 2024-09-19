package minigames.client.geowars;

import minigames.client.*;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.gameobjects.enemies.*;
import minigames.client.geowars.gameobjects.projectiles.*;
import minigames.client.geowars.scenes.Scene;
import minigames.client.geowars.ui.*;

import java.util.ArrayList;
import java.awt.*;
import java.io.File;

import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Renderer class for GeoWars.
 * Handles rendering of GameObjects to the screen.
 * This should be the only place that we implement Tickable in the entire game.
 * This should also be the only place that calls a draw() method.
 */
public class GeoWarsRenderer implements Tickable {

  // Misc util.
  private static final Logger logger = LogManager.getLogger(GeoWarsRenderer.class);

  // Singleton implementation
  private static GeoWarsRenderer instance;

  // Game Engine references
  private GeoWars engine;
  private boolean stopped;

  // UI Data
  private JFrame mainWindow;
  private JPanel mainPanel;
  private int defaultFontSize = 20;
  private Font defaultFont;

  // Game Object Lists
  private ArrayList<Wall> walls;
  private ArrayList<Enemy> enemies;
  private ArrayList<Projectile> projectiles;
  private Player player;
  private ArrayList<UIElement> uiElements;
  private ArrayList<GameObject> miscObjects;

  // Current Scene
  private Scene currentScene;

  // Concurrency Protection
  private static boolean isAdding = false;
  private static boolean isRemoving = false;
  private ArrayList<GameObject> toAdd;
  private ArrayList<GameObject> toRemove;

  // Private constructor to enforce singleton pattern.
  private GeoWarsRenderer(GeoWars engine) {
    this.engine = engine;
    stopped = false;

    // Initialise the GameObject lists
    walls = new ArrayList<Wall>();
    enemies = new ArrayList<Enemy>();
    projectiles = new ArrayList<Projectile>();
    player = null;
    uiElements = new ArrayList<UIElement>();
    miscObjects = new ArrayList<GameObject>();
    toAdd = new ArrayList<GameObject>();
    toRemove = new ArrayList<GameObject>();

    // Initialise the current scene
    currentScene = null;

    // Create the main panel
    this.initialiseMainPanel();

    // Load custom font
    this.loadCustomFont();

    // Pass the main panel to the engine
    engine.setMainPanel(mainPanel);
  }

  // Singleton instance retrieval method.
  public static GeoWarsRenderer getInstance(GeoWars engine) {
    if (instance == null) {
      instance = new GeoWarsRenderer(engine);
    }
    return instance;
  }

  // Main Panel Initialisation
  private void initialiseMainPanel() {
    // This adds our own render method to the JPanel.
    mainPanel = new JPanel() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
      }
    };
    // Set the preferred size of the main panel
    mainPanel.setPreferredSize(new Dimension(GeoWars.SCREEN_WIDTH, GeoWars.SCREEN_HEIGHT));
    mainPanel.setFocusable(true);
    mainPanel.requestFocusInWindow();
  }

  private void loadCustomFont() {
    /**
     * Possible solution to font loading issue for macOS users
     * try {
     * // Load the custom font from the path
     * InputStream is =
     * getClass().getResourceAsStream("/fonts/ZenDots-Regular.ttf");
     * if (is == null) {
     * logger.error("Font resource not found.");
     * return;
     * }
     * 
     * // Register font with GraphicsEnvironment
     * Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
     * GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
     * ge.registerFont(customFont);
     * 
     * // Set default font
     * defaultFont = customFont.deriveFont(Font.PLAIN, defaultFontSize);
     * 
     * logger.error("Custom font not found in the registered fonts.");
     * 
     * } catch (IOException | FontFormatException e) {
     * logger.error("Failed to load custom font: " + e.getMessage());
     * }
     */
    try {
      // Load the custom font
      Font customFont = Font.createFont(Font.TRUETYPE_FONT,
          new File("javaprojects\\client\\src\\main\\resources\\fonts\\ZenDots-Regular.ttf"));
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(customFont);

      Font fonts[] = ge.getAllFonts();
      for (Font f : fonts) {
        if (f.getFontName().equals("Zen Dots Regular")) {
          logger.info("Custom font loaded: " + f.getFontName());
          defaultFont = f.deriveFont(Font.PLAIN, defaultFontSize);
        }
      }

    } catch (Exception e) {
      logger.error("Failed to load custom font: " + e.getMessage());
    }
  }

  // Main render function. Gets called every Animator tick.
  private void render(Graphics g) {
    // Draw the background
    if (currentScene != null) {
      currentScene.drawBackground(g);
    }

    if (!toRemove.isEmpty()) {
      isRemoving = true;
      for (GameObject o : toRemove) {
        if (o instanceof Wall) {
          walls.remove(o);
        } else if (o instanceof Enemy) {
          enemies.remove(o);
        } else if (o instanceof Projectile) {
          projectiles.remove(o);
        } else if (o instanceof Player) {
          player = null;
        } else if (o instanceof UIElement) {
          uiElements.remove(o);
        } else {
          miscObjects.remove(o);
        }
      }
      toRemove.clear();
      isRemoving = false;
    }

    if (!toAdd.isEmpty()) {
      isAdding = true;
      for (GameObject o : toAdd) {
        if (o instanceof Wall) {
          walls.add((Wall) o);
        } else if (o instanceof Enemy) {
          enemies.add((Enemy) o);
        } else if (o instanceof Projectile) {
          projectiles.add((Projectile) o);
        } else if (o instanceof Player) {
          player = (Player) o;
        } else if (o instanceof UIElement) {
          uiElements.add((UIElement) o);
        } else {
          miscObjects.add(o);
        }
      }
      toAdd.clear();
      isAdding = false;
    }

    // Draw the walls
    for (Wall w : walls) {
      if (w.isEnabled()) {
        w.draw(g);
      }
    }

    // Draw the misc objects
    for (GameObject o : miscObjects) {
      if (o.isEnabled()) {
        o.draw(g);
      }
    }

    // Draw the enemies
    for (Enemy e : enemies) {
      if (e.isEnabled()) {
        e.draw(g);
      }
    }

    // Draw the projectiles
    for (Projectile p : projectiles) {
      if (p.isEnabled()) {
        p.draw(g);
      }
    }

    // Draw the player
    if (player != null) {
      if (player.isEnabled()) {
        player.draw(g);
      }
    }

    // Draw UI Elements
    for (UIElement ui : uiElements) {
      // Set the default font
      g.setFont(defaultFont);

      if (!ui.isDrawnByParent()) {
        if (ui.isEnabled()) {
          ui.draw(g);
        }
      }
    }

    /*
     * Debugging draw
     * g.setColor(Color.RED);
     * g.drawLine(0, GeoWars.SCREEN_HEIGHT / 2, GeoWars.SCREEN_WIDTH,
     * GeoWars.SCREEN_HEIGHT / 2);
     * g.drawLine(GeoWars.SCREEN_WIDTH / 2, 0, GeoWars.SCREEN_WIDTH / 2,
     * GeoWars.SCREEN_HEIGHT);
     */
  }

  // Show the main window. Called by the engine once the game is ready to start.
  public void show() {
    logger.info("Displaying level");

    mainWindow = new JFrame("GeoWars");
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainWindow.setContentPane(mainPanel);
    mainWindow.pack();
    // This places the window in the centre of the screen as opposed to the default
    // top left.
    mainWindow.setLocationRelativeTo(null);
    mainWindow.setVisible(true);
    mainWindow.requestFocus(null); // Commenting out this line is a possible solution to window focus issue for
                                   // macOS users
  }

  // Animator Tick Step
  @Override
  public void tick(Animator animator, long now, long delta) {
    // Repaint the main panel.
    mainPanel.repaint();

    // Request the next tick
    if (!stopped) {
      animator.requestTick(this);
    }
  }

  // Getters

  public static boolean isInstanceNull() {
    return instance == null;
  }

  // Setters

  public void setNewScene(Scene scene) {
    // this.cleanScene();
    this.currentScene = scene;
    logger.info("Scene set to: " + scene.getClass().getName());
  }

  // When we load a new scene, all object from the previous scene need to be
  // removed.
  public void cleanScene() {
    for (Wall w : walls) {
      removeGameObject(w);
    }

    for (Enemy e : enemies) {
      removeGameObject(e);
    }

    for (Projectile p : projectiles) {
      removeGameObject(p);
    }

    if (player != null) {
      removeGameObject(player);
    }

    for (UIElement ui : uiElements) {
      removeGameObject(ui);
    }

    for (GameObject o : miscObjects) {
      removeGameObject(o);
    }
  }

  // Registers a 'Renderable' object with the renderer.
  public void registerGameObject(GameObject o) {
    boolean hasAdded = false;
    while (!hasAdded) {
      if (!isAdding) {
        isAdding = true;
        toAdd.add(o);
        isAdding = false;
        hasAdded = true;
      }
    }
  }

  // Removes a 'Renderable' object from the renderer.
  public void removeGameObject(GameObject o) {
    boolean hasRemoved = false;
    while (!hasRemoved) {
      if (!isRemoving) {
        isRemoving = true;
        toRemove.add(o);
        isRemoving = false;
        hasRemoved = true;
      }
    }
  }

  // Stops the Renderer
  public void stop() {
    stopped = true;
    cleanScene();
    toAdd.clear();
    toRemove.clear();
    currentScene = null;
    mainWindow.dispose();
    instance = null;
  }
}
