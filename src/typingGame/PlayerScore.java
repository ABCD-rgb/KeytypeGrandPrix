package typingGame;

public class PlayerScore implements Comparable<PlayerScore> {
	String username;
    double wordsPerMinute;
    double accuracy;

    public PlayerScore(String username, double wordsPerMinute, double accuracy) {
        this.username = username;
        this.wordsPerMinute = wordsPerMinute;
        this.accuracy = accuracy;
    }

    // implement the compareTo method to define the sorting order
    @Override
    public int compareTo(PlayerScore other) {
        if (this.wordsPerMinute != other.wordsPerMinute) {
            return Double.compare(other.wordsPerMinute, this.wordsPerMinute);
        } else {
            return Double.compare(other.accuracy, this.accuracy);
        }
    }
    
    public String getUsername() {
        return username;
    }

    public double getWordsPerMinute() {
        return wordsPerMinute;
    }

    public double getAccuracy() {
        return accuracy;
    }
}