import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private GameLobby gameLobby;
    private String playerName;
    private boolean isHost = false;

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public ClientHandler(Socket clientSocket, GameLobby gameLobby) {
        this.clientSocket = clientSocket;
        this.gameLobby = gameLobby;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Be om spelarens namn
            out.println("ENTER_NAME");
            System.out.println("Skickade ENTER_NAME till klient.");
            playerName = in.readLine();
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Anonym";
            }
            System.out.println("Ansluten spelare: " + playerName);

            // Lägg till klienten i spelrummet
            gameLobby.addClient(this);

        } catch (IOException e) {
            System.err.println("Fel vid hantering av klient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void startGame() {
        new Thread(this::handleGame).start();
    }

    private void handleGame() {
        try {
            GameSession gameSession = gameLobby.getGameSession();

            // Om klienten är värd, skicka CHOOSE_CATEGORY och CHOOSE_ROUNDS i ordning
            if (isHost) {
                System.out.println(playerName + " är värd och väljer kategori och antal ronder.");

                // Skicka CHOOSE_CATEGORY och vänta på svar
                out.println("CHOOSE_CATEGORY");
                System.out.println("Skickade CHOOSE_CATEGORY till " + playerName);

                String category = in.readLine();
                if (category == null || category.trim().isEmpty()) {
                    category = "Geografi"; // Standardkategori om inget anges
                }
                gameSession.setChosenCategory(category);
                System.out.println("Värden valde kategori: " + category);

                // Skicka CHOOSE_ROUNDS och vänta på svar
                out.println("CHOOSE_ROUNDS");
                System.out.println("Skickade CHOOSE_ROUNDS till " + playerName);

                String roundsStr = in.readLine();
                int rounds;
                try {
                    rounds = Integer.parseInt(roundsStr);
                    if (rounds < 1 || rounds > 5) {
                        throw new NumberFormatException("Antal ronder måste vara mellan 1 och 5.");
                    }
                } catch (NumberFormatException e) {
                    rounds = 3; // Standardantal ronder om inget eller ogiltigt anges
                    System.err.println("Ogiltigt antal ronder från " + playerName + ": " + roundsStr + ". Använder standardvärdet 3.");
                }
                gameSession.setTotalRounds(rounds);
                System.out.println("Värden valde antal ronder: " + rounds);

                // Initiera spelet
                gameSession.initializeGame();
                System.out.println("Spelet har initialiserats.");

                // Skicka kategori och antal ronder till övriga klienter
                for (ClientHandler client : gameLobby.getClients()) {
                    if (client != this) {
                        client.sendMessage("CATEGORY:" + category);
                        client.sendMessage("ROUNDS:" + rounds);
                        System.out.println("Skickade CATEGORY och ROUNDS till " + client.getPlayerName());
                    }
                }

                // Meddela alla klienter att spelet startar
                for (ClientHandler client : gameLobby.getClients()) {
                    client.sendMessage("GAME_START");
                    System.out.println("Skickade GAME_START till " + client.getPlayerName());
                }
            } else {
                // För deltagare: vänta på att få kategori och antal ronder från servern
                // Detta hanteras av klienten via "CATEGORY:" och "ROUNDS:" meddelanden
                // Ingen åtgärd behövs här
                System.out.println(playerName + " är en deltagare och väntar på spelet att starta.");
            }

            while (!gameSession.isGameOver()) {
                synchronized (gameSession) {
                    Player currentPlayer = gameSession.getCurrentPlayer();

                    // Skicka "YOUR_TURN" eller "WAIT" till klienten
                    if (currentPlayer.getPlayerName().equals(playerName)) {
                        out.println("YOUR_TURN");
                        System.out.println("Skickade YOUR_TURN till " + playerName);
                    } else {
                        out.println("WAIT");
                        System.out.println("Skickade WAIT till " + playerName);
                    }

                    // Skicka frågan och alternativen till klienten
                    Question question = gameSession.getCurrentRoundQuestion();
                    if (question == null) {
                        break;
                    }

                    out.println("Fråga:" + question.getQuestion());
                    String optionsString = String.join("|", question.getOptions());
                    out.println("Alternativ:" + optionsString);
                    System.out.println("Skickade fråga till " + playerName + ": " + question.getQuestion());

                    if (currentPlayer.getPlayerName().equals(playerName)) {
                        // Vänta på spelarens svar
                        String clientResponse = in.readLine();
                        if (clientResponse == null) {
                            System.out.println("Spelaren " + playerName + " kopplade bort.");
                            break;
                        }

                        int answerIndex;
                        try {
                            answerIndex = Integer.parseInt(clientResponse);
                        } catch (NumberFormatException e) {
                            System.err.println("Ogiltigt svar från " + playerName + ": " + clientResponse);
                            out.println("Fel svar! Ogiltigt svar.|selectedIndex:0|correctIndex:" + question.getCorrectAnswer());
                            continue;
                        }

                        boolean isCorrect = gameSession.checkAnswer(answerIndex, playerName);

                        if (isCorrect) {
                            out.println("Rätt svar!|selectedIndex:" + answerIndex + "|correctIndex:" + question.getCorrectAnswer());
                            System.out.println(playerName + " svarade rätt med alternativ: " + answerIndex);
                        } else {
                            out.println("Fel svar!|selectedIndex:" + answerIndex + "|correctIndex:" + question.getCorrectAnswer());
                            String correctAnswer = question.getOptions()[question.getCorrectAnswer() - 1];
                            System.out.println(playerName + " svarade fel med alternativ: " + answerIndex + ". Rätt svar: " + correctAnswer);
                        }

                        // Gå vidare till nästa tur
                        gameSession.nextTurn();
                    }
                }
                // Vänta en kort stund innan nästa iteration
                Thread.sleep(100);
            }

            // Spelet är över
            out.println("GAME_OVER");
            System.out.println("Skickade GAME_OVER till " + playerName);

            // Skicka slutresultatet
            List<Player> finalResults = gameSession.getFinalResults();
            StringBuilder resultsMessage = new StringBuilder("RESULTS:");
            for (Player player : finalResults) {
                resultsMessage.append(player.getPlayerName())
                        .append(":")
                        .append(player.getScore())
                        .append("|");
            }
            out.println(resultsMessage.toString());
            System.out.println("Skickade RESULTS till " + playerName);
        } catch (NumberFormatException e) {
            System.err.println("Fel under spelet: For input string: \"" + e.getMessage() + "\"");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO-fel under spelet: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Tråd avbruten: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Stänger anslutning med " + playerName);
            } catch (IOException e) {
                System.err.println("Kunde inte stänga klientanslutning: " + e.getMessage());
            }
        }
    }
}
