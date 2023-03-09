package controller;


import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    @Test
    void testRemoveDefeatedPlayerFromGameLoopNegative1() {
        Game game = new Game(6);
        assertThrows(IllegalArgumentException.class, () -> game.removeDefeatedPlayer(-1));
    }

    @Test
    void testRemoveDefeatedPlayerFromGameLoop0() {
        Game game = new Game(6);
        game.removeDefeatedPlayer(0);
        assertEquals(game.playerArray.size(), 5);
        assertEquals(game.numberOfPlayers, 5);
    }

    @Test
    void testRemoveDefeatedPlayerFromGameLoop5() {
        Game game = new Game(6);
        game.removeDefeatedPlayer(5);
        assertEquals(game.playerArray.size(), 5);
        assertEquals(game.numberOfPlayers, 5);
    }

    @Test
    void testRemoveDefeatedPlayerFromGameLoop6() {
        Game game = new Game(6);
        assertThrows(IllegalArgumentException.class, () -> game.removeDefeatedPlayer(6));
    }

    @Test
    void testSetFirstPlayer() {
        Game game = new Game(6);
        Random r = EasyMock.mock(Random.class);
        EasyMock.expect(r.nextInt(6)).andReturn(3);
        EasyMock.replay(r);
        game.setFirstPlayer(r);
        assertEquals(game.current, 3);
        EasyMock.verify(r);
    }

    @Test
    void testNextTurn() {
        Game game = new Game(6);
        game.current = 0;
        game.nextPlayer();
        assertEquals(game.current, 1);
    }

    @Test
    void testNextTurn1() {
        Game game = new Game(6);
        game.current = 1;
        game.nextPlayer();
        assertEquals(game.current, 2);
    }

    @Test
    void testNextTurn5() {
        Game game = new Game(6);
        game.current = 5;
        game.nextPlayer();
        assertEquals(game.current, 0);
    }

    @Test
    void testInitGamePlayerArray() {
        Game g = new Game(6);
        assertEquals(g.gameSetup.getArmiesPerPlayer(), g.playerArray.get(0).getArmiesAvailable());
    }

}
