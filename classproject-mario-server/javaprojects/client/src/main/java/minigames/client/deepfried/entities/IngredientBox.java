package minigames.client.deepfried.entities;


public class IngredientBox extends Entity {
    /**
     * The ingredient box entity
     * 
     * Contains the logic for retrieving the ingredients box
     * 
     */
 
    // Constructor
    public IngredientBox(int x, int y) {
        super(x, y);
        loadImage("/deepFried/ingredientBox.png");
    }
  
}
