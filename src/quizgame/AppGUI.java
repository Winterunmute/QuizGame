package quizgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppGUI extends JFrame {

    private JPanel playerNamePanel; // Dynamisk panel för att skriva in spelarnamn

    public AppGUI() {
        // Ställ in JFrame-egenskaper
        setSize(500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Quiz Game");
        setResizable(false);

        // Wrapper-panel för att hålla allt
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(Color.decode("#33c1ff"));

        // Titel för "Hur många spelare?"
        JLabel playerChoiceTitle = new JLabel("Hur många spelare?");
        playerChoiceTitle.setFont(new Font("Monospaced", Font.BOLD, 28));
        playerChoiceTitle.setForeground(Color.decode("#ffffff"));
        playerChoiceTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrera horisontellt

        // Panel för antal spelare
        JPanel playerChoicePanel = new JPanel();
        playerChoicePanel.setBackground(Color.decode("#33c1ff"));
        playerChoicePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Mellanrum mellan knappar

        JButton buttonTwoPlayers = new JButton("2");
        JButton buttonFourPlayers = new JButton("4");
        Dimension playerButtonSize = new Dimension(100, 50);
        buttonTwoPlayers.setPreferredSize(playerButtonSize);
        buttonFourPlayers.setPreferredSize(playerButtonSize);

        playerChoicePanel.add(buttonTwoPlayers);
        playerChoicePanel.add(buttonFourPlayers);

        // Dynamisk panel för spelarnamn
        playerNamePanel = new JPanel();
        playerNamePanel.setBackground(Color.decode("#33c1ff"));
        playerNamePanel.setLayout(new BoxLayout(playerNamePanel, BoxLayout.Y_AXIS));
        playerNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrera horisontellt

        // Lyssnare för knapparna
        buttonTwoPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPlayerNameFields(2); // Visa textfält för 2 spelare
            }
        });

        buttonFourPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPlayerNameFields(4); // Visa textfält för 4 spelare
            }
        });

        // Titel för "Kategori"
        JLabel categoryTitle = new JLabel("Kategori");
        categoryTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
        categoryTitle.setForeground(Color.decode("#ffffff"));
        categoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel för kategoriknappar
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBackground(Color.decode("#33c1ff"));
        categoryPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 2x2 layout med mellanrum

        JButton historyButton = new JButton("Historia");
        JButton geographyButton = new JButton("Geografi");
        JButton sportsButton = new JButton("Sport");
        JButton chemistryButton = new JButton("Kemi");

        categoryPanel.add(historyButton);
        categoryPanel.add(geographyButton);
        categoryPanel.add(sportsButton);
        categoryPanel.add(chemistryButton);

        // Titel för "Rundor"
        JLabel roundTitle = new JLabel("Rundor");
        roundTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
        roundTitle.setForeground(Color.decode("#ffffff"));
        roundTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel för ronder
        JPanel roundPanel = new JPanel();
        roundPanel.setBackground(Color.decode("#33c1ff"));
        roundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Mellanrum mellan knappar

        for (int i = 1; i <= 6; i++) {
            JButton roundButton = new JButton(String.valueOf(i));
            roundButton.setPreferredSize(new Dimension(50, 50));
            roundPanel.add(roundButton);
        }

        // Start-knapp
        JButton startButton = new JButton("Starta spel");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(200, 50));

        // Lägg till komponenter i wrapper-panelen
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Avstånd
        wrapperPanel.add(playerChoiceTitle);
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Avstånd
        wrapperPanel.add(playerChoicePanel);
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Avstånd
        wrapperPanel.add(playerNamePanel); // Dynamiska spelarnamnfält
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Avstånd
        wrapperPanel.add(categoryTitle);
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Avstånd
        wrapperPanel.add(categoryPanel);
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Avstånd
        wrapperPanel.add(roundTitle);
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Avstånd
        wrapperPanel.add(roundPanel);
        wrapperPanel.add(Box.createRigidArea(new Dimension(20, 30))); // Avstånd
        wrapperPanel.add(startButton);

        // Lägg till wrapper-panelen i JFrame
        add(wrapperPanel);

        // Gör JFrame synlig
        setVisible(true);
    }

    // Funktion för att visa textfält för spelarnamn
    private void displayPlayerNameFields(int playerCount) {
        playerNamePanel.removeAll(); // Rensa tidigare textfält

        for (int i = 1; i <= playerCount; i++) {
            JLabel nameLabel = new JLabel("Spelare " + i + ":");
            nameLabel.setForeground(Color.decode("#ffffff"));
            nameLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrera horisontellt

            JTextField nameField = new JTextField(20);
            nameField.setMaximumSize(new Dimension(300, 30));
            nameField.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrera horisontellt

            playerNamePanel.add(nameLabel);
            playerNamePanel.add(Box.createRigidArea(new Dimension(0, 5))); // Avstånd
            playerNamePanel.add(nameField);
            playerNamePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Avstånd
        }

        // Uppdatera panelen och visa ändringar
        playerNamePanel.revalidate();
        playerNamePanel.repaint();
    }

    public static void main(String[] args) {
        new AppGUI();
    }
}