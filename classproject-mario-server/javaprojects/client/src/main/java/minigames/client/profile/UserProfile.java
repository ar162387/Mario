package minigames.client.profile;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import minigames.client.MinigameNetworkClient;


public class UserProfile {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(UserProfile.class);


    
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String dob;
    private String aboutMe;
    private String favGame;
    private String totalPlayTime;
    private Future<JsonObject> userData;
    private MinigameNetworkClient networkClient;

    // Constructor to inject the network client dependency
    public UserProfile(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;
    }


    public Future<JsonObject> fetchUserProfile(MinigameNetworkClient networkClient) {
        // TO DO: make this return networkClient.getCurrentUserProfile(); when auth is fixed.

        // getUserPrincipal() - requires logged in
        // getUserDetails(String username) - requires logged in
        // getCurrentUserProfile() - requires logged in
        // getUserProfile(String username) - login NOT REQUIRED

        
        // JSON objects to be stitched together
        Future<JsonObject> userDetailsFuture = networkClient.getUserDetails("jane_smith");
        Future<JsonObject> userProfileFuture = networkClient.getUserProfile("jane_smith");

        // Stitch the JSON objects together
        userData = userDetailsFuture.compose(userDetails -> {
            // Merge the user details and user profile JSON objects
            userDetails.mergeIn(userProfileFuture.result());
            // Return the merged JSON object
            //System.out.println("User Details JSON inside fetchUserProfile(): " + userDetails.encodePrettily());
            logger.info("User Details JSON inside fetchUserProfile(): " + userDetails.encodePrettily());
            //return Future.succeededFuture(userDetails);
            return networkClient.getUserProfile("jane_smith");
        });

        //return networkClient.getCurrentUserProfile(); // This returns profile of User when looged in
        return networkClient.getUserProfile("john_doe");
        /*
         * {
      "username": "john_doe",
      "password": "password123",
      "email": "john@example.com",
      "profile": {
        "first_name": "John",
        "last_name": "Doe",
        "date_of_birth": "1990-01-01",
        "bio": "Gamer extraordinaire",
        "favorite_game": "Chess",
        "total_play_time_minutes": "1200"
      }
         */

    }

    public Future<JsonObject> fetchOtherUserProfile(MinigameNetworkClient networkClient, String name) {
        // TO DO: make this return networkClient.getCurrentUserProfile(); when auth is fixed.
        return networkClient.getUserProfile(name);
    }


    // make a setter for ewach getter
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setDOB(String dob) {
        this.dob = dob;
    }
    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
    public void setFavoriteGame(String favGame) {
        this.favGame = favGame;
    }
    public void setTotalPlayTime(String totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }

    
    public String getEmail() {
        String email = "";
        try {
            email = ((JsonObject) userData).getString("email");
        } catch(Exception e){
            System.out.println("email data is null");
        }
        return email;
    }

    public String getUsername() {
        String uName = "";
        try {
            uName = ((JsonObject) userData).getString("username");
        } catch(Exception e){
            System.out.println("username data is null");
        }
        return uName;
    }

    public String getFirstName() {
        // make a try catch for null data
        String firstName = "";
        try {
            firstName = ((JsonObject) userData).getString("firstName");

        } catch (Exception e) {
            System.out.println("First Name Data is null");

        }
        return firstName;
    }

    public String getLastName() {
        String lastName = "";
        try {
            lastName = ((JsonObject) userData).getString("lastName");

        } catch (Exception e) {
            System.out.println("Last Name Data is null");

        }
        return lastName;
    }

    public String getDob() {
        String dob = "";
        try {
            dob = ((JsonObject) userData).getString("dob");

        } catch (Exception e) {
            System.out.println("Date of Birth Data is null");

        }
        return dob;
    }

    public String getAboutMe() {
        String aboutMe = "";
        try {
            aboutMe = ((JsonObject) userData).getString("aboutMe");

        } catch (Exception e) {
            System.out.println("About Me Data is null");

        }
        return aboutMe;
    }

    public String getFavoriteGame() {

        String favGame = "";
        try {
            favGame = ((JsonObject) userData).getString("favoriteGame");

        } catch (Exception e) {
            System.out.println("Favorite Game Data is null");

        }
        return favGame;
    }
    
    public String getTotalPlayTime() {

        String getTime = "";
        try {
            getTime = ((JsonObject) userData).getString("total_play_time_minutes");

        } catch (Exception e) {
            System.out.println("Total play time Data is null");

        }
        return getTime;
    }
}
