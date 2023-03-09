package controller;

import model.CardTrader;
import model.Player;
import model.PlayerColor;
import model.Territory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSetup {
    private int armiesPerPlayer;
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    private static final int BASE_ARMIES = 40;
    private static final int EXTRA_PLAYER_MULTIPLIER = 5;
    private static final PlayerColor[] PLAYER_COLORS = PlayerColor.values();

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

    public ArrayList<Player> fillPlayerArray(List<Territory> territories) {
        ArrayList<Player> playerArray = new ArrayList<>();
    	Random random = new Random();
    	CardTrader cardTrader = new CardTrader(random, territories);
        for (int i = 0; i < numberOfPlayers; i++) {
            PlayerColor playerColor = PLAYER_COLORS[i];
            Player p = new Player(playerColor, random, cardTrader);
            p.giveArmies(armiesPerPlayer);
            playerArray.add(p);
        }
        return playerArray;
    }

    public int getPlayerWhoGoesFirst(int highestRoller) throws IllegalArgumentException {
        if (highestRoller >= 1 && highestRoller <= 6) {
            return highestRoller - 1;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static List<Territory> initTerritories() {
        List<Territory> territories = new ArrayList<>();

        String[][] territoryData = readCSVFile("/territoryData.csv", 6, 13);
        for (int i = 0; i < territoryData.length; i++) {
            for (int j = 1; j < territoryData[i].length; j++) {
                if (territoryData[i][j] == null) {
                    continue;
                }
                territories.add(new Territory(territoryData[i][j], territoryData[i][0]));
            }
        }
        addAdjacentTerritories(territories);
        return territories;
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

    private static String[][] readCSVFile(String filePath, int fileRows, int fileCols) {
        String[][] toReturn = new String[fileRows][fileCols];
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(GameSetup.class.getResourceAsStream(filePath),
                            StandardCharsets.UTF_8));
            String line;
            for (int i = 0; i < fileRows; i++) {
                if ((line = br.readLine()) == null) {
                    throw new Exception("Incorrect File Size");
                }
                String[] values = line.split(",");
                for (int j = 0; j < values.length; j++) {
                    toReturn[i][j] = values[j];
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
