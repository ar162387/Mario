package minigames.server.profile;

public interface UserProfile {
    /**
     *  This class represents the users profile data (INTERFACE)
     * 
     *  preliminary data points: nickname, firstName, lastName, email
     *
     *  Represents an actual User Profile but abstracts away the api calls for sensitive data in the database.
     * 
     * Structure:
     *  get auth token and store in interface
     *  each get and put/post call, use stored auth token
     * 
     *  terminate auth token at end of interface
     */

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    String getNickname();
    void setNickname(String nickname);

    String getEmail();
    void setEmail(String email);



     }
