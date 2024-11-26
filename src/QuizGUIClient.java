import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class QuizGUIClient extends AppGUI {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;

    public QuizGUIClient() {
        super(); // Initierar GUI-komponenterna

        // Anslut till servern
        connectToServer();

        // Be användaren att ange sitt namn
        playerName = JOptionPane.showInputDialog(this, "Ange ditt namn");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Anonym";
        }
        out.println(playerName);

        // Uppdatera spelaretiketten
        setPlayerName(playerName);

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
        enableAnswerButtons(false);
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

            if (message.equals("WAIT")) {
                // Det är inte spelarens tur
                updateQuestion("Väntar på din tur...");
                enableAnswerButtons(false);

            } else if (message.equals("YOUR_TURN")) {
                // Det är spelarens tur
                enableAnswerButtons(true);

            } else if (message.startsWith("Fråga:")) {
                String questionText = message.substring(6);
                updateQuestion(questionText);

            } else if (message.startsWith("Alternativ:")) {
                String optionsString = message.substring(10);
                String[] options = optionsString.split("\\|");
                updateOptions(options);

            } else if (message.startsWith("Rätt svar!") || message.startsWith("Fel svar!")) {
                // Visa feedback och hantera färgmarkering av knappar
                String[] parts = message.split("\\|");
                String feedback = parts[0];
                int selectedIndex = Integer.parseInt(parts[1].split(":")[1]) - 1; // Indexering från 0
                int correctIndex = Integer.parseInt(parts[2].split(":")[1]) - 1;

                showFeedback(feedback);

                if (feedback.startsWith("Rätt svar!")) {
                    highlightCorrectAnswer(selectedIndex);
                } else {
                    highlightWrongAnswer(selectedIndex, correctIndex);
                }

                // Återställ knapparna efter en kort fördröjning
                Timer timer = new Timer(1000, e -> resetAnswerButtons());
                timer.setRepeats(false);
                timer.start();

            } else if (message.equals("GAME_OVER")) {
                // Spelet är över
                // Slutresultaten kommer att tas emot via "RESULTS:"-meddelandet

            } else if (message.startsWith("RESULTS:")) {
                // Hantera slutresultatet
                String resultsString = message.substring(8);
                String[] playerResults = resultsString.split("\\|");
                StringBuilder resultsMessage = new StringBuilder("<html><h1>Spelet är slut! Slutresultat:</h1><ul>");
                for (String playerResult : playerResults) {
                    if (!playerResult.isEmpty()) {
                        String[] partsResult = playerResult.split(":");
                        resultsMessage.append("<li>").append(partsResult[0])
                                .append(": ").append(partsResult[1]).append(" poäng</li>");
                    }
                }
                resultsMessage.append("</ul></html>");
                JOptionPane.showMessageDialog(this, resultsMessage.toString(), "Slutresultat", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);

            } else if (message.equals("GAME_START")) {
                // Spelet har startat
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "Question");

            } else if (message.equals("CHOOSE_CATEGORY")) {
                // Om spelaren ska välja kategori (endast värdklienten)
                String[] categories = {"Historia", "Geografi", "Sport", "Kemi"};
                String category = (String) JOptionPane.showInputDialog(this, "Välj kategori:", "Kategori",
                        JOptionPane.PLAIN_MESSAGE, null, categories, categories[0]);
                if (category != null) {
                    out.println(category);
                }

            } else if (message.equals("CHOOSE_ROUNDS")) {
                // Om spelaren ska välja antal ronder (endast värdklienten)
                String roundsStr = JOptionPane.showInputDialog(this, "Ange antal ronder (1-5):", "3");
                if (roundsStr != null) {
                    out.println(roundsStr);
                }

            }
            // Hantera andra meddelanden från servern vid behov
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizGUIClient());
    }
}
