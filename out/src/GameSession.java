//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class GameSession {
//
//    private ArrayList<Player> players;
//    private QuestionManager questionManager;
//    private List<Question> questionBank;
//    private int totalPlayers;
//    private int totalRounds;
//    private String chosenCategory;
//
//    private int currentQuestionIndex;
//    private int currentPlayerIndex;
//    private int currentRound;
//
//    public GameSession() {
//        try {
//            questionManager = new QuestionManager("questions.properties");
//        } catch (IOException e) {
//            System.out.println("Kunde inte ladda frågorna: " + e.getMessage());
//            e.printStackTrace();
//        }
//        players = new ArrayList<>();
//        totalPlayers = 0;
//        totalRounds = Configuration.getTotalRounds();
//        chosenCategory = "";
//        currentQuestionIndex = 0;
//        currentPlayerIndex = 0;
//        currentRound = 1;
//    }
//
//    // Metoder för att sätta spelinställningar
//    public void setTotalPlayers(int totalPlayers) {
//        this.totalPlayers = totalPlayers;
//        players = new ArrayList<>(totalPlayers);
//    }
//
//    public void addPlayer(String playerName) {
//        players.add(new Player(playerName));
//    }
//
//    public void setChosenCategory(String category) {
//        this.chosenCategory = category;
//    }
//
//    // Initialisera spelet
//    public void initializeGame() {
//        System.out.println("InitializeGame() anropad");
//
//        questionBank = questionManager.getQuestionsByCategory(chosenCategory);
//        if (questionBank == null || questionBank.isEmpty()) {
//            System.out.println("Inga frågor tillgängliga i kategorin: " + chosenCategory);
//            return;
//        }
//
//        Collections.shuffle(questionBank);
//        currentQuestionIndex = 0;
//        currentPlayerIndex = 0;
//        currentRound = 1;
//    }
//
//    // Hämta nästa fråga
//    public Question getNextQuestion() {
//        if (currentQuestionIndex >= questionBank.size() || currentRound > totalRounds) {
//            return null;
//        }
//        return questionBank.get(currentQuestionIndex++);
//    }
//
//    // Kontrollera spelarens svar
//    public boolean checkAnswer(int playerAnswer) {
//        Question currentQuestion = questionBank.get(currentQuestionIndex - 1);
//        boolean isCorrect = (playerAnswer == currentQuestion.getCorrectAnswer());
//        if (isCorrect) {
//            getCurrentPlayer().incrementScore();
//        }
//        return isCorrect;
//    }
//
//    // Gå till nästa tur
//    public void nextTurn() {
//        currentPlayerIndex++;
//        if (currentPlayerIndex >= players.size()) {
//            currentPlayerIndex = 0;
//            currentRound++;
//        }
//    }
//
//    // Kontrollera om spelet är slut
//    public boolean isGameOver() {
//        return currentRound > totalRounds;
//    }
//
//    // Hämta aktuell spelare
//    public Player getCurrentPlayer() {
//        return players.get(currentPlayerIndex);
//    }
//
//    // Hämta aktuell runda
//    public int getCurrentRound() {
//        return currentRound;
//    }
//
//    // Hämta slutresultat
//    public List<Player> getFinalResults() {
//        players.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
//        return players;
//    }
//
//    // Getter-metoder
//    public int getTotalPlayers() {
//        return totalPlayers;
//    }
//
//    public String getChosenCategory() {
//        return chosenCategory;
//    }
//
//    public List<Player> getPlayers() {
//        return players;
//    }
//
//    public Question getCurrentQuestion() {
//        if (currentQuestionIndex == 0) {
//            return null;
//        }
//        return questionBank.get(currentQuestionIndex - 1);
//    }
//}
