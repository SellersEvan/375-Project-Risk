package controller;


import model.CardTrader;
import model.Map.Continent;
import model.Map.Territory;
import model.Player;
import model.PlayerColor;
import org.easymock.EasyMock;
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

    @Test
    void testAttack(){
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
        Random randomMock = EasyMock.strictMock(Random.class);
        GameView uiMock = EasyMock.strictMock(GameView.class);
        game.ui = uiMock;
        Continent asia = new Continent("Asia", 5, 1);
        Continent northAmerica = new Continent("North America", 5, 1);
        CardTrader cardTrader = new CardTrader();

        Territory attackingTerritory = new Territory("attackingTerritory", asia);
        Territory defendingTerritory = new Territory("defendingTerritory", asia);
        Territory otherTerritory = new Territory("otherTerritory", northAmerica);
        attackingTerritory.addAdjacentTerritory(defendingTerritory);

        Player aggressor = new Player(PlayerColor.RED, "Joe", randomMock, cardTrader);
        Player defender = new Player(PlayerColor.GREEN, "Mama", randomMock, cardTrader);
        aggressor.giveArmies(5);
        defender.giveArmies(2);
        aggressor.occupyTerritory(attackingTerritory);
        aggressor.addArmiesToTerritory(attackingTerritory, 3);
        defender.occupyTerritory(defendingTerritory);
        defender.addArmiesToTerritory(defendingTerritory, 1);
        defender.occupyTerritory(otherTerritory);

        EasyMock.expect(uiMock.getNumberOfDice(2, aggressor.getName(), true)).andReturn(2);
        EasyMock.expect(uiMock.getNumberOfDice(1, defender.getName(), false)).andReturn(1);
        EasyMock.replay(uiMock);
        try {
            game.attack(attackingTerritory, defendingTerritory);
        } catch(Exception e){
            fail();
        }
        EasyMock.verify(uiMock);

    }

    @Test
    void testForify(){

    }

    @Test
    void testPhaseAction(){
        //tradecards
        //attacking
        //fortifying
    }

    @Test
    void testClaimTerritory(){
       
    }

    @Test
    void testPlaceArmies(){

    }
}
