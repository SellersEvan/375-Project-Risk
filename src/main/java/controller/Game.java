package controller;

import model.*;
import model.InvalidAttackException;
import model.Map.*;
import model.Map.World;
import model.Player;
import view.GameView;
import view.TerritoryButton;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class Game {
    protected Phase phase;
    private GameView ui;
    protected TerritoryButton selectedButton;
    protected ResourceBundle bundle;

    private final World world;
    protected PlayerController playerController;
    protected TerritoryController territoryController;
    protected ContinentController continentController;



    // [x] remove extra parameter on player control for number of players
    // [x] setup window
    // [x] change setup of resource bundle
    // [x] remove game setup
    // [X] Clean Up Main
    // [ ] Enable UI to use Phase instead of just string
    // [ ] remove button system
    // [ ] make methods private as needed
    // [ ] Split up Large Methods
    // [ ] testing


    public Game(World world, List<Player> players) {
        this.playerController    = new PlayerController(players);
        this.phase               = Phase.territoryClaim;
        this.world               = world;
        this.territoryController = new TerritoryController(world.getTerritories());
        this.continentController = new ContinentController(world.getContinents());
    }


    public void setupUI(ResourceBundle bundle, Class<GameView> viewClass) {
        this.bundle = bundle;
        try {
            this.ui = viewClass.getConstructor(Game.class, World.class).newInstance(this, world);
        } catch (NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public void begin() {
        this.update();
        String playerName = this.playerController.getCurrentPlayer().getName();
        String message    = this.bundle.getString("playerWillStartFirstMessage");
        this.ui.showMessage(String.format("%s %s", playerName, message));
    }


    public void update() {
        Player player = playerController.getCurrentPlayer();
        this.ui.setPhase(phase.toString());
        this.ui.setPlayer(player.getName(), player.getArmiesAvailable());
    }


    public void territoryAction(Territory territory, TerritoryButton button) throws InvalidAttackException {
        switch (phase) {
            case initialArmies:
                break;
            case territoryClaim:
                this.territoryClaim(territory, button);
                break;
            case placeArmies:
                this.placeArmies(territory, button);
                break;
            case attacking:
                this.attacking(territory, button);
                break;
            case fortifying:
                this.fortifying(territory, button);
                break;
        }
    }


    private void attacking(Territory territory, TerritoryButton button) throws InvalidAttackException {
        if (territoryController.getSelectedTerritory() == null) {
            if (!territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                ui.showMessage(bundle.getString("selectOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                ui.showMessage(bundle.getString("attackTerritoryMessage"));
            } else {
                territoryController.setSelectedTerritory(territory);
                selectedButton = button;
            }
        } else {
            if (territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                ui.showMessage(bundle.getString("cannotAttackOwnTerritoryMessage"));
            } else if (!territoryController.getSelectedTerritory().isAdjacentTerritory(territory)) {
                ui.showMessage(bundle.getString("attackNonAdjacentTerritoryMessage"));
            } else {
                Player defender = territory.getOccupant();
                int attack = ui.getNumberOfDice(territoryController.getSelectedTerritory().getArmies(),
                        playerController.getCurrentPlayer().getName(), true);
                int defend = ui.getNumberOfDice(territory.getArmies(),
                        territory.getOccupant().getName(), false);
                AttackData data = new AttackData(territoryController.getSelectedTerritory(), territory, attack, defend);
                int[] attackRolls = data.getADice();
                int[] defendRolls = data.getDDice();
                ui.displayRolls(attackRolls, defendRolls);
                int attemptAttack = playerController.getCurrentPlayer().
                        attackTerritory(data);
                if (attemptAttack != 0) {
                    int additional = -1;
                    while (additional < 0 || additional > attemptAttack) {
                        additional = ui.getNumber(bundle.getString("conqueredTerritoryMessage")
                                + attemptAttack);
                        if (additional < 0 || additional > attemptAttack) {
                            ui.showMessage(bundle.getString("invalidAmountMessage"));
                        }
                        territoryController.getSelectedTerritory().fortifyTerritory(territory, additional);
                    }
                }
                if (defender.hasLost()) {
                    ui.showMessage(defender.getName() + " " + bundle.getString("hasLostMessage"));
                    removeDefeatedPlayer(playerController.getIndexOfPlayer(defender));
                }
                if (playerController.getCurrentPlayer().hasWon()) {
                    ui.showMessage(playerController.getCurrentPlayer().getName()
                            + " " + bundle.getString("hasWonMessage"));
                    phase = Phase.gameOver;
                    update();
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
                ui.showMessage(bundle.getString("fortifyFromOwnTerritoryMessage"));
            } else if (territory.getArmies() <= 1) {
                ui.showMessage(bundle.getString("fortifyInvalidArmiesMessage"));
            } else {
                territoryController.setSelectedTerritory(null);
                selectedButton = button;
            }
        } else {
            if (!territory.getOccupant().equals(playerController.getCurrentPlayer())) {
                ui.showMessage(bundle.getString("fortifyToOwnTerritoryMessage"));
            } else if (!territoryController.getSelectedTerritory().isAdjacentTerritory(territory)) {
                ui.showMessage(bundle.getString("fortifyAdjacentTerritoryMessage"));
            } else {
                int additional = -1;
                int max = territoryController.getSelectedTerritory().getArmies() - 1;
                while (additional < 0 || additional > max) {
                    additional = ui.getNumber(bundle.getString("fortifyCountMessage") + max);
                    if (additional < 0 || additional > max) {
                        ui.showMessage(bundle.getString("invalidAmountMessage"));
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
                ui.showMessage(bundle.getString("selectTerritoryToClaimMessage"));
                break;
            case initialArmies:
                ui.showMessage(bundle.getString("selectPlaceArmiesMessage"));
                break;
            case tradeCards:
                if (playerController.getNumberOfCardForCurrentPlayer() >= 5) {
                    ui.showMessage(bundle.getString("mustTrade"));
                    break;
                }
                phase = Phase.placeArmies;
                playerController.addNewTurnArmiesForCurrentPlayer(continentController.getContinents());
                update();
                ui.showMessage(bundle.getString("tradeCardGainMessage") + " "
                        + playerController.getCurrentPlayer().getArmiesAvailable()
                        + " " + bundle.getString("tradeCardArmiesMessage"));
                break;
            case placeArmies:
                ui.showMessage(bundle.getString("placeAllArmiesMessage"));
                break;
            case attacking:
                playerController.getCurrentPlayer().endTurn();
                phase = Phase.fortifying;
                territoryController.setSelectedTerritory(null);
                selectedButton = null;
                while (playerController.getNumberOfCardForCurrentPlayer() > 6) {
                    ui.showMessage(bundle.getString("mustTrade"));
                    ui.openCardTradeDisplay();
                }
                update();
                break;
            case fortifying:
                phase = Phase.tradeCards;
                territoryController.setSelectedTerritory(null);
                selectedButton = null;
                changeTurn();
                update();
                break;
        }
    }


    private void territoryClaim(Territory territory, TerritoryButton button) {
        if (playerController.setPlayerOccupyTerritory(territory)) {
            button.setPlayer(playerController.getCurrentPlayerColor());
            changeTurn();
            checkIfAllClaimed();
        } else {
            ui.showMessage(bundle.getString("unableToClaimTerritoryMessage"));
        }
    }


    private void placeArmies(Territory territory, TerritoryButton button) {
        if (territory.getOccupant().equals(playerController.getCurrentPlayer())) {
            int amount = ui.getNumber(bundle.getString("howManyArmiesMessage"));
            if (!playerController.addArmiesToTerritoryForCurrentPlayer(territory, amount)) {
                ui.showMessage(bundle.getString("invalidAmountOfArmiesMessage"));
                return;
            }
            checkIfAllArmiesPlaced();
            update();
            button.updateDisplay();
        } else {

            ui.showMessage(bundle.getString("doNotControlTerritoryMessage"));
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
        update();
    }


    private void changeTurn() {
        nextPlayer();
        update();
    }


    void nextPlayer() {
        playerController.nextPlayer();
    }


    public void removeDefeatedPlayer(int playerNum) throws IllegalArgumentException {
        playerController.removeDefeatedPlayer(playerNum);
    }


    public ResourceBundle getBundle() {
        return this.bundle;
    }


    public Player getCurrentPlayer() {
        return playerController.getCurrentPlayer();
    }


    public Phase getPhase() {
        return phase;
    }


}
