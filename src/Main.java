import javax.swing.*;
import java.awt.*;

public class QuizGUI extends JFrame {

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

        JLabel questionLabel = new JLabel("What is the Matrix?");
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

        // Placeholder buttons for answers with Matrix colors
        JButton answerButton1 = new JButton("Choice 1");
        JButton answerButton2 = new JButton("Choice 2");
        JButton answerButton3 = new JButton("Choice 3");
        JButton answerButton4 = new JButton("Choice 4");

        // Set button colors for Matrix style
        Color buttonColor = new Color(0, 128, 0); // Darker green for button background
        Color buttonTextColor = new Color(0, 255, 0); // Neon green for text

        answerButton1.setBackground(buttonColor);
        answerButton1.setForeground(buttonTextColor);
        answerButton2.setBackground(buttonColor);
        answerButton2.setForeground(buttonTextColor);
        answerButton3.setBackground(buttonColor);
        answerButton3.setForeground(buttonTextColor);
        answerButton4.setBackground(buttonColor);
        answerButton4.setForeground(buttonTextColor);

        // Add buttons to answer panel
        answerPanel.add(answerButton1);
        answerPanel.add(answerButton2);
        answerPanel.add(answerButton3);
        answerPanel.add(answerButton4);

        mainPanel.add(answerPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            QuizGUI gui = new QuizGUI();
            gui.setVisible(true);
        });
    }
}
