package view.components;

import controller.Game;
import model.InvalidAttackException;
import model.PlayerColor;
import model.Map.Territory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class TerritoryButton extends JButton {

    private final Territory territory;
    private final Game game;

    private final static Border BORDER_UNSELECTED = new EmptyBorder(0, 0, 0, 0);
    private final static Border BORDER_SELECTED   = new MatteBorder(2, 2, 2, 2, Color.RED);



    public TerritoryButton(Territory territory, Game game) {
        super();
        this.territory  = territory;
        this.game       = game;
        this.setFont(new Font("SansSerif", Font.PLAIN, 10));
        this.setBorder(BORDER_UNSELECTED);
        this.setActionListener();
        this.updateDisplay();
    }


    private void setActionListener() {
        this.addActionListener(e -> {
            try {
                game.territoryAction(territory);
                this.updateDisplay();
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


    public void select() {
        this.setBorder(BORDER_SELECTED);
    }


    public void unselect() {
        this.setBorder(BORDER_UNSELECTED);
    }


    public Territory getTerritory() {
        return this.territory;
    }


    private void updateText(String text) {
        this.setText(String.format("<html><center>%s</center></html>", text.replaceAll("\\n", "<br>")));
    }

}
