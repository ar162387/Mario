package minigames.client.snake;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.client.MinigameNetworkClient;
import minigames.client.snake.ui.SnakeGame;
import java.awt.Color;
//import java.awt.Font;

public class AchievementHandler {

    private final MinigameNetworkClient mnClient;
    private final String player;
    private JsonArray achievements2;
    private final SnakeGame snakeGame; // Reference to the SnakeGame instance to access game state

    /**
     * Constructor for AchievementHandler
     *
     */
    public AchievementHandler(MinigameNetworkClient mnClient, String player, SnakeGame snakeGame) {
        this.mnClient = mnClient;
        this.player = player;
        this.snakeGame = snakeGame;
    }

        /**
     * Method to loop through and print each JSON achievement.
     */
    public void awardAchievement2(String achievementName) {
        System.err.println("Attempting to award achievement: " + achievementName); // Log the attempt to award the achievement

        // Fetch the user's achievements directly from the server
        
        // TODO: FIX - HARD CODED jane_smith for now to see if my ideas were right.. 
        mnClient.getUserAchievements("jane_smith")
                .onSuccess(achievements -> {
                    // Initialise a variable to hold the ID of the achievement we're looking for
                    String achievementId = null;
                        //System.out.println("1. ALL Achievement json: " + achievements);
                        //System.out.println("99. Achievemenet Size: " + achievements.size());
                        // Iterate through the list of achievements to find the one with the matching name
                    for (int i = 0; i < achievements.size(); i++) {
                        JsonObject achievement = achievements.getJsonObject(i);
                        //System.out.println("2. EACH Achievement json: " + achievement);
                        
                        String name = achievement.getString("name");
                        String description = achievement.getString("description");
                
                        System.out.println("808 Achievement Name: " + name);
                        System.out.println("808 Achievement Description: " + description);
                        
                        if (achievement.getString("name").equals(achievementName)) {
                            System.out.println("3. GOT HERE ");
                            // If we find the achievement, store its ID
                            System.out.println("66: You already have this achievement " + achievements);
                            achievementId = achievement.getString("id");
                            break;  // Exit the loop once the achievement is found
                        }
                    }

 
                })
                .onFailure(err -> {
                    //TODO: FIX message after i clearly know what im doing with this function
                    System.err.println("Failed to retrieve user profile: " + err.getMessage());
                });
    }

    public JPanel getPlayerAchievements(String currentPlayer, String game) {
        JPanel achievementPanel = new JPanel();
        achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
    
        System.err.println("Getting achievements for player: " + currentPlayer); // Log the attempt to get achievements for one player
    
        // Fetch achievements from the server
        mnClient.getUserAchievements(currentPlayer)
                .onSuccess(achievements -> {
                    // Iterate through all achievements
                    for (int i = 0; i < achievements.size(); i++) {
                        JsonObject achievement = achievements.getJsonObject(i);
    
                        // Fetch the game name and check if it matches the input game parameter
                        String achievementGame = achievement.getString("game");
                        if (achievementGame != null && achievementGame.equals(game)) {
                            
                            // Fetch the details of the achievement
                            String name = achievement.getString("name");
                            String description = achievement.getString("description");
    
                            System.out.println("Achievement Name: " + name);
                            System.out.println("Achievement Description: " + description);
    
                            // Create a JPanel for this achievement
                            JPanel card = new JPanel();
                            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                            card.setBackground(new Color(245, 245, 220));
    
                            // Create a border around the card
                            card.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.BLACK, 1),
                                BorderFactory.createEmptyBorder(10, 10, 10, 10)
                            ));
    
                            // Create JLabels for the achievement name and description
                            JLabel nameLabel = new JLabel("<html><body style='width: 200px; text-align: center;'>" + name + "</body></html>");
                            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            nameLabel.setForeground(Color.RED);
    
                            JLabel descriptionLabel = new JLabel("<html><body style='width: 200px; text-align: center;'>" + description + "</body></html>");
                            descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
                            // Add labels to the card
                            card.add(nameLabel);
                            card.add(descriptionLabel);
    
                            // Add the card to the main achievements panel
                            achievementPanel.add(card);
                        }
                    }
    
                    // Make sure the panel is revalidated and repainted after adding components
                    achievementPanel.revalidate();
                    achievementPanel.repaint();
                })
                .onFailure(err -> {
                    System.err.println("Failed to retrieve user profile: " + err.getMessage());
                });
    
        return achievementPanel;
    }
    
    


