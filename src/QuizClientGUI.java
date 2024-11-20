import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class QuizClientGUI {
    private JFrame frame;
    private JTextArea questionArea;
    private JButton[] optionButtons;
    private PrintWriter out;
    private BufferedReader in;

    public QuizClientGUI(String serverAddress, int port) {
        try {
            // Connect to the server
            Socket socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Set up the GUI
            initUI();

            // Start server communication in a separate thread
            new Thread(this::communicateWithServer).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Kunde inte ansluta till servern: " + e.getMessage(), "Fel", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        frame = new JFrame("Quiz Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        frame.add(new JScrollPane(questionArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setEnabled(false); // Initially disabled
            int finalI = i + 1; // Button index (1-based)
            optionButtons[i].addActionListener(e -> sendAnswer(finalI));
            buttonPanel.add(optionButtons[i]);
        }
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void communicateWithServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("QUESTION: ")) {
                    displayQuestion(line.substring(10)); // Remove "QUESTION: "
                } else if (line.matches("\\d\\. .*")) {
                    updateOption(line); // Update option button
                } else {
                    displayMessage(line); // Show feedback or final score
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Anslutningen till servern bröts: " + e.getMessage(), "Fel", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayQuestion(String question) {
        SwingUtilities.invokeLater(() -> {
            questionArea.setText(question);
            enableOptionButtons();
        });
    }

    private void updateOption(String optionText) {
        SwingUtilities.invokeLater(() -> {
            int optionIndex = Integer.parseInt(optionText.substring(0, 1)) - 1;
            optionButtons[optionIndex].setText(optionText);
            optionButtons[optionIndex].setEnabled(true);
        });
    }

    private void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            questionArea.append("\n" + message);
            if (message.startsWith("Spelet är slut!")) {
                disableOptionButtons();
            }
        });
    }

    private void enableOptionButtons() {
        for (JButton button : optionButtons) {
            button.setEnabled(true);
        }
    }

    private void disableOptionButtons() {
        for (JButton button : optionButtons) {
            button.setEnabled(false);
        }
    }

    private void sendAnswer(int answer) {
        out.println(answer); // Send the answer to the server
        disableOptionButtons(); // Prevent multiple submissions
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizClientGUI("127.0.0.1", 45555));
    }
}
