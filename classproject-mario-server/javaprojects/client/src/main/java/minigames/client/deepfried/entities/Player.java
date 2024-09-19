package minigames.client.deepfried.entities;

import java.util.ArrayList;


public class Player extends Entity {
    /**
     * Player class represents the player in the game
     * 
     * 
     */

    // The states a player can be in
    public enum State {
        MOVING,
        PREPPING,
        CARRYRAWONION,
        CARRYPREPONION,
        PLATING,
        SERVING
    }
    // Attributes 
    // The interaction distance, can interact with an item if it's close enough
    double interactionDistance = 100.0;
    private State currentState; // The current state of the player
    private int score; // the player's score
    //Image paths
    private String movingChef = "/deepFried/chefy.png";
    private String holdingRawOnion = "/deepFried/chef-onion.png";
    private String holdingPrepOnion = "/deepFried/chef-sliced.png";
    private String holdingPlatedOnion = "/deepFried/chef-dish.png";
    private String holdingPlate = "/deepFried/chef-plate.png";
    
    // Constructor
    public Player(int startX, int startY) {
        super(startX, startY);
        this.currentState = State.MOVING; // default state
        setScore(0);
        loadImage(movingChef); // loads the image of the player
        updateLocation();// update player location with coordinates   
    } 
    
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
        switch (currentState) {
            case MOVING:
                updateImage(movingChef);
                break;
            case PREPPING:
                updateImage(movingChef);
                break;
            case CARRYRAWONION:
                updateImage(holdingRawOnion);
                break;
            case CARRYPREPONION:
                updateImage(holdingPrepOnion);
                break;
            case PLATING:
                updateImage(holdingPlate);
                break;
            case SERVING:
                updateImage(holdingPlatedOnion);
                break;
            default:
                break;
        }     
    }

    // set location of playerImage on screen
    public void updateLocation(){
        getEntityImage().setLocation(this.x, this.y);
    }

    // Methods to move the player
    // Each checks player is in a state that can move, otherwise does nothing
    public void moveUp() {
        if (getCurrentState() != State.PREPPING) {
            //set bounds of where the player can move in the screen
            if(this.y > 20 && !(this.y == 283 && this.x >154 && this.x < 448 ) && !(this.y ==283 && this.x >609 && this.x <896)){
                this.y -= 6;
                updateLocation();
               // System.out.print("X: " + this.x + " Y: " +this.y);
            }
        }
    }
    public void moveDown() {
        if (getCurrentState() != State.PREPPING) {
            //set bounds of where the player can move in the screen
            if(this.y <433 && !(this.y == 133 && this.x > 154 && this.x < 448) && !(this.y ==133 && this.x > 609 && this.x < 896)){
                this.y += 6;
                updateLocation();
               // System.out.print("X: " + this.x + " Y: " +this.y); 
            }
        }  
    }
    public void moveLeft() {
        if (getCurrentState() != State.PREPPING) {
            //set bounds of where the player can move in the screen
            if(this.x >21 && !(this. x == 441 && this.y > 133 && this. y < 283) && !( this.x ==897 && this.y > 133 && this.y < 283)){
                this.x -= 6;
                updateLocation();
               // System.out.print("X: " + this.x + " Y: " +this.y); 
            }
        }  
    }
    public void moveRight() {
        if (getCurrentState() != State.PREPPING) {
            //set bounds of where the player can move in the screen
            if (this.x <994 && !(this.x ==153 && this.y > 133 && this.y < 283) && !(this.x ==609 && this.y >133 && this.y < 283)){
                this.x += 6;
                updateLocation();
               // System.out.print("X: " + this.x + " Y: " +this.y); 
            }
        } 
    }

    // Calculate distance between two points
    private double calculateDistance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    // function to be called when the player presses the 'interact' button
    // Takes a list of all entities in the game, and checks if any are close enough to the player
    public void checkInteract(ArrayList<Entity> entities){
        System.out.print("Checking Interaction");
        // System.out.print(entities.toString()); //to check contents of 'entities'
        if (!entities.isEmpty()){
            for (Entity entity : entities) {
                if (entity != this) {
                    double distance = calculateDistance(this.getCenterX(), this.getCenterY(), entity.getCenterX(), entity.getCenterY());
                    // If we're close enough, we interact
                    if (distance <= interactionDistance) {
                        // Call interact method to determine what to do from here
                        this.interact(entity);
                        break;
                    }
                }
            }
        }
    }

    // Handle different interactions if the client is close enough to an entity
    public void interact(Entity entity) {
        if (entity instanceof IngredientBox) {
            interactWithIngredientBox((IngredientBox) entity);
        } else if (entity instanceof Fryer) {
            interactWithFryer((Fryer) entity);
        } else if (entity instanceof PrepStation) {
            interactWithPrepStation((PrepStation) entity);
        } else if (entity instanceof SendOrderWindow) {
            interactWithSendOrderWindow((SendOrderWindow) entity);
        } else if (entity instanceof DishStack) {
            interactWithDishStack((DishStack) entity);
        } else if (entity instanceof BenchSurface) {
            interactWithBenchSurface((BenchSurface) entity);
        }
    }
    // Methods to interact with the game entities, should change states of player,
    // and potentially the entity it's interacting with
    public void interactWithIngredientBox(IngredientBox ingredientBox){
        // if in moving state, updates with the item carried by the player
        if (getCurrentState() == State.MOVING) {
            setCurrentState(State.CARRYRAWONION);
            System.out.println("Retrieved an Onion!");
        }
    }

    public void interactWithFryer(Fryer fryer){
        // If the fryer is empty and the chef is holding prepped onion
        if (getCurrentState() == State.CARRYPREPONION && fryer.getCurrentState() == Fryer.State.EMPTY) {
            fryer.placePreppedIngredient(); 
            setCurrentState(State.MOVING);
        }
         // If the food is fried and the chef is holding a plate
         if (getCurrentState() == State.PLATING && fryer.getCurrentState() == Fryer.State.COMPLETE) {
            fryer.retrieveCookedFood();
            // setItem(retrievedIngredient);
            setCurrentState(State.SERVING);
        }
    }

    // Player can't move while cutting the food
    public void interactWithPrepStation(PrepStation prepStation){
        // If we are putting food on the prep station
        if (getCurrentState() == State.CARRYRAWONION && prepStation.getCurrentState() == PrepStation.State.EMPTY) {
            prepStation.placeIngredient();
            setCurrentState(State.PREPPING);
        }
        // If the food is prepped
        if (getCurrentState() == State.PREPPING && prepStation.getCurrentState() == PrepStation.State.PREPPED) {
            prepStation.retrievePreppedIngredient();
            setCurrentState(State.CARRYPREPONION);
        }
    }

    public void interactWithDishStack(DishStack dishStack) {
        // If we're picking up a dish from the stack
       if (getCurrentState() == State.MOVING) {
            setCurrentState(State.PLATING);
       }
    }

    public void interactWithSendOrderWindow(SendOrderWindow sendOrderWindow){
        if (getCurrentState() == State.SERVING) {
                System.out.println("Order accepted!");
                setCurrentState(State.MOVING); // change state back to moving
                this.score += 10; // update score
                System.out.println("Current Score: " + getScore()); 
        }
    }

    public void interactWithBenchSurface(BenchSurface benchSurface) {
        // Placing an item on the bench
        if (benchSurface.getCurrentState() == BenchSurface.State.EMPTY && getCurrentState() != State.MOVING) {
            benchSurface.placeItem(getCurrentState());  
            setCurrentState(State.MOVING);
        }
        // Retrieving an item from the bench
        else if (benchSurface.getCurrentState() != BenchSurface.State.EMPTY && getCurrentState() == State.MOVING) {
            BenchSurface.State benchState = benchSurface.retrieveItem();
            switch (benchState) {
                case PLATE:
                    setCurrentState(State.PLATING);
                    break;
                case COMPLETEPLATE:
                    setCurrentState(State.SERVING);
                    break;
                case RAWONION:
                    setCurrentState(State.CARRYRAWONION);
                    break;
                case PREPPEDONION:
                    setCurrentState(State.CARRYPREPONION);
                    break;
                default:
                    break;
            } 
        }    
    } 
 }




