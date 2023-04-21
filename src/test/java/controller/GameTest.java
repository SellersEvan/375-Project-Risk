package controller;


import model.CardTrader;
import model.InvalidAttackException;
import model.Map.Continent;
import model.Map.MapManager;
import model.Map.Territory;
import model.Player;
import model.PlayerColor;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import view.GameView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
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

    @Nested
    class actionTests {
        Game game;
        Random randomMock;
        GameView uiMock;
        CardTrader cardTraderMock;
        Continent asia;
        Continent northAmerica;
        Territory attackingTerritory;
        Territory defendingTerritory;
        Territory otherTerritory;
        Player attacker;
        Player defender;

        @BeforeEach
        void setUp(){
            game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
            randomMock = EasyMock.strictMock(Random.class);
            EasyMock.expect(randomMock.nextInt(EasyMock.anyInt())).andReturn(-1).anyTimes();
            uiMock = EasyMock.strictMock(GameView.class);
            game.ui = uiMock;
            cardTraderMock = new CardTrader();

            attackingTerritory = MapManager.getInstance().getTerritories().get(0);   // Alaska (North America)
            defendingTerritory = MapManager.getInstance().getTerritories().get(1);   // Northwest_Terr (North America)
            otherTerritory     = MapManager.getInstance().getTerritories().get(4);  // Ontario (North    America)
            attacker = new Player(PlayerColor.RED, "Joe", randomMock, cardTraderMock);
            defender = new Player(PlayerColor.GREEN, "Mama", randomMock, cardTraderMock);
            attacker.giveArmies(5);
            defender.giveArmies(5);
            attacker.occupyTerritory(attackingTerritory);
            defender.occupyTerritory(defendingTerritory);
            defender.occupyTerritory(otherTerritory);
        }

        @Test
        void testAttack() throws InvalidAttackException {
            attacker.addArmiesToTerritory(attackingTerritory, 3);
            defender.addArmiesToTerritory(defendingTerritory, 1);

            EasyMock.expect(uiMock.getNumberOfDice(4, attacker.getName(), true)).andReturn(3);
            EasyMock.expect(uiMock.getNumberOfDice(2, defender.getName(), false)).andReturn(2);
            uiMock.displayRolls(new int[]{0,0,0}, new int[]{0,0});
            EasyMock.expectLastCall();
            uiMock.updateTerritoryButtons();
            EasyMock.expectLastCall();
            EasyMock.replay(uiMock, randomMock);

            game.attack(attackingTerritory, defendingTerritory);
            EasyMock.verify(uiMock);
            assertEquals(2, defendingTerritory.getArmies());
            assertEquals(2, attackingTerritory.getArmies());
        }

        @Test
        void testFortify() {
            defender.addArmiesToTerritory(otherTerritory, 1);
            EasyMock.replay(uiMock);
            otherTerritory.fortifyTerritory(defendingTerritory, 1);
            EasyMock.verify(uiMock);
            assertEquals(2, defendingTerritory.getArmies());
            assertEquals(1, otherTerritory.getArmies());
        }
        @Test
        void testInvalidFortify() {
            defender.addArmiesToTerritory(otherTerritory, 1);
            EasyMock.replay(uiMock);
            defendingTerritory.fortifyTerritory(otherTerritory, 1);
            EasyMock.verify(uiMock);
            assertEquals(1, defendingTerritory.getArmies());
            assertEquals(2, otherTerritory.getArmies());
        }
        @Test
        void testPhaseAction() {
            //tradecards
            //attacking
            //fortifying
        }

        @Test
        void testClaimTerritory() {

        }

        @Test
        void testPlaceArmies() {

        }
    }
}
