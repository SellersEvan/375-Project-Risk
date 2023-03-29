package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import controller.GameSetup;

public class TradingCardsIntegrationTest {

	@Test
	void testTradeInFirstValidSetNoBonus() {
		Random randomMock = EasyMock.strictMock(Random.class);
		List<Territory> territories = GameSetup.initTerritories();
		
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.BLUE, randomMock, cardTrader);
		player.giveArmies(1);
		// Player occupies Territory 3
		player.occupyTerritory(territories.get(3));
		
		// Card 1: (Territory 0, Infantry)
		// Card 2: (Territory 1, Cavalry)
		// Card 3: (Territory 2, Artillery)
		for (int i = 0; i < 3; i++) {
			EasyMock.expect(randomMock.nextInt(3)).andReturn(i);
			EasyMock.expect(randomMock.nextInt(42)).andReturn(i);
		}
		
		EasyMock.replay(randomMock);
		
		for (int i = 0; i < 3; i++) {
			player.drawCard();
		}
		
		assertTrue(player.tradeInCards(player.getCards()));
		assertEquals(4, player.getArmiesAvailable());
		assertEquals(0, player.getCards().size());
		assertEquals(1, cardTrader.numSetsTurnedIn);
		
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testTradeInSecondValidSetWithBonus() {
		List<Territory> territories = GameSetup.initTerritories();
		
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.RED, null, cardTrader);
		player.giveArmies(2);
		// Player occupies Territory 0 and 3
		player.occupyTerritory(territories.get(0));
		player.occupyTerritory(territories.get(3));
		
