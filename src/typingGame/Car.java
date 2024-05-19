package typingGame;

import javafx.scene.image.Image;
import java.util.Random;

public class Car extends Sprite {
	protected double speed;
    protected final static int CAR_WIDTH = 100;
    protected final static int CAR_HEIGHT = 300;
    protected final static double CAR_SPEED = 120 / CAR_WIDTH;
    public final static String[] CAR_COLORS = {"black", "blue", "orange", "yellow"};

    public Car(String color, double xPos, double yPos) {
        super("images/car_" + color + ".png", xPos, yPos);
        this.speed = CAR_SPEED;
    }
    
    public static String getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(CAR_COLORS.length);
        return CAR_COLORS[index];
    }  
    
    void move(double targetX, double length) {
        // calculate the distance to move for each word
        double distancePerWord = Game.WINDOW_WIDTH / length;

        // calculate the direction of movement
        double direction = Math.signum(targetX - getXPos());

        // calculate the movement distance for this frame
        double movement = direction * distancePerWord;

        // move the car towards the target position
        double newX = getXPos() + movement;

        // set the new position
        setXPos(newX);
        
        // ensure that the newX is within the boundaries
//        if (newX >= 0 && newX <= Game.WINDOW_WIDTH - CAR_WIDTH) {
//            // set the new position
//            setXPos(newX);
//        }
    }

    public void moveToEndOfScreen() {
        double targetX = Game.WINDOW_WIDTH - getWidth(); // move to the end of the screen
        setXPos(targetX);
    }

    // === setters ===
    public void setSpeed(double val) {
        this.speed = val;
    }

    // === getters ===
    public double getSpeed() {
        return this.speed;
    }
    
    public double getWidth() {
        return CAR_WIDTH;
    }

}
