package view.components;

import controller.Game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PhaseButton extends JButton {

    private final Game game;


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
            this.game.phaseAction();
        });
    }

}