package typingGame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import typingGame.QuoteFetcher;
import java.io.IOException;
import java.net.*;
import java.util.Optional;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/* This Class is an entry point for different scenes */


public class Game {
	private Stage stage;
	private Scene menuScene;	// main menu screen
	private Scene gameScene;	// typing game screen
	private Group root;
	private Canvas canvas;
	private DropShadow shadow;	// shadow effect when button is clicked
	private Button b1;
    private Button b2;
    private Button b3;
    private String textToType;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int SERVER_PORT = Constants.PORT;

	public Game() {
		this.canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		this.root = new Group();
		
		// canvas is showing at the root
		this.root.getChildren().add(this.canvas);
		this.gameScene = new Scene(this.root);
		this.shadow = new DropShadow();
		
		try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(Constants.IP);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
		
		fetchTextToType();
	}
	
	private ImageView createBackgroundImage() {
	    ImageView backgroundImage = new ImageView(Constants.BG_IMG);
	    backgroundImage.setFitWidth(Constants.WINDOW_WIDTH);
	    backgroundImage.setFitHeight(Constants.WINDOW_HEIGHT);
	    backgroundImage.setPreserveRatio(false); // Ensure the image fills the specified dimensions
	    return backgroundImage;
	}

	// setting up stage and running the app
	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setTitle("Key Type Grand Prix - Typing Game");
		
		// set the window logo
	    stage.getIcons().add(Constants.WINDOWLOGO);
		
		// game entry point
	    stage.setUserData(this);
		this.initMenu(stage);
		stage.setResizable(false);
		stage.show();
	}       
	
	private void fetchTextToType() {
        textToType = QuoteFetcher.fetchRandomQuote();
        if (textToType == null) {
            textToType = "Default text to type.";
        }
    }
	
	// display the main menu
