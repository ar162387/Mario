package minigames.client.profile;

import javax.swing.JOptionPane;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import minigames.client.MinigameNetworkClient;

public class UserProfileController {
    
    private static UserProfile userProfile;
    private UserProfileView userProfileView;
    private Vertx vertx;
    private MinigameNetworkClient networkClient;


    // Constructor to inject the model and view dependencies
    public UserProfileController(MinigameNetworkClient networkClient) {
        if (userProfile == null) { // Check if the model has already been initialized
            userProfile = new UserProfile(networkClient);
        }
        this.vertx = Vertx.vertx();
        this.networkClient = networkClient;
    }

    // Fetches own player profile from "profile" button on main menu in client
    public Future<UserProfileView> fetchUserProfile() {
        return userProfile.fetchUserProfile(networkClient).map(jsonResponse -> {

            userProfile.setFirstName(jsonResponse.getString("first_name"));
            userProfile.setLastName(jsonResponse.getString("last_name"));
            userProfile.setUserName(jsonResponse.getString("username"));
            userProfile.setEmail(jsonResponse.getString("email"));
            userProfile.setDOB(jsonResponse.getString("date_of_birth"));
            userProfile.setAboutMe(jsonResponse.getString("bio"));
            userProfile.setFavoriteGame(jsonResponse.getString("favorite_game"));
            userProfile.setTotalPlayTime(jsonResponse.getString("total_play_time_minutes"));
    
            // Initialize the UserProfileView with the JSON Object
            this.userProfileView = new UserProfileView(jsonResponse);
            // Return the initialized view
            return this.userProfileView;
        });
    }

     // Fetches other player profile from search bar on main menu in client
    public Future<UserProfileView> fetchOtherUserProfile(String name) {
        return userProfile.fetchOtherUserProfile(networkClient, name).map(jsonResponse -> {

            userProfile.setFirstName(jsonResponse.getString("first_name"));
            userProfile.setLastName(jsonResponse.getString("last_name"));
            userProfile.setUserName(jsonResponse.getString("username"));
            userProfile.setEmail(jsonResponse.getString("email"));
            userProfile.setDOB(jsonResponse.getString("date_of_birth"));
            userProfile.setAboutMe(jsonResponse.getString("bio"));
            userProfile.setFavoriteGame(jsonResponse.getString("favorite_game"));
            userProfile.setTotalPlayTime(jsonResponse.getString("total_play_time_minutes"));
    
            // Initialize the UserProfileView with the JSON Object
            this.userProfileView = new UserProfileView(jsonResponse);
            // Return the initialized view
            return this.userProfileView;
        });
    }


    /**
     * make a Profile UI to sit on frame called in minigameNetworkClientWindow()
     * @return
     */
    public UserProfileView getView() {
        return this.userProfileView;
    }
  
}
    

