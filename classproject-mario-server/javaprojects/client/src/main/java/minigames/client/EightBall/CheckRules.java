package minigames.client.EightBall;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * This class contains methods that are run after the balls stop moving before the turn ends. 
 */
public class CheckRules {
    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(CheckRules.class);

    private GameState gameState;
    private List<Ball> ballList;
    private List<Ball> activeBallList;
    private Shooter shooter;

    public EngineState applyRules(List<Entity>active,List<Entity> pocketed, GameState _gameState)
    {
        logger.info("applying rules");
        ballList = new ArrayList<>();
        activeBallList = new ArrayList<>();
        List<Entity> eList= new ArrayList<>();
        eList.addAll(active);
        eList.addAll(pocketed);

        gameState = _gameState;
        for (Entity e:eList)
        {
            if (e instanceof Ball)
            {
                ballList.add((Ball)e);
            }
            if (e instanceof Shooter)
            {
                shooter = (Shooter)e;
            }
        } 

        for (Entity e:active) {
            if (e instanceof Ball) {
                activeBallList.add((Ball)e);
            }
        }

        if (!gameState.isPlayersBallTypesDetermined()) {
            //TODO: firstHitBall is null??
            logger.info("the first ball hit was" + shooter.getValue(ShooterValues.FirstHitBall).toString());
            if (shooter.getValue(ShooterValues.FirstHitBall) == BallType.STRIPES) {
                gameState.getCurrentPlayer().setPlayerBallType(BallType.STRIPES);
                gameState.setPlayersBallTypesDetermined(true);
                logger.info("The players ball type is now" + gameState.getCurrentPlayer().getPlayerBallType().toString());
            } else if (shooter.getValue(ShooterValues.FirstHitBall) == BallType.SOLIDS) {
                gameState.getCurrentPlayer().setPlayerBallType(BallType.SOLIDS);
                gameState.setPlayersBallTypesDetermined(true);
                logger.info("The players ball type is now" + gameState.getCurrentPlayer().getPlayerBallType().toString());
            }
            //TODO: set other players ball type to be the opposite of the current player
        }

        if(checkNoBallHit()) {
            //TODO: broken behaviour if you change the order of the next 2 lines
            gameState.getOtherPlayer().setNumOfShots(2);
            gameState.setCurrentPlayer(gameState.getOtherPlayer());
        }
        /*
         Pocketed balls contains the balls that were sunk in the current turn and the order they were sunk in
         */
        

        
        
        boolean eightBallSunk = false;
        boolean cueBallSunk = false;
        logger.info(pocketed.size());
        for (Entity ball : pocketed) {
            if (ball instanceof Ball)
            {if (ball.getValue(BallValues.BallType) == BallType.EIGHT) {
                logger.info("8 Ball is sunk");
                eightBallSunk = true;
            } else if (ball.getValue(BallValues.BallType) == BallType.CUE) {
                logger.info("Cue ball is sunk");
                cueBallSunk = true;
            }
         } }

        boolean changeOver = false;
        //TODO: checkNoBallHit();
        //TODO: if 8 ball and cue ball sunk on the same shot
        if (eightBallSunk) {
            applySunkEightBall();
        } else if (cueBallSunk) {
            applySunkCue();
        }
        return EngineState.Turn;
    }


    /*
     * Check whether any of the balls has been hit and if not penalises the player
     * Should be run at the end of every turn irrespective of ball pocketed events
     */
    public boolean checkNoBallHit() {
        return true;
        //if (gameState.getCurrentPlayer().getPlayerBallType() == BallType.NA) {
        ///    return false;
        //} else {
            //for (Ball ball:EightBallEngine.getBallInstances()) {
                //TODO: track whether the balls been hit
                // if (ball.hit() == false) //TODO: implement hit method
            //    return true;
            //}
        // }
    }

    /*
     * The player who sinks the 8 Ball either wins or looses
     * Called after the 8 Ball is sunk
     */
    public void applySunkEightBall() {
        // Check if the player hasn't sunk all their own balls
        for (Ball ball:activeBallList) {
            // If there is a ball that belongs the player still active
            if (ball.getValue(BallValues.BallType)==(gameState.getCurrentPlayer().getPlayerBallType())) {
                logger.info("You loose");
                //TODO: the current player looses
            } else {
                logger.info("YOU win");
            }
        }

        // The player has met the conditions to win the game
        //TODO: the current player wins
    }

    public void applySunkCue() {
        gameState.getOtherPlayer().setNumOfShots(2);
        //TODO: some variations of pool penalise sinking the cue ball after all the players object balls are sunk with a loss but the wikipedia article im basing this on does not seem to refer to that        
    }
}
