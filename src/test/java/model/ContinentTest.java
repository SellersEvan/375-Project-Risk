package model;

import model.Map.Continent;
import model.Map.Territory;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContinentTest {


    @Test
    public void testCalculateContinentBonusWithEmptyList(){
        List<Continent> continents  = new ArrayList<Continent>(){{
           add(new Continent("Test 1", 5, 2));
        }};
        List<Territory> territories = new ArrayList<>();
        assertEquals(0, Continent.calculateContinentBonus(territories, continents));
    }


    @Test
    public void testCalculateContinentBonusWithAllTerritories(){
        List<Continent> continents  = new ArrayList<Continent>(){{
            add(new Continent("Test 1", 5, 2));
            add(new Continent("Test 1", 3, 5));
        }};
        List<Territory> territories = new ArrayList<Territory>(){{
            add(new Territory("Test 1", continents.get(0)));
            add(new Territory("Test 2", continents.get(0)));
            add(new Territory("Test 3", continents.get(0)));
            add(new Territory("Test 4", continents.get(0)));
            add(new Territory("Test 5", continents.get(0)));
            add(new Territory("Test 6", continents.get(1)));
            add(new Territory("Test 7", continents.get(1)));
            add(new Territory("Test 8", continents.get(1)));
        }};
        assertEquals(7, Continent.calculateContinentBonus(territories, continents));
    }


    @Test
    public void testCalculateContinentBonusMissingOneTerritoryFromEachContinent(){
        List<Continent> continents  = new ArrayList<Continent>(){{
            add(new Continent("Test 1", 6, 2));
            add(new Continent("Test 1", 3, 5));
        }};
        List<Territory> territories = new ArrayList<Territory>(){{
            add(new Territory("Test 1", continents.get(0)));
            add(new Territory("Test 2", continents.get(0)));
            add(new Territory("Test 3", continents.get(0)));
            add(new Territory("Test 4", continents.get(0)));
            add(new Territory("Test 5", continents.get(0)));
            add(new Territory("Test 6", continents.get(1)));
            add(new Territory("Test 7", continents.get(1)));
        }};
        assertEquals(0, Continent.calculateContinentBonus(territories, continents));
    }


    @Test
    public void testCalculateContinentBonusWithSingleContinent(){
        List<Continent> continents  = new ArrayList<Continent>(){{
            add(new Continent("Test 1", 5, 2));
            add(new Continent("Test 2", 10, 5));
        }};
        List<Territory> territories = new ArrayList<Territory>(){{
            add(new Territory("Test 1", continents.get(0)));
            add(new Territory("Test 2", continents.get(0)));
            add(new Territory("Test 3", continents.get(0)));
            add(new Territory("Test 4", continents.get(0)));
            add(new Territory("Test 5", continents.get(0)));
            add(new Territory("Test 6", continents.get(1)));
            add(new Territory("Test 7", continents.get(1)));
            add(new Territory("Test 8", continents.get(1)));
        }};
        assertEquals(2, Continent.calculateContinentBonus(territories, continents));
    }


}
