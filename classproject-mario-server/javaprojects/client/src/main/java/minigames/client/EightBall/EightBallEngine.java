package minigames.client.EightBall;

import java.awt.geom.Point2D;
import java.util.*;
import io.vertx.core.json.JsonObject;

public class EightBallEngine {
    //Simulates game physics and keeps track of instances

    private final Point2D pointZero = new Point2D.Double(0, 0);
    private float frictionFloor = 15f;
    //Potential idea, balls change speed when they collide.
    private float frictionBounce = 15f;
    private float sinkSpeed = 32;

    private List<Ball> entityBalls;
    private Table entityTable;
    private Shooter entityShooter;
    private List<Pocket> entityPockets;
    private Random rdm = new Random();

    private float cueRotate = (float) Math.PI;
    private float cuePowerChange = 0.7f;
    private float cueMove = 125;

    private EngineState engineState = EngineState.Turn;

    private float transitionCur = 0;
    private float transitionTime = 2.0f;

    

    public EightBallEngine() {
    }

    private void sortEntities(List<Entity> entities) {
        entityBalls = new ArrayList<>();
        entityPockets = new ArrayList<>();
        entityTable = null;

        for (Entity e : entities) {
            if (e instanceof Ball) {
                entityBalls.add((Ball) e);
            } else if (e instanceof Pocket) {
                entityPockets.add((Pocket) e);
            } else if (e instanceof Table) {
                entityTable = (Table) e;
            } else if (e instanceof Shooter) {
                entityShooter = (Shooter) e;
            }
        }
    }

    public Point2D getPointZero() {
        return (Point2D) pointZero.clone();
    }

    public Point2D giveImpulse(double spd) {
        //giveImpulse, creates a new vector in a certain length at a random direction, when no direction vector is given
        return giveImpulse(spd, new Point2D.Double(rdm.nextDouble(100) - 50, rdm.nextDouble(100) - 50));
    }

    public Point2D giveImpulse(double spd, Point2D dir) {
        //giveImpulse, creates a new vector in a certain length and certain direction
        dir = normalise((Point2D) dir.clone());
        return new Point2D.Double(dir.getX() * spd, dir.getY() * spd);
    }

    public Point2D pointMove(Point2D pnt1, Point2D pnt2) {
        //pointMove, returns a point as if pnt1 was moved by the given distance of pnt2
        return new Point2D.Double(pnt1.getX() + pnt2.getX(), pnt1.getY() + pnt2.getY());
    }

    public Point2D pointDiff(Point2D pnt1, Point2D pnt2) {
        //pointMove, returns the difference of coordinates between pnt1 and pnt2
        return new Point2D.Double(pnt1.getX() - pnt2.getX(), pnt1.getY() - pnt2.getY());
    }

    private double circleMeet(double diffX, double moveX, double diffY, double moveY, double totalR) {
        //Work in progress
        //Returns an output of a quadratic equation, when two ball are about to meet
        double moveSqr = Math.pow(moveX, 2) + Math.pow(moveY, 2);
        if (moveSqr < 0.01) {
            return 0;
        }
        double t1 = (-(moveX * diffX) - (moveY * diffY) + Math.sqrt(-(Math.pow(moveY, 2) * Math.pow(diffX, 2)) + (2 * moveX * moveY * diffX * diffY) + (Math.pow(moveX, 2) * totalR) + (Math.pow(moveY, 2) * totalR) - (Math.pow(moveX, 2) * Math.pow(diffY, 2)))) / moveSqr;
        double t2 = -((moveX * diffX) + (moveY * diffY) + Math.sqrt(-(Math.pow(moveY, 2) * Math.pow(diffX, 2)) + (2 * moveX * moveY * diffX * diffY) + (Math.pow(moveX, 2) * totalR) + (Math.pow(moveY, 2) * totalR) - (Math.pow(moveX, 2) * Math.pow(diffY, 2)))) / moveSqr;

        return Math.max(t1, t2);
    }

