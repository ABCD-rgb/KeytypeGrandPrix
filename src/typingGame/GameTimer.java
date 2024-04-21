package typingGame;

import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

// Responsible for making the game move

public class GameTimer extends AnimationTimer {
	private GraphicsContext gc;
	private Scene gameScene;
	private long startTime;
	private Car car;
	private boolean isKeyPressed = false; // Flag to indicate if a key is pressed
	
	// constants
	public final static Image BG_IMG = new Image("images/concrete-floor.jpg", Game.WINDOW_WIDTH, Game.WINDOW_WIDTH, false, false, false);
	
	public GameTimer(Scene gameScene, GraphicsContext gc) {
		this.gc = gc;
		this.gameScene = gameScene;
		this.startTime = System.nanoTime();
		this.car = new Car(20, Game.WINDOW_CENTER);
		
		// methods ran at the start of GameTimer
		this.handleKeyPressEvent();
	}
	
	
	@Override
	public void handle(long currentNanoTime) {
		long currentSec = TimeUnit.NANOSECONDS.toSeconds(currentNanoTime);
		
		// move car
		this.moveCar();
		
		// re-render car
		this.renderCar();	
	}
	
	
	// render the Car on the screen
	private void renderCar() {
	    // Clear the canvas before rendering
	    gc.clearRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
	    
	    // Draw the background image
	    gc.drawImage(BG_IMG, 0, 0, gameScene.getWidth(), gameScene.getHeight());
	    
		this.car.render(this.gc);	// display change of xPos and yPos on the canvas
	}
	
	
	// move car when typing on the keyboard
	private void moveCar() {
		this.car.move(isKeyPressed);
	}
	
	
	
	// TODO: <copied from previous project, needs to be revised>
	// listens to any key press and does corresponding actions
	private void handleKeyPressEvent() {
	    gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        public void handle(KeyEvent e) {
	            // Move the car when any key is pressed
	        	 isKeyPressed = true;
	        }
	    });

	    gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
	        public void handle(KeyEvent e) {
	        	 isKeyPressed = false;
	        }
	    });
	}
}
