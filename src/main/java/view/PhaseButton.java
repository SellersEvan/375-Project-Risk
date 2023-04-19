package view;

import controller.Game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PhaseButton extends JButton {
    private Game game;

    public PhaseButton(String text, Game game) {
        super(text);
        this.setForeground(Color.WHITE);
        this.setBackground(new Color(223, 41, 53));
        this.setBorder(new EmptyBorder(6, 16, 6, 16));
        this.setFont(new Font("SansSerif", Font.PLAIN, 18));
        this.game = game;
        setActionListener();
    }

    private void setActionListener() {
        this.addActionListener(e -> {
            game.phaseAction();
        });
    }
}
