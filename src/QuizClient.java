import java.io.*;
import java.net.*;
import java.util.Scanner;
//2
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
            while (true) {
                // Read the welcome or category prompt from the server
                serverMessage = in.readLine();
                System.out.println(serverMessage);

                if (serverMessage.startsWith("Tack för att du spelade!")) {
                    break; // Exit if the server sends a goodbye message
                }

                // Input category or exit
                String input = scanner.nextLine().trim();
                out.println(input); // Send input to the server

                if (input.equalsIgnoreCase("EXIT")) {
                    break; // Exit the client loop
                }

                while ((serverMessage = in.readLine()) != null) {
                    if (serverMessage.startsWith("QUESTION:")) {
                        System.out.println("\n" + serverMessage.substring(9));

                        // Display options
                        for (int i = 0; i < 4; i++) {
                            System.out.println(in.readLine());
                        }

                        // Get and send the answer
                        System.out.print("Ditt svar (1-4): ");
                        String answer = scanner.nextLine();
                        out.println(answer); // Send the answer to the server

                        // Display feedback
                        System.out.println(in.readLine());
                    } else if (serverMessage.startsWith("Spelet är slut!") || serverMessage.startsWith("Ogiltig kategori")) {
                        System.out.println(serverMessage);
                        break; // Break to allow choosing a new category
                    } else {
                        System.out.println(serverMessage);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Kunde inte ansluta till servern: " + e.getMessage());
        }
    }
}
