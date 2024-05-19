package typingGame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// represents all objects in the game

public class Sprite {
	protected Image img;
    protected double x, y;
    protected double width, height;
	
    public Sprite(String imagePath, double xPos, double yPos) {
        this.img = new Image(imagePath);
        this.x = xPos;
        this.y = yPos;
        this.setSize();
    }
	
	// method to set the image at a specific place
    public void render(GraphicsContext gc) {
        gc.drawImage(this.img, this.x, this.y);
    }
	
	// method to see the object's image
	protected void loadImage(Image img) {
		try {
			this.img = img;
			this.setSize();
		} catch(Exception e) {}
	}
	
	
	// === setters === 	
	private void setSize() {
		this.width = this.img.getWidth();
		this.height = this.img.getHeight();
	}
	
	public void setXPos(double xPos) {
        this.x = xPos;
    }
	
	public void setYPos(double yPos) {
        this.x = yPos;
    }
	
	// === getters ===
	public Image getImage() {
		return this.img;
	}
	
	public double getXPos() {
        return this.x;
    }
	
	public double getYPos() {
        return this.y;
    }
	
	public double getWidth() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height;
	}
}
