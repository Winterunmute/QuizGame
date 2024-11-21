package quizgame;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class QuizClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 45555;

    private static QuizGUI quizGUI;

    public static void main(String[] args) {
        System.out.println("Försöker ansluta till servern på " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Ansluten till servern!");

            quizGUI = new QuizGUI();
            quizGUI.setVisible(true);

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage); // Visa serverns meddelanden

                // Om servern ber om input, låt spelaren svara
                if (serverMessage.contains("Ditt svar") || serverMessage.contains("ange ditt namn") || serverMessage.contains("Välj en kategori:")) {
                    String userInput = scanner.nextLine();
                    out.println(userInput); // Skicka spelarens input till servern
                }
            }
        } catch (IOException e) {
            System.err.println("Kunde inte ansluta till servern: " + e.getMessage());
        }
    }
}