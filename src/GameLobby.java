import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameLobby {
    private int maxPlayers;
    private List<ClientHandler> clients;
    private GameSession gameSession;
    private boolean gameStarted;

    public GameLobby(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.clients = new ArrayList<>();
        this.gameSession = new GameSession();
        this.gameStarted = false;
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        if (gameStarted) {
            System.out.println("Lobbyn är full, letar efter en annan.");
            return;
        }

        clients.add(clientHandler);

        if (clients.size() == maxPlayers) {
            startGame();
        }
    }

    public synchronized boolean isFull() {
        return clients.size() >= maxPlayers;
    }

    public synchronized boolean isGameStarted() {
        return gameStarted;
    }


    private void startGame() {
        gameStarted = true;
        // Starta spelet när alla klienter har anslutit

        for (ClientHandler client : clients) {
            gameSession.addPlayer(client.getPlayerName());
        }


        gameSession.initializeGame();

        clients.get(0).setHost(true);
        clients.get(0).sendMessage("CHOOSE_CATEGORY");
        clients.get(0).sendMessage("CHOOSE_ROUNDS");

        for (int i = 1; i < clients.size(); i++) {
            clients.get(i).sendMessage("WAIT_FOR_GAME_TO_START");
        }


    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}
