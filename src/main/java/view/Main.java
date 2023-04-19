package view;

import controller.Game;
import model.Player;
import java.util.*;
import model.Map.World;


public class Main {


    public static void main(String[] args) {
        Setup.setupProperties();
        ResourceBundle bundle = Setup.selectLanguage();
        World world           = Setup.selectWorld(bundle);
        int numberOfPlayers   = Setup.selectNumberOfPlayers(bundle);
        List<Player> players  = Setup.setupPlayers(bundle, numberOfPlayers);

        Game game = new Game(world, players);
        game.setupUI(bundle, GameView.class);
        game.begin();
    }


}
