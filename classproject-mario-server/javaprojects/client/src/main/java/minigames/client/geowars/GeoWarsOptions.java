package minigames.client.geowars;

import minigames.client.geowars.util.Sound;

public class GeoWarsOptions {

  private int playerSpeed;
  private int musicVolume;
  private int sfxVolume;
  private boolean musicMuted;
  private boolean sfxMuted;

  /**
   * Default constructor for GeoWarsOptions.
   * Sets all options to default values.
   * These are the values that the game will start up with.
   */
  public GeoWarsOptions() {
    this.playerSpeed = 3;
    this.musicVolume = 10;
    this.sfxVolume = 10;
    this.musicMuted = false;
    this.sfxMuted = false;
  }

  /**
   * Getter for playerSpeed.
   * 
   * @return the player's speed
   */
  public int getPlayerSpeed() {
    return playerSpeed;
  }

  /**
   * Increments the player's speed by 1.
   * The player's speed can be between 1 and 5.
   */
  public void incrementPlayerSpeed() {
    if (this.playerSpeed < 5) {
      this.playerSpeed++;
    }
  }

  /**
   * Decrements the player's speed by 1.
   * The player's speed can be between 1 and 5.
   */
  public void decrementPlayerSpeed() {
    if (this.playerSpeed > 1) {
      this.playerSpeed--;
    }
  }

  /**
   * Getter for musicVolume.
   * 
   * @return the music volume
   */
  public int getMusicVolume() {
    return musicVolume;
  }

  /**
   * Sets the music volume.
   * Music volume can be between 0 and 10.
   * 
   * @param musicVolume the new music volume
   */
  public void setMusicVolume(int musicVolume) {
    if (musicVolume >= 0 && musicVolume <= 10) {
      this.musicVolume = musicVolume;
      if (!isMusicMuted()){
        Sound.getInstance().setMusicVolume((double) musicVolume * 0.1); //Updates the value musicVolume value to a range of 0.0 to 1.0
      }
    }
  }

  /**
   * Getter for sfxVolume.
   * 
   * @return the sfx volume
   */
  public int getSfxVolume() {
    return sfxVolume;
  }

  /**
   * Sets the sfx volume.
   * Sfx volume can be between 0 and 10.
   * 
   * @param sfxVolume the new sfx volume
   */
  public void setSfxVolume(int sfxVolume) {
    if (sfxVolume >= 0 && sfxVolume <= 10) {
      this.sfxVolume = sfxVolume;
      if (!isSfxMuted()){
        Sound.getInstance().setSfxVolume((double) sfxVolume * 0.1); //Updates the value sfxVolume value to a range of 0.0 to 1.0
      }
    }
  }

  /**
   * Getter for musicMuted.
   * 
   * @return true if music is muted, false otherwise
   */
  public boolean isMusicMuted() {
    return musicMuted;
  }

  /**
   * Setter for musicMuted.
   * 
   * @param musicMuted the new value for musicMuted
   */
  public void setMusicMuted(boolean musicMuted) {
    this.musicMuted = musicMuted;
    if (musicMuted){
      Sound.getInstance().setMusicVolume((double) 0.0); //Updates the volume to be 0 at the Sound clip level.
    } else {
      Sound.getInstance().setMusicVolume((double) getMusicVolume() * 0.1); //Updates the value sfxVolume value to a range of 0.0 to 1.0
    }
  }

  /**
   * Getter for sfxMuted.
   * 
   * @return true if sfx is muted, false otherwise
   */
  public boolean isSfxMuted() {
    return sfxMuted;
  }

  /**
   * Setter for sfxMuted.
   * 
   * @param sfxMuted the new value for sfxMuted
   */
  public void setSfxMuted(boolean sfxMuted) {
    this.sfxMuted = sfxMuted;
    if (sfxMuted){
      Sound.getInstance().setSfxVolume((double) 0.0); //Updates the volume to be 0 at the Sound clip level.
    } else {
      Sound.getInstance().setSfxVolume((double) getSfxVolume() * 0.1); //Updates the value sfxVolume value to a range of 0.0 to 1.0
    }
    
  }

}
