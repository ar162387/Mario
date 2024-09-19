package minigames.server.mario;

/**
 * The GameState enum represents the various states that the game can be in.
 */
public enum GameState {
    MAIN_MENU,
    PLAYING,
    PAUSED, // TODO: triggered by space bar?
    GAME_OVER
}