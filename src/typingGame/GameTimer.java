package typingGame;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
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
        this.timerText.setFont(Font.font("Arial", 16));
        this.timerText.setFill(Color.WHITE);
        this.words = textToType.split("\\s+");
        this.currentWordIndex = 0;

        // methods ran at the start of GameTimer
        this.handleKeyPressEvent();
    }

    @Override
    public void handle(long currentNanoTime) {
        long elapsedTime = currentNanoTime - startTime;
        long remainingTime = gameDuration - elapsedTime / 1_000_000_000;

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
    }

    private void renderBackground() {
        gc.clearRect(0, 0, gameScene.getWidth(), gameScene.getHeight());
        gc.drawImage(BG_IMG, 0, 0, gameScene.getWidth(), gameScene.getHeight());
    }

    private void renderCar() {
        car.render(gc); // Render the car
    }

    private void renderTextToType() {
        gc.setFont(new Font("Arial", 20));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Construct the text to display by joining remaining words
        StringBuilder displayText = new StringBuilder();
        for (int i = currentWordIndex; i < words.length; i++) {
            displayText.append(words[i]).append(" ");
        }
        gc.fillText(displayText.toString(), gameScene.getWidth() / 2, 50);
    }

    private void renderTimer(long remainingTime) {
        timerText.setText("Time: " + remainingTime + "s");
        timerText.setX(gameScene.getWidth() - 100);
        timerText.setY(20);
        gc.fillText(timerText.getText(), timerText.getX(), timerText.getY());
    }

    // TODO
    private void moveCar() {
        // Calculate the position to move the car based on the completion of the current word
        double wordCompletion = (double) currentWordIndex / words.length;
        double targetX = gameScene.getWidth() * wordCompletion;

        // Calculate the distance to the target position
        double distance = Math.abs(targetX - car.getXPos());

        // Calculate the adjusted speed based on the number of words
        // TODO divided by something works
        // I just did not know how to proportionally move the car that is dynamic to word count
        double adjustedSpeed = car.getSpeed() * (words.length - currentWordIndex) /0.11111;

        // Move the car towards the target position with adjusted speed
        car.move(targetX, adjustedSpeed);

        // Check if all words are typed, and move the car to the end of the screen
        // this is just test
        if (currentWordIndex == words.length) {
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



}
