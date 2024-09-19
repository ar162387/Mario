package minigames.client.bomberman;

import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.util.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.*;
import javax.xml.crypto.Data;

/** Sound for the Bomberman game
 * Cowritten by Tab Invaders and .WAV Dwellers
 *
 * Contributors: Arabella Wain
 *              Lixang Li lli32@myune.edu.au
 *
 * Example usage: - Li
 * // Play a sound
 *  Sound.getInstance.play(Sound.Type.GRUNT);
 *  //Play random enemy sound
 *  Sound.getInstance.playRandomEnemySound();
 *  // Play BGM
 *  Sound.getInstance.playMusic(Sound.Type.BGM);
 *  // Stop BGM
 *  Sound.getInstance.stopMusic();
 *  // Clear cache of clips
 *  Sound.getInstance.releaseAllClips();
 */
public class Sound {
    private static Sound instance ; //Singleton design pattern
    private AudioInputStream ais;
//    URL soundURL[] = new URL[30];
    public enum Type { //Added enum section for easy access
        MENU, BGM, GRUNT1, GRUNT2, GRUNT3, GRUNT4, GRUNT5, BUMP, EXPLOSION, LOSE, PLAYER_DEATH, TICKING, WIN
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
     * Used for mocking purposes only
     * @return the instance
     */
    public static Sound setInstance(Sound mockSound) {
        instance = mockSound;
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
            changeSoundVolume(clip, -6f);
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
            DebugManager.getInstance().logError(e.getMessage());;
        }
    }

    /** Will only play once
     *
     * @param type
     */
    public void playSFX(Type type) {
        play(type);
        DebugManager.getInstance().log("SFX = " + type);
    }

    /**
     * Stop current looping BGM
     * Plays the BGM music and loads it into a loop hashmap
     * This also reduces the original sound by 50%
     * @param type
     */
    public void playMusic(Type type) { //Change to take sound type - Li
        stopMusic(); // stops all looping clips
        play(type);
        loop(type);
        DebugManager.getInstance().log("Playing music = " + type);
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



    // Added private method to return URL for each ENUM

    /**
     * Switch for all the sound files
     * @param type
     * @return resource
     */
    private URL getURLForSoundType(Type type) {
        return switch (type) {
            case BGM ->  getClass().getResource("/sound/bomberman/backgroundmusic.wav");
            case GRUNT1 -> getClass().getResource("/sound/bomberman/grunt1.wav");
            case GRUNT2 -> getClass().getResource("/sound/bomberman/grunt2.wav");
            case GRUNT3 -> getClass().getResource("/sound/bomberman/grunt3.wav");
            case GRUNT4 -> getClass().getResource("/sound/bomberman/grunt4.wav");
            case GRUNT5 -> getClass().getResource("/sound/bomberman/grunt5.wav");
            case BUMP -> getClass().getResource("/sound/bomberman/bump.wav");
            case EXPLOSION -> getClass().getResource("/sound/bomberman/explosion.wav");
            case LOSE -> getClass().getResource("/sound/bomberman/lose.wav");
            case PLAYER_DEATH ->  getClass().getResource("/sound/bomberman/playerdeath.wav");
            case TICKING -> getClass().getResource("/sound/bomberman/ticking_3sec.wav"); // Edit: FIXED
            case WIN ->  getClass().getResource("/sound/bomberman/win.wav");
            case MENU -> getClass().getResource("/sound/bomberman/menu.wav");
        };
    }

    /**
     * Plays a random clip for enemy hits and deaths
     * @return Clip random enemy
     */
    public boolean playRandomEnemySound() {
        try {
            Type[] grunts = new Type[]{Type.GRUNT1, Type.GRUNT2, Type.GRUNT3, Type.GRUNT4, Type.GRUNT5};
            Random random = new Random();
            Integer randomIdx = random.nextInt(grunts.length);
            playSFX(grunts[randomIdx]);
            return true;
        } catch (Exception e) {
            DebugManager.getInstance().logError("Play random sound error: " + e.getMessage());
        }
        return  false;
    }

    /**
     * Changes the volume of the clip
     */
    private Clip changeSoundVolume(Clip clip, Float db){
        FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(db);
        return  clip;
    }
}