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
		this.car.render(this.gc);	// display change of xPos and yPos on the canvas
	}
	
	
	// move car when typing on the keyboard
	private void moveCar() {
		this.car.move();
	}
	
	
	
	// TODO: <copied from previous project, needs to be revised>
	// listens to any key press and does corresponding actions
	private void handleKeyPressEvent() {
//		this.gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
//			public void handle(KeyEvent e) {
//				// code is a value that represents the pressed key
//				KeyCode code = e.getCode();
//				moveMyFish(code);
//			}
//		});
//
//		this.gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
//			public void handle(KeyEvent e) {
//				KeyCode code = e.getCode();
//				stopMyFish(code);
//			}
//		});
	}
}
