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
                if (serverMessage.startsWith("QUESTION:")) {
                    System.out.println("\n" + serverMessage.substring(9)); // Visa frågan

                    // Visa alternativen
                    for (int i = 0; i < 4; i++) {
                        System.out.println(in.readLine());
                    }

                    // Låt spelaren svara
                    System.out.print("Ditt svar (1-4): ");
                    String answer = scanner.nextLine();
                    out.println(answer); // Skicka svaret till servern

                    // Visa feedback
                    System.out.println(in.readLine());
                } else {
                    System.out.println(serverMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("Kunde inte ansluta till servern: " + e.getMessage());
        }
    }
}