/**
 * This method was creating during development and pops a windo with unformtedd achievements for a player
 * // jane smith at the moment - just left it in here for now
 */
    public void awardAchievement22(String achievementName) {
        System.err.println("Attempting to award achievement: " + achievementName); // Log the attempt to award the achievement

        mnClient.getUserAchievements("jane_smith")
                .onSuccess(achievements -> {
                    // Create a parent JPanel with a vertical BoxLayout
                    JPanel achievementPanel = new JPanel();
                    achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));

                    // Iterate through the list of achievements to find the one with the matching name
                    for (int i = 0; i < achievements.size(); i++) {
                        JsonObject achievement = achievements.getJsonObject(i);
                        String name = achievement.getString("name");
                        String description = achievement.getString("description");

                        System.out.println("808 Achievement Name: " + name);
                        System.out.println("808 Achievement Description: " + description);

                        // Create a new JPanel for this achievement
                        JPanel card = new JPanel();
                        //card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

                        // Add achievement name and description to the card
                        JLabel nameLabel = new JLabel("Achievement Name: " + name);
                        JLabel descriptionLabel = new JLabel("<html><body style='width: 200px'>" + description + "</body></html>"); // Limit width for better wrapping

                        card.add(nameLabel);
                        card.add(descriptionLabel);

                        // Add some padding/margins
                        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                        // Add the card to the parent panel
                        achievementPanel.add(card);

                        // Optional: Highlight the card if it matches the given achievementName
                        if (achievement.getString("name").equals(achievementName)) {
                            //card.setBackground(Color.YELLOW);
                            System.out.println("3. GOT HERE ");
                        }
                    }

                    // Display the achievement panel in a frame (or integrate it into your existing UI)
                    JFrame frame = new JFrame("Achievements");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.add(new JScrollPane(achievementPanel));
                    frame.setSize(400, 600);
                    frame.setVisible(true);

                })
                .onFailure(err -> {
                    System.err.println("Failed to retrieve user profile: " + err.getMessage());
                });
    }

//     // Not using this at the moment was created during development
//     public void awardAchievement(String achievementName) {
//         System.err.println("Attempting to award achievement: " + achievementName); // Log the attempt to award the achievement

//         // Fetch the user's achievements directly from the server
        
//         // TODO: FIX - HARD CODED jane_smith for now to see if my ideas were right.. 
//         mnClient.getUserAchievements("jane_smith")
//                 .onSuccess(achievements -> {
//                     // Initialise a variable to hold the ID of the achievement we're looking for
//                     String achievementId = null;
//                         System.out.println("1. ALL Achievement json: " + achievements);
//                         System.out.println("99. Achievemenet Size: " + achievements.size());
//                         // Iterate through the list of achievements to find the one with the matching name
//                     for (int i = 0; i < achievements.size(); i++) {
//                         JsonObject achievement = achievements.getJsonObject(i);
//                         System.out.println("2. EACH Achievement json: " + achievement);
                        
                        
//                         if (achievement.getString("name").equals(achievementName)) {
//                             System.out.println("3. GOT HERE ");
//                             // If we find the achievement, store its ID
//                             System.out.println("66: You already have this achievement " + achievements);
//                             achievementId = achievement.getString("id");
//                             break;  // Exit the loop once the achievement is found
//                         }
//                     }

//                     System.out.println("4. ACHIEVEMENT ID:" + achievementId);
//                     if (achievementId == null) {
//                         System.err.println("Achievement to be added: " + achievementName + ". which we will consider Unlocking...");
//                         //TODO: Jordan is going to give us a method awardAChievement(player, Json Object) Josn Object to be inserted 
//                         // will have 5 fields id,game,name,description,points in that order and requires us to validate it server side
//                         // Code we have previously only succesfully inserted ID
//                         // If the achievement ID was NOT found, unlock the achievement for the player
// /*                         mnClient.unlockAchievement(player, Integer.toString(achievements.size() + 1), "Test Achievement")
//                                 .onSuccess(v -> {
//                                     System.out.println("Achievement unlocked: " + achievementName);
//                                 })
//                                 .onFailure(err -> {
//                                     System.err.println("Failed to unlock achievement: " + err.getMessage());
//                                 }); */
//                                 System.out.println("999. Here we will add achievement to a player" + achievements);
//                     } else {
//                         System.out.println("Achievement json: " + achievements);
//                         System.err.println("Achievement not found: " + achievementName);
//                     }
//                 })
//                 .onFailure(err -> {
//                     System.err.println("Failed to retrieve user profile: " + err.getMessage());
//                 });
//     }

    public void awardAchievement(String achievementName) {
        System.out.println("Attempting to award achievement: " + achievementName);
        
        mnClient.awardAchievement("jane_smith", achievementName)
            .onSuccess(v -> {
                System.out.println("Achievement awarded: " + achievementName);
                //updateAchievementsDisplay();
            })
            .onFailure(err -> {
                System.err.println("Failed to award achievement: " + err.getMessage());
            });
    }

    public void updateAchievementsDisplay() {
        JPanel updatedAchievements = getPlayerAchievements(player, "Snake");
        snakeGame.updateAchievementsPanel(updatedAchievements);
    }

    /**
     * Method to handle game over and award achievements based on player's score
     */
    public void handleGameOver() {
        int score = snakeGame.getApplesEaten();
        System.err.println("Game Over! Player scored: " + score);
        
        if (score >= 2) {
            System.err.println("Player achieved a score of 2 or more. Awarding achievement...");
            awardAchievement("Score2");
        }
        if (score >= 5) {
            //awardAchievement("That's a long Snake");
            //awardAchievement("AchievementChampion");
            awardAchievement("Amazing Score");
            awardAchievement("Speedy Snake");
            awardAchievement("Score5");

        }
    }

}

