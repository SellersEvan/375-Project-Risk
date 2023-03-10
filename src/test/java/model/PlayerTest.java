package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import controller.GameSetup;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

class PlayerTest {

	private void occupyTerritoriesSetup(Player player, int numTerritories) {
		player.giveArmies(numTerritories);
		for(int i = 0; i < numTerritories; i++) {
			Territory alreadyOwnsMock = EasyMock.strictMock(Territory.class);
			EasyMock.expect(alreadyOwnsMock.isOccupied()).andReturn(false);
			alreadyOwnsMock.setController(player);
			alreadyOwnsMock.addArmies(1);
			EasyMock.replay(alreadyOwnsMock);
			player.occupyTerritory(alreadyOwnsMock);
			EasyMock.verify(alreadyOwnsMock);
		}
	}
	
	@Test
	void testPlayerColors() {
		Player redPlayer = new Player(PlayerColor.RED, null, null);
		Player greenPlayer = new Player(PlayerColor.GREEN, null, null);
		Player bluePlayer = new Player(PlayerColor.BLUE, null, null);
		Player yellowPlayer = new Player(PlayerColor.YELLOW, null, null);
		Player purplePlayer = new Player(PlayerColor.PURPLE, null, null);
		Player blackPlayer = new Player(PlayerColor.BLACK, null, null);
		
		assertTrue(redPlayer.getColor() == PlayerColor.RED);
		assertTrue(greenPlayer.getColor() == PlayerColor.GREEN);
		assertTrue(bluePlayer.getColor() == PlayerColor.BLUE);
		assertTrue(yellowPlayer.getColor() == PlayerColor.YELLOW);
		assertTrue(purplePlayer.getColor() == PlayerColor.PURPLE);
		assertTrue(blackPlayer.getColor() == PlayerColor.BLACK);
	}
	
	@Test
	void testStartsWithNoTerritories() {
		Player player = new Player(PlayerColor.RED, null, null);
		
		assertEquals(player.getOccupiedTerritories().size(), 0);
	}

	@Test
	void testPlayerOccupyUnoccupiedTerritory1() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Territory unoccupiedTerritoryMock = EasyMock.strictMock(Territory.class);
		EasyMock.expect(unoccupiedTerritoryMock.isOccupied()).andReturn(false);
		unoccupiedTerritoryMock.setController(player);
		unoccupiedTerritoryMock.addArmies(1);

		EasyMock.replay(unoccupiedTerritoryMock);
		
