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
//import javafx.scene.control.TextArea;

public class ChatClient {
    private static final DatagramSocket socket;	// socket to send and receive data
    private VBox messageBox;	// message box to display chat messages

    static {
        try {
            socket = new DatagramSocket();	// init on any available port
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static final InetAddress address;	// address of the server

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }   

    private static final int SERVER_PORT = 8000; // port number for the server
    private static final TextField inputBox = new TextField();	// input box to type messages
//  private static final TextArea messageArea = new TextArea();
    
    private Scene gameScene;
    private GraphicsContext gc;
    private Stage stage;
    private String identifier;	// username of the player
    
    public ChatClient(Scene gameScene, GraphicsContext gc, Stage stage, String username) {
        this.gameScene = gameScene;
        this.gc = gc;
        this.stage = stage;
        this.identifier = username;
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
                Game game = new Game();	// return to the main menu when the Escape key is pressed
                game.setStage(stage);
            }
        });
        
        stage.setScene(chatScene);
    }
    
    // method to start the game
    public void handleStartGameMessage() {
        Platform.runLater(() -> {
            stage.setScene(gameScene);	// switch to the game scene
            String textToType = "type the text because this is test test test.";	// text to type in the game
            GameTimer gameTimer = new GameTimer(gameScene, gc, textToType, stage);
            gameTimer.start();	// start the game timer
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
        
}

//import javafx.scene.Scene;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//public class ChatClient {
//    private static final DatagramSocket socket;
//
//    static {
//        try {
//            socket = new DatagramSocket(); // init to any available port
//        } catch (SocketException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static final InetAddress address;
//
//    static {
//        try {
//            address = InetAddress.getByName("localhost");
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static final String identifier = "Adam";
//
//    private static final int SERVER_PORT = 8000; // send to server
//
//    private static final TextArea messageArea = new TextArea();
//
//    private static final TextField inputBox = new TextField();
//
//    private int numPlayers; // Total number of players
//    private int readyPlayers; // Number of players ready to start the game
//
//    private Scene gameScene;
//    private GraphicsContext gc;
//    private Stage stage;
//
//    public ChatClient(Scene gameScene, GraphicsContext gc, Stage stage, int numPlayers) {
//        this.gameScene = gameScene;
//        this.gc = gc;
//        this.stage = stage;
//        this.numPlayers = numPlayers;
//        this.readyPlayers = 0;
//    }
//
//    public void joinChat() {
//        // thread for receiving messages
//        ClientThread clientThread = new ClientThread(socket, messageArea);
//        clientThread.start();
//
//        // send initialization message to the server
//        byte[] uuid = ("init;" + identifier).getBytes();
//        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
//        try {
//            socket.send(initialize);
//        } catch (IOException err) {
//            throw new RuntimeException(err);
//        }
//    }
//
//    public void runChat() {
//        Button sendButton = new Button("Send");
//        Button startButton = new Button("Start Game");
//        VBox root = new VBox(10, messageArea, inputBox, sendButton, startButton);
//
//        sendButton.setOnAction(e -> {
//            String temp = identifier + ": " + inputBox.getText(); // message to send
//            messageArea.setText(messageArea.getText() + inputBox.getText() + "\n"); // update messages on screen
//            byte[] msg = temp.getBytes(); // convert to bytes
//            inputBox.setText(""); // remove text from input box
//
//            // create a packet & send
//            DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
//            try {
//                socket.send(send);
//            } catch (IOException err) {
//                throw new RuntimeException(err);
//            }
//        });
//
//        startButton.setOnAction(e -> {
//            readyPlayers++; // increment the number of ready players
//            if (readyPlayers >= numPlayers) { // if all players are ready, start the game
//                startGame();
//            }
//        });
//
//        Scene chatScene = new Scene(root, 800, 600);
//        stage.setScene(chatScene);
//    }
//
//    private void startGame() {
//        stage.setScene(gameScene);
//        String textToType = "type the text because this is test test test.";
//        GameTimer gameTimer = new GameTimer(gameScene, gc, textToType, stage);
//        gameTimer.start();    // internally calls the handle() method of GameTimer
//    }
//}

