/**
 * Klass som representerar en runda i quizspelet.
 */
class Round {
    private int roundNumber;
    private String player1Name;
    private int player1Score;
    private String player2Name;
    private int player2Score;

    public Round(int roundNumber, String player1Name, int player1Score, String player2Name, int player2Score) {
        this.roundNumber = roundNumber;
        this.player1Name = player1Name;
        this.player1Score = player1Score;
        this.player2Name = player2Name;
        this.player2Score = player2Score;
    }

    // Getters
    public int getRoundNumber() { return roundNumber; }
    public String getPlayer1Name() { return player1Name; }
    public int getPlayer1Score() { return player1Score; }
    public String getPlayer2Name() { return player2Name; }
    public int getPlayer2Score() { return player2Score; }
}