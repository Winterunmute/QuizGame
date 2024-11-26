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

    public ClientHandler(Socket clientSocket, GameLobby gameLobby ) {
        this.clientSocket = clientSocket;
        this.gameLobby = gameLobby;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Ta emot spelarens namn
            out.println("ENTER_NAME");
            playerName = in.readLine();

            // Lägg till klienten i spelrummet
            gameLobby.addClient(this);

            synchronized (gameLobby) {
                while (!gameLobby.isGameStarted()) {
                    gameLobby.wait();
                }
            }

        } catch (IOException | InterruptedException exception) {
            System.err.println("Fel vid hantering av klient: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void handleGame() {
        try {
            GameSession gameSession = gameLobby.getGameSession();

            // Om klienten är värd, vänta på att få kategori och antal ronder från klienten
            if (isHost) {
                String category = in.readLine();
                gameSession.setChosenCategory(category);

                String roundsStr = in.readLine();
                int rounds = Integer.parseInt(roundsStr);
                gameSession.setTotalRounds(rounds);

                gameSession.initializeGame();

                // Skicka kategori och antal ronder till övriga klienter
                for (ClientHandler client : gameLobby.getClients()) {
                    if (client != this) {
                        client.sendMessage("CATEGORY:" + category);
                        client.sendMessage("ROUNDS:" + roundsStr);
                    }
                }
            } else {
                // Vänta på att få kategori och antal ronder från servern
                String categoryMessage = in.readLine();
                String roundsMessage = in.readLine();

                String category = categoryMessage.substring(9);
                String roundsStr = roundsMessage.substring(7);

                gameSession.setChosenCategory(category);
                int rounds = Integer.parseInt(roundsStr);
                gameSession.setTotalRounds(rounds);

                gameSession.initializeGame();
            }

            while (!gameSession.isGameOver()) {
                synchronized (gameSession) {
                    Player currentPlayer = gameSession.getCurrentPlayer();

                    // Skicka frågan och alternativen till alla klienter
                    if (currentPlayer.getPlayerName().equals(playerName)) {
                        out.println("YOUR_TURN");
                    } else {
                        out.println("WAIT");
                    }

                    Question question = gameSession.getCurrentRoundQuestion();
                    if (question == null) {
                        break;
                    }

                    out.println("Fråga:" + question.getQuestion());
                    String optionsString = String.join("|", question.getOptions());
                    out.println("Alternativ:" + optionsString);

                    if (currentPlayer.getPlayerName().equals(playerName)) {
                        // Vänta på spelarens svar
                        String clientResponse = in.readLine();
                        int answerIndex = Integer.parseInt(clientResponse);
                        boolean isCorrect = gameSession.checkAnswer(answerIndex, playerName);

                        if (isCorrect) {
                            out.println("Rätt svar!");
                        } else {
                            String correctAnswer = question.getOptions()[question.getCorrectAnswer() - 1];
                            out.println("Fel svar! Rätt svar är: " + correctAnswer);
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

        } catch (Exception e) {
            System.err.println("Fel under spelet: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Kunde inte stänga klientanslutning: " + e.getMessage());
            }

        }

    }

    public void startGame() {
        new Thread(() -> handleGame()).start();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

}