    private Boolean checkPlacementLegal(Ball ball) {

        //Moves ball instance, checking for collisions and calculate trajectory.
        Point2D ballPos = (Point2D) ball.getValue(BallValues.Position, pointZero.clone());
        double ballRadius = ((double) ball.getValue(BallValues.Radius, 0)) * ((double) ball.getValue(BallValues.Scale, 1.00));

        //Get table values
        Point2D tablePos = (Point2D) entityTable.getValue(TableValues.Position);
        Point2D tableSize = (Point2D) entityTable.getValue(TableValues.Size);

        //Check if ball is colliding with table
        if (((ballPos.getX()) - ballRadius <= tablePos.getX()) || (ballPos.getX() + ballRadius >= tablePos.getX() + tableSize.getX()) || (ballPos.getY() - ballRadius <= tablePos.getY()) || (ballPos.getY() + ballRadius >= tablePos.getY() + tableSize.getY())) {
            return false;
        }

        //Loop through other Hole instances checking for collisions
        for (Pocket p : entityPockets) {

            //Temporarily store pocket values
            Point2D pocketPos = (Point2D) p.getValue(PocketValues.DrawPosition, pointZero.clone());
            Double pocketRadius = (double) p.getValue(PocketValues.DrawRadius, 0);
            if (!((boolean) p.getValue(PocketValues.IsFull, false))) {
                //Check if ball is going to collide with hole. If so, it begins to sink
                if (new Point2D.Double(ballPos.getX(), ballPos.getY()).distance(pocketPos) < ballRadius + pocketRadius) {
                    return false;
                }
            }
        }

        //Loop through other ball instances checking for collisions
        for (Ball b : entityBalls) {
            if (b.getId() != ball.getId()) {
                //Temporarily store other ball values
                Point2D b2Pos = (Point2D) b.getValue(BallValues.Position, pointZero.clone());
                Double b2Radius = ((double) b.getValue(BallValues.Radius, 0)) * ((double) b.getValue(BallValues.Scale, 1.00));

                //Check if ball is inside another ball
                if (new Point2D.Double(ballPos.getX(), ballPos.getY()).distance(b2Pos) < ballRadius + b2Radius) {
                    return false;
                }
            }
        }

        return true;
    }

