package model;

import model.Map.MapManager;
import model.Map.Territory;

import java.util.List;
import java.util.Random;

public class Card {

	private Random random;
	private final Territory picturedTerritory;
	private final CardSymbol symbol;

	public Card() {
		this.random = new Random();
		this.symbol = pickRandomSymbol();
		this.picturedTerritory = pickRandomTerritory();
	}

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
		return CardSymbol.values()[this.random.nextInt(3)];
	}

	private Territory pickRandomTerritory() {
		List<Territory> choices = MapManager.getTerritories();
		return choices.get(this.random.nextInt(choices.size()));
	}

	public Territory getPicturedTerritory() {
		return this.picturedTerritory;
	}
	
	public CardSymbol getSymbol() {
		return this.symbol;
	}
}