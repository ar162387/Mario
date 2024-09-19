package minigames.client.deepfried.entities;

public class BenchSurface extends Entity {
    /**
     * The bench surface class that defines the bench to place things on
     * 
     * TO DO: make sure this can be divided for each "square" of the bench and implement a new BenchSurface for each Square.
     * 
     */
    public enum State{
        EMPTY,
        PLACING,
        PLATE,
        COMPLETEPLATE,
        RAWONION,
        PREPPEDONION
    }
     // Attributes
    private State currentState;
    private String emptyBench = "/deepFried/benchSurface.png";
    private String emptyPlate = "/deepFried/plate.png";
    private String completePlate = "/deepFried/preppedDish.png";
    private String rawOnion = "/deepFried/whole-onion.png";
    private String preppedOnion = "/deepFried/sliced-onion.png";

    // Constructor
    public BenchSurface(int x, int y) {
        super(x, y);
        this.currentState = State.EMPTY;
        loadImage(emptyBench);
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
        switch (currentState) {
            case EMPTY:
                updateImage(emptyBench);
                break;
            case PLATE:
                updateImage(emptyPlate);
                break;
            case COMPLETEPLATE:
                updateImage(completePlate);
                break;
            case RAWONION:
                updateImage(rawOnion);
                break;
            case PREPPEDONION:
                updateImage(preppedOnion);
                break;
            default:
                break;
        }
    }

    // Method to place an item on the bench
    public void placeItem(Player.State playerState) {
        // timer.cancel();
        if (getCurrentState() == State.EMPTY) {
            // add the item and change state
            switch (playerState) {
                case CARRYRAWONION:
                    setCurrentState(State.RAWONION);
                    break;
                case CARRYPREPONION:
                    setCurrentState(State.PREPPEDONION);
                    break;
                case PLATING:
                    setCurrentState(State.PLATE);
                    break;
                case SERVING:
                    setCurrentState(State.COMPLETEPLATE);
                    break;
                default:
                    break;
            }
            System.out.println("Placed an item");
        } 
    }
    // Method to retrieve the item from the bench
    public BenchSurface.State retrieveItem() {
        if (getCurrentState() != State.EMPTY) {
            BenchSurface.State benchState = getCurrentState();
            // Remove item and change state
            setCurrentState(State.EMPTY);
            System.out.println("retrieved an item");
            return benchState;
        } else {
            return null;
        }
    }
    
}