    private void moveCheck(Ball ball, double deltaTime) {

        //Moves ball instance, checking for collisions and calculate trajectory.
        Point2D ballPos = (Point2D) ball.getValue(BallValues.Position, pointZero.clone());
        double ballRadius = ((double) ball.getValue(BallValues.Radius, 0)) * ((double) ball.getValue(BallValues.Scale, 1.00));
        Point2D ballVel = (Point2D) ball.getValue(BallValues.Velocity, pointZero.clone());
        BallType ballType = (BallType) ball.getValue(BallValues.BallType,BallType.NEUTRAL);
        int sinkingIn = (int) ball.getValue(BallValues.SinkingIn, -1);
        //Temporarily store ball values
        double stepTest = 10;
        double ballSpd = ballVel.distance(pointZero);

        double remSpd = Math.max(0, (ballSpd * deltaTime) - (0.5 * frictionFloor * Math.pow(deltaTime, 2)));
        Point2D testVec = normalise(ballVel);

        double testSpd = 0;

        //Get table values
        Point2D tablePos = (Point2D) entityTable.getValue(TableValues.Position);
        Point2D tableSize = (Point2D) entityTable.getValue(TableValues.Size);
        //Loop until ball has travelled the distance of remSpd
        while (remSpd > 0) {
            //Prepare min values to track the closest collision
            Ball minBall = null;
            testSpd = Math.min(stepTest, remSpd);
            double minSpd = testSpd;
            double pushForce = 0;

            Point2D minVec = (Point2D) testVec.clone();
            //Don't check for table is sinking.
            if (sinkingIn == -1) {
                //Check if ball is going to collide with table, if so decrease travel distance
                while ((ballPos.getX() + (testVec.getX() * (testSpd - pushForce)) - ballRadius <= tablePos.getX()) || (ballPos.getX() + (testVec.getX() * (testSpd - pushForce)) + ballRadius >= tablePos.getX() + tableSize.getX()) || (ballPos.getY() + (testVec.getY() * (testSpd - pushForce)) - ballRadius <= tablePos.getY()) || (ballPos.getY() + (testVec.getY() * (testSpd - pushForce)) + ballRadius >= tablePos.getY() + tableSize.getY())) {
                    pushForce += 1;
                }
                //Check if ball was going to collide with table
                if (pushForce > 0) {
                    minSpd = testSpd - pushForce;

                    //Figure which direction the ball should bounce
                    if ((ballPos.getX() + (testVec.getX() * stepTest) - ballRadius <= tablePos.getX()) || (ballPos.getX() + (testVec.getX() * stepTest) + ballRadius >= tablePos.getX() + tableSize.getX())) {
                        minVec = new Point2D.Double(testVec.getX() * -1, testVec.getY());
                    } else if ((ballPos.getY() + (testVec.getY() * stepTest) - ballRadius <= tablePos.getY()) || (ballPos.getY() + (testVec.getY() * stepTest) + ballRadius >= tablePos.getY() + tableSize.getY())) {
                        minVec = new Point2D.Double(testVec.getX(), testVec.getY() * -1);
                    }
                    pushForce = 0;
                }
            } else {
                //If ball is sinking pocket, set sinking behaviour
                Pocket p = getPocket(sinkingIn);
                Point2D pPos = (Point2D) p.getValue(PocketValues.DrawPosition);

                //Ball will continually to move towards centre of pocket
                minSpd = Math.min(testSpd, ballPos.distance(pPos));
                if (minSpd <= 0.01) {
                    remSpd = 0;
                }
                testVec = giveImpulse(1, pointDiff(pPos, ballPos));
            }
            //Loop through other Hole instances checking for collisions
            for (Pocket p : entityPockets) {

                //Temporarily store pocket values
                Point2D pocketPos = (Point2D) p.getValue(PocketValues.DrawPosition, pointZero.clone());
                Double pocketRadius = (double) p.getValue(PocketValues.DrawRadius, 0);
                if (!((boolean) p.getValue(PocketValues.IsFull, false))) {
                    //Check if ball is going to collide with hole. If so, it begins to sink
                    if (new Point2D.Double(ballPos.getX() + (testVec.getX() * (testSpd - pushForce)), ballPos.getY() + (testVec.getY() * (testSpd - pushForce))).distance(pocketPos) < ballRadius + pocketRadius) {
                        //Set ball to sink in pocket
                        sinkingIn = p.getId();
                        p.setValue(PocketValues.IsFull, true);
                    }
                }
            }

            //Loop through other ball instances checking for collisions
            for (Ball b : entityBalls) {
                if (b.getId() != ball.getId()) {
                    //Temporarily store other ball values
                    Point2D b2Pos = (Point2D) b.getValue(BallValues.Position, pointZero.clone());
                    Double b2Radius = ((double) b.getValue(BallValues.Radius, 0)) * ((double) b.getValue(BallValues.Scale, 1.00));
                    Point2D b2Vel = (Point2D) b.getValue(BallValues.Velocity, pointZero.clone());

                    Point2D newVel = (Point2D) ballVel.clone();
                    //Check if ball is going to collide with table, if so decrease travel distance
                    while (new Point2D.Double(ballPos.getX() + (testVec.getX() * (testSpd - pushForce)), ballPos.getY() + (testVec.getY() * (testSpd - pushForce))).distance(b2Pos) < ballRadius + b2Radius) {
                        //Check if ball was already inside other ball
                        if (ballPos.distance(b2Pos) < ballRadius + b2Radius) {
                            pushForce = testSpd;
                            remSpd = 0;
                            break;
                        }
                        pushForce += 1;
                    }

                    //Check if the ball could even move
                    if (pushForce > stepTest - 0.5) {
                        remSpd -= pushForce;
                    }
                    //Check if ball did collide with another ball
                    else if (minSpd > testSpd - pushForce && pushForce > 0.01) {


                        //Calculate new bounce direction
                        Point2D predPoint = pointMove(ballPos, giveImpulse(testSpd, testVec));

                        Point2D dir = pointDiff(predPoint,b2Pos);
                        Point2D tangent = new Point2D.Double(dir.getY(), dir.getX());
                        Double tangentAngle = Math.atan2(tangent.getY(), tangent.getX());
                        Double velocityAngle = Math.atan2(testVec.getY(), testVec.getX());
                        Double newAngle = (tangentAngle + (Math.PI) - (velocityAngle));
                        //System.out.println("Old Angle: "+(velocityAngle*180/(2*Math.PI))+" New Angle: "+(newAngle*180/(2*Math.PI)));
                        //minVec = new Point2D.Double(Math.cos(newAngle), -1 * Math.sin(newAngle));
                        //System.out.println("Created new angle:" +(Math.atan2(minVec.getY(),minVec.getX()))*180/(2*Math.PI));

                        minVec=giveImpulse(1,dir);

                        minSpd = testSpd - pushForce;
                        minBall = b;
                        pushForce = 0;
                    }
                }
            }

            //Remove travelled distance from remSpd
            remSpd -= minSpd;
            //update ball position
            ballPos = pointMove(ballPos, new Point2D.Double(testVec.getX() * minSpd, testVec.getY() * minSpd));
            testVec = (Point2D) minVec.clone();
            //Check if there was a ball collision
            if (minBall != null) {



                //Calculate new ballSpd
                ballSpd = (ballSpd * 0.5)+frictionBounce;
                //Temp store other ball values;
                Point2D minBPos = (Point2D) minBall.getValue(BallValues.Position, pointZero.clone());
                Double minBRadius = ((double) minBall.getValue(BallValues.Radius, 0) * ((double) minBall.getValue(BallValues.Scale, 1.00)));
                Point2D minBVel = (Point2D) minBall.getValue(BallValues.Velocity, pointZero.clone());
                Double minBSpd = minBVel.distance(pointZero);
                BallType minBType = (BallType) minBall.getValue(BallValues.BallType, BallType.NEUTRAL);
                //check if shooter has yet to hit a ball

                    if ((BallType)entityShooter.getValue(ShooterValues.FirstHitBall,BallType.NEUTRAL)==BallType.NEUTRAL) {
                        //check if one the balls that collided is the cue ball;
                        if (ballType==BallType.CUE)
                        {
                            entityShooter.setValue(ShooterValues.FirstHitBall,minBType);
                        }
                        else if (minBType==BallType.CUE)
                        {
                            entityShooter.setValue(ShooterValues.FirstHitBall,ballType);
                        }

                    }


                Point2D tempVec = (Point2D) testVec.clone();
                //Check if other ball is about to collide
                if (ballPos.distance(pointMove(minBPos, minBVel)) < (ballRadius + minBRadius)) {
                    minBSpd = minBSpd * 0.5;
                    //WIP transfer force to the first ball
                    ballSpd+=minBSpd;

                    minBall.setValue(BallValues.Velocity, giveImpulse(minBSpd, minBVel));
                }

                //Apply ball's force to other ball
                minBall.setValue(BallValues.Velocity, pointMove((Point2D) minBall.getValue(BallValues.Velocity), giveImpulse(-1 * ballSpd, tempVec)));
            }
            //Uncomment statement below to see frame of impact
            //remSpd=0;
        }
        //If sinking decrease ball radius
        if (sinkingIn != -1) {
            ballRadius = Math.max(0, ballRadius - (sinkSpeed * deltaTime));
            //Check if ball radius is near zero
            if (ballRadius <= 0.01) {
                Pocket p = getPocket(sinkingIn);
                if (p != null) {
                    p.setValue(PocketValues.IsFull, false);
                }
                ball.setValue(BallValues.FullySunk, true);
            }
            ball.setValue(BallValues.Scale, (double) ballRadius / ((double) ball.getValue(BallValues.Radius, 1.00)));
            ball.setValue(BallValues.SinkingIn, sinkingIn);
        }
        //Update ball values
        ball.setValue(BallValues.Velocity, giveImpulse(Math.max(0, ballSpd - (frictionFloor * deltaTime)), testVec));//new Point2D.Double(testVec.getX()*ballSpd,testVec.getY()*ballSpd));
        ball.setValue(BallValues.Position, ballPos);
    }

