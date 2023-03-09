package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import controller.GameSetup;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

class CardTraderTest {

	@Test
	void testCalculateValueNoSetsTurnedIn() {
		CardTrader cardTrader = new CardTrader(null, null);
		int expected = 4;
		int actual = cardTrader.getCurrentSetValue();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testCalculateValueOneSetTurnedIn() {
		CardTrader cardTrader = new CardTrader(null, null);
		cardTrader.numSetsTurnedIn = 1;
		int expected = 6;
		int actual = cardTrader.getCurrentSetValue();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testCalculateValueFourSetsTurnedIn() {
		CardTrader cardTrader = new CardTrader(null, null);
		cardTrader.numSetsTurnedIn = 4;
		int expected = 12;
		int actual = cardTrader.getCurrentSetValue();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testCalculateValueFiveSetsTurnedIn() {
		CardTrader cardTrader = new CardTrader(null, null);
		cardTrader.numSetsTurnedIn = 5;
		int expected = 15;
		int actual = cardTrader.getCurrentSetValue();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testCalculateValueSixSetsTurnedIn() {
		CardTrader cardTrader = new CardTrader(null, null);
		cardTrader.numSetsTurnedIn = 6;
		int expected = 20;
		int actual = cardTrader.getCurrentSetValue();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testCalculateValueMaxTurnedIn() {
		CardTrader cardTrader = new CardTrader(null, null);
		cardTrader.numSetsTurnedIn = 429496731;
		int expected = Integer.MAX_VALUE - 2;
		int actual = cardTrader.getCurrentSetValue();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testCalculateValueTooManyTurnedIn() {
		CardTrader cardTrader = new CardTrader(null, null);
		cardTrader.numSetsTurnedIn = 429496732;
		
		assertThrows(IllegalStateException.class, () -> {cardTrader.getCurrentSetValue();}, "Too many card sets turned in.");
	}
	
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
	
	private Set<Card> territoryBonusSetup(int setSize, int numMatches, Player player) {
		Set<Card> cardSet = new HashSet<Card>();
		int matchesMade = 0;
		for (int i = 0; i < setSize; i++) {
			Card newCard;
			if (matchesMade < numMatches) {
				newCard = new Card(player.getOccupiedTerritories().get(matchesMade), CardSymbol.INFANTRY);
				matchesMade++;
			} else {
				newCard = new Card(EasyMock.strictMock(Territory.class), CardSymbol.INFANTRY);
			}
			cardSet.add(newCard);
		}
		return cardSet;
	}
	
	@Test
	void testTerritoryBonusOneMatch1() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 3);
		
		assertTrue(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(3, 1, player)));
	}
	
	@Test
	void testTerritoryBonusOneMatch2() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 1);
		
		assertTrue(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(3, 1, player)));
	}
	
