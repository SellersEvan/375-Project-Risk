package model;

import java.util.*;

public class Player {
	static final int HIGHER_GAIN_THRESHOLD = 12;
	static final int DEFAULT_GAIN = 3;
	static final int GAIN_DIVISOR = 3;

	private PlayerColor color;
	private String name;
	private List<Territory> occupiedTerritories;
	private Set<Card> cards;
	private int armiesAvailable;
	private Random random;
	private CardTrader cardTrader;
	boolean capturedThisTurn;
	
	public Player(PlayerColor color, Random random, CardTrader cardTrader) {
		this.color = color;
		this.name = color.toString();
		this.random = random;
		this.cardTrader = cardTrader;
		this.occupiedTerritories = new ArrayList<Territory>();
		this.cards = new HashSet<Card>();
		this.armiesAvailable = 0;
		this.capturedThisTurn = false;
	}
	public Player(PlayerColor color, String name, Random random, CardTrader cardTrader) {
		this.color = color;
		this.name = name;
		this.random = random;
		this.cardTrader = cardTrader;
		this.occupiedTerritories = new ArrayList<Territory>();
		this.cards = new HashSet<Card>();
		this.armiesAvailable = 0;
		this.capturedThisTurn = false;
	}
	
	public PlayerColor getColor() {
		return this.color;
	}
	public String getName() {
		return this.name;
	}
	
	public List<Territory> getOccupiedTerritories() {
		return this.occupiedTerritories;
	}

	public boolean hasLost() {
		return this.occupiedTerritories.isEmpty();
	}

	public boolean hasWon() {
		return this.occupiedTerritories.size() == 42;
	}

	public void giveArmies(int numArmies) {
		this.armiesAvailable += numArmies;
	}
	
	public int getArmiesAvailable() {
		return this.armiesAvailable;
	}

	public boolean occupyTerritory(Territory territory) {
		if (territory.isOccupied() || this.armiesAvailable < 1) {
			return false;
		}
		addOccupiedTerritory(territory);
		this.armiesAvailable--;
		territory.addArmies(1);
		return true;
	}

	public boolean addArmiesToTerritory(Territory territory, int numArmies) {
		if (territory == null) {
			throw new NullPointerException();
		} else if (numArmies <= 0
					|| numArmies > this.armiesAvailable
					|| !this.occupiedTerritories.contains(territory)) {
			return false;
		}
		this.armiesAvailable -= numArmies;
		territory.addArmies(numArmies);
		return true;
	}

	private void addOccupiedTerritory(Territory territory) {
		this.occupiedTerritories.add(territory);
		territory.setController(this);
	}

	public void addNewTurnArmies() {
		giveArmies(calculateArmiesGainedFromTerritoryCount()
				+ Territory.calculateArmiesFromContinentBonus(occupiedTerritories));
	}

	public int calculateArmiesGainedFromTerritoryCount() {
		if (this.occupiedTerritories.size() < HIGHER_GAIN_THRESHOLD) {
			return DEFAULT_GAIN;
		}
		return this.occupiedTerritories.size() / GAIN_DIVISOR;	
	}

	public int[] rollDice(int numberOfDice) {
		if (numberOfDice < 0) {
			throw new IllegalArgumentException("Number of dice must be zero or more.");
		}
		int[] results = new int[numberOfDice];
		for (int i = 0; i < numberOfDice; i++) {
			results[i] = this.random.nextInt(6) + 1;
		}
		return results;
	}
	
	public int attackTerritory(Territory attacking, Territory defending, 
			int[] attackerRolls, int[] defenderRolls) throws InvalidAttackException {
		Player attackingPlayer = attacking.getController();
		Player defendingPlayer = defending.getController();
		if (attackingPlayer.getColor() != this.color) {
			throw new InvalidAttackException("Cannot attack with a Territory in another's control.");
		} else if (attackingPlayer.getColor() == defendingPlayer.getColor()) {
			throw new InvalidAttackException("Cannot attack own Territory.");
		}
		if (attacking.attackTerritory(defending, attackerRolls, defenderRolls)) {
			this.captureDefeatedTerritory(defendingPlayer, attacking, defending);
			return attacking.getArmies() - 1;
		}
		return 0;
	}

	void captureDefeatedTerritory(Player defendingPlayer,
			Territory attacking, Territory defending) {
		defendingPlayer.occupiedTerritories.remove(defending);
		defending.setController(this);
		this.occupiedTerritories.add(defending);
		this.capturedThisTurn = true;
		attacking.fortifyTerritory(defending, 1);
		this.handlePossiblePlayerDefeat(defendingPlayer);
	}
	
	void handlePossiblePlayerDefeat(Player defendingPlayer) {
		if (defendingPlayer.hasLost()) {
			this.takeCardsFromDefeated(defendingPlayer);
		}
	}
	
	void takeCardsFromDefeated(Player defeatedPlayer) {
		this.cards.addAll(defeatedPlayer.cards);
		defeatedPlayer.cards.clear();
	}
	
	public boolean endTurn() {
		if (this.capturedThisTurn) {
			this.drawCard();
			this.capturedThisTurn = false;
			return true;
		}
		return false;
	}
	
	void drawCard() {
		this.cards.add(new Card(this.random));
	}
	
	public Set<Card> getCards() {
		return this.cards;
	}

	public boolean tradeInCards(Set<Card> cardSet) {
		if (cardSet.size() != 3) {
			return false;
		}
		for (Card card : cardSet) {
			if (!this.cards.contains(card)) {
				return false;
			}
		}
		if (!this.cardTrader.tradeInCardSet(this, cardSet)) {
			return false;
		}
		this.cards.removeAll(cardSet);
		return true;
	}
}
