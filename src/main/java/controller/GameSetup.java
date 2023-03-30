package controller;

import model.CardTrader;
import model.Player;
import model.PlayerColor;
import model.Territory;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameSetup {
    private int armiesPerPlayer;
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    private static final int BASE_ARMIES = 40;
    private static final int EXTRA_PLAYER_MULTIPLIER = 5;

    protected int numberOfPlayers;

    public GameSetup(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public void setInitialArmies() {
        if (numberOfPlayers >= MIN_PLAYERS && numberOfPlayers <= MAX_PLAYERS) {
            this.armiesPerPlayer = (BASE_ARMIES - ((numberOfPlayers - 2) * EXTRA_PLAYER_MULTIPLIER));
        } else {
            throw new IllegalArgumentException("playerCount must be between 2 and 6");
        }
    }

    public int getArmiesPerPlayer() {
        return armiesPerPlayer;
    }


    public int getPlayerWhoGoesFirst(int highestRoller) throws IllegalArgumentException {
        if (highestRoller >= 1 && highestRoller <= 6) {
            return highestRoller - 1;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void addAdjacentTerritories(List<Territory> territories) {
        String[][] adjacentData =
                readCSVFile("/adjacentTerritoriesData.csv", 42, 6);
        for (int i = 0; i < adjacentData.length; i++) {
            for (int j = 0; j < adjacentData[i].length; j++) {
                if (adjacentData[i][j] == null) {
                    continue;
                }
                territories.get(i).addAdjacentTerritory(
                        territories.get(Integer.parseInt(adjacentData[i][j])));
            }
        }
    }

    private ArrayList<Player> defaultPlayers(){
        ArrayList<Player> defaultPlayers = new ArrayList<Player>();
        Random random = new Random();
        CardTrader cardTrader = new CardTrader();
        defaultPlayers.add(new Player(PlayerColor.YELLOW, "Colonel Mustard", random, cardTrader));
        defaultPlayers.add(new Player(PlayerColor.GREEN, "Mr. Green", random, cardTrader));
        defaultPlayers.add(new Player(PlayerColor.BLUE, "Mrs. Peacock", random, cardTrader));
        defaultPlayers.add(new Player(PlayerColor.PURPLE, "Professor Plum", random, cardTrader));
        defaultPlayers.add(new Player(PlayerColor.RED, "Miss Scarlet", random, cardTrader));
        defaultPlayers.add(new Player(PlayerColor.BLACK, "Mrs. White", random, cardTrader));
        return defaultPlayers;
    }
    public ArrayList<Player> fillPlayerArray(int numberOfPlayers) {
        ArrayList<Player> usualSuspects = defaultPlayers();
        ArrayList<Player> results = new ArrayList<Player>();
        for(int count=0; count<numberOfPlayers; count++){
            results.add(usualSuspects.get(count));
        }
        return results;
    }
}
