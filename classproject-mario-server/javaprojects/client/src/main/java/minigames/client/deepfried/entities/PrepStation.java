package minigames.client.deepfried.entities;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrepStation extends Entity {
    /**
     * The prep station entity
     * 
     * Contains the logic for placing and retrieving the prepared food
     * 
     */
    public enum State {
        EMPTY,
        PREPPING,
        PREPPED
    }

    private State currentState;

    private String empty = "/deepFried/chopping-board.png";
    private String prepping = "/deepFried/chopping-clock.png";
    private String complete = "/deepFried/chopping-complete.png";

    
    // Constructor
    public PrepStation(int x, int y) {
        super(x, y);
        this.currentState = State.EMPTY; // default state
        loadImage(empty); // load prepStation image
    }

    public State getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(State newState) {
        this.currentState = newState;
        switch (newState) {
            case EMPTY:
                updateImage(empty);
                break;
            case PREPPING:
                updateImage(prepping);
                break;
            case PREPPED:
                updateImage(complete);
                break;
            default:
                break;
        }
    }

    public void startPrepping(int prepTimeInSeconds) {
        setCurrentState(State.PREPPING);
        totalTime = prepTimeInSeconds * 1000;
        remainingTime = totalTime;
        startTimer(remainingTime);
    }

    @Override
    public void startTimer(int time) {
        timer = new Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishPrepping();
            }
        });
        timer.setRepeats(false);
        startTime = System.currentTimeMillis();
        timer.start();
    }
      
    // Method to finish prepping
    private void finishPrepping() {
        setCurrentState(State.PREPPED);
        timer.stop();
        remainingTime = 0;
        timer = null;
        System.out.println("Prepping complete! The ingredient is ready.");
    }
    // Method to place an ingredient on the station
    // Changes the state of the ingredient to prepped, could look at adding a prep timer here
    public void placeIngredient() {
        if (getCurrentState() == State.EMPTY) {
            startPrepping(2);
            setCurrentState(State.PREPPING);
            //set ingredient state to prepped
            System.out.println("Ingredient is being prepped!");
        } 
    }

    // Method to retrieve the prepped ingredient
    public void retrievePreppedIngredient() {
        if (getCurrentState() == State.PREPPED) {
            setCurrentState(State.EMPTY);
            System.out.println("Prepped ingredient retrieved!");
        } 
    }
}