//	private void initMenu(Stage stage) {
//		Canvas canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
//		GraphicsContext gc = canvas.getGraphicsContext2D();
//		
//		gc.drawImage(Constants.BG_IMG, 0, 0);
//		
//		// display buttons: "new game", "instructions", "leaderboards"
//		VBox menuButtons = this.createMenuButtons();
//		
//		StackPane menuRoot = new StackPane();
//		menuRoot.getChildren().addAll(canvas, menuButtons);
//		this.menuScene = new Scene(menuRoot);
//		
//		stage.setScene(this.menuScene);	// sets the scene to the menu scene
//	}
	
	private void initMenu(Stage stage) {
	    StackPane root = new StackPane();

	    ImageView backgroundImage = createBackgroundImage();

	    // Create a canvas to draw additional content, if necessary
	    Canvas canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    
	    // Draw the background image using GraphicsContext
	    gc.drawImage(Constants.BG_IMG, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

	    // Display buttons: "new game", "instructions", "leaderboards"
	    VBox menuButtons = this.createMenuButtons();
	    
	    root.getChildren().addAll(backgroundImage, canvas, menuButtons);
	    
	    Scene menuScene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
	    
	    // Return to the main screen when Esc key is pressed
	    menuScene.setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.ESCAPE) {
	            stage.close(); // Close the application
	        }
	    });

	    stage.setScene(menuScene); // Sets the scene to the menu scene
	}
	
	// buttons for the main menu
	private VBox createMenuButtons() {
		// NOTE: VBox is used to layout its children in a single vertical column
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.BASELINE_CENTER);
		vbox.setPadding(new Insets(50, 5, 5, 5));
		vbox.setSpacing(20);
		
		Constants.LOGO.setFitWidth(600);
		Constants.LOGO.setFitHeight(200);

	    b1 = new Button("New Game");
        b2 = new Button("Instructions");
        b3 = new Button("Leaderboards");

		Font font = Font.font("Verdana", FontWeight.BOLD, 25);
		b1.setFont(font);
		b2.setFont(font);
		b3.setFont(font);
		b1.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #314528; -fx-background-color: #9BE86B; -fx-border-color:#314528; -fx-border-radius: 15px; -fx-border-width: 5px" );
		b2.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #343857; -fx-background-color: #A2D9FF; -fx-border-color:#343857; -fx-border-radius: 15px; -fx-border-width: 5px" );
		b3.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #343857; -fx-background-color: #A2D9FF; -fx-border-color:#343857; -fx-border-radius: 15px; -fx-border-width: 5px" );

		vbox.getChildren().addAll(Constants.LOGO,b1,b2,b3);//add the logo & buttons to the vbox
		
		// actions when a specific button is pressed
		b1.setOnMousePressed(event -> darkenButton(b1));
        b1.setOnMouseReleased(event -> resetButtonStyle(b1));
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                b1.setEffect(shadow);
                changeScene(stage, "game");
            }
        });

        b2.setOnMousePressed(event -> darkenButton(b2));
        b2.setOnMouseReleased(event -> resetButtonStyle(b2));
        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                b2.setEffect(shadow);
                changeScene(stage, "instructions");
            }
        });

        b3.setOnMousePressed(event -> darkenButton(b3));
        b3.setOnMouseReleased(event -> resetButtonStyle(b3));
        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                b3.setEffect(shadow);
                changeScene(stage, "leaderboards");
            }
        });

        return vbox;
	}
	
	private void darkenButton(Button button) {
		if (button == b1) {
			button.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #FFFFFF; -fx-background-color: #6B8E23; -fx-border-color:#314528; -fx-border-radius: 15px; -fx-border-width: 5px");
		} else {
			button.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #FFFFFF; -fx-background-color: #6EA5CB; -fx-border-color:#343857; -fx-border-radius: 15px; -fx-border-width: 5px");
		}
    }
	
	private void resetButtonStyle(Button button) {
	    if (button == b1) {
	        button.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #314528; -fx-background-color: #9BE86B; -fx-border-color:#314528; -fx-border-radius: 15px; -fx-border-width: 5px");
	    } else {
	        button.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #343857; -fx-background-color: #A2D9FF; -fx-border-color:#343857; -fx-border-radius: 15px; -fx-border-width: 5px");
	    }
	}
	
	
	// scene picker
	protected void changeScene(Stage stage, String string) {	
		// entry point to the actual game
		if (string.equals("game")) {
			initChat(stage);
		} else if (string.equals("instructions")) {
			initInstruct(stage);
		
		} else if (string.equals("leaderboards")) {
			initLeaderboards(stage);
		}
	}
	
	public void initInstruct(Stage stage) {
		StackPane root = new StackPane();

		ImageView backgroundImage = createBackgroundImage();
	    
	    VBox content = new VBox();
	    content.setAlignment(Pos.CENTER);
	    content.setSpacing(20);
	    
	    Font titleFont = Font.font("Verdana", FontWeight.BOLD, 30);
	    Font bodyFont = Font.font("Verdana", 16);

	    Constants.LOGO.setFitWidth(150);
	    Constants.LOGO.setFitHeight(50);
	    
	    Label instructionsHeading = new Label("Instructions");
	    instructionsHeading.setFont(titleFont);

	    Label introText = new Label("Rev up your engines and put your typing skills to the ultimate test!");
	    introText.setFont(bodyFont);

	    Label readyText = new Label("Ready to dive into the action? Here's your quick-start guide!");
	    readyText.setFont(bodyFont);
	    readyText.setStyle("-fx-font-weight: bold;");

	    VBox instructionsBox = new VBox();
	    instructionsBox.setSpacing(10);
	    instructionsBox.setPadding(new Insets(20));
	    instructionsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
	    instructionsBox.setMaxWidth(600);

	    Label instruction1 = new Label("• Watch as the text appears on your screen");
	    instruction1.setFont(bodyFont);

	    Label instruction2 = new Label("• Type the displayed text as quickly and accurately as possible");
	    instruction2.setFont(bodyFont);

	    Label instruction3 = new Label("• Watch your car speed up with each correct keystroke");
	    instruction3.setFont(bodyFont);

	    Label instruction4 = new Label("• Maintain a high level of accuracy to avoid penalties");
	    instruction4.setFont(bodyFont);

	    Label instruction5 = new Label("• Be the first to complete the text and cross the finish line to win!");
	    instruction5.setFont(bodyFont);

	    instructionsBox.getChildren().addAll(instruction1, instruction2, instruction3, instruction4, instruction5);

	    Label joinText = new Label("Join now and experience the thrill of a high-speed typing race!");
	    joinText.setFont(bodyFont);

	    Label returnText = new Label("Press [ESC] to return to the main menu");
	    returnText.setFont(bodyFont);

	    content.getChildren().addAll(Constants.LOGO, instructionsHeading, introText, readyText,
	            instructionsBox, joinText, returnText);

	    root.getChildren().addAll(backgroundImage, content);

	    Scene instructionsScene = new Scene(root, 800, 600);
	    
	    // return to the main screen when Enter key is pressed
	    instructionsScene.setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.ESCAPE) {
	            initMenu(stage); // call the initMenu method to reinitialize the main menu
	        }
	    });
	    
	    stage.setScene(instructionsScene);
	}
	
	public void initLeaderboards(Stage stage) {
	    StackPane root = new StackPane();

	    ImageView backgroundImage = createBackgroundImage();
	    
	    BorderPane content = new BorderPane();
	    content.setMaxWidth(800);
	    content.setMaxHeight(600);

	    VBox topContent = new VBox();
	    topContent.setAlignment(Pos.CENTER);
	    topContent.setSpacing(20);
	    topContent.setPadding(new Insets(50, 0, 20, 0));

	    Font titleFont = Font.font("Verdana", FontWeight.BOLD, 30);
	    Font bodyFont = Font.font("Verdana", 16);

	    Constants.LOGO.setFitWidth(150);
	    Constants.LOGO.setFitHeight(50);

	    Label leaderboardsHeading = new Label("Leaderboards");
	    leaderboardsHeading.setFont(titleFont);

	    topContent.getChildren().addAll(Constants.LOGO, leaderboardsHeading);

	    VBox leaderboardsBox = new VBox();
	    leaderboardsBox.setSpacing(10);
	    leaderboardsBox.setPadding(new Insets(20));
	    leaderboardsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
	    leaderboardsBox.setMaxWidth(600);
	    leaderboardsBox.setMaxHeight(300);
	    leaderboardsBox.setAlignment(Pos.CENTER);

	    // Fetch leaderboard data from the server and populate the leaderboardsBox
	    fetchLeaderboardData(leaderboardsBox);

	    VBox centerContent = new VBox(leaderboardsBox);
	    centerContent.setAlignment(Pos.CENTER);
	    centerContent.setStyle("-fx-background-radius: 30px; -fx-background-color: white;");
	    centerContent.setMaxWidth(600);
	    centerContent.setMaxHeight(300);

	    VBox bottomContent = new VBox();
	    bottomContent.setAlignment(Pos.CENTER);
	    bottomContent.setPadding(new Insets(20, 0, 50, 0));

	    Label returnText = new Label("Press [ESC] to return to the main menu");
	    returnText.setFont(bodyFont);

	    bottomContent.getChildren().add(returnText);

	    content.setTop(topContent);
	    content.setCenter(centerContent);
	    content.setBottom(bottomContent);

	    root.getChildren().addAll(backgroundImage, content);

	    Scene leaderboardsScene = new Scene(root, 800, 600);
	    
	    // Return to the main screen when Esc key is pressed
	    leaderboardsScene.setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.ESCAPE) {
	            initMenu(stage); // Call the initMenu method to reinitialize the main menu
	        }
	    });
	    
	    stage.setScene(leaderboardsScene);
	}
	
	// chat scene --> chats between players who joined	
	public void initChat(Stage stage) {
	    StackPane root = new StackPane();
	    root.setStyle("-fx-background-color: #A6C9CB;");

	    VBox content = new VBox();
	    content.setAlignment(Pos.CENTER);
	    content.setSpacing(15);

	    ImageView logo = new ImageView(new Image("images/logo.png"));
	    logo.setFitWidth(600);
	    logo.setFitHeight(200);

	    Font buttonFont = Font.font("Verdana", FontWeight.BOLD, 25);

	    Button trainingModeButton = new Button("Training Mode");
	    trainingModeButton.setFont(buttonFont);
	    trainingModeButton.setStyle("-fx-background-radius: 15px; -fx-text-fill: #314528; -fx-background-color: #9BE86B; -fx-border-color:#314528; -fx-border-radius: 15px; -fx-border-width: 5px");
	    trainingModeButton.setOnMousePressed(event -> darkenButton(trainingModeButton));
	    trainingModeButton.setOnMouseReleased(event -> resetButtonStyle(trainingModeButton));
	    trainingModeButton.setOnAction(event -> startTrainingMode(stage));

	    Button raceModeButton = new Button("Race Mode");
	    raceModeButton.setFont(buttonFont);
	    raceModeButton.setStyle("-fx-background-radius: 15px; -fx-text-fill: #343857; -fx-background-color: #A2D9FF; -fx-border-color:#343857; -fx-border-radius: 15px; -fx-border-width: 5px");
	    raceModeButton.setOnMousePressed(event -> darkenButton(raceModeButton));
	    raceModeButton.setOnMouseReleased(event -> resetButtonStyle(raceModeButton));

	    HBox usernameBox = new HBox();
	    usernameBox.setAlignment(Pos.CENTER);
	    usernameBox.setSpacing(10);
	    usernameBox.setVisible(false);
	    usernameBox.setStyle("-fx-background-color: #C7E2F5; -fx-background-radius: 10px; -fx-padding: 40px 1px;");
	    usernameBox.setMaxWidth(400); // set a maximum width for the username box

	    TextField usernameField = new TextField();
	    usernameField.setPromptText("Enter your username");
	    usernameField.setStyle("-fx-background-radius: 20px; -fx-pref-width: 200px;");
	    usernameField.setOnAction(event -> {
	        String username = usernameField.getText().trim();
	        if (!username.isEmpty()) {
	            GraphicsContext gc = this.canvas.getGraphicsContext2D();
	            ChatClient chatClient = new ChatClient(gameScene, gc, stage, username);
	            chatClient.runChat();
	        }
	    });

	    Button enterButton = new Button("Enter");
	    enterButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
	    enterButton.setOnAction(event -> {
	        String username = usernameField.getText().trim();
	        if (!username.isEmpty()) {
	            GraphicsContext gc = this.canvas.getGraphicsContext2D();
	            ChatClient chatClient = new ChatClient(gameScene, gc, stage, username);
	            chatClient.runChat();
	        }
	    });

	    usernameBox.getChildren().addAll(usernameField, enterButton);

	    raceModeButton.setOnAction(event -> {
	        usernameBox.setVisible(true);
	        usernameField.requestFocus(); // automatically activate the username field
	    });
	    
	    Label returnLabel = new Label("Press [ESC] to return to the main menu");
	    returnLabel.setFont(Font.font("Verdana", 16));
	    VBox.setMargin(returnLabel, new Insets(0, 0, -20, 0));

	    content.getChildren().addAll(logo, trainingModeButton, raceModeButton, usernameBox, returnLabel);

	    root.getChildren().add(content);

	    Scene chatScene = new Scene(root, 800, 600);
        chatScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                ChatClient chatClient = (ChatClient) stage.getUserData();
                if (chatClient != null) {
                    chatClient.disconnect();
                }
                initMenu(stage);
            }
        });
	    
	    stage.setScene(chatScene);
	}
	
	private void newTextToType() {
        String newSentence = QuoteFetcher.fetchRandomQuote();
        if (newSentence != null) {
            this.textToType = newSentence;
            System.out.println("New sentence: " + this.textToType);
        } else {
            System.out.println("Failed to fetch a new sentence");
        }
    }
	
	public String getTextToType() {
	    return this.textToType;
	}
	
	private void startTrainingMode(Stage stage) {
	    newTextToType();
	    GraphicsContext gc = this.canvas.getGraphicsContext2D();
	    // 1 and 1 is for readyClients and userID respectively (for multiplayer consideration)
	    GameTimer gameTimer = new GameTimer(gameScene, gc, textToType, stage, 1, 1, null, null);
	    stage.setScene(gameScene);
	    gameTimer.start();
	}
	
	private void startRaceMode(Stage stage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Username");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter your username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            GraphicsContext gc = this.canvas.getGraphicsContext2D();
            ChatClient chatClient = new ChatClient(gameScene, gc, stage, username);
            chatClient.runChat();
        });
    }
	
	private void fetchLeaderboardData(VBox leaderboardsBox) {
	    // clear the existing scores
	    leaderboardsBox.getChildren().clear();

	    // Send a request to the server to fetch the leaderboard data
	    String message = "getLeaderboard";
	    byte[] data = message.getBytes();
	    DatagramPacket packet = new DatagramPacket(data, data.length, address, SERVER_PORT);
	    try {
	        socket.send(packet);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Receive the leaderboard data from the server
	    byte[] buffer = new byte[1024];
	    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
	    try {
	        socket.receive(receivePacket);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    String leaderboardData = new String(receivePacket.getData(), 0, receivePacket.getLength());
	    String[] playerScores = leaderboardData.split("\\|");

	    if (playerScores.length > 0 && !playerScores[0].isEmpty()) {
	        // Populate the leaderboardsBox with the received data
	        int count = 0;
	        for (String playerScore : playerScores) {
	            if (count >= 10) {
	                break; // Limit to 10 entries
	            }
	            String[] parts = playerScore.split(";");
	            if (parts.length == 3) {
	                String identifier = parts[0];
	                double wordsPerMinute = Double.parseDouble(parts[1]);
	                double accuracy = Double.parseDouble(parts[2]);
	                Label scoreLabel = new Label(String.format("%s - %d wpm, %.0f%% accuracy", identifier, (int)wordsPerMinute, accuracy));
	                scoreLabel.setFont(Font.font("Verdana", 16));
	                leaderboardsBox.getChildren().add(scoreLabel);
	                count++;
	            }
	        }
	    } else {
	        // Display a message when no scores are available
	        Label noScoresLabel = new Label("No scores available.");
	        noScoresLabel.setFont(Font.font("Verdana", 16));
	        leaderboardsBox.getChildren().add(noScoresLabel);
	    }
	}
}
