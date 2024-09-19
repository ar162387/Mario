package minigames.server.api.auth;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.CredentialValidationException;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;

public class BasicCredentials extends UsernamePasswordCredentials {

    public BasicCredentials(String username, String password) {
        super(username, password);
    }

    @Override
    public <V> void checkValid(V arg) throws CredentialValidationException {

        super.checkValid(arg);

        if (getPassword().length() < 8) {
            throw new CredentialValidationException("Password must be at least 8 characters long");
        }
    }

    public JsonObject toJson() {
        return super.toJson();
    }
}