		assertTrue(player.occupyTerritory(unoccupiedTerritoryMock));
		assertEquals(player.getOccupiedTerritories().size(), 1);
		assertEquals(player.getArmiesAvailable(), 0);
		EasyMock.verify(unoccupiedTerritoryMock);
	}

	@Test
	void testPlayerOccupyUnoccupiedTerritory1Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Territory unoccupiedTerritory = new Territory("Test", "Test");
		assertFalse(unoccupiedTerritory.isOccupied());

		assertTrue(player.occupyTerritory(unoccupiedTerritory));

		assertEquals(player.getOccupiedTerritories().size(), 1);
		assertEquals(player.getArmiesAvailable(), 0);
	}
	
	@Test
	void testPlayerOccupyUnoccupiedTerritory2() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 2);
		player.giveArmies(2);
		Territory unoccupiedTerritoryMock = EasyMock.strictMock(Territory.class);
		EasyMock.expect(unoccupiedTerritoryMock.isOccupied()).andReturn(false);
		unoccupiedTerritoryMock.setController(player);
		unoccupiedTerritoryMock.addArmies(1);

		EasyMock.replay(unoccupiedTerritoryMock);

		assertTrue(player.occupyTerritory(unoccupiedTerritoryMock));
		assertEquals(player.getOccupiedTerritories().size(), 3);
		assertEquals(player.getArmiesAvailable(), 1);
		EasyMock.verify(unoccupiedTerritoryMock);
	}

	@Test
	void testPlayerOccupyUnoccupiedTerritory2Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(3);
		Territory unoccupiedTerritory1 = new Territory("Test", "Test");
		Territory unoccupiedTerritory2 = new Territory("Test", "Test");
		Territory unoccupiedTerritory3 = new Territory("Test", "Test");
		assertFalse(unoccupiedTerritory1.isOccupied());
		assertFalse(unoccupiedTerritory2.isOccupied());
		assertFalse(unoccupiedTerritory3.isOccupied());

		assertTrue(player.occupyTerritory(unoccupiedTerritory1));
		assertTrue(player.occupyTerritory(unoccupiedTerritory2));
		assertTrue(player.occupyTerritory(unoccupiedTerritory3));

		assertEquals(player.getOccupiedTerritories().size(), 3);
		assertEquals(player.getArmiesAvailable(), 0);
	}
	
	@Test
	void testPlayerOccupyOccupiedTerritory1() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(2);
		Territory occupiedTerritoryMock = EasyMock.strictMock(Territory.class);
		EasyMock.expect(occupiedTerritoryMock.isOccupied()).andReturn(true);
		
		EasyMock.replay(occupiedTerritoryMock);
		
		assertFalse(player.occupyTerritory(occupiedTerritoryMock));
		assertEquals(player.getOccupiedTerritories().size(), 0);
		assertEquals(player.getArmiesAvailable(), 2);
		EasyMock.verify(occupiedTerritoryMock);
	}

	@Test
	void testPlayerOccupyOccupiedTerritory1Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		Player playerThatOccupies = new Player(PlayerColor.BLUE, null, null);
		player.giveArmies(1);
		playerThatOccupies.giveArmies(1);
		Territory occupiedTerritory = new Territory("Test", "Test");
		playerThatOccupies.occupyTerritory(occupiedTerritory);

		assertTrue(occupiedTerritory.isOccupied());
		assertFalse(player.occupyTerritory(occupiedTerritory));
		assertEquals(player.getOccupiedTerritories().size(), 0);
		assertEquals(playerThatOccupies.getOccupiedTerritories().size(), 1);
		assertEquals(player.getArmiesAvailable(), 1);
	}
	
	@Test
	void testPlayerOccupyOccupiedTerritory2() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 1);
		Territory occupiedTerritoryMock = EasyMock.strictMock(Territory.class);
		EasyMock.expect(occupiedTerritoryMock.isOccupied()).andReturn(true);
		
		EasyMock.replay(occupiedTerritoryMock);
		
		assertFalse(player.occupyTerritory(occupiedTerritoryMock));
		assertEquals(player.getOccupiedTerritories().size(), 1);
		assertEquals(player.getArmiesAvailable(), 0);
		EasyMock.verify(occupiedTerritoryMock);
	}

	@Test
	void testPlayerOccupyOccupiedTerritory2Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		Player playerThatOccupies = new Player(PlayerColor.BLUE, null, null);
		Territory occupiedTerritory1 = new Territory("Test", "Test");
		Territory occupiedTerritory2 = new Territory("Test", "Test");
		player.giveArmies(2);
		playerThatOccupies.giveArmies(1);
		player.occupyTerritory(occupiedTerritory1);
		playerThatOccupies.occupyTerritory(occupiedTerritory2);

		assertFalse(player.occupyTerritory(occupiedTerritory2));
		assertEquals(player.getOccupiedTerritories().size(), 1);
		assertEquals(player.getArmiesAvailable(), 1);
	}

	@Test
	void testPlayerOccupyUnoccupiedTerritoryNotEnoughArmies() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 1);
		Territory unoccupiedTerritoryMock = EasyMock.strictMock(Territory.class);
		EasyMock.expect(unoccupiedTerritoryMock.isOccupied()).andReturn(false);
		
		EasyMock.replay(unoccupiedTerritoryMock);
		
		assertFalse(player.occupyTerritory(unoccupiedTerritoryMock));
		assertEquals(player.getOccupiedTerritories().size(), 1);
		assertEquals(player.getArmiesAvailable(), 0);
		EasyMock.verify(unoccupiedTerritoryMock);
	}

	@Test
	void testPlayerOccupyUnoccupiedTerritoryNotEnoughArmiesIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		Territory unoccupiedTerritory = new Territory("Test", "Test");
		assertFalse(player.occupyTerritory(unoccupiedTerritory));
		assertEquals(player.getOccupiedTerritories().size(), 0);
		assertEquals(player.getArmiesAvailable(), 0);

	}
	
	@Test
	void testOccupyNullTerritory() {
		Player player = new Player(PlayerColor.RED, null, null);
		Territory nullTerritory = null;
		
		assertThrows(NullPointerException.class, () -> {player.occupyTerritory(nullTerritory);}, "Null Territory given to occupyTerritory.");
	}
	
	@Test
	void testAddToNullTerritory() {
		Player player = new Player(PlayerColor.RED, null, null);
		Territory nullTerritory = null;
		
		assertThrows(NullPointerException.class, () -> {player.addArmiesToTerritory(nullTerritory, 1);}, "Null Territory given to addArmiesToTerritory.");
	}
	
	@Test
	void testAddNegativeArmies() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 1);
		player.giveArmies(2);
		Territory occupiedTerritoryMock = player.getOccupiedTerritories().get(0);
		
		assertFalse(player.addArmiesToTerritory(occupiedTerritoryMock, -1));
		assertEquals(player.getArmiesAvailable(), 2);
	}

	@Test
	void testAddNegativeArmiesIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(3);
		Territory territory = new Territory("Test", "Test");
		player.occupyTerritory(territory);
		assertFalse(player.addArmiesToTerritory(territory, -1));
		assertEquals(player.getArmiesAvailable(), 2);
	}
	
	@Test
	void testAddZeroArmies() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 2);
		player.giveArmies(1);
		Territory occupiedTerritoryMock = player.getOccupiedTerritories().get(1);
		
		assertFalse(player.addArmiesToTerritory(occupiedTerritoryMock, 0));
		assertEquals(player.getArmiesAvailable(), 1);
	}

	@Test
	void testAddZeroArmiesIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(2);
		Territory occupiedTerritory = new Territory("Test", "Test");
		assertFalse(occupiedTerritory.isOccupied());
		assertTrue(player.occupyTerritory(occupiedTerritory));
		assertFalse(player.addArmiesToTerritory(occupiedTerritory, 0));
		assertEquals(player.getArmiesAvailable(), 1);
	}
	
	@Test
	void testSuccessfullyAddOneArmy() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 1);
		player.giveArmies(1);
		Territory occupiedTerritoryMock = player.getOccupiedTerritories().get(0);
		EasyMock.reset(occupiedTerritoryMock);
		occupiedTerritoryMock.addArmies(1);
		
		EasyMock.replay(occupiedTerritoryMock);
		
		assertTrue(player.addArmiesToTerritory(occupiedTerritoryMock, 1));
		assertEquals(player.getArmiesAvailable(), 0);
		EasyMock.verify(occupiedTerritoryMock);
	}

	@Test
	void testSuccessfullyAddOneArmyIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(2);
		Territory occupiedTerritory = new Territory("Test", "Test");
		assertTrue(player.occupyTerritory(occupiedTerritory));
		assertTrue(player.addArmiesToTerritory(occupiedTerritory, 1));
		assertEquals(player.getArmiesAvailable(), 0);
	}
	
	@Test
	void testAddArmiesToUnoccupiedTerritory1() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Territory unoccupiedTerritoryMock = EasyMock.strictMock(Territory.class);
		
		assertFalse(player.addArmiesToTerritory(unoccupiedTerritoryMock, 1));
		assertEquals(player.getArmiesAvailable(), 1);
	}

	@Test
	void testAddArmiesToUnoccupiedTerritory1Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Territory unoccupiedTerritory = new Territory("Test", "Test");
		assertFalse(unoccupiedTerritory.isOccupied());

		assertFalse(player.addArmiesToTerritory(unoccupiedTerritory, 1));
		assertEquals(player.getArmiesAvailable(), 1);
	}
	
	@Test
	void testAddMoreArmiesThanAvailable() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 2);
		Territory occupiedTerritoryMock = player.getOccupiedTerritories().get(0);
		EasyMock.reset(occupiedTerritoryMock);
		
		assertFalse(player.addArmiesToTerritory(occupiedTerritoryMock, 1));
		assertEquals(player.getArmiesAvailable(), 0);
	}

	@Test
	void testAddMoreArmiesThanAvailableIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Territory occupiedTerritory = new Territory("Test", "Test");
		player.occupyTerritory(occupiedTerritory);
		assertFalse(player.addArmiesToTerritory(occupiedTerritory, 1));
		assertEquals(player.getArmiesAvailable(), 0);
	}
	
	@Test
	void testAddArmiesToUnoccupiedTerritory2() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 2);
		player.giveArmies(2);
		Territory unoccupiedTerritoryMock = EasyMock.strictMock(Territory.class);
		
		assertFalse(player.addArmiesToTerritory(unoccupiedTerritoryMock, 1));
		assertEquals(player.getArmiesAvailable(), 2);
	}

	@Test
	void testAddArmiesToUnoccupiedTerritory2Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(3);
		Territory occupiedTerritory1 = new Territory("Test", "Test");
		Territory occupiedTerritory2 = new Territory("Test", "Test");
		player.occupyTerritory(occupiedTerritory1);
		player.occupyTerritory(occupiedTerritory2);
		Territory unoccupiedTerritory = new Territory("Test", "Test");
		assertFalse(player.addArmiesToTerritory(unoccupiedTerritory, 1));
		assertEquals(player.getArmiesAvailable(), 1);
	}
	
	@Test
	void testSuccessfullyAddMultipleArmies() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 1);
		player.giveArmies(3);
		Territory occupiedTerritoryMock = player.getOccupiedTerritories().get(0);
		EasyMock.reset(occupiedTerritoryMock);
		occupiedTerritoryMock.addArmies(2);
		
		EasyMock.replay(occupiedTerritoryMock);
		
		assertTrue(player.addArmiesToTerritory(occupiedTerritoryMock, 2));
		assertEquals(player.getArmiesAvailable(), 1);
		EasyMock.verify(occupiedTerritoryMock);
	}

	@Test
	void testSuccessfullyAddMultipleArmiesIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(3);
		Territory occupiedTerritory = new Territory("Test", "Test");
		assertTrue(player.occupyTerritory(occupiedTerritory));
		assertTrue(player.addArmiesToTerritory(occupiedTerritory, 2));
		assertEquals(player.getArmiesAvailable(), 0);
	}
	
	@Test
	void testArmiesGainedZeroTerritoryCount() {
		Player player = new Player(PlayerColor.RED, null, null);
		
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 3);
	}
	
	@Test
	void testArmiesGainedOneTerritoryCount() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 1);
		
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 3);
	}

	@Test
	void testArmiesGainedOneTerritoryCountIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Territory territory = new Territory("Test", "Test");
		player.occupyTerritory(territory);
		assertEquals((player.getOccupiedTerritories().size()), 1);
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 3);
	}
	
	@Test
	void testArmiesGainedElevenTerritoryCount() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 11);
		
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 3);
	}

	@Test
	void testArmiesGainedElevenTerritoryCountIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(11);
		for(int i = 0; i < 11; i++) {
			Territory t = new Territory("Test", "Test");
			player.occupyTerritory(t);
		}
		assertEquals(player.getOccupiedTerritories().size(), 11);
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 3);
	}
	
	@Test
	void testArmiesGainedTwelveTerritoryCount() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 12);
		
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 4);
	}

	@Test
	void testArmiesGainedTwelveTerritoryCountIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(12);
		for(int i = 0; i < 12; i++) {
			Territory t = new Territory("Test", "Test");
			player.occupyTerritory(t);
		}
		assertEquals(player.getOccupiedTerritories().size(), 12);
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 4);
	}
	
	@Test
	void testArmiesGainedThirteenTerritoryCount() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 13);
		
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 4);
	}

	@Test
	void testArmiesGainedThirteenTerritoryCountIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(13);
		for(int i = 0; i < 13; i++) {
			Territory t = new Territory("Test", "Test");
			player.occupyTerritory(t);
		}
		assertEquals(player.getOccupiedTerritories().size(), 13);
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 4);
	}
	
	@Test
	void testArmiesGainedFifteenTerritoryCount() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 15);
		
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 5);
	}

	@Test
	void testArmiesGainedFifteenTerritoryCountIntegration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(15);
		for(int i = 0; i < 15; i++) {
			Territory t = new Territory("Test", "Test");
			player.occupyTerritory(t);
		}
		assertEquals(player.getOccupiedTerritories().size(), 15);
		assertEquals(player.calculateArmiesGainedFromTerritoryCount(), 5);
	}

	@Test
	void testPlayerHasLost1() {
		Player player = new Player(PlayerColor.RED, null, null);
		assertTrue(player.hasLost());
	}

	@Test
	void testPlayerHasLost2() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 10);
		assertFalse(player.hasLost());
	}

	@Test
	void testPlayerHasLost2Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		Territory t = new Territory("Test", "Test");
		player.giveArmies(1);
		player.occupyTerritory(t);
		assertFalse(player.hasLost());
	}

	@Test
	void testPlayerHasWon1() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 42);
		assertTrue(player.hasWon());
	}

	@Test
	void testPlayerHasWon1Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(42);
		for(int i = 0; i < 42; i++) {
			Territory t = new Territory("Test", "Test");
			player.occupyTerritory(t);
		}
		assertTrue(player.hasWon());
	}

	@Test
	void testPlayerHasWon2() {
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 40);
		assertFalse(player.hasWon());
	}

	@Test
	void testPlayerHasWon2Integration() {
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(40);
		for(int i = 0; i < 40; i++) {
			Territory t = new Territory("Test", "Test");
			player.occupyTerritory(t);
		}
		assertFalse(player.hasWon());
	}
	
	@Test
	void testRollNegativeDice() {
		Player player = new Player(PlayerColor.RED, null, null);
		
		assertThrows(IllegalArgumentException.class, () -> {player.rollDice(-1);}, "Number of dice must be zero or more.");
	}
	
	@Test
	void testRollZeroDice() {
		Player player = new Player(PlayerColor.RED, null, null);
		assertTrue(Arrays.equals(player.rollDice(0), new int[0]));
	}
	
	@Test
	void testRollOneDie() {
		Random randomMock = EasyMock.strictMock(Random.class);
		Player player = new Player(PlayerColor.RED, randomMock, null);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(0);
		
		EasyMock.replay(randomMock);
		
		int [] expected = {1};
		assertTrue(Arrays.equals(player.rollDice(1), expected));
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testRollTwoDice() {
		Random randomMock = EasyMock.strictMock(Random.class);
		Player player = new Player(PlayerColor.RED, randomMock, null);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(5);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		
		EasyMock.replay(randomMock);
		
		int [] expected = {6, 2};
		assertTrue(Arrays.equals(player.rollDice(2), expected));
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testAttackTerritoryCapture1() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		this.occupyTerritoriesSetup(defendingPlayer, 1);
		Territory attackingMock = attackingPlayer.getOccupiedTerritories().get(0);
		Territory defendingMock = defendingPlayer.getOccupiedTerritories().get(0);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		EasyMock.reset(attackingMock);
		EasyMock.reset(defendingMock);
		
		EasyMock.expect(attackingMock.getController()).andReturn(attackingPlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(defendingPlayer);
		EasyMock.expect(attackingMock.attackTerritory(defendingMock, 
				attackerRolls, defenderRolls)).andReturn(true);
		attackingPlayer.captureDefeatedTerritory(defendingPlayer, attackingMock, defendingMock);
		EasyMock.expect(attackingMock.getArmies()).andReturn(1);
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		assertEquals(0, attackingPlayer.attackTerritory(attackingMock, 
				defendingMock, attackerRolls, defenderRolls));
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackTerritoryCapture1Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		attackingPlayer.giveArmies(1);
		defendingPlayer.giveArmies(1);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		attackingPlayer.occupyTerritory(attackT);
		attackT.addArmies(1);
		defendingPlayer.occupyTerritory(defendT);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		assertEquals(0, attackingPlayer.attackTerritory(attackT,
				defendT, attackerRolls, defenderRolls));
	}

	@Test
	void testAttackTerritoryNotCapture1() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		this.occupyTerritoriesSetup(defendingPlayer, 1);
		Territory attackingMock = attackingPlayer.getOccupiedTerritories().get(0);
		Territory defendingMock = defendingPlayer.getOccupiedTerritories().get(0);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		EasyMock.reset(attackingMock);
		EasyMock.reset(defendingMock);
		
		EasyMock.expect(attackingMock.getController()).andReturn(attackingPlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(defendingPlayer);
		EasyMock.expect(attackingMock.attackTerritory(defendingMock, 
				attackerRolls, defenderRolls)).andReturn(false);
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		assertEquals(0, attackingPlayer.attackTerritory(attackingMock, 
				defendingMock, attackerRolls, defenderRolls));
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackTerritoryNotCapture1Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		attackingPlayer.giveArmies(1);
		defendingPlayer.giveArmies(1);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		attackingPlayer.occupyTerritory(attackT);
		attackT.addArmies(1);
		defendT.addArmies(1);
		defendingPlayer.occupyTerritory(defendT);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		assertEquals(0, attackingPlayer.attackTerritory(attackT,
				defendT, attackerRolls, defenderRolls));
		assertNotEquals(attackingPlayer, defendT.getController());
	}
	
	@Test
	void testAttackTerritoryCapture2() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		this.occupyTerritoriesSetup(defendingPlayer, 1);
		Territory attackingMock = attackingPlayer.getOccupiedTerritories().get(0);
		Territory defendingMock = defendingPlayer.getOccupiedTerritories().get(0);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		EasyMock.reset(attackingMock);
		EasyMock.reset(defendingMock);
		
		EasyMock.expect(attackingMock.getController()).andReturn(attackingPlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(defendingPlayer);
		EasyMock.expect(attackingMock.attackTerritory(defendingMock, 
				attackerRolls, defenderRolls)).andReturn(true);
		attackingPlayer.captureDefeatedTerritory(defendingPlayer, attackingMock, defendingMock);
		EasyMock.expect(attackingMock.getArmies()).andReturn(3);
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		assertEquals(2, attackingPlayer.attackTerritory(attackingMock, 
				defendingMock, attackerRolls, defenderRolls));
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackTerritoryCapture2Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		attackingPlayer.giveArmies(1);
		defendingPlayer.giveArmies(1);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		attackingPlayer.occupyTerritory(attackT);
		attackT.addArmies(3);
		defendingPlayer.occupyTerritory(defendT);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		assertEquals(2, attackingPlayer.attackTerritory(attackT,
				defendT, attackerRolls, defenderRolls));
	}
	
	@Test
	void testAttackOwnTerritory() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		this.occupyTerritoriesSetup(attackingPlayer, 2);
		Territory attackingMock = attackingPlayer.getOccupiedTerritories().get(0);
		Territory defendingMock = attackingPlayer.getOccupiedTerritories().get(1);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		EasyMock.reset(attackingMock);
		EasyMock.reset(defendingMock);
		
		EasyMock.expect(attackingMock.getController()).andReturn(attackingPlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(attackingPlayer);
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		InvalidAttackException e = assertThrows(InvalidAttackException.class, 
				() -> {attackingPlayer.attackTerritory(attackingMock, defendingMock, attackerRolls, defenderRolls);});
		assertEquals("Cannot attack own Territory.", e.getMessage());
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackOwnTerritoryIntegration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		attackingPlayer.giveArmies(2);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		attackingPlayer.occupyTerritory(attackT);
		attackingPlayer.occupyTerritory(defendT);
		attackT.addArmies(1);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		InvalidAttackException e = assertThrows(InvalidAttackException.class,
				() -> {attackingPlayer.attackTerritory(attackT, defendT, attackerRolls, defenderRolls);});
		assertEquals("Cannot attack own Territory.", e.getMessage());
	}
	
	@Test
	void testAttackWithUncontrolledTerritory1() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player bluePlayer = new Player(PlayerColor.BLUE, null, null);
		Player purplePlayer = new Player(PlayerColor.PURPLE, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		Territory attackingMock = EasyMock.strictMock(Territory.class);
		Territory defendingMock = EasyMock.strictMock(Territory.class);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		
		EasyMock.expect(attackingMock.getController()).andReturn(bluePlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(purplePlayer);
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		InvalidAttackException e = assertThrows(InvalidAttackException.class, 
				() -> {attackingPlayer.attackTerritory(attackingMock, defendingMock, attackerRolls, defenderRolls);});
		assertEquals("Cannot attack with a Territory in another's control.", e.getMessage());
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackWithUncontrolledTerritory1Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player bluePlayer = new Player(PlayerColor.BLUE, null, null);
		Player purplePlayer = new Player(PlayerColor.PURPLE, null, null);
		bluePlayer.giveArmies(1);
		purplePlayer.giveArmies(1);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		bluePlayer.occupyTerritory(attackT);
		purplePlayer.occupyTerritory(defendT);
		attackT.addArmies(1);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		InvalidAttackException e = assertThrows(InvalidAttackException.class,
				() -> {attackingPlayer.attackTerritory(attackT, defendT, attackerRolls, defenderRolls);});
		assertEquals("Cannot attack with a Territory in another's control.", e.getMessage());
	}
	
	@Test
	void testAttackWithUncontrolledTerritory2() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player bluePlayer = new Player(PlayerColor.BLUE, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		Territory attackingMock = EasyMock.strictMock(Territory.class);
		Territory defendingMock = EasyMock.strictMock(Territory.class);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		
		EasyMock.expect(attackingMock.getController()).andReturn(bluePlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(bluePlayer);
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		InvalidAttackException e = assertThrows(InvalidAttackException.class, 
				() -> {attackingPlayer.attackTerritory(attackingMock, defendingMock, attackerRolls, defenderRolls);});
		assertEquals("Cannot attack with a Territory in another's control.", e.getMessage());
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackWithUncontrolledTerritory2Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player bluePlayer = new Player(PlayerColor.BLUE, null, null);
		bluePlayer.giveArmies(2);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		bluePlayer.occupyTerritory(attackT);
		bluePlayer.occupyTerritory(defendT);
		attackT.addArmies(1);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		InvalidAttackException e = assertThrows(InvalidAttackException.class,
				() -> {attackingPlayer.attackTerritory(attackT, defendT, attackerRolls, defenderRolls);});
		assertEquals("Cannot attack with a Territory in another's control.", e.getMessage());
	}
	
	@Test
	void testAttackTerritoryNotCapture2() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player defendingPlayer = new Player(PlayerColor.BLACK, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		this.occupyTerritoriesSetup(defendingPlayer, 1);
		Territory attackingMock = attackingPlayer.getOccupiedTerritories().get(0);
		Territory defendingMock = defendingPlayer.getOccupiedTerritories().get(0);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		EasyMock.reset(attackingMock);
		EasyMock.reset(defendingMock);
		
		EasyMock.expect(attackingMock.getController()).andReturn(attackingPlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(defendingPlayer);
		EasyMock.expect(attackingMock.attackTerritory(defendingMock, 
				attackerRolls, defenderRolls)).andReturn(false);
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		assertEquals(0, attackingPlayer.attackTerritory(attackingMock, 
				defendingMock, attackerRolls, defenderRolls));
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackTerritoryNotCapture2Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		attackingPlayer.giveArmies(1);
		defendingPlayer.giveArmies(1);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		attackingPlayer.occupyTerritory(attackT);
		attackT.addArmies(2);
		defendT.addArmies(2);
		defendingPlayer.occupyTerritory(defendT);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		assertEquals(3, attackT.getArmies());
		assertEquals(0, attackingPlayer.attackTerritory(attackT,
				defendT, attackerRolls, defenderRolls));
		assertEquals(2, defendT.getArmies());
		assertNotEquals(attackingPlayer, defendT.getController());
	}
	
	@Test
	void testAttackTerritoryInvalidRolls1() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player defendingPlayer = new Player(PlayerColor.BLACK, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		this.occupyTerritoriesSetup(defendingPlayer, 2);
		Territory attackingMock = attackingPlayer.getOccupiedTerritories().get(0);
		Territory defendingMock = defendingPlayer.getOccupiedTerritories().get(0);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		EasyMock.reset(attackingMock);
		EasyMock.reset(defendingMock);
		
		EasyMock.expect(attackingMock.getController()).andReturn(attackingPlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(defendingPlayer);
		EasyMock.expect(attackingMock.attackTerritory(defendingMock, 
				attackerRolls, defenderRolls)).andThrow(
						new InvalidAttackException("Attacker does not possess enough troops for that many rolls"));
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		InvalidAttackException e = assertThrows(InvalidAttackException.class, 
				() -> {attackingPlayer.attackTerritory(attackingMock, defendingMock, attackerRolls, defenderRolls);});
		
		assertEquals(e.getMessage(), "Attacker does not possess enough troops for that many rolls");
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackTerritoryInvalidRolls1Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		attackingPlayer.giveArmies(1);
		defendingPlayer.giveArmies(1);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		attackingPlayer.occupyTerritory(attackT);
		defendingPlayer.occupyTerritory(defendT);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		InvalidAttackException e = assertThrows(InvalidAttackException.class,
				() -> {attackingPlayer.attackTerritory(attackT, defendT, attackerRolls, defenderRolls);});
		assertEquals(e.getMessage(), "Attacker does not possess enough troops for that many rolls");
	}
	
	@Test
	void testAttackTerritoryInvalidRolls2() throws InvalidAttackException {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("captureDefeatedTerritory")
				.createStrictMock();
		Player defendingPlayer = new Player(PlayerColor.BLACK, null, null);
		this.occupyTerritoriesSetup(attackingPlayer, 1);
		this.occupyTerritoriesSetup(defendingPlayer, 1);
		Territory attackingMock = attackingPlayer.getOccupiedTerritories().get(0);
		Territory defendingMock = defendingPlayer.getOccupiedTerritories().get(0);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1};
		EasyMock.reset(attackingMock);
		EasyMock.reset(defendingMock);
		
		EasyMock.expect(attackingMock.getController()).andReturn(attackingPlayer);
		EasyMock.expect(defendingMock.getController()).andReturn(defendingPlayer);
		EasyMock.expect(attackingMock.attackTerritory(defendingMock, 
				attackerRolls, defenderRolls)).andThrow(
						new InvalidAttackException("Defender does not possess enough troops for that many rolls"));
		
		EasyMock.replay(attackingPlayer);
		EasyMock.replay(attackingMock);
		EasyMock.replay(defendingMock);
		
		InvalidAttackException e = assertThrows(InvalidAttackException.class, 
				() -> {attackingPlayer.attackTerritory(attackingMock, defendingMock, attackerRolls, defenderRolls);});
		
		assertEquals(e.getMessage(), "Defender does not possess enough troops for that many rolls");
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(attackingMock);
		EasyMock.verify(defendingMock);
	}

	@Test
	void testAttackTerritoryInvalidRolls2Integration() throws InvalidAttackException {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defendingPlayer = new Player(PlayerColor.GREEN, null, null);
		attackingPlayer.giveArmies(1);
		defendingPlayer.giveArmies(1);
		Territory attackT = new Territory("Test", "Test");
		Territory defendT = new Territory("Test", "Test");
		attackingPlayer.occupyTerritory(attackT);
		defendingPlayer.occupyTerritory(defendT);
		attackT.addArmies(1);
		int [] attackerRolls = {2};
		int [] defenderRolls = {1, 1};
		InvalidAttackException e = assertThrows(InvalidAttackException.class,
				() -> {attackingPlayer.attackTerritory(attackT, defendT, attackerRolls, defenderRolls);});
		assertEquals(e.getMessage(), "Defender does not possess enough troops for that many rolls");
	}

	@Test
	public void testAddNewArmiesWith1Territory(){
		Territory mockedTerritory = EasyMock.strictMock(Territory.class);
		EasyMock.expect(mockedTerritory.getContinent()).andReturn("North America");
		Player red = new Player(PlayerColor.RED, new Random(), null);
		red.getOccupiedTerritories().add(mockedTerritory);
		EasyMock.replay(mockedTerritory);

		assertEquals(0, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(3, red.getArmiesAvailable());
		EasyMock.verify(mockedTerritory);
	}

	@Test
	public void testAddNewArmiesWith1TerritoryIntegration(){
		Territory territory = new Territory("Test", "North America");
		Player red = new Player(PlayerColor.RED, new Random(), null);
		red.giveArmies(1);
		red.occupyTerritory(territory);
		assertEquals(0, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(3, red.getArmiesAvailable());
	}

	@Test
	public void testAddNewArmiesWith9Territories1Continent(){
		Player red = new Player(PlayerColor.RED, new Random(), null);
		List<Territory> mockedTerritories = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			Territory mockedTerritory = EasyMock.strictMock(Territory.class);
			EasyMock.expect(mockedTerritory.getContinent()).andReturn("North America");
			red.getOccupiedTerritories().add(mockedTerritory);
			mockedTerritories.add(mockedTerritory);
		}
		for (int i = 0; i < 9; i++) {
			EasyMock.replay(mockedTerritories.get(i));
		}

		assertEquals(0, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(8, red.getArmiesAvailable());
		for (int i = 0; i < 9; i++) {
			EasyMock.verify(mockedTerritories.get(i));
		}
	}

	@Test
	public void testAddNewArmiesWith9Territories1ContinentIntegration(){
		Player red = new Player(PlayerColor.RED, new Random(), null);
		red.giveArmies(9);
		for (int i = 0; i < 9; i++) {
			Territory t = new Territory("Test", "North America");
			red.occupyTerritory(t);
		}
		assertEquals(0, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(8, red.getArmiesAvailable());
	}

	@Test
	public void testAddNewArmiesWith15Territories10ArmiesAlready(){
		Player red = new Player(PlayerColor.RED, new Random(), null);
		red.giveArmies(10);
		List<Territory> mockedTerritories = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			Territory mockedTerritory = EasyMock.strictMock(Territory.class);
			EasyMock.expect(mockedTerritory.getContinent()).andReturn("Testing");
			red.getOccupiedTerritories().add(mockedTerritory);
			mockedTerritories.add(mockedTerritory);
		}
		for (int i = 0; i < 15; i++) {
			EasyMock.replay(mockedTerritories.get(i));
		}

		assertEquals(10, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(15, red.getArmiesAvailable());
		for (int i = 0; i < 15; i++) {
			EasyMock.verify(mockedTerritories.get(i));
		}
	}

	@Test
	public void testAddNewArmiesWith15Territories10ArmiesAlreadyIntegration(){
		Player red = new Player(PlayerColor.RED, new Random(), null);
		red.giveArmies(25);
		for (int i = 0; i < 15; i++) {
			Territory t = new Territory("Test", "Test");
			red.occupyTerritory(t);
		}
		assertEquals(10, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(15, red.getArmiesAvailable());
	}

	@Test
	public void testAddNewArmiesWith19Territories3Continents(){
		Player red = new Player(PlayerColor.RED, new Random(), null);
		List<Territory> mockedTerritories = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			Territory mockedTerritory = EasyMock.strictMock(Territory.class);
			EasyMock.expect(mockedTerritory.getContinent()).andReturn("North America");
			red.getOccupiedTerritories().add(mockedTerritory);
			mockedTerritories.add(mockedTerritory);
		}
		for (int i = 0; i < 4; i++) {
			Territory mockedTerritory = EasyMock.strictMock(Territory.class);
			EasyMock.expect(mockedTerritory.getContinent()).andReturn("South America");
			red.getOccupiedTerritories().add(mockedTerritory);
			mockedTerritories.add(mockedTerritory);
		}
		for (int i = 0; i < 6; i++) {
			Territory mockedTerritory = EasyMock.strictMock(Territory.class);
			EasyMock.expect(mockedTerritory.getContinent()).andReturn("Africa");
			red.getOccupiedTerritories().add(mockedTerritory);
			mockedTerritories.add(mockedTerritory);
		}

		for (int i = 0; i < 19; i++) {
			EasyMock.replay(mockedTerritories.get(i));
		}

		assertEquals(0, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(16, red.getArmiesAvailable());
		for (int i = 0; i < 19; i++) {
			EasyMock.verify(mockedTerritories.get(i));
		}
	}

	@Test
	public void testAddNewArmiesWith19Territories3ContinentsIntegration(){
		Player red = new Player(PlayerColor.RED, new Random(), null);
		red.giveArmies(19);
		for (int i = 0; i < 9; i++) {
			Territory t = new Territory("Test", "North America");
			red.occupyTerritory(t);
		}
		for (int i = 0; i < 4; i++) {
			Territory t = new Territory("Test", "South America");
			red.occupyTerritory(t);
		}
		for (int i = 0; i < 6; i++) {
			Territory t = new Territory("Test", "Africa");
			red.occupyTerritory(t);
		}
		assertEquals(0, red.getArmiesAvailable());
		red.addNewTurnArmies();
		assertEquals(16, red.getArmiesAvailable());
	}
	
	@Test
	public void testHandlePossiblePlayerDefeatedNotDefeated() {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("takeCardsFromDefeated")
				.createStrictMock();
		Player defendingPlayer = EasyMock.strictMock(Player.class);
		
		EasyMock.expect(defendingPlayer.hasLost()).andReturn(false);
		
		EasyMock.replay(defendingPlayer);
		EasyMock.replay(attackingPlayer);
		
		attackingPlayer.handlePossiblePlayerDefeat(defendingPlayer);
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(defendingPlayer);
	}
	
	@Test
	public void testHandlePossiblePlayerDefeatedIsDefeated() {
		Player attackingPlayer = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("takeCardsFromDefeated")
				.createStrictMock();
		Player defendingPlayer = EasyMock.strictMock(Player.class);
		
		EasyMock.expect(defendingPlayer.hasLost()).andReturn(true);
		attackingPlayer.takeCardsFromDefeated(defendingPlayer);
		
		EasyMock.replay(defendingPlayer);
		EasyMock.replay(attackingPlayer);
		
		attackingPlayer.handlePossiblePlayerDefeat(defendingPlayer);
		
		EasyMock.verify(attackingPlayer);
		EasyMock.verify(defendingPlayer);
	}
	
	@Test
	public void testTakeCardsBothEmpty() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(0, attackingPlayer.getCards().size());
	}
	
	@Test
	public void testTakeEmptyCardsAlreadyHasOne() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		attackingPlayer.getCards().add(card1);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(1, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
	}
	
	@Test
	public void testTakeEmptyCardsAlreadyHasMultiple() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		attackingPlayer.getCards().add(card1);
		attackingPlayer.getCards().add(card2);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(2, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
		assertTrue(attackingPlayer.getCards().contains(card2));
	}
	
	@Test
	public void testTakeOneCardHasNone() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		defeatedPlayer.getCards().add(card1);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(1, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
		assertFalse(defeatedPlayer.getCards().contains(card1));
	}
	
	@Test
	public void testTakeMutipleCardsHasNone() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		defeatedPlayer.getCards().add(card1);
		defeatedPlayer.getCards().add(card2);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(2, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
		assertTrue(attackingPlayer.getCards().contains(card2));
	}
	
	@Test
	public void testTakeCardsOneEach() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		attackingPlayer.getCards().add(card1);
		defeatedPlayer.getCards().add(card2);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(2, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
		assertTrue(attackingPlayer.getCards().contains(card2));
	}
	
	@Test
	public void testTakeMultipleCardsHasOne() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		attackingPlayer.getCards().add(card1);
		defeatedPlayer.getCards().add(card2);
		defeatedPlayer.getCards().add(card3);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(3, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
		assertTrue(attackingPlayer.getCards().contains(card2));
		assertTrue(attackingPlayer.getCards().contains(card3));
	}
	
	@Test
	public void testTakeOneCardHasMultiple() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		attackingPlayer.getCards().add(card1);
		attackingPlayer.getCards().add(card2);
		defeatedPlayer.getCards().add(card3);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(3, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
		assertTrue(attackingPlayer.getCards().contains(card2));
		assertTrue(attackingPlayer.getCards().contains(card3));
	}
	
	@Test
	public void testTakeCardsMultipleEach() {
		Player attackingPlayer = new Player(PlayerColor.RED, null, null);
		Player defeatedPlayer = new Player(PlayerColor.BLUE, null, null);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		Card card4 = EasyMock.strictMock(Card.class);
		Card card5 = EasyMock.strictMock(Card.class);
		attackingPlayer.getCards().add(card1);
		attackingPlayer.getCards().add(card2);
		attackingPlayer.getCards().add(card4);
		defeatedPlayer.getCards().add(card3);
		defeatedPlayer.getCards().add(card5);
		
		attackingPlayer.takeCardsFromDefeated(defeatedPlayer);
		
		assertEquals(0, defeatedPlayer.getCards().size());
		assertEquals(5, attackingPlayer.getCards().size());
		assertTrue(attackingPlayer.getCards().contains(card1));
		assertTrue(attackingPlayer.getCards().contains(card2));
		assertTrue(attackingPlayer.getCards().contains(card3));
		assertTrue(attackingPlayer.getCards().contains(card4));
		assertTrue(attackingPlayer.getCards().contains(card5));
	}
	
	@Test
	public void testEndTurnNoCapture() {
		Player player = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("drawCard")
				.createStrictMock();
		
		EasyMock.replay(player);
		
		assertFalse(player.endTurn());
		assertFalse(player.capturedThisTurn);
		
		EasyMock.verify(player);
	}
	
	@Test
	public void testEndTurnDidCapture() {
		Player player = EasyMock.partialMockBuilder(Player.class)
				.withConstructor(PlayerColor.class, Random.class, CardTrader.class)
				.withArgs(PlayerColor.RED, null, null)
				.addMockedMethod("drawCard")
				.createStrictMock();
		player.capturedThisTurn = true;
		
		player.drawCard();
		
		EasyMock.replay(player);
		
		assertTrue(player.endTurn());
		assertFalse(player.capturedThisTurn);
		
		EasyMock.verify(player);
	}
	
	
	
	@Test
	public void testSuccessfulCardTrade1() {
		CardTrader cardTraderMock = EasyMock.strictMock(CardTrader.class);
		
		Player player = new Player(PlayerColor.PURPLE, null, cardTraderMock);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card1);
		tradeInSet.add(card2);
		tradeInSet.add(card3);
		
		EasyMock.expect(cardTraderMock.tradeInCardSet(player, tradeInSet)).andReturn(true);
		
		EasyMock.replay(cardTraderMock);
		
		assertTrue(player.tradeInCards(tradeInSet));
		assertEquals(0, player.getCards().size());
		
		EasyMock.verify(cardTraderMock);
	}
	
	@Test
	public void testTooSmallSet() {
		CardTrader cardTraderMock = EasyMock.strictMock(CardTrader.class);
		
		Player player = new Player(PlayerColor.PURPLE, null, cardTraderMock);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		Card card4 = EasyMock.strictMock(Card.class);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		player.getCards().add(card4);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card1);
		
		EasyMock.replay(cardTraderMock);
		
		assertFalse(player.tradeInCards(tradeInSet));
		assertEquals(4, player.getCards().size());
		
		EasyMock.verify(cardTraderMock);
	}
	
	@Test
	public void testInvalidSet() {
		CardTrader cardTraderMock = EasyMock.strictMock(CardTrader.class);
		
		Player player = new Player(PlayerColor.PURPLE, null, cardTraderMock);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card1);
		tradeInSet.add(card2);
		tradeInSet.add(card3);
		
		EasyMock.expect(cardTraderMock.tradeInCardSet(player, tradeInSet)).andReturn(false);
		
		EasyMock.replay(cardTraderMock);
		
		assertFalse(player.tradeInCards(tradeInSet));
		assertEquals(3, player.getCards().size());
		
		EasyMock.verify(cardTraderMock);
	}
	
	@Test
	public void testDoesNotHaveCards() {
		CardTrader cardTraderMock = EasyMock.strictMock(CardTrader.class);
		
		Player player = new Player(PlayerColor.PURPLE, null, cardTraderMock);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		Card card4 = EasyMock.strictMock(Card.class);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card2);
		tradeInSet.add(card3);
		tradeInSet.add(card4);
		
		EasyMock.replay(cardTraderMock);
		
		assertFalse(player.tradeInCards(tradeInSet));
		assertEquals(3, player.getCards().size());
		
		EasyMock.verify(cardTraderMock);
	}
	
	@Test
	public void testPlayerHasInsufficientCards() {
		CardTrader cardTraderMock = EasyMock.strictMock(CardTrader.class);
		
		Player player = new Player(PlayerColor.PURPLE, null, cardTraderMock);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		
		player.getCards().add(card1);
		player.getCards().add(card3);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card1);
		tradeInSet.add(card2);
		tradeInSet.add(card3);
		
		EasyMock.replay(cardTraderMock);
		
		assertFalse(player.tradeInCards(tradeInSet));
		assertEquals(2, player.getCards().size());
		
		EasyMock.verify(cardTraderMock);
	}
	
	@Test
	public void testSuccessfulCardTrade2() {
		CardTrader cardTraderMock = EasyMock.strictMock(CardTrader.class);
		
		Player player = new Player(PlayerColor.PURPLE, null, cardTraderMock);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		Card card4 = EasyMock.strictMock(Card.class);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		player.getCards().add(card4);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card1);
		tradeInSet.add(card2);
		tradeInSet.add(card4);
		
		EasyMock.expect(cardTraderMock.tradeInCardSet(player, tradeInSet)).andReturn(true);
		
		EasyMock.replay(cardTraderMock);
		
		assertTrue(player.tradeInCards(tradeInSet));
		assertEquals(1, player.getCards().size());
		
		EasyMock.verify(cardTraderMock);
	}
	
	@Test
	public void testTooLargeSet() {
		CardTrader cardTraderMock = EasyMock.strictMock(CardTrader.class);
		
		Player player = new Player(PlayerColor.PURPLE, null, cardTraderMock);
		Card card1 = EasyMock.strictMock(Card.class);
		Card card2 = EasyMock.strictMock(Card.class);
		Card card3 = EasyMock.strictMock(Card.class);
		Card card4 = EasyMock.strictMock(Card.class);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		player.getCards().add(card4);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card1);
		tradeInSet.add(card2);
		tradeInSet.add(card3);
		tradeInSet.add(card4);
		
		EasyMock.replay(cardTraderMock);
		
		assertFalse(player.tradeInCards(tradeInSet));
		assertEquals(4, player.getCards().size());
		
		EasyMock.verify(cardTraderMock);
	}
}
