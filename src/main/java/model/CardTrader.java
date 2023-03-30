package model;

import model.Map.Territory;

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

	private boolean cardSetIsValid(Set<Card> cardSet) {
		if (cardSet.size() != 3) {
			return false;
		}
		CardSymbol[] setSymbols = new CardSymbol[3];

		int index = 0;
		for (Card card : cardSet) {
			CardSymbol currentSymbol = card.getSymbol();
			if (currentSymbol == CardSymbol.WILD) {
				return true;
			}
			setSymbols[index] = currentSymbol;
			index++;
		}
		boolean isValid = cardSetIsThreeOfAKind(setSymbols) || cardSetIsOneOfEach(setSymbols);
		return isValid;
	}

	private boolean cardSetIsThreeOfAKind(CardSymbol[] setSymbols) {
		boolean allSame = (setSymbols[0] == setSymbols[1]) && (setSymbols[1] == setSymbols[2]);
		return allSame;
	}

	private boolean cardSetIsOneOfEach(CardSymbol[] setSymbols) {
		boolean oneOfEach = (setSymbols[0] != setSymbols[1])
				&& (setSymbols[1] != setSymbols[2])
				&& (setSymbols[0] != setSymbols[2]);
		return oneOfEach;
	}

	public boolean tradeInCardSet(Player player, Set<Card> cardSet) {
		if (this.cardSetIsValid(cardSet)) {
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
