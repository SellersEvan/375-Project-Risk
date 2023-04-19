package controller;

import model.Map.Continent;
import model.Map.Territory;
import model.Player;
import model.PlayerColor;

import java.util.Collections;
import java.util.List;

public class PlayerController {

    public static final int  MIN_PLAYERS = 2;
    public static final int  MAX_PLAYERS = 6;
    private static final int BASE_ARMIES = 40;
    private static final int EXTRA_PLAYER_MULTIPLIER = 5;

    protected List<Player> players;
    protected int currentPlayer = 0;


    public PlayerController(List<Player> players) {
        if (players.size() >= MIN_PLAYERS && players.size() <= MAX_PLAYERS) {
            this.players = players;
            this.shufflePlayers();
            this.setupArmies();
        } else {
            throw new IllegalArgumentException("Must have between 2-6 Player");
        }
    }


    private void shufflePlayers() {
        Collections.shuffle(this.players);
    }


    private void setupArmies() {
        int amountOfPlayers = this.players.size();
        int armiesPerPlayer = (BASE_ARMIES - ((amountOfPlayers - 2) * EXTRA_PLAYER_MULTIPLIER));
        for (Player player: this.players)
            player.giveArmies(armiesPerPlayer);
    }


    public List<Player> getPlayers() {
        return this.players;
    }


    public int getNumberOfPlayers() {
        return this.players.size();
    }


    public Player getCurrentPlayer() {
        return this.players.get(currentPlayer);
    }


    public void removePlayer(Player player) {
        this.players.remove(player);
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
