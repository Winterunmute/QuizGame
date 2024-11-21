package quizgame;

public class Player {
    private String playerName;
    private int score;

    public Player(String playerName) {
        this.playerName = playerName;
        this.score = 0;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }
}
