import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    private static final int PORT = 45555;
    private static final int MAX_PLAYERS_PER_GAME = 2;
    private final List<GameLobby> lobbies = new ArrayList<>();

    public QuizServer() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                System.out.println("Väntar på en klient...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Klient ansluten från: " + clientSocket.getInetAddress());

                assignClientToLobby(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    private synchronized void assignClientToLobby(Socket clientSocket) {
        GameLobby assignedLobby = null;
        // Loop igenom lobbies och leta efter en lobby som inte har startat och har plats för fler
        for (GameLobby gameLobby : lobbies) {
            if (!gameLobby.isGameStarted() && !gameLobby.isFull()) {
                assignedLobby = gameLobby;
                return;
            }
        }

        // Om ingen lobby finns, skapa en ny
        if (assignedLobby == null) {
            assignedLobby = new GameLobby(MAX_PLAYERS_PER_GAME);
            lobbies.add(assignedLobby);
        }

        ClientHandler clientHandler = new ClientHandler(clientSocket, assignedLobby);

        assignedLobby.addClient(clientHandler);
    }
    public static void main(String[] args) {
        new QuizServer();
    }
}
