import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppGUI extends JFrame {

    protected GameSession gameSession;
    protected JPanel mainPanel;
    protected JPanel setupPanel;
    protected JPanel questionPanel;
    protected JLabel questionLabel;
    protected JButton[] answerButtons;
    protected JLabel playerLabel;
    protected JLabel roundLabel;

    // Styling variabler för att undvika duplicering av kod
    private Color bgColor = Color.decode("#33c1ff");
    private Font titleFont = new Font("Monospaced", Font.BOLD, 24);
    private Font titleFontSmaller = new Font("Monospaced", Font.BOLD, 18);
    private Color textColor = Color.decode("#ffffff");

    public AppGUI() {
        gameSession = new GameSession();

        // Styling av JFrame
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Quiz Game");
        setResizable(false);

        // Huvudpanelen som använder CardLayout för att kunna ändra från startskärm till spelskärm i samma utrymme
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel);

        // Startskärms panelen
        setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));
        setupPanel.setBackground(bgColor);

        // Titeln för namnval panelen och styling
        JLabel playerChoiceTitle = new JLabel("Ange namn på spelare nedan");
        playerChoiceTitle.setFont(titleFont);
        playerChoiceTitle.setForeground(textColor);
        playerChoiceTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel för att hålla inputfälten för namnen
        JPanel playerNamePanel = new JPanel();

        // Layout och styling för playerNamePanel
        playerNamePanel.setLayout(new BoxLayout(playerNamePanel, BoxLayout.Y_AXIS));
        playerNamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        playerNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNamePanel.setBackground(bgColor);

        // Etikett och textfält för spelare 1
        JLabel inputFieldLabelP1 = new JLabel("Namn på spelare 1");
        inputFieldLabelP1.setFont(titleFontSmaller);
        inputFieldLabelP1.setForeground(Color.WHITE);
        inputFieldLabelP1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputFieldPlayer1 = new JTextField();
        inputFieldPlayer1.setMaximumSize(new Dimension(300, 30));
        inputFieldPlayer1.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Etikett och textfält för spelare 2
        JLabel inputFieldLabelP2 = new JLabel("Namn på spelare 2");
        inputFieldLabelP2.setFont(titleFontSmaller);
        inputFieldLabelP2.setForeground(Color.WHITE);
        inputFieldLabelP2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputFieldPlayer2 = new JTextField();
        inputFieldPlayer2.setFont(titleFontSmaller);
        inputFieldPlayer2.setMaximumSize(new Dimension(300, 30));
        inputFieldPlayer2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Lägger till komponenterna deklarerade ovan med mellanrum för clean GUI
        playerNamePanel.add(inputFieldLabelP1);
        playerNamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        playerNamePanel.add(inputFieldPlayer1);
        playerNamePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        playerNamePanel.add(inputFieldLabelP2);
        playerNamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        playerNamePanel.add(inputFieldPlayer2);


        // Titeln för kategorival-sektionen
        JLabel categoryTitle = new JLabel("Kategori");
        categoryTitle.setFont(titleFont);
        categoryTitle.setForeground(textColor);
        categoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Kategorival panelen med 4 olika knappar som representerar de 4 olika kategorierna
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBackground(Color.decode("#33c1ff"));
        categoryPanel.setLayout(new GridLayout(2, 2, 10, 10));

        // Knapparna
        JButton historyButton = new JButton("Historia");
        JButton geographyButton = new JButton("Geografi");
        JButton sportsButton = new JButton("Sport");
        JButton chemistryButton = new JButton("Kemi");

        // Lägger till knapparna på categoryPanel
        categoryPanel.add(historyButton);
        categoryPanel.add(geographyButton);
        categoryPanel.add(sportsButton);
        categoryPanel.add(chemistryButton);

        // Titeln för val av rundor sektionen
        JLabel roundTitle = new JLabel("Rundor");
        roundTitle.setFont(titleFont);
        roundTitle.setForeground(textColor);
        roundTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panelen för val av rundor
        JPanel roundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        roundPanel.setBackground(Color.decode("#33c1ff"));

        // En for loop för att lägga till 4 stycken knappar med val från 2 till 5 rundor
        for (int i = 2; i <= 5; i++) {
            JButton roundButton = new JButton(String.valueOf(i));
            roundButton.setPreferredSize(new Dimension(50, 50));
            final int rounds = i;
            roundButton.addActionListener(e -> {
                gameSession.setTotalRounds(rounds);

            });
            roundPanel.add(roundButton);
        }

        // Start knappen
        JButton startButton = new JButton("Starta spel");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(200, 50));

        // Lägger till alla paneler och titlar vi skapat ovan på setUpPanel panelen
        // (Panelen som omringar start skärmen)
        setupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        setupPanel.add(playerChoiceTitle);
        setupPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        setupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        setupPanel.add(playerNamePanel);
        setupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        setupPanel.add(categoryTitle);
        setupPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        setupPanel.add(categoryPanel);
        setupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        setupPanel.add(roundTitle);
        setupPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        setupPanel.add(roundPanel);
        setupPanel.add(Box.createRigidArea(new Dimension(20, 30)));
        setupPanel.add(startButton);

        // Panelen som visas när ett spel har startats nedan - - - - - -

        questionPanel = new JPanel();
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setBackground(bgColor);

        // Panelen längs upp som visar nuvarande runda och namn på spelare
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        infoPanel.setBackground(bgColor);

        playerLabel = new JLabel("Spelare:");
        playerLabel.setFont(titleFontSmaller);
        playerLabel.setForeground(textColor);
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        roundLabel = new JLabel("Runda:");
        roundLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        roundLabel.setForeground(Color.WHITE);
        roundLabel.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(playerLabel);
        infoPanel.add(roundLabel);

        // Placerar infopanelen längst upp av skärmen
        questionPanel.add(infoPanel, BorderLayout.NORTH);

        // Panelen för alternativ av frågor
        questionLabel = new JLabel("Väntar på fråga...");
        questionLabel.setFont(titleFontSmaller);
        questionLabel.setForeground(textColor);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionPanel.add(questionLabel, BorderLayout.CENTER);

        // Panel för svarsalternativ
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

            // Lägger till dessa för att kunna hantera ändringen av färg vid fel / rätt svar
            answerButtons[i].setOpaque(true);
            answerButtons[i].setContentAreaFilled(true);

            final int answerIndex = i + 1;
            answerButtons[i].addActionListener(e -> handleAnswerSelected(answerIndex));
            answerPanel.add(answerButtons[i]);
        }

        questionPanel.add(answerPanel, BorderLayout.SOUTH);

        // Add panels to main panel
        mainPanel.add(setupPanel, "Setup");
        mainPanel.add(questionPanel, "Question");


        historyButton.addActionListener(e -> gameSession.setChosenCategory("Historia"));
        geographyButton.addActionListener(e -> gameSession.setChosenCategory("Geografi"));
        sportsButton.addActionListener(e -> gameSession.setChosenCategory("Sport"));
        chemistryButton.addActionListener(e -> gameSession.setChosenCategory("Kemi"));

        startButton.addActionListener(e -> {
            // Hämta in text från inputrutorna
            String player1Name = inputFieldPlayer1.getText().trim();
            String player2Name = inputFieldPlayer2.getText().trim();

            // Använd standardnamn om något fält är tomt
            if (player1Name.isEmpty()) {
                player1Name = "Anonym 1";
            }
            if (player2Name.isEmpty()) {
                player2Name = "Anonym 2";
            }

            // Lägg till spelarna i spelet
            gameSession.addPlayer(player1Name);
            gameSession.addPlayer(player2Name);

            // Initialisera spelet
            gameSession.initializeGame();

            // Ladda första frågan
            loadNextQuestion();

            // Byt till frågepanelen
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "Question");
        });

        setVisible(true);
    }


    protected void loadNextQuestion() {
        if (gameSession.isGameOver()) {
            showFinalResults();
            return;
        }

        Question question = gameSession.getNextQuestion();
        if (question == null) {
            showFinalResults();
            return;
        }

        // Uppdatera spelare och runda
        Player currentPlayer = gameSession.getCurrentPlayer();
        playerLabel.setText("Spelare: " + currentPlayer.getPlayerName());
        roundLabel.setText("Runda: " + gameSession.getCurrentRound());

        // Uppdatera frågeetiketten
        questionLabel.setText("<html>" + question.getQuestion() + "</html>");

        // Uppdatera svarsknapparna
        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            answerButtons[i].setText(options[i]);
            answerButtons[i].setEnabled(true);
        }

        // Inaktivera oanvända knappar om färre än 4 alternativ
        for (int i = options.length; i < answerButtons.length; i++) {
            answerButtons[i].setText("");
            answerButtons[i].setEnabled(false);
        }
    }

    protected void handleAnswerSelected(int answerIndex) {
        // Kontrollera om svaret är korrekt
        boolean isCorrect = gameSession.checkAnswer(answerIndex);

        // Hämta den aktuella frågan
        Question currentQuestion = gameSession.getCurrentQuestion();
        int correctAnswerIndex = currentQuestion.getCorrectAnswer() - 1; // Nollindexering

        // Inaktivera alla knappar efter valet
        for (JButton button : answerButtons) {
            button.setEnabled(false);
        }

        // Markera knapparna
        if (isCorrect) {
            // Om användaren klickar på knappen med rätt svar: sätt bakgrunden på den klickade knappen till grön
            answerButtons[answerIndex - 1].setBackground(Color.GREEN);
        } else {

            // Om användaren klickar en knapp med fel svar: sätt bakgrunden röd på den klickade knappen
            answerButtons[answerIndex - 1].setBackground(Color.RED);
            // och bakgrunden grön på den rätta
            answerButtons[correctAnswerIndex].setBackground(Color.GREEN);
        }

        // Tvingar GUI:t att uppdatera färgerna innan timer
        for (JButton button : answerButtons) {
            button.repaint();
        }

        // Sätter en timer så att färgerna hinner visas innan nästa fråga laddar
        Timer timer = new Timer(1000, e -> {
            // Återställ knappfärger
            for (JButton button : answerButtons) {
                button.setBackground(Color.WHITE);
                button.setEnabled(true); // Återaktivera knappar
            }
            // Gå vidare till nästa tur
            gameSession.nextTurn();
            loadNextQuestion();
        });
        timer.setRepeats(false);
        timer.start();
    }

    protected void showFinalResults() {
        List<Player> finalResults = gameSession.getFinalResults();
        StringBuilder resultsMessage = new StringBuilder("<html><h1>Spelet är slut! Slutresultat:</h1><ul>");
        for (Player player : finalResults) {
            resultsMessage.append("<li>").append(player.getPlayerName())
                    .append(": ").append(player.getScore()).append(" poäng</li>");
        }
        resultsMessage.append("</ul></html>");

        JOptionPane.showMessageDialog(this, resultsMessage.toString(), "Slutresultat", JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    public static void main(String[] args) {
        new AppGUI();
    }
}
