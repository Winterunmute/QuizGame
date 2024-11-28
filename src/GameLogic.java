import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

/**
 * Hanterar spellogiken för en spelsession mellan två spelare.
 * Ansvarar för frågehantering, poängräkning och spelflöde.
 */
public class GameLogic {
    private ClientHandler player; // Aktuell spelare
    private ClientHandler opponent; // Motståndarspelare
    private List<Question> gameQuestions; // Lista med spelets frågor
    private String chosenCategory; // Vald frågekategori
    private int score; // Spelarens poäng
    private int totalScore; // Total poäng för alla rundor
    private boolean categoryChosen; // Flag för om kategori har valts
    private boolean firstPlayerDone; // Flag för om första spelaren är klar
    private boolean secondPlayerDone; // Flag för om andra spelaren är klar

    public GameLogic(ClientHandler player, ClientHandler opponent) {
        this.player = player;
        this.opponent = opponent;
    }

    public void playGame(PrintWriter out, BufferedReader in) throws Exception {
        int questionsPerRound = Configuration.getQuestionsPerRound();
        int totalRounds = Configuration.getTotalRounds();
        totalScore = 0;

        for (int round = 1; round <= totalRounds; round++) {
            score = 0;
            boolean myTurnToChooseCategory = (round == 1) ? player.isFirstPlayer()
                    : ((round % 2 == 1) ? player.isFirstPlayer() : !player.isFirstPlayer());

            handleCategorySelection(myTurnToChooseCategory, round, out, in);
            handleQuestionRound(myTurnToChooseCategory, round, questionsPerRound, out, in);
            showRoundResults(round, out);
            resetRoundState(round < totalRounds);

        }

        showGameResults(out);
    }

    private void handleCategorySelection(boolean myTurnToChooseCategory, int round, PrintWriter out, BufferedReader in)
            throws Exception {
        if (myTurnToChooseCategory) {
            QuestionManager questionManager = new QuestionManager("questions.properties");
            List<String> categories = questionManager.getAllCategories();
            out.println("Runda " + round + ": Välj kategori: " + String.join(", ", categories));
            chosenCategory = in.readLine();
            opponent.getGameLogic().chosenCategory = chosenCategory;

            gameQuestions = questionManager.getQuestionsByCategory(chosenCategory);
            Collections.shuffle(gameQuestions);
            opponent.getGameLogic().gameQuestions = gameQuestions;

            opponent.getGameLogic().categoryChosen = true;
        } else {
            out.println("Runda " + round + ": Väntar på att " + opponent.getPlayerName() + " ska välja kategori...");
            while (!categoryChosen) {
                Thread.sleep(100);
            }
            out.println(opponent.getPlayerName() + " valde kategorin: " + chosenCategory);
        }
    }

    private void handleQuestionRound(boolean myTurnToChooseCategory, int round, int questionsPerRound,
            PrintWriter out, BufferedReader in) throws Exception {
        if (myTurnToChooseCategory) {
            out.println("Din tur! Svara på frågorna.");
            playQuestionsForRound(round, questionsPerRound, out, in);
            opponent.getGameLogic().firstPlayerDone = true;

            out.println("Väntar på att " + opponent.getPlayerName() + " ska spela klart sin tur...");
            while (!opponent.getGameLogic().secondPlayerDone) {
                Thread.sleep(100);
            }
        } else {
            out.println("Väntar på att " + opponent.getPlayerName() + " ska spela klart sin tur...");
            while (!firstPlayerDone) {
                Thread.sleep(100);
            }

            out.println("Din tur! Svara på frågorna.");

            playQuestionsForRound(round, questionsPerRound, out, in);
            secondPlayerDone = true;
        }
    }

    private void playQuestionsForRound(int round, int questionsPerRound, PrintWriter out, BufferedReader in)
            throws IOException {
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

    private void showRoundResults(int round, PrintWriter out) {
        String roundResult = String.format("Runda %d resultat:\n%s: %d poäng\n%s: %d poäng",
                round, player.getPlayerName(), score, opponent.getPlayerName(), opponent.getGameLogic().score);
        out.println(roundResult);
        totalScore += score;
    }

    private void resetRoundState(boolean hasNextRound) throws InterruptedException {
        if (hasNextRound) {
            Thread.sleep(1000);
            firstPlayerDone = false;
            secondPlayerDone = false;
            opponent.getGameLogic().firstPlayerDone = false;
            opponent.getGameLogic().secondPlayerDone = false;
            score = 0;
            opponent.getGameLogic().score = 0;
            chosenCategory = null;
            opponent.getGameLogic().chosenCategory = null;
            categoryChosen = false;
            opponent.getGameLogic().categoryChosen = false;
        }
    }

    private void showGameResults(PrintWriter out) {
        if (totalScore > opponent.getGameLogic().totalScore) {
            out.println("Grattis! Du vann spelet!");
        } else if (totalScore < opponent.getGameLogic().totalScore) {
            out.println("Tyvärr, " + opponent.getPlayerName() + " vann spelet!");
        } else {
            out.println("Det blev oavgjort!");
        }
    }

    public int getScore() {
        return score;
    }

    public int getTotalScore() {
        return totalScore;
    }
}