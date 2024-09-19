package minigames.client.snake;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

/**
 * Sound manager for the Snake game.
 */
public class Sound {

    // Enum for various sound types in Snake game
    public enum Type {
        EAT_APPLE, GAME_OVER, BACKGROUND_MUSIC, HIT, POWER_UP, MENU
    }

    private static Sound instance;  // Singleton instance
    private AudioInputStream ais;
    private HashMap<Type, Optional<Clip>> clipCache = new HashMap<>();  // Cache clips
    private HashSet<Clip> loopingClips = new HashSet<>();  // Looping sounds

    // Private constructor for Singleton
    private Sound() {
        loadAllSounds();  // Load all sound files into cache
    }

    // Get the singleton instance
    public static Sound getInstance() {
        if (instance == null) {
            instance = new Sound();
        }
        return instance;
    }

    // Load all sound files and cache them
    private void loadAllSounds() {
        for (Type type : Type.values()) {
            clipCache.put(type, loadSoundClip(type));
        }
    }

    // Load a sound clip based on its type
    private Optional<Clip> loadSoundClip(Type type) {
        try {
            URL soundURL = getSoundURL(type);  // Get URL for the sound type
            ais = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            System.out.println("Loaded sound for type: " + type);
            return Optional.of(clip);
        } catch (Exception e) {
            System.err.println("Failed to load sound for type: " + type + " - " + e.getMessage());
            return Optional.empty();
        }
    }

    // Play a sound effect
    public void play(Type type) {
        clipCache.get(type).ifPresent(clip -> {
            clip.setMicrosecondPosition(0);  // Rewind the clip
            clip.start();  // Play the clip
        });
    }

    // Loop a sound effect
    public void loop(Type type) {
        clipCache.get(type).ifPresent(clip -> {
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // Loop continuously
            loopingClips.add(clip);  // Add to looping clips set
        });
    }

    // Stop looping sounds
    public void stopLoop(Type type) {
        clipCache.get(type).ifPresent(clip -> {
            clip.stop();  // Stop the clip
            loopingClips.remove(clip);  // Remove from looping set
        });
    }

    // Stop all looping sounds
    public void stopAllLoops() {
        for (Clip clip : loopingClips) {
            clip.stop();
        }
        loopingClips.clear();
    }

    // Release all resources
    public void releaseAllClips() {
        clipCache.values().forEach(clip -> {
            clip.ifPresent(DataLine::close);  // Close all clips
        });
        try {
            if (ais != null) {
                ais.close();  // Close the audio stream
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the URL of the sound file for each type
    private URL getSoundURL(Type type) {
        switch (type) {
            case EAT_APPLE:
                return getClass().getResource("/sound/snake/snakeEat.wav");
            case GAME_OVER:
                return getClass().getResource("/sound/snake/snakeGameover.wav");
            case BACKGROUND_MUSIC:
                return getClass().getResource("/sound/snake/snakeInGame.wav");
            case HIT:
                return getClass().getResource("/sound/snake/snakeHit.wav");
            case MENU:
                return getClass().getResource("/sound/snake/snakeMenu.wav");
            case POWER_UP:
                return getClass().getResource("/sound/snake/snakePowerup.wav");

            default:
                throw new IllegalArgumentException("Unknown sound type: " + type);
        }
    }
}
