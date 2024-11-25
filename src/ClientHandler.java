import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;

    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            // Skapar en instans av GameSession och startar spelet
            GameSession gameSession = new GameSession();

            // Ta emot spelinställningar från klienten
            String numPlayersStr = in.readLine();
            int totalPlayers = Integer.parseInt(numPlayersStr);
            gameSession.setTotalPlayers(totalPlayers);

            for (int i = 0; i < totalPlayers; i++) {
                String playerName = in.readLine();
                gameSession.addPlayer(playerName);
            }

            String category = in.readLine();
            gameSession.setChosenCategory(category);

            String totalRoundsStr = in.readLine();
            int totalRounds = Integer.parseInt(totalRoundsStr);
            gameSession.setTotalRounds(totalRounds);

            gameSession.initializeGame();
            System.out.println("Spelet har initialiserats på serversidan");

            // Spelets huvudloop
            while (!gameSession.isGameOver()) {
                Player currentPlayer = gameSession.getCurrentPlayer();
                out.println("Spelare: " + currentPlayer.getPlayerName());

                Question question = gameSession.getNextQuestion();
                if (question == null) {
                    System.out.println("Inga fler frågor tillgängliga");
                    break;
                }

                // Skicka frågan och alternativen
                out.println("Fråga:" + question.getQuestion());
                String optionsString = String.join("|", question.getOptions());
                out.println("Alternativ:" + optionsString);
                System.out.println("Skickar fråga: " + question.getQuestion());
                System.out.println("Skickar alternativ: " + optionsString);


                // Vänta på spelarens svar
                String clientResponse = in.readLine();
                System.out.println("Mottog svar från klienten: " + clientResponse);

                int answerIndex = Integer.parseInt(clientResponse);
                boolean isCorrect = gameSession.checkAnswer(answerIndex);

                if (isCorrect) {
                    out.println("Rätt svar!");
                } else {
                    out.println("Fel svar! Rätt svar är: " + question.getOptions()[question.getCorrectAnswer() - 1]);
                }

                // Gå till nästa tur
                gameSession.nextTurn();
            }

            // Skicka spelets slutmeddelande
            out.println("GAME_OVER");

        } catch (Exception e) {
            System.err.println("Fel vid hantering av klient: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Kunde inte stänga klientanslutning: " + e.getMessage());
            }
        }
    }
}
