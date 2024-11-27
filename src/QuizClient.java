import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Ansluten till Quiz Server.");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("Fråga: ")) {
                    System.out.println("\n" + serverMessage);

                    // Läs och visa alternativ
                    serverMessage = in.readLine(); // "Välj ett alternativ:"
                    System.out.println(serverMessage);
                    for (int i = 0; i < 4; i++) {
                        String option = in.readLine();
                        System.out.println(option);
                    }
                    System.out.print("Din input (1-4): ");
                    String input = console.readLine();
                    out.println(input);
                } else {
                    System.out.println("Server: " + serverMessage);

                    if (serverMessage.contains("Ange ditt namn") ||
                            serverMessage.contains("Välj kategori")) {
                        System.out.print("Din input: ");
                        String input = console.readLine();
                        out.println(input);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Kunde inte ansluta till servern: " + e.getMessage());
        }
    }
}