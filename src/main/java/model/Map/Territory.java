package model.Map;

import model.InvalidAttackException;
import model.Player;

import java.util.ArrayList;
import java.util.Arrays;


public class Territory {


    private static final int MAX_ATTACKS = 3;
    private static final int MAX_DEFENDS = 2;

    private final String name;
    private final Continent continent;
    private final ArrayList<Territory> adjacentTerritories;
    private Player occupant;
    private int armies;


    public Territory(String name, Continent continent) {
        this.name                = name;
        this.continent           = continent;
        this.armies              = 0;
        this.occupant            = null;
        this.adjacentTerritories = new ArrayList<>();
    }


    public String getName() {
        return this.name;
    }


    public Continent getContinent() {
        return this.continent;
    }


    public void setOccupant(Player player) {
        this.occupant = player;
    }


    public Player getOccupant() {
        return this.occupant;
    }


    public boolean hasOccupant() {
        return this.occupant != null;
    }


    private boolean hasSameOccupant(Territory territory) {
        return this.occupant.equals(territory.occupant);
    }


    public void addAdjacentTerritory(Territory territory) {
        this.adjacentTerritories.add(territory);
    }


    public boolean isAdjacentTerritory(Territory toCheck) {
        return this.adjacentTerritories.contains(toCheck);
    }


    public void addArmies(int numberOfArmies) {
        this.armies += numberOfArmies;
    }


    private void removeArmies(int numberOfArmies) {
        this.armies -= numberOfArmies;
    }


    public int getArmies() {
        return this.armies;
    }



    // Returns true if attacked territory has lost all troops
    public boolean attackTerritory(Territory defender, int[] attackerRolls, int[] defenderRolls)
            throws InvalidAttackException {

        if (attackerRolls.length == 0 || defenderRolls.length == 0
                || attackerRolls.length > MAX_ATTACKS || defenderRolls.length > MAX_DEFENDS) {
            throw new InvalidAttackException("Invalid roll values");
        }
        if (attackerRolls.length >= this.armies) {
            throw new InvalidAttackException(
                    "Attacker does not possess enough troops for that many rolls");
        } else if (defenderRolls.length > defender.armies) {
            throw new InvalidAttackException(
                    "Defender does not possess enough troops for that many rolls");
        }
        Arrays.sort(attackerRolls);
        Arrays.sort(defenderRolls);
        handleAttackRolls(defender, attackerRolls, defenderRolls);

        return defender.armies == 0;
    }

    private void handleAttackRolls(Territory defender, int[] attackerRolls, int[] defenderRolls) {
        removeLosingArmy(defender,
                attackerRolls[attackerRolls.length - 1], defenderRolls[defenderRolls.length - 1]);
        if (attackerRolls.length > 1 && defenderRolls.length > 1) {
            removeLosingArmy(defender,
                attackerRolls[attackerRolls.length - 2], defenderRolls[defenderRolls.length - 2]);
        }
    }

    private void removeLosingArmy(Territory defender, int attackerValue, int defenderValue) {
        if (attackerValue > defenderValue) {
            defender.removeArmies(1);
        } else {
            removeArmies(1);
        }
    }


    public boolean fortifyTerritory(Territory toFortify, int unitsToMove) {
        if (!hasSameOccupant(toFortify)) {
            return false;
        } else if (unitsToMove > armies - 1 || unitsToMove <= 0) {
            return false;
        }
        removeArmies(unitsToMove);
        toFortify.addArmies(unitsToMove);
        return true;
    }


    @Override
    public String toString() {
        return "Territory Name: " + name + ", Continent: " + continent;
    }


}
