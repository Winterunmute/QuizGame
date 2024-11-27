import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class QuizServer {
    private static final int PORT = 45555;
    private static ClientHandler waitingClient = null;

    public QuizServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Ny klient ansluten: " + clientSocket.getInetAddress());

                ClientHandler clientHandler;

                synchronized (QuizServer.class) {
                    if (waitingClient == null) {
                        // No waiting client, set this client as first player
                        clientHandler = new ClientHandler(clientSocket, true);
                        waitingClient = clientHandler;
                        clientHandler.sendMessage("Väntar på att en annan spelare ska ansluta...");
                    } else {
                        // Pair with waiting client, set this as second player
                        clientHandler = new ClientHandler(clientSocket, false);
                        waitingClient.setOpponent(clientHandler);
                        clientHandler.setOpponent(waitingClient);
                        new Thread(waitingClient).start();
                        new Thread(clientHandler).start();
                        waitingClient = null;
                        continue; // Both threads started, continue to next loop
                    }
                }

                // For first player, their thread will wait until paired
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new QuizServer();
    }

    // Innre klass för att hantera klientkommunikation
    public static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ClientHandler opponent;
        private String playerName;
        private int score;
        private boolean isFirstPlayer;
        private String chosenCategory;
        private boolean hasFinishedRound;

        public ClientHandler(Socket clientSocket, boolean isFirstPlayer) {
            this.clientSocket = clientSocket;
            this.isFirstPlayer = isFirstPlayer;
            this.score = 0;
        }

        public void setOpponent(ClientHandler opponent) {
            this.opponent = opponent;
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        @Override
        public void run() {
            try (
                    BufferedReader inLocal = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter outLocal = new PrintWriter(clientSocket.getOutputStream(), true)) {
                this.in = inLocal;
                this.out = outLocal;

                // Initiera frågehanteraren
                QuestionManager questionManager = new QuestionManager("questions.properties");

                // Fråga efter spelarnamn
                out.println("Ange ditt namn:");
                playerName = in.readLine();

                if (isFirstPlayer) {
                    out.println("Väntar på att en annan spelare ska ansluta...");
                    // Vänta tills en motståndare har anslutit
                    while (opponent == null) {
                        Thread.sleep(100);
                    }
                }

                // Kategori-val för första spelaren
                if (isFirstPlayer) {
                    // Visa tillgängliga kategorier
                    List<String> categories = questionManager.getAllQuestions()
                            .stream()
                            .map(Question::getCategory)
                            .distinct()
                            .toList();
                    out.println("Välj kategori: " + String.join(", ", categories));
                    chosenCategory = in.readLine().trim();
                    opponent.chosenCategory = chosenCategory;
                    opponent.sendMessage("Spelare " + playerName + " valde kategori: " + chosenCategory);
                } else {
                    out.println("Väntar på att " + opponent.playerName + " ska välja kategori...");
                    while (chosenCategory == null) {
                        Thread.sleep(100);
                    }
                }

                // Spela rundor
                int totalRounds = Configuration.getTotalRounds();
                for (int round = 1; round <= totalRounds; round++) {
                    playRound(round, questionManager);
                }

                // Visa slutresultat
                showFinalResults();

            } catch (IOException | InterruptedException e) {
                System.err.println("Fel vid hantering av klient: " + e.getMessage());
            }
        }

        private synchronized void playRound(int round, QuestionManager questionManager)
                throws IOException, InterruptedException {
            out.println("Runda " + round);

            // Hämta frågor för denna runda
            List<Question> questions = questionManager.getQuestionsByCategory(chosenCategory);
            Collections.shuffle(questions);
            List<Question> roundQuestions = questions.subList(0, Math.min(3, questions.size()));

            if (isFirstPlayer) {
                // First player's turn
                out.println("Din tur att spela!");
                opponent.sendMessage("Väntar på att " + playerName + " ska spela klart...");
                askQuestions(roundQuestions);
                hasFinishedRound = true;

                // Signal opponent it's their turn
                synchronized (opponent) {
                    opponent.notify();
                }

                // Wait for opponent to finish
                synchronized (this) {
                    while (!opponent.hasFinishedRound) {
                        wait();
                    }
                }
            } else {
                // Second player waits for their turn
                synchronized (this) {
                    while (!opponent.hasFinishedRound) {
                        wait();
                    }
                }

                out.println("Din tur att spela!");
                askQuestions(roundQuestions);
                hasFinishedRound = true;

                // Signal first player we're done
                synchronized (opponent) {
                    opponent.notify();
                }
            }

            // Show round results only after both players have finished
            showRoundResults(round);

            // Reset flags for next round
            hasFinishedRound = false;
            opponent.hasFinishedRound = false;
        }

        private void askQuestions(List<Question> questions) throws IOException {
            for (Question question : questions) {
                out.println("Fråga: " + question.getQuestion());
                out.println("Alternativ: " + String.join(", ", question.getOptions()));

                try {
                    String answer = in.readLine().trim();
                    int answerNum = Integer.parseInt(answer);
                    if (answerNum == question.getCorrectAnswer()) {
                        score++;
                        out.println("Rätt svar!");
                    } else {
                        out.println("Fel svar! Rätt svar var: " +
                                question.getOptions()[question.getCorrectAnswer() - 1]);
                    }
                } catch (NumberFormatException e) {
                    out.println("Ogiltigt svar, hoppar över frågan.");
                }
            }
        }

        private void showRoundResults(int round) {
            String scoreMessage = String.format("Poängställning efter runda %d:\n%s: %d poäng\n%s: %d poäng",
                    round, playerName, score, opponent.playerName, opponent.score);
            sendMessage(scoreMessage);
            opponent.sendMessage(scoreMessage);
        }

        private void showFinalResults() {
            String finalMessage = "SLUTRESULTAT:\n" +
                    playerName + ": " + score + " poäng\n" +
                    opponent.playerName + ": " + opponent.score + " poäng\n" +
                    "Vinnare: " + (score > opponent.score ? playerName
                            : score < opponent.score ? opponent.playerName : "Oavgjort!");
            sendMessage(finalMessage);
            opponent.sendMessage(finalMessage);
        }
    }
}