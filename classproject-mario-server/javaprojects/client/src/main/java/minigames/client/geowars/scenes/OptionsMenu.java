package minigames.client.geowars.scenes;

import minigames.client.geowars.*;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.ui.*;
import minigames.client.geowars.util.Vector2D;

/**
 * This class represents the options menu of the game.
 * It contains the instructions to build the options menu.
 * 
 * This scene is accessed only from the main menu.
 */
public class OptionsMenu extends Menu {

  private GeoWarsOptions options;

  private Slider playerSpeedSlider;
  private Slider musicVolumeSlider;
  private Slider sfxVolumeSlider;

  /**
   * Constructor for OptionsMenu.
   * 
   * @param engine The GeoWars client.
   */
  public OptionsMenu(GeoWars engine) {
    super(engine);
    this.options = gm.getOptions();
  }

  /**
   * Load the options menu.
   * This method contains the instructions to build the options menu.
   * The options menu contains:
   * - A Title text that says 'Options'
   * - 3 'sliders' that allow the player to change their speed, the music volume,
   * and the SFX volume
   * - Next to the music and sfx volume sliders, there should be a 'mute' button.
   * - A 'Back to Menu' button
   */
  @Override
  public void load() {
    super.load();

    // Title
    this.addUIElement(generateTitleText());

    // Player Speed
    this.addUIElement(generatePlayerSpeedSlider());

    // Music Volume
    this.addUIElement(generateMusicVolumeSlider());

    // SFX Volume
    this.addUIElement(generateSFXVolumeSlider());

    // Back to Menu
    this.addUIElement(generateBackToMenuButton());
  }

  @Override
  public Scene recreate() {
    return new OptionsMenu(engine);
  }

  /**
   * Generate the title text for the options menu.
   * Text says 'Options', and is centered at the top of the screen.
   * 
   * @return The title text.
   */
  private Text generateTitleText() {
    Text title = new Text(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, 70));
    title.setAlignment(Text.ALIGN_CENTER);
    title.setContent("Options");
    title.setFontSize(70);
    title.setColor(Drawing.TITLE_COLOR);

