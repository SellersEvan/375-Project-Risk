package controller;

import model.*;
import model.InvalidAttackException;
import model.Map.*;
import model.Player;
import view.GameView;
import view.TerritoryButton;
import java.util.*;

public class Game {
    protected Phase currentPhase;
    private GameView gameView;
    private MapLoader map;
    protected TerritoryButton selectedButton;

    protected PlayerController playerController;
    protected TerritoryController territoryController;
    protected ContinentController continentController;

    protected GameSetup gameSetup;
    protected ResourceBundle messages;

    public Game(int numberOfPlayers, MapLoader map, ArrayList<Player> players) {
        gameSetup = new GameSetup(numberOfPlayers);
        playerController = new PlayerController(numberOfPlayers, players);
        setupMap(map);
    }
    public Game(int numberOfPlayers, MapLoader map) {
        gameSetup = new GameSetup(numberOfPlayers);
        playerController = new PlayerController(numberOfPlayers, gameSetup.fillPlayerArray(numberOfPlayers));
        setupMap(map);

    }
    public void setupMap(MapLoader map) {
        gameSetup.setInitialArmies();
        for (Player p: playerController.getPlayerArray()) {
            p.giveArmies(gameSetup.getArmiesPerPlayer());
        }
        this.map         = map;
        territoryController = new TerritoryController(this.map.getTerritories());
        continentController = new ContinentController(this.map.getContinents());

        setFirstPlayer(new Random());
        currentPhase = Phase.territoryClaim;
    }


    public Game(int numberOfPlayers) {
        this(numberOfPlayers, new MapLoaderYAML(MapLoader.getMapFiles().get("Earth")));
    }
    public Game(int numberOfPlayers, ArrayList<Player> players) {
        this(numberOfPlayers, new MapLoaderYAML(MapLoader.getMapFiles().get("Earth")), players);
    }


    public void initWindow() {
        gameView = new GameView(this, this.map);
        updateGameView();
        gameView.showMessage(playerController.getCurrentPlayer().getName() + " "
                + messages.getString("playerWillStartFirstMessage"));
    }

