package quizgame;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class QuizClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 45555;

    public static void main(String[] args) {
        System.out.println("Försöker ansluta till servern på " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Ansluten till servern!");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage); // Visa serverns meddelande

                // Kontrollera om servern väntar på inmatning
                if (serverMessage.endsWith(":") || serverMessage.endsWith("?")) {
                    // Servern ber om inmatning
                    String userInput = scanner.nextLine();
                    out.println(userInput); // Skicka inmatningen tillbaka till servern
                } else if (serverMessage.startsWith("Fråga:")) {
                    // Servern skickar en fråga
                    // Visa alternativen
                    for (int i = 0; i < 4; i++) {
                        String option = in.readLine();
                        System.out.println(option);
                    }

                    // Be användaren om svar
                    System.out.print("Ditt svar: ");
                    String answer = scanner.nextLine();
                    out.println(answer); // Skicka svaret till servern

                    // Visa feedback
                    String feedback = in.readLine();
                    System.out.println(feedback);
                }
                // Fortsätt att läsa nästa meddelande från servern
            }
        } catch (IOException e) {
            System.err.println("Kunde inte ansluta till servern: " + e.getMessage());
        }
    }
}
