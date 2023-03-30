package controller;

import model.InvalidAttackException;
import model.Map.*;
import model.Player;
import view.GameView;
import view.TerritoryButton;

import java.io.File;
import java.util.*;

public class Game {
    protected Phase currentPhase;
    private GameView gameView;
    private final List<Territory> territories;
    private final List<Continent> continents;
    private Territory selectedTerritory;
    protected TerritoryButton selectedButton;

    protected ArrayList<Player> playerArray;
    protected int numberOfPlayers;
    protected int current;
    protected GameSetup gameSetup;

    protected ResourceBundle messages;

    public Game(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        gameSetup = new GameSetup(numberOfPlayers);
        gameSetup.setInitialArmies();

        File map = MapLoader.getMapFiles().get("Earth");
        MapLoader mapLoader = new MapLoaderYAML(map);
        this.territories = mapLoader.getTerritories();
        this.continents  = mapLoader.getContinents();

        playerArray = gameSetup.fillPlayerArray(territories);
        setFirstPlayer(new Random());

        currentPhase = Phase.territoryClaim;
    }

    public void initWindow() {
        gameView = new GameView(this);
        gameView.addButtons(territories, this);
        updateGameView();
        gameView.showMessage(messages.getString(playerArray.get(current).getName()) + " "
                + messages.getString("playerWillStartFirstMessage"));
    }

    public void updateGameView() {
        Player player = playerArray.get(current);
        gameView.setPhase(currentPhase.toString());
        gameView.setPlayer(player.getName(), player.getArmiesAvailable());
    }

    public void territoryAction(Territory territory, TerritoryButton button) throws InvalidAttackException {
        switch (currentPhase) {
            case territoryClaim:
                territoryClaim(territory, button);
                break;
            case initialArmies:
            case placeArmies:
                placeArmies(territory, button);
                break;
            case attacking:
                attacking(territory, button);
                break;
            case fortifying:
                fortifying(territory, button);
                break;
        }
    }

