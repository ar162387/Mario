package minigames.client.profile;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import io.vertx.core.json.JsonObject;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.IOException;
import javax.imageio.ImageIO;



public class UserProfileView extends JPanel{

    private String usernameData;
    private String emailData;
    private String firstNameData;
    private String lastNameData;
    private String dobData;
    private String aboutMeData;
    private String favoriteGameData;

    public UserProfileView(JsonObject profile) { 

        //updateProfile(profile);

        
        usernameData = profile.getString("username");
        firstNameData = profile.getString("first_name");
        lastNameData = profile.getString("last_name");
        emailData = profile.getString("email");
        dobData = profile.getString("date_of_birth");
        aboutMeData = profile.getString("bio");
        favoriteGameData = profile.getString("favorite_game");

        // Set the layout of the panel
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        // Calculate dynamic padding (for example, 2% of screen height and width)
        int paddingHeight = (int) (screenHeight * 0.02);
        int paddingWidth = (int) (screenWidth * 0.02);

        // Apply dynamic padding
        gbc.insets = new Insets(paddingHeight, paddingWidth, paddingHeight, paddingWidth);
   
        // Profile image icon
        JLabel profileImage = new JLabel();
        try {
            BufferedImage img = ImageIO.read(getClass().getResource("/images/profile/blank-profile-picture.PNG")); 
            Image scaledImage = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Width and height in pixels
            profileImage.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 20, 20);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(profileImage, gbc);
 
        // Username label
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Username:");
        add(usernameLabel, gbc);

        // Username Field
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,10); // Top, Left, Bottom, Right padding
        JTextField usernameField = new JTextField("Username", 20);
        usernameField.setEditable(false);
        //JLabel usernameField = new JLabel(usernameData);
        usernameField.setBackground(new Color(255, 239, 148));
        add(usernameField, gbc);

        // First name label
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel firstNameLabel = new JLabel("First Name:");
        add(firstNameLabel, gbc);

        // First name field
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,00); // Top, Left, Bottom, Right padding
        //JTextField firstNameField = new JTextField("First Name", 15);
        //firstNameField.setEditable(false);
        JLabel firstNameField = new JLabel(firstNameData);
        firstNameField.setBackground(new Color(218, 212, 237));
        add(firstNameField, gbc);

        // Last name Label
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel lastNameLabel = new JLabel("Last Name:");
        add(lastNameLabel, gbc);

        // Last name field
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,00,10,10); // Top, Left, Bottom, Right padding
        //JTextField lastNameField = new JTextField("Lase Name", 15);
        //lastNameField.setEditable(false);
        JLabel lastNameField = new JLabel(lastNameData);
        lastNameField.setBackground(new Color(218, 212, 237));
        add(lastNameField, gbc);

        
        // Email Label
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel emailLabel = new JLabel("Email:");
        add(emailLabel, gbc);

        // Email field
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,10); // Top, Left, Bottom, Right padding
        JTextField emailField = new JTextField("Email", 30);
        emailField.setEditable(false);
        //JLabel emailField = new JLabel(emailData);
        emailField.setBackground(new Color(173, 216, 230));
        add(emailField, gbc);

        // DOB Label
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel dobLabel = new JLabel("Date of Birth:");
        add(dobLabel, gbc);

        // Date of Birth (DOB) field
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,10); // Top, Left, Bottom, Right padding
        //JTextField dobField = new JTextField("Date of Birth", 12);
        //dobField.setEditable(false);
        JLabel dobField = new JLabel(dobData);
        dobField.setBackground(new Color(187, 237, 189));
        add(dobField, gbc);

        // About me/ Bio Label
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel aboutMeLabel = new JLabel("About me:");
        add(aboutMeLabel, gbc);

        // About Me/Bio section
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,10); // Top, Left, Bottom, Right padding
        JTextArea aboutMeArea = new JTextArea(aboutMeData,5,50);
        aboutMeArea.setLineWrap(true);
        aboutMeArea.setWrapStyleWord(true);
        aboutMeArea.setBackground(new Color(218, 212, 237));
        add(new JScrollPane(aboutMeArea), gbc);

        // Favorite game Label
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel favGameLabel = new JLabel("Favorite game:");
        add(favGameLabel, gbc);

        // Favorite Game section
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,10); // Top, Left, Bottom, Right padding
        //JTextField favoriteGameField = new JTextField(profile.getFavoriteGame(), 20);
        //favoriteGameField.setEditable(false);
        JLabel favoriteGameField = new JLabel(favoriteGameData);
        favoriteGameField.setBackground(new Color(224, 224, 224));
        add(favoriteGameField, gbc);

        // recent acheivements Label
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel achievementLabel = new JLabel("Achievements:");
        add(achievementLabel, gbc);

        // recent acheivements section
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,10,10,10); // Top, Left, Bottom, Right padding
        // To Do:  change this from a text field to maybe seperate tiles for achievements
        JTextField achievmentField = new JTextField("Achievements", 50);
        achievmentField.setEditable(false);
        //JLabel achievmentData = new JLabel("Achievements");
        achievmentField.setBackground(new Color(224, 224, 224));
        add(achievmentField, gbc);


    }

}
         
