import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class QuizServer {
    private static final int PORT = 45555;
    private final List<Socket> waitingClients = new ArrayList<>(); // Lista för att hantera väntande klienter

    public QuizServer() throws IOException {

        // Starta servern
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                System.out.println("Väntar på en klient...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Klient ansluten från: " + clientSocket.getInetAddress());

               handleNewClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    private void handleNewClient(Socket clientSocket) {
        synchronized (waitingClients) {
            // Kolla om det finns en väntande klient i waitingClients
            if (!waitingClients.isEmpty()) {
                // Om det finns, skapa ett spel för de två klienterna
                Socket player1 = waitingClients.remove(0);
                System.out.println("Kopplar ihop klienter för ett nytt spel..");
                GameSession gameSession = new GameSession(); // Skapar upp en ny GameSession instans
                new Thread(new ClientHandler(player1, gameSession)).start();
                new Thread(new ClientHandler(clientSocket, gameSession)).start();
            } else {
                // Annars lägg till den nya klienten i väntelistan
                System.out.println("Väntelistan är tom. Lägger till klient i kön...");
            }
        }
    }



    public static void main(String[] args) throws IOException {
        new QuizServer();
    }
}
