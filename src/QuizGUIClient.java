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

    // Fixar till handleServerMessage för enklare hantering och läsbarhet
    private void handleServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String command = message.split(":")[0];
            switch (command) {
                case "WAIT" -> handleWaitMessage();
                case "YOUR_TURN" -> handleYourTurnMessage();
                case "Fråga" -> handleQuestionMessage(message);
                case "Alternativ" -> handleOptionsMessage(message);
                case "Rätt svar!", "Fel svar!" -> handleFeedbackMessage(message);
                case "GAME_OVER" -> handleGameOverMessage();
                case "RESULTS" -> handleResultsMessage(message);
                case "GAME_START" -> handleGameStartMessage();
                case "CHOOSE_CATEGORY" -> handleChooseCategoryMessage();
                case "CHOOSE_ROUNDS" -> handleChooseRoundsMessage();
                default -> System.out.println("Okänt meddelande: " + message);
            }
        });
    }

    private void handleWaitMessage() {
        updateQuestion("Väntar på din tur...");
        enableAnswerButtons(false);
    }

    private void handleYourTurnMessage() {
        enableAnswerButtons(true);
    }

    private void handleQuestionMessage(String message) {
        String questionText = message.substring(6);
        updateQuestion(questionText);
    }

    private void handleOptionsMessage(String message) {
        String optionsString = message.substring(10);
        String[] options = optionsString.split("\\|");
        updateOptions(options);
    }

    private void handleFeedbackMessage(String message) {
        String[] parts = message.split("\\|");
        String feedback = parts[0];
        int selectedIndex = Integer.parseInt(parts[1].split(":")[1]) - 1;
        int correctIndex = Integer.parseInt(parts[2].split(":")[1]) - 1;

        showFeedback(feedback);

        if (feedback.startsWith("Rätt svar!")) {
            highlightCorrectAnswer(selectedIndex);
        } else {
            highlightWrongAnswer(selectedIndex, correctIndex);
        }

        Timer timer = new Timer(1000, e -> resetAnswerButtons());
        timer.setRepeats(false);
        timer.start();
    }

    private void handleGameOverMessage() {
        // Game over logic if needed
    }

    private void handleResultsMessage(String message) {
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
    }

    private void handleGameStartMessage() {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "Question");
    }

    private void handleChooseCategoryMessage() {
        String[] categories = {"Historia", "Geografi", "Sport", "Kemi"};
        String category = (String) JOptionPane.showInputDialog(this, "Välj kategori:", "Kategori",
                JOptionPane.PLAIN_MESSAGE, null, categories, categories[0]);
        if (category != null) {
            out.println(category);
        }
    }

    private void handleChooseRoundsMessage() {
        String roundsStr = JOptionPane.showInputDialog(this, "Ange antal ronder (1-5):", "3");
        if (roundsStr != null) {
            out.println(roundsStr);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizGUIClient());
    }
}
