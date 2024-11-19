import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class QuizServer {

    private InetAddress ip = InetAddress.getByName("127.0.0.1");
    private int portNum = 55555;


    public QuizServer() throws IOException {

        ServerSocket serverSocket = new ServerSocket(portNum);
        Socket clientSocket = serverSocket.accept();

        handleClient(clientSocket);

        QuestionManger questionManger = new QuestionManger();
        questionManger questionBank = questionManger.getQuestion;

    }

    // Metod som använder en Thread lambda expression för att hantera flera klienter samtidigt
    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try {
                PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String clientInput;
                while ((clientInput = serverIn.readLine()) != null) {
                    System.out.println("Från klient: " + clientInput);

                    serverOut.println(clientInput);
                }

                serverIn.close();
                serverOut.close();
                clientSocket.close();
                System.out.println("Klienten kopplade ifrån.");


            } catch (IOException e) {
                System.out.println("Klient fel: " + e.getMessage());
            }
        }).start();

    }

    public static void main(String[] args) throws IOException {

       new QuizServer();

    }
}
