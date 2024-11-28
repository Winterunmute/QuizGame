import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

/**
 * Huvudklass för klientens grafiska gränssnitt i quizspelet.
 * Hanterar all användarinteraktion och kommunikation med servern.
 */
public class QuizClientGUI extends JFrame {
    // Konstanter för nätverkskommunikation
    private static final int SERVER_PORT = 45555;

    // Nätverkskomponenter
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // GUI-komponenter för olika vyer
    private JPanel mainPanel; // Huvudpanel som innehåller alla andra paneler
    private JPanel gamePanel; // Panel för spelvy
    private JPanel welcomePanel; // Panel för välkomstvy
    private JPanel categoryPanel; // Panel för kategorival
    private JPanel resultPanel; // Panel för resultatvy
    private JTextArea messageArea; // Textområde för meddelanden
    private JButton[] answerButtons; // Knappar för svarsalternativ
    private JLabel questionLabel; // Etikett för frågor
    private JLabel statusLabel; // Etikett för statusmeddelanden
    private JTextArea resultArea; // Textområde för resultat

    // Styling variables
    private Color bgColor = Color.decode("#33c1ff");
    private Font titleFont = new Font("Monospaced", Font.BOLD, 24);
    private Font titleFontSmaller = new Font("Monospaced", Font.BOLD, 18);
    private Font inputFieldFont = new Font("Monospaced", Font.PLAIN, 16);
    private Color textColor = Color.decode("#ffffff");

    // Variabel för att hålla reda på vilket svar som valts
    private int selectedAnswerIndex = -1; // 0-baserad index

