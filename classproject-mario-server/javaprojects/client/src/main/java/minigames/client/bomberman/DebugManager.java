package minigames.client.bomberman;

/**
 * The DebugManager class is a singleton for handling debugging and logging
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 */
public class DebugManager {
    private static DebugManager instance;
    private boolean debugMode;

    /**
     * Constructor
     */
    private DebugManager() {
        debugMode = false;
    }

    /**
     * Get the instance of the DebugManager
     * @return instance
     */
    public static synchronized DebugManager getInstance() {
        if (instance == null) {
            instance = new DebugManager();
        }
        return instance;
    }

    /**
     * Check if debug mode is enabled
     * @return debugMode
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Toggle debug mode
     */
    public void toggleDebugMode() {
        debugMode = !debugMode;
        System.out.println("Debug mode: " + debugMode);
    }

    /**
     * Log a message
     * @param message
     */
    public void log(String message) {
        if (debugMode) {
            System.out.println(message);
        }
    }

    /**
     * Log an error message
     * @param message
     */
    public void logError(String message) {
        if (debugMode) {
            System.err.println(message);
        }
    }
}


