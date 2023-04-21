package model;

import static org.junit.jupiter.api.Assertions.*;

import model.Map.Continent;
import model.Map.Territory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private List<Continent> setupContinentsForSecretMissions() {
        List<Continent> continents = new ArrayList<>();
        // Create 5 continents, each with 2 territories
        for (int i = 0; i < 5; i++) {
            Continent continent = new Continent("Test", 0);
            new Territory("Test", continent);
            new Territory("Test", continent);
            continents.add(continent);
        }
        return continents;
    }
    @Test
    void testPlayerDoesNotWinSecretMission1() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(10);
        List<Continent> testContinents = this.setupContinentsForSecretMissions();
        Random randomMock = EasyMock.strictMock(Random.class);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(0);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(0);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(1);
        EasyMock.replay(randomMock);
        player.setWinCondition(new SecretMissionWin(player, testContinents, randomMock));

        player.occupyTerritory((Territory) testContinents.get(0).territories.toArray()[0]);

        assertFalse(player.hasWon());
        EasyMock.verify(randomMock);
    }

    @Test
    void testPlayerDoesNotWinSecretMission2() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(10);
        List<Continent> testContinents = this.setupContinentsForSecretMissions();
        Random randomMock = EasyMock.strictMock(Random.class);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(0);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(1);
        EasyMock.replay(randomMock);
        player.setWinCondition(new SecretMissionWin(player, testContinents, randomMock));

        // Player controls one continent but not the other
        player.occupyTerritory((Territory) testContinents.get(0).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(0).territories.toArray()[1]);

        assertFalse(player.hasWon());
        EasyMock.verify(randomMock);
    }

    @Test
    void testPlayerDoesWinSecretMission1() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(10);
        List<Continent> testContinents = this.setupContinentsForSecretMissions();
        Random randomMock = EasyMock.strictMock(Random.class);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(0);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(1);
        EasyMock.replay(randomMock);
        player.setWinCondition(new SecretMissionWin(player, testContinents, randomMock));

        // Player controls both target continents
        player.occupyTerritory((Territory) testContinents.get(0).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(0).territories.toArray()[1]);

        player.occupyTerritory((Territory) testContinents.get(1).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(1).territories.toArray()[1]);

        assertTrue(player.hasWon());
        EasyMock.verify(randomMock);
    }

    @Test
    void testPlayerDoesWinSecretMission2() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(10);
        List<Continent> testContinents = this.setupContinentsForSecretMissions();
        Random randomMock = EasyMock.strictMock(Random.class);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(4);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(3);
        EasyMock.replay(randomMock);
        player.setWinCondition(new SecretMissionWin(player, testContinents, randomMock));

        // Player controls both target continents as well as a non-target continent
        player.occupyTerritory((Territory) testContinents.get(0).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(0).territories.toArray()[1]);

        player.occupyTerritory((Territory) testContinents.get(3).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(3).territories.toArray()[1]);

        player.occupyTerritory((Territory) testContinents.get(4).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(4).territories.toArray()[1]);

        assertTrue(player.hasWon());
        EasyMock.verify(randomMock);
    }

    @Test
    void testPlayerDoesNotWinSecretMissionControlsWrongContinents() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(10);
        List<Continent> testContinents = this.setupContinentsForSecretMissions();
        Random randomMock = EasyMock.strictMock(Random.class);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(0);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(1);
        EasyMock.replay(randomMock);
        player.setWinCondition(new SecretMissionWin(player, testContinents, randomMock));

        // Player controls two continents, but one is not a target
        player.occupyTerritory((Territory) testContinents.get(2).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(2).territories.toArray()[1]);

        player.occupyTerritory((Territory) testContinents.get(1).territories.toArray()[0]);
        player.occupyTerritory((Territory) testContinents.get(1).territories.toArray()[1]);

        assertFalse(player.hasWon());
        EasyMock.verify(randomMock);
    }

    @Test
    void testPlayerDoesNotWinSecretMissionOnlyPartialControl() {
        Player player = new Player(PlayerColor.RED, null, null);
        player.giveArmies(10);
        List<Continent> testContinents = this.setupContinentsForSecretMissions();
        Random randomMock = EasyMock.strictMock(Random.class);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(0);
        EasyMock.expect(randomMock.nextInt(testContinents.size())).andReturn(1);
        EasyMock.replay(randomMock);
        player.setWinCondition(new SecretMissionWin(player, testContinents, randomMock));

        // Player controls only partially controls each continent
        player.occupyTerritory((Territory) testContinents.get(2).territories.toArray()[0]);

        player.occupyTerritory((Territory) testContinents.get(1).territories.toArray()[0]);

        assertFalse(player.hasWon());
        EasyMock.verify(randomMock);
    }


}
