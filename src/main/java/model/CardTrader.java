package model;

import java.util.List;
import java.util.Set;

public class CardTrader {
	
	int numSetsTurnedIn;
	
	public CardTrader() {
		this.numSetsTurnedIn = 0;
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
}
