package typingGame;

import javafx.scene.image.Image;

public class Road extends Sprite {
	public Road(String imagePath, double xPos, double yPos, double width, double height) {
        super(imagePath, xPos, yPos);
        setWidth(width);
        setHeight(height);
    }
    
    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}