		Card card1 = new Card(territories.get(0), CardSymbol.CAVALRY);
		Card card2 = new Card(territories.get(0), CardSymbol.INFANTRY);
		Card card3 = new Card(territories.get(2), CardSymbol.CAVALRY);
		Card card4 = new Card(territories.get(4), CardSymbol.CAVALRY);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		player.getCards().add(card4);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card1);
		tradeInSet.add(card3);
		tradeInSet.add(card4);
		
		// Make this the second set traded in of the game
		cardTrader.numSetsTurnedIn = 1;
		
		assertTrue(player.tradeInCards(tradeInSet));
		assertEquals(8, player.getArmiesAvailable());
		assertEquals(1, player.getCards().size());
		assertEquals(2, cardTrader.numSetsTurnedIn);
	}
	
	@Test
	void testInvalidSet() {
		List<Territory> territories = GameSetup.initTerritories();
		
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.RED, null, cardTrader);
		player.giveArmies(2);
		// Player occupies Territory 1 and 2
		player.occupyTerritory(territories.get(1));
		player.occupyTerritory(territories.get(2));
		
		Card card1 = new Card(territories.get(0), CardSymbol.CAVALRY);
		Card card2 = new Card(territories.get(0), CardSymbol.INFANTRY);
		Card card3 = new Card(territories.get(2), CardSymbol.CAVALRY);
		Card card4 = new Card(territories.get(4), CardSymbol.CAVALRY);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		player.getCards().add(card4);
		
		Set<Card> tradeInSet = new HashSet<Card>();
		tradeInSet.add(card2);
		tradeInSet.add(card3);
		tradeInSet.add(card4);
		
		assertFalse(player.tradeInCards(tradeInSet));
		assertEquals(0, player.getArmiesAvailable());
		assertEquals(4, player.getCards().size());
		assertEquals(0, cardTrader.numSetsTurnedIn);
	}
	
	@Test
	void testTradeInNinthAndTenthSets() {
		List<Territory> territories = GameSetup.initTerritories();
		
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.RED, null, cardTrader);
		player.giveArmies(2);
		// Player occupies Territory 1 and 2
		player.occupyTerritory(territories.get(1));
		player.occupyTerritory(territories.get(2));
		
		Card card1 = new Card(territories.get(1), CardSymbol.CAVALRY);
		Card card2 = new Card(territories.get(2), CardSymbol.INFANTRY);
		Card card3 = new Card(territories.get(3), CardSymbol.CAVALRY);
		Card card4 = new Card(territories.get(4), CardSymbol.CAVALRY);
		Card card5 = new Card(territories.get(1), CardSymbol.ARTILLERY);
		Card card6 = new Card(territories.get(0), CardSymbol.CAVALRY);
		
		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card3);
		player.getCards().add(card4);
		player.getCards().add(card5);
		player.getCards().add(card6);
		
		// One-of-each set with a Territory bonus
		Set<Card> tradeInSet1 = new HashSet<Card>();
		tradeInSet1.add(card1);
		tradeInSet1.add(card2);
		tradeInSet1.add(card5);
		
		// One-of-each set with a Territory bonus
		Set<Card> tradeInSet2 = new HashSet<Card>();
		tradeInSet2.add(card3);
		tradeInSet2.add(card4);
		tradeInSet2.add(card6);
		
		// Make this the ninth set traded in of the game
		cardTrader.numSetsTurnedIn = 8;
		
		// Test turning in the first set 
		assertTrue(player.tradeInCards(tradeInSet1));
		assertEquals(32, player.getArmiesAvailable());
		assertEquals(3, player.getCards().size());
		assertEquals(9, cardTrader.numSetsTurnedIn);
		
		// Test turning in the second set 
		// (32 armies from previous for a total of 67)
		assertTrue(player.tradeInCards(tradeInSet2));
		assertEquals(67, player.getArmiesAvailable());
		assertEquals(0, player.getCards().size());
		assertEquals(10, cardTrader.numSetsTurnedIn);
	}
	
	@Test
	void testTwoPlayersTurnInValidSets() {
		List<Territory> territories = GameSetup.initTerritories();
		
		CardTrader cardTrader = new CardTrader();
		Player player1 = new Player(PlayerColor.RED, null, cardTrader);
		Player player2 = new Player(PlayerColor.GREEN, null, cardTrader);
		player1.giveArmies(2);
		player2.giveArmies(1);
		// Player 1 occupies Territory 1 and 2
		player1.occupyTerritory(territories.get(1));
		player1.occupyTerritory(territories.get(2));
		// Player 2 occupies Territory 41
		player2.occupyTerritory(territories.get(41));
		
		Card card1 = new Card(territories.get(0), CardSymbol.CAVALRY);
		Card card2 = new Card(territories.get(2), CardSymbol.INFANTRY);
		Card card3 = new Card(territories.get(3), CardSymbol.CAVALRY);
		Card card4 = new Card(territories.get(4), CardSymbol.CAVALRY);
		Card card5 = new Card(territories.get(1), CardSymbol.ARTILLERY);
		Card card6 = new Card(territories.get(41), CardSymbol.CAVALRY);
		
		player1.getCards().add(card1);
		player1.getCards().add(card3);
		player1.getCards().add(card4);
		
		player2.getCards().add(card2);
		player2.getCards().add(card5);
		player2.getCards().add(card6);
		
		// One-of-each set with a Territory bonus
		Set<Card> tradeInSet1 = new HashSet<Card>();
		tradeInSet1.add(card1);
		tradeInSet1.add(card3);
		tradeInSet1.add(card4);
		
		// One-of-each set with a Territory bonus
		Set<Card> tradeInSet2 = new HashSet<Card>();
		tradeInSet2.add(card2);
		tradeInSet2.add(card5);
		tradeInSet2.add(card6);
		
		// Make this the ninth set traded in of the game
		cardTrader.numSetsTurnedIn = 4;
		
		// Test Player 1 turning in the first set (no bonus)
		assertTrue(player1.tradeInCards(tradeInSet1));
		assertEquals(12, player1.getArmiesAvailable());
		assertEquals(0, player1.getCards().size());
		assertEquals(5, cardTrader.numSetsTurnedIn);
		
		// Test Player 2 turning in the second set (no bonus)
		assertTrue(player2.tradeInCards(tradeInSet2));
		assertEquals(17, player2.getArmiesAvailable());
		assertEquals(0, player2.getCards().size());
		assertEquals(6, cardTrader.numSetsTurnedIn);
	}
}
