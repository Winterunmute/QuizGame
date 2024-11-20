import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

 class QuizClientGUI {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 45555;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public QuizClientGUI() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            showCategorySelection();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Kunde inte ansluta till servern: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void showCategorySelection() {
        while (true) {
            String[] categories = {"Historia", "Geografi", "Vetenskap"};
            String category = (String) JOptionPane.showInputDialog(
                    null,
                    "Välj en kategori:",
                    "Kategori",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    categories,
                    categories[0]);

            if (category == null) {
                closeConnection();
                System.exit(0);
            }

            out.println(category);

            try {
                String serverResponse = in.readLine();
                if (serverResponse != null && serverResponse.startsWith("INVALID_CATEGORY")) {
                    JOptionPane.showMessageDialog(null, "Ogiltig kategori. Försök igen!", "Fel", JOptionPane.WARNING_MESSAGE);
                } else {
                    startQuiz();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Fel vid kommunikation med servern: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                closeConnection();
                System.exit(1);
            }
        }
    }

    private void startQuiz() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("QUESTION:")) {
                    String question = serverMessage.substring(9);
                    String[] options = new String[4];
                    for (int i = 0; i < 4; i++) {
                        options[i] = in.readLine();
                    }

                    String answer = (String) JOptionPane.showInputDialog(
                            null,
                            question,
                            "Quizfråga",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (answer == null) {
                        closeConnection();
                        System.exit(0);
                    }

                    out.println(answer);

                    String feedback = in.readLine();
                    JOptionPane.showMessageDialog(null, feedback, "Feedback", JOptionPane.INFORMATION_MESSAGE);
                } else if (serverMessage.equals("ROUND_OVER")) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, serverMessage, "Meddelande", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Fel vid kommunikation med servern: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            closeConnection();
            System.exit(1);
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Kunde inte stänga anslutningen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QuizClientGUI::new);
    }
}
