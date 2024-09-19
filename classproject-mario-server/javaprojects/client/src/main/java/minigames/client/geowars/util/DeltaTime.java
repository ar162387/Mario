package minigames.client.geowars.util;

import java.time.Duration;
import java.time.Instant;

/*
 * All classes that are using movement or anything else that happens over time should be making use
 * of this so they can find out how long it has been since the last update as that will not be
 * constant. All of these effects should be calculated in terms of X/second, and them multiplied by
 * deltaTime to get the correct value.
 */

public final class DeltaTime {
    private DeltaTime() {
    }

    private static Instant lastTime = Instant.now();
    private static double deltaTime = 0;

    private static Instant lastPaused = Instant.now();
    private static Duration levelTimePrePause = Duration.ZERO;
    private static double levelTime = 0;

    private static boolean paused = false;

    /**
     * Initiates deltaTime to being counting.
     */
    public static void startDeltaTime() {
        lastTime = Instant.now();
    }

    /**
     * Updates deltaTime based on how much time has passed.
     */
    public static void calcDeltaTime() {
        Duration dt = Duration.between(lastTime, Instant.now());
        lastTime = Instant.now();
        deltaTime = (double) dt.toMillis() / 1000;

        if (!paused) {
            dt = Duration.between(lastPaused, Instant.now());
            dt = dt.plus(levelTimePrePause);
            levelTime = (double) dt.toMillis() / 1000;

            // System.out.println("levelTime: " + levelTime);
        }
    }

    /**
     * Gets the current Delta Time converted to seconds
     * 
     * @return double containing the current deltaTime in seconds
     */
    public static double delta() {
        return deltaTime;
    }

    /**
     * Captures the current time as the time when the level started.
     */
    public static void startLevelTime() {
        lastPaused = Instant.now();
        levelTimePrePause = Duration.ZERO;
    }

    /**
     * Captures the current time as the time when the level was paused.
     */
    private static void pauseLevelTime() {
        if (!paused) {
            levelTimePrePause = levelTimePrePause.plus(Duration.between(lastPaused, Instant.now()));
            paused = true;
        }
    }

    private static void resumeLevelTime() {
        if (paused) {
            lastPaused = Instant.now();
            paused = false;
        }
    }

    public static void setPause(boolean pause) {
        if (pause) {
            pauseLevelTime();
        } else {
            resumeLevelTime();
        }
    }

    /**
     * Calculates the time that has passed since the level started.
     * 
     * @return double containing the time that has passed since the level started in
     *         seconds.
     */
    public static double levelTime() {
        return levelTime;
    }

    public static String levelTimeToString(double time) {
        int seconds = (int) time % 60;
        int minutes = Math.floorDiv((int) time, 60);
        int hundredths = (int) (time * 100) % 100;
        return String.format("%d:%02d.%02d", minutes, seconds, hundredths);
    }
}
