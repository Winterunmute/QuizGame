import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
    }

    private void addPlayers(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Hur många antal spelare? (2 eller 4) ");

        boolean validInput = false;
        while (!validInput) {
            String input = in.readLine(); // Läser in input av klienten genom BufferedReader
            try {
                totalPlayers = Integer.parseInt(input);
                if (totalPlayers == 2 || totalPlayers == 4) {
                    validInput = true;
                } else {
                    out.println("Ogilt antal spelare.");
                }
            } catch (NumberFormatException e) {
                out.println("Fel, försök igen.");
            }
        }

        // Samla in namn för varje spelare
        for (int i = 1; i <= totalPlayers; i++) {
            out.println("Ange namn för spelare " + i + ":");
            String playerName = in.readLine(); // Läs spelarens namn
            addPlayer(playerName); // Lägg till spelare
        }

        // Bekräfta alla spelare
        out.println("Alla spelare har registrerats:");
        for (Player player : players) {
            out.println("- " + player.getPlayerName());
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
    public void startGame(PrintWriter out, BufferedReader in) throws IOException {
        addPlayers(out, in);
        chooseCategory();
        sendQuestion();
    }

    public static void main(String[] args) {
        GameSession gameSession = new GameSession();

    }
}
