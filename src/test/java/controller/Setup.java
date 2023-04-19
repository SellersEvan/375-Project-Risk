package controller;

import model.CardTrader;
import model.Map.World;
import model.Player;
import model.PlayerColor;

import java.util.ArrayList;
import java.util.Random;

public class Setup {


    public static ArrayList<Player> defaultPlayers() {
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


    public static ArrayList<Player> fillPlayerArray(int numberOfPlayers) {
        ArrayList<Player> usualSuspects = defaultPlayers();
        ArrayList<Player> results = new ArrayList<Player>();
        for (int count = 0; count < numberOfPlayers; count++) {
            results.add(usualSuspects.get(count));
        }
        return results;
    }


    public static World defaultWorld() {
        return new World(World.getMapFiles().get("Earth"));
    }


}
