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

        // Lägg till en testknapp för felsökning
        addTestButton();

        // Gör fönstret synligt
        setVisible(true);

        // Ta fram fönstret
        this.toFront();
        this.repaint();
    }

    private void buildQuestionPanel() {
        questionPanel = new JPanel();
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setBackground(bgColor);

        // Panel för spelarinformation högst upp
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        infoPanel.setBackground(bgColor);

        playerLabel = new JLabel("Spelare: ");
        playerLabel.setFont(titleFontSmaller);
        playerLabel.setForeground(textColor);
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        roundLabel = new JLabel("Runda: ");
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

    private void addTestButton() {
        JButton testButton = new JButton("Test");
        testButton.setFont(titleFontSmaller);
        testButton.setBackground(Color.YELLOW);
        testButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Testknappen fungerade!"));
        questionPanel.add(testButton, BorderLayout.EAST);
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
        questionLabel.setText("<html><h2>" + questionText + "</h2></html>");
    }

    // Metod för att uppdatera svarsalternativen
    public void updateOptions(String[] options) {
        for (int i = 0; i < answerButtons.length; i++) {
            if (i < options.length) {
                answerButtons[i].setText(options[i].trim());
                System.out.println("Knapparna uppdaterades med alternativ: " + options[i].trim());
            } else {
                answerButtons[i].setText("");
                answerButtons[i].setEnabled(false);
                answerButtons[i].setBackground(Color.WHITE);
                System.out.println("Knapparna uppdaterades utan alternativ.");
            }
        }
        // Inaktivera knapparna tills det är spelarens tur
        enableAnswerButtons(false);
    }

    // Metod för att aktivera/inaktivera svarsknapparna
    public void enableAnswerButtons(boolean enable) {
        for (JButton button : answerButtons) {
            button.setEnabled(enable);
        }
        System.out.println("Svarsknapparna är nu " + (enable ? "aktiverade." : "inaktiverade."));
    }

    // Metod för att visa feedback till spelaren
    public void showFeedback(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // Metod för att markera rätt svar
    public void highlightCorrectAnswer(int correctIndex) {
        // Indexering från 0
        if (correctIndex >= 0 && correctIndex < answerButtons.length) {
            answerButtons[correctIndex].setBackground(Color.GREEN);
        }
    }

    // Metod för att markera fel och rätt svar
    public void highlightWrongAnswer(int selectedIndex, int correctIndex) {
        if (selectedIndex >= 0 && selectedIndex < answerButtons.length) {
            answerButtons[selectedIndex].setBackground(Color.RED);
        }
        if (correctIndex >= 0 && correctIndex < answerButtons.length) {
            answerButtons[correctIndex].setBackground(Color.GREEN);
        }
    }

    // Metod för att återställa svarsknapparna
    public void resetAnswerButtons() {
        for (JButton button : answerButtons) {
            button.setBackground(Color.WHITE);
            button.setEnabled(false);
        }
    }

    // Metod för att visa en specifik panel i mainPanel
    public void showPanel(String name) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, name);
        System.out.println("Visar panel: " + name);
    }

    public static void main(String[] args) {
        new QuizGUIClient(); // Ändrat till QuizGUIClient för att köra klienten direkt
    }
}
