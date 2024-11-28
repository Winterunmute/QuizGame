import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Huvudserver för quizspelet.
 * Hanterar nya klientanslutningar och skapar ClientHandler-instanser.
 */
public class QuizServer {
    // Port som servern lyssnar på
    private static final int PORT = 45555;

    /**
     * Skapar en ny serverinstans som lyssnar efter klientanslutningar.
     * För varje ny klient skapas en ClientHandler som hanterar kommunikationen.
     */
    public QuizServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Ny klient ansluten: " + clientSocket.getInetAddress());

                // Skapa en ny ClientHandler för den anslutna klienten
                ClientHandler player = new ClientHandler(clientSocket);

                // Lägg till spelaren till MatchMaker
                MatchMaker.addPlayer(player);
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new QuizServer();
    }
}
