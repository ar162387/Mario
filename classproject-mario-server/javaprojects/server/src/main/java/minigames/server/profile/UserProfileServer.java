package minigames.server.profile;

public class UserProfileServer implements UserProfile {
/**
 * Our UserProfileServer holds UserProfile. 
 * When it receives a CommandPackage, it finds the UserProfile and calls it.
 */

    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    
    
    public UserProfileServer(String firstName, String lastName, String nickname, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.email = email;
    }


    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    
}
