package typingGame;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

public class GameTimer extends AnimationTimer {
    private GraphicsContext gc;
    private Scene gameScene;
    private long startTime;
    private Car car;
    private String textToType;
    private long gameDuration;
    private Text timerText;
    private String[] words;
    private int currentWordIndex;

    // constants
    public final static Image BG_IMG = new Image("images/concrete-floor.jpg", Game.WINDOW_WIDTH, Game.WINDOW_WIDTH, false, false, false);

    public GameTimer(Scene gameScene, GraphicsContext gc, String textToType) {
        this.gc = gc;
        this.gameScene = gameScene;
        this.startTime = System.nanoTime();
        this.car = new Car(20, Game.WINDOW_CENTER);
        this.textToType = textToType;
        this.gameDuration = 60; // 1 minute game duration
        this.timerText = new Text();
        this.timerText.setFont(Font.font("Verdana", 16));
        this.timerText.setFill(Color.WHITE);
        this.words = textToType.split("\\s+");
        this.currentWordIndex = 0;

        // methods ran at the start of GameTimer
//        this.createTextInputField();
        this.handleKeyPressEvent();
    }

    @Override
    public void handle(long currentNanoTime) {
        long elapsedTime = currentNanoTime - startTime;
        long remainingTime = gameDuration - elapsedTime / 1_000_000_000;
        
        // Check if time is up
        if (remainingTime <= 0) {
            stop(); // Stop the game
            gameOver(); // Display game over message
            return;
        }


        // Check if time is up
        if (remainingTime <= 0) {
            stop(); // Stop the game
            return;
        }

        // Render background, car, text to type, and timer
        this.renderBackground();
        this.renderCar();
        this.renderTextToType();
        this.renderTimer(remainingTime);
        
        // Check if all words are typed and display congrats message
        if (currentWordIndex == words.length) {
            congratsMessage();
            stop(); // Stop the game
        }
        
    }

    private void renderBackground() {
    	gc.clearRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
        gc.setFill(Color.web("#A6C9CB"));
        gc.fillRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
        Image roadImage = new Image("images/road.png", gameScene.getWidth(), gameScene.getHeight(), false, false);
        gc.drawImage(roadImage, 0, 0);
    }

    private void renderCar() {
        car.render(gc); // Render the car
    }
    
    private void createTextInputField() {
        TextField textField = new TextField();
        textField.setLayoutX((gameScene.getWidth() - 400) / 2);
        textField.setLayoutY(gameScene.getHeight() - 60);
        textField.setPrefWidth(400);
        textField.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        textField.setFont(new Font("Verdana", 16));
        ((Group) gameScene.getRoot()).getChildren().add(textField);
        textField.requestFocus();
    }

    private void renderTextToType() {
        gc.setFill(Color.web("#C7E2F5"));
        gc.fillRoundRect(50, gameScene.getHeight() - 100, gameScene.getWidth() - 100, 80, 10, 10);
        gc.setFont(new Font("Verdana", 20));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);

        StringBuilder displayText = new StringBuilder();
        for (int i = currentWordIndex; i < words.length; i++) {
            displayText.append(words[i]).append(" ");
        }
        gc.fillText(displayText.toString(), gameScene.getWidth() / 2, gameScene.getHeight() - 70);
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
        gc.fillRoundRect(20, 20, 100, 40, 10, 10);
        gc.setFont(new Font("Verdana", 16));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Speed: " + car.getSpeed(), 70, 45);
    }

    private void moveCar() {
        // Move the car only when there are words left to type
        if (currentWordIndex < words.length) {
            // Calculate the target position for the car
            double wordWidth = gameScene.getWidth() / words.length;
            double targetX = (currentWordIndex + 1) * wordWidth;

            // Move the car to the target position
            car.move(targetX, (double) words.length);

            // Check if the current word is fully typed
            if (words[currentWordIndex].isEmpty()) {
                currentWordIndex++; // Move to the next word
            }
        } else {
            // If all words are typed, move the car to the end of the screen
            car.moveToEndOfScreen();
        }
    }


    private void handleKeyPressEvent() {
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                char typedChar = e.getText().charAt(0);
                String currentWord = words[currentWordIndex];
                if (e.getCode() == KeyCode.SPACE) {
                    // If space is pressed, move to the next word only if the current word is fully typed
                    if (currentWord.isEmpty()) {
                        currentWordIndex++;
                        moveCar();
                    }
                } else if (!currentWord.isEmpty() && currentWord.charAt(0) == typedChar) {
                    // Remove the first character from the current word
                    words[currentWordIndex] = currentWord.substring(1);
                    // Check if the current word is completed
                    if (currentWord.isEmpty()) {
                        currentWordIndex++;
                        moveCar(); 
                    }
                }
            }
        });
    }


    private void gameOver() {
        // Display game over message
        System.out.println("Game Over! Your time is up.");
    }

    private void congratsMessage() {
        // Display congratulations message
        System.out.println("Congratulations! You have reached the end.");
    }

}
