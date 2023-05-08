package view;

import controller.Game;
import model.*;

import java.util.*;
import model.Map.World;


public class Main {


    public static final boolean DEVELOPER_MODE = false;


    public static void main(String[] args) {
        if (!DEVELOPER_MODE) {
            setupProduction();
        } else {
            setDevelopment();
        }
    }


    private static void setupProduction() {
        Setup.setupProperties();
        ResourceBundle bundle = Setup.selectLanguage();
        World world           = Setup.selectWorld(bundle);
        int numberOfPlayers   = Setup.selectNumberOfPlayers(bundle);
        List<Player> players  = Setup.setupPlayers(bundle, numberOfPlayers);
        boolean enableSecretMission = Setup.selectSecretMissionMode(bundle);

        if (enableSecretMission) {
            for (Player p : players) {
                p.setWinCondition(new SecretMissionWin(p, world.getContinents(), new Random()));
            }
            Setup.displaySecretMissions(bundle, players);
        }

        Game game = new Game(world, players);
        game.setupUI(bundle, GameView.class);
        game.begin();
    }


    private static void setDevelopment() {
        Setup.setupProperties();
        ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
        World world           = new World(World.getMapFiles().get("Earth"));

        Random random         = new Random();
        CardTrader cardTrader = new CardTrader();
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(PlayerColor.GREEN, "Mr. Green", random, cardTrader));
        players.add(new Player(PlayerColor.BLUE, "Mrs. Peacock", random, cardTrader));

        Game game = new Game(world, players);
        game.setupUI(bundle, GameView.class);
        game.begin();
    }


}