    public Pocket getPocket(int _findId) {
        //getHole finds hole with specific id
        for (Pocket p : entityPockets) {
            if (p.getId() == _findId) {
                return p;
            }
        }
        return null;
    }

    public Point2D normalise(Point2D _point) {
        //Normalise a Point2D coordinate
        double dis = _point.distance(pointZero);
        if (dis <= 0.01) {
            return (Point2D) pointZero.clone();
        }

        return new Point2D.Double(_point.getX() / dis, _point.getY() / dis);
    }

    public void setState(EngineState state) {
        engineState = state;
    }

    public void update(float deltaTime, List<Entity> entities, HashMap<Inputs, Boolean> keyInputs) {
        
        //Sort instances
        sortEntities(entities);
        switch (engineState) {
            //The players turn
            case EngineState.Turn -> {
                Ball cueBall = (Ball) entityShooter.getValue(ShooterValues.CueBall, null);
                Point2D sPos = (Point2D) entityShooter.getValue(ShooterValues.Position, pointZero);
                if (cueBall != null) {
                    switch ((ShooterStates) entityShooter.getValue(ShooterValues.ShooterState, ShooterStates.Move)) {
                        case ShooterStates.Move-> {
                            if (keyInputs.get(Inputs.Down)) {
                                sPos=pointMove(sPos, new Point2D.Double(0, cueMove*deltaTime));
                            }
                            if (keyInputs.get(Inputs.Left)) {
                                sPos=pointMove(sPos, new Point2D.Double( -cueMove*deltaTime,0));
                            }
                            if (keyInputs.get(Inputs.Up)) {
                                sPos=pointMove(sPos, new Point2D.Double(0, -cueMove*deltaTime));
                            }
                            if (keyInputs.get(Inputs.Right)) {
                                sPos=pointMove(sPos, new Point2D.Double(cueMove*deltaTime,0));
                            }
                            entityShooter.setValue(ShooterValues.Position, sPos);
                            cueBall.setValue(BallValues.Position, sPos);

                            if (keyInputs.get(Inputs.Enter) && checkPlacementLegal(cueBall)) {
                               entityShooter.setValue(ShooterValues.ShooterState, ShooterStates.Aim);
                            }
                        }
						case ShooterStates.Aim-> {
							float sPower = (float)entityShooter.getValue(ShooterValues.Power,0f);
                            float sPowerMax = (float)entityShooter.getValue(ShooterValues.PowerMax,1000f);
							float sAngle = (float)entityShooter.getValue(ShooterValues.Angle,0f);
							if (keyInputs.get(Inputs.PowerDecrease)) {
								sPower = Math.max(0,sPower-(cuePowerChange*deltaTime));
							}
							if (keyInputs.get(Inputs.PowerIncrease)) {
								sPower = Math.min(sPowerMax,sPower+(cuePowerChange*deltaTime));
							}
							if (keyInputs.get(Inputs.RotateAntiClockwise)) {
								sAngle =(float) ((sAngle + (deltaTime*cueRotate))% (2*Math.PI));
							}
							if (keyInputs.get(Inputs.RotateClockwise)) {
								sAngle =(float) ((sAngle - (deltaTime*cueRotate) + (2*Math.PI))% (2*Math.PI));
							}
							if (keyInputs.get(Inputs.Cancel)) {
								entityShooter.setValue(ShooterValues.ShooterState, ShooterStates.Move);
							}

							entityShooter.setValue(ShooterValues.Power, sPower);
							entityShooter.setValue(ShooterValues.Angle, sAngle);

							if (keyInputs.get(Inputs.Enter)) {
								//Apply angle and power to cueball
								cueBall.setValue(BallValues.Velocity, giveImpulse(sPower, new Point2D.Float((float)Math.cos(sAngle),(float)(-1*Math.sin(sAngle)))));

								//hide shooter object and resume engine
								entityShooter.setValue(ShooterValues.ShooterState, ShooterStates.Off);
								engineState = EngineState.Simulate;
                                
							}
						}
        			}
    			}
			}
    		//Initiate after the players turn
    		case EngineState.Simulate -> {
				//Update ball instances
    			for (Ball b : entityBalls) {
					//move ball accounting for collisions and bouncing
					moveCheck((Ball) b, deltaTime);
					//When a ball stops, occasionally give it a push
					//if (((Point2D) b.getValue(BallValues.Velocity, pointZero.clone())).distance(pointZero) < 0.01 && rdm.nextInt(18) == 9) {
					// b.setValue(BallValues.Velocity, giveImpulse(400));
					//}
    			}

				//Check if balls have stopped
				if (checkBallsStopped()) {
					transitionCur+=deltaTime;
					if (transitionCur>=transitionTime) {
						transitionCur=0;
						engineState = engineState.Outcome;
					}
				} else {
					transitionCur=0;
				}
			}
		}
	}

