import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private ArrayList<Player> players = new ArrayList<>(); // Lista för att lagra spelarna i spelet
    private QuestionManager questionManager; // Hanterar frågor från en extern fil
    private List<Question> questionBank; // Lista över frågor från den valda kategorin

    private String chosenCategory; // Håller koll på den valda kategorin

    // Konstruktor som initialiserar QuestionManager
    public GameSession() {
        try {
            questionManager = new QuestionManager("src/questions.properties");
        } catch (IOException e) {
            System.out.println("Kunde inte ladda frågorna: " + e.getMessage());
        }
    }

    // Metod som startar spelet genom att anropa addPlayers, chooseCategory och sendQuestion
    public void startGame(PrintWriter out, BufferedReader in) throws IOException {
        addPlayers(out, in);
        chooseCategory(out, in);
        sendQuestion(out, in);
    }

    // Metod för att lägga till spelare till spelet
    private void addPlayers(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Ange namn för de som ska spela:");
        for (int i = 1; i <= 4; i++) {
            addPlayer(out, in, i);
        }

        // Bekräfta att spelarna har registrerats
        out.println("Alla spelare har registrerats:");
        for (Player player : players) {
            out.println("- " + player.getPlayerName());
        }
    }

    // Metod för att lägga till en spelare och begära deras namn
    private void addPlayer(PrintWriter out, BufferedReader in, int playerNum) throws IOException {
        out.println("Spelare " + playerNum + ", ange ditt namn:");
        String playerName = in.readLine();
        players.add(new Player(playerName)); // Skapar och lägger till en ny spelare i listan
    }

    // Metod som låter spelarna välja kategori
    public void chooseCategory(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Välj en kategori:");
        out.println("1. Geografi");
        out.println("2. Historia");
        out.println("3. Sport");

        String categoryChoice = in.readLine();

        // Använder en lambda switch för att välja kategori baserat på spelarens input (ser clean ut)
        switch (categoryChoice) {
            case "1" -> chosenCategory = "Geografi";
            case "2" -> chosenCategory = "Historia";
            case "3" -> chosenCategory = "Sport";
            default -> {
                chosenCategory = "Geografi"; // Standardkategori
                out.println("Ogiltigt val. Standardkategori: Geografi.");
            }
        }

        out.println("Vald kategori: " + chosenCategory); // Bekräftar valet
    }

    // Metod som skickar frågor till spelarna och tar emot deras svar
    private void sendQuestion(PrintWriter out, BufferedReader in) throws IOException {
        // Hämta frågor från den valda kategorin
        questionBank = questionManager.getQuestion(chosenCategory);
        if (questionBank == null || questionBank.isEmpty()) {
            out.println("Inga frågor tillgängliga i kategorin: " + chosenCategory);
            return; // Avslutar om det inte finns några frågor
        }

        // Itererar över varje spelare och ställer frågor
        for (Player player : players) {
            out.println("Det är " + player.getPlayerName() + "s tur!");

            // Itererar över frågorna i den valda kategorin
            for (Question question : questionBank) {
                out.println("Fråga: " + question.getQuestion());
                String[] options = question.getOptions();

                // Skicka alternativen till klienten
                for (int i = 0; i < options.length; i++) {
                    out.println((i + 1) + ". " + options[i]);
                }

                // Be spelaren att svara
                out.println("Ditt svar (1-4):");
                String clientResponse = in.readLine();

                try {
                    int answerIndex = Integer.parseInt(clientResponse);
                    if (answerIndex < 0 || answerIndex > 4) {
                        out.println("Svar utanför val 1-4!");
                        continue;
                    }
                    if (answerIndex == question.getCorrectAnswer()) {
                        out.println("Rätt svar!");
                        player.incrementScore(); // Uppdaterar spelarens poäng
                    } else {
                        out.println("Fel svar! Rätt svar är: " + options[question.getCorrectAnswer() - 1]);
                    }
                } catch (NumberFormatException e) {
                    out.println("Ogiltigt svar. Du får ingen poäng för denna fråga.");
                }
            }
        }

        // Skicka slutresultat till alla spelare
        out.println("Spelet är slut! Resultat:");
        for (Player player : players) {
            out.println(player.getPlayerName() + " fick totalt " + player.getScore() + " poäng!");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Resultat.txt", true))) {
            for (Player player : players) {
                writer.write(player.getPlayerName() + ": " + player.getScore() + "poäng");
                writer.newLine();
            }
            out.println("Resultat sparat till fil.");
        }catch (IOException e) {
            out.println("Kunde inte spara resultat: " + e.getMessage());
        }
    }
}