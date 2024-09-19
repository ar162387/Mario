package minigames.client.EightBall;

import javafx.scene.canvas.GraphicsContext;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Shooter extends Entity {
    public Shooter(int _id, HashMap<ShooterValues, Object> _values) {
        super(_id, new LinkedHashMap<Object,Object>(_values));
    }

    public Object getValue(ShooterValues _key,Object _default) {
        //getValue attempts to return the value of the key
        return super.getValue(_key,_default);
    }

    public Object getValue(ShooterValues _key) {
        //getValue attempts to return the value of the key, using null as the _default value
        return super.getValue(_key);
    }

    public void setValue(ShooterValues _key, Object _value) {
        super.setValue(_key,_value);
    }

    @Override
    public void draw(GraphicsContext _context) {
        //Draw Shooter
        Point2D pos = (Point2D)getValue(ShooterValues.Position);
        switch ((ShooterStates)getValue(ShooterValues.ShooterState, ShooterStates.Off)) {
            case Off -> {
                return;
            }
            case Move -> {
                _context.fillText("Move!", pos.getX(), pos.getY()+24);
                return;
            }
            case Aim -> {
                Float angle = (float) (((float)getValue(ShooterValues.Angle))*180/(Math.PI));
                _context.fillText("Power: "+(getValue(ShooterValues.Power)).toString(), pos.getX(), pos.getY()+24);
                _context.fillText("Angle: "+ angle.toString(), pos.getX(), pos.getY()+50);
            }
        }
    }
}