    private void attacking(Territory territory, TerritoryButton button) throws InvalidAttackException {
        if (selectedTerritory == null) {
            if (!territory.getOccupant().equals(playerArray.get(current))) {
                gameView.showMessage(messages.getString("selectOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                gameView.showMessage(messages.getString("attackTerritoryMessage"));
            } else {
                selectedTerritory = territory;
                selectedButton = button;
            }
        } else {
            if (territory.getOccupant().equals(playerArray.get(current))) {
                gameView.showMessage(messages.getString("cannotAttackOwnTerritoryMessage"));
            } else if (!selectedTerritory.isAdjacentTerritory(territory)) {
                gameView.showMessage(messages.getString("attackNonAdjacentTerritoryMessage"));
            } else {
                Player defender = territory.getOccupant();
                int attack = gameView.getNumberOfDice(selectedTerritory.getArmies(),
                        playerArray.get(current).getName(), true);
                int defend = gameView.getNumberOfDice(territory.getArmies(),
                        territory.getController().getName(), false);
                int[] attackRolls = playerArray.get(current).rollDice(attack);
                int[] defendRolls = territory.getOccupant().rollDice(defend);
                gameView.displayRolls(attackRolls, defendRolls);
                int attemptAttack = playerArray.get(current).attackTerritory(selectedTerritory,
                        territory, attackRolls, defendRolls);
                if (attemptAttack != 0) {
                    int additional = -1;
                    while (additional < 0 || additional > attemptAttack) {
                        additional = gameView.getNumber(messages.getString("conqueredTerritoryMessage")
                                + attemptAttack);
                        if (additional < 0 || additional > attemptAttack) {
                            gameView.showMessage(messages.getString("invalidAmountMessage"));
                        }
                        selectedTerritory.fortifyTerritory(territory, additional);
                    }
                }
                if (defender.hasLost()) {
                    gameView.showMessage(defender.getName() + " " + messages.getString("hasLostMessage"));
                    removeDefeatedPlayer(playerArray.indexOf(defender));
                }
                if (playerArray.get(current).hasWon()) {
                    gameView.showMessage(playerArray.get(current).getName()
                            + " " + messages.getString("hasWonMessage"));
                    currentPhase = Phase.gameOver;
                    updateGameView();
                }
                selectedButton.updateDisplay();
                button.updateDisplay();
            }
            selectedTerritory = null;
            selectedButton = null;
        }
    }

    private void fortifying(Territory territory, TerritoryButton button) {
        if (selectedTerritory == null) {
            if (!territory.getOccupant().equals(playerArray.get(current))) {
                gameView.showMessage(messages.getString("fortifyFromOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                gameView.showMessage(messages.getString("fortifyInvalidArmiesMessage"));
            } else {
                selectedTerritory = territory;
                selectedButton = button;
            }
        } else {
            if (!territory.getOccupant().equals(playerArray.get(current))) {
                gameView.showMessage(messages.getString("fortifyToOwnTerritoryMessage"));
            } else if (!selectedTerritory.isAdjacentTerritory(territory)) {
                gameView.showMessage(messages.getString("fortifyAdjacentTerritoryMessage"));
            } else {
                int additional = -1;
                int max = selectedTerritory.getArmies() - 1;
                while (additional < 0 || additional > max) {
                    additional = gameView.getNumber(messages.getString("fortifyCountMessage") + max);
                    if (additional < 0 || additional > max) {
                        gameView.showMessage(messages.getString("invalidAmountMessage"));
                    }
                    selectedTerritory.fortifyTerritory(territory, additional);
                }
                selectedButton.updateDisplay();
                button.updateDisplay();
                phaseAction();
            }
            selectedTerritory = null;
            selectedButton = null;
        }
    }

    public void phaseAction() {
        switch (currentPhase) {
            case territoryClaim:
                gameView.showMessage(messages.getString("selectTerritoryToClaimMessage"));
                break;
            case initialArmies:
                gameView.showMessage(messages.getString("selectPlaceArmiesMessage"));
                break;
            case tradeCards:
                if (playerArray.get(current).getCards().size() >= 5) {
                    gameView.showMessage(messages.getString("mustTrade"));
                    break;
                }
                currentPhase = Phase.placeArmies;
                playerArray.get(current).addNewTurnArmies(this.continents);
                updateGameView();
                gameView.showMessage(messages.getString("tradeCardGainMessage") + " "
                        + playerArray.get(current).getArmiesAvailable()
                        + " " + messages.getString("tradeCardArmiesMessage"));
                break;
            case placeArmies:
                gameView.showMessage(messages.getString("placeAllArmiesMessage"));
                break;
            case attacking:
                playerArray.get(current).endTurn();
                currentPhase = Phase.fortifying;
                selectedTerritory = null;
                selectedButton = null;
                while (playerArray.get(current).getCards().size() > 6) {
                    gameView.showMessage(messages.getString("mustTrade"));
                    gameView.openCardTradeDisplay();
                }
                updateGameView();
                break;
            case fortifying:
                currentPhase = Phase.tradeCards;
                selectedTerritory = null;
                selectedButton = null;
                changeTurn();
                updateGameView();
                break;
        }
    }

    private void territoryClaim(Territory territory, TerritoryButton button) {
        if (playerArray.get(current).occupyTerritory(territory)) {
            button.setPlayer(playerArray.get(current).getColor());
            changeTurn();
            checkIfAllClaimed();
        } else {
            gameView.showMessage(messages.getString("unableToClaimTerritoryMessage"));
        }
    }

    private void placeArmies(Territory territory, TerritoryButton button) {
        if (territory.getOccupant().equals(playerArray.get(current))) {
            int amount = gameView.getNumber(messages.getString("howManyArmiesMessage"));
            if (!playerArray.get(current).addArmiesToTerritory(territory, amount)) {
                gameView.showMessage(messages.getString("invalidAmountOfArmiesMessage"));
                return;
            }
            checkIfAllArmiesPlaced();
            updateGameView();
            button.updateDisplay();
        } else {

            gameView.showMessage(messages.getString("doNotControlTerritoryMessage"));
        }
    }


    private void checkIfAllArmiesPlaced() {
        switch (currentPhase) {
            case initialArmies:
                if (playerArray.get(current).getArmiesAvailable() == 0) {
                    changeTurn();
                }
                if (playerArray.get(current).getArmiesAvailable() == 0) {
                    currentPhase = Phase.tradeCards;
                }
                break;
            case placeArmies:
                if (playerArray.get(current).getArmiesAvailable() == 0) {
                    currentPhase = Phase.attacking;
                }
        }
    }


    private void checkIfAllClaimed() {
        for (Territory territory : territories) {
            if (!territory.hasOccupant()) return;
        }
        currentPhase = Phase.initialArmies;
        updateGameView();
    }

    private void changeTurn() {
        nextPlayer();
        updateGameView();
    }

    void nextPlayer() {
        if (++current == playerArray.size()) {
            current = 0;
        }
    }

    public void setFirstPlayer(Random r) {
        int startingPlayer = r.nextInt(numberOfPlayers) + 1;
        current = gameSetup.getPlayerWhoGoesFirst(startingPlayer);
    }


    public void removeDefeatedPlayer(int playerNum) throws IllegalArgumentException {
        Player currentPlayer = playerArray.get(current);
        if (playerNum < 0 || playerNum > numberOfPlayers - 1) {
            throw new IllegalArgumentException("player number must be between "
                    + "0 and the number of players minus one");
        }
        playerArray.remove(playerNum);
        numberOfPlayers--;
        current = playerArray.indexOf(currentPlayer);
    }

    public void setLanguageBundle(String bundleName) {
        this.messages = ResourceBundle.getBundle(bundleName);
    }

    public ResourceBundle getLanguageBundle() {
        return this.messages;
    }

    public Player getCurrentPlayer() {
        return playerArray.get(current);
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }
}
