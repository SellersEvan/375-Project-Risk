package view.components;

import controller.Game;
import model.InvalidAttackException;
import model.PlayerColor;
import model.Map.Territory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TerritoryButton extends JButton {

    private final Territory territory;
    private final Game game;


    public TerritoryButton(Territory territory, Game game) {
        super();
        this.territory = territory;
        this.game      = game;
        this.setFont(new Font("SansSerif", Font.PLAIN, 10));
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.setActionListener();
        this.updateDisplay();
    }


    private void setActionListener() {
        this.addActionListener(e -> {
            try {
                game.territoryAction(territory, this);
            } catch (InvalidAttackException ex) {
                ex.printStackTrace();
            }
        });
    }


    public void updateDisplay() {
        if (this.territory.hasOccupant()) {
            PlayerColor color = this.territory.getOccupant().getColor();
            this.setBackground(PlayerColor.getBackground(color));
            this.setForeground(PlayerColor.getForeground(color));
            this.updateText(String.format("%s%n%d %s",
                    this.territory.getOccupant().getName(),
                    this.territory.getArmies(),
                    game.getBundle().getString("armies")));
        } else {
            this.setBackground(Color.GRAY);
            this.setForeground(Color.WHITE);
            this.updateText("Unclaimed");
        }

    }


    private void updateText(String text) {
        this.setText(String.format("<html><center>%s</center></html>", text.replaceAll("\\n", "<br>")));
    }

}
