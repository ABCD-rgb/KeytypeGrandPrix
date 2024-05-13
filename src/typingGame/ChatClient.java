package typingGame;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.*;

public class ChatClient {
    private static final DatagramSocket socket;

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

    private static final String identifier = "Adam";

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
    
    
    public void joinChat() {
        // thread for receiving messages
        ClientThread clientThread = new ClientThread(socket, messageArea);
        clientThread.start();

        // send initialization message to the server
        byte[] uuid = ("init;" + identifier).getBytes();
        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
        try {        	
        	socket.send(initialize);
        } catch (IOException err) {
        	throw new RuntimeException(err);
        }
    }
    
    
    public void runChat() {
    	Button sendButton = new Button("Send");
	    Button startButton = new Button("Start Game");
    	VBox root = new VBox(10, messageArea, inputBox, sendButton, startButton);
    	
    	inputBox.setOnKeyPressed(e -> {
    	    if (e.getCode() == KeyCode.ENTER) {
    	        sendMessage();
    	    }
    	});
	    
    	sendButton.setOnAction(e -> {
    	    sendMessage();
    	});
        
        startButton.setOnAction(e -> {
        	stage.setScene(gameScene);
			String textToType = "type the text because this is test test test.";
			GameTimer gameTimer = new GameTimer(gameScene, gc, textToType, stage);
			gameTimer.start();	// internally calls the handle() method of GameTimer
        });
        
        
        Scene chatScene = new Scene(root, 800, 600);
        stage.setScene(chatScene);
        
        
    }
    
    private void sendMessage() {
        String temp = identifier + ": " + inputBox.getText(); // message to send
        messageArea.setText(messageArea.getText() + inputBox.getText() + "\n"); // update messages on screen
        byte[] msg = temp.getBytes(); // convert to bytes
        inputBox.setText(""); // remove text from input box

        // create a packet & send
        DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
        try {
            socket.send(send);
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
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

