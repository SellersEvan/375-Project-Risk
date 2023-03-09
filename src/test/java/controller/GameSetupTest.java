package controller;

import model.Player;
import model.Territory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameSetupTest {

    @Test
    void testGameSetup1() {
        GameSetup g = new GameSetup(2);
        assertEquals(2,g.numberOfPlayers);
    }
    @Test
    void testGameSetup6() {
        GameSetup g = new GameSetup(6);
        assertEquals(6,g.numberOfPlayers);
    }



    @Test
    void testSetInitialTroops1() {
        GameSetup g = new GameSetup(1);
        assertThrows(IllegalArgumentException.class,
                g::setInitialArmies);
    }

    @Test
    void testSetInitialTroops2() {
        GameSetup g = new GameSetup(2);
        g.setInitialArmies();
        assertEquals(40, g.getArmiesPerPlayer());
    }

    @Test
    void testSetInitialTroops6() {
        GameSetup g = new GameSetup(6);
        g.setInitialArmies();
        assertEquals(20, g.getArmiesPerPlayer());
    }

    @Test
    void testSetInitialTroops7() {
        GameSetup g = new GameSetup(7);
        assertThrows(IllegalArgumentException.class,
                g::setInitialArmies);
    }

    @Test
    void testFillPlayerArray3() {
        GameSetup g = new GameSetup(6);

        assertFalse(g.fillPlayerArray(null).isEmpty());
    }

    @Test
    void testDistinctPlayerColors() {
        GameSetup g = new GameSetup(6);
        ArrayList<Player> playerArray = g.fillPlayerArray(null);
        assertNotEquals(playerArray.get(1).getColor(), playerArray.get(2).getColor());
    }

    @Test
    void testSetPlayerWhoGoesFirst() {
        GameSetup g = new GameSetup(6);
        assertThrows(IllegalArgumentException.class, () -> g.getPlayerWhoGoesFirst(0));
    }
    @Test
    void testSetPlayerWhoGoesFirst1() {
        GameSetup g = new GameSetup(6);
        assertEquals(0, g.getPlayerWhoGoesFirst(1));
    }
    @Test
    void testSetPlayerWhoGoesFirst6() {
        GameSetup g = new GameSetup(6);
        assertEquals(5, g.getPlayerWhoGoesFirst(6));
    }
    @Test
    void testSetPlayerWhoGoesFirst7() {
        GameSetup g = new GameSetup(6);
        assertThrows(IllegalArgumentException.class, () -> g.getPlayerWhoGoesFirst(7));
    }
    @Test
    public void testInitTerritories(){
        List<Territory> territoryList = GameSetup.initTerritories();

        assertEquals("Territory Name: Alaska, Continent: North America", territoryList.get(0).toString());
        assertEquals("Territory Name: Argentina, Continent: South America", territoryList.get(12).toString());
        assertEquals("Territory Name: Western Australia, Continent: Australia", territoryList.get(22).toString());
        assertEquals("Territory Name: Southern Europe, Continent: Europe", territoryList.get(41).toString());
    }

    @Test
    public void testAlaskaAdjacency(){
        List<Territory> allTerritories = GameSetup.initTerritories();
        assertTrue(allTerritories.get(0).isAdjacent(allTerritories.get(1)));
        assertTrue(allTerritories.get(0).isAdjacent(allTerritories.get(3)));
        assertTrue(allTerritories.get(0).isAdjacent(allTerritories.get(25)));
        assertFalse(allTerritories.get(0).isAdjacent(allTerritories.get(0)));
        assertFalse(allTerritories.get(0).isAdjacent(allTerritories.get(4)));
    }

    @Test
    public void testIndonesiaAdjacency(){
        List<Territory> allTerritories = GameSetup.initTerritories();
        assertTrue(allTerritories.get(19).isAdjacent(allTerritories.get(20)));
        assertTrue(allTerritories.get(19).isAdjacent(allTerritories.get(22)));
        assertTrue(allTerritories.get(19).isAdjacent(allTerritories.get(34)));
        assertFalse(allTerritories.get(19).isAdjacent(allTerritories.get(19)));
        assertFalse(allTerritories.get(19).isAdjacent(allTerritories.get(21)));
    }

    @Test
    public void testRussiaAdjacency(){
        List<Territory> allTerritories = GameSetup.initTerritories();
        assertTrue(allTerritories.get(37).isAdjacent(allTerritories.get(26)));
        assertTrue(allTerritories.get(37).isAdjacent(allTerritories.get(28)));
        assertTrue(allTerritories.get(37).isAdjacent(allTerritories.get(32)));
        assertTrue(allTerritories.get(37).isAdjacent(allTerritories.get(36)));
        assertTrue(allTerritories.get(37).isAdjacent(allTerritories.get(39)));
        assertTrue(allTerritories.get(37).isAdjacent(allTerritories.get(41)));
        assertFalse(allTerritories.get(37).isAdjacent(allTerritories.get(40)));
    }

}
