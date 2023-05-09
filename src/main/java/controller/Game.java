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
    protected GameView ui;
    protected ResourceBundle bundle;

    private final World world;
    protected PlayerController playerController;
    protected TerritoryController territoryController;
    protected ContinentController continentController;


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


    protected void selectAttack(Territory territory) throws InvalidAttackException {
        if (this.territoryController.getSelectedTerritory() != null) {
            this.ui.selectTerritory(this.territoryController.getSelectedTerritory(), territory);
            this.attack(this.territoryController.getSelectedTerritory(), territory);
            this.territoryController.setSelectedTerritory(null);
            this.ui.selectTerritory();
        } else if (!playerController.getCurrentPlayer().equals(territory.getOccupant())) {
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


    protected void attack(Territory territoryAttacker, Territory territoryDefender) throws InvalidAttackException {
        Player defender = territoryDefender.getOccupant();
        Player attacker = territoryAttacker.getOccupant();
        if (defender.equals(attacker)) {
            this.ui.showMessage(this.bundle.getString("cannotAttackOwnTerritoryMessage"));
            return;
        } else if (!territoryAttacker.isAdjacentTerritory(territoryDefender)) {
            this.ui.showMessage(this.bundle.getString("attackNonAdjacentTerritoryMessage"));
            return;
        }

        int attackerDice = this.ui.getNumberOfDice(territoryAttacker.getArmies(), attacker.getName(), true);
        int defenderDice = this.ui.getNumberOfDice(territoryDefender.getArmies(), defender.getName(), false);
        int[] attackRolls = territoryAttacker.getOccupant().rollDice(attackerDice);
        int[] defendRolls = territoryDefender.getOccupant().rollDice(defenderDice);
        AttackData battle = new AttackData(territoryAttacker, territoryDefender, attackRolls, defendRolls);
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


    protected void selectFortify(Territory territory) {
        if (this.territoryController.getSelectedTerritory() != null) {
            this.ui.selectTerritory(this.territoryController.getSelectedTerritory(), territory);
            this.fortify(this.territoryController.getSelectedTerritory(), territory);
            this.territoryController.setSelectedTerritory(null);
            this.ui.selectTerritory();
        } else if (!this.playerController.getCurrentPlayer().equals(territory.getOccupant())) {
            this.ui.showMessage(this.bundle.getString("fortifyFromOwnTerritoryMessage"));
            this.ui.selectTerritory();
        } else if (territory.getArmies() <= 1) {
            this.ui.showMessage(this.bundle.getString("fortifyInvalidArmiesMessage"));
            this.ui.selectTerritory();
        } else {
            this.territoryController.setSelectedTerritory(territory);
            this.ui.selectTerritory(territory);
        }
    }


    protected void fortify(Territory territoryFrom, Territory territoryTo) {
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


    protected void claimTerritory(Territory territory) {
        if (this.playerController.setPlayerOccupyTerritory(territory)) {
            this.changeTurn();
            this.allTerritoriesClaimed();
        } else {
            this.ui.showMessage(bundle.getString("unableToClaimTerritoryMessage"));
        }
    }


    protected void placeArmies(Territory territory) {
        if (playerController.getCurrentPlayer().equals(territory.getOccupant())) {
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


    protected void checkIfAllArmiesPlaced() {
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


    protected void allTerritoriesClaimed() {
        if (!territoryController.allTerritoriesClaimed())
            return;
        phase = Phase.initialArmies;
        update();
    }


    protected void changeTurn() {
        nextPlayer();
        update();
    }


    protected void nextPlayer() {
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
