package typingGame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.text.Text;


/* This Class displays the animation inside the Game */


public class GameTimer extends AnimationTimer {
    private GraphicsContext gc;
    private Scene gameScene;
    private Stage stage;
    private long startTime;
    private long gameDuration;
    private long remainingTime;
    private Text timerText;
    private String[] words;
    private int currentWordIndex;
    private int totalCharactersTyped;
    private int correctCharactersTyped;
    // data for multiplayer
    private DatagramSocket socket;
    private InetAddress address;	// address of the server
    private static final int SERVER_PORT = Constants.PORT; // port number for the server
    
    private Car carUser;
    private List<Car> carOpponents = new ArrayList<>();
    private int totalPlayers;
    private int userID;
    private boolean isMultiplayer;
    private Client client;
    
    public GameTimer(Scene gameScene, GraphicsContext gc, String textToType, Stage stage, int readyClients, int userID, DatagramSocket socket, InetAddress address, Client client) {
//      this.gameDuration = calculateGameDuration(textToType);
//      this.timerText = new Text();
//      this.timerText.setFont(Font.font("Verdana", 16));
//      this.timerText.setFill(Color.WHITE);
//      this.isMultiplayer = false;
    	this.gc = gc;
        this.gameScene = gameScene;
        this.startTime = System.nanoTime();
        this.words = textToType.split("\\s+");
        this.currentWordIndex = 0;
        this.totalCharactersTyped = 0;
        this.correctCharactersTyped = 0;
        this.stage = stage;
        this.isMultiplayer = (socket != null && address != null);
        this.totalPlayers = readyClients;
        this.userID = userID;
        this.client = client;

        int xPos = 20;
        int ySpacing = 50;
        int totalHeight = (totalPlayers - 1) * ySpacing;
        int startY = (Constants.WINDOW_HEIGHT - totalHeight) / 2;

        for (int i = 1; i <= totalPlayers; i++) {
            int yPos = startY + (i - 1) * ySpacing;
            if (this.userID == i) {
                this.carUser = new Car(xPos, yPos, userID);
            } else {
                carOpponents.add(new Car(xPos, yPos, i));
            }
        }
        
        // assign socket and address
        this.socket = socket;
        this.address = address;

        // methods ran at the start of GameTimer
        this.handleKeyPressEvent();
    }


    @Override
    public void handle(long currentNanoTime) {
    	try {
	        long elapsedTime = currentNanoTime - startTime;
//	        remainingTime = gameDuration - elapsedTime / 1_000_000_000;
//	        
//	        // check if time is up
//	        if (remainingTime <= 0) {
//	            stop(); // stop the game
//	            gameOverMessage(); // display game over message
//	            gameScene.setOnKeyPressed(null);
//	            handleGameOverKeyPress();
//	            return;
//	        }
	
	        // render background, car, text to type, and timer
	        this.renderBackground();
	        
	        this.renderCars();
	        this.renderTextToType();
	        this.renderAccuracyMeter();
	        this.renderSpeedometer();
	        
    	} catch (Exception e) {
            e.printStackTrace();
            // handle the exception gracefully and ensure that the timer continues running
        }
    }
    
    // calculate the duration of the game based on the text to type
    private long calculateGameDuration(String textToType) {
        int characterCount = textToType.length();
        double wordsPerMinute = 180.0; // assuming an average typing speed of 180 words per minute
        double minutesRequired = characterCount / wordsPerMinute;
        long gameDurationInSeconds = (long) Math.ceil(minutesRequired * 60); // convert minutes to seconds
        return gameDurationInSeconds;
    }

    private void renderBackground() {
    	gc.clearRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
        gc.setFill(Color.web("#A6C9CB"));
        gc.fillRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
    }
        
    private void renderCars() {
        for (int i = 0; i < totalPlayers; i++) {
            double yPos;
            if (i == userID - 1) {
                yPos = carUser.getYPos();
            } else if (i < carOpponents.size()) {
                yPos = carOpponents.get(i).getYPos();
            } else {
                continue;
            }
                    
	        // render road image
//	        Image roadImage = new Image("images/road.png", gameScene.getWidth(), gameScene.getHeight(), false, false);
//          Image roadImage = new Image("images/road.png", gameScene.getWidth(), Constants.CAR_HEIGHT * 20, false, false);
//          gc.drawImage(roadImage, 0, yPos - Constants.CAR_HEIGHT);
//	        gc.drawImage(roadImage, 0, 0);
        }

        carUser.render(gc);
        for (Car car : carOpponents) {
            car.render(gc);
        }
    }

