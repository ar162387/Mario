package minigames.client.EightBall;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Pocket extends Entity {
    public Pocket(int _id, HashMap<PocketValues, Object> _values) {
        super(_id, new LinkedHashMap<Object,Object>(_values));
    }

    public Object getValue(PocketValues _key, Object _default) {
        //getValue attempts to return the value of the key
        return super.getValue(_key,_default);
    }

    public Object getValue(PocketValues _key) {
        //getValue attempts to return the value of the key, using null as the _default value
        return super.getValue(_key);

    }

    public void setValue(PocketValues _key, Object _value) {
     super.setValue(_key,_value);
    }

    @Override
    public void draw(GraphicsContext _context) {
        //Draw Hole
        Point2D pos = (Point2D) getValue(PocketValues.DrawPosition);
        double radius = (double)getValue(PocketValues.DrawRadius);
        _context.setFill(Color.BLACK);
        _context.fillOval((int)Math.round(pos.getX()-radius),(int)Math.round(pos.getY()-radius),(int)Math.round(radius*2),(int)Math.round(radius*2));

    }
}
