package model.Map;

import java.util.HashMap;
import java.util.List;


public class Continent {


    public final String name;
    public final int territories;
    public final int bonus;


    public Continent(String name, int territories, int bonus) {
        this.name        = name;
        this.territories = territories;
        this.bonus       = bonus;
    }


    public static int calculateContinentBonus(List<Territory> controlledTerritories, List<Continent> continents) {
        HashMap<Continent, Integer> byContinent;
        byContinent = Continent.getTerritoryCountByContinent(controlledTerritories);
        int bonus = 0;

        for (Continent continent : continents) {
            if (!byContinent.containsKey(continent)) continue;
            if (byContinent.get(continent) != continent.territories) continue;
            bonus += continent.bonus;
        }

        return bonus;
    }


    private static HashMap<Continent, Integer> getTerritoryCountByContinent(List<Territory> territories) {
        HashMap<Continent, Integer> byContinent = new HashMap<>();
        for (Territory territory : territories) {
            Continent continent = territory.getContinent();
            if (!byContinent.containsKey(continent))
                byContinent.put(continent, 0);
            byContinent.put(continent, byContinent.get(continent) + 1);
        }
        return byContinent;
    }


}
