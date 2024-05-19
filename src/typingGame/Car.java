package typingGame;

import javafx.scene.image.Image;


/* This Class is the player as displayed on the screen */


public class Car extends Sprite {
    protected double speed;
    private int carID;
    
    public Car(int xPos, int yPos, int carID) {
        super(xPos, yPos);
        this.speed = Constants.CAR_SPEED;
        Image carImg = new Image(Constants.CAR_IMG, Constants.CAR_WIDTH, Constants.CAR_HEIGHT, false, false);
        this.loadImage(carImg);
    }


    void move(double targetX, double length) {
        // calculate the distance to move for each word
        double distancePerWord = Constants.WINDOW_WIDTH / length;

        // calculate the direction of movement
        double direction = Math.signum(targetX - getXPos());

        // calculate the movement distance for this frame
        double movement = direction * distancePerWord;

        // move the car towards the target position
        double newX = getXPos() + movement;

        // ensure that the newX is within the boundaries
        if (newX >= 0 && newX <= Constants.WINDOW_WIDTH - Constants.CAR_WIDTH) {
            // set the new position
            setXPos(newX);
        }
    }


    // this is just test
    public void moveToEndOfScreen() {
        double targetX = Constants.WINDOW_WIDTH - getWidth(); // move to the end of the screen
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
        return Constants.CAR_WIDTH;
    }
    
    public int getCarID() {
    	return this.carID;
    }

}
