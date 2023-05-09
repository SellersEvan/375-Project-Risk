package controller;


import model.CardTrader;
import model.InvalidAttackException;
import model.Map.Continent;
import model.Map.MapManager;
import model.Map.Territory;
import model.Map.World;
import model.Player;
import model.PlayerColor;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import view.GameView;

import java.util.*;

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
    static class ActionTests {
        Game game;
        Random randomMock;
        GameView uiMock;
        CardTrader cardTraderMock;
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

    }


    @Test
    void testGetBundle() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        assertEquals(game.bundle, game.getBundle());
    }


    @Test
    void testBegin() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.ui.setDetails(game.playerController.getCurrentPlayer().getName(), 20, "territoryClaim");
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);
        game.begin();
        assertEquals(Phase.territoryClaim, game.getPhase());
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionTerritoryClaim() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.territoryClaim;

        Player cPlayer = game.getCurrentPlayer();
        game.territoryAction(territory);
        assertEquals(cPlayer, territory.getOccupant());
        assertNotEquals(cPlayer, game.getCurrentPlayer());
    }


    @Test
    void testTerritoryActionPlaceArmies() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.placeArmies;

        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(5);
        game.ui.setDetails(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        Player cPlayer = game.getCurrentPlayer();
        cPlayer.occupyTerritory(territory);
        game.territoryAction(territory);
        assertEquals(cPlayer, territory.getOccupant());
        assertEquals(6, territory.getArmies());
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionPlaceArmiesNotOwner() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.placeArmies;

        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territory);
        assertEquals(0, territory.getArmies());
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionPlaceArmiesTooMany() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.placeArmies;

        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(100);
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        Player cPlayer = game.getCurrentPlayer();
        cPlayer.occupyTerritory(territory);
        game.territoryAction(territory);
        assertEquals(cPlayer, territory.getOccupant());
        assertEquals(1, territory.getArmies());
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionAttackNotOccupied() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;

        game.ui.selectTerritory();
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territory);
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionAttackNotEnoughArmies() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;

        Player cPlayer = game.getCurrentPlayer();
        cPlayer.occupyTerritory(territory);
        game.ui.selectTerritory();
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territory);
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionAttackFirst() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;

        Player cPlayer = game.getCurrentPlayer();
        cPlayer.occupyTerritory(territory);
        territory.addArmies(3);
        game.ui.selectTerritory(territory);
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territory);
        assertEquals(territory, game.territoryController.getSelectedTerritory());
        EasyMock.verify(game.ui);
    }


    @Test
    void testAttackLose() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryAttacker = territories.get(3);
        Territory territoryDefender = territories.get(4);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player attacker   = new Player(PlayerColor.RED, "Joe", random, trader);
        Player defender   = new Player(PlayerColor.GREEN, "Mama", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(attacker); add(defender); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;
        game.territoryController.setSelectedTerritory(territoryAttacker);

        attacker.occupyTerritory(territoryAttacker);
        defender.occupyTerritory(territoryDefender);
        territoryAttacker.addArmies(10);
        territoryDefender.addArmies(10);

        game.ui.selectTerritory(territoryAttacker, territoryDefender);
        game.ui.selectTerritory();
        EasyMock.expect(random.nextInt(6)).andReturn(3);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        game.ui.updateTerritoryButtons();
        game.ui.displayRolls(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(3);
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(2);
        EasyMock.expectLastCall();
        EasyMock.replay(random, game.ui);

        game.territoryAction(territoryDefender);
        assertNull(game.territoryController.getSelectedTerritory());
        EasyMock.verify(random, game.ui);
    }


    @Test
    void testAttackWin() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryAttacker = territories.get(3);
        Territory territoryDefender = territories.get(4);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player attacker   = new Player(PlayerColor.RED, "Joe", random, trader);
        Player defender   = new Player(PlayerColor.GREEN, "Mama", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(attacker); add(defender); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;
        game.territoryController.setSelectedTerritory(territoryAttacker);

        attacker.occupyTerritory(territoryAttacker);
        defender.occupyTerritory(territoryDefender);
        territoryAttacker.addArmies(10);
        territoryDefender.addArmies(1);

        game.ui.selectTerritory(territoryAttacker, territoryDefender);
        game.ui.selectTerritory();
        EasyMock.expect(random.nextInt(6)).andReturn(5);
        EasyMock.expect(random.nextInt(6)).andReturn(5);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        game.ui.updateTerritoryButtons();
        game.ui.displayRolls(EasyMock.anyObject(), EasyMock.anyObject());
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(5);
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(3);
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(2);
        EasyMock.expectLastCall();
        EasyMock.replay(random, game.ui);

        game.territoryAction(territoryDefender);
        assertNull(game.territoryController.getSelectedTerritory());
        assertEquals(1, game.playerController.getNumberOfPlayers());
        EasyMock.verify(random, game.ui);
    }


    @Test
    void testAttackWinInvalidMove() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryAttacker = territories.get(3);
        Territory territoryDefender = territories.get(4);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player attacker   = new Player(PlayerColor.RED, "Joe", random, trader);
        Player defender   = new Player(PlayerColor.GREEN, "Mama", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(attacker); add(defender); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;
        game.territoryController.setSelectedTerritory(territoryAttacker);

        attacker.occupyTerritory(territoryAttacker);
        defender.occupyTerritory(territoryDefender);
        territoryAttacker.addArmies(10);
        territoryDefender.addArmies(1);

        game.ui.selectTerritory(territoryAttacker, territoryDefender);
        game.ui.selectTerritory();
        EasyMock.expect(random.nextInt(6)).andReturn(5);
        EasyMock.expect(random.nextInt(6)).andReturn(5);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        game.ui.updateTerritoryButtons();
        game.ui.displayRolls(EasyMock.anyObject(), EasyMock.anyObject());
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(-1);
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(5);
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(3);
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(2);
        EasyMock.expectLastCall();
        EasyMock.replay(random, game.ui);

        game.territoryAction(territoryDefender);
        assertNull(game.territoryController.getSelectedTerritory());
        assertEquals(1, game.playerController.getNumberOfPlayers());
        EasyMock.verify(random, game.ui);
    }


    @Test
    void testAttackWinInvalidMove2() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryAttacker = territories.get(3);
        Territory territoryDefender = territories.get(4);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player attacker   = new Player(PlayerColor.RED, "Joe", random, trader);
        Player defender   = new Player(PlayerColor.GREEN, "Mama", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(attacker); add(defender); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;
        game.territoryController.setSelectedTerritory(territoryAttacker);

        attacker.occupyTerritory(territoryAttacker);
        defender.occupyTerritory(territoryDefender);
        territoryAttacker.addArmies(10);
        territoryDefender.addArmies(1);

        game.ui.selectTerritory(territoryAttacker, territoryDefender);
        game.ui.selectTerritory();
        EasyMock.expect(random.nextInt(6)).andReturn(5);
        EasyMock.expect(random.nextInt(6)).andReturn(5);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        EasyMock.expect(random.nextInt(6)).andReturn(1);
        game.ui.updateTerritoryButtons();
        game.ui.displayRolls(EasyMock.anyObject(), EasyMock.anyObject());
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(100);
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(5);
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(3);
        EasyMock.expect(game.ui.getNumberOfDice(EasyMock.anyInt(), EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(2);
        EasyMock.expectLastCall();
        EasyMock.replay(random, game.ui);

        game.territoryAction(territoryDefender);
        assertNull(game.territoryController.getSelectedTerritory());
        assertEquals(1, game.playerController.getNumberOfPlayers());
        EasyMock.verify(random, game.ui);
    }


    @Test
    void testAttackSame() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryAttacker = territories.get(3);
        Territory territoryDefender = territories.get(4);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player attacker   = new Player(PlayerColor.RED, "Joe", random, trader);
        Player defender   = new Player(PlayerColor.GREEN, "Mama", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(attacker); add(defender); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;
        game.territoryController.setSelectedTerritory(territoryAttacker);

        attacker.occupyTerritory(territoryAttacker);
        attacker.occupyTerritory(territoryDefender);
        territoryAttacker.addArmies(10);
        territoryDefender.addArmies(10);

        game.ui.selectTerritory(territoryAttacker, territoryDefender);
        game.ui.selectTerritory();
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui, random);

        game.territoryAction(territoryDefender);
        EasyMock.verify(random, game.ui);
    }


    @Test
    void testAttackNotAdjacent() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryAttacker = territories.get(3);
        Territory territoryDefender = territories.get(30);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player attacker   = new Player(PlayerColor.RED, "Joe", random, trader);
        Player defender   = new Player(PlayerColor.GREEN, "Mama", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(attacker); add(defender); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.attacking;
        game.territoryController.setSelectedTerritory(territoryAttacker);

        attacker.occupyTerritory(territoryAttacker);
        defender.occupyTerritory(territoryDefender);
        territoryAttacker.addArmies(10);
        territoryDefender.addArmies(10);

        game.ui.selectTerritory(territoryAttacker, territoryDefender);
        game.ui.selectTerritory();
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui, random);

        game.territoryAction(territoryDefender);
        EasyMock.verify(random, game.ui);
    }


    @Test
    void testTerritoryActionFortifyNotOccupied() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.fortifying;

        game.ui.selectTerritory();
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territory);
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionFortifyNotEnoughArmies() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.fortifying;

        Player cPlayer = game.getCurrentPlayer();
        cPlayer.occupyTerritory(territory);
        game.ui.selectTerritory();
        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territory);
        EasyMock.verify(game.ui);
    }


    @Test
    void testTerritoryActionFortifyFirst() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territory = territories.get(3);
        Game game = new Game(world, Setup.fillPlayerArray(6));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.fortifying;

        Player cPlayer = game.getCurrentPlayer();
        cPlayer.occupyTerritory(territory);
        territory.addArmies(3);
        game.ui.selectTerritory(territory);
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territory);
        assertEquals(territory, game.territoryController.getSelectedTerritory());
        EasyMock.verify(game.ui);
    }


    @Test
    void testFortifyValid() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryFrom = territories.get(3);
        Territory territoryTo = territories.get(4);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player player     = new Player(PlayerColor.RED, "Joe", random, trader);
        Player playerAlt  = new Player(PlayerColor.RED, "Joe2", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(player); add(playerAlt); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.fortifying;
        game.territoryController.setSelectedTerritory(territoryFrom);

        player.occupyTerritory(territoryFrom);
        player.occupyTerritory(territoryTo);
        territoryFrom.addArmies(10);
        territoryTo.addArmies(1);

        game.ui.selectTerritory(territoryFrom, territoryTo);
        EasyMock.expect(game.ui.getNumber(EasyMock.anyString())).andReturn(5);
        game.ui.selectTerritory();
        game.ui.setDetails(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyString());
        game.ui.setDetails(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyString());
        game.ui.updateTerritoryButtons();
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territoryTo);
        assertNull(game.territoryController.getSelectedTerritory());
        assertEquals(7, territoryTo.getArmies());
        assertEquals(6, territoryFrom.getArmies());
        assertEquals(2, game.playerController.getNumberOfPlayers());
        EasyMock.verify(game.ui);
    }


    @Test
    void testFortifyDifferentOccupant() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryFrom = territories.get(3);
        Territory territoryTo = territories.get(4);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player player     = new Player(PlayerColor.RED, "Joe", random, trader);
        Player playerAlt  = new Player(PlayerColor.RED, "Joe2", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(player); add(playerAlt); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.fortifying;
        game.territoryController.setSelectedTerritory(territoryFrom);

        player.occupyTerritory(territoryFrom);
        playerAlt.occupyTerritory(territoryTo);
        territoryFrom.addArmies(10);
        territoryTo.addArmies(1);

        game.ui.selectTerritory(territoryFrom, territoryTo);
        game.ui.showMessage(EasyMock.anyString());
        game.ui.selectTerritory();
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territoryTo);
        assertNull(game.territoryController.getSelectedTerritory());
        assertEquals(2, territoryTo.getArmies());
        assertEquals(11, territoryFrom.getArmies());
        assertEquals(2, game.playerController.getNumberOfPlayers());
        EasyMock.verify(game.ui);
    }


    @Test
    void testFortifyNotAdjacent() throws InvalidAttackException {
        World world = Setup.defaultWorld();
        List<Territory> territories = world.getTerritories();
        Territory territoryFrom = territories.get(3);
        Territory territoryTo = territories.get(20);

        CardTrader trader = EasyMock.mock(CardTrader.class);
        Random random     = EasyMock.mock(Random.class);
        Player player     = new Player(PlayerColor.RED, "Joe", random, trader);
        Player playerAlt  = new Player(PlayerColor.RED, "Joe2", random, trader);

        Game game = new Game(world, new ArrayList<Player>(){{ add(player); add(playerAlt); }});
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.fortifying;
        game.territoryController.setSelectedTerritory(territoryFrom);

        player.occupyTerritory(territoryFrom);
        player.occupyTerritory(territoryTo);
        territoryFrom.addArmies(10);
        territoryTo.addArmies(1);

        game.ui.selectTerritory(territoryFrom, territoryTo);
        game.ui.showMessage(EasyMock.anyString());
        game.ui.selectTerritory();
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.territoryAction(territoryTo);
        assertNull(game.territoryController.getSelectedTerritory());
        assertEquals(2, territoryTo.getArmies());
        assertEquals(11, territoryFrom.getArmies());
        assertEquals(2, game.playerController.getNumberOfPlayers());
        EasyMock.verify(game.ui);
    }


    @Test
    void testPhaseActionTerritoryClaim() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(2));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.territoryClaim;

        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.phaseAction();
        EasyMock.verify(game.ui);
    }


    @Test
    void testPhaseActionInitialArmies() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(2));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.initialArmies;

        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.phaseAction();
        EasyMock.verify(game.ui);
    }


    @Test
    void testPhaseActionPlaceArmies() {
        Game game = new Game(Setup.defaultWorld(), Setup.fillPlayerArray(2));
        game.bundle = ResourceBundle.getBundle("messages_en");
        game.ui     = EasyMock.mock(GameView.class);
        game.phase  = Phase.placeArmies;

        game.ui.showMessage(EasyMock.anyString());
        EasyMock.expectLastCall();
        EasyMock.replay(game.ui);

        game.phaseAction();
        EasyMock.verify(game.ui);
    }





}
