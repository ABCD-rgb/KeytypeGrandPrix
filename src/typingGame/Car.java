package typingGame;

import javafx.scene.image.Image;


/* This Class is the player as displayed on the screen */


public class Car extends Sprite {
    protected double speed;
	
    // constants
    protected final static int CAR_WIDTH = 55;
    protected final static int CAR_HEIGHT = 30;
    protected final static double CAR_SPEED = 120 / CAR_WIDTH;
    public final static String CAR_IMG = "images/car_yellow.png";

    public Car(int xPos, int yPos) {
        super(xPos, yPos);
        this.speed = Car.CAR_SPEED;
        Image carImg = new Image(Car.CAR_IMG, Car.CAR_WIDTH, Car.CAR_HEIGHT, false, false);
        this.loadImage(carImg);
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

        // ensure that the newX is within the boundaries
        if (newX >= 0 && newX <= Game.WINDOW_WIDTH - CAR_WIDTH) {
            // set the new position
            setXPos(newX);
        }
    }


    // this is just test
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
