//package quizgame;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//
//public class AppGUI extends JFrame {
//
//    private GameSession gameSession;
//    private JPanel playerNamePanel; // Panel for player name text fields
//
//    public AppGUI() {
//        gameSession = new GameSession();
//
//        // JFrame properties
//        setSize(500, 800);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setTitle("Quiz Game");
//        setResizable(false);
//
//        // Wrapper panel
//        JPanel wrapperPanel = new JPanel();
//        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
//        wrapperPanel.setBackground(Color.decode("#33c1ff"));
//
//        // Title: "How many players?"
//        JLabel playerChoiceTitle = new JLabel("Hur många spelare?");
//        playerChoiceTitle.setFont(new Font("Monospaced", Font.BOLD, 28));
//        playerChoiceTitle.setForeground(Color.decode("#ffffff"));
//        playerChoiceTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        // Player count selection panel
//        JPanel playerChoicePanel = new JPanel();
//        playerChoicePanel.setBackground(Color.decode("#33c1ff"));
//        playerChoicePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
//
//        JButton buttonTwoPlayers = new JButton("2");
//        JButton buttonFourPlayers = new JButton("4");
//        Dimension playerButtonSize = new Dimension(100, 50);
//        buttonTwoPlayers.setPreferredSize(playerButtonSize);
//        buttonFourPlayers.setPreferredSize(playerButtonSize);
//
//        playerChoicePanel.add(buttonTwoPlayers);
//        playerChoicePanel.add(buttonFourPlayers);
//
//        // Player name input panel
//        playerNamePanel = new JPanel();
//        playerNamePanel.setBackground(Color.decode("#33c1ff"));
//        playerNamePanel.setLayout(new BoxLayout(playerNamePanel, BoxLayout.Y_AXIS));
//        playerNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        // Category title
//        JLabel categoryTitle = new JLabel("Kategori");
//        categoryTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
//        categoryTitle.setForeground(Color.decode("#ffffff"));
//        categoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        // Category panel
//        JPanel categoryPanel = new JPanel();
//        categoryPanel.setBackground(Color.decode("#33c1ff"));
//        categoryPanel.setLayout(new GridLayout(2, 2, 10, 10));
//
//        JButton historyButton = new JButton("Historia");
//        JButton geographyButton = new JButton("Geografi");
//        JButton sportsButton = new JButton("Sport");
//        JButton chemistryButton = new JButton("Kemi");
//
//        categoryPanel.add(historyButton);
//        categoryPanel.add(geographyButton);
//        categoryPanel.add(sportsButton);
//        categoryPanel.add(chemistryButton);
//
//        // Round title
//        JLabel roundTitle = new JLabel("Rundor");
//        roundTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
//        roundTitle.setForeground(Color.decode("#ffffff"));
//        roundTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        // Round panel
//        JPanel roundPanel = new JPanel();
//        roundPanel.setBackground(Color.decode("#33c1ff"));
//        roundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
//
//        for (int i = 3; i <= 5; i++) {
//            JButton roundButton = new JButton(String.valueOf(i));
//            roundButton.setPreferredSize(new Dimension(50, 50));
//            roundPanel.add(roundButton);
//        }
//
//        // Start button
//        JButton startButton = new JButton("Starta spel");
//        startButton.setFont(new Font("Monospaced", Font.BOLD, 20));
//        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//        startButton.setPreferredSize(new Dimension(200, 50));
//
//        // Button listeners
//        buttonTwoPlayers.addActionListener(e -> {
//            displayPlayerNameFields(2);
//            gameSession.setTotalPlayers(2);
//        });
//
//        buttonFourPlayers.addActionListener(e -> {
//            displayPlayerNameFields(4);
//            gameSession.setTotalPlayers(4);
//        });
//
//        historyButton.addActionListener(e -> gameSession.setChosenCategory("Historia"));
//        geographyButton.addActionListener(e -> gameSession.setChosenCategory("Geografi"));
//        sportsButton.addActionListener(e -> gameSession.setChosenCategory("Sport"));
//        chemistryButton.addActionListener(e -> gameSession.setChosenCategory("Kemi"));
//
//        // Show test dialog when "Starta spel" is clicked
//        startButton.addActionListener(e -> showTestDialog());
//
//        // Add components to wrapper panel
//        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20)));
//        wrapperPanel.add(playerChoiceTitle);
//        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        wrapperPanel.add(playerChoicePanel);
//        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20)));
//        wrapperPanel.add(playerNamePanel); // Dynamically generated player name fields
//        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20)));
//        wrapperPanel.add(categoryTitle);
//        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        wrapperPanel.add(categoryPanel);
//        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20)));
//        wrapperPanel.add(roundTitle);
//        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        wrapperPanel.add(roundPanel);
//        wrapperPanel.add(Box.createRigidArea(new Dimension(20, 30)));
//        wrapperPanel.add(startButton);
//
//        // Add wrapper panel to JFrame
//        add(wrapperPanel);
//
//        setVisible(true);
//    }
//
//    private void showTestDialog() {
//        StringBuilder message = new StringBuilder();
//        message.append("Antal spelare: ").append(gameSession.getTotalPlayers()).append("\n");
//        message.append("Vald kategori: ").append(gameSession.getChosenCategory()).append("\n");
//        message.append("Spelarnamn:\n");
//        message.append(gameSession.getPlayerNames()); // Directly append the string of player names
//
//        JOptionPane.showMessageDialog(this, message.toString(), "Testresultat", JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    // Display text fields for player names dynamically
//    private void displayPlayerNameFields(int playerCount) {
//        playerNamePanel.removeAll(); // Clear previous fields
//
//        for (int i = 1; i <= playerCount; i++) {
//            JLabel nameLabel = new JLabel("Spelare " + i + ":");
//            nameLabel.setForeground(Color.decode("#ffffff"));
//            nameLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
//            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//            JTextField nameField = new JTextField(20);
//            nameField.setMaximumSize(new Dimension(300, 30));
//            nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//            // Add focus listener to call addPlayer when focus is lost
//            int playerIndex = i; // Store index for closure
//            nameField.addFocusListener(new FocusListener() {
//                @Override
//                public void focusGained(FocusEvent e) {
//                    // Do nothing when focus is gained
//                }
//
//                @Override
//                public void focusLost(FocusEvent e) {
//                    String playerName = nameField.getText().trim();
//                    if (!playerName.isEmpty()) {
//                        gameSession.addPlayerTest(playerIndex, playerName);
//                    }
//                }
//            });
//
//            playerNamePanel.add(nameLabel);
//            playerNamePanel.add(Box.createRigidArea(new Dimension(0, 5)));
//            playerNamePanel.add(nameField);
//            playerNamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        }
//
//        // Update the panel to reflect the changes
//        playerNamePanel.revalidate();
//        playerNamePanel.repaint();
//    }
//
//    public static void main(String[] args) {
//        new AppGUI();
//    }
//}