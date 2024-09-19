package minigames.client.EightBall;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.geom.Point2D;
import java.util.*;

public class Ball extends Entity {
    //Ball class that stores and handles ball related variables and functions
    public Ball(int _id, HashMap<BallValues, Object> _values) {
        super(_id, new LinkedHashMap<Object,Object>(_values));
    }

    public Object getValue(BallValues _key,Object _default) {
        // getValue attempts to return the value of the key
        return super.getValue(_key,_default);
    }

    public Object getValue(BallValues _key) {
        //getValue attempts to return the value of the key, using null as the _default value
        return super.getValue(_key);
    }

    public void setValue(BallValues _key, Object _value) {
        super.setValue(_key,_value);
    }

    @Override
    public void draw(GraphicsContext _context) {
        // Draw Ball
        Point2D pos = (Point2D) getValue(BallValues.Position);
        double radius = ((Number) getValue(BallValues.Radius)).doubleValue();
        double scale = ((Number) getValue(BallValues.Scale)).doubleValue();
    
        _context.setFill(Color.RED);
        _context.fillOval(
            (int) Math.round(pos.getX() - (radius * scale)),
            (int) Math.round(pos.getY() - (radius * scale)),
            (int) Math.round((radius * scale) * 2),
            (int) Math.round((radius * scale) * 2)
        );
    }
}
