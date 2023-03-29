package model;

import model.Map.Territory;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class CardTrader {
	
	int numSetsTurnedIn;
	Random random;
	List<Territory> territories;
	
	public CardTrader(Random random, List<Territory> territories) {
		this.numSetsTurnedIn = 0;
		this.random = random;
		this.territories = territories;
	}
	
	public int getCurrentSetValue() {
		if (this.numSetsTurnedIn < 5) {
			return 4 + this.numSetsTurnedIn * 2;
		} else if (this.numSetsTurnedIn < 429496732) {
			return (this.numSetsTurnedIn - 2) * 5;
		}
		throw new IllegalStateException("Too many card sets turned in.");
	}
	
	public boolean getsTerritoryBonus(Player player, Set<Card> cardSet) {
		List<Territory> occupiedTerritories = player.getOccupiedTerritories();
		for (Card card : cardSet) {
			if (occupiedTerritories.contains(card.getPicturedTerritory())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean tradeInCardSet(Player player, Set<Card> cardSet) {
		int numInfantry = 0;
		int numCavalry = 0;
		int numArtillery = 0;
		
		for (Card card : cardSet) {
			switch (card.getSymbol()) {
				case INFANTRY:
					numInfantry++;
					break;
				case CAVALRY:
					numCavalry++;
					break;
				case ARTILLERY:
					numArtillery++;
					break;
			}
		}
		
		if ((numInfantry == 3 || numArtillery == 3 || numCavalry == 3) 
				|| (numInfantry == 1 && numCavalry == 1 && numArtillery == 1)) {
			if (this.getsTerritoryBonus(player, cardSet)) {
				player.giveArmies(2);
			}
			player.giveArmies(this.getCurrentSetValue());
			this.numSetsTurnedIn++;
			return true;
		}
		
		return false;
	}
	
	public Card generateNewCard() {
		CardSymbol symbol = null;
		switch (this.random.nextInt(3)) {
			case 0:
				symbol = CardSymbol.INFANTRY;
				break;
			case 1:
				symbol = CardSymbol.CAVALRY;
				break;
			case 2:
				symbol = CardSymbol.ARTILLERY;
				break;
		}
		return new Card(this.territories.get(this.random.nextInt(this.territories.size())), symbol);
	}
}
