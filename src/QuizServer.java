import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    private static final int PORT = 45555;
    private static final int MAX_PLAYERS_PER_GAME = 2;
    private final GameLobby gameLobby;

    public QuizServer() {
        gameLobby = new GameLobby(MAX_PLAYERS_PER_GAME);

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                System.out.println("Väntar på en klient...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Klient ansluten från: " + clientSocket.getInetAddress());

                // Starta en ny tråd för att hantera klienten
                ClientHandler clientHandler = new ClientHandler(clientSocket, gameLobby);
                executorService.submit(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new QuizServer();
    }
}
