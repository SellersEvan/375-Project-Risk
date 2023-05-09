package controller;

import model.Map.Continent;
import model.Map.Territory;
import model.Player;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {


    @Test
    void testGetContinent() {
        List<Continent> continents = Setup.defaultWorld().getContinents();
        ContinentController controller = new ContinentController(continents);
        assertEquals(continents, controller.getContinents());
    }


    @Test
    void testGetTerritories() {
        List<Territory> territories = Setup.defaultWorld().getTerritories();
        TerritoryController controller = new TerritoryController(territories);
        assertEquals(territories, controller.getTerritories());
    }


    @Test
    void testSelectTerritory() {
        List<Territory> territories = Setup.defaultWorld().getTerritories();
        TerritoryController controller = new TerritoryController(territories);
        controller.setSelectedTerritory(territories.get(4));
        assertEquals(territories.get(4), controller.getSelectedTerritory());
    }


    @Test
    void testUnselectTerritory() {
        List<Territory> territories = Setup.defaultWorld().getTerritories();
        TerritoryController controller = new TerritoryController(territories);
        controller.setSelectedTerritory(territories.get(4));
        controller.setSelectedTerritory(null);
        assertNull(controller.getSelectedTerritory());
    }


    @Test
    void testNoneClaimedTerritory() {
        List<Territory> territories = Setup.defaultWorld().getTerritories();
        TerritoryController controller = new TerritoryController(territories);
        assertFalse(controller.allTerritoriesClaimed());
    }


    @Test
    void testSomeClaimedTerritory() {
        List<Territory> territories = Setup.defaultWorld().getTerritories();
        TerritoryController controller = new TerritoryController(territories);
        Player player = EasyMock.mock(Player.class);
        int i = 0;
        for (Territory territory : territories) {
            if (i % 2 == 0)
                territory.setOccupant(player);
            i += 1;
        }
        assertFalse(controller.allTerritoriesClaimed());
    }


    @Test
    void testAllClaimedTerritory() {
        List<Territory> territories = Setup.defaultWorld().getTerritories();
        TerritoryController controller = new TerritoryController(territories);
        Player player = EasyMock.mock(Player.class);
        for (Territory territory : territories) {
            territory.setOccupant(player);
        }
        assertTrue(controller.allTerritoriesClaimed());
    }


    @Test
    void testSetupPlayer6() {
        List<Player> players = Setup.fillPlayerArray(6);
        new Game(Setup.defaultWorld(), players);
        for (Player player : players) {
            assertEquals(20, player.getArmiesAvailable());
        }
    }


    @Test
    void testSetupPlayer3() {
        List<Player> players = Setup.fillPlayerArray(3);
        new Game(Setup.defaultWorld(), players);
        for (Player player : players) {
            assertEquals(35, player.getArmiesAvailable());
        }
    }


    @Test
    void testCurrentPlayer() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        players = controller.getPlayers();
        assertEquals(players.get(0), controller.getCurrentPlayer());
    }


    @Test
    void testNextPlayer1() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        players = controller.getPlayers();
        controller.nextPlayer();
        assertEquals(players.get(1), controller.getCurrentPlayer());
    }


    @Test
    void testNextPlayer2() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        players = controller.getPlayers();
        controller.nextPlayer();
        controller.nextPlayer();
        assertEquals(players.get(2), controller.getCurrentPlayer());
    }


    @Test
    void testNextPlayer3() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        players = controller.getPlayers();
        controller.nextPlayer();
        controller.nextPlayer();
        controller.nextPlayer();
        assertEquals(players.get(0), controller.getCurrentPlayer());
    }


    @Test
    void testAmountPlayer2() {
        List<Player> players = Setup.fillPlayerArray(2);
        PlayerController controller = new PlayerController(players);
        assertEquals(2, controller.getNumberOfPlayers());
    }


    @Test
    void testAmountPlayer6() {
        List<Player> players = Setup.fillPlayerArray(6);
        PlayerController controller = new PlayerController(players);
        assertEquals(6, controller.getNumberOfPlayers());
    }


    @Test
    void testRemovePlayer() {
        List<Player> players = Setup.fillPlayerArray(6);
        PlayerController controller = new PlayerController(players);
        controller.removePlayer(players.get(2));
        assertEquals(5, controller.getNumberOfPlayers());
    }


    @Test
    void testCurrentIndexPlayer3() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        players = controller.getPlayers();
        assertEquals(0, controller.getIndexOfPlayer(players.get(0)));
    }


    @Test
    void testCurrentIndexPlayer6() {
        List<Player> players = Setup.fillPlayerArray(6);
        PlayerController controller = new PlayerController(players);
        players = controller.getPlayers();
        assertEquals(4, controller.getIndexOfPlayer(players.get(4)));
    }


    @Test
    void testCurrentPlayerArmies() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        assertEquals(35, controller.getArmiesAvailableForCurrentPlayer());
    }


    @Test
    void testCurrentPlayerCards0() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        assertEquals(0, controller.getNumberOfCardForCurrentPlayer());
    }


    @Test
    void testClaimTerritoryPlayer() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        Territory territory = new Territory("Foo", new Continent("Boo", 3));
        controller.setPlayerOccupyTerritory(territory);
        assertTrue(territory.hasOccupant());
    }


    @Test
    void testAddArmiesTerritoryPlayer() {
        List<Player> players = Setup.fillPlayerArray(3);
        PlayerController controller = new PlayerController(players);
        Territory territory = new Territory("Foo", new Continent("Boo", 3));
        controller.setPlayerOccupyTerritory(territory);
        controller.addArmiesToTerritoryForCurrentPlayer(territory, 2);
        assertEquals(3, territory.getArmies());
    }




}
