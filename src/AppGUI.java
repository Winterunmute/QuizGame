import javax.swing.*;
import java.awt.*;

public class AppGUI extends JFrame {

    protected JPanel mainPanel;
    protected JPanel questionPanel;
    protected JLabel questionLabel;
    protected JButton[] answerButtons;
    protected JLabel playerLabel;
    protected JLabel roundLabel;

    // Styling variabler
    private Color bgColor = Color.decode("#33c1ff");
    private Font titleFont = new Font("Monospaced", Font.BOLD, 24);
    private Font titleFontSmaller = new Font("Monospaced", Font.BOLD, 18);
    private Color textColor = Color.decode("#ffffff");

    public AppGUI() {
        // Styling av JFrame
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Quiz Game");
        setResizable(false);

        // Huvudpanelen med CardLayout
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel);

        // Bygg frågepanelen
        buildQuestionPanel();

        // Lägg till frågepanelen i mainPanel
        mainPanel.add(questionPanel, "Question");

        // Gör fönstret synligt
        setVisible(true);
    }

    private void buildQuestionPanel() {
        questionPanel = new JPanel();
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setBackground(bgColor);

        // Panel för spelarinformation högst upp
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        infoPanel.setBackground(bgColor);

        playerLabel = new JLabel("Spelare:");
        playerLabel.setFont(titleFontSmaller);
        playerLabel.setForeground(textColor);
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        roundLabel = new JLabel("Runda:");
        roundLabel.setFont(titleFontSmaller);
        roundLabel.setForeground(textColor);
        roundLabel.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(playerLabel);
        infoPanel.add(roundLabel);

        // Lägg till infoPanel överst i questionPanel
        questionPanel.add(infoPanel, BorderLayout.NORTH);

        // Etikett för frågetexten
        questionLabel = new JLabel("Väntar på fråga...");
        questionLabel.setFont(titleFontSmaller);
        questionLabel.setForeground(textColor);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        questionPanel.add(questionLabel, BorderLayout.CENTER);

        // Panel för svarsknappar
        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        answerPanel.setBackground(bgColor);
        answerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        answerButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JButton("Val " + (i + 1));
            answerButtons[i].setFont(titleFontSmaller);
            answerButtons[i].setBackground(Color.WHITE);
            answerButtons[i].setForeground(Color.BLACK);
            answerButtons[i].setPreferredSize(new Dimension(100, 100));
            answerButtons[i].setFocusPainted(false);

            // Möjliggör färgändring på knapparna
            answerButtons[i].setOpaque(true);
            answerButtons[i].setContentAreaFilled(true);

            final int answerIndex = i + 1;
            // ActionListener implementeras i underklassen (QuizGUIClient)
            answerButtons[i].addActionListener(e -> handleAnswerSelected(answerIndex));

            answerPanel.add(answerButtons[i]);
        }

        questionPanel.add(answerPanel, BorderLayout.SOUTH);
    }

    // Metod för att hantera när en svarsknapp trycks ned
    protected void handleAnswerSelected(int answerIndex) {
        // Denna metod kommer att överskrivas i QuizGUIClient
    }

    // Metod för att sätta spelarens namn i GUI:t
    public void setPlayerName(String playerName) {
        playerLabel.setText("Spelare: " + playerName);
    }

    // Metod för att uppdatera runda i GUI:t
    public void setRoundNumber(int roundNumber) {
        roundLabel.setText("Runda: " + roundNumber);
    }

    // Metod för att uppdatera frågetexten
    public void updateQuestion(String questionText) {
        questionLabel.setText("<html>" + questionText + "</html>");
    }

    // Metod för att uppdatera svarsalternativen
    public void updateOptions(String[] options) {
        for (int i = 0; i < options.length; i++) {
            answerButtons[i].setText(options[i]);
            // Inaktivera knapparna tills det är spelarens tur
            answerButtons[i].setEnabled(false);
            // Återställ knappfärgen
            answerButtons[i].setBackground(Color.WHITE);
        }
        // Inaktivera oanvända knappar om det finns färre än 4 alternativ
        for (int i = options.length; i < answerButtons.length; i++) {
            answerButtons[i].setText("");
            answerButtons[i].setEnabled(false);
            answerButtons[i].setBackground(Color.WHITE);
        }
    }

    // Metod för att aktivera/inaktivera svarsknapparna
    public void enableAnswerButtons(boolean enable) {
        for (JButton button : answerButtons) {
            button.setEnabled(enable);
        }
    }

    // Metod för att visa feedback till spelaren
    public void showFeedback(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // Metod för att markera rätt svar
    public void highlightCorrectAnswer(int correctIndex) {
        // Indexering från 0
        answerButtons[correctIndex].setBackground(Color.GREEN);
    }

    // Metod för att markera fel och rätt svar
    public void highlightWrongAnswer(int selectedIndex, int correctIndex) {
        answerButtons[selectedIndex].setBackground(Color.RED);
        answerButtons[correctIndex].setBackground(Color.GREEN);
    }

    // Metod för att återställa svarsknapparna
    public void resetAnswerButtons() {
        for (JButton button : answerButtons) {
            button.setBackground(Color.WHITE);
            button.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        new AppGUI();
    }
}
