package controller;

import model.Map.Continent;
import model.Map.Territory;
import model.Player;
import model.PlayerColor;

import java.util.List;

public class PlayerController {
    protected List<Player> players;
    protected int currentPlayer;


    public PlayerController(List<Player> players) {
        this.players = players;
    }


    public PlayerColor getCurrentPlayerColor() {
        return getCurrentPlayer().getColor();
    }


    public List<Player> getPlayers() {
        return this.players;
    }


    public int getNumberOfPlayers() {
        return this.players.size();
    }


    public void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }


    public Player getCurrentPlayer() {
        return this.players.get(currentPlayer);
    }


    public void removeDefeatedPlayer(int playerNum) {
        Player currentPlayerObject = this.players.get(currentPlayer);
        if (playerNum < 0 || playerNum > this.players.size() - 1) {
            throw new IllegalArgumentException("player number must be between "
                    + "0 and the number of players minus one");
        }
        this.players.remove(playerNum);
        this.currentPlayer = players.indexOf(currentPlayerObject);
    }


    public void nextPlayer() {
        if (++currentPlayer == players.size()) {
            this.currentPlayer = 0;
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


    public boolean setPlayerOccupyTerritory(Territory territory) {
        return getCurrentPlayer().occupyTerritory(territory);
    }


    public int getIndexOfPlayer(Player defender) {
        return players.indexOf(defender);
    }


}
