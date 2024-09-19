package minigames.server.api.user;

public enum UserProperty {
    EMAIL,
    HASHED_PASSWORD,
    CREATED_AT,
    LAST_LOGIN,
    PROFILE,
    PLAY_TIME,
    ACHIEVEMENTS,
    SCORES;

    @Override
    public String toString() {
        return switch (this) {
            case EMAIL -> "Email";
            case HASHED_PASSWORD -> "Password";
            case CREATED_AT -> "Created At";
            case LAST_LOGIN -> "Last Login";
            case PROFILE -> "Profile";
            case PLAY_TIME -> "Play Time";
            case ACHIEVEMENTS -> "Achievements";
            case SCORES -> "Scores";
        };
    }

    public String toSnakeCase() {
        return this.name().toLowerCase().replace('_', ' ');
    }
}