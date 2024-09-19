package minigames.server.api.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import minigames.server.api.user.UserProperty;
import minigames.server.database.repositories.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class AuthProvider implements AuthenticationProvider {
    
    private static final Logger logger = LogManager.getLogger(AuthProvider.class);
    private final UserRepository userRepository;

    @Inject
    public AuthProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("AuthProvider initialised with {}", userRepository.getClass().getSimpleName());
    }

    @Override
    public void authenticate(Credentials credentials, Handler<AsyncResult<User>> resultHandler) {
        if (!(credentials instanceof BasicCredentials)) {
            resultHandler.handle(Future.failedFuture("Invalid credentials type"));
            return;
        }
        
        BasicCredentials basicCredentials = (BasicCredentials) credentials;
        authenticateBasic(basicCredentials, resultHandler);
    }

    private void authenticateBasic(BasicCredentials basicCredentials, Handler<AsyncResult<User>> resultHandler) {
        logger.info("Authenticating user: {}", basicCredentials.getUsername());
        
        String inputUsername = basicCredentials.getUsername();
        String password = basicCredentials.getPassword();

        userRepository.userExists(inputUsername).compose(exists -> {
            if (!exists) {
                logger.info("Authentication failed: Invalid username");
                return Future.failedFuture("Invalid username or password");
            }
            return userRepository.getHashedPassword(inputUsername);
        }).compose(storedHash -> {
            boolean passwordMatches = HashingService.comparePassword(password, storedHash);
            logger.info("Password matches: {}", passwordMatches);

            if (passwordMatches) {
                logger.info("Authentication successful");
                return userRepository.getUserData(inputUsername);
            } else {
                logger.info("Authentication failed: Invalid password");
                return Future.failedFuture("Invalid username or password");
            }
        }).compose(appUser -> {
            Long id = appUser.getId();
            String username = appUser.getUsername();
            String hashedPassword = (String) appUser.getProperties().getProperty(UserProperty.HASHED_PASSWORD);
            String email = (String) appUser.getProperties().getProperty(UserProperty.EMAIL);
            JsonObject attributes = appUser.getProperties().toJson();

            AuthUser authUser = new AuthUser(id, username, hashedPassword, email, attributes);
            return Future.succeededFuture(authUser);
        }).onComplete(ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(Future.succeededFuture((User) ar.result()));
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String username = authInfo.getString("username");
        String password = authInfo.getString("password");
        BasicCredentials basicCredentials = new BasicCredentials(username, password);
        authenticate(basicCredentials, resultHandler);
    }
}