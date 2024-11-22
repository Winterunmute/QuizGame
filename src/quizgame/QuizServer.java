package quizgame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class QuizServer {
    private static final int PORT = 45555;


    public QuizServer() throws IOException {

        // Starta servern
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                System.out.println("Väntar på en klient...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Klient ansluten från: " + clientSocket.getInetAddress());

                // Starta en ny tråd för att hantera klienten
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
            new QuizServer();
    }
}
