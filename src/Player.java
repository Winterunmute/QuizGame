import javax.swing.*;

public class Player extends JFrame {

    private String name;
    private int score;

    private JFrame frame;
    private JLabel nameLabel, scoreLabel;
    private JButton button;
    private JButton resetButton;


    public Player() {
        this.name = JOptionPane.showInputDialog("Enter your name");
        this.score = 0;


        frame = new JFrame("Player");
        nameLabel = new JLabel("Player" + name);
        scoreLabel = new JLabel("Score: " + score);
        button = new JButton("Counter");
        resetButton = new JButton("Reset");

        JPanel panel = new JPanel();
        panel.add(nameLabel);
        panel.add(scoreLabel);
        panel.add(button);
        panel.add(resetButton);

        frame.add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(500, 500);

        button.addActionListener(e -> {
            score++;
            scoreLabel.setText("Score: " + score);
        });
        resetButton.addActionListener(e -> {
            score = 0;
            scoreLabel.setText("Score: " + score);

        });
        JOptionPane.showMessageDialog(frame, "Welcome " + name);
    }
    public static void main(String[] args) {
        new Player();
    }
}
