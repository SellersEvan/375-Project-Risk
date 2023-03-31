package model;

import model.Map.Continent;
import model.Map.Territory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TerritoryTest {


    @Test
    public void testInvalidTerritoryIdNotAdjacent(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory mainTerritory = new Territory("Test", continent);
        Territory toCheck = new Territory("Test", continent);

        assertFalse(mainTerritory.isAdjacentTerritory(toCheck));
    }

    @Test
    public void testValidTerritoryIdNotAdjacent(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory mainTerritory = new Territory("Test", continent);
        Territory adjTerritory = new Territory("Test", continent);
        mainTerritory.addAdjacentTerritory(adjTerritory);
        Territory toCheck = new Territory("Test", continent);

        assertFalse(mainTerritory.isAdjacentTerritory(toCheck));
    }

    @Test
    public void testValidTerritoryIdIsAdjacent(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory mainTerritory = new Territory("Test", continent);
        Territory toCheck = new Territory("Test", continent);
        mainTerritory.addAdjacentTerritory(toCheck);

        assertTrue(mainTerritory.isAdjacentTerritory(toCheck));
    }

    @Test
    public void testValidTerritoryIdIsAdjacentLargerList(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory mainTerritory = new Territory("Test", continent);
        Territory toCheck = new Territory("Test", continent);
        mainTerritory.addAdjacentTerritory(toCheck);
        Territory adjTerritory = new Territory("Test", continent);
        mainTerritory.addAdjacentTerritory(adjTerritory);

        assertTrue(mainTerritory.isAdjacentTerritory(toCheck));
    }

    @Test
    public void testInvalidTerritoryIdIsAdjacentLargerList(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory mainTerritory = new Territory("Test", continent);
        Territory adjTerritory1 = new Territory("Test", continent);
        Territory adjTerritory2 = new Territory("Test", continent);
        mainTerritory.addAdjacentTerritory(adjTerritory1);
        mainTerritory.addAdjacentTerritory(adjTerritory2);

        Territory toCheck = new Territory("Test", continent);

        assertFalse(mainTerritory.isAdjacentTerritory(toCheck));
    }

    @Test
    public void testMultipleValidAdjacentTerritories(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory mainTerritory = new Territory("Test", continent);
        Territory adjTerritory = new Territory("Test", continent);
        Territory firstTrue = new Territory("Test", continent);
        Territory secondTrue = new Territory("Test", continent);
        Territory firstFalse = new Territory("Test", continent);
        Territory secondFalse = new Territory("Test", continent);


        mainTerritory.addAdjacentTerritory(firstTrue);
        mainTerritory.addAdjacentTerritory(adjTerritory);
        mainTerritory.addAdjacentTerritory(secondTrue);



        assertTrue(mainTerritory.isAdjacentTerritory(firstTrue));
        assertTrue(mainTerritory.isAdjacentTerritory(secondTrue));
        assertFalse(mainTerritory.isAdjacentTerritory(firstFalse));
        assertFalse(mainTerritory.isAdjacentTerritory(secondFalse));
    }

    @Test
    public void test20ValidAdjacentTerritories(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory mainTerritory = new Territory("Test", continent);
        Territory[] territories = new Territory[20];

        for (int i = 0; i < 20; i++) {
            territories[i] = new Territory("Test", continent);
        }
        for (int i = 0; i < 10; i++) {
            mainTerritory.addAdjacentTerritory(territories[i]);
        }

        for (int i = 0; i < 20; i++) {
            if (i < 10){
                assertTrue(mainTerritory.isAdjacentTerritory(territories[i]));
            } else {
                assertFalse(mainTerritory.isAdjacentTerritory(territories[i]));
            }
        }
    }

    @Test
    public void testAttackTerritoryAttackerHas0Armies(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(2);
        int[] attackerRolls = new int[]{1};
        int[] defenderRolls = new int[]{1};

        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);
        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(data));

        assertEquals("Attacker does not possess enough troops for that many rolls", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderHas0Armies(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(2);
        Territory defender = new Territory("Test", continent);
        int[] attackerRolls = new int[]{1};
        int[] defenderRolls = new int[]{1};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(data));

        assertEquals("Defender does not possess enough troops for that many rolls", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryInvalidRolls(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(1);
        Territory defender = new Territory("Test", continent);
        int[] attackerRolls = new int[]{};
        int[] defenderRolls = new int[]{};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(data));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderInvalidRoll(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(1);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(1);
        int[] attackerRolls = new int[]{1};
        int[] defenderRolls = new int[]{};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(data));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderLoses1AndIsEmpty(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(2);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(1);
        int[] attackerRolls = new int[]{5};
        int[] defenderRolls = new int[]{2};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertTrue(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(0, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryDefenderLoses1(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(2);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{5};
        int[] defenderRolls = new int[]{2};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertFalse(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(2);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2};
        int[] defenderRolls = new int[]{5};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertFalse(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(1, attacker.getArmies());
    }

    @Test
    public void testAttackTerritoryDefenderLoses2(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(4);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,5,6};
        int[] defenderRolls = new int[]{3,4};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertFalse(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(2, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1DefenderLoses1(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(4);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,2,6};
        int[] defenderRolls = new int[]{3,4};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertFalse(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, attacker.getArmies());
        assertEquals(3, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1DefenderLoses1OrderMatters(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(4);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{6,2,2};
        int[] defenderRolls = new int[]{3,4};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertFalse(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, attacker.getArmies());
        assertEquals(3, defender.getArmies());
    }
    @Test
    public void testAttackTerritoryDefenderLoses1OrderMatters(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(4);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{6,3};
        int[] defenderRolls = new int[]{4};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertFalse(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(4, attacker.getArmies());
        assertEquals(3, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryAttackerLoses1OrderMatters(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(4);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{5};
        int[] defenderRolls = new int[]{6,3};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


        try {
            assertFalse(attacker.attackTerritory(data));
        } catch (InvalidAttackException e) {
            e.printStackTrace();
            fail("attackTerritory threw exception");
        }

        assertEquals(3, attacker.getArmies());
        assertEquals(4, defender.getArmies());
    }

    @Test
    public void testAttackTerritoryTooManyRollValues(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(4);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,2,6,8};
        int[] defenderRolls = new int[]{3,4};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(data));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryDefenderTooManyRollValues(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(4);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(4);
        int[] attackerRolls = new int[]{2,2};
        int[] defenderRolls = new int[]{3,4,3};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(data));

        assertEquals("Invalid roll values", thrown.getMessage());
    }

    @Test
    public void testAttackTerritoryOneArmyShort(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory attacker = new Territory("Test", continent);
        attacker.addArmies(1);
        Territory defender = new Territory("Test", continent);
        defender.addArmies(2);
        int[] attackerRolls = new int[]{2};
        int[] defenderRolls = new int[]{3};
        AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

        InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                () -> attacker.attackTerritory(data));

        assertEquals("Attacker does not possess enough troops for that many rolls", thrown.getMessage());
    }

    @Test
    public void testFortifyTerritoryMove5Units(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(10);
        moveTo.addArmies(5);

        assertTrue(moveFrom.fortifyTerritory(moveTo, 5));
        assertEquals(5, moveFrom.getArmies());
        assertEquals(10, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryMove20Units(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(30);
        moveTo.addArmies(5);

        assertTrue(moveFrom.fortifyTerritory(moveTo, 20));
        assertEquals(10, moveFrom.getArmies());
        assertEquals(25, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryCallerDoesntHaveEnoughUnits(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 5));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryInvalidNumberOfUnits(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, -1));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryInvalidNumberOfUnits2(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, -1));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryMoveAllUnitsFromTerritory(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(10);
        moveTo.addArmies(1);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 10));
        assertEquals(10, moveFrom.getArmies());
        assertEquals(1, moveTo.getArmies());
    }

    @Test
    public void testFortifyUnownedTerritory(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Player blue = new Player(PlayerColor.BLUE, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(blue);
        moveFrom.addArmies(5);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 1));
        assertEquals(5, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryInvalidNumberOfUnits3(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", continent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", continent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 0));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testIsOccupiedNotOccupied(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory territory = new Territory("Test", continent);
        assertFalse(territory.hasOccupant());
    }

    @Test
    public void testIsOccupiedIsOccupied(){
        Continent continent = EasyMock.mock(Continent.class);
        Player red = new Player(PlayerColor.RED, null, null);
        Territory territory = new Territory("Test", continent);
        territory.setOccupant(red);
        assertTrue(territory.hasOccupant());
        assertEquals(red, territory.getOccupant());
    }

    @Test
    public void testGetters(){
        Continent continent = EasyMock.mock(Continent.class);
        Territory territory = new Territory("Name", continent);
        assertEquals("Name", territory.getName());
        assertEquals(continent, territory.getContinent());
    }
}