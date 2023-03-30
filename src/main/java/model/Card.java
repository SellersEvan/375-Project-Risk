package model;

import model.Map.MapManager;
import model.Map.Territory;

import java.util.List;
import java.util.Random;

public class Card {

	static final int WILD_CARD_RATIO = 22; // One in 22 cards are wild cards
	private Random random;
	private final Territory picturedTerritory;
	private final CardSymbol symbol;

	public Card(Random random) {
		this.random = random;
		this.symbol = pickRandomSymbol();
		this.picturedTerritory = pickRandomTerritory();
	}

	public Card(Territory picturedTerritory, CardSymbol symbol) {
		this.symbol = symbol;
		this.picturedTerritory = picturedTerritory;
	}

	private CardSymbol pickRandomSymbol() {
		boolean isWildCard = (this.random.nextInt(WILD_CARD_RATIO) == 0);
		if (isWildCard) {
			return CardSymbol.WILD;
		}
		return CardSymbol.values()[this.random.nextInt(3)];
	}

	private Territory pickRandomTerritory() {
		List<Territory> choices = MapManager.getInstance().getTerritories();
		return choices.get(this.random.nextInt(choices.size()));
	}

	public Territory getPicturedTerritory() {
		return this.picturedTerritory;
	}
	
	public CardSymbol getSymbol() {
		return this.symbol;
	}
}