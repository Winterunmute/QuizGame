import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientHandler opponent;
    private boolean isFirstPlayer;
    private String playerName;
    private GameLogic gameLogic;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
        this.gameLogic = new GameLogic(this, opponent);
    }

    public void setIsFirstPlayer(boolean isFirstPlayer) {
        this.isFirstPlayer = isFirstPlayer;
    }

    public boolean isFirstPlayer() {
        return isFirstPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Välkommen! Ange ditt namn:");
            playerName = in.readLine();

            if (isFirstPlayer) {
                out.println("Hej " + playerName + "! Väntar på en annan spelare...");
                while (opponent == null || opponent.playerName == null) {
                    Thread.sleep(100);
                }
                out.println("En motståndare har anslutit: " + opponent.playerName);
            } else {
                out.println("Hej " + playerName + "! Väntar på att " + opponent.playerName + " ska starta spelet...");
                while (opponent.playerName == null) {
                    Thread.sleep(100);
                }
                out.println("Du spelar mot: " + opponent.playerName);
            }

            gameLogic.playGame(out, in);

        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Kunde inte stänga anslutningen: " + e.getMessage());
            }
        }
    }
}
