package minigames.server.api.user;

import io.vertx.core.json.JsonObject;

public class AppUser {
    private final Long id;
    private final String username;
    private final UserProperties properties;

    public AppUser(Long id, String username, UserProperties properties) {
        this.id = id;
        this.username = username;
        this.properties = properties;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserProperties getProperties() {
        return properties;
    }

    public JsonObject toJson() {
        return new JsonObject()
            .put("id", id)
            .put("username", username)
            .put("properties", properties.toJson());
    }
}