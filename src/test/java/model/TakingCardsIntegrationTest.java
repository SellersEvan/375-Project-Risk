package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class TakingCardsIntegrationTest {
	
	@Test
	void testPlayerDefeatsAndNoCardsToTake() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);

		Territory attackingTerritory = new Territory("attackingTerritory", "Asia");
		Territory attackedTerritory = new Territory("attackedTerritory", "Asia");
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.GREEN, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.BLUE, randomMock, cardTrader);
		aggressor.giveArmies(5);
		defender.giveArmies(2);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 4);
		defender.occupyTerritory(attackedTerritory);
		defender.addArmiesToTerritory(attackedTerritory, 1);
		
		// Attacker rolls
		EasyMock.expect(randomMock.nextInt(6)).andReturn(5);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(3);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(4);
		
		// Defender rolls
		EasyMock.expect(randomMock.nextInt(6)).andReturn(3);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(4);
		
		EasyMock.replay(randomMock);
		
		int [] attackerRolls = aggressor.rollDice(3);
		int [] defenderRolls = defender.rollDice(2);

		assertEquals(3, aggressor.attackTerritory(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls));
		assertTrue(aggressor.getOccupiedTerritories().contains(attackedTerritory));
		assertEquals(2, aggressor.getOccupiedTerritories().size());
		assertEquals(0, defender.getOccupiedTerritories().size());
		assertTrue(defender.hasLost());
		assertEquals(4, attackingTerritory.getArmies());
		assertEquals(1, attackedTerritory.getArmies());
		
		assertEquals(0, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testPlayerDefeatsAndTakesTenCards() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);

		Territory attackingTerritory = new Territory("attackingTerritory", "Asia");
		Territory attackedTerritory = new Territory("attackedTerritory", "Asia");
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.GREEN, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.BLUE, randomMock, cardTrader);
		aggressor.giveArmies(4);
		defender.giveArmies(1);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 3);
		defender.occupyTerritory(attackedTerritory);
		
		for (int i = 0; i < 10; i++) {
			EasyMock.expect(randomMock.nextInt(3)).andReturn(i % 3);
			EasyMock.expect(randomMock.nextInt(MapManager.getTerritories().size())).andReturn(i % 2);
		}
		
		// Attacker rolls
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(6);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		
		// Defender roll
		EasyMock.expect(randomMock.nextInt(6)).andReturn(5);
		
		EasyMock.replay(randomMock);
		
		// Defender holds 10 cards
		for (int i = 0; i < 10; i++) {
			defender.drawCard();
		}
		
		int [] attackerRolls = aggressor.rollDice(3);
		int [] defenderRolls = defender.rollDice(1);

		assertEquals(2, aggressor.attackTerritory(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls));
		assertTrue(aggressor.getOccupiedTerritories().contains(attackedTerritory));
		assertEquals(2, aggressor.getOccupiedTerritories().size());
		assertEquals(0, defender.getOccupiedTerritories().size());
		assertTrue(defender.hasLost());
		assertEquals(3, attackingTerritory.getArmies());
		assertEquals(1, attackedTerritory.getArmies());
		
		// Attacker took all 10 cards
		assertEquals(10, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testPlayerCapturesButNotDefeatTakesNoCards() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);

		Territory attackingTerritory = new Territory("attackingTerritory", "Asia");
		Territory attackedTerritory = new Territory("attackedTerritory", "Asia");
		Territory otherTerritory = new Territory("otherTerritory", "North America");
		
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.YELLOW, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.RED, randomMock, cardTrader);
		aggressor.giveArmies(3);
		defender.giveArmies(3);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 2);
		defender.occupyTerritory(attackedTerritory);
		defender.occupyTerritory(otherTerritory);
		defender.addArmiesToTerritory(otherTerritory, 1);
		
		for (int i = 0; i < 7; i++) {
			EasyMock.expect(randomMock.nextInt(3)).andReturn(i % 3);
			EasyMock.expect(randomMock.nextInt(MapManager.getTerritories().size())).andReturn(i % 2);
		}
		
		// Attacker rolls
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(2);
		
		// Defender roll
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		
		EasyMock.replay(randomMock);
		
		// Defender holds 10 cards
		for (int i = 0; i < 5; i++) {
			defender.drawCard();
		}
		aggressor.drawCard();
		aggressor.drawCard();
		
		int [] attackerRolls = aggressor.rollDice(2);
		int [] defenderRolls = defender.rollDice(1);

		assertEquals(1, aggressor.attackTerritory(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls));
		assertTrue(aggressor.getOccupiedTerritories().contains(attackedTerritory));
		assertEquals(2, aggressor.getOccupiedTerritories().size());
		assertEquals(1, defender.getOccupiedTerritories().size());
		assertFalse(defender.hasLost());
		assertEquals(2, attackingTerritory.getArmies());
		assertEquals(1, attackedTerritory.getArmies());
		
		// Attacker took all no cards and already had 2
		assertEquals(2, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testPlayerDefeatsAndTakesThreeCards() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);

		Territory attackingTerritory = new Territory("attackingTerritory", "Asia");
		Territory attackedTerritory = new Territory("attackedTerritory", "Asia");
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.GREEN, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.BLUE, randomMock, cardTrader);
		aggressor.giveArmies(4);
		defender.giveArmies(1);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 3);
		defender.occupyTerritory(attackedTerritory);
		
		for (int i = 0; i < 6; i++) {
			EasyMock.expect(randomMock.nextInt(3)).andReturn(i % 3);
			EasyMock.expect(randomMock.nextInt(MapManager.getTerritories().size())).andReturn(i % 2);
		}
		
		// Attacker rolls
		EasyMock.expect(randomMock.nextInt(6)).andReturn(6);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(4);
		
		// Defender roll
		EasyMock.expect(randomMock.nextInt(6)).andReturn(5);
		
		EasyMock.replay(randomMock);
		
		aggressor.drawCard();
		aggressor.drawCard();
		aggressor.drawCard();
		// Defender holds 3 cards
		for (int i = 0; i < 3; i++) {
			defender.drawCard();
		}
		
		int [] attackerRolls = aggressor.rollDice(2);
		int [] defenderRolls = defender.rollDice(1);

		assertEquals(2, aggressor.attackTerritory(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls));
		assertTrue(aggressor.getOccupiedTerritories().contains(attackedTerritory));
		assertEquals(2, aggressor.getOccupiedTerritories().size());
		assertEquals(0, defender.getOccupiedTerritories().size());
		assertTrue(defender.hasLost());
		assertEquals(3, attackingTerritory.getArmies());
		assertEquals(1, attackedTerritory.getArmies());
		
		// Attacker took all 3 cards to add to their own 3 cards for a total of 6
		assertEquals(6, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
}
