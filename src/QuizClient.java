import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class QuizClient {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 55555;

    public static void main(String[] args) {
        System.out.println("Försöker ansluta till servern på: " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

        try(Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Ansluten till servern!");


            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Servern säger: " + serverMessage);
            }

        } catch (IOException e) {
            System.err.println("Kunde inte ansluta till servern: " + e.getMessage());
        }


    }



}