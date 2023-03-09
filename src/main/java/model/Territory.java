package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class Territory {
    static final int MAX_ATTACKS = 3;
    static final int MAX_DEFENDS = 2;
    static final int NORTH_AMERICAN_TERRITORIES = 9;
    static final int SOUTH_AMERICAN_TERRITORIES = 4;
    static final int AFRICAN_TERRITORIES = 6;
    static final int AUSTRALIAN_TERRITORIES = 4;
    static final int ASIAN_TERRITORIES = 12;
    static final int EUROPEAN_TERRITORIES = 7;

    static final int NORTH_AMERICAN_BONUS = 5;
    static final int SOUTH_AMERICAN_BONUS = 2;
    static final int AFRICAN_BONUS = 3;
    static final int AUSTRALIAN_BONUS = 2;
    static final int ASIAN_BONUS = 7;
    static final int EUROPEAN_BONUS = 5;

    private Player controller = null;

    private ArrayList<Territory> adjacentTerritories = new ArrayList<>();
    private int armies;
    private String name;

    public String getName() {
        return name;
    }

    public Player getController() {
        return controller;
    }

    private String continent;

    private static int northAmericanTerritories;
    private static int southAmericanTerritories;
    private static int africanTerritories;
    private static int australianTerritories;
    private static int asianTerritories;
    private static int europeanTerritories;

    @Override
    public String toString() {
        return "Territory Name: " + name + ", Continent: " + continent;
    }

    public Territory(String name, String continent) {
        this.name = name;
        this.continent = continent;
    }

    public boolean isAdjacent(Territory toCheck) {
        return adjacentTerritories.contains(toCheck);
    }

    public void addAdjacentTerritory(Territory t) {
        adjacentTerritories.add(t);
    }

    public void addArmies(int i) {
        this.armies += i;
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
            defender.loseArmies(1);
        } else {
            loseArmies(1);
        }
    }

    private void loseArmies(int i) {
        this.armies -= i;
    }

    public int getArmies() {
        return armies;
    }

    public boolean fortifyTerritory(Territory toFortify, int unitsToMove) {
        if (!checkSameController(toFortify)) {
            return false;
        } else if (unitsToMove > armies - 1 || unitsToMove <= 0) {
            return false;
        }
        loseArmies(unitsToMove);
        toFortify.addArmies(unitsToMove);
        return true;
    }

    public static int calculateArmiesFromContinentBonus(List<Territory> controlledTerritories) {
        resetStaticCounts();
        Stream<String> continents = controlledTerritories.stream().map(Territory::getContinent);
        continents.forEach(Territory::incrementCorrectField);
        int total = 0;
        if (northAmericanTerritories == NORTH_AMERICAN_TERRITORIES) {
            total += NORTH_AMERICAN_BONUS;
        }
        if (southAmericanTerritories == SOUTH_AMERICAN_TERRITORIES) {
            total += SOUTH_AMERICAN_BONUS;
        }
        if (africanTerritories == AFRICAN_TERRITORIES) {
            total += AFRICAN_BONUS;
        }
        if (australianTerritories == AUSTRALIAN_TERRITORIES) {
            total += AUSTRALIAN_BONUS;
        }
        if (asianTerritories == ASIAN_TERRITORIES) {
            total += ASIAN_BONUS;
        }
        if (europeanTerritories == EUROPEAN_TERRITORIES) {
            total += EUROPEAN_BONUS;
        }
        return total;
    }

    private static void resetStaticCounts() {
        northAmericanTerritories = 0;
        southAmericanTerritories = 0;
        africanTerritories = 0;
        australianTerritories = 0;
        asianTerritories = 0;
        europeanTerritories = 0;
    }

    private static void incrementCorrectField(String continent) {
        switch (continent) {
            case "North America":
                northAmericanTerritories++;
                break;
            case "South America":
                southAmericanTerritories++;
                break;
            case "Africa":
                africanTerritories++;
                break;
            case "Australia":
                australianTerritories++;
                break;
            case "Asia":
                asianTerritories++;
                break;
            case "Europe":
                europeanTerritories++;
                break;
        }
    }

    private boolean checkSameController(Territory territory) {
        return controller.getColor() == territory.controller.getColor();
    }

    public boolean isOccupied() {
        return !(controller == null);
    }

    public void setController(Player color) {
        this.controller = color;
    }

    public String getContinent() {
        return continent;
    }
}
