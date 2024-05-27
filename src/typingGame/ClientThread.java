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


/* This Class takes a client and runs it on a Thread */


public class ClientThread extends Thread {
    private DatagramSocket socket;	// socket to send and receive data
    private byte[] incoming = new byte[256];	// byte array to store incoming data
    private VBox messageBox;	// message box to display chat messages
    private ChatClient chatClient;	// chat client object
    
    
    public ClientThread(DatagramSocket socket, VBox messageBox, ChatClient chatClient) {
        this.socket = socket;
        this.messageBox = messageBox;
        this.chatClient = chatClient;
    }

    
    @Override    
    public void run() {
        System.out.println("starting thread");
        
        while (true) {
        	try{
				Thread.sleep(1);
			}catch(Exception ioe){}
        	
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);    // receive packet
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            //System.out.println(receivedMessage);
            if (receivedMessage.startsWith("updatePosition:")) {
                System.out.println("Received: " + receivedMessage);
                String[] parts = receivedMessage.split(":");
                if (parts.length == 3) {
                    int enemyIndex = Integer.parseInt(parts[1]);
                    int enemyCurrentIndex = Integer.parseInt(parts[2]);
                    // for testing
                    //System.out.println("Updating opponent with enemyIndex: " + enemyIndex + ", enemyCurrentIndex: " + enemyCurrentIndex);
                    chatClient.getGameTimer().moveOpponent(enemyIndex, enemyCurrentIndex);
                }
            }

            // handle different types of messages
            else if (receivedMessage.startsWith("startGame")) {
                String[] parts = receivedMessage.split(";");
                if (parts.length >= 4) {
                    int readyClients = Integer.parseInt(parts[1]);
                    int userID = Integer.parseInt(parts[2]);
                    String textToType = parts[3]; // get the sentence from the server
                    Platform.runLater(() -> {
                        chatClient.handleStartGameMessage(readyClients, userID, textToType); // call the method to start the game
                    });
                } else {
                    System.out.println("Received invalid startGame message: " + receivedMessage);
                }
            } else if (receivedMessage.startsWith("fetchResponse:")) { // handle fetch response with chat history
                String[] messages = receivedMessage.substring(14).split("\\|");
                Platform.runLater(() -> {
                    for (String message : messages) {
                        if (message.endsWith(" has entered the waiting room.")) {
                            String senderName = message.substring(0, message.length() - 30);
                            chatClient.displayEnterMessage(senderName); // display enter message
                        } else if (message.endsWith(" is ready")) {
                            String senderName = message.substring(0, message.length() - 9);
                            chatClient.displayReadyMessage(senderName); // display ready message
                        } else if (message.endsWith(" has disconnected.")) {
                        	String senderName = message.substring(0, message.length() - 9);
                        	chatClient.displayExitMessage(senderName);
                        }
                    }
                });
            } else { // handle regular chat messages
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
//        clientChat.moveEnemy(userId, position);
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
//    
//    private void handlePositionUpdate(String receivedMessage) {
//        String positionData = receivedMessage.substring("updatePosition:".length());
//        String[] parts = positionData.split(":");
//        if (parts.length == 3) {
//            int opponentID = Integer.parseInt(parts[0]);
//            double x = Double.parseDouble(parts[1]);
//            double y = Double.parseDouble(parts[2]);
//            Platform.runLater(() -> {
//                chatClient.updateOpponentPosition(opponentID, x, y); // call the method to update opponent's position
//            });
//        }
//    }
    

}