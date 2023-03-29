package model;

import model.Map.Territory;

public class Card {
	
	private Territory picturedTerritory;
	private CardSymbol symbol;
	
	public Card(Territory picturedTerritory, CardSymbol symbol) {
		this.picturedTerritory = picturedTerritory;
		this.symbol = symbol;
	}
	
	public Territory getPicturedTerritory() {
		return this.picturedTerritory;
	}
	
	public CardSymbol getSymbol() {
		return this.symbol;
	}
}