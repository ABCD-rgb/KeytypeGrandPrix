package typingGame;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


/* This Class is the client-logic (sends and receives data using the server as the middleman) */


public class ChatClient {
    private DatagramSocket socket;	// socket to send and receive data
    private InetAddress address;	// address of the server
    private static final int SERVER_PORT = Constants.PORT; // port number for the server
    
    private String identifier;	// username of the player
    private VBox messageBox;	// message box to display chat messages
    private static final TextField inputBox = new TextField();	// input box to type messages
    private Scene gameScene;
    private GraphicsContext gc;
    private Stage stage;
    private boolean isReady;
	private GameTimer gameTimer;

    public ChatClient(Scene gameScene, GraphicsContext gc, Stage stage, String username) {
        this.gameScene = gameScene;
        this.gc = gc;
        this.stage = stage;
        this.identifier = username;
        this.isReady = false;
        connect();
    }
    
    
    // Method to connect to the server
    public void connect() {
        try {
            socket = new DatagramSocket(); // init on any available port
            address = InetAddress.getByName(Constants.IP);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public GameTimer getGameTimer() {
    	return this.gameTimer;
    }
    
    // method to join the chat room
    public void runChat() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #A6C9CB;");      
        
        VBox topBox = new VBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.setSpacing(20);
        topBox.setPadding(new Insets(20));

        ImageView logo = new ImageView(new Image("images/logo.png"));
        logo.setFitWidth(150);
        logo.setFitHeight(50);

        // display waiting area message
        Text waitingMessage = new Text("Waiting for players to join...");
        waitingMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        waitingMessage.setFill(Color.web("#343857"));
        waitingMessage.setTextAlignment(TextAlignment.CENTER);

        topBox.getChildren().addAll(logo, waitingMessage);
        root.setTop(topBox);
        
        // create the message box to display chat messages
        messageBox = new VBox(10);
        messageBox.setStyle("-fx-background-color: #A6C9CB;");
        messageBox.setPadding(new Insets(10));
        
        // start a separate thread for receiving messages
        ClientThread clientThread = new ClientThread(socket, messageBox, this);
        clientThread.start();

        // send initialization message to the server with the user's identifier
        byte[] uuid = ("init;" + identifier).getBytes();
        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
        try {
            socket.send(initialize);
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
        
        // create a ScrollPane to make the message area scrollable
        ScrollPane scrollPane = new ScrollPane(messageBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #A6C9CB; -fx-background-color: #A6C9CB;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // hide horizontal scroll bar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // hide vertical scroll bar
        root.setCenter(scrollPane);
        messageBox.heightProperty().addListener((observable) -> {
            scrollPane.setVvalue(1.0);	// autoscroll to the bottom when a new message is added
        });

        // create the input box to type messages
        inputBox.setPromptText("Type here...");
        inputBox.setStyle("-fx-background-radius: 20px; -fx-pref-width: 600px;");
        inputBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();	// send the message when the Enter key is pressed
            }
        });

        // create the send button
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        sendButton.setOnAction(e -> {
            sendMessage();	// send the message when the button is clicked
        });
                
        // send a message to the server to fetch previous chats and messages
        String fetchMessage = "fetch:" + identifier;
        byte[] fetchMsg = fetchMessage.getBytes();
        DatagramPacket fetchSend = new DatagramPacket(fetchMsg, fetchMsg.length, address, SERVER_PORT);
        try {
            socket.send(fetchSend);
        } catch (IOException err) {
            throw new RuntimeException(err);
        }

        // create the start game button
        Button startButton = new Button("START GAME");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        startButton.setOnAction(e -> {
        	// send a message to the server to indicate that the player is ready
        	this.isReady = true;
            String message = identifier + " is ready";
            byte[] msg = message.getBytes();
            DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
            try {
                socket.send(send);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }

            startButton.setDisable(true);	// disable the start game button after clicking
            startButton.setStyle("-fx-background-color: #A9A9A9; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            displayReadyMessage(identifier);	// display a message that the player is ready

            // send the fetched sentence to the server
            Game game = (Game) stage.getUserData();
            String textToType = game.getTextToType();
            String sentenceMessage = "sentence:" + textToType;
            byte[] sentenceMsg = sentenceMessage.getBytes();
            DatagramPacket sentenceSend = new DatagramPacket(sentenceMsg, sentenceMsg.length, address, SERVER_PORT);
            try {
                socket.send(sentenceSend);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }
        });

        // create a horizontal box for the input box and send button        
        HBox inputArea = new HBox();
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setSpacing(10);
        inputArea.setStyle("-fx-background-color: #C7E2F5; -fx-background-radius: 10px; -fx-padding: 25px 20px;");
        inputArea.setMaxWidth(600);
        inputBox.setPrefWidth(400);
        HBox.setHgrow(inputBox, Priority.ALWAYS);
        sendButton.setMinWidth(100);

        inputArea.getChildren().addAll(inputBox, sendButton);

        // create a label to return to the main menu
        Label returnLabel = new Label("Press [ESC] to return to the main menu");
        returnLabel.setFont(Font.font("Verdana", 16));
        VBox.setMargin(returnLabel, new Insets(0, 0, 20, 0));
        
        // create a vertical box for the start game button and input box
        VBox bottomBox = new VBox(20, startButton, inputArea, returnLabel);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        Scene chatScene = new Scene(root, 800, 600);
        chatScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
            	disconnect();
            	// reduce the number of players
                Game game = new Game();	// return to the main menu when the Escape key is pressed
                game.setStage(stage);
            }
        });
        
        stage.setScene(chatScene);
    }
    
    
    // method to start the game
    public void handleStartGameMessage(int readyClients, int userID, String textToType) {
    	Platform.runLater(() -> {
            GraphicsContext gc = this.gc;
            gc.clearRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            stage.setScene(gameScene);
            this.gameTimer = new GameTimer(gameScene, gc, textToType, stage, readyClients, userID, this.socket, this.address);
            this.gameTimer.start();
        });
    }
    
    // method to send a message to the server
    public void displayEnterMessage(String userName) {
        Text enterText = new Text(userName + " has entered the waiting room.");
        enterText.setFill(Color.BLUE);
        enterText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextFlow enterMessage = new TextFlow(enterText);
        enterMessage.setTextAlignment(TextAlignment.CENTER);

        VBox.setMargin(enterMessage, new Insets(10));
        messageBox.getChildren().add(enterMessage);
    }
    
    
    // method to display a message that a player is ready
    public void displayReadyMessage(String userName) {
        Text readyText = new Text(userName + " is ready.");
        readyText.setFill(Color.GREEN);
        readyText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        TextFlow readyMessage = new TextFlow(readyText);
        readyMessage.setTextAlignment(TextAlignment.CENTER);
        
        VBox.setMargin(readyMessage, new Insets(10));
        messageBox.getChildren().add(readyMessage);
    }
    
    // method to display a message that a player is ready
    public void displayExitMessage(String userName) {
        Text exitText = new Text(userName + " has disconnected.");
        exitText.setFill(Color.RED);
        exitText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        TextFlow exitMessage = new TextFlow(exitText);
        exitMessage.setTextAlignment(TextAlignment.CENTER);
        
        VBox.setMargin(exitMessage, new Insets(10));
        messageBox.getChildren().add(exitMessage);
    }
    
    // method to send a message to the server
    private void sendMessage() {
        String message = inputBox.getText();
        if (!message.isEmpty()) {
            String temp = identifier + ": " + message;
            byte[] msg = temp.getBytes();
            inputBox.setText("");	// clear the input box

            // create a message bubble for the sent message
            TextFlow messageBubble = createMessageBubble(message, true, "You");
            messageBox.getChildren().add(messageBubble);

            DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
            try {
                socket.send(send);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }
        }
    }
    
    // method to create a message bubble with the given message, sender name, and style
    private TextFlow createMessageBubble(String message, boolean isMyMessage, String senderName) {
        TextFlow messageBubble = new TextFlow();
        messageBubble.setMaxWidth(300);
        messageBubble.setPadding(new Insets(10));

        Text senderText = new Text();
        senderText.setFill(Color.web("#2196F3"));
        senderText.setStyle("-fx-font-weight: bold;");

        Text messageText = new Text(message);
        messageText.setFill(Color.BLACK);

        if (isMyMessage) {
            messageBubble.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 20px;");
            VBox.setMargin(messageBubble, new Insets(0, 0, 0, 475));
            senderText.setText("You: ");
        } else {
            messageBubble.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20px;");
            VBox.setMargin(messageBubble, new Insets(0, 475, 0, 0));
            senderText.setText(senderName + ": ");
        }

        messageBubble.getChildren().addAll(senderText, messageText);

        return messageBubble;
    }
    
    // method to save the player's score
    public void sendScore(double wordsPerMinute, double accuracy) {
        String message = "score;" + identifier + ";" + wordsPerMinute + ";" + accuracy;
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, SERVER_PORT);
        try {
            socket.send(packet);
            System.out.println("Score sent to server: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Method to disconnect from the server
    public void disconnect() {
    	// remove the player from the player list in the server
    	String message = "disconnect;"+this.identifier+";"+this.isReady;
    	byte[] data = message.getBytes();
    	DatagramPacket packet = new DatagramPacket(data, data.length, address, SERVER_PORT);
    	try {
    		socket.send(packet);
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (socket != null && !socket.isClosed()) {
    			socket.close();
    		}    		
    	}
    	
    }
    
    

}

