import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameSession {

    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Player> players = new ArrayList<>();
    private QuestionManager questionManager;
    private List<Question> questionBank;

    private String chosenCategory;
    private int totalPlayers = 0;


    public GameSession () {

        try {
            questionManager = new QuestionManager("src/questions.properties");
        } catch (IOException e) {
            e.getMessage();
        }

        addPlayers();
        chooseCategory();
        startGame();
    }

    private void addPlayers() {

        System.out.println("Hur många antal spelare? ");
        String input = scanner.next();

        totalPlayers = Integer.parseInt(input);

        if (totalPlayers == 2) {
            addPlayer("1");
            addPlayer("2");
        } else if (totalPlayers == 4) {
            addPlayer("1");
            addPlayer("2");
            addPlayer("3");
            addPlayer("4");
        }

    }

    private void addPlayer( String playerNum) {
        System.out.println("Ange namn för spelare " + playerNum);
        String playerName = scanner.next();

        Player player = new Player(playerName);
        this.players.add(player);

    }

    public void chooseCategory() {
        System.out.println("Välj en kategori:");
        System.out.println("1. Geografi");
        System.out.println("2. Historia");
        System.out.println("3. Sport");


        scanner.nextLine();

        String input = "";
        boolean validChoice = false;

        while (!validChoice) {
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    chosenCategory = "Geografi";
                    validChoice = true;
                    break;
                case "2":
                    chosenCategory = "Historia";
                    validChoice = true;
                    break;
                case "3":
                    chosenCategory = "Sport";
                    validChoice = true;
                    break;
                default:
                    System.out.println("Ogiltigt val, standardkategori blir Geografi.");
                    chosenCategory = "Geografi";
                    break;
            }
        }
        System.out.println("Vald kategori: " + chosenCategory);
    }

    private void sendQuestion() {
        // Hämta frågor från den valda kategorin
        questionBank = questionManager.getQuestion(chosenCategory);

        System.out.println("Välkommen till Quiz!");
        int score = 0;

        // Skicka frågor och ta emot svar
        for (Question question : questionBank) {
            System.out.println("Fråga: " + question.getQuestion());
            String[] options = question.getOptions();
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }

            // Vänta på att spelaren skickar svar
            System.out.print("Ditt svar (1-4): ");
            String clientResponse = scanner.nextLine();  // Använd scanner för att läsa svar
            int answerIndex = Integer.parseInt(clientResponse);
            if (answerIndex == question.getCorrectAnswer()) {
                System.out.println("Rätt svar!");
                score++;
            } else {
                System.out.println("Fel svar! Rätt svar är: " + options[question.getCorrectAnswer() - 1]);
            }
        }

        // Skicka slutresultat
        System.out.println("Spelet är slut! Du fick " + score + " poäng.");
    }
    public void startGame() {
        sendQuestion();
    }

    public static void main(String[] args) {
        GameSession gameSession = new GameSession();
        gameSession.chooseCategory();


        gameSession.startGame();
    }
}
