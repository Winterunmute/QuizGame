import java.util.concurrent.ConcurrentLinkedQueue;

// MatchMaker hanterar klienter och parar ihop dem 1 och 1 i varje spel
public class MatchMaker {

    // Sätter upp en kö för anslutna klienter som väntar på att spela
    private static final ConcurrentLinkedQueue<QuizServer.ClientHandler> waitingPlayers = new ConcurrentLinkedQueue<>();

    /*
     * Använder synchronized keyword så att endast en tråd i taget kan köra denna
     * metod
     * för att undvika race conditions när spelare matchas
     */
    public static synchronized void addPlayer(QuizServer.ClientHandler player) {
        if (waitingPlayers.isEmpty()) {
            // Första spelaren anländer
            player.setIsFirstPlayer(true);
            waitingPlayers.add(player);
            // Starta tråden för första spelaren
            new Thread(player).start();
        } else {
            // Andra spelaren anländer - matcha med väntande spelare
            QuizServer.ClientHandler firstPlayer = waitingPlayers.poll();
            player.setIsFirstPlayer(false);

            // Koppla ihop spelarna
            firstPlayer.setOpponent(player);
            player.setOpponent(firstPlayer);

            // Starta tråden för andra spelaren
            new Thread(player).start();
        }
    }
}
