package minigames.client.EightBall;

import java.awt.geom.Point2D;
import java.text.RuleBasedCollator;
import java.util.*;

import io.vertx.core.json.JsonObject;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.*;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EightBallController extends Scene {

    private AnimationTimer gameCycle;
    private boolean gameRun = true;
    private double deltaTime = (double) (1000 / 240);
    private EightBallEngine engine;
    private EightBallRender render;


    private CheckRules checkRules;
    private GameState gameState;
    private int idTick = 0;
    private long lastMoment = (long) -1;

    private List<Entity> entitiesActive;
    private List<Entity> entitiesIdle;

    private List<Entity> entitiesPocketed;

    private Map<Inputs, KeyCode> keyBinds = new HashMap<>();
    private Map<Inputs, Boolean> keyInputs = new HashMap<>();

    /*
     * Fields for server communication
     * commandServer - The command server for the game, send through here
     * lastShot - The last shot made by the other player
     * ballPosition - The current position of the balls on the server
     */
    private EightBallCommands commandServer;
    private JsonObject lastShot;
    private JsonObject ballPosition;

    public EightBallController(Parent parent, double v, double v1, Canvas canvas) {
        //pass through parent arguments
        super(parent, v, v1);
        checkRules = new CheckRules();
        gameState= new GameState();

        init(canvas);

        keyBinds.put(Inputs.Down, KeyCode.DOWN);
        keyBinds.put(Inputs.Up, KeyCode.UP);
        keyBinds.put(Inputs.Left, KeyCode.LEFT);
        keyBinds.put(Inputs.Right, KeyCode.RIGHT);
        keyBinds.put(Inputs.PowerIncrease, KeyCode.UP);
        keyBinds.put(Inputs.PowerDecrease, KeyCode.DOWN);
        keyBinds.put(Inputs.RotateClockwise, KeyCode.RIGHT);
        keyBinds.put(Inputs.RotateAntiClockwise, KeyCode.LEFT);
        keyBinds.put(Inputs.Enter, KeyCode.SPACE);
        keyBinds.put(Inputs.Cancel, KeyCode.X);

        //fill the key values with false
        for (Inputs i:keyBinds.keySet()) {
            keyInputs.put(i,false);
        }

        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyInputEvent(event,true);
            }
        });

        this.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                keyInputEvent(event,false);
            }
        });
    }

    private void keyInputEvent(KeyEvent event, Boolean isPressed) {
        for (Inputs key : keyBinds.keySet()) {
            if (event.getCode() == keyBinds.get(key)) {
                keyInputs.put(key, isPressed);
            }
        }
    }

    public void mousePrimary()
    {
        keyInputs.put(Inputs.Enter, true);
    }
    public void mouseSecondary()
    {
        keyInputs.put(Inputs.Cancel, true);
    }

    public void mouseMove(float x, float y)
    {
        //Check if engine state is at player's turn
        if (engine.getEngineState()==EngineState.Turn)
        {
            engine.moveShooter(x,y);
        }
    }

    private void createBallRack(Point2D originPoint, double radius, double seperation) {
        //Create a rack of 15 balls
        //TODO: modify to include the proper ball types (e.g. striped, solid, 8ball etc.)
        int i =0;
        int rows = 5;
        int a =0;
        int cols = 1;
        while (i<rows) {
            while (a<cols) {
                entitiesActive.add(createBall(originPoint,radius,8, BallType.SOLIDS));
                a+=1;
                originPoint.setLocation(originPoint.getX()+((radius*2) + seperation),originPoint.getY());
            }
            originPoint.setLocation(originPoint.getX()-((radius*((a*2)+1)) + (seperation*(a+0.5))),originPoint.getY()-((radius*2) + seperation));
            a=0;
            cols+=1;
            i+=1;
        }
    }

    private Ball createBall(Point2D position, double radius, double mass, BallType ballType) {
        //createBall function constructs a new ball instance
        HashMap<BallValues, Object> values = new LinkedHashMap<>();
        values.put(BallValues.Position, position.clone());
        values.put(BallValues.Mass, mass);
        values.put(BallValues.Radius, radius);
        values.put(BallValues.Scale, 1.00);
        values.put(BallValues.Velocity, engine.getPointZero());
        values.put(BallValues.BallType, ballType);

        return new Ball(generateId(), values);
    }

    private Pocket createHole(Point2D position, double radius) {
        //createHole function constructs a new hole instance
        HashMap<PocketValues, Object> values = new LinkedHashMap<>();
        values.put(PocketValues.DrawPosition, position.clone());
        values.put(PocketValues.DrawRadius, radius);
        return new Pocket(generateId(), values);
    }
    private Table createTable(Point2D position, Point2D size) {
        HashMap<TableValues, Object> values = new LinkedHashMap<>();
        values.put(TableValues.Position, position.clone());
        values.put(TableValues.Size, size.clone());

        return new Table(generateId(), values);
    }

    private int generateId() {
        //generateId function gives a new id number when called
        idTick+=1;
        return idTick;
    }


    public Shooter createShooter(float _maxPower,Ball _cueBall) {
        HashMap<ShooterValues, Object> values = new LinkedHashMap<>();
        values.put(ShooterValues.Position, new Point2D.Float(0,0));
        values.put(ShooterValues.CueBall, _cueBall);
        values.put(ShooterValues.Angle,0f);
        values.put(ShooterValues.Power,0f);
        values.put(ShooterValues.PowerMax,_maxPower);
        values.put(ShooterValues.ShooterState,ShooterStates.Move);

        return new Shooter(generateId(), values);
    }

    public void init(Canvas canvas) {
        //Establish engine and render
        engine = new EightBallEngine();
        render = new EightBallRender(canvas);

        //Create instance
        entitiesActive = new ArrayList<Entity>();
        entitiesIdle = new ArrayList<Entity>();
        entitiesPocketed = new ArrayList<Entity>();
        //Setup Table
        Point2D tablePos = new Point2D.Float(150,270);
        Point2D tableSize = new Point2D.Float(700,350);

        entitiesActive.add(createTable(tablePos, tableSize));
        //Create Hole Instances
        double holeRadius = 20;
        // Just expanding the first instance to make it easier to read
        entitiesActive.add(createHole(new Point2D.Float((float) (tablePos.getX()+(holeRadius/2)),
                                                        (float)(tablePos.getY()+(holeRadius/2))), holeRadius));
        entitiesActive.add(createHole(new Point2D.Float((float)(tablePos.getX()+(tableSize.getX()/2)),(float)tablePos.getY()),holeRadius));
        entitiesActive.add(createHole(new Point2D.Float((float)(tablePos.getX()+(tableSize.getX())-(holeRadius/2)),(float)(tablePos.getY()+(holeRadius/2))),holeRadius));

        entitiesActive.add(createHole(new Point2D.Float((float)(tablePos.getX()+(holeRadius/2)),(float)(tablePos.getY()+tableSize.getY()-(holeRadius/2))),holeRadius));
        entitiesActive.add(createHole(new Point2D.Float((float)(tablePos.getX()+(tableSize.getX()/2)),(float)(tablePos.getY()+tableSize.getY())),holeRadius));
        entitiesActive.add(createHole(new Point2D.Float((float)(tablePos.getX()+(tableSize.getX())-(holeRadius/2)),(float)(tablePos.getY()+tableSize.getY()-(holeRadius/2))),holeRadius));

        //Create ball instances
        Point2D spawnPoint = new Point2D.Float(720,500);
        createBallRack(spawnPoint, 12, 4);

        //Create cueball and shooter
        Ball cueBall = createBall(spawnPoint,8,8, BallType.CUE);
        entitiesActive.add(cueBall);
        entitiesActive.add(createShooter(1000,cueBall));


        //gameCycle establishes the game loop.
        gameCycle = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //Functions the game will perform every tick.
                if (lastMoment < 0) {
                    lastMoment = now;
                }
                float deltaTime = (float)((now - lastMoment)/(1e9));
                lastMoment = now;
                if (gameRun) {
                    //Clears canvas for upcoming game state.
                    render.prepare();
                    //Run a step in the engine
                    engine.update(deltaTime,new ArrayList<>(entitiesActive), new HashMap<>(keyInputs));
            
                    //We want Enter when pressed, not down
                    keyInputs.put(Inputs.Enter,false);
                    keyInputs.put(Inputs.Cancel,false);
                    //Uncomment to see FPS
                    //System.out.println(1/(deltaTime));

                    //find sunk balls and transfer them to pocketedEntities
                    int i;
                    for (i=0; i<entitiesActive.size(); i++) {
                        Entity e = entitiesActive.get(i);
                        if (e instanceof Ball) {
                            if ((boolean)((Ball) e).getValue(BallValues.FullySunk,false)) { 
                                e.setValue(BallValues.FullySunk, false);
                                e.setValue(BallValues.SinkingIn, -1);
                                e.setValue(BallValues.Scale, 1.00);
                                entitiesPocketed.add(e);
                                entitiesActive.remove(e);
                                i-=1;
                            }
                        }
                    }
                    if (engine.getEngineState()==EngineState.Send)
                    {
//Ready to send object information
                    }

                    if (engine.getEngineState()==EngineState.Waiting)
                    {
//Waiting to receive commands

                    }

                    if (engine.getEngineState()==EngineState.Outcome) {
                        engine.setState(checkRules.applyRules(entitiesActive,entitiesPocketed,gameState));
                        //Clear out pocketed entities
                        for (i=0; i<entitiesPocketed.size(); i++) {
                            Entity e = entitiesPocketed.get(i);
                            if (e instanceof Ball) {
                                entitiesIdle.add(e);
                                entitiesPocketed.remove(e);
                                i-=1;
                            }
                        }
                    }

                    //Check if its the players next turn
                    checkNextTurn();
                    //Draws game state;
                    render.render(new ArrayList<>(entitiesActive));
                } else {
                    gameCycle.stop();
                }
            }
        };
        gameCycle.start();
    }

    private void checkNextTurn() {
        if (engine.getEngineState()==EngineState.Turn) {
        //Look for Shooter object
            Shooter shooter = null;
            for (Entity e: entitiesActive) {
                if (e instanceof Shooter) {
                    //Check Shooter is off
                    if ((ShooterStates)e.getValue(ShooterValues.ShooterState,ShooterStates.Off) == ShooterStates.Off) {
                        shooter = (Shooter) e;
                    }
                }
            }
            if (shooter!=null) {
                //Reset Shooter
                shooter.setValue(ShooterValues.ShooterState,ShooterStates.Move);
                shooter.setValue(ShooterValues.Power, 0.0f);
                shooter.setValue(ShooterValues.Angle, 0.0f);
                shooter.setValue(ShooterValues.FirstHitBall, BallType.NEUTRAL);

                int cueBallId = ((Ball) shooter.getValue(ShooterValues.CueBall)).getId();

                //Look for cueBall
                int i;
                //Check if cue ball is in Pocketed
                for (i=0; i< entitiesPocketed.size(); i++) {
                    Entity isCueBall = entitiesPocketed.get(i);
                    if (isCueBall.getId()==cueBallId) {   
                        entitiesActive.add((Ball)entitiesPocketed.remove(i));
                        break;
                    }
                }
                //Check if cueBall is in idle
                for (i=0; i < entitiesIdle.size(); i++) {
                    Entity isCueBall = entitiesIdle.get(i);
                    if (isCueBall.getId()==cueBallId) {
                        isCueBall.setValue(BallValues.Scale, 1.00);
                        entitiesActive.add(entitiesIdle.remove(i));
                        break;
                    }
                }  
            }
        }
    }

    /**
     * Returns the engine for the EightBall game.
     * Just used to pass the commandServer to the engine.
     * @return the engine for the EightBall game
     */
    public EightBallEngine getEngine() {
        return engine;
    }

    /**
     * Passes the commandServer to the engine.
     * Allows for command execution across the game
     * @param commandServer the commandServer for the EightBall game, created in EightBallGame
     */
    public void setCommandServer(EightBallCommands commandServer) {
        this.commandServer = commandServer;
    }

    /**
     * Updating game state after other player has made a move
     * @param ballPosition Correct ball positions. See EightBallCommands for format
     */
    public void setBallPos(JsonObject ballPosition) {
        this.ballPosition = ballPosition;
    }

    /**
     * Used to "replay" the other player's last shot
     * @param lastShot The shot the last player made. See EightBallCommands for format
     */
    public void setLastShot(JsonObject lastShot) {
        this.lastShot = lastShot;
    }
}




    

