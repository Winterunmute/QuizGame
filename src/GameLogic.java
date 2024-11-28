import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

// Klass som hanterar spellogiken för en pågående spelomgång mellan två spelare
public class GameLogic {
    // Referens till den aktiva spelaren och dess motståndare
    private ClientHandler player;
    private ClientHandler opponent;

    // volatile för variabler som delas mellan trådar
    // Delad information mellan spelarna
    private volatile String chosenCategory;
    private List<Question> gameQuestions;
    private int score;

    // Flaggor för att synkronisera spelarnas turer
    private volatile boolean firstPlayerDone = false;
    private volatile boolean secondPlayerDone = false;
    private int totalScore = 0;
    private volatile boolean categoryChosen = false;

    // Konstruktor som sätter upp spelet mellan två spelare
    public GameLogic(ClientHandler player, ClientHandler opponent) {
        this.player = player;
        this.opponent = opponent;
    }

    // Huvudmetod som kör hela spelomgången
    public void playGame(PrintWriter out, BufferedReader in) throws Exception {
        int questionsPerRound = Configuration.getQuestionsPerRound();
        int totalRounds = Configuration.getTotalRounds();
        totalScore = 0;

        // Kör alla rundor i spelet
        for (int round = 1; round <= totalRounds; round++) {
            score = 0;
            // Avgör vem som ska välja kategori denna runda
            boolean myTurnToChooseCategory = (round == 1) ? player.isFirstPlayer()
                    : ((round % 2 == 1) ? player.isFirstPlayer() : !player.isFirstPlayer());

            handleCategorySelection(myTurnToChooseCategory, round, out, in);
            handleQuestionRound(myTurnToChooseCategory, round, questionsPerRound, out, in);
            showRoundResults(round, out);
            resetRoundState(round < totalRounds);
        }

        showGameResults(out);
    }

    // Hanterar processen där en kategori väljs för rundan
    private void handleCategorySelection(boolean myTurnToChooseCategory, int round, PrintWriter out, BufferedReader in)
            throws Exception {
        if (myTurnToChooseCategory) {
            // Om det är denna spelares tur att välja kategori
            QuestionManager questionManager = new QuestionManager("questions.properties");
            List<String> categories = questionManager.getAllCategories();
            out.println("Runda " + round + ": Välj kategori: " + String.join(", ", categories));
            chosenCategory = in.readLine();

            // Synkronisera vald kategori med motståndaren
            opponent.getGameLogic().chosenCategory = chosenCategory;

            // Hämta och blanda frågorna för kategorin
            gameQuestions = questionManager.getQuestionsByCategory(chosenCategory);
            Collections.shuffle(gameQuestions);
            opponent.getGameLogic().gameQuestions = gameQuestions;

            opponent.getGameLogic().categoryChosen = true;
        } else {
            // Om det är motståndarens tur att välja kategori
            out.println("Runda " + round + ": Väntar på att " + opponent.getPlayerName() + " ska välja kategori...");
            while (!categoryChosen) {
                Thread.sleep(100);
            }
            out.println(opponent.getPlayerName() + " valde kategorin: " + chosenCategory);
        }
    }

    // Hanterar en spelrunda med frågor
    private void handleQuestionRound(boolean myTurnToChooseCategory, int round, int questionsPerRound,
            PrintWriter out, BufferedReader in) throws Exception {
        if (myTurnToChooseCategory) {
            // Första spelaren svarar på frågorna
            out.println("Din tur! Svara på frågorna.");
            playQuestionsForRound(round, questionsPerRound, out, in);
            opponent.getGameLogic().firstPlayerDone = true;

            // Vänta på att motståndaren ska bli klar
            out.println("Väntar på att " + opponent.getPlayerName() + " ska spela klart sin tur...");
            while (!opponent.getGameLogic().secondPlayerDone) {
                Thread.sleep(100);
            }
        } else {
            // Andra spelaren väntar på sin tur
            out.println("Väntar på att " + opponent.getPlayerName() + " ska spela klart sin tur...");
            while (!firstPlayerDone) {
                Thread.sleep(100);
            }

            // Andra spelaren svarar på frågorna
            out.println("Din tur! Svara på frågorna.");
            playQuestionsForRound(round, questionsPerRound, out, in);
            secondPlayerDone = true;
        }
    }

    // Kör frågorna för en spelrunda
    private void playQuestionsForRound(int round, int questionsPerRound, PrintWriter out, BufferedReader in)
            throws IOException {
        for (int i = 0; i < questionsPerRound; i++) {
            Question question = gameQuestions.get((round - 1) * questionsPerRound + i);
            out.println("Fråga: " + question.getQuestion());
            out.println("Välj ett alternativ:");

            // Visa svarsalternativ
            String[] options = question.getOptions();
            for (int j = 0; j < options.length; j++) {
                out.println((j + 1) + ". " + options[j]);
            }

            // Hantera spelarens svar
            String answer = in.readLine();
            if (Integer.parseInt(answer) == question.getCorrectAnswer()) {
                score++;
                out.println("Rätt!");
            } else {
                out.println("Fel!");
            }
        }
    }

    // Visar resultatet för den aktuella rundan
    private void showRoundResults(int round, PrintWriter out) {
        String roundResult = String.format("Runda %d resultat:\n%s: %d poäng\n%s: %d poäng",
                round, player.getPlayerName(), score, opponent.getPlayerName(), opponent.getGameLogic().score);
        out.println(roundResult);
        totalScore += score;
    }

    // Återställer spelstatus mellan rundorna
    private void resetRoundState(boolean hasNextRound) throws InterruptedException {
        if (hasNextRound) {
            Thread.sleep(1000);
            // Återställ alla statusflaggor och poäng
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

    // Visar slutresultatet för hela spelet
    private void showGameResults(PrintWriter out) {
        if (totalScore > opponent.getGameLogic().totalScore) {
            out.println("Grattis! Du vann spelet!");
        } else if (totalScore < opponent.getGameLogic().totalScore) {
            out.println("Tyvärr, " + opponent.getPlayerName() + " vann spelet!");
        } else {
            out.println("Det blev oavgjort!");
        }
    }

    // Hämtar aktuell poäng
    public int getScore() {
        return score;
    }

    // Hämtar total poäng
    public int getTotalScore() {
        return totalScore;
    }
}