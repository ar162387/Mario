package minigames.server.api.user;

import io.vertx.core.json.JsonObject;

import java.util.EnumMap;
import java.util.Map;

public class UserProperties {
    private final Map<UserProperty, Object> properties;

    public UserProperties(JsonObject json) {
        this.properties = new EnumMap<>(UserProperty.class);
        for (UserProperty property : UserProperty.values()) {
            if (json.containsKey(property.name().toLowerCase())) {
                properties.put(property, json.getValue(property.name().toLowerCase()));
            }
        }
    }

    public Object getProperty(UserProperty property) {
        return properties.get(property);
    }

    public void setProperty(UserProperty property, Object value) {
        properties.put(property, value);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        for (Map.Entry<UserProperty, Object> entry : properties.entrySet()) {
            json.put(entry.getKey().name().toLowerCase(), entry.getValue());
        }
        return json;
    }
}