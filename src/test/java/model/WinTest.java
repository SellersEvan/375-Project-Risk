package model;

import static org.junit.jupiter.api.Assertions.*;

import model.Map.Continent;
import model.Map.Territory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class WinTest {

    private void occupyTerritoriesSetup(Player player, int numTerritories) {
        player.giveArmies(numTerritories);
        for(int i = 0; i < numTerritories; i++) {
            Territory alreadyOwnsMock = EasyMock.strictMock(Territory.class);
            EasyMock.expect(alreadyOwnsMock.hasOccupant()).andReturn(false);
            alreadyOwnsMock.setOccupant(player);
            alreadyOwnsMock.addArmies(1);
            EasyMock.replay(alreadyOwnsMock);
            player.occupyTerritory(alreadyOwnsMock);
            EasyMock.verify(alreadyOwnsMock);
        }
    }
    @Test
    void testPlayerHasWonDomination1() {
        Player player = new Player(PlayerColor.RED, null, null);
        occupyTerritoriesSetup(player, 42);
        assertTrue(player.hasWon());
    }

    @Test
    void testPlayerHasWonDomination1Integration() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(42);
        Continent continent = EasyMock.mock(Continent.class);
        for(int i = 0; i < 42; i++) {
            Territory t = new Territory("Test", continent);
            player.occupyTerritory(t);
        }
        assertTrue(player.hasWon());
    }

    @Test
    void testPlayerHasWonDomination2() {
        Player player = new Player(PlayerColor.RED, null, null);
        occupyTerritoriesSetup(player, 40);
        assertFalse(player.hasWon());
    }

    @Test
    void testPlayerHasWonDomination2Integration() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(40);
        Continent continent = EasyMock.mock(Continent.class);
        for(int i = 0; i < 40; i++) {
            Territory t = new Territory("Test", continent);
            player.occupyTerritory(t);
        }
        assertFalse(player.hasWon());
    }
}
