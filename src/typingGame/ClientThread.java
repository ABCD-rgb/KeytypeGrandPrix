package typingGame;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;	
import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
//import javafx.geometry.Pos;
//import javafx.scene.control.Label;
//import javafx.scene.layout.HBox;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
import java.util.List;

public class ClientThread extends Thread {

    private DatagramSocket socket;	// socket to send and receive data
    private byte[] incoming = new byte[256];	// byte array to store incoming data
    private VBox messageBox;	// message box to display chat messages
    private ChatClient chatClient;	// chat client object
    private List<Car> cars;
    private String[] words;
    
    public ClientThread(DatagramSocket socket, VBox messageBox, ChatClient chatClient, List<Car> cars, String[] words) {
        this.socket = socket;
        this.messageBox = messageBox;
        this.chatClient = chatClient;
        this.cars = cars;
        this.words = words;
    }

    @Override    
    public void run() {
        System.out.println("starting thread");
        while (true) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);	// receive packet
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            
            // handle different types of messages
            if (receivedMessage.startsWith("startGame:")) {
                int numReadyPlayers = Integer.parseInt(receivedMessage.substring(10));
                Platform.runLater(() -> {
                    chatClient.handleStartGameMessage(numReadyPlayers);	// handle start game message
                });
            } else if (receivedMessage.startsWith("fetchResponse:")) {	// handle fetch response with chat history
                String[] messages = receivedMessage.substring(14).split("\\|");
                Platform.runLater(() -> {
                    for (String message : messages) {
                        if (message.endsWith(" has entered the waiting room.")) {
                            String senderName = message.substring(0, message.length() - 30);
                            chatClient.displayEnterMessage(senderName);	// display enter message
                        } else if (message.endsWith(" is ready")) {
                            String senderName = message.substring(0, message.length() - 9);
                            chatClient.displayReadyMessage(senderName);	// display ready message
                        }
                    }
                });
            } else {	// handle regular chat messages
                String[] parts = receivedMessage.split(": ");
                if (parts.length == 2) {
                    String senderName = parts[0];
                    String chatMessage = parts[1];

                    // create a message bubble for the received message
                    TextFlow messageBubble = createMessageBubble(chatMessage, false, senderName);
                    
                    // update the message box on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        messageBox.getChildren().add(messageBubble);
                    });
                }
            }
        }
    }
       
    // create a message bubble with the given message, sender name, and style
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
    
    private void moveOtherCars(int carIndex, double targetX) {
        Car otherCar = cars.get(carIndex);
        otherCar.move(targetX, (double) words.length);
    }
}

//public void run() {
//System.out.println("starting thread");
//while (true) {
//  DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
//  try {
//      socket.receive(packet);
//  } catch (IOException e) {
//      throw new RuntimeException(e);
//  }
//  
//  String receivedMessage = new String(packet.getData(), 0, packet.getLength());
//  if (receivedMessage.equals("startGame")) {
//      Platform.runLater(() -> {
//          chatClient.handleStartGameMessage();
//      });
//  } else if (receivedMessage.startsWith("fetchResponse:")) {
//      String[] messages = receivedMessage.substring(14).split("\\|");
//      Platform.runLater(() -> {
//          for (String message : messages) {
//              if (message.endsWith(" has entered the waiting room.")) {
//                  String senderName = message.substring(0, message.length() - 30);
//                  chatClient.displayEnterMessage(senderName);
//              } else if (message.endsWith(" is ready")) {
//                  String senderName = message.substring(0, message.length() - 9);
//                  chatClient.displayReadyMessage(senderName);
//              } else {
//                  // Handle regular chat messages
//                  // ...
//              }
//          }
//      });
//  } else if (receivedMessage.endsWith(" is ready")) {
//      String senderName = receivedMessage.substring(0, receivedMessage.length() - 9);
//      Platform.runLater(() -> {
//          chatClient.displayReadyMessage(senderName);
//      });
//  } else if (receivedMessage.endsWith(" has entered the waiting room.")) {
//      String senderName = receivedMessage.substring(0, receivedMessage.length() - 30);
//      Platform.runLater(() -> {
//          chatClient.displayEnterMessage(senderName);
//      });
//  } else {
//  	String[] parts = receivedMessage.split(": ");
//      String senderName = parts[0];
//      String message = parts[1];
//
//      // create a message bubble for the received message
//      TextFlow messageBubble = createMessageBubble(message, false, senderName);
//      
//      // update the message box on the JavaFX Application Thread
//      Platform.runLater(() -> {
//          messageBox.getChildren().add(messageBubble);
//      });
//  }            
//}
//}
