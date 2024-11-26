import java.util.ArrayList;
import java.util.List;

public class GameLobby {

    private List<ClientHandler> clients;
    private GameSession gameSession;
    private int maxPlayers;
    private boolean gameStarted = false;

    public GameLobby(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.clients = new ArrayList<>();
        this.gameSession = new GameSession();
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        if (gameStarted) {
            clientHandler.sendMessage("Spelet har redan startat.");
            return;
        }

        clients.add(clientHandler);
        System.out.println("Klient tillagd: " + clientHandler.getPlayerName());

        if (clients.size() == maxPlayers) {
            gameStarted = true;
            System.out.println("Max antal spelare uppnått. Startar spelet...");
            startGame();
        }
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    private void startGame() {
        gameSession.setTotalPlayers(maxPlayers);
        for (ClientHandler client : clients) {
            String playerName = client.getPlayerName();
            gameSession.addPlayer(playerName);
            System.out.println("Spelare tillagd i GameSession: " + playerName);
        }

        // Bestäm en värd (första klienten) som väljer kategori och antal ronder
        ClientHandler host = clients.get(0);
        host.setHost(true);
        System.out.println(host.getPlayerName() + " är värd.");

        // Starta spelhanteringslogik för varje klient
        for (ClientHandler client : clients) {
            client.startGame();
        }
    }
}
