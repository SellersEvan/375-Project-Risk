package controller;

import model.Map.Continent;
import model.Map.Territory;
import model.Player;
import model.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class PlayerController {
    protected ArrayList<Player> playerArray;
    protected int numberOfPlayers;
    protected int currentPlayer;

    public PlayerController(int numberOfPlayers, ArrayList<Player> playerArray) {
        this.numberOfPlayers = numberOfPlayers;
        this.playerArray = playerArray;
    }

    public PlayerColor getCurrentPlayerColor() {
        return getCurrentPlayer().getColor();
    }

    public ArrayList<Player> getPlayerArray() {
        return playerArray;
    }

    public int getNumberOfPlayers() {
        return this.numberOfPlayers;
    }

    public void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }

    public Player getCurrentPlayer() {
        return playerArray.get(currentPlayer);
    }

    public void removeDefeatedPlayer(int playerNum) {
        Player currentPlayerObject = playerArray.get(currentPlayer);
        if (playerNum < 0 || playerNum > numberOfPlayers - 1) {
            throw new IllegalArgumentException("player number must be between "
                    + "0 and the number of players minus one");
        }
        playerArray.remove(playerNum);
        numberOfPlayers--;
        currentPlayer = playerArray.indexOf(currentPlayerObject);
    }

    public void nextPlayer() {
        if (++currentPlayer == playerArray.size()) {
            currentPlayer = 0;
        }
    }

    public int getNumberOfCardForCurrentPlayer() {
        return getCurrentPlayer().getCards().size();
    }

    public int getArmiesAvailableForCurrentPlayer() {
        return getCurrentPlayer().getArmiesAvailable();
    }

    public void addNewTurnArmiesForCurrentPlayer(List<Continent> continents) {
        getCurrentPlayer().addNewTurnArmies(continents);
    }

    public boolean addArmiesToTerritoryForCurrentPlayer(Territory territory, int amount) {
        return getCurrentPlayer().addArmiesToTerritory(territory, amount);
    }

    public boolean doesPlayerOccupyTerritory(Territory territory) {
        return getCurrentPlayer().occupyTerritory(territory);
    }

    public int getIndexOfPlayer(Player defender) {
        return playerArray.indexOf(defender);
    }


}
