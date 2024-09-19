package minigames.server.api.auth.guice;
// package minigames.server.auth.guice;

// import com.google.inject.AbstractModule;
// import com.google.inject.Provides;
// import com.google.inject.Singleton;
// import io.vertx.core.Vertx;
// import io.vertx.ext.auth.authentication.AuthenticationProvider;
// import io.vertx.ext.auth.jwt.JWTAuth;
// import io.vertx.ext.auth.jwt.JWTAuthOptions;
// import io.vertx.ext.auth.PubSecKeyOptions;
// import minigames.server.MinigameNetworkServer;
// import minigames.server.auth.AuthProvider;
// import minigames.server.user.UserService;
// import minigames.server.user.UserServiceImpl;
// import minigames.server.user.UserRepository;
// import minigames.server.user.MockUserRepository;

// public class MinigameServerModule extends AbstractModule {

//     private final Vertx vertx;

//     public MinigameServerModule(Vertx vertx) {
//         this.vertx = vertx;
//     }

//     @Override
//     protected void configure() {
//         bind(AuthenticationProvider.class).to(AuthProvider.class).in(Singleton.class);
//         bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
//         bind(UserRepository.class).to(MockUserRepository.class).in(Singleton.class);
//     }

//     @Provides
//     @Singleton
//     Vertx provideVertx() {
//         return vertx;
//     }

//     @Provides
//     @Singleton
//     JWTAuth provideJWTAuth(Vertx vertx) {
//         JWTAuthOptions config = new JWTAuthOptions()
//             .addPubSecKey(new PubSecKeyOptions()
//                 .setAlgorithm("HS256")
//                 .setBuffer("totally-secret-key"));
//         return JWTAuth.create(vertx, config);
//     }

//     @Provides
//     @Singleton
//     MinigameNetworkServer provideMinigameNetworkServer(Vertx vertx, AuthenticationProvider authProvider, UserService userService) {
//         return new MinigameNetworkServer(vertx, authProvider, userService);
//     }
// }