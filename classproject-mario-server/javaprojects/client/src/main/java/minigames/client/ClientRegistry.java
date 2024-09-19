package minigames.client;

import java.util.HashMap;

/**
 * Holds information on which games are available and can be served up to clients.
 */
public class ClientRegistry {

    private HashMap<String, GameClient> gameClients = new HashMap<>();
    private HashMap<String, Boolean> forWebSocket = new HashMap<>();

    /**
     * Called by your GameServer to register it as being available to play
     * @param name 
     * @param gs
     */
    public void registerGameClient(String name, GameClient gc, boolean isForWebSockets) {

        gameClients.put(name, gc);
        forWebSocket.put(name, isForWebSockets);
    }

    public void registerGameClient(String name, GameClient gc) { // make isForWebSockets optional
        gameClients.put(name, gc);
        forWebSocket.put(name, false);
    }

    public GameClient getGameClient(String name) {
        return gameClients.get(name);
    }

    public boolean isForWebSockets(String name){
        return forWebSocket.get(name);
    }

}