    public void updateGameView() {
        Player player = playerController.getCurrentPlayer();
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
        if (territoryController.getSelectedTerritory() == null) {
            if (!territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                gameView.showMessage(messages.getString("selectOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                gameView.showMessage(messages.getString("attackTerritoryMessage"));
            } else {
                territoryController.setSelectedTerritory(territory);
                selectedButton = button;
            }
        } else {
            if (territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                gameView.showMessage(messages.getString("cannotAttackOwnTerritoryMessage"));
            } else if (!territoryController.getSelectedTerritory().isAdjacentTerritory(territory)) {
                gameView.showMessage(messages.getString("attackNonAdjacentTerritoryMessage"));
            } else {
                Player defender = territory.getOccupant();
                int attack = gameView.getNumberOfDice(territoryController.getSelectedTerritory().getArmies(),
                        playerController.getCurrentPlayer().getName(), true);
                int defend = gameView.getNumberOfDice(territory.getArmies(),
                        territory.getOccupant().getName(), false);
                AttackData data = new AttackData(territoryController.getSelectedTerritory(), territory, attack, defend);
                int[] attackRolls = data.getADice();
                int[] defendRolls = data.getDDice();
                gameView.displayRolls(attackRolls, defendRolls);
                int attemptAttack = playerController.getCurrentPlayer().
                        attackTerritory(data);
                if (attemptAttack != 0) {
                    int additional = -1;
                    while (additional < 0 || additional > attemptAttack) {
                        additional = gameView.getNumber(messages.getString("conqueredTerritoryMessage")
                                + attemptAttack);
                        if (additional < 0 || additional > attemptAttack) {
                            gameView.showMessage(messages.getString("invalidAmountMessage"));
                        }
                        territoryController.getSelectedTerritory().fortifyTerritory(territory, additional);
                    }
                }
                if (defender.hasLost()) {
                    gameView.showMessage(defender.getName() + " " + messages.getString("hasLostMessage"));
                    removeDefeatedPlayer(playerController.getIndexOfPlayer(defender));
                }
                if (playerController.getCurrentPlayer().hasWon()) {
                    gameView.showMessage(playerController.getCurrentPlayer().getName()
                            + " " + messages.getString("hasWonMessage"));
                    currentPhase = Phase.gameOver;
                    updateGameView();
                }
                selectedButton.updateDisplay();
                button.updateDisplay();
            }
            territoryController.setSelectedTerritory(null);
            selectedButton = null;
        }
    }

    private void fortifying(Territory territory, TerritoryButton button) {
        if (territoryController.getSelectedTerritory() == null) {
            if (!territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                gameView.showMessage(messages.getString("fortifyFromOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                gameView.showMessage(messages.getString("fortifyInvalidArmiesMessage"));
            } else {
                territoryController.setSelectedTerritory(null);
                selectedButton = button;
            }
        } else {
            if (!territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                gameView.showMessage(messages.getString("fortifyToOwnTerritoryMessage"));
            } else if (!territoryController.getSelectedTerritory().isAdjacentTerritory(territory)) {
                gameView.showMessage(messages.getString("fortifyAdjacentTerritoryMessage"));
            } else {
                int additional = -1;
                int max = territoryController.getSelectedTerritory().getArmies() - 1;
                while (additional < 0 || additional > max) {
                    additional = gameView.getNumber(messages.getString("fortifyCountMessage") + max);
                    if (additional < 0 || additional > max) {
                        gameView.showMessage(messages.getString("invalidAmountMessage"));
                    }
                    territoryController.getSelectedTerritory().fortifyTerritory(territory, additional);
                }
                selectedButton.updateDisplay();
                button.updateDisplay();
                phaseAction();
            }
            territoryController.setSelectedTerritory(null);
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
                if (playerController.getNumberOfCardForCurrentPlayer() >= 5) {
                    gameView.showMessage(messages.getString("mustTrade"));
                    break;
                }
                currentPhase = Phase.placeArmies;
                playerController.addNewTurnArmiesForCurrentPlayer(continentController.getContinents());
                updateGameView();
                gameView.showMessage(messages.getString("tradeCardGainMessage") + " "
                        + playerController.getCurrentPlayer().getArmiesAvailable()
                        + " " + messages.getString("tradeCardArmiesMessage"));
                break;
            case placeArmies:
                gameView.showMessage(messages.getString("placeAllArmiesMessage"));
                break;
            case attacking:
                playerController.getCurrentPlayer().endTurn();
                currentPhase = Phase.fortifying;
                territoryController.setSelectedTerritory(null);
                selectedButton = null;
                while (playerController.getNumberOfCardForCurrentPlayer() > 6) {
                    gameView.showMessage(messages.getString("mustTrade"));
                    gameView.openCardTradeDisplay();
                }
                updateGameView();
                break;
            case fortifying:
                currentPhase = Phase.tradeCards;
                territoryController.setSelectedTerritory(null);
                selectedButton = null;
                changeTurn();
                updateGameView();
                break;
        }
    }

    private void territoryClaim(Territory territory, TerritoryButton button) {
        if (playerController.doesPlayerOccupyTerritory(territory)) {
            button.setPlayer(playerController.getCurrentPlayerColor());
            changeTurn();
            checkIfAllClaimed();
        } else {
            gameView.showMessage(messages.getString("unableToClaimTerritoryMessage"));
        }
    }

    private void placeArmies(Territory territory, TerritoryButton button) {
        if (territory.getOccupant().equals(playerController.getCurrentPlayer())) {
            int amount = gameView.getNumber(messages.getString("howManyArmiesMessage"));
            if (!playerController.addArmiesToTerritoryForCurrentPlayer(territory, amount)) {
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
                if (playerController.getArmiesAvailableForCurrentPlayer() == 0) {
                    changeTurn();
                }
                if (playerController.getArmiesAvailableForCurrentPlayer() == 0) {
                    currentPhase = Phase.tradeCards;
                }
                break;
            case placeArmies:
                if (playerController.getArmiesAvailableForCurrentPlayer() == 0) {
                    currentPhase = Phase.attacking;
                }
        }
    }


    private void checkIfAllClaimed() {
        if (territoryController.checkIfAllClaimed())
            return;
        currentPhase = Phase.initialArmies;
        updateGameView();
    }

    private void changeTurn() {
        nextPlayer();
        updateGameView();
    }

    void nextPlayer() {
        playerController.nextPlayer();
    }

    public void setFirstPlayer(Random r) {
        int startingPlayer = r.nextInt(playerController.getNumberOfPlayers()) + 1;
        playerController.setCurrentPlayer(gameSetup.getPlayerWhoGoesFirst(startingPlayer));
    }


    public void removeDefeatedPlayer(int playerNum) throws IllegalArgumentException {
        playerController.removeDefeatedPlayer(playerNum);
    }

    public void setLanguageBundle(String bundleName) {
        this.messages = ResourceBundle.getBundle(bundleName);
    }

    public ResourceBundle getLanguageBundle() {
        return this.messages;
    }

    public Player getCurrentPlayer() {
        return playerController.getCurrentPlayer();
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }
}
