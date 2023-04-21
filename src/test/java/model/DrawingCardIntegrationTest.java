package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import model.Map.Continent;
import model.Map.MapManager;
import model.Map.Territory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

class DrawingCardIntegrationTest {

	@Test
	void testPlayerCapturesAndDrawsNoDefeat() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);
		Continent asia = new Continent("Asia", 1);
		Continent northAmerica = new Continent("North America", 1);

		Territory attackingTerritory = new Territory("attackingTerritory", asia);
		Territory attackedTerritory = new Territory("attackedTerritory", asia);
		Territory otherTerritory = new Territory("otherTerritory", northAmerica);
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.RED, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.GREEN, randomMock, cardTrader);
		aggressor.giveArmies(2);
		defender.giveArmies(2);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 1);
		defender.occupyTerritory(attackedTerritory);
		defender.occupyTerritory(otherTerritory);
		
		// Rolls for the attack
		EasyMock.expect(randomMock.nextInt(6)).andReturn(5);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(4);
		
		// Generation of new card
		EasyMock.expect(randomMock.nextInt(22)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(3)).andReturn(2);
		EasyMock.expect(randomMock.nextInt(MapManager.getInstance().getTerritories().size())).andReturn(0);
		
		EasyMock.replay(randomMock);
		
		int [] attackerRolls = aggressor.rollDice(1);
		int [] defenderRolls = defender.rollDice(1);
		AttackData data = new AttackData(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls);

		assertEquals(0, aggressor.attackTerritory(data));
		assertTrue(aggressor.getOccupiedTerritories().contains(attackedTerritory));
		assertEquals(2, aggressor.getOccupiedTerritories().size());
		assertEquals(1, defender.getOccupiedTerritories().size());
		assertEquals(1, attackingTerritory.getArmies());
		assertEquals(1, attackedTerritory.getArmies());
		
		aggressor.endTurn();
		assertEquals(1, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testPlayerDoesNotCaptureNoDraw() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);
		Continent asia = new Continent("Asia", 1);

		Territory attackingTerritory = new Territory("attackingTerritory", asia);
		Territory attackedTerritory = new Territory("attackedTerritory", asia);
		Territory otherTerritory = new Territory("otherTerritory", asia);
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.RED, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.GREEN, randomMock, cardTrader);
		aggressor.giveArmies(2);
		defender.giveArmies(2);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 1);
		defender.occupyTerritory(attackedTerritory);
		defender.occupyTerritory(otherTerritory);
		
		// Rolls for the attack
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(6);
		
		EasyMock.replay(randomMock);
		
		int [] attackerRolls = aggressor.rollDice(1);
		int [] defenderRolls = defender.rollDice(1);
		AttackData data = new AttackData(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls);

		assertEquals(0, aggressor.attackTerritory(data));
		assertFalse(aggressor.getOccupiedTerritories().contains(attackedTerritory));
		assertEquals(1, aggressor.getOccupiedTerritories().size());
		assertEquals(2, defender.getOccupiedTerritories().size());
		assertEquals(1, attackingTerritory.getArmies());
		assertEquals(1, attackedTerritory.getArmies());
		
		aggressor.endTurn();
		assertEquals(0, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testPlayerCapturesAndDrawsAndTakesDefeatedCard() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);
		Continent asia = new Continent("Asia", 1);

		Territory attackingTerritory = new Territory("attackingTerritory", asia);
		Territory attackedTerritory = new Territory("attackedTerritory", asia);
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.BLUE, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.BLACK, randomMock, cardTrader);
		aggressor.giveArmies(4);
		defender.giveArmies(1);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 3);
		defender.occupyTerritory(attackedTerritory);
		
		// Generation of defender's already owned card
		EasyMock.expect(randomMock.nextInt(22)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(3)).andReturn(0);
		EasyMock.expect(randomMock.nextInt(MapManager.getInstance().getTerritories().size())).andReturn(1);
		
		// Rolls for the attack
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(3);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(5);
		
		// Defense roll
		EasyMock.expect(randomMock.nextInt(6)).andReturn(4);
		
		// Generation of new card
		EasyMock.expect(randomMock.nextInt(22)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(3)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(MapManager.getInstance().getTerritories().size())).andReturn(1);
		
		EasyMock.replay(randomMock);
		
		defender.drawCard();
		
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
		
		aggressor.endTurn();
		// Should have drawn 1 card and taken 1 from the defeated player
		assertEquals(2, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testPlayerWinsAttackButDoesNotCaptureNoDraw() throws InvalidAttackException {
		Random randomMock = EasyMock.strictMock(Random.class);
		Continent asia = new Continent("Asia", 1);

		Territory attackingTerritory = new Territory("attackingTerritory", asia);
		Territory attackedTerritory = new Territory("attackedTerritory", asia);
		attackingTerritory.addAdjacentTerritory(attackedTerritory);
		
		CardTrader cardTrader = new CardTrader();
		
		Player aggressor = new Player(PlayerColor.RED, randomMock, cardTrader);
		Player defender = new Player(PlayerColor.GREEN, randomMock, cardTrader);
		aggressor.giveArmies(3);
		defender.giveArmies(3);
		aggressor.occupyTerritory(attackingTerritory);
		aggressor.addArmiesToTerritory(attackingTerritory, 2);
		defender.occupyTerritory(attackedTerritory);
		defender.addArmiesToTerritory(attackedTerritory, 2);
		
		// Generation of attacker's already owned card
		EasyMock.expect(randomMock.nextInt(22)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(3)).andReturn(2);
		EasyMock.expect(randomMock.nextInt(MapManager.getInstance().getTerritories().size())).andReturn(0);
		
		// Rolls for the attack
		EasyMock.expect(randomMock.nextInt(6)).andReturn(4);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(2);
		
		// Defense rolls
		EasyMock.expect(randomMock.nextInt(6)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(6)).andReturn(4);
		
		EasyMock.replay(randomMock);
		
		aggressor.drawCard();
		int [] attackerRolls = aggressor.rollDice(2);
		int [] defenderRolls = defender.rollDice(2);
		AttackData data = new AttackData(attackingTerritory, attackedTerritory, attackerRolls, defenderRolls);

		assertEquals(0, aggressor.attackTerritory(data));
		assertFalse(aggressor.getOccupiedTerritories().contains(attackedTerritory));
		assertEquals(1, aggressor.getOccupiedTerritories().size());
		assertEquals(1, defender.getOccupiedTerritories().size());
		assertEquals(2, attackingTerritory.getArmies());
		assertEquals(2, attackedTerritory.getArmies());
		
		aggressor.endTurn();
		// Already had one and didn't draw again upon turn end
		assertEquals(1, aggressor.getCards().size());
		
		EasyMock.verify(randomMock);
	}
}
