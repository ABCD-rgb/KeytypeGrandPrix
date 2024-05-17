package typingGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/* Define CONSTANTS used in multiple classes here */

public class Constants {
	public static final int PORT = 8000;
	public static final String IP = "localhost";
	
    public final static int CAR_WIDTH = 55;
    public final static int CAR_HEIGHT = 30;
    public final static double CAR_SPEED = 120 / CAR_WIDTH;
    public final static String CAR_IMG = "images/car_yellow.png";
	
	public final static int WINDOW_WIDTH = 800;
	public final static int WINDOW_HEIGHT = 600;
	public final static int WINDOW_CENTER = WINDOW_WIDTH / 2;
	public final static Image BG_IMG = new Image("images/welcome_bg.png", WINDOW_WIDTH, WINDOW_WIDTH, false, false);
	public final static ImageView LOGO = new ImageView(new Image("images/logo.png"));
	public final static Image WINDOWLOGO = new Image("images/window_logo.png");
	
    // Private constructor to prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
