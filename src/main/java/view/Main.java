package view;

import controller.Game;
import model.CardTrader;
import model.Player;
import java.util.*;
import model.Map.World;
import model.PlayerColor;


public class Main {


    private static final boolean SETUP_MODE_PRODUCTION = true;


    public static void main(String[] args) {
        if (SETUP_MODE_PRODUCTION) {
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
        System.out.println(enableSecretMission);

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
