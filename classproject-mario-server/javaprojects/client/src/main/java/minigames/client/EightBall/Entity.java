package minigames.client.EightBall;

import java.util.*;
import javafx.scene.canvas.GraphicsContext;

public class Entity {

    final int id;
    private HashMap<Object,Object> values = new LinkedHashMap<>();

    public Entity(int _id, HashMap<Object,Object> _values) {
        id = _id;
        values = new LinkedHashMap<>(_values);
    }

    public void draw(GraphicsContext _context) {
        //Draw Entity
    }

    public int getId() {
        return id;
    }

    public Object getValue(Object _key,Object _default) {
        //getValue attempts to return the value of the key
        if (values.containsKey(_key)) {
            if (values.get(_key) instanceof Collection) {
                return new HashSet<>((Collection<?>) values.get(_key));
            }
            else {
                return values.get(_key);
            }
        }
        return _default;
    }

    public Object getValue(Object _key) {
        //getValue attempts to return the value of the key, using null as the _default value
        return getValue(_key,null);
    }

    public void setValue(Object _key, Object _value) {
        values.put(_key, _value);
    }
}
