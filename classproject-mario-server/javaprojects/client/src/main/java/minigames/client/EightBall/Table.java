package minigames.client.EightBall;

import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Table extends Entity {
    public Table(int _id, HashMap<TableValues, Object> _values) {
        super(_id, new LinkedHashMap<Object,Object>(_values));
    }
    //Table class that stores and handles Table related variables and functions

    public Object getValue(TableValues _key,Object _default) {
        //getValue attempts to return the value of the key
        return super.getValue(_key,_default);
    }

    public Object getValue(TableValues _key) {
        //getValue attempts to return the value of the key, using null as the _default value
        return super.getValue(_key);
    }

    public void setValue(TableValues _key, Object _value) {
        super.setValue(_key,_value);
    }

    @Override
    public void draw(GraphicsContext _context) {
        //Draw table
        Point2D position = (Point2D) getValue(TableValues.Position);
        Point2D size = (Point2D) getValue(TableValues.Size);
        _context.strokeRect(position.getX(), position.getY(), size.getX(), size.getY());
    }
}
