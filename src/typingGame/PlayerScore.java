package typingGame;

public class PlayerScore implements Comparable<PlayerScore> {
    String identifier;
    double wordsPerMinute;
    double accuracy;

    public PlayerScore(String identifier, double wordsPerMinute, double accuracy) {
        this.identifier = identifier;
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
    
    public String getIdentifier() {
        return identifier;
    }

    public double getWordsPerMinute() {
        return wordsPerMinute;
    }

    public double getAccuracy() {
        return accuracy;
    }
}