	@Test
	void testTerritoryBonusNoTerritories() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		
		assertFalse(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(3, 0, player)));
	}
	
	@Test
	void testTerritoryBonusNoMatches() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 3);
		
		assertFalse(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(3, 0, player)));
	}
	
	@Test
	void testTerritoryBonusMultipleMatches() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 4);
		
		assertTrue(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(3, 2, player)));
	}
	
	@Test
	void testTerritoryBonusNullPlayer() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = null;
		
		Set<Card> cardSet = territoryBonusSetup(3, 0, player);
		assertThrows(NullPointerException.class, () -> {cardTrader.getsTerritoryBonus(player, cardSet);});
	}
	
	@Test
	void testTerritoryBonusNullCardSet() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		
		Set<Card> cardSet = null;
		assertThrows(NullPointerException.class, () -> {cardTrader.getsTerritoryBonus(player, cardSet);});
	}
	
	@Test
	void testTerritoryBonusSetSizeTwo1() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 2);
		
		assertTrue(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(2, 1, player)));
	}
	
	@Test
	void testTerritoryBonusSetSizeTwo2() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		
		assertFalse(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(2, 0, player)));
	}
	
	@Test
	void testTerritoryBonusSetSizeFour() {
		CardTrader cardTrader = new CardTrader(null, null);
		Player player = new Player(PlayerColor.RED, null, null);
		occupyTerritoriesSetup(player, 3);
		
		assertTrue(cardTrader.getsTerritoryBonus(player, territoryBonusSetup(4, 2, player)));
	}
	
	@Test
	void testValidCombinationAllOne1() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		EasyMock.expect(cardTrader.getsTerritoryBonus(player, cardSet)).andReturn(false);
		EasyMock.expect(cardTrader.getCurrentSetValue()).andReturn(4);
		
		EasyMock.replay(cardTrader);
		
		assertTrue(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(4, player.getArmiesAvailable());
		assertEquals(1, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testInvalidCombination1() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.ARTILLERY));
		
		EasyMock.replay(cardTrader);
		
		assertFalse(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(0, player.getArmiesAvailable());
		assertEquals(0, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testValidCombinationOneEach1() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.ARTILLERY));
		EasyMock.expect(cardTrader.getsTerritoryBonus(player, cardSet)).andReturn(false);
		EasyMock.expect(cardTrader.getCurrentSetValue()).andReturn(4);
		
		EasyMock.replay(cardTrader);
		
		assertTrue(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(4, player.getArmiesAvailable());
		assertEquals(1, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testInvalidCombination2() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		cardTrader.numSetsTurnedIn = 3;
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		
		EasyMock.replay(cardTrader);
		
		assertFalse(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(1, player.getArmiesAvailable());
		assertEquals(3, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testValidCombinationAllOne2() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.ARTILLERY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.ARTILLERY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.ARTILLERY));
		EasyMock.expect(cardTrader.getsTerritoryBonus(player, cardSet)).andReturn(false);
		EasyMock.expect(cardTrader.getCurrentSetValue()).andReturn(4);
		
		EasyMock.replay(cardTrader);
		
		assertTrue(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(5, player.getArmiesAvailable());
		assertEquals(1, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testValidCombinationAllOne3() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		cardTrader.numSetsTurnedIn = 1;
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		EasyMock.expect(cardTrader.getsTerritoryBonus(player, cardSet)).andReturn(false);
		EasyMock.expect(cardTrader.getCurrentSetValue()).andReturn(6);
		
		EasyMock.replay(cardTrader);
		
		assertTrue(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(6, player.getArmiesAvailable());
		assertEquals(2, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testValidCombinationOneEach2() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		cardTrader.numSetsTurnedIn = 2;
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(2);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.ARTILLERY));
		EasyMock.expect(cardTrader.getsTerritoryBonus(player, cardSet)).andReturn(false);
		EasyMock.expect(cardTrader.getCurrentSetValue()).andReturn(8);
		
		EasyMock.replay(cardTrader);
		
		assertTrue(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(10, player.getArmiesAvailable());
		assertEquals(3, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testNullPlayerTrade() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		cardTrader.numSetsTurnedIn = 2;
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = null;
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.INFANTRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.ARTILLERY));
		EasyMock.expect(cardTrader.getsTerritoryBonus(player, cardSet)).andReturn(false);
		EasyMock.expect(cardTrader.getCurrentSetValue()).andReturn(8);
		
		EasyMock.replay(cardTrader);
		
		assertThrows(NullPointerException.class, () -> {cardTrader.tradeInCardSet(player, cardSet);});
		assertEquals(2, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testNullCardSetTrade() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Set<Card> cardSet = null;
		
		EasyMock.replay(cardTrader);
		
		assertThrows(NullPointerException.class, () -> {cardTrader.tradeInCardSet(player, cardSet);});
		assertEquals(1, player.getArmiesAvailable());
		assertEquals(0, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testInvalidSetSizeTrade() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		cardTrader.numSetsTurnedIn = 7;
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(1);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		
		EasyMock.replay(cardTrader);
		
		assertFalse(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(1, player.getArmiesAvailable());
		assertEquals(7, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testAddsTerritoryBonus() {
		CardTrader cardTrader = EasyMock.partialMockBuilder(CardTrader.class)
				.addMockedMethod("getCurrentSetValue")
				.addMockedMethod("getsTerritoryBonus")
				.createStrictMock();
		cardTrader.numSetsTurnedIn = 1;
		Territory picturedTerritoryMock = EasyMock.strictMock(Territory.class);
		Player player = new Player(PlayerColor.RED, null, null);
		player.giveArmies(5);
		Set<Card> cardSet = new HashSet<Card>();
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		cardSet.add(new Card(picturedTerritoryMock, CardSymbol.CAVALRY));
		EasyMock.expect(cardTrader.getsTerritoryBonus(player, cardSet)).andReturn(true);
		EasyMock.expect(cardTrader.getCurrentSetValue()).andReturn(6);
		
		EasyMock.replay(cardTrader);
		
		assertTrue(cardTrader.tradeInCardSet(player, cardSet));
		assertEquals(13, player.getArmiesAvailable());
		assertEquals(2, cardTrader.numSetsTurnedIn);
		EasyMock.verify(cardTrader);
	}
	
	@Test
	void testGenerateInfantryCard() {
		Random randomMock = EasyMock.strictMock(Random.class);
		List<Territory> territories = GameSetup.initTerritories();
		CardTrader cardTrader = new CardTrader(randomMock, territories);
		EasyMock.expect(randomMock.nextInt(3)).andReturn(0);
		EasyMock.expect(randomMock.nextInt(territories.size())).andReturn(0);
		
		EasyMock.replay(randomMock);
		
		Card newCard = cardTrader.generateNewCard();
		assertTrue(newCard.getSymbol() == CardSymbol.INFANTRY);
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testGenerateCavalryCard() {
		Random randomMock = EasyMock.strictMock(Random.class);
		List<Territory> territories = GameSetup.initTerritories();
		CardTrader cardTrader = new CardTrader(randomMock, territories);
		EasyMock.expect(randomMock.nextInt(3)).andReturn(1);
		EasyMock.expect(randomMock.nextInt(territories.size())).andReturn(0);
		
		EasyMock.replay(randomMock);
		
		Card newCard = cardTrader.generateNewCard();
		assertTrue(newCard.getSymbol() == CardSymbol.CAVALRY);
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testGenerateArtilleryCard() {
		Random randomMock = EasyMock.strictMock(Random.class);
		List<Territory> territories = GameSetup.initTerritories();
		CardTrader cardTrader = new CardTrader(randomMock, territories);
		EasyMock.expect(randomMock.nextInt(3)).andReturn(2);
		EasyMock.expect(randomMock.nextInt(territories.size())).andReturn(0);
		
		EasyMock.replay(randomMock);
		
		Card newCard = cardTrader.generateNewCard();
		assertTrue(newCard.getSymbol() == CardSymbol.ARTILLERY);
		EasyMock.verify(randomMock);
	}
	
	@Test
	void testAllTerritoriesCanBeGenerated() {
		Random randomMock = EasyMock.strictMock(Random.class);
		List<Territory> territories = GameSetup.initTerritories();
		Set<Territory> generated = new HashSet<Territory>();
		CardTrader cardTrader = new CardTrader(randomMock, territories);
		for (int i = 0; i < 14; i++) {
			EasyMock.expect(randomMock.nextInt(3)).andReturn(0);
			EasyMock.expect(randomMock.nextInt(territories.size())).andReturn(i);
			EasyMock.replay(randomMock);
			
			Card newCard = cardTrader.generateNewCard();
			assertTrue(generated.add(newCard.getPicturedTerritory()));
			assertTrue(newCard.getSymbol() == CardSymbol.INFANTRY);
			EasyMock.verify(randomMock);
			EasyMock.reset(randomMock);
		}
		for (int i = 14; i < 28; i++) {
			EasyMock.expect(randomMock.nextInt(3)).andReturn(1);
			EasyMock.expect(randomMock.nextInt(territories.size())).andReturn(i);
			EasyMock.replay(randomMock);
			
			Card newCard = cardTrader.generateNewCard();
			assertTrue(generated.add(newCard.getPicturedTerritory()));
			assertTrue(newCard.getSymbol() == CardSymbol.CAVALRY);
			EasyMock.verify(randomMock);
			EasyMock.reset(randomMock);
		}
		for (int i = 28; i < 42; i++) {
			EasyMock.expect(randomMock.nextInt(3)).andReturn(2);
			EasyMock.expect(randomMock.nextInt(territories.size())).andReturn(i);
			EasyMock.replay(randomMock);
			
			Card newCard = cardTrader.generateNewCard();
			assertTrue(generated.add(newCard.getPicturedTerritory()));
			assertTrue(newCard.getSymbol() == CardSymbol.ARTILLERY);
			EasyMock.verify(randomMock);
			EasyMock.reset(randomMock);
		}
		for (Territory territory : territories) {
			assertTrue(generated.contains(territory));
		}
	}
}
