package model;

import model.Map.Continent;
import model.Map.Territory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TerritoryTest {
    private Continent mockContinent;

    @BeforeEach
    public void setupVariables(){
        mockContinent = EasyMock.mock(Continent.class);
    }

    @Nested
    class AdjacencyTestNet {

        @Test
        public void testInvalidTerritoryIdNotAdjacent() {
            Territory mainTerritory = new Territory("Test", mockContinent);
            Territory toCheck = new Territory("Test", mockContinent);

            assertFalse(mainTerritory.isAdjacentTerritory(toCheck));
        }

        @Test
        public void testValidTerritoryIdNotAdjacent() {
            Territory mainTerritory = new Territory("Test", mockContinent);
            Territory adjTerritory = new Territory("Test", mockContinent);
            mainTerritory.addAdjacentTerritory(adjTerritory);
            Territory toCheck = new Territory("Test", mockContinent);

            assertFalse(mainTerritory.isAdjacentTerritory(toCheck));
        }

        @Test
        public void testValidTerritoryIdIsAdjacent() {
            Territory mainTerritory = new Territory("Test", mockContinent);
            Territory toCheck = new Territory("Test", mockContinent);
            mainTerritory.addAdjacentTerritory(toCheck);

            assertTrue(mainTerritory.isAdjacentTerritory(toCheck));
        }

        @Test
        public void testValidTerritoryIdIsAdjacentLargerList() {
            Territory mainTerritory = new Territory("Test", mockContinent);
            Territory toCheck = new Territory("Test", mockContinent);
            mainTerritory.addAdjacentTerritory(toCheck);
            Territory adjTerritory = new Territory("Test", mockContinent);
            mainTerritory.addAdjacentTerritory(adjTerritory);

            assertTrue(mainTerritory.isAdjacentTerritory(toCheck));
        }

        @Test
        public void testInvalidTerritoryIdIsAdjacentLargerList() {
            Territory mainTerritory = new Territory("Test", mockContinent);
            Territory adjTerritory1 = new Territory("Test", mockContinent);
            Territory adjTerritory2 = new Territory("Test", mockContinent);
            mainTerritory.addAdjacentTerritory(adjTerritory1);
            mainTerritory.addAdjacentTerritory(adjTerritory2);

            Territory toCheck = new Territory("Test", mockContinent);

            assertFalse(mainTerritory.isAdjacentTerritory(toCheck));
        }

        @Test
        public void testMultipleValidAdjacentTerritories() {
            Territory mainTerritory = new Territory("Test", mockContinent);
            Territory adjTerritory = new Territory("Test", mockContinent);
            Territory firstTrue = new Territory("Test", mockContinent);
            Territory secondTrue = new Territory("Test", mockContinent);
            Territory firstFalse = new Territory("Test", mockContinent);
            Territory secondFalse = new Territory("Test", mockContinent);


            mainTerritory.addAdjacentTerritory(firstTrue);
            mainTerritory.addAdjacentTerritory(adjTerritory);
            mainTerritory.addAdjacentTerritory(secondTrue);


            assertTrue(mainTerritory.isAdjacentTerritory(firstTrue));
            assertTrue(mainTerritory.isAdjacentTerritory(secondTrue));
            assertFalse(mainTerritory.isAdjacentTerritory(firstFalse));
            assertFalse(mainTerritory.isAdjacentTerritory(secondFalse));
        }

        @Test
        public void test20ValidAdjacentTerritories() {
            Territory mainTerritory = new Territory("Test", mockContinent);
            Territory[] territories = new Territory[20];

            for (int i = 0; i < 20; i++) {
                territories[i] = new Territory("Test", mockContinent);
            }
            for (int i = 0; i < 10; i++) {
                mainTerritory.addAdjacentTerritory(territories[i]);
            }

            for (int i = 0; i < 20; i++) {
                if (i < 10) {
                    assertTrue(mainTerritory.isAdjacentTerritory(territories[i]));
                } else {
                    assertFalse(mainTerritory.isAdjacentTerritory(territories[i]));
                }
            }
        }
    }

    @Nested
    class attackTestNet {
        private Territory attacker;
        private Territory defender;

        @BeforeEach
        public void setupAttackVariables(){
            attacker = new Territory("Test", mockContinent);
            defender = new Territory("Test", mockContinent);
        }

        @Test
        public void testAttackTerritoryAttackerHas0Armies() {
            defender.addArmies(2);
            int[] attackerRolls = new int[]{1};
            int[] defenderRolls = new int[]{1};

            AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);
            InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                    () -> attacker.attackTerritory(data));

            assertEquals("Attacker does not possess enough troops for that many rolls", thrown.getMessage());
        }

        @Test
        public void testAttackTerritoryDefenderHas0Armies() {
            attacker.addArmies(2);
            int[] attackerRolls = new int[]{1};
            int[] defenderRolls = new int[]{1};
            AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


            InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                    () -> attacker.attackTerritory(data));

            assertEquals("Defender does not possess enough troops for that many rolls", thrown.getMessage());
        }

        @Test
        public void testAttackTerritoryInvalidRolls() {
            attacker.addArmies(1);
            int[] attackerRolls = new int[]{};
            int[] defenderRolls = new int[]{};
            AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);


            InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                    () -> attacker.attackTerritory(data));

            assertEquals("Invalid roll values", thrown.getMessage());
        }

        @Test
        public void testAttackTerritoryDefenderInvalidRoll() {
            attacker.addArmies(1);
            defender.addArmies(1);
            int[] attackerRolls = new int[]{1};
            int[] defenderRolls = new int[]{};
            AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

            InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                    () -> attacker.attackTerritory(data));

            assertEquals("Invalid roll values", thrown.getMessage());
        }

        @Test
        public void testAttackTerritoryDefenderLoses1AndIsEmpty() {
            attacker.addArmies(2);
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
        public void testAttackTerritoryDefenderLoses1() {
            attacker.addArmies(2);
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
        public void testAttackTerritoryAttackerLoses1() {
            attacker.addArmies(2);
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
        public void testAttackTerritoryDefenderLoses2() {
            attacker.addArmies(4);
            defender.addArmies(4);
            int[] attackerRolls = new int[]{2, 5, 6};
            int[] defenderRolls = new int[]{3, 4};
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
        public void testAttackTerritoryAttackerLoses1DefenderLoses1() {
            attacker.addArmies(4);
            defender.addArmies(4);
            int[] attackerRolls = new int[]{2, 2, 6};
            int[] defenderRolls = new int[]{3, 4};
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
        public void testAttackTerritoryAttackerLoses1DefenderLoses1OrderMatters() {
            attacker.addArmies(4);
            defender.addArmies(4);
            int[] attackerRolls = new int[]{6, 2, 2};
            int[] defenderRolls = new int[]{3, 4};
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
        public void testAttackTerritoryDefenderLoses1OrderMatters() {
            attacker.addArmies(4);
            defender.addArmies(4);
            int[] attackerRolls = new int[]{6, 3};
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
        public void testAttackTerritoryAttackerLoses1OrderMatters() {
            attacker.addArmies(4);
            defender.addArmies(4);
            int[] attackerRolls = new int[]{5};
            int[] defenderRolls = new int[]{6, 3};
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
        public void testAttackTerritoryTooManyRollValues() {
            attacker.addArmies(4);
            defender.addArmies(4);
            int[] attackerRolls = new int[]{2, 2, 6, 8};
            int[] defenderRolls = new int[]{3, 4};
            AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

            InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                    () -> attacker.attackTerritory(data));

            assertEquals("Invalid roll values", thrown.getMessage());
        }

        @Test
        public void testAttackTerritoryDefenderTooManyRollValues() {
            attacker.addArmies(4);
            defender.addArmies(4);
            int[] attackerRolls = new int[]{2, 2};
            int[] defenderRolls = new int[]{3, 4, 3};
            AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

            InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                    () -> attacker.attackTerritory(data));

            assertEquals("Invalid roll values", thrown.getMessage());
        }

        @Test
        public void testAttackTerritoryOneArmyShort() {
            attacker.addArmies(1);
            defender.addArmies(2);
            int[] attackerRolls = new int[]{2};
            int[] defenderRolls = new int[]{3};
            AttackData data = new AttackData(attacker, defender, attackerRolls, defenderRolls);

            InvalidAttackException thrown = assertThrows(InvalidAttackException.class,
                    () -> attacker.attackTerritory(data));

            assertEquals("Attacker does not possess enough troops for that many rolls", thrown.getMessage());
        }
    }

    @Nested
    class FortifyTestNet {
        Player red;
        Territory moveFrom;
        Territory moveTo;

        @BeforeEach
        public void setupFortifyVariables(){
            red = new Player(PlayerColor.RED, null, null);
            moveFrom = new Territory("Test", mockContinent);
            moveFrom.setOccupant(red);
            moveTo = new Territory("Test", mockContinent);
            moveTo.setOccupant(red);
        }

        @Test
        public void testFortifyTerritoryMove5Units() {
            moveFrom.addArmies(10);
            moveTo.addArmies(5);

            assertTrue(moveFrom.fortifyTerritory(moveTo, 5));
            assertEquals(5, moveFrom.getArmies());
            assertEquals(10, moveTo.getArmies());
        }

        @Test
        public void testFortifyTerritoryMove20Units() {
            Player red = new Player(PlayerColor.RED, null, null);
            Territory moveFrom = new Territory("Test", mockContinent);
            moveFrom.setOccupant(red);
            Territory moveTo = new Territory("Test", mockContinent);
            moveTo.setOccupant(red);
            moveFrom.addArmies(30);
            moveTo.addArmies(5);

            assertTrue(moveFrom.fortifyTerritory(moveTo, 20));
            assertEquals(10, moveFrom.getArmies());
            assertEquals(25, moveTo.getArmies());
        }

        @Test
        public void testFortifyTerritoryCallerDoesntHaveEnoughUnits() {
            moveFrom.addArmies(1);
            moveTo.addArmies(5);

            assertFalse(moveFrom.fortifyTerritory(moveTo, 5));
            assertEquals(1, moveFrom.getArmies());
            assertEquals(5, moveTo.getArmies());
        }

        @Test
        public void testFortifyTerritoryInvalidNumberOfUnits() {
            moveFrom.addArmies(1);
            moveTo.addArmies(5);

            assertFalse(moveFrom.fortifyTerritory(moveTo, -1));
            assertEquals(1, moveFrom.getArmies());
            assertEquals(5, moveTo.getArmies());
        }

        @Test
        public void testFortifyTerritoryInvalidNumberOfUnits2() {
            moveFrom.addArmies(1);
            moveTo.addArmies(5);

            assertFalse(moveFrom.fortifyTerritory(moveTo, -1));
            assertEquals(1, moveFrom.getArmies());
            assertEquals(5, moveTo.getArmies());
        }

        @Test
        public void testFortifyTerritoryMoveAllUnitsFromTerritory() {
            moveFrom.addArmies(10);
            moveTo.addArmies(1);

            assertFalse(moveFrom.fortifyTerritory(moveTo, 10));
            assertEquals(10, moveFrom.getArmies());
            assertEquals(1, moveTo.getArmies());
        }
    }

    @Test
    public void testFortifyUnownedTerritory() {
        Player red = new Player(PlayerColor.RED, null, null);
        Player blue = new Player(PlayerColor.BLUE, null, null);
        Territory moveFrom = new Territory("Test", mockContinent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", mockContinent);
        moveTo.setOccupant(blue);
        moveFrom.addArmies(5);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 1));
        assertEquals(5, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testFortifyTerritoryInvalidNumberOfUnits3() {
        Player red = new Player(PlayerColor.RED, null, null);
        Territory moveFrom = new Territory("Test", mockContinent);
        moveFrom.setOccupant(red);
        Territory moveTo = new Territory("Test", mockContinent);
        moveTo.setOccupant(red);
        moveFrom.addArmies(1);
        moveTo.addArmies(5);

        assertFalse(moveFrom.fortifyTerritory(moveTo, 0));
        assertEquals(1, moveFrom.getArmies());
        assertEquals(5, moveTo.getArmies());
    }

    @Test
    public void testIsOccupiedNotOccupied() {
        Territory territory = new Territory("Test", mockContinent);
        assertFalse(territory.hasOccupant());
    }

    @Test
    public void testIsOccupiedIsOccupied() {
        Player red = new Player(PlayerColor.RED, null, null);
        Territory territory = new Territory("Test", mockContinent);
        territory.setOccupant(red);
        assertTrue(territory.hasOccupant());
        assertEquals(red, territory.getOccupant());
    }


    @Test
    public void testGetters(){
        Territory territory = new Territory("Name", mockContinent);
        assertEquals("Name", territory.getName());
        assertEquals(mockContinent, territory.getContinent());
    }
}