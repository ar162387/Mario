package minigames.client;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;

import javax.swing.SwingUtilities;
import minigames.client.mario.*;

import minigames.client.backgrounds.Starfield;
import minigames.client.profile.UserProfileController;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.NativeCommands.QuitToMenu;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.util.List;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * The main window that appears.
 *
 * For simplicity, we give it a BorderLayout with panels for north, south, east,
 * west, and center.
 *
 * This makes it simpler for games to load up the UI however they wish, though
 * the default expectation
 * is that the centre just has an 800x600 canvas.
 */
public class MinigameNetworkClientWindow {

    MinigameNetworkClient networkClient;

    JFrame frame;

    JPanel container;
    JPanel navbar;

    JPanel parent;
    JPanel north;
    JPanel center;
    JPanel south;
    JPanel west;
    JPanel east;

    JLabel messageLabel;

    // We hang on to this one for registering in servers
    JTextField nameField;

    String username;
    String forgottenPasswordCode; 

    private static final Logger logger = LogManager.getLogger(MinigameNetworkClientWindow.class);


    public MinigameNetworkClientWindow(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        frame.add(container);

        navbar = new JPanel();

        // Set the navbar to be a set height here
        int navbarHeight = 27;
        navbar.setPreferredSize(new Dimension(navbar.getWidth(), navbarHeight));
        navbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, navbarHeight));

        navbar.setBackground(Color.BLACK);
        navbar.setOpaque(true);
        navbar.setLayout(new BoxLayout(navbar, BoxLayout.X_AXIS));

        updateNavigationBar();
        container.add(navbar);

        parent = new JPanel(new BorderLayout());

        north = new JPanel();
        parent.add(north, BorderLayout.NORTH);
        center = new JPanel();
        center.setPreferredSize(new Dimension(800, 600));
        parent.add(center, BorderLayout.CENTER);
        south = new JPanel();
        parent.add(south, BorderLayout.SOUTH);
        east = new JPanel();
        parent.add(east, BorderLayout.EAST);
        west = new JPanel();
        parent.add(west, BorderLayout.WEST);

        container.add(parent);
        frame.add(container);

        nameField = new JTextField(20);
        nameField.setText("Algernon");

        /*
        // Tells the width and height of the screen in the terminal
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = frame.getWidth();
                int newHeight = frame.getHeight();
                System.out.println("Window resized to: Width = " + newWidth + ", Height = " + newHeight);
            }
        });
         */

    }

    /** Refreshes navigation bar */
    public void updateNavigationBar() {
        navbar.removeAll();

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setOpaque(true);

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener((evt) -> {
            QuitToMenu qtm = new QuitToMenu();
            networkClient.execute(qtm);
        });
        leftPanel.add(homeButton);

        if (username != null && !username.isEmpty()) {
            JButton profileButton = new JButton("Profile");
            profileButton.addActionListener((evt) -> {
                openProfilePage();
            });
            leftPanel.add(profileButton);
        }

        /**
         * create and handle search bar + search button to search for player profiles
         */
        String searchBarMessage = "Search for players here";
        JButton profileSearchButton = new JButton("Search");
        JTextField profileSearchField = new JTextField(searchBarMessage, 15);
        profileSearchField.setForeground(Color.GRAY);

        Dimension buttonSize = profileSearchButton.getPreferredSize();
        profileSearchField.setPreferredSize(new Dimension(profileSearchField.getPreferredSize().width, buttonSize.height));        
        
        profileSearchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (profileSearchField.getText().equals(searchBarMessage)) {
                    profileSearchField.setText("");
                    profileSearchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (profileSearchField.getText().isEmpty()) {
                    profileSearchField.setForeground(Color.GRAY);
                    profileSearchField.setText(searchBarMessage);
                }
            }
        });
        profileSearchField.addActionListener((evt) -> {
            String username = profileSearchField.getText().trim();
            System.out.println(username);
            if (!username.isEmpty() || !username.equals(searchBarMessage)) {
                searchPlayerProfile(username);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a Username to search.", "Search Error",
                        JOptionPane.ERROR_MESSAGE);
                networkClient.runMainMenuSequence();
            }
        });

        profileSearchButton.addActionListener((evt) -> {
            String username = profileSearchField.getText().trim();
            System.out.println(username);
            if (!username.isEmpty() || !username.equals(searchBarMessage)) {
                searchPlayerProfile(username);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a Username to search.", "Search Error",
                        JOptionPane.ERROR_MESSAGE);
                networkClient.runMainMenuSequence();
            }
        });
        leftPanel.add(profileSearchField);
        leftPanel.add(profileSearchButton);

        // Panel for right-aligned components
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setOpaque(true);

        if (username != null && !username.isEmpty()) {
            // show username
            JLabel userInfo = new JLabel(username);
            userInfo.setForeground(Color.WHITE);
            rightPanel.add(userInfo);



            // show manage account details
            JButton manageAccount = new JButton("Manage Account");
            manageAccount.addActionListener((evt) -> {
                showAccountManagementPage(false);
            });
            rightPanel.add(manageAccount);

            // show log-out button
            JButton logout = new JButton("Log out");
            logout.addActionListener((evt) -> {
                username = null;
                QuitToMenu qtm = new QuitToMenu();
                networkClient.execute(qtm);

                updateNavigationBar();
            });
            rightPanel.add(logout);
        } else {
            JButton login = new JButton("Log in");
            login.addActionListener((evt) -> {
                showLoginPage();
            });
            rightPanel.add(login);

        JButton signUpPageButton = new JButton("Sign Up");
        signUpPageButton.addActionListener(evt -> {
            showSignUpPage();
        });
        rightPanel.add(signUpPageButton);
        }


        navbar.add(leftPanel);
        navbar.add(Box.createHorizontalGlue());
        navbar.add(rightPanel);

        navbar.revalidate(); // Ensure UI updates
        navbar.repaint(); // Ensure UI updates
    }

    /** Removes all components from the south panel */
    public void clearSouth() {
        south.removeAll();
    }

    /** Clears all sections of the UI */
    public void clearAll() {
        for (JPanel p : new JPanel[] { north, south, east, west, center }) {
            p.removeAll();
        }
    }

    /** Adds a component to the north part of the main window */
    public void addNorth(Component c) {
        north.add(c);
    }

    /** Adds a component to the south part of the main window */
    public void addSouth(Component c) {
        south.add(c);
    }

    /** Adds a component to the east part of the main window */
    public void addEast(Component c) {
        east.add(c);
    }

    /** Adds a component to the west part of the main window */
    public void addWest(Component c) {
        west.add(c);
    }

    /** Adds a component to the center of the main window */
    public void addCenter(Component c) {
        center.add(c);
    }

    /**
     * Adds an accessible way to programatically resize the window to a larger
     * resolution than default
     */
    public void setCenterSize(Dimension d) {
        center.setPreferredSize(d);
    }

    /**
     * "Packs" the frame, setting its size to match the preferred layout sizes of
     * its component
     */
    public void pack() {
        frame.pack();
        parent.repaint();
    }

    /** Makes the main window visible */
    public void show() {
        pack();
        centreWindow();
        frame.setVisible(true);
    }

    /** Centers the window in the middle of the screen */
    public void centreWindow() {
        frame.setLocationRelativeTo(null);
    }

    /**
     * Shows a simple message layered over a retro-looking starfield.
     * Terrible placeholder art.
     */
    public void showStarfieldMessage(String s) {
        clearAll();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(new Starfield(networkClient.animator), JLayeredPane.DEFAULT_LAYER);
        layeredPane.setBackground(new Color(0, 0, 0, 0));
        layeredPane.setPreferredSize(new Dimension(800, 600));

        JLabel label = new JLabel(s);
        label.setOpaque(true);
        label.setForeground(Color.CYAN);
        label.setBackground(Color.BLACK);
        label.setFont(new Font("Monospaced", Font.PLAIN, 36));
        Dimension labelSize = label.getPreferredSize();
        label.setSize(labelSize);
        label.setLocation((int) (400 - labelSize.getWidth() / 2), (int) (300 - labelSize.getHeight() / 2));
        layeredPane.add(label, JLayeredPane.MODAL_LAYER);

        center.add(layeredPane);
        pack();
    }

    /**
     * Search Player Profile
     */

    public void searchPlayerProfile(String username) {
        clearAll();
        // call new profile controller
        UserProfileController controller = new UserProfileController(networkClient);
        // retrieve the "profile View" object by handling the asynchronous comnpletion
        // of .fetchOtherUserProfile()
        controller.fetchOtherUserProfile(username).onSuccess(profileView -> {
            if (profileView == null) {
                System.out.println("Profile view is null!");
                JOptionPane.showMessageDialog(frame, "Please enter a valid Username to search.", "Search Error",
                        JOptionPane.ERROR_MESSAGE);

                return;
            } else {
                System.out.println("Profile Loaded Successfully!");
            }
            // Add the profile view to the center panel of the main frame
            center.add(profileView);
            // Pack and repaint the frame to update the UI
            pack();
            parent.repaint();

        }).onFailure(error -> {
            // Handle the error (e.g., show an error message)
            System.out.println("Failed to load profile: " + error.getMessage());
        });

    }

    /**
     * Shows a player profile.
     */

    public void openProfilePage() {
        clearAll();
        // call new profile controller
        UserProfileController controller = new UserProfileController(networkClient);
        // retrieve the "profile View" object by handling the asynchronous comnpletion
        // of .fetchUserProfile()
        controller.fetchUserProfile().onSuccess(profileView -> {
            if (profileView == null) {
                System.out.println("Profile view is null!");
                return;
            } else {
                System.out.println("Profile Loaded Successfully!");
            }
            // Add the profile view to the center panel of the main frame
            center.add(profileView);
            // Pack and repaint the frame to update the UI
            pack();
            parent.repaint();

        }).onFailure(error -> {
            // Handle the error (e.g., show an error message)
            System.out.println("Failed to load profile: " + error.getMessage());
        });
    }

    /**
     * Shows a simple log-in page.
     */

    public void showLoginPage() {
        clearAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton devLogin = new JButton("Dev - auto log in"); // TODO: remove this
        devLogin.addActionListener((evt) -> {
            networkClient.login("jane_smith", "password123")
                    .compose(res -> networkClient.getUserPrincipal())
                    .onSuccess(userDetails -> {
                        logger.info("Login successful. User principal details returned: " + userDetails);


                        clearAll();
                        JPanel successPanel = new JPanel();

                        // FIXME: this is a temporary workaround until .getUserPrincipal() is resolved
                        String username = userDetails.getString("username", "jane_smith");

                        JLabel success = new JLabel("Login successful! Welcome, " + username);

                        this.username = username;
                        updateNavigationBar();

                        JButton homeButton = new JButton("Start Playing");
                        homeButton.addActionListener((e) -> {
                            QuitToMenu qtm = new QuitToMenu();
                            networkClient.execute(qtm);
                        });

                        successPanel.add(success);
                        center.add(successPanel);
                        center.add(homeButton);
                        pack();
                        parent.repaint();
                    })
                    .onFailure(err -> {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Login failed: " + err.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
        });
        panel.add(devLogin);


        JLabel usernameLabel = new JLabel("Username");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        JTextField passwordField = new JTextField();
        JButton loginButton = new JButton("Log in");

        loginButton.addActionListener((evt) -> {
            networkClient.login(usernameField.getText(), passwordField.getText())
                    .compose(res -> networkClient.getUserPrincipal())
                    .onSuccess(userDetails -> {
                        clearAll();
                        JPanel successPanel = new JPanel();

                        //FIXME: this is a temporary workaround until .getUserPrincipal() is resolved
                        String username = userDetails.getString("username", usernameField.getText());
                        JLabel success = new JLabel("Login successful! Welcome, " + username);

                        JButton homeButton = new JButton("Start Playing");
                        homeButton.addActionListener((e) -> {
                            QuitToMenu qtm = new QuitToMenu();
                            networkClient.execute(qtm);
                        });

                        this.username = username;
                        updateNavigationBar();

                        successPanel.add(success);
                        successPanel.add(homeButton);
                        center.add(successPanel);
                        pack();
                        parent.repaint();
                    })
                    .onFailure(err -> {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Login failed: " + err.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
        });

        JButton forgottenPasswordButton = new JButton("Forgotten Username/Password");
        forgottenPasswordButton.addActionListener((evt) -> {
            showForgottenPasswordPage();
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(forgottenPasswordButton);

        center.add(panel);
        pack();
        parent.repaint();
    }

    /**
     * Shows account management page.
     */

    private <T extends JComponent> T addFormComponent(int x, int y, int width, int anchor, String text, JPanel panel,
            Class<T> componentClass) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.anchor = anchor;
        if (componentClass == JTextField.class)
            gbc.fill = GridBagConstraints.HORIZONTAL;
        try {
            // Create an instance of the component (e.g., JLabel or JButton)
            T component = componentClass.getDeclaredConstructor(String.class).newInstance(text);
            panel.add(component, gbc);
            return component;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Something went wrong creating form component.");
        }
    }

    public void showAccountManagementPage(boolean editing) {
        clearAll();

        // get user information
        networkClient.getUserDetails(username)
                .onSuccess(res -> {
                    JsonObject userDetails = res;

                    JPanel panel = new JPanel(new GridBagLayout());


                    Class<? extends JComponent> shortFieldClass = editing ? JTextField.class : JLabel.class;
                    Class<? extends JComponent> longFieldClass = editing ? JTextArea.class : JLabel.class;

                    addFormComponent(0, 0, 1, GridBagConstraints.EAST, "Username:", panel, JLabel.class);
                    JComponent userField = addFormComponent(1, 0, 1, GridBagConstraints.WEST, userDetails.getString("username"), panel,
                            JLabel.class);
                    addFormComponent(0, 1, 1, GridBagConstraints.EAST, "Email:", panel, JLabel.class);
                    JComponent emailField = addFormComponent(1, 1, 1, GridBagConstraints.WEST, userDetails.getJsonObject("properties").getString("email"), panel,
                            shortFieldClass);

                    String editButtonLabel = editing ? "Save Details" : "Edit Details";
                    JButton editButton = addFormComponent(0, 8, 2, GridBagConstraints.CENTER, editButtonLabel, panel,
                            JButton.class);
                    editButton.addActionListener((evt) -> {
                        if (editing && emailField instanceof JTextField) {
                            JsonObject updatedDetails = new JsonObject();
                            updatedDetails.put("email", ((JTextField) emailField).getText());
                            networkClient.updateUserDetails(username, updatedDetails).onSuccess(res2 -> {
                                showAccountManagementPage(false);
                            });
                        }
                        else
                            showAccountManagementPage(true); 
                    });

                    JButton passwordResetButton = addFormComponent(0, 9, 2, GridBagConstraints.CENTER, "Reset Password", panel, JButton.class);
                    passwordResetButton.addActionListener((evt) -> {
                        showPasswordResetPage(null); //FIXME: send email address
                    });

                    center.add(panel);
                    pack();
                    parent.repaint();
                });
    }

    public void showForgottenPasswordPage() {
        clearAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel emailLabel = new JLabel("Email");
        JTextField emailField = new JTextField();
        JButton resetPasswordButton = new JButton("Send Password Reset Link");

        // generate a random string to confirm email address
        this.forgottenPasswordCode = UUID.randomUUID().toString();

        resetPasswordButton.addActionListener((evt) -> {
            JsonObject emailDetails = new JsonObject();
            emailDetails.put("to", emailField.getText());
            emailDetails.put("subject", "Password Reset (COSC220 Demo)");
            emailDetails.put("body", "Use the following key to reset your password: " + this.forgottenPasswordCode);
            // TODO: add username to email body
            networkClient.sendEmail(emailDetails)
                .onSuccess(res -> {
                    showEnterForgottenPasswordCodePage();
                })
                .onFailure(err -> {
                    JOptionPane.showMessageDialog(frame, "Failed to send password reset link: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
        });

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(resetPasswordButton);

        center.add(panel);
        pack();
        parent.repaint();
    }

    public void showEnterForgottenPasswordCodePage() {
        clearAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel infoText = new JLabel("A password reset code has been sent to your email address. Enter the code in the textbox below.");
        JTextField codeField = new JTextField();
        JButton submitCodeButton = new JButton("Verify Code");

        submitCodeButton.addActionListener((evt) -> {
            // TODO: check that the code matches the one sent
            if (!codeField.getText().equals(this.forgottenPasswordCode)) {
                JOptionPane.showMessageDialog(frame, "Invalid code", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showPasswordResetPage(null); // FIXME: send email address
        });

        panel.add(infoText);
        panel.add(codeField);
        panel.add(submitCodeButton);

        center.add(panel);
        pack();
        parent.repaint();
    }

    public void showPasswordResetPage(String email) {
        clearAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        networkClient.getUsernameFromEmail("jane.smith@example.com") //FIXME: get email address dynamically
            .onSuccess(username -> {
                JLabel usernameInfo = new JLabel("Setting password for user: " + username);

                JLabel passwordLabel = new JLabel("Password");
                JTextField passwordField = new JTextField();
                JLabel passwordConfirmationLabel = new JLabel("Confirm Password");
                JTextField passwordConfirmationField = new JTextField();
                JButton changePasswordButton = new JButton("Change Password");

                changePasswordButton.addActionListener((evt) -> {
                    // check that passwords match
                    if (!passwordField.getText().equals(passwordConfirmationField.getText())) {
                        JOptionPane.showMessageDialog(frame, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

            // TODO: change password in database - (if come from 'forgotten password' link, also need to get username)
        });

        panel.add(usernameInfo);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(passwordConfirmationLabel);
        panel.add(passwordConfirmationField);
        panel.add(changePasswordButton);

        center.add(panel);
        pack();
        parent.repaint();
            });

    }

    /**
     * Shows a list of GameServers to pick from
     *
     * TODO: Prettify!
     * 
     * @param servers
     */
    public void showGameServers(List<GameServerDetails> servers) {
        clearAll();

        // Set the frame size
        frame.setPreferredSize(new Dimension(1300, 900));

        // Use a GridBagLayout to control the placement of tiles in specific columns
        JPanel panel = new JPanel(new GridBagLayout());

        // Tiles in the grid Pattern
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; 
        gbc.gridwidth = 1; 
        gbc.weightx = 1; 
        gbc.weighty = 1; 
        gbc.insets = new Insets(0, 0, 0, 0); // Adding padding around the tiles
        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically

        // Add tiles to columns 2, 3, and 4
        for (int i = 0; i < servers.size(); i++) {
            int j = i;
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints tileGbc = new GridBagConstraints();

            JLabel l = new JLabel(String.format("<html><h1>%s</h1><p>%s</p></html>", servers.get(i).name(), servers.get(i).description()));            tileGbc.gridx = 0;
            tileGbc.gridy = 0;
            tileGbc.gridwidth = 1;
            tileGbc.gridheight = 3;
            tileGbc.weightx = 1;
            tileGbc.weighty = 1;
            tileGbc.insets = new Insets(0, 0, 0, 0);
            tileGbc.fill = GridBagConstraints.BOTH;
            tileGbc.anchor = GridBagConstraints.CENTER; // Center the label
            p.add(l, tileGbc);

            

            // Button configuration
            JButton newG = new JButton("Open Game");
            newG.addActionListener(evt -> {

                if (servers.get(j).name().equalsIgnoreCase("minesweeper")) {
                    networkClient.newGame("Minesweeper", nameField.getText());
                }
                // Add an additional condition for another game (e.g., "Snake")
                else if (servers.get(j).name().equalsIgnoreCase("snake")) {
                    networkClient.newGame("Snake", nameField.getText());
                }
                // Add condition for Connect Four
                else if (servers.get(j).name().equalsIgnoreCase("connectfour")) {
                    networkClient.newGame("ConnectFour", nameField.getText());
                }
                // Add condition for TicTacToe 
                else if (servers.get(j).name().equalsIgnoreCase("TicTacToe")) {
                    networkClient.newGame("TicTacToe", nameField.getText());
                }
                // General case for other games
                else {
                    networkClient.getGameMetadata(servers.get(j).name())
                        .onSuccess(list -> showGames(servers.get(j).name(), list));
                }
            });

            // Position the button at the bottom center of the tile
            tileGbc.gridx = 0;
            tileGbc.gridy = 4;
            tileGbc.gridwidth = 1;
            tileGbc.gridheight = 1;
            tileGbc.weightx = 1;
            tileGbc.weighty = 0;
            tileGbc.insets = new Insets(5, 5, 5, 5);
            tileGbc.anchor = GridBagConstraints.CENTER;
            p.add(newG, tileGbc);

            // Adjusting width and height for tiles to fit in columns 2, 3, 4
            p.setPreferredSize(new Dimension(350, 135)); 
            p.setMaximumSize(new Dimension(350, 160));
            p.setMinimumSize(new Dimension(350, 120));

            // make grid use columns 2,3,4 of the 5 columns
            gbc.gridx = 1 + (i % 3); // Modulo to loop through columns 2, 3, 4
            gbc.gridy = i / 3; // Increment row for every 3 tiles placed in grid

            
            panel.add(p, gbc);  
        }

        // Use a container panel to ensure the main panel is centered
        JPanel containerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints containerGbc = new GridBagConstraints();
        containerGbc.gridx = 0;
        containerGbc.gridy = 0;
        containerGbc.weightx = 1;
        containerGbc.weighty = 1;
        containerGbc.anchor = GridBagConstraints.CENTER; // Center the main panel
        containerPanel.add(panel, containerGbc);

        // Directly add the container panel with tiles to the center area
        center.add(containerPanel);

        // Bottom panel for the Sign-Up button
        //JPanel signUpPanel = new JPanel(); // new FlowLayout(FlowLayout.CENTER)
        JButton signUpPageButton = new JButton("Sign Up Page");
        signUpPageButton.addActionListener(evt -> {
            showSignUpPage();
        });
        JButton testAchievementButton = new JButton("Test Add Achievement");
        testAchievementButton.addActionListener(e -> networkClient.runAchievementTest());

        //signUpPanel.add(signUpPageButton);
        //south.add(signUpPanel);
        //south.add(testAchievementButton);
        
        
        //JPanel testPanel = new JPanel();
        //testPanel.add(testAchievementButton);
        // south.add(testPanel);

        /*
         * JPanel signUpPanel = new JPanel();
         * JButton signUpPageButton = new JButton("Sign Up Page");
         * signUpPageButton.addActionListener(evt -> {
         * showSignUpPage();
         * });
         * signUpPanel.add(signUpPageButton);
         * south.add(signUpPanel);
         */

        frame.pack();
        frame.setLocationRelativeTo(null);
        parent.repaint();
    }


    /**
     * Shows a list of games to pick from
     *
     * TODO: Prettify!
     * 
     * @param gameServer
     */
    
    public void showGames(String gameServer, List<GameMetadata> inProgress) {
        clearAll();

        JPanel namePanel = new JPanel();
        JLabel nameLabel = new JLabel("Your name");
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        north.add(namePanel);
        // Moved button here in a seperate top panel
        JPanel topPanel = new JPanel();
        JButton newG = new JButton("New game");
        newG.addActionListener((evt) -> {
            if (gameServer.equalsIgnoreCase("mario")) {
                SwingUtilities.invokeLater(() -> {
                    MarioMainMenu mainMenu = new MarioMainMenu(networkClient);
                    mainMenu.display();
                	});            
            } else {
            // FIXME: We've got a hardcoded player name here
            networkClient.newGame(gameServer, nameField.getText());
         }
        });
        topPanel.add(newG);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        List<JPanel> gamePanels = inProgress.stream().map((g) -> {
            JPanel p = new JPanel();
            JLabel l = new JLabel(
                    String.format("<html><h1>%s</h1><p>%s</p></html>", g.name(), String.join(",", g.players())));
            JButton join = new JButton("Join game");
            join.addActionListener((evt) -> {
                networkClient.joinGame(gameServer, g.name(), nameField.getText());
            });
            join.setEnabled(g.joinable());
            p.add(l);
            p.add(join);
            return p;
        }).toList();

        for (JPanel gamePanel : gamePanels) {
            panel.add(gamePanel);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        // combine scroll panel and top panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        // Add the button panel at the top
        mainPanel.add(topPanel, BorderLayout.NORTH); 
        mainPanel.add(scrollPane, BorderLayout.CENTER); 
    
        // Add the container panel to the center area
        center.add(mainPanel);
        pack();
        parent.repaint();
    }
    

    /**
     * Return a reference to this window's frame
     */
    public JFrame getFrame() {
        return frame;
    }

    public void showSignUpPage() {
        clearAll();
        System.out.println("show sign up ppage");
        // Create the frame
        JFrame frame = new JFrame("Sign Up");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2));

        // Create components
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JTextField passText = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailText = new JTextField();

        JButton signUpButton = new JButton("Sign Up");
        JButton cancelButton = new JButton("Cancel");

        frame.add(userLabel);
        frame.add(userText);
        frame.add(passLabel);
        frame.add(passText);
        frame.add(emailLabel);
        frame.add(emailText);
        frame.add(signUpButton);
        frame.add(cancelButton);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passText.getText());
                String email = emailText.getText();

                int i = 0;
                while (i < 1) {
                    if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "All fields are required!", "Error",
                                JOptionPane.ERROR_MESSAGE);

                        username = null;
                        password = null;
                        email = null;

                    } else if (password.length() < 8) {
                        JOptionPane.showMessageDialog(frame, "Password must be 8 characters", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        username = null;
                        password = null;
                        email = null;

                    } else if (!email.contains("@")) {
                        JOptionPane.showMessageDialog(frame, "Not a valid email address", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        username = null;
                        password = null;
                        email = null;

                    } else {
                        i = i + 1;
                        JOptionPane.showMessageDialog(frame, "Registration Successful!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                    }
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                jsonObject.put("email", email);

                System.out.println("This is the JSON object" + jsonObject);

                networkClient.register(jsonObject)
                        .onSuccess(userDetails -> {
                            clearAll();
                            JPanel successPanel = new JPanel();
                            String username2 = jsonObject.getString("username"); //TODO: get from user object once fixed
                            JLabel success = new JLabel("Registration successful! Welcome, " + username2);

                            updateNavigationBar();
                            System.out.println("Got to here in login successful step");

                            JButton logIn = new JButton("Log in to continue"); 
                            logIn.addActionListener((evt) -> {
                                showLoginPage();
                            });

                            successPanel.add(success);
                            successPanel.add(logIn);
                            center.add(successPanel);
                            pack();
                            parent.repaint();
                        })
                        .onFailure(err -> {
                            System.out.println("Got to here in error step");
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "Login failed: " + err.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        });

            }

        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the sign-up window
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}