    // Huvudkonstruktor som sätter upp fönstret och startar spelet
    public QuizClientGUI() {
        setTitle("Quiz Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);

        initializeComponents();
        connectToServer();
    }

    // Skapar alla paneler och lägger till dem i huvudfönstret
    private void initializeComponents() {
        mainPanel = new JPanel(new CardLayout());

        // Skapa alla paneler
        initializeWelcomePanel();
        initializeCategoryPanel();
        initializeGamePanel();
        initializeResultPanel(); // Lägg till denna metod

        // Lägg till panelerna i huvudpanelen med namn för att kunna växla mellan dem
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(categoryPanel, "category");
        mainPanel.add(gamePanel, "game");
        mainPanel.add(resultPanel, "result"); // Lägg till resultPanel till mainPanel

        add(mainPanel);
    }

    // Skapar välkomstskärmen där spelaren anger sitt namn
    private void initializeWelcomePanel() {
        welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(bgColor);

        JLabel titleLabel = new JLabel("Quiz Game");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Ange ditt namn:");
        nameLabel.setFont(titleFontSmaller);
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(300, 30));
        nameField.setFont(inputFieldFont);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Gå med i spel");
        startButton.setFont(titleFontSmaller);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(52, 152, 219));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                out.println(playerName);
                switchToCategoryPanel(); // Växla till kategoriskärmen efter att namnet skickats
            } else {
                JOptionPane.showMessageDialog(this, "Ange ditt namn.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        welcomePanel.add(nameLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        welcomePanel.add(nameField);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        welcomePanel.add(startButton);
        welcomePanel.add(Box.createVerticalGlue());
    }

    // Skapar kategoriskärmen där spelaren väljer frågekategori
    private void initializeCategoryPanel() {
        categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(bgColor);

        JLabel categoryLabel = new JLabel("Välj Kategori");
        categoryLabel.setFont(titleFont);
        categoryLabel.setForeground(textColor);
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBackground(bgColor);

        String[] categories = { "Historia", "Geografi", "Sport", "Teknik", "Musik", "Film" };
        for (String category : categories) {
            JButton categoryButton = new JButton(category);
            categoryButton.setFont(titleFontSmaller);
            categoryButton.setBackground(Color.WHITE);
            categoryButton.setForeground(Color.BLACK);
            categoryButton.setFocusPainted(false);
            categoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            categoryButton.addActionListener(e -> {
                out.println(category);
                ((CardLayout) mainPanel.getLayout()).show(mainPanel, "game");
            });
            buttonPanel.add(categoryButton);
        }

        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        categoryPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    // Skapar spelskärmen där frågorna visas och besvaras
    private void initializeGamePanel() {
        gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setBackground(bgColor);

        statusLabel = new JLabel("Väntar på spel att starta...");
        statusLabel.setFont(titleFontSmaller);
        statusLabel.setForeground(textColor);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        questionLabel = new JLabel("");
        questionLabel.setFont(titleFontSmaller);
        questionLabel.setForeground(textColor);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonsPanel.setBackground(bgColor);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        answerButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JButton();
            answerButtons[i].setFont(titleFontSmaller);
            answerButtons[i].setBackground(Color.WHITE);
            answerButtons[i].setForeground(Color.BLACK);
            answerButtons[i].setFocusPainted(false);
            answerButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            final int answer = i + 1;
            int finalI = i;
            answerButtons[i].addActionListener(e -> {
                out.println(String.valueOf(answer));
                selectedAnswerIndex = finalI; // Spara vilket svar som valts
                enableAnswerButtons(false); // Inaktivera knapparna för att förhindra flera val
                statusLabel.setText("Svar skickat. Väntar på feedback...");
            });
            buttonsPanel.add(answerButtons[i]);
        }

        gamePanel.add(statusLabel, BorderLayout.NORTH);
        gamePanel.add(questionLabel, BorderLayout.CENTER);
        gamePanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    // Skapar resultatpanelen där slutresultaten visas
    private void initializeResultPanel() {
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(bgColor);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Lägg till marginaler

        JLabel resultTitle = new JLabel("Slutresultat");
        resultTitle.setFont(titleFont);
        resultTitle.setForeground(textColor);
        resultTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultArea = new JTextArea();
        resultArea.setFont(inputFieldFont);
        resultArea.setEditable(false);
        resultArea.setForeground(textColor);
        resultArea.setBackground(bgColor);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultArea.setMaximumSize(new Dimension(400, 200));

        JButton exitButton = new JButton("Avsluta");
        exitButton.setFont(titleFontSmaller);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> System.exit(0));

        resultPanel.add(resultTitle);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        resultPanel.add(resultArea);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        resultPanel.add(exitButton);

        mainPanel.add(resultPanel, "result");
    }

    // Ansluter till spelservern och sätter upp strömmar för kommunikation
    private void connectToServer() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Starta en separat tråd som lyssnar på meddelanden från servern
            new Thread(this::handleServerMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Kunde inte ansluta till servern: " + e.getMessage(),
                    "Anslutningsfel",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // Växlar till kategoriskärmen
    private void switchToCategoryPanel() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "category");
    }

    // Hanterar alla inkommande meddelanden från servern
    private void handleServerMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> processServerMessage(finalMessage));
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Lost connection to server: " + e.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }

    // Tolkar och hanterar olika typer av meddelanden från servern
    private void processServerMessage(String message) {
        // Om meddelandet indikerar att det är dags att välja kategori
        if (message.startsWith("Runda") && message.contains("Välj kategori:")) {
            // Extrahera kategorier från meddelandet
            String[] parts = message.split(":");
            if (parts.length > 2) { // Förvänta formatet "Runda X: Välj kategori: Cat1, Cat2, ..."
                String[] categories = parts[2].trim().split(",");
                showCategorySelection(categories);
            }
        }
        // Om meddelandet är en ny fråga
        else if (message.startsWith("Fråga: ")) {
            // Återställ knapparnas färger och text innan ny fråga visas
            resetAnswerButtons();

            questionLabel.setText("<html><center>" + message.substring(7) + "</center></html>");
            enableAnswerButtons(true);
            statusLabel.setText("Please select an answer.");
        }
        // Om meddelandet är ett svarsalternativ
        else if (message.matches("\\d\\..*")) {
            int optionNum = Character.getNumericValue(message.charAt(0)) - 1;
            if (optionNum >= 0 && optionNum < answerButtons.length) {
                answerButtons[optionNum].setText(message.substring(3).trim());
            }
        }
        // Om meddelandet är feedback på svaret
        else if (message.startsWith("Rätt!")) {
            statusLabel.setText("Correct Answer!");
            if (selectedAnswerIndex >= 0 && selectedAnswerIndex < answerButtons.length) {
                fadeButtonColor(answerButtons[selectedAnswerIndex], Color.GREEN);
            }
            resetSelectedAnswer();
        } else if (message.startsWith("Fel!")) {
            statusLabel.setText("Incorrect Answer!");
            if (selectedAnswerIndex >= 0 && selectedAnswerIndex < answerButtons.length) {
                fadeButtonColor(answerButtons[selectedAnswerIndex], Color.RED);
            }
            resetSelectedAnswer();
        }
        // Om meddelandet är resultat eller andra meddelanden
        else {
            statusLabel.setText(message);
            if (message.startsWith("Resultat:")) {
                showResult(message.substring(9).trim());
            } else if (message.contains("motståndare har anslutit") ||
                    message.contains("Du spelar mot:")) {
                ((CardLayout) mainPanel.getLayout()).show(mainPanel, "game");
            }
        }
    }

    // Aktiverar eller inaktiverar alla svarsknappar
    private void enableAnswerButtons(boolean enable) {
        for (JButton button : answerButtons) {
            button.setEnabled(enable);
        }
    }

    // Återställer svarsknapparnas färger och text
    private void resetAnswerButtons() {
        for (JButton button : answerButtons) {
            button.setBackground(Color.WHITE);
            button.setText("");
        }
    }

    // Återställer den valda svaret
    private void resetSelectedAnswer() {
        selectedAnswerIndex = -1;
    }

    // Uppdaterar kategoriskärmen med nya kategorier och visar den
    private void showCategorySelection(String[] categories) {
        // Rensa gamla komponenter
        categoryPanel.removeAll();
        categoryPanel.setLayout(new BorderLayout());

        // Skapa ny titel och knappanel
        JLabel titleLabel = new JLabel("Välj Kategori", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Skapa knappar för varje kategori
        for (String category : categories) {
            JButton categoryButton = new JButton(category.trim());
            categoryButton.setFont(titleFontSmaller);
            categoryButton.setBackground(Color.WHITE);
            categoryButton.setForeground(Color.BLACK);
            categoryButton.setFocusPainted(false);
            categoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            categoryButton.addActionListener(e -> {
                out.println(category.trim());
                ((CardLayout) mainPanel.getLayout()).show(mainPanel, "game");
            });
            buttonPanel.add(categoryButton);
        }

        // Lägg till komponenter och visa panelen
        categoryPanel.add(titleLabel, BorderLayout.NORTH);
        categoryPanel.add(buttonPanel, BorderLayout.CENTER);
        categoryPanel.revalidate();
        categoryPanel.repaint();

        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "category");
    }

    // Visar resultatpanelen med slutresultaten
    private void showResult(String resultText) {
        // Uppdatera resultatområdet
        resultArea.setText(resultText);

        // Visa resultatpanelen
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "result");
    }

    // Metod för att animera färgändring på knappar med fade-effekt och
    // återställning
    private void fadeButtonColor(JButton button, Color targetColor) {
        Color initialColor = button.getBackground();
        int steps = 5; // Minska antalet steg för snabbare fade
        int delay = 20; // Minska fördröjningen mellan steg

        // Beräkna stegvisa färgändringar
        float[] initialRGB = initialColor.getRGBComponents(null);
        float[] targetRGB = targetColor.getRGBComponents(null);
        float deltaR = (targetRGB[0] - initialRGB[0]) / steps;
        float deltaG = (targetRGB[1] - initialRGB[1]) / steps;
        float deltaB = (targetRGB[2] - initialRGB[2]) / steps;

        Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            int currentStep = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < steps) {
                    float newR = initialRGB[0] + deltaR * currentStep;
                    float newG = initialRGB[1] + deltaG * currentStep;
                    float newB = initialRGB[2] + deltaB * currentStep;
                    button.setBackground(new Color(newR, newG, newB));
                    currentStep++;
                } else {
                    button.setBackground(targetColor);
                    timer.stop();

                    // Starta en ny timer för att återställa färgen efter en kort stund
                    Timer resetTimer = new Timer(200, new ActionListener() { // 0.2 sekund delay
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button.setBackground(Color.WHITE);
                        }
                    });
                    resetTimer.setRepeats(false);
                    resetTimer.start();
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QuizClientGUI().setVisible(true);
        });
    }
}
