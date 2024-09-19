package minigames.client.geowars;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.m;

import minigames.client.geowars.gameobjects.PlayerController;
import minigames.client.geowars.util.Sound;

public class InputManager {

    private static final Logger logger = LogManager.getLogger(InputManager.class);

    private static InputManager instance = null;

    private GeoWars engine;
    private GameManager gameManager;
    private PlayerController pController;
    private JPanel panel;
    private MouseAdapter mAdapter;
    private boolean paused = false;
    public Action upActionReleased, upActionPressed, downActionReleased, downActionPressed, leftActionReleased,
            leftActionPressed, rightActionReleased, rightActionPressed, pAction, escAction;
    public InputMap inputMap;
    public ActionMap actionMap;
    private boolean leftMousePressed;
    private boolean mouseOverButton = false;

    private InputManager(GeoWars engine, JPanel panel) {
        this.engine = engine;
        this.panel = panel;

        if (GameManager.isInstanceNull()) {
            logger.error("GameManager instance is null in InputManager constructor.");
        } else {
            this.gameManager = GameManager.getInstance(null, null);
        }

        // Adds basic KeyBinding elements used across all keybinds
        inputMap = this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = this.panel.getActionMap();

        upActionReleased = new UpActionReleased();
        downActionReleased = new DownActionReleased();
        leftActionReleased = new LeftActionReleased();
        rightActionReleased = new RightActionReleased();
        upActionPressed = new UpActionPressed();
        downActionPressed = new DownActionPressed();
        leftActionPressed = new LeftActionPressed();
        rightActionPressed = new RightActionPressed();
        pAction = new PAction();
        escAction = new EscAction();

        // Mapping Releases and pressed events to control accelleration in the future.
        // KeyBoard Input mappings
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "upReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "upPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "wReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "wPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "downReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "downPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "sReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "sPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "leftPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "aReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "aPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rightPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "dReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "dPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, true), "pReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "escReleased");
        // Action Mappings
        actionMap.put("upReleased", upActionReleased);
        actionMap.put("upPressed", upActionPressed);
        actionMap.put("wReleased", upActionReleased);
        actionMap.put("wPressed", upActionPressed);
        actionMap.put("downReleased", downActionReleased);
        actionMap.put("downPressed", downActionPressed);
        actionMap.put("sReleased", downActionReleased);
        actionMap.put("sPressed", downActionPressed);
        actionMap.put("leftReleased", leftActionReleased);
        actionMap.put("leftPressed", leftActionPressed);
        actionMap.put("aReleased", leftActionReleased);
        actionMap.put("aPressed", leftActionPressed);
        actionMap.put("rightReleased", rightActionReleased);
        actionMap.put("rightPressed", rightActionPressed);
        actionMap.put("dReleased", rightActionReleased);
        actionMap.put("dPressed", rightActionPressed);
        actionMap.put("pReleased", pAction);
        actionMap.put("escReleased", escAction);

        addML();
    }

    public static boolean isInstanceNull() {
        return instance == null;
    }

    public static InputManager getInstance(GeoWars engine, JPanel panel) {
        if (instance == null) {
            instance = new InputManager(engine, panel);
        }
        return instance;
    }

    public void addPlayerController(PlayerController playerController) {
        this.pController = playerController;
    }

    public void releasePlayerController() {
        this.pController = null;
    }

    public class PAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("p pressed/released");
            // P pauses a level but does not resume it.
            if (!paused) {
                gameManager.pauseLevel(true);
            }
        }
    }

    public class EscAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("esc pressed/released");
            // Esc pauses a level and resumes it.
            gameManager.pauseLevel(!paused);
        }
    }

    public class UpActionReleased extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setUp(false);
                    break;
                default:
                    break;
            }
        }
    }

    public class UpActionPressed extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setUp(true);
                    break;
                default:
                    break;
            }
        }
    }

    public class DownActionReleased extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setDown(false);
                    break;
                default:
                    break;
            }
        }
    }

    public class DownActionPressed extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setDown(true);
                    break;
                default:
                    break;
            }
        }
    }

    public class LeftActionReleased extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setLeft(false);
                    break;
                default:
                    break;
            }
        }
    }

    public class LeftActionPressed extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setLeft(true);
                    break;
                default:
                    break;
            }
        }
    }

    public class RightActionReleased extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setRight(false);
                    break;
                default:
                    break;
            }
        }
    }

    public class RightActionPressed extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (engine.getGameState()) {
                case 1:
                    pController.setRight(true);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Adds a mouseListener to the window the player was initialised on.
     */
    public void addML() {
        this.panel.addMouseListener(this.mAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                // logger.info("Mouse Pressed");
                if (pController != null && !mouseOverButton) {
                    pController.setTimeSinceBullet(1 / pController.getFireRate());
                    pController.setPress(true);
                    Sound.getInstance().playLoopSFX(Sound.Type.SHOOT); // Stops the shooting sound when released.
                }
                leftMousePressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // logger.info("Mouse Released");
                if (pController != null && !mouseOverButton) {
                    pController.setPress(false);
                    Sound.getInstance().stop(Sound.Type.SHOOT); // Stops the shooting sound when released.
                }
                leftMousePressed = false;
            }
        });
    }

    /**
     * Removes a mouseListener in the window the player was initialised on.
     */
    public void removeML() {
        this.panel.removeMouseListener(this.mAdapter);
    }

    // GETTERS

    /**
     * Retrieves the mouse's location, relative to the window's location
     * 
     * @return a Point representing the mouse's position inside the window
     */
    public Point getRelativeLocation() {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        Point windowLocation = this.panel.getLocationOnScreen();
        return new Point(mouseLocation.x - windowLocation.x, mouseLocation.y - windowLocation.y);
    }

    /**
     * Retrieves the paused value.
     * 
     * @return paused which a multiplier used by the player movement calculations.
     */
    public boolean getPaused() {
        return this.paused;
    }

    /**
     * Retrieves the leftMousePressed value.
     * 
     * @return leftMousePressed which is a boolean value representing if the left
     *         mouse button is pressed.
     */
    public boolean getLeftMousePressed() {
        return leftMousePressed;
    }

    // SETTERS
    /**
     * Sets the paused boolean value to the provided state.
     * 
     * @param pause - new boolean value for paused to be set to
     */
    public void setPaused(boolean pause) {
        this.paused = pause;
    }

    /**
     * Sets the leftMousePressed boolean value to the provided state.
     * 
     * @param leftMousePressed - new boolean value for leftMousePressed to be set to
     */
    public void setMouseOverButton(boolean mouseOverButton) {
        this.mouseOverButton = mouseOverButton;
    }

    public void cleanup() {
        removeML();
        mAdapter = null;
        actionMap.clear();
        inputMap.clear();
        panel = null;
        instance = null;
    }
}
