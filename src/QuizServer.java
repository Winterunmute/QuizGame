import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class QuizServer {
    private static final int PORT = 45555;

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
