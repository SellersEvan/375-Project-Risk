package model;

import controller.GameSetup;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryTest {

    @Test
    public void testInvalidTerritoryIdNotAdjacent(){
        Territory mainTerritory = new Territory("Test", "Test");
        Territory toCheck = new Territory("Test", "Test");

        assertFalse(mainTerritory.isAdjacent(toCheck));
    }

    @Test
    public void testValidTerritoryIdNotAdjacent(){
        Territory mainTerritory = new Territory("Test", "Test");
        Territory adjTerritory = new Territory("Test", "Test");
        mainTerritory.addAdjacentTerritory(adjTerritory);
        Territory toCheck = new Territory("Test", "Test");

        assertFalse(mainTerritory.isAdjacent(toCheck));
    }

    @Test
    public void testValidTerritoryIdIsAdjacent(){
        Territory mainTerritory = new Territory("Test", "Test");
        Territory toCheck = new Territory("Test", "Test");
        mainTerritory.addAdjacentTerritory(toCheck);

        assertTrue(mainTerritory.isAdjacent(toCheck));
    }

    @Test
    public void testValidTerritoryIdIsAdjacentLargerList(){
        Territory mainTerritory = new Territory("Test", "Test");
        Territory toCheck = new Territory("Test", "Test");
        mainTerritory.addAdjacentTerritory(toCheck);
        Territory adjTerritory = new Territory("Test", "Test");
        mainTerritory.addAdjacentTerritory(adjTerritory);

        assertTrue(mainTerritory.isAdjacent(toCheck));
    }

    @Test
    public void testInvalidTerritoryIdIsAdjacentLargerList(){
        Territory mainTerritory = new Territory("Test", "Test");
        Territory adjTerritory1 = new Territory("Test", "Test");
        Territory adjTerritory2 = new Territory("Test", "Test");
        mainTerritory.addAdjacentTerritory(adjTerritory1);
        mainTerritory.addAdjacentTerritory(adjTerritory2);

        Territory toCheck = new Territory("Test", "Test");

        assertFalse(mainTerritory.isAdjacent(toCheck));
    }

    @Test
    public void testMultipleValidAdjacentTerritories(){
        Territory mainTerritory = new Territory("Test", "Test");
        Territory adjTerritory = new Territory("Test", "Test");
        Territory firstTrue = new Territory("Test", "Test");
        Territory secondTrue = new Territory("Test", "Test");
        Territory firstFalse = new Territory("Test", "Test");
        Territory secondFalse = new Territory("Test", "Test");


        mainTerritory.addAdjacentTerritory(firstTrue);
        mainTerritory.addAdjacentTerritory(adjTerritory);
        mainTerritory.addAdjacentTerritory(secondTrue);



        assertTrue(mainTerritory.isAdjacent(firstTrue));
        assertTrue(mainTerritory.isAdjacent(secondTrue));
        assertFalse(mainTerritory.isAdjacent(firstFalse));
        assertFalse(mainTerritory.isAdjacent(secondFalse));
    }

    @Test
    public void test20ValidAdjacentTerritories(){
        Territory mainTerritory = new Territory("Test", "Test");
        Territory[] territories = new Territory[20];

        for (int i = 0; i < 20; i++) {
            territories[i] = new Territory("Test", "Test");
        }
        for (int i = 0; i < 10; i++) {
            mainTerritory.addAdjacentTerritory(territories[i]);
        }

        for (int i = 0; i < 20; i++) {
            if (i < 10){
                assertTrue(mainTerritory.isAdjacent(territories[i]));
            } else {
                assertFalse(mainTerritory.isAdjacent(territories[i]));
            }
        }
    }

    @Test
    public void testAttackTerritoryAttackerHas0Armies(){
        Territory attacker = new Territory("Test", "Test");
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(2);
        int[] attackerRolls = new int[]{1};
        int[] defenderRolls = new int[]{1};


        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(defender, attackerRolls, defenderRolls));

        assertEquals("Attacker does not possess enough troops for that many rolls", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderHas0Armies(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(2);
        Territory defender = new Territory("Test", "Test");
        int[] attackerRolls = new int[]{1};
        int[] defenderRolls = new int[]{1};


        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(defender, attackerRolls, defenderRolls));

        assertEquals("Defender does not possess enough troops for that many rolls", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryInvalidRolls(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(1);
        Territory defender = new Territory("Test", "Test");
        int[] attackerRolls = new int[]{};
        int[] defenderRolls = new int[]{};


        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(defender, attackerRolls, defenderRolls));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderInvalidRoll(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(1);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(1);
        int[] attackerRolls = new int[]{1};
        int[] defenderRolls = new int[]{};

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(defender, attackerRolls, defenderRolls));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderLoses1AndIsEmpty(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(2);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(1);
        int[] attackerRolls = new int[]{5};
        int[] defenderRolls = new int[]{2};


        try {
            assertTrue(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(0, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryDefenderLoses1(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(2);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{5};
        int[] defenderRolls = new int[]{2};


        try {
            assertFalse(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(2);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2};
        int[] defenderRolls = new int[]{5};


        try {
            assertFalse(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(1, attacker.getArmies());
    }

    @Test
    public void testAttackTerritoryDefenderLoses2(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(4);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,5,6};
        int[] defenderRolls = new int[]{3,4};


        try {
            assertFalse(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(2, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1DefenderLoses1(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(4);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,2,6};
        int[] defenderRolls = new int[]{3,4};


        try {
            assertFalse(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, attacker.getArmies());
        assertEquals(3, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1DefenderLoses1OrderMatters(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(4);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{6,2,2};
        int[] defenderRolls = new int[]{3,4};


        try {
            assertFalse(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, attacker.getArmies());
        assertEquals(3, defender.getArmies());
    }
    @Test
    public void testAttackTerritoryDefenderLoses1OrderMatters(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(4);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{6,3};
        int[] defenderRolls = new int[]{4};


        try {
            assertFalse(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(4, attacker.getArmies());
        assertEquals(3, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1OrderMatters(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(4);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{5};
        int[] defenderRolls = new int[]{6,3};


        try {
            assertFalse(attacker.attackTerritory(defender, attackerRolls, defenderRolls));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, attacker.getArmies());
        assertEquals(4, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryTooManyRollValues(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(4);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,2,6,8};
        int[] defenderRolls = new int[]{3,4};

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(defender, attackerRolls, defenderRolls));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderTooManyRollValues(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(4);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,2};
        int[] defenderRolls = new int[]{3,4,3};

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(defender, attackerRolls, defenderRolls));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryOneArmyShort(){
        Territory attacker = new Territory("Test", "Test");
        attacker.addArmies(1);
        Territory defender = new Territory("Test", "Test");
        defender.addArmies(2);
        int[] attackerRolls = new int[]{2};
        int[] defenderRolls = new int[]{3};

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(defender, attackerRolls, defenderRolls));

        assertEquals("Attacker does not possess enough troops for that many rolls", thrown.getMessage());
    }

    @Test
    public void testFortifyTerritoryMove5Units(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(red);
        moveFrom.addArmies(10);
        moveTo.addArmies(5);

        assertTrue(moveFrom.fortifyTerritory(moveTo, 5));
        assertEquals(5, moveFrom.getArmies());
        assertEquals(10, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryMove20Units(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(red);
        moveFrom.addArmies(30);
        moveTo.addArmies(5);

        assertTrue(moveFrom.fortifyTerritory(moveTo, 20));
        assertEquals(10, moveFrom.getArmies());
        assertEquals(25, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryCallerDoesntHaveEnoughUnits(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 5));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryInvalidNumberOfUnits(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, -1));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryInvalidNumberOfUnits2(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, -1));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryMoveAllUnitsFromTerritory(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(red);
        moveFrom.addArmies(10);
        moveTo.addArmies(1);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 10));
        assertEquals(10, moveFrom.getArmies());
        assertEquals(1, moveTo.getArmies());
    }

    @Test
    public void testFortifyUnownedTerritory(){
        Player red = new Player(PlayerColor.RED, null, null);
        Player blue = new Player(PlayerColor.BLUE, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(blue);
        moveFrom.addArmies(5);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 1));
        assertEquals(5, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }
    
    @Test
    public void testFortifyTerritoryInvalidNumberOfUnits3(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", "Test");
        moveFrom.setController(red);
        Territory moveTo = new Territory("Test", "Test");
        moveTo.setController(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 0));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testCalculateContinentBonusWithEmptyList(){
        List<Territory> territoryList = new ArrayList<>();
        assertEquals(0, Territory.calculateArmiesFromContinentBonus(territoryList));
    }

    @Test
    public void testCalculateContinentBonusWithAllTerritories(){
        GameSetup gameSetup = new GameSetup(6);
        List<Territory> territoryList = gameSetup.initTerritories();
        assertEquals(24, Territory.calculateArmiesFromContinentBonus(territoryList));
    }

    @Test
    public void testCalculateContinentBonusMissingOneTerritoryFromEachContinent(){
        GameSetup gameSetup = new GameSetup(6);
        List<Territory> territoryList = gameSetup.initTerritories();
        territoryList.remove(41);
        territoryList.remove(34);
        territoryList.remove(22);
        territoryList.remove(18);
        territoryList.remove(12);
        territoryList.remove(8);

        assertEquals(0, Territory.calculateArmiesFromContinentBonus(territoryList));
    }

    @Test
    public void testCalculateContinentBonusWithAllNorthAmericanTerritories(){
        GameSetup gameSetup = new GameSetup(6);
        List<Territory> allTerritories = gameSetup.initTerritories();
        List<Territory> territoryList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            territoryList.add(allTerritories.get(i));
        }

        assertEquals(5, Territory.calculateArmiesFromContinentBonus(territoryList));
    }

    @Test
    public void testCalculateContinentBonusWithAllNorthAndSouthAmericanTerritories(){
        GameSetup gameSetup = new GameSetup(6);
        List<Territory> allTerritories = gameSetup.initTerritories();
        List<Territory> territoryList = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            territoryList.add(allTerritories.get(i));
        }

        assertEquals(7, Territory.calculateArmiesFromContinentBonus(territoryList));
    }

    @Test
    public void testIsOccupiedNotOccupied(){
        Territory territory = new Territory("Test", "Test");
        assertFalse(territory.isOccupied());
    }

    @Test
    public void testIsOccupiedIsOccupied(){
        Player red = new Player(PlayerColor.RED, null, null);
        Territory territory = new Territory("Test", "Test");
        territory.setController(red);
        assertTrue(territory.isOccupied());
        assertEquals(red, territory.getController());
    }

    @Test
    public void testGetters(){
        Territory territory = new Territory("Name", "Continent");
        assertEquals("Name", territory.getName());
        assertEquals("Continent", territory.getContinent());
    }
}