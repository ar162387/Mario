package minigames.client.RodentsRevenge;

import javax.swing.*;
import java.awt.*;

public class TimerScorePanel extends JPanel {
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JLabel deathLabel;
    private int timeRemain;
    private int score;
    private int numOfDeath;

    public TimerScorePanel() {
        setLayout(new BorderLayout()); // BorderLayout with horizontal and vertical gaps

        // Customize font and color for retro look
        Font retroFont = new Font("Courier", Font.BOLD, 18);
        Color retroColor = new Color(255, 165, 0); // Orange for a retro feel

        // Create the labels with default text
        scoreLabel = new JLabel("Score: 0");
        deathLabel = new JLabel("Deaths: 0");
        timerLabel = new JLabel("Time: 0", new ImageIcon(getClass().getResource("/RodentsRevenge/Map/timer.png")), JLabel.CENTER);

        // Set the font and color for each label
        scoreLabel.setFont(retroFont);
        deathLabel.setFont(retroFont);
        timerLabel.setFont(retroFont);

        scoreLabel.setForeground(Color.GREEN);
        deathLabel.setForeground(Color.RED);
        timerLabel.setForeground(retroColor);

        // Add padding to the labels for spacing
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        deathLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(scoreLabel, BorderLayout.WEST);  // Score label on the left
        add(timerLabel, BorderLayout.CENTER); // Timer label in the center
        add(deathLabel, BorderLayout.EAST);  // Deaths label on the right

    }

    // Update methods
    public void setTime(int time) {
        timeRemain = time;
        timerLabel.setText("Time: " + timeRemain);
    }

    public void setNumOfDeath(int num) {
        numOfDeath = num;
        deathLabel.setText("Deaths: " + numOfDeath);
    }

    public void setScore(int s) {
        score = s;
        scoreLabel.setText("Score: " + score);
    }

}
