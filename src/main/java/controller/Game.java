package controller;

import model.*;
import model.InvalidAttackException;
import model.Map.*;
import model.Map.World;
import model.Player;
import view.GameView;
import view.TerritoryButton;
import java.util.*;


public class Game {
    protected Phase phase;
    private GameView view;
    private World world;
    protected TerritoryButton selectedButton;

    protected PlayerController playerController;
    protected TerritoryController territoryController;
    protected ContinentController continentController;

    protected GameSetup gameSetup;
    protected ResourceBundle resource;


    // [ ] remove button system
    // [ ] remove extra paramater on player control for number of players
    // [ ] setup window
    // [ ] change setup of resource bundle
    // [ ] remove game setup
    // [ ] make methods private as needed
    // [ ] testing


//    public Game(List<Player> players, World world, ResourceBundle messages, GameView view) {
//        this.playerController = new PlayerController(players);
//        this.gameView         = view;
//        this.
//        this.setupWorld();
//    }
//

    public Game(int numberOfPlayers, World world, ArrayList<Player> players) {
        this.gameSetup   = new GameSetup(numberOfPlayers);
        playerController = new PlayerController(players);
        setupWorld(world);
    }


    private void setupWorld(World world) {
        gameSetup.setInitialArmies();
        for (Player p: playerController.getPlayers()) {
            p.giveArmies(gameSetup.getArmiesPerPlayer());
        }
        this.world          = world;
        territoryController = new TerritoryController(this.world.getTerritories());
        continentController = new ContinentController(this.world.getContinents());
        setFirstPlayer(new Random());
        phase = Phase.territoryClaim;
    }


    public void initWindow() {
        view = new GameView(this, this.world);
        updateGameView();
        view.showMessage(playerController.getCurrentPlayer().getName() + " "
                + resource.getString("playerWillStartFirstMessage"));
    }


    public void updateGameView() {
        Player player = playerController.getCurrentPlayer();
        view.setPhase(phase.toString());
        view.setPlayer(player.getName(), player.getArmiesAvailable());
    }


