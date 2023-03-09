package view;

import controller.Game;

import javax.swing.*;

public class PhaseButton extends JButton {
    private Game game;

    public PhaseButton(String text, Game game) {
        super(text);
        this.game = game;
        setActionListener();
    }

    private void setActionListener() {
        this.addActionListener(e -> {
            game.phaseAction();
        });
    }
}
