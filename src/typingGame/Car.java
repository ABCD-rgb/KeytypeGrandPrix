package typingGame;

import javafx.scene.image.Image;

// Represents the player of the game

public class Car extends Sprite {
	protected double speed; 
	
	// constants
	protected final static int CAR_DIAMETER = 40;
	protected final static double CAR_SPEED = 120/CAR_DIAMETER;
	public final static String CAR_IMG = "images/car_yellow.png";
	
	public Car(int xPos, int yPos) {
		super(xPos, yPos);
		this.speed = Car.CAR_SPEED;
		Image carImg = new Image(Car.CAR_IMG, Car.CAR_DIAMETER, Car.CAR_DIAMETER, false, false);
		this.loadImage(carImg);
	}
	
	// TODO
	void move() {
		
	}
	
	
	// === setters ===
	public void setSpeed(double val) {
		this.speed = val;
	}
	
	// === getters ===
	public double getSpeed() {
		return this.speed;
	}
}