	private Boolean checkBallsStopped() {
		//Check if all balls have stopped moving/falling
		for (Ball b : entityBalls) {
			if ((((Point2D)b
					.getValue(BallValues.Velocity,pointZero.clone()))
					.distance(pointZero)>0.01) || ((int)b.getValue(BallValues.SinkingIn,-1)>0 && (double)b.getValue(BallValues.Scale,1)>0.01)) {
				return false;
			}
		}
		return true;
	}

	public EngineState getEngineState() {
		return engineState;
	}

    public void moveShooter(float x, float y)
    {
        //check if shooter object exists
        if (entityShooter!=null) {
            ShooterStates state = (ShooterStates) entityShooter.getValue(ShooterValues.ShooterState,ShooterStates.Off);
            if (state == ShooterStates.Move)
            {
                //Move ball
                entityShooter.setValue(ShooterValues.Position, new Point2D.Float(x,y));
            }
            else if (state == ShooterStates.Aim)
            {
                Point2D pos = (Point2D) entityShooter.getValue(ShooterValues.Position, getPointZero());
                Point2D diffVec = pointDiff(pos,new Point2D.Float(x,y));
                //Calculate power
                float maxPower = (float)entityShooter.getValue(ShooterValues.PowerMax,0f);
                float power = maxPower*(Math.min(150,(float)diffVec.distance(getPointZero()))/150);
                entityShooter.setValue(ShooterValues.Power, power);
                //Calculate direction
                float angle = (float) (Math.atan2(-diffVec.getY(),diffVec.getX())+((2*Math.PI)));

                entityShooter.setValue(ShooterValues.Angle,angle);


            }
        }
    }

