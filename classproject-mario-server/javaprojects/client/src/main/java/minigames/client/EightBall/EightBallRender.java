package minigames.client.EightBall;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class EightBallRender {
 //Draws game instances on the screen
    private Canvas canvas;
    Image background;
    GraphicsContext context;
    private List<Point2D> circlePositions = new ArrayList<>();
    private List<Double> circleRadii = new ArrayList<>();


    public EightBallRender(Canvas _canvas) {
        //canvas is the drawable surface
        canvas = _canvas;
        //context is the tool to draw upon the surface
        context = canvas.getGraphicsContext2D();
    }

    public void prepareCircles(List<Point2D> _circlePositions, List<Double> _circleRadii) {
            //Update circle positions and radius
            circlePositions = List.copyOf(_circlePositions);
            circleRadii = List.copyOf(_circleRadii);
        }

    public void render(List<Entity> _entities) {
        //Refresh Panel
        context.save();

        //Draw Instances
        for (Entity entity : _entities) {
            entity.draw(context);
        }

        context.restore();
    }

    public void prepare() {
        //Clears screen for the next draw event.
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());;
       // context.setFill(Color.WHEAT);
        //context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());;
    }
}
