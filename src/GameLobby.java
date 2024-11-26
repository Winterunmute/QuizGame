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
        if (clients.size() == maxPlayers) {
            gameStarted = true;
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
        // Starta spelet när alla klienter har anslutit

        gameSession.setTotalPlayers(maxPlayers);
        for (ClientHandler client : clients) {
            String playerName = client.getPlayerName();
            gameSession.addPlayer(playerName);
        }

        // Bestäm en värd (första klienten) som väljer kategori och antal ronder
        ClientHandler host = clients.get(0);
        host.setHost(true);
        host.sendMessage("CHOOSE_CATEGORY");
        host.sendMessage("CHOOSE_ROUNDS");

        // När värden har valt kategori och ronder, initierar vi spelet
        // Detta kan hanteras via metoder som väntar på dessa värden
    }
}
