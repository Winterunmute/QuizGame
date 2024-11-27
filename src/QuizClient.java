import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class QuizClient {

    // Adressen till servern
    private static final InetAddress SERVER_ADDRESS;
    // Porten som servern lyssnar på
    private static final int SERVER_PORT = 45555;

    static {
        try {
            SERVER_ADDRESS = InetAddress.getByName("localhost"); // Sätter upp adressen till servern
        } catch (UnknownHostException e) {
            throw new RuntimeException("Kunde inte hitta servern", e);
        }
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)) {

            System.out.println("Ansluten till Quiz Server.");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                // Hantera olika meddelanden från servern
                if (serverMessage.startsWith("Väntar på")) {
                    System.out.println(serverMessage);
                    continue;
                }

                if (serverMessage.startsWith("Välj kategori:")) {
                    System.out.println(serverMessage);
                    String category = scanner.nextLine();
                    out.println(category);
                    continue;
                }

                if (serverMessage.startsWith("Fråga:")) {
                    System.out.println("\n" + serverMessage);
                    // Läs in och visa alternativen
                    String options = in.readLine();
                    if (options.startsWith("Alternativ:")) {
                        String[] alternativ = options.substring(11).split(",");
                        for (int i = 0; i < alternativ.length; i++) {
                            System.out.println((i + 1) + ". " + alternativ[i].trim());
                        }
                        System.out.print("\nVälj ett alternativ (1-" + alternativ.length + "): ");
                        String input = scanner.nextLine();
                        out.println(input);
                    }
                    continue;
                }

                // Visa poängställning och andra meddelanden
                if (serverMessage.contains("poäng") || serverMessage.contains("Rätt") ||
                        serverMessage.contains("Fel") || serverMessage.contains("valde kategori")) {
                    System.out.println(serverMessage);
                    continue;
                }

                // Hantera namn-input
                if (serverMessage.equals("Ange ditt namn:")) {
                    System.out.println(serverMessage);
                    String name = scanner.nextLine();
                    out.println(name);
                    continue;
                }

                // Standardhantering av övriga meddelanden
                System.out.println(serverMessage);
            }

        } catch (IOException e) {
            System.err.println("Fel vid anslutning till servern: " + e.getMessage());
        }
    }
}