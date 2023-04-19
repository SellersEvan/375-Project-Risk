package view;

import controller.Game;
import model.PlayerColor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class PlayerFlag extends JButton {


    private final Game game;


    public PlayerFlag(Game game) {
        super("");
        this.setBackground(null);
        this.setBorder(new EmptyBorder(25, 25, 25, 25));
        this.game = game;
    }


    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.white);
        g.fillRect(1, 3, 27, 27);
        g.fillRect(15, 7, 27, 27);

        g.setColor(PlayerColor.getBackground(this.game.getCurrentPlayer().getColor()));
        g.fillRect(2, 4, 25, 25);
        g.fillRect(16, 8, 25, 25);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,6,50);

        g.setColor(Color.GRAY);
        g.fillRect(2,2,2,46);
    }

}
