package quizgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private ArrayList<Player> players = new ArrayList<>(); // Lista för att lagra spelarna i spelet
    private QuestionManager questionManager; // Hanterar frågor från en extern fil
    private List<Question> questionBank; // Lista över frågor från den valda kategorin
    private int totalPlayers = 0;
    private int totalRounds = 3;
    private String chosenCategory; // Håller koll på den valda kategorin

    // Konstruktor som initialiserar QuestionManager
    public GameSession() {
        try {
            questionManager = new QuestionManager("src/quizgame/questions.properties");
        } catch (IOException e) {
            System.out.println("Kunde inte ladda frågorna: " + e.getMessage());
        }
    }

    // Metod som startar spelet genom att anropa addPlayers, chooseCategory och starta spelet
    public void startGame(PrintWriter out, BufferedReader in) throws IOException {
        addPlayers(out, in);
        chooseCategory(out, in);
        chooseRounds(out, in);
        playRounds(out, in);
        displayFinalResults(out);
    }

    // Metod för att lägga till spelare till spelet
    private void addPlayers(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Ange antal spelare (1-4):");
        String numPlayersStr = in.readLine();
        try {
            totalPlayers = Integer.parseInt(numPlayersStr);
            if (totalPlayers < 1 || totalPlayers > 4) {
                out.println("Ogiltigt antal spelare. Standardvärde är 1 spelare.");
                totalPlayers = 1;
            }
        } catch (NumberFormatException e) {
            out.println("Ogiltig inmatning. Standardvärde är 1 spelare.");
            totalPlayers = 1;
        }

        out.println("Ange namn för spelarna:");
        for (int i = 1; i <= totalPlayers; i++) {
            out.println("Spelare " + i + ", ange ditt namn:");
            String playerName = in.readLine();
            players.add(new Player(playerName)); // Skapar och lägger till en ny spelare i listan
        }

        // Bekräfta att spelarna har registrerats
        out.println("Alla spelare har registrerats:");
        for (Player player : players) {
            out.println("- " + player.getPlayerName());
        }
    }

    // Metod som låter spelarna välja kategori
    private void chooseCategory(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Välj en kategori:");
        out.println("1. Geografi");
        out.println("2. Historia");
        out.println("3. Sport");
        out.println("4. Kemi");

        String categoryChoice = in.readLine();

        switch (categoryChoice) {
            case "1":
                chosenCategory = "Geografi";
                break;
            case "2":
                chosenCategory = "Historia";
                break;
            case "3":
                chosenCategory = "Sport";
                break;
            case "4":
                chosenCategory = "Kemi";
                break;
            default:
                chosenCategory = "Geografi"; // Standardkategori
                out.println("Ogiltigt val. Standardkategori: Geografi.");
                break;
        }

        out.println("Vald kategori: " + chosenCategory); // Bekräftar valet
    }

    // Metod för att låta spelarna välja antal ronder
    private void chooseRounds(PrintWriter out, BufferedReader in) throws IOException {
        out.println("Ange antal ronder (1-5):");
        String roundsStr = in.readLine();
        try {
            totalRounds = Integer.parseInt(roundsStr);
            if (totalRounds < 1 || totalRounds > 5) {
                out.println("Ogiltigt antal ronder. Standardvärde är 3 ronder.");
                totalRounds = 3;
            }
        } catch (NumberFormatException e) {
            out.println("Ogiltig inmatning. Standardvärde är 3 ronder.");
            totalRounds = 3;
        }
        out.println("Totalt antal ronder: " + totalRounds);
    }

    // Metod som spelar spelet genom att hantera ronder och frågor
    private void playRounds(PrintWriter out, BufferedReader in) throws IOException {
        // Hämta frågor från den valda kategorin
        questionBank = questionManager.getQuestionsByCategory(chosenCategory);
        if (questionBank == null || questionBank.isEmpty()) {
            out.println("Inga frågor tillgängliga i kategorin: " + chosenCategory);
            return; // Avslutar om det inte finns några frågor
        }

        // Blanda frågorna för slumpmässighet
        java.util.Collections.shuffle(questionBank);

        // Kontrollera att det finns tillräckligt med frågor
        int maxQuestions = totalRounds * totalPlayers;
        if (questionBank.size() < maxQuestions) {
            out.println("Inte tillräckligt med frågor i kategorin. Minskar antal ronder.");
            totalRounds = questionBank.size() / totalPlayers;
            out.println("Nytt antal ronder: " + totalRounds);
        }

        int questionIndex = 0;

        // Spela varje runda
        for (int round = 1; round <= totalRounds; round++) {
            out.println("----- Runda " + round + " -----");

            // Varje spelare får en fråga per runda
            for (Player player : players) {
                out.println(player.getPlayerName() + ", det är din tur!");

                // Hämta nästa fråga
                if (questionIndex >= questionBank.size()) {
                    out.println("Inga fler frågor tillgängliga.");
                    break;
                }
                Question question = questionBank.get(questionIndex++);
                out.println("Fråga: " + question.getQuestion());
                String[] options = question.getOptions();

                // Skicka alternativen till klienten
                for (int i = 0; i < options.length; i++) {
                    out.println((i + 1) + ". " + options[i]);
                }

                // Be spelaren att svara
                out.println("Ditt svar (1-" + options.length + "):");
                String clientResponse = in.readLine();

                try {
                    int answerIndex = Integer.parseInt(clientResponse);
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
    }

    // Metod för att visa slutresultat till alla spelare
    private void displayFinalResults(PrintWriter out) {
        out.println("----- Spelet är slut! Slutresultat: -----");
        // Sortera spelare efter poäng i fallande ordning
        players.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        for (Player player : players) {
            out.println(player.getPlayerName() + " fick totalt " + player.getScore() + " poäng!");
        }

        // Tillkännage vinnaren/vinnarna
        int highestScore = players.get(0).getScore();
        List<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.getScore() == highestScore) {
                winners.add(player);
            }
        }

        if (winners.size() == 1) {
            out.println("Grattis " + winners.get(0).getPlayerName() + "! Du är vinnaren!");
        } else {
            out.println("Det är oavgjort mellan:");
            for (Player winner : winners) {
                out.println("- " + winner.getPlayerName());
            }
            out.println("Grattis till alla vinnare!");
        }
    }

    // Getter- och setter-metoder
    public void setChosenCategory(String category) {
        this.chosenCategory = category;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public String getChosenCategory() {
        return chosenCategory;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public String getPlayerNames() {
        StringBuilder names = new StringBuilder();
        for (Player player : players) { // lista för spelare
            names.append(player.getPlayerName()).append("\n");
        }
        return names.toString();
    }
}
