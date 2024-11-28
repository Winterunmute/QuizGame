import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Hanterar matchmaking mellan spelare som ansluter till servern.
 * Parar ihop spelare och initierar spelsessioner.
 */
public class MatchMaker {
    // Trådsäker kö för väntande spelare
    private static ConcurrentLinkedQueue<ClientHandler> waitingPlayers = new ConcurrentLinkedQueue<>();

    /**
     * Lägger till en ny spelare i kön och försöker para ihop med annan väntande
     * spelare.
     * Om det finns två spelare, skapas en match och spelet startas.
     *
     */
    public static synchronized void addPlayer(ClientHandler player) {
        waitingPlayers.add(player);

        // Om det finns minst två spelare som väntar, paara ihop dem
        if (waitingPlayers.size() >= 2) {
            ClientHandler player1 = waitingPlayers.poll();
            ClientHandler player2 = waitingPlayers.poll();

            // Sätt motståndare
            player1.setOpponent(player2);
            player2.setOpponent(player1);

            // Sätt vilken spelare som är först
            player1.setIsFirstPlayer(true);
            player2.setIsFirstPlayer(false);

            // Starta trådar för båda spelarna
            new Thread(player1).start();
            new Thread(player2).start();
        }
    }
}