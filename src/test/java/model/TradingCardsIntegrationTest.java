package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import model.Map.MapManager;
import model.Map.Territory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;


public class TradingCardsIntegrationTest {


	@Test
	void testTradeInFirstValidSetNoBonus() {
		Random randomMock = EasyMock.strictMock(Random.class);
		Territory territory4 = EasyMock.mock(Territory.class);
		List<Territory> territories = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			territories.add(EasyMock.mock(Territory.class));
		}
		territories.set(3, territory4);
		MapManager.getInstance().setTerritories(territories);
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.BLUE, randomMock, cardTrader);
		EasyMock.expect(territory4.hasOccupant()).andReturn(false);
		territory4.addArmies(1);
		territory4.setOccupant(player);
		EasyMock.expectLastCall();
		EasyMock.replay(territory4);
		player.giveArmies(1);
		// Player occupies Territory 3
		player.occupyTerritory(territories.get(3));

		// Card 1: (Territory 0, Infantry)
		// Card 2: (Territory 1, Cavalry)
		// Card 3: (Territory 2, Artillery)
		for (int i = 0; i < 3; i++) {
			EasyMock.expect(randomMock.nextInt(22)).andReturn(10);
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

		EasyMock.verify(randomMock, territory4);
	}

	@Test
	void testTradeInSecondValidSetWithBonus() {
		Territory territory1 = EasyMock.mock(Territory.class);
		Territory territory4 = EasyMock.mock(Territory.class);
		List<Territory> territories = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			territories.add(EasyMock.mock(Territory.class));
		}
		territories.set(0, territory1);
		territories.set(3, territory4);
		MapManager.getInstance().setTerritories(territories);
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.RED, null, cardTrader);
		EasyMock.expect(territory4.hasOccupant()).andReturn(false);
		territory4.addArmies(1);
		territory4.setOccupant(player);
		EasyMock.expect(territory1.hasOccupant()).andReturn(false);
		territory1.addArmies(1);
		territory1.setOccupant(player);
		EasyMock.expectLastCall();
		EasyMock.replay(territory1, territory4);

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

		Set<Card> tradeInSet = new HashSet<>();
		tradeInSet.add(card1);
		tradeInSet.add(card3);
		tradeInSet.add(card4);

		// Make this the second set traded in of the game
		cardTrader.numSetsTurnedIn = 1;

		assertTrue(player.tradeInCards(tradeInSet));
		assertEquals(8, player.getArmiesAvailable());
		assertEquals(1, player.getCards().size());
		assertEquals(2, cardTrader.numSetsTurnedIn);
		EasyMock.verify(territory1, territory4);
	}


	@Test
	void testInvalidSet() {
		Territory territory2 = EasyMock.mock(Territory.class);
		Territory territory3 = EasyMock.mock(Territory.class);
		List<Territory> territories = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			territories.add(EasyMock.mock(Territory.class));
		}
		territories.set(1, territory2);
		territories.set(2, territory3);
		MapManager.getInstance().setTerritories(territories);
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.RED, null, cardTrader);
		EasyMock.expect(territory2.hasOccupant()).andReturn(false);
		territory2.addArmies(1);
		territory2.setOccupant(player);
		EasyMock.expect(territory3.hasOccupant()).andReturn(false);
		territory3.addArmies(1);
		territory3.setOccupant(player);
		EasyMock.expectLastCall();
		EasyMock.replay(territory2, territory3);

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

		Set<Card> tradeInSet = new HashSet<>();
		tradeInSet.add(card2);
		tradeInSet.add(card3);
		tradeInSet.add(card4);

		assertFalse(player.tradeInCards(tradeInSet));
		assertEquals(0, player.getArmiesAvailable());
		assertEquals(4, player.getCards().size());
		assertEquals(0, cardTrader.numSetsTurnedIn);
		EasyMock.verify(territory2, territory3);
	}

	@Test
	void testTradeInNinthSet() {
		Territory territory2 = EasyMock.mock(Territory.class);
		Territory territory3 = EasyMock.mock(Territory.class);
		List<Territory> territories = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			territories.add(EasyMock.mock(Territory.class));
		}
		territories.set(1, territory2);
		territories.set(2, territory3);
		MapManager.getInstance().setTerritories(territories);
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.RED, null, cardTrader);
		EasyMock.expect(territory2.hasOccupant()).andReturn(false);
		territory2.addArmies(1);
		territory2.setOccupant(player);
		EasyMock.expect(territory3.hasOccupant()).andReturn(false);
		territory3.addArmies(1);
		territory3.setOccupant(player);
		EasyMock.expectLastCall();
		EasyMock.replay(territory2, territory3);

		player.giveArmies(2);
		// Player occupies Territory 1 and 2
		player.occupyTerritory(territories.get(1));
		player.occupyTerritory(territories.get(2));

		Card card1 = new Card(territories.get(1), CardSymbol.CAVALRY);
		Card card2 = new Card(territories.get(2), CardSymbol.INFANTRY);
		Card card5 = new Card(territories.get(1), CardSymbol.ARTILLERY);

		player.getCards().add(card1);
		player.getCards().add(card2);
		player.getCards().add(card5);

		// One-of-each set with a Territory bonus
		Set<Card> tradeInSet1 = new HashSet<>();
		tradeInSet1.add(card1);
		tradeInSet1.add(card2);
		tradeInSet1.add(card5);

		// Make this the ninth set traded in of the game
		cardTrader.numSetsTurnedIn = 8;

		// Test turning in the ninth set
		assertTrue(player.tradeInCards(tradeInSet1));
		assertEquals(32, player.getArmiesAvailable());
		assertEquals(0, player.getCards().size());
		assertEquals(9, cardTrader.numSetsTurnedIn);
		EasyMock.verify(territory2, territory3);
	}
	@Test
	void testTradeInTenthSet() {
		Territory territory2 = EasyMock.mock(Territory.class);
		Territory territory3 = EasyMock.mock(Territory.class);
		List<Territory> territories = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			territories.add(EasyMock.mock(Territory.class));
		}
		territories.set(1, territory2);
		territories.set(2, territory3);
		MapManager.getInstance().setTerritories(territories);
		CardTrader cardTrader = new CardTrader();
		Player player = new Player(PlayerColor.RED, null, cardTrader);
		EasyMock.expect(territory2.hasOccupant()).andReturn(false);
		territory2.addArmies(1);
		territory2.setOccupant(player);
		EasyMock.expect(territory3.hasOccupant()).andReturn(false);
		territory3.addArmies(1);
		territory3.setOccupant(player);
		EasyMock.expectLastCall();
		EasyMock.replay(territory2, territory3);

		player.giveArmies(2);
		// Player occupies Territory 1 and 2
		player.occupyTerritory(territories.get(1));
		player.occupyTerritory(territories.get(2));

		Card card3 = new Card(territories.get(3), CardSymbol.CAVALRY);
		Card card4 = new Card(territories.get(4), CardSymbol.CAVALRY);
		Card card6 = new Card(territories.get(0), CardSymbol.CAVALRY);

		player.getCards().add(card3);
		player.getCards().add(card4);
		player.getCards().add(card6);

		// One-of-each set with a Territory bonus
		Set<Card> tradeInSet2 = new HashSet<>();
		tradeInSet2.add(card3);
		tradeInSet2.add(card4);
		tradeInSet2.add(card6);

		// Make this the ninth set traded in of the game
		cardTrader.numSetsTurnedIn = 9;

		// Test turning in the second set
		assertTrue(player.tradeInCards(tradeInSet2));
		assertEquals(35, player.getArmiesAvailable());
		assertEquals(0, player.getCards().size());
		assertEquals(10, cardTrader.numSetsTurnedIn);
		EasyMock.verify(territory2, territory3);
	}


	@Test
	void testTwoPlayersTurnInValidSets() {
		Territory territory2 = EasyMock.mock(Territory.class);
		Territory territory3 = EasyMock.mock(Territory.class);
		Territory territory42 = EasyMock.mock(Territory.class);
		List<Territory> territories = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			territories.add(EasyMock.mock(Territory.class));
		}
		territories.set(1, territory2);
		territories.set(2, territory3);
		territories.set(41, territory42);
		MapManager.getInstance().setTerritories(territories);
		CardTrader cardTrader = new CardTrader();
		Player player1 = new Player(PlayerColor.RED, null, cardTrader);
		Player player2 = new Player(PlayerColor.GREEN, null, cardTrader);
		EasyMock.expect(territory2.hasOccupant()).andReturn(false);
		territory2.addArmies(1);
		territory2.setOccupant(player1);
		EasyMock.expect(territory3.hasOccupant()).andReturn(false);
		territory3.addArmies(1);
		territory3.setOccupant(player1);
		EasyMock.expect(territory42.hasOccupant()).andReturn(false);
		territory42.addArmies(1);
		territory42.setOccupant(player2);
		EasyMock.expectLastCall();
		EasyMock.replay(territory2, territory3, territory42);

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
		Set<Card> tradeInSet1 = new HashSet<>();
		tradeInSet1.add(card1);
		tradeInSet1.add(card3);
		tradeInSet1.add(card4);

		// One-of-each set with a Territory bonus
		Set<Card> tradeInSet2 = new HashSet<>();
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
		EasyMock.verify(territory2, territory3, territory42);
	}
}
