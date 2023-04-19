package controller;


import model.CardTrader;
import model.Player;
import model.PlayerColor;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    @Test
    void testNextTurn() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.playerController.currentPlayer = 0;
        game.nextPlayer();
        assertEquals(game.playerController.currentPlayer, 1);
    }

    @Test
    void testNextTurn1() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.playerController.currentPlayer = 1;
        game.nextPlayer();
        assertEquals(game.playerController.currentPlayer, 2);
    }

    @Test
    void testNextTurn5() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.playerController.currentPlayer = 5;
        game.nextPlayer();
        assertEquals(game.playerController.currentPlayer, 0);
    }

    @Test
    void testInitGamePlayerArray() {
        Game g = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
        assertEquals(20, g.playerController.players.get(0).getArmiesAvailable());
    }

    @Test
    void testSetPlayers() {
        Player p1 = new Player(PlayerColor.BLACK, "AAAAAAAAAAAAAAAAA", new Random(), new CardTrader());
        Player p2 = new Player(PlayerColor.BLACK, "Marbo", new Random(), new CardTrader());
        ArrayList<Player> playerList = new ArrayList<Player>();
        playerList.add(p1);
        playerList.add(p2);
        Game g = new Game(Setup.defaultWorld(), playerList);
        String playerName1 = g.playerController.players.get(0).getName();
        String playerName2 = g.playerController.players.get(1).getName();
        assertTrue("AAAAAAAAAAAAAAAAA".equals(playerName1) || "AAAAAAAAAAAAAAAAA".equals(playerName2));
    }


    @Test
    void testDefaultPlayers(){
        ArrayList<Player> players = Setup.fillPlayerArray(6);
        Game g = new Game(Setup.defaultWorld(), players);
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player player : g.playerController.getPlayers()) {
            playerNames.add(player.getName());
        }
        assertTrue(playerNames.contains("Colonel Mustard"));
    }


}
