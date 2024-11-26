import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class QuizGUIClient extends AppGUI {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public QuizGUIClient() {
        super(); // Initierar GUI-komponenterna

        // Anslut till servern
        connectToServer();

        // Starta en tråd för att lyssna på serverns meddelanden
        new Thread(new ServerListener()).start();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 45555);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Ansluten till servern!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Kunde inte ansluta till servern: " + e.getMessage(),
                    "Anslutningsfel", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    protected void handleAnswerSelected(int answerIndex) {
        out.println(answerIndex);
        // Inaktivera knapparna tills servern svarar
        for (JButton button : answerButtons) {
            button.setEnabled(false);
        }
    }

    @Override
    protected void loadNextQuestion() {
        // Ladda nästa fråga från servern
        out.println("GET_NEXT_QUESTION");
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("Mottog meddelande från servern: " + serverMessage);
                    handleServerMessage(serverMessage);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(QuizGUIClient.this,
                        "Anslutningen till servern förlorad: " + e.getMessage(),
                        "Kommunikationsfel", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    private void handleServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("Fråga:")) {
                String questionText = message.substring(6);
                questionLabel.setText("<html>" + questionText + "</html>");
            } else if (message.startsWith("Alternativ:")) {
                String optionsString = message.substring(10);
                String[] options = optionsString.split("\\|");

                // Uppdatera svarsknapparna
                for (int i = 0; i < options.length; i++) {
                    answerButtons[i].setText(options[i]);
                    answerButtons[i].setEnabled(true);
                }
                // Inaktivera oanvända knappar om färre än 4 alternativ
                for (int i = options.length; i < answerButtons.length; i++) {
                    answerButtons[i].setText("");
                    answerButtons[i].setEnabled(false);
                }
            } else if (message.startsWith("Rätt svar!") || message.startsWith("Fel svar!")) {
                // Visa feedback
                JOptionPane.showMessageDialog(this, message);
                // Förbered för nästa fråga
                loadNextQuestion();
            } else if (message.equals("GAME_OVER")) {
                showFinalResults();
            }
            // Hantera andra meddelanden från servern vid behov
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizGUIClient());
    }
}