    return title;
  }

  private Panel generatePlayerSpeedSlider() {
    Panel panel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, 220), new Vector2D(1000, 150));
    panel.setColor(Drawing.TRANSPARENT_COLOR);

    // Text
    Text text = new Text(engine, new Vector2D());
    text.setContent("Player Speed");
    text.setFontSize(30);
    text.setAlignment(Text.ALIGN_CENTER);

    panel.addElement(text, new Vector2D(0, -40));

    // Player speed slider
    playerSpeedSlider = new Slider(engine, new Vector2D(), true, 1, 0, 5, 1);
    playerSpeedSlider.setValue(options.getPlayerSpeed());

    panel.addElement(playerSpeedSlider, new Vector2D(0, 20));

    // Plus Button
    Button plusButton = new Button(engine, new Vector2D(0, 0), new Vector2D(50, 50));
    plusButton.setColor(Drawing.TRANSPARENT_COLOR);

    panel.addElement(plusButton, new Vector2D(300, 20));

    plusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.incrementPlayerSpeed();
        playerSpeedSlider.setValue(options.getPlayerSpeed());
      }
    });

    Image plusImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.PLUS);

    plusButton.addElement(plusImage, new Vector2D());

    // Minus Button
    Button minusButton = new Button(engine, new Vector2D(0, 0), new Vector2D(50, 50));
    minusButton.setColor(Drawing.TRANSPARENT_COLOR);

    panel.addElement(minusButton, new Vector2D(-300, 20));

    minusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.decrementPlayerSpeed();
        playerSpeedSlider.setValue(options.getPlayerSpeed());
      }
    });

    Image minusImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.MINUS);

    minusButton.addElement(minusImage, new Vector2D());

    return panel;
  }

  private Panel generateMusicVolumeSlider() {
    Panel panel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, 420), new Vector2D(1000, 150));
    panel.setColor(Drawing.TRANSPARENT_COLOR);

    // Text
    Text text = new Text(engine, new Vector2D());
    text.setContent("Music Volume");
    text.setFontSize(30);
    text.setAlignment(Text.ALIGN_CENTER);

    panel.addElement(text, new Vector2D(0, -40));

    // Music Volume slider
    musicVolumeSlider = new Slider(engine, new Vector2D(), true, 1, 0, 10, 1);
    musicVolumeSlider.setValue(options.getMusicVolume());

    panel.addElement(musicVolumeSlider, new Vector2D(0, 20));

    // Plus Button
    Button plusButton = new Button(engine, new Vector2D(0, 0), new Vector2D(50, 50));
    plusButton.setColor(Drawing.TRANSPARENT_COLOR);

    panel.addElement(plusButton, new Vector2D(300, 20));

    plusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.setMusicVolume(options.getMusicVolume() + 1);
        musicVolumeSlider.setValue(options.getMusicVolume());
      }
    });

    Image plusImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.PLUS);

    plusButton.addElement(plusImage, new Vector2D());

    // Minus Button
    Button minusButton = new Button(engine, new Vector2D(0, 0), new Vector2D(50, 50));
    minusButton.setColor(Drawing.TRANSPARENT_COLOR);

    panel.addElement(minusButton, new Vector2D(-300, 20));

    minusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.setMusicVolume(options.getMusicVolume() - 1);
        musicVolumeSlider.setValue(options.getMusicVolume());
      }
    });

    Image minusImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.MINUS);

    minusButton.addElement(minusImage, new Vector2D());

    // Mute Button
    Button muteButton = new Button(engine, new Vector2D(0, 0), new Vector2D(80, 80));

    panel.addElement(muteButton, new Vector2D(-400, 20));

    Image muteImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.MUTE);
    if (options.isMusicMuted()) {
      muteImage.setColor(Drawing.QUIT_COLOR);
    } else {
      muteImage.setColor(Drawing.START_COLOR);
    }

    muteButton.addElement(muteImage, new Vector2D());

    muteButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.setMusicMuted(!options.isMusicMuted());
        if (options.isMusicMuted()) {
          muteImage.setColor(Drawing.QUIT_COLOR);
        } else {
          muteImage.setColor(Drawing.START_COLOR);
        }
      }
    });

    return panel;
  }

  private Panel generateSFXVolumeSlider() {
    Panel panel = new Panel(engine, new Vector2D(GeoWars.SCREEN_WIDTH / 2, 570), new Vector2D(1000, 150));
    panel.setColor(Drawing.TRANSPARENT_COLOR);

    // Text
    Text text = new Text(engine, new Vector2D());
    text.setContent("SFX Volume");
    text.setFontSize(30);
    text.setAlignment(Text.ALIGN_CENTER);

    panel.addElement(text, new Vector2D(0, -40));

    // SFX Volume slider
    sfxVolumeSlider = new Slider(engine, new Vector2D(), true, 1, 0, 10, 1);
    sfxVolumeSlider.setValue(options.getSfxVolume());

    panel.addElement(sfxVolumeSlider, new Vector2D(0, 20));

    // Plus Button
    Button plusButton = new Button(engine, new Vector2D(0, 0), new Vector2D(50, 50));
    plusButton.setColor(Drawing.TRANSPARENT_COLOR);

    panel.addElement(plusButton, new Vector2D(300, 20));

    plusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.setSfxVolume(options.getSfxVolume() + 1);
        sfxVolumeSlider.setValue(options.getSfxVolume());
      }
    });

    Image plusImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.PLUS);

    plusButton.addElement(plusImage, new Vector2D());

    // Minus Button
    Button minusButton = new Button(engine, new Vector2D(0, 0), new Vector2D(50, 50));
    minusButton.setColor(Drawing.TRANSPARENT_COLOR);

    panel.addElement(minusButton, new Vector2D(-300, 20));

    minusButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.setSfxVolume(options.getSfxVolume() - 1);
        sfxVolumeSlider.setValue(options.getSfxVolume());
      }
    });

    Image minusImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.MINUS);

    minusButton.addElement(minusImage, new Vector2D());

    // Mute Button
    Button muteButton = new Button(engine, new Vector2D(0, 0), new Vector2D(80, 80));

    panel.addElement(muteButton, new Vector2D(-400, 20));

    Image muteImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.MUTE);
    if (options.isSfxMuted()) {
      muteImage.setColor(Drawing.QUIT_COLOR);
    } else {
      muteImage.setColor(Drawing.START_COLOR);
    }

    muteButton.addElement(muteImage, new Vector2D());

    muteButton.setAction(new ButtonAction() {
      @Override
      public void execute() {
        options.setSfxMuted(!options.isSfxMuted());
        if (options.isSfxMuted()) {
          muteImage.setColor(Drawing.QUIT_COLOR);
        } else {
          muteImage.setColor(Drawing.START_COLOR);
        }
      }
    });

    return panel;
  }

  private Button generateBackToMenuButton() {
    Button btm = new Button(engine, new Vector2D(cornerButtonOffset, cornerButtonOffset),
        new Vector2D(cornerButtonSize, cornerButtonSize));
    btm.setColor(Drawing.TRANSPARENT_COLOR);

    btm.setAction(new ButtonAction() {
      @Override
      public void execute() {
        sm.loadScene(new MainMenu(engine));
      }
    });

    Image btmImage = new Image(engine, new Vector2D(), true, 1, 0, Drawing.ARROW);
    btmImage.setColor(Drawing.QUIT_COLOR);

    btm.addElement(btmImage, new Vector2D());

    return btm;
  }

}
