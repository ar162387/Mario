// package minigames.server.auth;

// import io.vertx.core.AsyncResult;
// import io.vertx.core.Handler;
// import io.vertx.ext.auth.User;

// import org.mockito.ArgumentCaptor;
// import static org.mockito.Mockito.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// public class AuthenticationTests {

//     private BasicAuthProvider authProvider;
//     private BasicCredentials validCredentials;
//     private BasicCredentials invalidPasswordCredentials;
//     private BasicCredentials invalidUsernameCredentials;

//     @BeforeEach
//     void setUp() {
//         validCredentials = new BasicCredentials("admin", "admin123");
        
//         authProvider = spy(new BasicAuthProvider());
//         doReturn(validCredentials).when(authProvider).loadMockCredentials();
//         System.out.println("Expected hashed password: " + validCredentials.getPassword());

//         invalidPasswordCredentials = new BasicCredentials("admin", "wrongpassword");
//         invalidUsernameCredentials = new BasicCredentials("wronguser", "admin123");
//     }

//     @Test
//     @DisplayName("Authenticate with valid credentials")
//     void authenticateWithValidCredentials() {
//         Handler<AsyncResult<User>> resultHandler = mock(Handler.class);
//         ArgumentCaptor<AsyncResult<User>> captor = ArgumentCaptor.forClass(AsyncResult.class);

//         authProvider.authenticate(validCredentials, resultHandler);

//         verify(resultHandler).handle(captor.capture());
//         assertTrue(captor.getValue().succeeded());
//         assertNotNull(captor.getValue().result());
//     }

//     @Test
//     @DisplayName("Verify Hashing works, and returns repeatable results")
//     void hashPasswords() {
//         String testPassword = "password123";
//         String hashedPassword = HashingService.hashPassword(testPassword);
//         assertTrue(testPassword != hashedPassword);
//         assertTrue(HashingService.comparePassword(testPassword, hashedPassword));
//     }

//     @Test
//     @DisplayName("Authentication fails with invalid password")
//     void authenticateWithInvalidPassword() {
//         Handler<AsyncResult<User>> resultHandler = mock(Handler.class);
//         ArgumentCaptor<AsyncResult<User>> captor = ArgumentCaptor.forClass(AsyncResult.class);

//         authProvider.authenticate(invalidPasswordCredentials, resultHandler);

//         verify(resultHandler).handle(captor.capture());
//         assertTrue(captor.getValue().failed());
//         assertNull(captor.getValue().result());
//     }

//     @Test
//     @DisplayName("Authentication fails with invalid password")
//     void authenticateWithInvalidUsername() {
//         Handler<AsyncResult<User>> resultHandler = mock(Handler.class);
//         ArgumentCaptor<AsyncResult<User>> captor = ArgumentCaptor.forClass(AsyncResult.class);
        
//         authProvider.authenticate(invalidUsernameCredentials, resultHandler);

//         verify(resultHandler).handle(captor.capture());
//         assertTrue(captor.getValue().failed());
//         assertNull(captor.getValue().result());
//     }
// }
