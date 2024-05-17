package typingGame;

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
//import javafx.scene.control.TextField;


/* This Class displays the animation inside the Game */


public class GameTimer extends AnimationTimer {
    private GraphicsContext gc;
    private Scene gameScene;
    private Stage stage;
    private long startTime;
    private Car car;
    private String textToType;
    private long gameDuration;
    private long remainingTime;
    private Text timerText;
    private String[] words;
    private int currentWordIndex;
    private int totalCharactersTyped;
    private int correctCharactersTyped;

    // constants
    public final static Image BG_IMG = new Image("images/concrete-floor.jpg", Constants.WINDOW_WIDTH, Constants.WINDOW_WIDTH, false, false, false);

    public GameTimer(Scene gameScene, GraphicsContext gc, String textToType, Stage stage) {
        this.gc = gc;
        this.gameScene = gameScene;
        this.startTime = System.nanoTime();
        this.car = new Car(20, Constants.WINDOW_HEIGHT - 293); // adjust the y-position as needed
        this.textToType = textToType;
        this.gameDuration = 15;
        this.timerText = new Text();
        this.timerText.setFont(Font.font("Verdana", 16));
        this.timerText.setFill(Color.WHITE);
        this.words = textToType.split("\\s+");
        this.currentWordIndex = 0;
        this.totalCharactersTyped = 0;
        this.correctCharactersTyped = 0;
        this.stage = stage;

        // methods ran at the start of GameTimer
        this.handleKeyPressEvent();
    }

    @Override
    public void handle(long currentNanoTime) {
    	try {
	        long elapsedTime = currentNanoTime - startTime;
	        remainingTime = gameDuration - elapsedTime / 1_000_000_000;
	        
	        // check if time is up
	        if (remainingTime <= 0) {
	            stop(); // stop the game
	            gameOverMessage(); // display game over message
	            gameScene.setOnKeyPressed(null);
	            handleGameOverKeyPress();
	            return;
	        }
	
	        // render background, car, text to type, and timer
	        this.renderBackground();
	        
	        // render road image
	        Image roadImage = new Image("images/road.png", gameScene.getWidth(), gameScene.getHeight(), false, false);
	        gc.drawImage(roadImage, 0, 0);
	        
	        this.renderCar();
	        this.renderTextToType();
	        this.renderTimer(remainingTime);
	        this.renderSpeedometer();
	        
    	} catch (Exception e) {
            e.printStackTrace();
            // handle the exception gracefully and ensure that the timer continues running
        }
    }

    private void renderBackground() {
    	gc.clearRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
        gc.setFill(Color.web("#A6C9CB"));
        gc.fillRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
    }

    private void renderCar() {
        car.render(gc);
    }

    private void renderTextToType() {
        gc.setFill(Color.web("#C7E2F5"));
        gc.fillRoundRect(50, gameScene.getHeight() - 100, gameScene.getWidth() - 100, 50, 10, 10);
        gc.setFont(new Font("Lucida Console", 20));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);

        StringBuilder displayText = new StringBuilder();
        for (int i = currentWordIndex; i < words.length; i++) {
            displayText.append(words[i]).append(" ");
        }
        gc.fillText(displayText.toString(), gameScene.getWidth() / 2, gameScene.getHeight() - 70);
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

    private void renderTimer(long remainingTime) {
        gc.setFill(Color.web("#FFCF11"));
        gc.fillRoundRect(gameScene.getWidth() - 120, 20, 100, 40, 10, 10);
        gc.setFont(new Font("Verdana", 16));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Time: " + remainingTime + "s", gameScene.getWidth() - 70, 45);
    }
    
    private void renderSpeedometer() {
        gc.setFill(Color.web("#FFCF11"));
        gc.fillRoundRect(20, 20, 300, 40, 10, 10);
        gc.setFont(new Font("Verdana", 16));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.LEFT);
        double wordsPerMinute = calculateWordsPerMinute();
        double accuracy = calculateAccuracy();
        gc.fillText(String.format("Speed: %.0f WPM | Accuracy: %.0f%%", wordsPerMinute, accuracy), 30, 45);
    }

    private void moveCar() {
        // move the car only when there are words left to type
        if (currentWordIndex < words.length) {
            // calculate the target position for the car
            double wordWidth = gameScene.getWidth() / words.length;
            double targetX = (currentWordIndex + 1) * wordWidth;

            // move the car to the target position
            car.move(targetX, (double) words.length);

            // check if the current word is fully typed
            if (words[currentWordIndex].isEmpty()) {
                currentWordIndex++; // Move to the next word
            }
        } else {
            // if all words are typed, move the car to the end of the screen
            car.moveToEndOfScreen();
        }
    }
    
    private double calculateWordsPerMinute() {
    	long elapsedTimeInNanos = System.nanoTime() - startTime;
        double elapsedTimeInSeconds = elapsedTimeInNanos / 1_000_000_000.0;
        return (double) correctCharactersTyped / 5.0 / elapsedTimeInSeconds * 60.0;
    }

    private double calculateAccuracy() {
        if (totalCharactersTyped == 0) {
            return 100.0;
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
                            typedChar = e.getText().charAt(0);
                        }
                        if (currentWord.charAt(0) == typedChar) {
                            // remove the first character from the current word
                            words[currentWordIndex] = currentWord.substring(1);
                            correctCharactersTyped++;
                            // check if the current word is completed
                            if (currentWord.isEmpty()) {
                                currentWordIndex++;
                                moveCar();
                            }
                        } else {
                            displayIncorrectKeyMessage();
                        }
                    }
                }

                if (currentWordIndex == words.length) {
                    displayRaceCompleteMessage();
                    stop();
                    gameScene.setOnKeyPressed(null);
                    handleGameOverKeyPress();
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
                    // return to the main menu
                    Game game = new Game();
                    game.setStage(stage);
                }
            }
        });

        // TODO: RANK based on player's rank (1st,2nd,3rd,etc.)
        // display game over popup with stats
        String gameOverStats = String.format("Time Elapsed: %d seconds\nWords Per Minute: %.2f", gameDuration - remainingTime, calculateWordsPerMinute());
        displayGameOverPopup(gameOverStats);
    }

    private void displayGameOverPopup(String gameOverStats) {
        // group to hold the popup elements
        Group popupGroup = new Group();

        // background element
        Rectangle background = new Rectangle(gameScene.getWidth(), gameScene.getHeight(), Color.rgb(0, 0, 0, 0.7));
        popupGroup.getChildren().add(background);

        // text to display game over stats
        Text statsText = new Text(gameOverStats);
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