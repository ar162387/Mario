package minigames.server.api.auth;

import io.vertx.core.Future;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.*;
import io.vertx.ext.auth.authorization.Authorization;

public class AuthUser implements User {
    private Long id;
    private String username;
    private String hashedPassword;
    private String email;
    private JsonObject attributes;

    public AuthUser(Long id, String username, String hashedPassword, String email, JsonObject attributes) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.attributes = attributes;
    }

    public static AuthUser fromJson(JsonObject json) {
        return new AuthUser(
            json.getLong("id"),
            json.getString("username"),
            json.getString("hashedPassword"),
            json.getString("email"),
            json.getJsonObject("attributes")
        );
    }

    public void setAttributes(JsonObject jsonObject) {
        this.attributes = jsonObject;
    }

    @Override
    public JsonObject attributes() {
        return attributes != null ? attributes : new JsonObject();
    }

    @Override
    public User isAuthorized(Authorization authority, Handler<AsyncResult<Boolean>> resultHandler) {
        resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Authorisation not implemented")));
        return this;
    }

    @Override
    public JsonObject principal() {
        return new JsonObject()
            .put("id", id)
            .put("username", username)
            .put("hashedPassword", hashedPassword)
            .put("email", email);
    }

    @Override
    public User merge(User other) {
        throw new UnsupportedOperationException("Unimplemented method 'merge'");
    }

    public String getAttribute(String attrKey) {
        if (this.attributes != null && this.attributes.containsKey(attrKey)) {
            return this.attributes.getString(attrKey);
        }
        return "";
    }

    @Deprecated
    @Override

    public void setAuthProvider(io.vertx.ext.auth.AuthProvider authProvider) {
        throw new UnsupportedOperationException("Unimplemented method 'setAuthProvider'");
    }
}