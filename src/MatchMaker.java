import java.util.concurrent.ConcurrentLinkedQueue;

// MatchMaker hanterar klienter och parar ihop dem 1 och 1 i varje spel
public class MatchMaker {

    // Sätter upp en kö för anslutna klienter som väntar på att spela (tills en annan klient ansluter)
    private static final ConcurrentLinkedQueue<ClientHandler> waitingClients = new ConcurrentLinkedQueue<>();

    /* Använder synchronized keyword så att metoden låses tills en klient har svarat på sina frågor och gjort sin tur */
    public static synchronized void addClient(ClientHandler client) {
        waitingClients.add(client);

        // Sätter upp en condition check för att se om antalet anslutna klienter överstiger eller är lika med 2
        if (waitingClients.size() >= 2) {
            // Om det stämmer plockar vi ut de 2 första klienterna från kön
            ClientHandler player1 = waitingClients.poll();
            ClientHandler player2 = waitingClients.poll();

            // Startar ett nytt spel med de två klienterna
            new Thread(() -> startGameSession(player1, player2)).start();


        }
    }

    // Denna metod hanterar uppkopplingen mellan 2 klienter till ett och samma spel
    private static void startGameSession(ClientHandler player1, ClientHandler player2) {
        // Skapar upp en ny GameSession instans med spelarna
        GameSession gameSession = new GameSession();
        gameSession.setTotalPlayers(2);

        // Länkar klienterna till spelomgången
        player1.setGameSession(gameSession);
        player2.setGameSession(gameSession);

        // Para ihop klienterna med varandra
        player1.setOpponent(player2);
        player2.setOpponent(player1);

        // Starta spelet för båda klienterna
        player1.startGame();
        player2.startGame();


    }


}
