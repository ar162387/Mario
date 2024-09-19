package minigames.client.RodentsRevenge;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.*;

/** Sound for the RodentsRevenge game
 * Cowritten by Tab Invaders and .WAV Dwellers
 *
 * Contributors: Arabella Wain
 *              Lixang Li lli32@myune.edu.au
 *              Charles Cavanagh
 *
 * Example usage: - Li
 * // Play a track
 *  Sound.getInstance().play(Sound.Type.GRUNT);
 *  // Play a track, then loop
 *  Sound.getInstance().playMusic(Sound.Type.BGM);
 *  // Play a single effect
 *  Sound.getInstance().playSFX(Soung.Type.CLICK);
 *  //Play random enemy sound
 *  Sound.getInstance().playRandomEnemySound();
 *  // Play BGM
 *  Sound.getInstance().playMusic(Sound.Type.BGM);
 *  // Stop BGM
 *  Sound.getInstance().stopMusic();
 *  // Clear cache of clips
 *  Sound.getInstance().releaseAllClips();
 */
public class Sound {
    private static Sound instance ; //Singleton design pattern
    private AudioInputStream ais;
    
    public enum Type { //Added enum section for easy access
    	CAT, CHEESE, GAME, MENU, SELECTION //Change these to suit your sound files
    }
    
 // Added private method to return URL for each ENUM

    /**
     * Switch for all the sound files
     * @param type
     * @return resource
     */
    private URL getURLForSoundType(Type type) {
        return switch (type) { // Add paths to sound file here. Change enums to match Type
        case CAT ->  getClass().getResource("/RodentsRevenge/Sound/rrCat.wav");
        case CHEESE -> getClass().getResource("/RodentsRevenge/Sound/rrCheese.wav");
        case GAME -> getClass().getResource("/RodentsRevenge/Sound/rrInGame.wav");
        case MENU -> getClass().getResource("/RodentsRevenge/Sound/rrMenu.wav");
        case SELECTION -> getClass().getResource("/RodentsRevenge/Sound/selection.wav");
        };
    }

    HashMap<Type,Optional<Clip>> clipHashMap = createClipMap(); // Edit: Cache all the clips

    HashSet<Clip> loops = new HashSet<Clip>();

    private Sound() {
        // Prevent instantiation of sound class SINGLETON
    }

    public static Sound getInstance() {
        if (instance == null) {
            instance = new Sound();
        }
        return instance;
    }

    /**
     * Sets the file from URL
     * @param type of sound (Grunt, BGM)
     * @return clip
     */
    public Optional<Clip> setFile(Type type) { // Edit: Returns a Clip, so we can store them and close them together

        try {
             ais = AudioSystem.getAudioInputStream(getURLForSoundType(type));  //Changed to take sound type
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return Optional.of(clip);
        } catch(Exception e) {
            System.out.println("Error in setting file: " + e.getMessage()); // Added error message
            return Optional.empty();
        }
    }

    /**
     * Creates a cache of clips for the game to access
     * @return
     */
    HashMap<Type,Optional<Clip>> createClipMap() {
        HashMap<Type,Optional<Clip>> clipHashMap = new HashMap<>();
        Arrays.stream(Type.values()).forEach((type -> clipHashMap.put(type, setFile(type))));
        return clipHashMap;
    }

    /**
     * Play clip using optional management
     * @param type
     */
    public void play(Type type) { // Edit to take type
        clipHashMap.get(type).ifPresent(clip -> {
            clip.setMicrosecondPosition(0); // Rewind
            clip.start(); // play
        });
    }

    /**
     * Loop clip using optional
     * Add looping clip to set of loops
     * @param type
     */
    public void loop(Type type) { // Edit to take type
        clipHashMap.get(type).ifPresent(clip -> {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            loops.add(clip);
        });
    }

    /**
     * Stop clip using optional
     * @param type
     */
    public void stop(Type type) { // Edit to take type
        clipHashMap.get(type).ifPresent(clip -> {
            if (clip.isRunning()) { // Edit: check running
                clip.stop();
            }
        });


    }

    /**
     * Finally release all clips in cache to free memory for other games
     */
    public final void releaseAllClips() {
        clipHashMap.values().forEach(clip -> {
            clip.ifPresent(DataLine::stop);
            clip.ifPresent(DataLine::close); // Close the clip to release resources
        });
        try {
            ais.close(); // Close the input stream
        } catch (IOException e) {
        	
        }
    }

    /** Will only play once
     *
     * @param type
     */
    public void playSFX(Type type) {
        play(type);
    }
    public void playMusic(Type type) { //Change to take sound type - Li
        stopMusic(); // stops all looping clips
        play(type);
        loop(type);
        //System.out.println("Playing music = " + type);
    }

    // Not sure when you'd like to use this so I'll just leave it here for now - Arabella Wain

    /**
     * Stops all looping music clips and removes them from the HashSet
     */
    public final void stopMusic() {
        loops.forEach(DataLine::stop);
//        loops.forEach(clip -> clip.setFramePosition(0)); //Rewind sound
        loops.clear();
    }
}
