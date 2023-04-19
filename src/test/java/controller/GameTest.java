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
    void testRemoveDefeatedPlayerFromGameLoopNegative1() {
        Game game = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        assertThrows(IllegalArgumentException.class, () -> game.removeDefeatedPlayer(-1));
    }


    @Test
    void testRemoveDefeatedPlayerFromGameLoop0() {
        Game game = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.removeDefeatedPlayer(0);
        assertEquals(game.playerController.players.size(), 5);
        assertEquals(game.playerController.getNumberOfPlayers(), 5);
    }

    @Test
    void testRemoveDefeatedPlayerFromGameLoop5() {
        Game game = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.removeDefeatedPlayer(5);
        assertEquals(game.playerController.players.size(), 5);
        assertEquals(game.playerController.getNumberOfPlayers(), 5);
    }

    @Test
    void testRemoveDefeatedPlayerFromGameLoop6() {
        Game game = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        assertThrows(IllegalArgumentException.class, () -> game.removeDefeatedPlayer(6));
    }

    @Test
    void testNextTurn() {
        Game game = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.playerController.currentPlayer = 0;
        game.nextPlayer();
        assertEquals(game.playerController.currentPlayer, 1);
    }

    @Test
    void testNextTurn1() {
        Game game = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.playerController.currentPlayer = 1;
        game.nextPlayer();
        assertEquals(game.playerController.currentPlayer, 2);
    }

    @Test
    void testNextTurn5() {
        Game game = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.playerController.currentPlayer = 5;
        game.nextPlayer();
        assertEquals(game.playerController.currentPlayer, 0);
    }

    @Test
    void testInitGamePlayerArray() {
        Game g = new Game(6, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        assertEquals(20, g.playerController.players.get(0).getArmiesAvailable());
    }

    @Test
    void testSetPlayers() {
        Player p1 = new Player(PlayerColor.BLACK, "AAAAAAAAAAAAAAAAA", new Random(), new CardTrader());
        Player p2 = new Player(PlayerColor.BLACK, "Marbo", new Random(), new CardTrader());
        ArrayList<Player> playerList = new ArrayList<Player>();
        playerList.add(p1);
        playerList.add(p2);
        Game g = new Game(2, Setup.defaultWorld(), playerList);
        String playerName1 = g.playerController.players.get(0).getName();
        String playerName2 = g.playerController.players.get(1).getName();
        assertTrue("AAAAAAAAAAAAAAAAA".equals(playerName1) || "AAAAAAAAAAAAAAAAA".equals(playerName2));
    }


    @Test
    void testDefaultPlayers(){
        Game g = new Game(2, Setup.defaultWorld(), Setup.fillPlayerArray(6));
        String playerName1 = g.playerController.players.get(0).getName();
        String playerName2 = g.playerController.players.get(1).getName();
        assertTrue("Colonel Mustard".equals(playerName1) || "Colonel Mustard".equals(playerName2));
    }


}
