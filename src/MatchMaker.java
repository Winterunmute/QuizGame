import java.util.concurrent.ConcurrentLinkedQueue;

// Klass som hanterar matchning av spelare för att starta spel
public class MatchMaker {
    // Kö med väntande spelare som ska matchas ihop
    private static ConcurrentLinkedQueue<ClientHandler> waitingPlayers = new ConcurrentLinkedQueue<>();

    // Lägger till en spelare i kön och försöker matcha med annan väntande spelare
    public static synchronized void addPlayer(ClientHandler player) {
        waitingPlayers.add(player);

        // Om det finns minst två spelare som väntar, para ihop dem
        if (waitingPlayers.size() >= 2) {
            ClientHandler player1 = waitingPlayers.poll();
            ClientHandler player2 = waitingPlayers.poll();

            // Koppla ihop spelarna som motståndare
            player1.setOpponent(player2);
            player2.setOpponent(player1);

            // Bestäm vem som börjar
            player1.setIsFirstPlayer(true);
            player2.setIsFirstPlayer(false);

            // Starta speltrådar för båda spelarna
            new Thread(player1).start();
            new Thread(player2).start();
        }
    }
}