package minigames.client.EightBall;

import minigames.client.EightBall.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class EngineTest {
private EightBallEngine engine;
private EightBallController controller;
private int idCounter=0;
    private Ball createBall(Point2D position, double radius, double mass, Point2D velocity) {
        //createBall function constructs a new ball instance
        HashMap<BallValues, Object> values = new LinkedHashMap<>();
        values.put(BallValues.Position, position.clone());
        values.put(BallValues.Mass, mass);
        values.put(BallValues.Radius, radius);
        values.put(BallValues.Scale, 1.00);
        values.put(BallValues.Velocity,velocity);
        idCounter++;

        return new Ball(idCounter, values);
    }

    private Table createTable(Point2D position, Point2D size) {
        HashMap<TableValues, Object> values = new LinkedHashMap<>();
        values.put(TableValues.Position, position.clone());
        values.put(TableValues.Size, size.clone());
        idCounter++;

        return new Table(idCounter, values);
    }
    public Shooter createShooter(Ball _cueBall) {
        HashMap<ShooterValues, Object> values = new LinkedHashMap<>();
        values.put(ShooterValues.Position, new Point2D.Float(0,0));
        values.put(ShooterValues.CueBall, _cueBall);
        values.put(ShooterValues.Angle,0f);
        values.put(ShooterValues.Power,0f);
        values.put(ShooterValues.ShooterState,ShooterStates.Move);
        idCounter++;
        return new Shooter(idCounter, values);
    }
    @BeforeEach
    void setUp() {
        engine = new EightBallEngine();
    }

    @Test
    void testBallCollision1() {
//First collision scenario checks directions of balls when a moving ball directly hits a stationary ball.

        Ball ball1 = createBall(new Point2D.Float(0,40),10,1,new Point2D.Float(100,0));
        Ball ball2 = createBall(new Point2D.Float(41,40),10,1,new Point2D.Float(0,0));

        List<Entity> entities = new ArrayList<>();
        entities.add(createTable(new Point2D.Float(-1000,-1000),new Point2D.Float(2000,2000)));
        entities.add(createShooter(null));
        entities.add(ball1);
        entities.add(ball2);

        HashMap<Inputs,Boolean> inputs = new HashMap<>();

        engine.setState(EngineState.Simulate);
        engine.update(1f,entities,inputs);

        //check ball1 is moving left
        System.out.println( engine.normalise((Point2D) ball1.getValue(BallValues.Velocity,engine.getPointZero())));
        System.out.println( engine.normalise((Point2D) ball2.getValue(BallValues.Velocity,engine.getPointZero())));
        assertEquals(engine.pointDiff( engine.normalise((Point2D) ball1.getValue(BallValues.Velocity,engine.getPointZero())),new Point2D.Float(-1,0)).distance(engine.getPointZero()),0,0.01);

        //check ball2 is moving right
        assertEquals(engine.pointDiff( engine.normalise((Point2D) ball2.getValue(BallValues.Velocity,engine.getPointZero())),new Point2D.Float(1,0)).distance(engine.getPointZero()),0,0.01);

    }
@Test
@Disabled
    void testBallCollision2() {
//Second collision scenario checks directions of balls; when a moving ball indirectly hits a stationary ball.
        Ball ball1 = createBall(new Point2D.Float(0,40),10,1,new Point2D.Float(100,0));
        Ball ball2 = createBall(new Point2D.Float(41,45),10,1,new Point2D.Float(0,0));

        List<Entity> entities = new ArrayList<>();
        entities.add(createTable(new Point2D.Float(-1000,-1000),new Point2D.Float(2000,2000)));
        entities.add(createShooter(null));
        entities.add(ball1);
        entities.add(ball2);

        HashMap<Inputs,Boolean> inputs = new HashMap<>();

        engine.setState(EngineState.Simulate);
        engine.update(1f,entities,inputs);

        System.out.println( engine.normalise((Point2D) ball1.getValue(BallValues.Velocity,engine.getPointZero())));
        System.out.println( engine.normalise((Point2D) ball2.getValue(BallValues.Velocity,engine.getPointZero())));

        //check ball1 is moving left and up

        assertEquals(engine.pointDiff( engine.normalise((Point2D) ball1.getValue(BallValues.Velocity,engine.getPointZero())),new Point2D.Float(-0.707f,-0.707f)).distance(engine.getPointZero()),0,0.01);

        //check ball2 is moving right and down
        assertEquals(engine.pointDiff( engine.normalise((Point2D) ball2.getValue(BallValues.Velocity,engine.getPointZero())),new Point2D.Float(0.707f,0.707f)).distance(engine.getPointZero()),0,0.01);


    }
}
