import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameSession {
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Player> players = new ArrayList<>();
    private QuestionManager questionManager;

    private String chosenCategory;
    private int totalPlayers = 0;

    public GameSession() {
        try {
            questionManager = new QuestionManager("src/questions.properties");
        } catch (IOException e) {
            System.err.println("Fel vid laddning av frågorna: " + e.getMessage());
            return;
        }

        addPlayers();
        chooseCategory();
        startGame();
    }

    // Lägg till spelare baserat på användarens input
    private void addPlayers() {
        System.out.println("Hur många spelare? (2 eller 4)");
        String input = scanner.next();

        totalPlayers = Integer.parseInt(input);

        if (totalPlayers == 2 || totalPlayers == 4) {
            for (int i = 1; i <= totalPlayers; i++) {
                addPlayer(String.valueOf(i));
            }
        } else {
            System.out.println("Ogiltigt antal spelare. Välj mellan 2 eller 4 spelare.");
        }
    }

    // Lägg till en spelare
    private void addPlayer(String playerNum) {
        System.out.println("Ange namn för spelare " + playerNum);
        String playerName = scanner.next();

        Player player = new Player(playerName);
        this.players.add(player);
    }

    // Välj kategori
    public void chooseCategory() {
        System.out.println("Välj kategori:");
        System.out.println("1. Geografi");
        System.out.println("2. Historia");
        String categoryChoice = scanner.nextLine();

        if ("1".equals(categoryChoice)) {
            chosenCategory = "Geografi";
        } else if ("2".equals(categoryChoice)) {
            chosenCategory = "Historia";
        } else {
            System.out.println("Ogiltigt val. Standardkategori: Geografi.");
            chosenCategory = "Geografi";
        }

        System.out.println("Vald kategori: " + chosenCategory);
    }

    // Starta spelet och börja ställa frågor
    private void startGame() {
        // Få frågor från den valda kategorin
        List<Question> questions = questionManager.getQuestionsByCategory(chosenCategory);

        // Skicka frågor till spelarna
        for (Question question : questions) {
            sendQuestionToPlayers(question);
        }

        System.out.println("Spelet är slut!");
    }

    // Skicka fråga och alternativ till alla spelare och ta emot deras svar
    private void sendQuestionToPlayers(Question question) {
        System.out.println("Fråga: " + question.getQuestion());
        String[] options = question.getOptions();

        // Visa alternativen
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }

        // Ta emot svar från spelarna
        for (Player player : players) {
            System.out.println(player.getPlayerName() + ", vad är ditt svar?");
            String answer = scanner.next();

            // Kontrollera om svaret är korrekt
            if (Integer.parseInt(answer) == question.getCorrectAnswer()) {
                System.out.println("Rätt svar!");
                player.incrementScore();
            } else {
                System.out.println("Fel svar! Rätt svar är: " + options[question.getCorrectAnswer() - 1]);
            }
        }
    }

    // Starta spelet
    public static void main(String[] args) {
        new GameSession();
    }
}
