import java.util.concurrent.ConcurrentLinkedQueue;

public class MatchMaker {
    private static ConcurrentLinkedQueue<ClientHandler> waitingPlayers = new ConcurrentLinkedQueue<>();

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