    private String[] wrapText(String text, double maxWidth) {
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            double lineWidth = gc.getFont().getSize() * (currentLine.length() + word.length()) * 0.6; // Adjust the factor as needed
            if (lineWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }
        lines.add(currentLine.toString());

        return lines.toArray(new String[0]);
    }
    
    private void renderTextToType() {
        gc.setFont(new Font("Lucida Console", 20));
        gc.setTextAlign(TextAlignment.CENTER);

        StringBuilder displayText = new StringBuilder();
        for (int i = currentWordIndex; i < words.length; i++) {
            displayText.append(words[i]).append(" ");
        }

        // calculate the maximum width for the text
        double maxWidth = gameScene.getWidth() - 100;

        // wrap the text into multiple lines if necessary
        String[] lines = wrapText(displayText.toString(), maxWidth);

        // calculate the height of the text
        double textHeight = gc.getFont().getSize() * lines.length;
        double textY = gameScene.getHeight() - 70;

        // calculate the background rectangle dimensions
        double backgroundWidth = maxWidth;
        double backgroundHeight = textHeight + 30;
        double backgroundX = (gameScene.getWidth() - backgroundWidth) / 2;
        double backgroundY = textY - textHeight / 2 - 20;

        // draw the background rectangle
        gc.setFill(Color.web("#C7E2F5"));
        gc.fillRoundRect(backgroundX, backgroundY, backgroundWidth, backgroundHeight, 10, 10);

        // draw the text lines
        gc.setFill(Color.BLACK);
        for (int i = 0; i < lines.length; i++) {
            double lineY = textY + (i - (lines.length - 1) / 2.0) * gc.getFont().getSize();
            gc.fillText(lines[i], gameScene.getWidth() / 2, lineY);
        }
    }
    