    public void territoryAction(Territory territory, TerritoryButton button) throws InvalidAttackException {
        switch (phase) {
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
                view.showMessage(resource.getString("selectOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                view.showMessage(resource.getString("attackTerritoryMessage"));
            } else {
                territoryController.setSelectedTerritory(territory);
                selectedButton = button;
            }
        } else {
            if (territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                view.showMessage(resource.getString("cannotAttackOwnTerritoryMessage"));
            } else if (!territoryController.getSelectedTerritory().isAdjacentTerritory(territory)) {
                view.showMessage(resource.getString("attackNonAdjacentTerritoryMessage"));
            } else {
                Player defender = territory.getOccupant();
                int attack = view.getNumberOfDice(territoryController.getSelectedTerritory().getArmies(),
                        playerController.getCurrentPlayer().getName(), true);
                int defend = view.getNumberOfDice(territory.getArmies(),
                        territory.getOccupant().getName(), false);
                AttackData data = new AttackData(territoryController.getSelectedTerritory(), territory, attack, defend);
                int[] attackRolls = data.getADice();
                int[] defendRolls = data.getDDice();
                view.displayRolls(attackRolls, defendRolls);
                int attemptAttack = playerController.getCurrentPlayer().
                        attackTerritory(data);
                if (attemptAttack != 0) {
                    int additional = -1;
                    while (additional < 0 || additional > attemptAttack) {
                        additional = view.getNumber(resource.getString("conqueredTerritoryMessage")
                                + attemptAttack);
                        if (additional < 0 || additional > attemptAttack) {
                            view.showMessage(resource.getString("invalidAmountMessage"));
                        }
                        territoryController.getSelectedTerritory().fortifyTerritory(territory, additional);
                    }
                }
                if (defender.hasLost()) {
                    view.showMessage(defender.getName() + " " + resource.getString("hasLostMessage"));
                    removeDefeatedPlayer(playerController.getIndexOfPlayer(defender));
                }
                if (playerController.getCurrentPlayer().hasWon()) {
                    view.showMessage(playerController.getCurrentPlayer().getName()
                            + " " + resource.getString("hasWonMessage"));
                    phase = Phase.gameOver;
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
                view.showMessage(resource.getString("fortifyFromOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                view.showMessage(resource.getString("fortifyInvalidArmiesMessage"));
            } else {
                territoryController.setSelectedTerritory(null);
                selectedButton = button;
            }
        } else {
            if (!territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                view.showMessage(resource.getString("fortifyToOwnTerritoryMessage"));
            } else if (!territoryController.getSelectedTerritory().isAdjacentTerritory(territory)) {
                view.showMessage(resource.getString("fortifyAdjacentTerritoryMessage"));
            } else {
                int additional = -1;
                int max = territoryController.getSelectedTerritory().getArmies() - 1;
                while (additional < 0 || additional > max) {
                    additional = view.getNumber(resource.getString("fortifyCountMessage") + max);
                    if (additional < 0 || additional > max) {
                        view.showMessage(resource.getString("invalidAmountMessage"));
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
        switch (phase) {
            case territoryClaim:
                view.showMessage(resource.getString("selectTerritoryToClaimMessage"));
                break;
            case initialArmies:
                view.showMessage(resource.getString("selectPlaceArmiesMessage"));
                break;
            case tradeCards:
                if (playerController.getNumberOfCardForCurrentPlayer() >= 5) {
                    view.showMessage(resource.getString("mustTrade"));
                    break;
                }
                phase = Phase.placeArmies;
                playerController.addNewTurnArmiesForCurrentPlayer(continentController.getContinents());
                updateGameView();
                view.showMessage(resource.getString("tradeCardGainMessage") + " "
                        + playerController.getCurrentPlayer().getArmiesAvailable()
                        + " " + resource.getString("tradeCardArmiesMessage"));
                break;
            case placeArmies:
                view.showMessage(resource.getString("placeAllArmiesMessage"));
                break;
            case attacking:
                playerController.getCurrentPlayer().endTurn();
                phase = Phase.fortifying;
                territoryController.setSelectedTerritory(null);
                selectedButton = null;
                while (playerController.getNumberOfCardForCurrentPlayer() > 6) {
                    view.showMessage(resource.getString("mustTrade"));
                    view.openCardTradeDisplay();
                }
                updateGameView();
                break;
            case fortifying:
                phase = Phase.tradeCards;
                territoryController.setSelectedTerritory(null);
                selectedButton = null;
                changeTurn();
                updateGameView();
                break;
        }
    }


    private void territoryClaim(Territory territory, TerritoryButton button) {
        if (playerController.setPlayerOccupyTerritory(territory)) {
            button.setPlayer(playerController.getCurrentPlayerColor());
            changeTurn();
            checkIfAllClaimed();
        } else {
            view.showMessage(resource.getString("unableToClaimTerritoryMessage"));
        }
    }


    private void placeArmies(Territory territory, TerritoryButton button) {
        if (territory.getOccupant().equals(playerController.getCurrentPlayer())) {
            int amount = view.getNumber(resource.getString("howManyArmiesMessage"));
            if (!playerController.addArmiesToTerritoryForCurrentPlayer(territory, amount)) {
                view.showMessage(resource.getString("invalidAmountOfArmiesMessage"));
                return;
            }
            checkIfAllArmiesPlaced();
            updateGameView();
            button.updateDisplay();
        } else {

            view.showMessage(resource.getString("doNotControlTerritoryMessage"));
        }
    }


    private void checkIfAllArmiesPlaced() {
        switch (phase) {
            case initialArmies:
                if (playerController.getArmiesAvailableForCurrentPlayer() == 0) {
                    changeTurn();
                }
                if (playerController.getArmiesAvailableForCurrentPlayer() == 0) {
                    phase = Phase.tradeCards;
                }
                break;
            case placeArmies:
                if (playerController.getArmiesAvailableForCurrentPlayer() == 0) {
                    phase = Phase.attacking;
                }
        }
    }


    private void checkIfAllClaimed() {
        if (territoryController.checkIfAllClaimed())
            return;
        phase = Phase.initialArmies;
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
        this.resource = ResourceBundle.getBundle(bundleName);
    }


    public ResourceBundle getLanguageBundle() {
        return this.resource;
    }


    public Player getCurrentPlayer() {
        return playerController.getCurrentPlayer();
    }


    public Phase getPhase() {
        return phase;
    }


}
