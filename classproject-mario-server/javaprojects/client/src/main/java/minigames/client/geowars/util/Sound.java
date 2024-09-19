package minigames.client.geowars.util;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.sound.sampled.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sound {
    private static final Logger logger = LogManager.getLogger(Sound.class);
    private static Sound instance; //Singleton design pattern
    private AudioInputStream ais;
    public enum Type { //enum that relates to a specifc sound
        BGM, DEATH, MENU, SHOOT, SPENEMY, WAVE
    }
    HashMap<Type,Optional<Clip>> clipHashMap = createClipMap(); // Edit: Cache all the clips
    HashSet<Clip> loops = new HashSet<Clip>();

    //the locally stored volume values, valid values range between 0.0 and 1.0. E.g. 0.5 = 50% volume.
    private double sfxVolume = 1.0; //Max volume 100% by default
    private double musicVolume = 1.0; //Max volume 100% by default


    private Sound() {} // Prevent instantiation of sound class SINGLETON

    /**
     * Switch for all the sound files
     * @param type
     * @return resource
     */
    private URL getURLForSoundType(Type type) {
        return switch (type) { // Add paths to sound file here. Change enums to match Type
        case BGM ->  getClass().getResource("/sound/geowars/geowarsBGM.wav");
        case DEATH -> getClass().getResource("/sound/geowars/geowarsDie.wav");
        case MENU -> getClass().getResource("/sound/geowars/geowarsMenu.wav");
        case SHOOT -> getClass().getResource("/sound/geowars/geowarsShoot.wav");
        case SPENEMY -> getClass().getResource("/sound/geowars/geowarsSpecialSpawn.wav");
        case WAVE ->  getClass().getResource("/sound/geowars/geowarsWaveSpawn.wav");
        };
    }

    /**
     * Retrieves the sound instance referenced.
     * @return the sound instance in use.
     */
    public static Sound getInstance() {
        if (instance == null) {
            instance = new Sound();
        }
        return instance;
    }

    /**
     * Sets the file from URL
     * @param type of sound (BGM)
     * @return clip
     */
    public Optional<Clip> setFile(Type type) { //Returns a Clip, so we can store them and close them together
        try {
            ais = AudioSystem.getAudioInputStream(getURLForSoundType(type)); //Changed to take sound type
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            
            // Sets the volumes of the clip to 100% as part of initialisation.
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20.0f * (float) Math.log10(1.0));

            return Optional.of(clip);
        } catch(Exception e) {
            logger.error("Error in setting file: " + e.getMessage()); // Added error message to logs
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
    public void loop(Type type) {
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
        	logger.error("Error in releasing sound clips: " + e.getMessage());
        }
    }

    /** 
     * Will only play the SFX once
     * @param type
     */
    public void playSFX(Type type) {
        play(type);
    }

    /** 
     * Will play the SFX on a loop
     * @param type
     */
    public void playLoopSFX(Type type) {
        stop(type);
        play(type);
        loop(type);
    }

    /**
     * Plays music style clips on a loop.
     * @param type
     */
    public void playMusic(Type type) {
        clipHashMap.get(type).ifPresent(clip -> {
            if (!clip.isRunning()) { // Making sure the music clip desired isn't running already.
                stopMusic(); // stops all looping clips
                play(type);
                loop(type);
            }
        });
    }

    /**
     * Stops all looping music clips and removes them from the HashSet
     */
    public final void stopMusic() {
        loops.forEach(DataLine::stop);
        loops.clear();
    }

    /**
     * Gets the current SFX volume in the sound class
     * @return
     */
    public double getSfxVolume(){
        return sfxVolume;
    }

    /**
     * Gets the current music volume in the sound class
     * @return
     */
    public double getMusicVolume(){
        return musicVolume;
    }

    /**
     * Triggers the SFX clip volumes to be updated to
     * the provided new volume.
     * @param newVolume - value between 0.0 and 1.0.
     */
    public void setSfxVolume(double newVolume){
        sfxVolume = newVolume;
        updateAllVolumes();
    }

    /**
     * Triggers the music clip volumes to be updated to
     * the provided new volume.
     * @param newVolume - value between 0.0 and 1.0.
     */
    public void setMusicVolume(double newVolume){
        musicVolume = newVolume;
        updateAllVolumes();
    }

    /**
     * Updates all the volumes of all the clips in the instance.
     * Filters clips based on if they are music of SFX as volumes
     * are handled independantly.
     */
    public void updateAllVolumes(){
        clipHashMap.keySet().forEach(type -> {
            if (type == Type.BGM || type == Type.MENU) {
                updateClipVolume(type, getMusicVolume());
            } else {
                updateClipVolume(type, getSfxVolume());
            }
        });
    }

    /**
     * Update a specific clip's volume
     * @param type - clips enum type to identify which is requried to update.
     * @param newVolume - value between 0.0 and 1.0.
     */
    public void updateClipVolume(Type type, double newVolume){
        if (newVolume < 0 || newVolume > 1) { //volume must be in the range of 0 to 1, where 0 is 0% and 1 is 100%.
            throw new IllegalArgumentException("Volume not valid, must be between 0.0 and 1.0: " + newVolume);
        }
        clipHashMap.get(type).ifPresent(clip -> {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(newVolume));
        });
    }
}
