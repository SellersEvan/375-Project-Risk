package controller;

import model.CardTrader;
import model.Player;
import model.PlayerColor;
import model.Map.Territory;

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


}
