public class Player {

    private String playerName;
    private int playerScore = 0;


    public Player(String playerName) {
        this.playerName = playerName;
    }

    // Vi kallar på dessa metoder i GameSession klassen som sedan skickar vidare till GUI'n för att visa på skärmen

    // Kallar på denna getter för att visa namnet för spelaren i GUI'n
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    // Kallar på denna getter för att visa poängen för spelaren i GUI'n
    public int getPlayerScore() {
        return playerScore;
    }

    // Metod för att uppdatera poängen för spelarna
    public void updateScore () {
        this.playerScore += 1;
    }
}
