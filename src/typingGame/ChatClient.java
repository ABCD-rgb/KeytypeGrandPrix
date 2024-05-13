package typingGame;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.*;

public class ChatClient {
    private static final DatagramSocket socket;
    private VBox messageBox; // declare messageBox as an instance variable

    static {
        try {
            socket = new DatagramSocket(); // init to any available port
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static final InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String identifier = "Alan";

    private static final int SERVER_PORT = 8000; // send to server

    private static final TextArea messageArea = new TextArea();

    private static final TextField inputBox = new TextField();
    
    
    private Scene gameScene;
    private GraphicsContext gc;
    private Stage stage;
    
    public ChatClient(Scene gameScene, GraphicsContext gc, Stage stage) {
    	this.gameScene = gameScene;
    	this.gc = gc;
    	this.stage = stage;
    }
    
    //    public void joinChat() {
//        // thread for receiving messages
//        ClientThread clientThread = new ClientThread(socket, messageBox);
//        clientThread.start();
//
//        // send initialization message to the server
//        byte[] uuid = ("init;" + identifier).getBytes();
//        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
//        try {        	
//        	socket.send(initialize);
//        } catch (IOException err) {
//        	throw new RuntimeException(err);
//        }
//    }
    
    public void runChat() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #A6C9CB;");
        
        // create the message box
        messageBox = new VBox(10);
        messageBox.setStyle("-fx-background-color: #A6C9CB;");
        messageBox.setPadding(new Insets(10));
        
        // thread for receiving messages
        ClientThread clientThread = new ClientThread(socket, messageBox);
        clientThread.start();

        // send initialization message to the server
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
        root.setCenter(scrollPane);

        // create the input box
        inputBox.setPromptText("Type here...");
        inputBox.setStyle("-fx-background-radius: 20px; -fx-pref-width: 600px;");
        inputBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        // create the send button
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        sendButton.setOnAction(e -> {
            sendMessage();
        });

        // create the start game button
        Button startButton = new Button("START GAME");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        startButton.setOnAction(e -> {
            stage.setScene(gameScene);
            String textToType = "type the text because this is test test test.";
            GameTimer gameTimer = new GameTimer(gameScene, gc, textToType, stage);
            gameTimer.start();    // internally calls the handle() method of GameTimer
        });

        // create a horizontal box for the input box and send button
        HBox inputHBox = new HBox(10, inputBox, sendButton);
        inputHBox.setAlignment(Pos.CENTER);
        inputHBox.setPadding(new Insets(0, 0, 10, 0));

        // create a vertical box for the start game button and input box
        VBox bottomBox = new VBox(20, startButton, inputHBox);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        Scene chatScene = new Scene(root, 800, 600);
        stage.setScene(chatScene);
    }
    
    private void sendMessage() {
        String message = inputBox.getText();
        if (!message.isEmpty()) {
            String temp = identifier + ": " + message;
            byte[] msg = temp.getBytes();
            inputBox.setText("");

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

