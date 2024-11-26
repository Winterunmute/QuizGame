import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSession {

    private ArrayList<Player> players;
    private QuestionManager questionManager;
    private List<Question> questionBank;
    private int totalPlayers;
    private int totalRounds;
    private String chosenCategory;

    private int currentQuestionIndex;
    private int currentPlayerIndex;
    private int currentRound;
    private Question currentRoundQuestion;

    public GameSession() {
        try {
            questionManager = new QuestionManager("questions.properties");
        } catch (IOException e) {
            System.out.println("Kunde inte ladda frågorna: " + e.getMessage());
            e.printStackTrace();
        }
        players = new ArrayList<>();
        totalPlayers = 0;
        totalRounds = 3;
        chosenCategory = "";
        currentQuestionIndex = 0;
        currentPlayerIndex = 0;
        currentRound = 1;
    }

    // Metoder för att sätta spelinställningar
    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        players = new ArrayList<>(totalPlayers);
    }

    public void addPlayer(String playerName) {
        players.add(new Player(playerName));
    }

    public void setChosenCategory(String category) {
        this.chosenCategory = category;
    }

    public void setTotalRounds(int rounds) {
        this.totalRounds = rounds;
    }

    // Initialisera spelet
    public void initializeGame() {
        System.out.println("InitializeGame() anropad");

        questionBank = questionManager.getQuestionsByCategory(chosenCategory);
        if (questionBank == null || questionBank.isEmpty()) {
            System.out.println("Inga frågor tillgängliga i kategorin: " + chosenCategory);
            return;
        }

        Collections.shuffle(questionBank);
        currentQuestionIndex = 0;
        currentPlayerIndex = 0;
        currentRound = 1;

        // Hämta första frågan
        currentRoundQuestion = questionBank.get(currentQuestionIndex++);
    }

    public Question getCurrentRoundQuestion() {
        return currentRoundQuestion;
    }

    // Hämta nästa fråga
    public Question getNextQuestion() {
        if (currentQuestionIndex >= questionBank.size() || currentRound > totalRounds) {
            return null;
        }
        return questionBank.get(currentQuestionIndex++);
    }

    // Kontrollera spelarens svar
    public boolean checkAnswer(int playerAnswer, String playerName) {
        boolean isCorrect = (playerAnswer == currentRoundQuestion.getCorrectAnswer());
        // Uppdatera spelarens poäng
        for (Player player : players) {
            if (player.getPlayerName().equals(playerName)) {
                if (isCorrect) {
                    player.incrementScore();
                }
                break;
            }
        }
        return isCorrect;
    }

    public void nextTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
            currentRound++;
            if (currentQuestionIndex < questionBank.size()) {
                currentRoundQuestion = questionBank.get(currentQuestionIndex++);
            } else {
                currentRoundQuestion = null;
            }
        }
    }

    // Kontrollera om spelet är slut
    public boolean isGameOver() {
        return currentRound > totalRounds || currentRoundQuestion == null;
    }

    // Hämta aktuell spelare
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // Hämta aktuell runda
    public int getCurrentRound() {
        return currentRound;
    }

    // Hämta slutresultat
    public List<Player> getFinalResults() {
        players.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        return players;
    }

    // Getter-metoder
    public int getTotalPlayers() {
        return totalPlayers;
    }

    public String getChosenCategory() {
        return chosenCategory;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Question getCurrentQuestion() {
        if (currentQuestionIndex == 0) {
            return null;
        }
        return questionBank.get(currentQuestionIndex - 1);
    }
}
