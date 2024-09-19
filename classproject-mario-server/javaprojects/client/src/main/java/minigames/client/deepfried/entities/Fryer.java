package minigames.client.deepfried.entities;



import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Fryer extends Entity {
        /**
     * The fryer entity
     * 
     * Contains the logic for frying/ cooking the food
     * 
     * TO DO: fryer.finishFrying() Return a cookedFood item.
     * 
     */
    public enum State {
        EMPTY, // nothing in the fryer
        FRYING, // frying something
        COMPLETE // finished frying
    }

    private State currentState;
    //Images
    private String emptyFryer = "/deepFried/fryer.png";
    private String frying = "/deepFried/fryer-!.png";
    private String complete = "/deepFried/fryer-tick.png";

    
    // Constructor
    public Fryer(int x, int y) {
        super(x, y);
        this.currentState = State.EMPTY;
        loadImage(emptyFryer); //need a fryer image
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
        switch (currentState) {
            case EMPTY:
                updateImage(emptyFryer);
                break;
            case FRYING:
                updateImage(frying);
                break;
            case COMPLETE:
                updateImage(complete);
                break;
            default:
                break;
        }
    }

    // Method to start cooking an ingredient (Take a prepped Ingredient and start a timer)
    public void startFrying(int fryTimeInSeconds) {
        setCurrentState(State.FRYING);
        totalTime = fryTimeInSeconds * 1000;
        remainingTime = totalTime;
        startTimer(remainingTime);
    }

    @Override
    public void startTimer(int time) {
        timer = new Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishFrying();
            }
        });
        timer.setRepeats(false);
        startTime = System.currentTimeMillis();
        timer.start();
    }
        
    // Method to finish frying
    private void finishFrying() {
        setCurrentState(State.COMPLETE);
        timer.stop();
        remainingTime = 0;
        timer = null;
        System.out.println("Frying complete! The ingredient is ready.");
    }

    // Method to place a prepped ingredient into the fryer
    public void placePreppedIngredient() {
        if (getCurrentState() == State.EMPTY) {
            startFrying(7);
            System.out.println("Placed an ingredient into the fryer.");
        } else {
            System.out.println("Fryer is currently in use");
        }
    }
    
    // Method to remove the fried ingredient from the fryer
    public void retrieveCookedFood() {
        if (getCurrentState() == State.COMPLETE) {
            setCurrentState(State.EMPTY);
            System.out.println("Retrieved cooked food");
        } else {
            System.out.println("The ingredient is not yet fully fried.");
        }
    }
    
}



