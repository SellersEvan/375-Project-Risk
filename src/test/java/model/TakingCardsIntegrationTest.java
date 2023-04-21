package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import controller.Setup;
import model.Map.Continent;
import model.Map.MapManager;
import model.Map.Territory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TakingCardsIntegrationTest {
	Random randomMock;
	Territory attackingTerritory;
	Territory attackedTerritory;
	Player aggressor;
	Player defender;
	CardTrader cardTrader;

	@BeforeAll
	static void setupMap() {
		Setup.defaultWorld();
	}

	@BeforeEach
	void doSetup(){
		randomMock = EasyMock.strictMock(Random.class);

		Continent asia = EasyMock.mock(Continent.class);
		attackingTerritory = new Territory("attackingTerritory", asia);
		attackedTerritory = new Territory("attackedTerritory", asia);
		attackingTerritory.addAdjacentTerritory(attackedTerritory);

		cardTrader = new CardTrader();

		aggressor = new Player(PlayerColor.GREEN, randomMock, cardTrader);
		defender = new Player(PlayerColor.BLUE, randomMock, cardTrader);
		aggressor.giveArmies(5);
		defender.giveArmies(2);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 3);
		defender.occupyTerritory(attackedTerritory);
	}
	@Test
	void testPlayerDefeatsAndNoCardsToTake() throws InvalidAttackException {
		aggressor.addArmiesToTerritory(attackingTerritory, 1);
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
		AttackData data = new AttackData(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls);

		assertEquals(3, aggressor.attackTerritory(data));
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
		for (int i = 0; i < 10; i++) {
			EasyMock.expect(randomMock.nextInt(22)).andReturn(21);
			EasyMock.expect(randomMock.nextInt(3)).andReturn(i % 3);
			EasyMock.expect(randomMock.nextInt(MapManager.getInstance().getTerritories().size())).andReturn(i % 2);
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
		AttackData data = new AttackData(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls);

		assertEquals(2, aggressor.attackTerritory(data));
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
        Continent asia = EasyMock.mock(Continent.class);
        Continent northAmerica = EasyMock.mock(Continent.class);

		Territory attackingTerritory = new Territory("attackingTerritory", asia);
		Territory attackedTerritory = new Territory("attackedTerritory", asia);
		Territory otherTerritory = new Territory("otherTerritory", northAmerica);

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
			EasyMock.expect(randomMock.nextInt(22)).andReturn(21);
			EasyMock.expect(randomMock.nextInt(3)).andReturn(i % 3);
			EasyMock.expect(randomMock.nextInt(MapManager.getInstance().getTerritories().size())).andReturn(i % 2);
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
		AttackData data = new AttackData(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls);

		assertEquals(1, aggressor.attackTerritory(data));
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
		for (int i = 0; i < 6; i++) {
			EasyMock.expect(randomMock.nextInt(22)).andReturn(21);
			EasyMock.expect(randomMock.nextInt(3)).andReturn(i % 3);
			EasyMock.expect(randomMock.nextInt(MapManager.getInstance().getTerritories().size())).andReturn(i % 2);
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
		AttackData data = new AttackData(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls);

		assertEquals(2, aggressor.attackTerritory(data));
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
