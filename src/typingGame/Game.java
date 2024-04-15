package typingGame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

// Entry point for different scenes

public class Game {
	private Stage stage;
	private Scene menuScene;	// main menu screen
	private Scene gameScene;	// typing game screen
	private Group root;
	private Canvas canvas;
	private DropShadow shadow;	// shadow effect when button is clicked
	
	// constants
	public final static int WINDOW_WIDTH = 800;	// same as height
	public final static int WINDOW_CENTER = WINDOW_WIDTH / 2;
	public final static Image BG_IMG = new Image("images/concrete-floor.jpg", Game.WINDOW_WIDTH, Game.WINDOW_WIDTH, false, false);

	public Game() {
		this.canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_WIDTH);
		this.root = new Group();
		// canvas is showing at the root
		this.root.getChildren().add(this.canvas);
		this.gameScene = new Scene(this.root);
		this.shadow = new DropShadow();
	}
	
	
	// setting up stage and running the app
	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setTitle("KeyTypeGrandPrix");
		
		// game entry point
		this.initMenu(stage);
		stage.setResizable(false);
		stage.show();
	}
	
	
	// display the main menu
	private void initMenu(Stage stage) {
		Canvas canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_WIDTH);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		// Image bg = new Image("images/concrete-floor.png", Game.WINDOW_WIDTH, Game.WINDOW_WIDTH, false, false);
		gc.drawImage(BG_IMG, 0, 0);
		
		// display buttons: "new game", "instructions", "about"
		VBox menuButtons = this.createMenuButtons();
		
		StackPane menuRoot = new StackPane();
		menuRoot.getChildren().addAll(canvas, menuButtons);
		this.menuScene = new Scene(menuRoot);
		
		stage.setScene(this.menuScene);	// sets the scene to the menu scene
	}
	
	
	// buttons for the main menu
	private VBox createMenuButtons() {
		// NOTE: VBox is used to layout its children in a single vertical column
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(5));
		vbox.setSpacing(10);

		Button b1 = new Button("New Game");
		Button b2 = new Button("Instructions");
		Button b3 = new Button("About");

		Font font = Font.font("Times New Roman", FontWeight.BOLD, 35); // yung 30 here is yung size kaya malaki yung button sa display
		b1.setFont(font);
		b2.setFont(font);
		b3.setFont(font);
		b1.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #9F2B00; -fx-background-color: linear-gradient(#FF8B00, #F4D03F, #FF8B00); -fx-border-color:#9F2B00; -fx-border-radius: 15px; -fx-border-width: 5px" );
		b2.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #9F2B00; -fx-background-color: linear-gradient(#FF8B00, #F4D03F, #FF8B00); -fx-border-color:#9F2B00; -fx-border-radius: 15px; -fx-border-width: 5px" );
		b3.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #9F2B00; -fx-background-color: linear-gradient(#FF8B00, #F4D03F, #FF8B00); -fx-border-color:#9F2B00; -fx-border-radius: 15px; -fx-border-width: 5px" );

		vbox.getChildren().addAll(b1,b2,b3);//add the buttons to the vbox

		// actions when a specific button is pressed
		b1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				b1.setEffect(shadow);
				changeScene(stage, "game");
			}
		});
		b2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				b2.setEffect(shadow);
				changeScene(stage, "instructions");
			}
		});
		b3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				b3.setEffect(shadow);
				changeScene(stage, "about");
			}
		});
		
		return vbox;
	}
	
	
	
	// scene picker
	protected void changeScene(Stage stage, String string) {
		GraphicsContext gc = this.canvas.getGraphicsContext2D();
		
		// entry point to the actual game
		if (string.equals("game")) {
			stage.setScene(gameScene);
			GameTimer gameTimer = new GameTimer(gameScene, gc);
			gameTimer.start();	// internally calls the handle() method of GameTimer
		
		} else if (string.equals("instructions")) {
			initInstruct(stage);
		
		} else if (string.equals("about")) {
			initAbout(stage);
		}
	}
	
	
	// TODO: instructions scene
	public void initInstruct(Stage stage) {
		
	}
	
	
	// TODO: about scene
	public void initAbout(Stage stage) {
		
	}
}
