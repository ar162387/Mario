package minigames.client.gwent.ui;

import minigames.client.gwent.GameController;
import javax.swing.*;

public class PassTurnPanel {
    private final JPanel passEndTurnPanel;

    public PassTurnPanel(GameController gameController) {
        this.passEndTurnPanel = new JPanel();
        initialisePanel(gameController);
    }

    private void initialisePanel(GameController gameController) {
        passEndTurnPanel.setLayout(new BoxLayout(passEndTurnPanel, BoxLayout.X_AXIS));

        JButton passButton = new JButton("Pass Turn");
        passButton.addActionListener(e -> gameController.passRound());

        passEndTurnPanel.add(passButton);
    }

    public JPanel getPanel() {
        return passEndTurnPanel;
    }
}
