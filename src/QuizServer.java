import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class QuizServer {
    private static final int PORT = 45555;

    public QuizServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servern är igång på port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Ny klient ansluten: " + clientSocket.getInetAddress());

                MatchMaker.addPlayer(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Kunde inte starta servern: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new QuizServer();
    }

    // Inner class for handling client communication
    public static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ClientHandler opponent;
        private boolean isFirstPlayer;
        private String chosenCategory;
        private List<Question> gameQuestions;
        private int score = -1;
        private String playerName;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.score = -1;
        }

        public void setOpponent(ClientHandler opponent) {
            this.opponent = opponent;
        }

        public void setIsFirstPlayer(boolean isFirstPlayer) {
            this.isFirstPlayer = isFirstPlayer;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Get player name first
                out.println("Välkommen! Ange ditt namn:");
                playerName = in.readLine();

                if (isFirstPlayer) {
                    out.println("Hej " + playerName + "! Väntar på en annan spelare...");
                    while (opponent == null) {
                        Thread.sleep(500);
                    }
                    out.println("En motståndare har anslutit: " + opponent.playerName);

                    // First player chooses category
                    QuestionManager questionManager = new QuestionManager("questions.properties");
                    List<String> categories = questionManager.getAllCategories();
                    out.println("Välj kategori: " + String.join(", ", categories));
                    chosenCategory = in.readLine();
                    opponent.chosenCategory = chosenCategory;

                    // Get questions and share with opponent
                    gameQuestions = questionManager.getQuestionsByCategory(chosenCategory);
                    Collections.shuffle(gameQuestions);
                    opponent.gameQuestions = gameQuestions;
                } else {
                    out.println("Hej " + playerName + "! " + opponent.playerName + " väljer kategori...");
                    while (chosenCategory == null) {
                        Thread.sleep(500);
                    }
                    out.println(opponent.playerName + " valde kategorin: " + chosenCategory);
                }

                // Play game
                playGame();

            } catch (Exception e) {
                System.err.println("Error handling client: " + e.getMessage());
            }
        }

        private void playGame() {
            try {
                int questionsPerRound = Configuration.getQuestionsPerRound();
                int totalRounds = Configuration.getTotalRounds();

                for (int round = 1; round <= totalRounds; round++) {
                    // First player plays their questions
                    if (isFirstPlayer) {
                        out.println("Runda " + round + " börjar! Din tur!");
                        for (int i = 0; i < questionsPerRound; i++) {
                            Question question = gameQuestions.get((round - 1) * questionsPerRound + i);
                            out.println("Fråga: " + question.getQuestion());
                            out.println("Välj ett alternativ:");
                            String[] options = question.getOptions();
                            for (int j = 0; j < options.length; j++) {
                                out.println((j + 1) + ". " + options[j]);
                            }

                            String answer = in.readLine();
                            if (Integer.parseInt(answer) == question.getCorrectAnswer()) {
                                score++;
                                out.println("Rätt!");
                            } else {
                                out.println("Fel!");
                            }
                        }
                        out.println("Väntar på att " + opponent.playerName + " ska spela klart rundan...");
                        while (opponent.score == -1) {
                            Thread.sleep(100);
                        }
                    } else {
                        // Second player waits for first player
                        out.println("Väntar på att " + opponent.playerName + " ska spela klart sin tur...");
                        while (opponent.score == -1) {
                            Thread.sleep(100);
                        }

                        out.println("Din tur!");
                        for (int i = 0; i < questionsPerRound; i++) {
                            Question question = gameQuestions.get((round - 1) * questionsPerRound + i);
                            out.println("Fråga: " + question.getQuestion());
                            out.println("Välj ett alternativ:");
                            String[] options = question.getOptions();
                            for (int j = 0; j < options.length; j++) {
                                out.println((j + 1) + ". " + options[j]);
                            }

                            String answer = in.readLine();
                            if (Integer.parseInt(answer) == question.getCorrectAnswer()) {
                                score++;
                                out.println("Rätt!");
                            } else {
                                out.println("Fel!");
                            }
                        }
                    }

                    // Show round results to both players
                    String roundResult = String.format("Runda %d resultat:\n%s: %d poäng\n%s: %d poäng",
                            round, playerName, score, opponent.playerName, opponent.score);
                    out.println(roundResult);

                    // Reset scores for next round
                    if (round < totalRounds) {
                        score = -1;
                        Thread.sleep(1000); // Give time for both players to see results
                    }
                }

                // Show final results after all rounds
                if (score > opponent.score) {
                    out.println("Grattis! Du vann spelet!");
                } else if (score < opponent.score) {
                    out.println("Tyvärr, " + opponent.playerName + " vann spelet!");
                } else {
                    out.println("Det blev oavgjort!");
                }

            } catch (Exception e) {
                System.err.println("Error during game: " + e.getMessage());
            }
        }
    }
}