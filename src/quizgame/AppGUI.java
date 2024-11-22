package quizgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AppGUI extends JFrame {

    private GameSession gameSession;
    private JPanel mainPanel; // Main panel with CardLayout
    private JPanel setupPanel; // Initial setup panel
    private JPanel questionPanel; // Question and answer panel
    private JLabel questionLabel;
    private JButton[] answerButtons;

    public AppGUI() {
        gameSession = new GameSession();

        // JFrame properties
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Quiz Game");
        setResizable(false);

        // Main panel with CardLayout
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel);

        // Setup panel
        setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));
        setupPanel.setBackground(Color.decode("#33c1ff"));

        // Title: "Hur många spelare?"
        JLabel playerChoiceTitle = new JLabel("Hur många spelare?");
        playerChoiceTitle.setFont(new Font("Monospaced", Font.BOLD, 28));
        playerChoiceTitle.setForeground(Color.decode("#ffffff"));
        playerChoiceTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Player count selection panel
        JPanel playerChoicePanel = new JPanel();
        playerChoicePanel.setBackground(Color.decode("#33c1ff"));
        playerChoicePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton buttonTwoPlayers = new JButton("2");
        JButton buttonFourPlayers = new JButton("4");
        Dimension playerButtonSize = new Dimension(100, 50);
        buttonTwoPlayers.setPreferredSize(playerButtonSize);
        buttonFourPlayers.setPreferredSize(playerButtonSize);

        playerChoicePanel.add(buttonTwoPlayers);
        playerChoicePanel.add(buttonFourPlayers);

        // Player name input panel
        JPanel playerNamePanel = new JPanel();
        playerNamePanel.setBackground(Color.decode("#33c1ff"));
        playerNamePanel.setLayout(new BoxLayout(playerNamePanel, BoxLayout.Y_AXIS));
        playerNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Category title
        JLabel categoryTitle = new JLabel("Kategori");
        categoryTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
        categoryTitle.setForeground(Color.decode("#ffffff"));
        categoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Category panel
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBackground(Color.decode("#33c1ff"));
        categoryPanel.setLayout(new GridLayout(2, 2, 10, 10));

        JButton historyButton = new JButton("Historia");
        JButton geographyButton = new JButton("Geografi");
        JButton sportsButton = new JButton("Sport");
        JButton chemistryButton = new JButton("Kemi");

        categoryPanel.add(historyButton);
        categoryPanel.add(geographyButton);
        categoryPanel.add(sportsButton);
        categoryPanel.add(chemistryButton);

        // Round selection title
        JLabel roundTitle = new JLabel("Rundor");
        roundTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
        roundTitle.setForeground(Color.decode("#ffffff"));
        roundTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Round panel
        JPanel roundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        roundPanel.setBackground(Color.decode("#33c1ff"));

        for (int i = 1; i <= 5; i++) {
            JButton roundButton = new JButton(String.valueOf(i));
            roundButton.setPreferredSize(new Dimension(50, 50));
            roundPanel.add(roundButton);
        }

        // Start button
        JButton startButton = new JButton("Starta spel");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(200, 50));

        // Add components to setup panel
        setupPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        setupPanel.add(playerChoiceTitle);
        setupPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        setupPanel.add(playerChoicePanel);
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

        // Question panel
        questionPanel = new JPanel();
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setBackground(Color.BLACK);

        // Question label
        questionLabel = new JLabel("Väntar på fråga...");
        questionLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
        questionLabel.setForeground(new Color(0, 255, 0)); // Neon green for text
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionPanel.add(questionLabel, BorderLayout.CENTER);

        // Answer buttons panel
        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        answerPanel.setBackground(Color.BLACK);
        answerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        answerButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JButton("Val " + (i + 1));
            answerButtons[i].setBackground(new Color(0, 128, 0)); // Darker green
            answerButtons[i].setForeground(new Color(0, 255, 0)); // Neon green
            answerPanel.add(answerButtons[i]);
        }

        questionPanel.add(answerPanel, BorderLayout.SOUTH);

        // Add panels to main panel
        mainPanel.add(setupPanel, "Setup");
        mainPanel.add(questionPanel, "Question");

        // Button listeners
        buttonTwoPlayers.addActionListener(e -> {
            gameSession.setTotalPlayers(2);
            updatePlayerNameFields(playerNamePanel, 2);
        });

        buttonFourPlayers.addActionListener(e -> {
            gameSession.setTotalPlayers(4);
            updatePlayerNameFields(playerNamePanel, 4);
        });

        historyButton.addActionListener(e -> gameSession.setChosenCategory("Historia"));
        geographyButton.addActionListener(e -> gameSession.setChosenCategory("Geografi"));
        sportsButton.addActionListener(e -> gameSession.setChosenCategory("Sport"));
        chemistryButton.addActionListener(e -> gameSession.setChosenCategory("Kemi"));

        startButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "Question");
        });

        setVisible(true);
    }

    private void updatePlayerNameFields(JPanel playerNamePanel, int playerCount) {
        playerNamePanel.removeAll();

        for (int i = 1; i <= playerCount; i++) {
            JLabel nameLabel = new JLabel("Spelare " + i + ":");
            nameLabel.setForeground(Color.WHITE);
            JTextField nameField = new JTextField(20);
            nameField.setMaximumSize(new Dimension(300, 30));

            playerNamePanel.add(nameLabel);
            playerNamePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerNamePanel.add(nameField);
            playerNamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        playerNamePanel.revalidate();
        playerNamePanel.repaint();
    }

    public static void main(String[] args) {
        new AppGUI();
    }
}