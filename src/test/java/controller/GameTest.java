package controller;


import model.CardTrader;
import model.Map.Continent;
import model.Map.Territory;
import model.Player;
import model.PlayerColor;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import view.GameView;

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
            uiMock = EasyMock.strictMock(GameView.class);
            game.ui = uiMock;
            cardTraderMock = new CardTrader();

            asia = new Continent("Asia", 5, 1);
            northAmerica = new Continent("North America", 5, 1);
            attackingTerritory = new Territory("attackingTerritory", asia);
            defendingTerritory = new Territory("defendingTerritory", asia);
            otherTerritory = new Territory("otherTerritory", northAmerica);
            attackingTerritory.addAdjacentTerritory(defendingTerritory);
            attacker = new Player(PlayerColor.RED, "Joe", randomMock, cardTraderMock);
            defender = new Player(PlayerColor.GREEN, "Mama", randomMock, cardTraderMock);
            attacker.occupyTerritory(attackingTerritory);
            defender.occupyTerritory(defendingTerritory);
            defender.occupyTerritory(otherTerritory);
        }

        @Test
        void testAttack() {
            attacker.giveArmies(5);
            defender.giveArmies(2);
            attacker.addArmiesToTerritory(attackingTerritory, 3);
            defender.addArmiesToTerritory(defendingTerritory, 1);

            EasyMock.expect(uiMock.getNumberOfDice(2, attacker.getName(), true)).andReturn(2);
            EasyMock.expect(uiMock.getNumberOfDice(1, defender.getName(), false)).andReturn(1);
            EasyMock.replay(uiMock);

            try {
                game.attack(attackingTerritory, defendingTerritory);
            } catch (Exception e) {
                fail();
            }
            EasyMock.verify(uiMock);
        }

        @Test
        void testFortify() {

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
