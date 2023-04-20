package model.Map;

import java.util.*;


public class Continent {


    public final String name;
    public final Set<Territory> territories = new HashSet<>();
    public final int bonus;


    public Continent(String name, int bonus) {
        this.name        = name;
        this.bonus       = bonus;
    }

    public void addTerritory(Territory territory) {
        this.territories.add(territory);
    }

    public Set<Territory> getComposingTerritories() {
        return Collections.unmodifiableSet(this.territories);
    }

    public static int calculateContinentBonus(List<Territory> controlledTerritories, List<Continent> continents) {
        HashMap<Continent, Integer> byContinent;
        byContinent = Continent.getTerritoryCountByContinent(controlledTerritories);
        int bonus = 0;

        for (Continent continent : continents) {
            if (!byContinent.containsKey(continent)) continue;
            if (byContinent.get(continent) != continent.territories.size()) continue;
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
