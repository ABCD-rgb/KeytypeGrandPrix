package typingGame;

public class PlayerScore {
    private String playerName;
    private double typingSpeed;
    private double accuracy;

    public PlayerScore(String playerName, double typingSpeed, double accuracy) {
        this.playerName = playerName;
        this.typingSpeed = typingSpeed;
        this.accuracy = accuracy;
    }
    
    // === getters ===
	public String getPlayerName() {
		return playerName;
	}
	
	public double getTypingSpeed() {
		return typingSpeed;
	}
	
	public double getAccuracy() {
		return accuracy;
	}
    
    // === setters ===
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public void setTypingSpeed(double typingSpeed) {
		this.typingSpeed = typingSpeed;
	}
	
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public String toString() {
		return "Player: " + playerName + ", Typing Speed: " + typingSpeed + ", Accuracy: " + accuracy;
	}
}
