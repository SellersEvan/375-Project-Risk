package controller;

import model.*;
import model.InvalidAttackException;
import model.Map.*;
import model.Map.World;
import model.Player;
import view.GameView;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class Game {
    protected Phase phase;
    private GameView ui;
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
    // [x] Enable UI to use Phase instead of just string
    // [x] remove button system
    // [x] make methods private as needed
    // [x] Split up Large Methods
    // [x] Draw Path
    // [x] Add Button Outline
    // [x] Manual Testing
    // [ ] Add Additional Map
    // [x] Simplify removeDefeatedPlayer
    // [ ] WTF phaseAction
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
        this.ui.setDetails(player.getName(), player.getArmiesAvailable(), phase.toString());
    }


    public void territoryAction(Territory territory) throws InvalidAttackException {
        switch (phase) {
            case territoryClaim:
                this.claimTerritory(territory);
                break;
            case initialArmies:
            case placeArmies:
                this.placeArmies(territory);
                break;
            case attacking:
                this.selectAttack(territory);
                break;
            case fortifying:
                this.selectFortify(territory);
                break;
        }
    }


    public void selectAttack(Territory territory) throws InvalidAttackException {
        if (this.territoryController.getSelectedTerritory() != null) {
            this.ui.selectTerritory(this.territoryController.getSelectedTerritory(), territory);
            this.attack(this.territoryController.getSelectedTerritory(), territory);
            this.territoryController.setSelectedTerritory(null);
            this.ui.selectTerritory();
        } else if (!territory.getOccupant().equals(playerController.getCurrentPlayer())) {
            this.ui.selectTerritory();
            this.ui.showMessage(bundle.getString("selectOwnTerritoryMessage"));
        } else if (territory.getArmies() <= 1) {
            this.ui.selectTerritory();
            this.ui.showMessage(bundle.getString("attackTerritoryMessage"));
        } else {
            this.ui.selectTerritory(territory);
            this.territoryController.setSelectedTerritory(territory);
        }
    }


    public void attack(Territory territoryAttacker, Territory territoryDefender) throws InvalidAttackException {
        Player defender = territoryDefender.getOccupant();
        Player attacker = territoryAttacker.getOccupant();
        if (defender.equals(attacker)) {
            this.ui.showMessage(this.bundle.getString("cannotAttackOwnTerritoryMessage"));
            return;
        } else if (!territoryAttacker.isAdjacentTerritory(territoryDefender)) {
            this.ui.showMessage(this.bundle.getString("attackNonAdjacentTerritoryMessage"));
            return;
        }

        int attack = this.ui.getNumberOfDice(territoryAttacker.getArmies(), attacker.getName(), true);
        int defend = this.ui.getNumberOfDice(territoryDefender.getArmies(), defender.getName(), false);
        AttackData battle = new AttackData(territoryAttacker, territoryDefender, attack, defend);
        int[] attackRolls = battle.getADice();
        int[] defendRolls = battle.getDDice();
        this.ui.displayRolls(attackRolls, defendRolls);

        int attemptAttack = attacker.attackTerritory(battle);
        if (attemptAttack != 0) {
            int additional = -1;
            while (additional < 0 || additional > attemptAttack) {
                additional = this.ui.getNumber(bundle.getString("conqueredTerritoryMessage") + attemptAttack);
                if (additional < 0 || additional > attemptAttack) {
                    this.ui.showMessage(bundle.getString("invalidAmountMessage"));
                }
                territoryAttacker.fortifyTerritory(territoryDefender, additional);
            }
        }

        if (defender.hasLost()) {
            this.ui.showMessage(defender.getName() + " " + bundle.getString("hasLostMessage"));
            this.playerController.removePlayer(defender);
        }

        if (attacker.hasWon()) {
            this.ui.showMessage(attacker.getName() + " " + bundle.getString("hasWonMessage"));
            this.phase = Phase.gameOver;
            this.update();
        }

        this.ui.updateTerritoryButtons();
    }


    public void selectFortify(Territory territory) {
        if (this.territoryController.getSelectedTerritory() != null) {
            this.ui.selectTerritory(this.territoryController.getSelectedTerritory(), territory);
            this.fortify(this.territoryController.getSelectedTerritory(), territory);
            this.territoryController.setSelectedTerritory(null);
            this.ui.selectTerritory();
        } else if (!territory.getOccupant().equals(this.playerController.getCurrentPlayer())) {
            this.ui.showMessage(this.bundle.getString("fortifyFromOwnTerritoryMessage"));
            this.ui.selectTerritory();
        } else if (territory.getArmies() <= 1) {
            this.ui.showMessage(this.bundle.getString("fortifyInvalidArmiesMessage"));
            this.ui.selectTerritory();
        }
        this.territoryController.setSelectedTerritory(territory);
        this.ui.selectTerritory(territory);
    }


    private void fortify(Territory territoryFrom, Territory territoryTo) {
        if (!territoryFrom.getOccupant().equals(territoryTo.getOccupant())) {
            this.ui.showMessage(this.bundle.getString("fortifyToOwnTerritoryMessage"));
            return;
        } else if (!territoryFrom.isAdjacentTerritory(territoryTo)) {
            this.ui.showMessage(this.bundle.getString("fortifyAdjacentTerritoryMessage"));
            return;
        }
        int additional = -1;
        int max = territoryFrom.getArmies() - 1;
        while (additional < 0 || additional > max) {
            additional = this.ui.getNumber(this.bundle.getString("fortifyCountMessage") + max);
            if (additional < 0 || additional > max) {
                this.ui.showMessage(this.bundle.getString("invalidAmountMessage"));
            }
            territoryFrom.fortifyTerritory(territoryTo, additional);
        }
        this.phaseAction();
        this.ui.updateTerritoryButtons();
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
                while (playerController.getNumberOfCardForCurrentPlayer() > 6) {
                    ui.showMessage(bundle.getString("mustTrade"));
                    ui.openCardTradeDisplay();
                }
                update();
                break;
            case fortifying:
                phase = Phase.tradeCards;
                territoryController.setSelectedTerritory(null);
                changeTurn();
                update();
                break;
        }
    }


    private void claimTerritory(Territory territory) {
        if (this.playerController.setPlayerOccupyTerritory(territory)) {
            this.changeTurn();
            this.checkIfAllClaimed();
        } else {
            this.ui.showMessage(bundle.getString("unableToClaimTerritoryMessage"));
        }
    }


    private void placeArmies(Territory territory) {
        if (territory.getOccupant().equals(playerController.getCurrentPlayer())) {
            int amount = this.ui.getNumber(this.bundle.getString("howManyArmiesMessage"));
            if (!this.playerController.addArmiesToTerritoryForCurrentPlayer(territory, amount)) {
                this.ui.showMessage(this.bundle.getString("invalidAmountOfArmiesMessage"));
                return;
            }
            this.checkIfAllArmiesPlaced();
            this.update();
        } else {
            this.ui.showMessage(bundle.getString("doNotControlTerritoryMessage"));
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
        this.territoryController.setSelectedTerritory(null);
        this.playerController.nextPlayer();
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
