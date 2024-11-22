package quizgame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientHandler implements Runnable {

    private final Socket clientSocket;



    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;

    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            // Välkomna spelaren
            out.println("Välkommen till QuizServer!");

            // Skapar en instans av GameSession och startar spelet
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




}

