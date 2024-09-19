package minigames.server.api.profile;

import java.util.EnumMap;
import java.util.Map;

import io.vertx.core.json.JsonObject;

public class Profile {
    public enum Field {
        FIRST_NAME,
        LAST_NAME,
        DATE_OF_BIRTH,
        BIO,
        FAVORITE_GAME,
        TOTAL_PLAY_TIME_MINUTES
    }

    private final Map<Field, String> data;

    // default constructor
    public Profile() {
        this.data = new EnumMap<>(Field.class);
    }

    // constructor from JsonObject
    public Profile(JsonObject json) {
        this();
        for (Field field : Field.values()) {
            String value = json.getString(toSnakeCase(field.name()));
            if (value != null) {
                set(field, value);
            }
        }
    }

    // getter
    public String get(Field field) {
        return data.get(field);
    }

    // setter
    public void set(Field field, String value) {
        data.put(field, value);
    }

    // convert ProfileData to JsonObject
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        for (Map.Entry<Field, String> entry : data.entrySet()) {
            json.put(toSnakeCase(entry.getKey().name()), entry.getValue());
        }
        return json;
    }
    private String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}