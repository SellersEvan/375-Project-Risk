package view;

import controller.Game;
import model.InvalidAttackException;
import model.PlayerColor;
import model.Map.Territory;

import javax.swing.*;
import java.awt.*;

public class TerritoryButton extends JButton {
    private Territory territory;
    private Game gameController;

    public TerritoryButton(String text, Territory territory, Game game) {
        super(text);
        this.territory = territory;
        this.gameController = game;
        setActionListener();
    }

    private void setActionListener() {
        this.addActionListener(e -> {
            try {
                gameController.territoryAction(territory, this);
            } catch (InvalidAttackException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void setPlayer(PlayerColor color) {
        this.setText(gameController.getLanguageBundle().getString("armies") + " " + territory.getArmies());
        switch (color) {
            case RED:
                this.setBackground(Color.RED);
                this.setForeground(Color.WHITE);
                break;
            case GREEN:
                this.setBackground(Color.GREEN);
                this.setForeground(Color.BLACK);
                break;
            case BLUE:
                this.setBackground(Color.BLUE);
                this.setForeground(Color.WHITE);
                break;
            case YELLOW:
                this.setBackground(Color.YELLOW);
                this.setForeground(Color.BLACK);
                break;
            case PURPLE:
                this.setBackground(Color.decode("#6a0dad"));
                this.setForeground(Color.WHITE);
                break;
            case BLACK:
                this.setBackground(Color.BLACK);
                this.setForeground(Color.WHITE);
                break;
        }
    }

    public void updateDisplay() {
        setPlayer(territory.getOccupant().getColor());
        setText(gameController.getLanguageBundle().getString("armies") + " " + territory.getArmies());
    }
}