    /**
     * Returns the game state as a JSON object. To be passed to server.
     * Format:
     * {
     * "1":{"x":10,"y":0,"type":"Stripes","sunk":true},
     * "2":{"x":20,"y":15,"type":"Solids","sunk":false},
     * "3":{"x":0,"y":5,"type":"Cue","sunk":false}
     * }
     * @return JsonObject with game info
     */
    private JsonObject getGameState() {
        //Return game state
        JsonObject gameState = new JsonObject();
        for (Ball b : entityBalls) {
            JsonObject ballState = new JsonObject();
            ballState.put("x", b.getValue(BallValues.Position).toString());
            ballState.put("y", b.getValue(BallValues.Position).toString());
            ballState.put("type", b.getValue(BallValues.BallType));
            ballState.put("sunk", b.getValue(BallValues.FullySunk, 0));
            gameState.put(String.valueOf(b.getId()), ballState);
        }
        return gameState;
    }

    /**
     * Returns the shooter state as a JSON object
     * Format:
     * {"angle":170.65,"power":270.04}
     * @param angle float angle, 0 to 360
     * @param power float power, 0 to 1000
     * @return
     */
    private JsonObject shooterJson(float angle, float power) {
        //Return shooter state
        JsonObject shooterState = new JsonObject();
        shooterState.put("angle", angle);
        shooterState.put("power", power);
        return shooterState;
    }
}