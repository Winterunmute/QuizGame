import java.util.ArrayList;
import java.util.Scanner;

public class GameSession {

    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Player> players = new ArrayList<>();

    private String chosenCategory;
    private int totalPlayers = 0;

    public GameSession () {

        addPlayers();
        // Loggar namnen för att testa om det funkade rätt
        for (Player player : players) {
            System.out.println(player.getPlayerName());
        }
    }

    private void addPlayers() {

        System.out.println("Hur många antal spelare? ");
        String input = scanner.next();

        totalPlayers = Integer.parseInt(input);

        if (totalPlayers == 2) {
            addPlayer("1");
            addPlayer("2");
        } else if (totalPlayers == 4) {
            addPlayer("1");
            addPlayer("2");
            addPlayer("3");
            addPlayer("4");
        }

    }

    private void addPlayer( String playerNum) {
        System.out.println("Ange namn för spelare " + playerNum);
        String playerName = scanner.next();

        Player player = new Player(playerName);
        this.players.add(player);

    }

    public void chooseCategory() {

    }

    public static void main(String[] args) {
        new GameSession();
    }
}
