import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class QuizClientGUI extends JFrame {
    private static final int SERVER_PORT = 45555;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JPanel mainPanel;
    private JPanel gamePanel;
    private JPanel welcomePanel;
    private JPanel categoryPanel;
    private JTextArea messageArea;
    private JButton[] answerButtons;
    private JLabel questionLabel;
    private JLabel statusLabel;

    // Styling variables
    private Color bgColor = Color.decode("#33c1ff");
    private Font titleFont = new Font("Monospaced", Font.BOLD, 24);
    private Font titleFontSmaller = new Font("Monospaced", Font.BOLD, 18);
    private Font inputFieldFont = new Font("Monospaced", Font.PLAIN, 16);
    private Color textColor = Color.decode("#ffffff");

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

        // Lägg till panelerna i huvudpanelen med namn för att kunna växla mellan dem
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(categoryPanel, "category");
        mainPanel.add(gamePanel, "game");

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

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(titleFontSmaller);
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(300, 30));
        nameField.setFont(inputFieldFont);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Join Game");
        startButton.setFont(titleFontSmaller);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                out.println(playerName);
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

        JLabel categoryLabel = new JLabel("Choose Category");
        categoryLabel.setFont(titleFont);
        categoryLabel.setForeground(textColor);
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBackground(bgColor);

        String[] categories = { "Historia", "Geografi", "Sport", "Teknik", "Musik", "Film" };
        for (String category : categories) {
            JButton categoryButton = new JButton(category);
            categoryButton.setFont(titleFontSmaller);
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

        statusLabel = new JLabel("Waiting for game to start...");
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
            final int answer = i + 1;
            answerButtons[i].addActionListener(e -> out.println(String.valueOf(answer)));
            buttonsPanel.add(answerButtons[i]);
        }

        gamePanel.add(statusLabel, BorderLayout.NORTH);
        gamePanel.add(questionLabel, BorderLayout.CENTER);
        gamePanel.add(buttonsPanel, BorderLayout.SOUTH);
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
            // Extract categories from the message
            String[] parts = message.split(":");
            if (parts.length > 1) {
                String[] categories = parts[2].trim().split(",");
                showCategorySelection(categories);
            }
        }
        // Om meddelandet är en ny fråga
        else if (message.startsWith("Fråga: ")) {
            questionLabel.setText("<html><center>" + message.substring(7) + "</center></html>");
            enableAnswerButtons(true);
        }
        // Om meddelandet är ett svarsalternativ
        else if (message.matches("\\d\\..*")) {
            int optionNum = Character.getNumericValue(message.charAt(0)) - 1;
            answerButtons[optionNum].setText(message.substring(3).trim());
        }
        // Övriga meddelanden (status, resultat, etc)
        else {
            statusLabel.setText(message);
            if (message.contains("Rätt!") || message.contains("Fel!")) {
                enableAnswerButtons(false);
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
            // När en kategori väljs, skicka valet till servern och visa spelskärmen
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QuizClientGUI().setVisible(true);
        });
    }
}