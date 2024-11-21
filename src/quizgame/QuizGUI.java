package quizgame;

import javax.swing.*;
import java.awt.*;

public class QuizGUI extends JFrame {

    private JLabel questionLabel;
    private JButton[] answerButtons;
    private String[] currentOptions;
    private String currentQuestion;

    // Konstruktor utan parametrar (parameterlös konstruktor)
    public QuizGUI() {
        // Set up the main frame
        setTitle("Matrix Quiz Game");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.BLACK); // Dark background color
        add(mainPanel);

        // Title Label
        JLabel titleLabel = new JLabel("Matrix Quiz Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 255, 0)); // Neon green text for Matrix effect
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Question Panel
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(Color.BLACK);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        questionLabel = new JLabel("Väntar på fråga...");
        questionLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
        questionLabel.setForeground(new Color(0, 255, 0)); // Neon green for question text
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionPanel.add(questionLabel);

        mainPanel.add(questionPanel, BorderLayout.CENTER);

        // Answer Panel
        JPanel answerPanel = new JPanel();
        answerPanel.setLayout(new GridLayout(2, 2, 10, 10));
        answerPanel.setBackground(Color.BLACK);
        answerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create placeholder buttons for answers
        answerButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JButton("Choice " + (i + 1));
            answerButtons[i].setBackground(new Color(0, 128, 0)); // Darker green
            answerButtons[i].setForeground(new Color(0, 255, 0)); // Neon green
            answerPanel.add(answerButtons[i]);
        }

        mainPanel.add(answerPanel, BorderLayout.SOUTH);
    }

    // Method to update the question and options from the server
    public void updateQuestion(String question, String[] options) {
        this.currentQuestion = question;
        this.currentOptions = options;

        questionLabel.setText(currentQuestion);
        for (int i = 0; i < 4; i++) {
            answerButtons[i].setText(currentOptions[i]);
        }
    }

    // Method to get the player's answer
    public String getPlayerAnswer() {
        // Get the answer selected by the player (for now we just get the text of the selected button)
        for (int i = 0; i < 4; i++) {
            if (answerButtons[i].getText().equals(currentOptions[i])) {
                return String.valueOf(i + 1); // Return answer as a string (1-4)
            }
        }
        return "1"; // Default answer if no selection is made
    }

    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            QuizGUI gui = new QuizGUI();
            gui.setVisible(true);
        });
    }
}
