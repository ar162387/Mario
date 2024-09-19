package minigames.client.deepfried;

import org.junit.jupiter.api.*;

import minigames.client.deepfried.entities.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/*
 * Run with ./gradlew :client:test --tests "minigames.client.deepfried.PlayerTest"
 * Output is in \classproject\javaprojects\client\build\reports\tests\test\index.html
 */

public class PlayerTest {
    private static Player player;
    private static IngredientBox ingredientBox;
    private static Fryer fryer;
    private static PrepStation prepStation;
    private static SendOrderWindow sendOrderWindow;
    private static DishStack dishStack;
    private static BenchSurface benchSurface;

    @BeforeAll
    public static void setUp() {
        player = new Player(50, 50);
        
        ingredientBox = mock(IngredientBox.class);
        fryer = mock(Fryer.class);
        prepStation = mock(PrepStation.class);
        sendOrderWindow = mock(SendOrderWindow.class);
        dishStack = mock(DishStack.class);
        benchSurface = mock(BenchSurface.class);
    }

    @Test
    public void testInteractWithIngredientBox() {
        
        // Set up the player in the MOVING state
        player.setCurrentState(Player.State.MOVING);
        // Interact with the IngredientBox
        player.interact(ingredientBox);
        // Verify that the player's state is now CARRYITEM
        assertEquals(Player.State.CARRYRAWONION, player.getCurrentState());
        // Verify that player cannot pick up an ingredient if they are not in moving state
        player.setCurrentState(Player.State.PLATING);
        player.interact(ingredientBox);
        assertEquals(Player.State.PLATING, player.getCurrentState());
    }
    @Test
    public void testInteractWithFryer() {
        // set up entity behaviour for the tests
        when(fryer.getCurrentState())
        .thenReturn(Fryer.State.EMPTY)     
        .thenReturn(Fryer.State.COMPLETE);

        // Set up the player in the CARRYPREPONION state
        player.setCurrentState(Player.State.CARRYPREPONION);
        fryer.setCurrentState(Fryer.State.EMPTY);
        // Interact with the Fryer
        player.interact(fryer);
        // Verify that the player's state is now MOVING (placed the ingredient in the fryer)
        assertEquals(Player.State.MOVING, player.getCurrentState());
        // Verify that the player can remove an ingredient from the fryer when in PLATING state
        player.setCurrentState(Player.State.PLATING);
        fryer.setCurrentState(Fryer.State.COMPLETE);
        player.interact(fryer);
        assertEquals(Player.State.SERVING, player.getCurrentState());
    }
    @Test
    public void testInteractWithPrepStation() {
        // set up entity behaviour for the tests
        when(prepStation.getCurrentState())
        .thenReturn(PrepStation.State.EMPTY)     
        .thenReturn(PrepStation.State.PREPPING)
        .thenReturn(PrepStation.State.PREPPED);
        // Set up the player in the CARRYRAWONION state
        player.setCurrentState(Player.State.CARRYRAWONION);
        // Interact with the IngredientBox
        player.interact(prepStation);
        // Verify that the player's state is now PREPPING
        assertEquals(Player.State.PREPPING, player.getCurrentState());
        // Verify that the onion can be retrieved
        player.interact(prepStation);
        assertEquals(Player.State.CARRYPREPONION, player.getCurrentState());
        // Verify that player cannot begin prepping if they are not holding a raw ingredient
        player.setCurrentState(Player.State.PLATING);
        player.interact(prepStation);
        assertEquals(Player.State.PLATING, player.getCurrentState());
    }
    @Test
    public void testInteractWithSendOrderWindow() {
      
        // Set up the player in the Serving state (holding a complete plate)
        player.setCurrentState(Player.State.SERVING);
        // Interact with the sendOrderwindow
        player.interact(sendOrderWindow);
        // Verify that the player's state is now MOVING and the score has increased by 10 points
        assertEquals(Player.State.MOVING, player.getCurrentState());
        assertEquals(10, player.getScore());
        // Verify that player cannot send an order if they are not in the SERVING state
        player.setCurrentState(Player.State.PLATING);
        player.interact(sendOrderWindow);
        assertEquals(Player.State.PLATING, player.getCurrentState());
    }
    @Test
    public void testInteractWithDishStack() {
        // Set up the player in the MOVING state
        player.setCurrentState(Player.State.MOVING);
        // Interact with the dishStack
        player.interact(dishStack);
        // Verify that the player's state is now PLATING (i.e. retrieved a dish)
        assertEquals(Player.State.PLATING, player.getCurrentState());
        // Verify that player cannot pick up a plate if they are not in moving state
        player.setCurrentState(Player.State.CARRYRAWONION);
        player.interact(dishStack);
        assertEquals(Player.State.CARRYRAWONION, player.getCurrentState());
    }
    @Test
    public void testInteractWithBenchSurface() {
        // set up entity behaviour for the tests
        when(benchSurface.getCurrentState())
        .thenReturn(BenchSurface.State.EMPTY)     
        .thenReturn(BenchSurface.State.PLATE)
        .thenReturn(BenchSurface.State.EMPTY)     
        .thenReturn(BenchSurface.State.RAWONION)
        .thenReturn(BenchSurface.State.EMPTY)     
        .thenReturn(BenchSurface.State.PREPPEDONION)
        .thenReturn(BenchSurface.State.EMPTY)     
        .thenReturn(BenchSurface.State.COMPLETEPLATE);
        // Placing a plate on the bench
        player.setCurrentState(Player.State.PLATING);
        player.interact(benchSurface);
        assertEquals(Player.State.MOVING, player.getCurrentState());
        assertEquals(BenchSurface.State.PLATE, benchSurface.getCurrentState());
        // Placing a raw onion on the bench
        player.setCurrentState(Player.State.CARRYRAWONION);
        player.interact(benchSurface);
        assertEquals(Player.State.MOVING, player.getCurrentState());
        assertEquals(BenchSurface.State.RAWONION, benchSurface.getCurrentState());
        // Placing a prepped onion on the bench
        player.setCurrentState(Player.State.CARRYPREPONION);
        player.interact(benchSurface);
        assertEquals(Player.State.MOVING, player.getCurrentState());
        assertEquals(BenchSurface.State.PREPPEDONION, benchSurface.getCurrentState());
        // Placing a complete plate on the bench
        player.setCurrentState(Player.State.SERVING);
        player.interact(benchSurface);
        assertEquals(Player.State.MOVING, player.getCurrentState());
        assertEquals(BenchSurface.State.COMPLETEPLATE, benchSurface.getCurrentState());
   
    }
    // @Test
    // public void testInteractWithIngredientBox() {
    //     // Set up the player in the MOVING state
    //     player.setCurrentState(Player.State.MOVING);

    //     // Mock the ingredient to be retrieved
    //     Ingredient mockIngredient = mock(Ingredient.class);
    //     when(ingredientBox.retrieveIngredient()).thenReturn(mockIngredient);

    //     // Interact with the IngredientBox
    //     player.interact(ingredientBox);

    //     // Verify that the player is now carrying the item
    //     assertEquals(mockIngredient, player.getItem());

    //     // Verify that the player's state is now CARRYITEM
    //     assertEquals(Player.State.CARRYITEM, player.getCurrentState());
    // }
}
