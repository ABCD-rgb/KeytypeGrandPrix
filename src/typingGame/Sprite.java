package typingGame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/* This Class represents all objects in the game */

public class Sprite {
	protected Image img;
	protected double x, y;
	protected double dx, dy;
	protected double width, height;
	
	public Sprite(int xPos, int yPos) {
		this.x = xPos;
		this.y = yPos;
	}
	
	// method to set the image at a specific place
	void render(GraphicsContext gc) {
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
	
	public void setDX(double val) {
		this.dx = val;
	}
	
	public void setDY(double val) {
		this.dy = val;
	}
	
	public void setXPos(double val) {
		this.x = val;
	}
	
	public void setYPos(double val) {
		this.x = val;
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
