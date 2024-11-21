package quizgame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class QuizServer {
    private static final int PORT = 45555;

    public QuizServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                System.out.println("Väntar på en klient...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Klient ansluten från: " + clientSocket.getInetAddress());

                // Starta en ny tråd för att hantera klienten
                // Lambda thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    // Metod för att hantera en klient
    private void handleClient(Socket clientSocket) {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            // Skapa en ny instans av GameSession för klienten och starta spelet
            GameSession gameSession = new GameSession();
            gameSession.startGame(out, in);

        } catch (IOException e) {
            System.err.println("Fel vid hantering av klient: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Kunde inte stänga klientanslutning: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new QuizServer();
    }
}