    private void displayMessage(String message, Color color, int durationInMillis) {
        Text messageText = new Text(message);
        messageText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        messageText.setFill(color);
        messageText.setVisible(true);
        messageText.setX(gameScene.getWidth() / 2 - messageText.getLayoutBounds().getWidth() / 2);
        messageText.setY(150);
        ((Group) gameScene.getRoot()).getChildren().add(messageText);

        new Thread(() -> {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> ((Group) gameScene.getRoot()).getChildren().remove(messageText));
        }).start();
    }
    
    private void displayIncorrectKeyMessage() {
        displayMessage("Incorrect key!", Color.web("#EF5350"), 500);
    }

    private void displayRaceCompleteMessage() {
        displayMessage("Race Complete!", Color.web("#314528"), 5000);
    }
    
    private void gameOverMessage() {
        displayMessage("Game Over!", Color.web("#EF5350"), 5000);
        System.out.println("Game Over! Your time is up.");
    }

    private void renderAccuracyMeter() {
        gc.setFill(Color.web("#FFCF11"));
        gc.fillRoundRect(gameScene.getWidth() - 180, 20, 160, 40, 10, 10);
        gc.setFont(new Font("Verdana", 16));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        double accuracy = calculateAccuracy();
        gc.fillText("Accuracy: " + String.format("%.2f", accuracy) + "%", gameScene.getWidth() - 100, 45);
    }
    
    private void renderSpeedometer() {
        gc.setFill(Color.web("#FFCF11"));
        gc.fillRoundRect(20, 20, 140, 40, 10, 10);
        gc.setFont(new Font("Verdana", 16));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.LEFT);
        double wordsPerMinute = calculateWordsPerMinute();
        gc.fillText(String.format("Speed: %.0f WPM", wordsPerMinute), 30, 45);
    }

    private void moveCar() {
        // move the car only when there are words left to type
        if (currentWordIndex < words.length) {
            // calculate the target position for the car
            double wordWidth = gameScene.getWidth() / words.length;
            double targetX = (currentWordIndex + 1) * wordWidth;

            // move the car to the target position
            carUser.move(targetX, (double) words.length);
            
            
            // check if the current word is fully typed
            if (words[currentWordIndex].isEmpty()) {
                currentWordIndex++; // Move to the next word
            }
        } else {
            // if all words are typed, move the car to the end of the screen
            carUser.moveToEndOfScreen();
        }
    }
    
    public void moveOpponent(int index, int curr) {
        System.out.println("moveOpponent called with index: " + index + ", curr: " + curr);
        Car opponent = null;

        for (Car car : carOpponents) {
            if (index == car.getCarID()) {
                opponent = car;
                break; // found the car, no need to continue loop
            }
        }

        if (opponent == null) {
            //System.out.println("Opponent car not found for index: " + index);
            return;
        }

        double wordWidth = gameScene.getWidth() / words.length;
        double targetX = (curr + 1) * wordWidth;

        //System.out.println("Moving opponent car to x position: " + targetX);
        opponent.move(targetX, words.length);

        // Print the opponent car's new position
        //System.out.println("Opponent car new position: " + opponent.getXPos());
    }
    
    private double calculateWordsPerMinute() {
    	long elapsedTimeInNanos = System.nanoTime() - startTime;
        double elapsedTimeInSeconds = elapsedTimeInNanos / 1_000_000_000.0;
        return (double) correctCharactersTyped / 5.0 / elapsedTimeInSeconds * 60.0;
    }

    private double calculateAccuracy() {
        if (totalCharactersTyped == 0) {
            return 0.0; // return 0 accuracy if no characters are typed
        }
        return (double) correctCharactersTyped / totalCharactersTyped * 100.0;
    }
    
    private void handleKeyPressEvent() {
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ESCAPE) {
                    stop(); // stop the game timer
                    displayMessage("Game Paused", Color.web("#343857"), 2000); // display a pause message
                    gameScene.setOnKeyPressed(null);
                    handlePauseKeyPress(); // attach the key event handler for pause/resume and return to main menu
                } else if (words.length > 0 && currentWordIndex < words.length) {
                    String currentWord = words[currentWordIndex];
                    if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                        // if space/enter is pressed, move to the next word only if the current word is fully typed
                        if (currentWord.isEmpty()) {                        	
                            currentWordIndex++;
                            moveCar();
                        }
                    } else if (!currentWord.isEmpty()) {
                        char typedChar;
                        if (e.getCode() == KeyCode.CAPS) {
                            // check if Caps Lock is pressed
                            String text = e.getText();
                            if (!text.isEmpty()) {
                                typedChar = text.toUpperCase().charAt(0);
                            } else {
                                System.out.println("Caps Lock key pressed");
                                return; // ignore if Caps Lock was pressed and e.getText() returns empty string
                            }
                        } else if (e.getCode() == KeyCode.SHIFT) {
                            // check if Shift key is pressed
                            String text = e.getText();
                            if (!text.isEmpty()) {
                                typedChar = text.charAt(0);
                            } else {
                                System.out.println("Shift key pressed");
                                return; // ignore if Shift key was pressed and e.getText() returns empty string
                            }
                        } else if (e.getCode() == KeyCode.CONTROL) {
                            // ignore if Control key is pressed
                            return;
                        } else {
                            if (e.isShiftDown()) {
                                // Shift is down, get the uppercase character
                                typedChar = e.getText().toUpperCase().charAt(0);
                            } else {
                                // Shift is not down, use the character as is
                                typedChar = e.getText().charAt(0);
                            }
                        }
                        totalCharactersTyped++;
                        if (currentWord.charAt(0) == typedChar) {
                            // remove the first character from the current word
                            words[currentWordIndex] = currentWord.substring(1);
                            correctCharactersTyped++;
                            // check if the current word is completed
                            if (words[currentWordIndex].isEmpty()) {
                                currentWordIndex++;
                                moveCar();
                            }
                        } else {
                            displayIncorrectKeyMessage();
                        }
                    }
                    
                    if (isMultiplayer == true) {
                    	 String message = "updatePosition:"+ Integer.toString(userID) + ":" + currentWordIndex;
                           byte[] fetchMsg = message.getBytes();
                           DatagramPacket fetchSend = new DatagramPacket(fetchMsg, fetchMsg.length, address, SERVER_PORT);
                           try {
       						socket.send(fetchSend);
       					} catch (IOException e1) {
       						// TODO Auto-generated catch block
       						e1.printStackTrace();
       					}
                    }
                    // check if the entire sentence is typed correctly
                    if (currentWordIndex == words.length) {
                        displayRaceCompleteMessage();
                        stop();
                        gameScene.setOnKeyPressed(null);
                        handleGameOverKeyPress();
                    }
                }
            }
        });
    }

    private void handlePauseKeyPress() {
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ENTER) {
                    start(); // resume the game timer
                    displayMessage("Game Resumed", Color.web("#314528"), 2000); // display a resume message
                    handleKeyPressEvent(); // reattach the key event handler for game input
                } else if (e.getCode() == KeyCode.ESCAPE) {
                    // return to the main menu
                    Game game = new Game();
                    game.setStage(stage);
                }
            }
        });
    }
    
    private void handleGameOverKeyPress() {
    	gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ESCAPE) {
                	// remove the player from the player list in the server
                	String message = "disconnectGameEnd;";
                	byte[] data = message.getBytes();
                	DatagramPacket packet = new DatagramPacket(data, data.length, address, SERVER_PORT);
                	try {
                		socket.send(packet);
                	} catch (IOException err) {
                		err.printStackTrace();
                	} finally {
                		if (socket != null && !socket.isClosed()) {
                			socket.close();
                		}
                	}
                    // return to the main menu
                    Game game = new Game();
                    game.setStage(stage);
                }
            }
        });
    	
    	// send the score to the server along with the username
        if (isMultiplayer) {
            String message = "score:" + client.getIdentifer() + ":" + calculateWordsPerMinute() + ":" + calculateAccuracy();
            byte[] scoreData = message.getBytes();
            DatagramPacket scoreSend = new DatagramPacket(scoreData, scoreData.length, address, SERVER_PORT);
            try {
                socket.send(scoreSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // display game over popup with stats
        String gameOverStats = String.format("Words Per Minute: %.2f\nAccuracy: %.2f%%", calculateWordsPerMinute(), calculateAccuracy());
        displayGameOverPopup(gameOverStats);
    }

    private void displayGameOverPopup(String gameOverStats) {
        // group to hold the popup elements
        Group popupGroup = new Group();

        // background element
        Rectangle background = new Rectangle(gameScene.getWidth(), gameScene.getHeight(), Color.rgb(0, 0, 0, 0.7));
        popupGroup.getChildren().add(background);

        // modify the gameOverStats string to display only words per minute and accuracy
        String[] statsLines = gameOverStats.split("\n");
        String modifiedStats = "";
        for (String line : statsLines) {
            if (line.startsWith("Words Per Minute") || line.startsWith("Accuracy")) {
                modifiedStats += line + "\n";
            }
        }

        // text to display game over stats
        Text statsText = new Text(modifiedStats);
        statsText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        statsText.setFill(Color.WHITE);
        statsText.setTextAlignment(TextAlignment.CENTER);
        statsText.setLayoutX((gameScene.getWidth() - statsText.getLayoutBounds().getWidth()) / 2);
        statsText.setLayoutY((gameScene.getHeight() - statsText.getLayoutBounds().getHeight()) / 2);
        popupGroup.getChildren().add(statsText);

        // text for user to return to main menu
        Text returnText = new Text("Press Esc to return to main menu");
        returnText.setFont(Font.font("Verdana", 16));
        returnText.setFill(Color.WHITE);
        returnText.setTextAlignment(TextAlignment.CENTER);
        returnText.setLayoutX((gameScene.getWidth() - returnText.getLayoutBounds().getWidth()) / 2);
        returnText.setLayoutY(statsText.getLayoutY() + statsText.getLayoutBounds().getHeight() + 20);
        popupGroup.getChildren().add(returnText);

        // adds the popup group to the game scene
        ((Group) gameScene.getRoot()).getChildren().add(popupGroup);
    }


}