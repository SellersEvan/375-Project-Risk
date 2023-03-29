package model.Map;

import controller.GameSetup;
import model.Map.Territory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapManager {
    private static List<Territory> TERRITORIES = new ArrayList<>();


    public static List<Territory> getTerritories() {
        return Collections.unmodifiableList(TERRITORIES);
    }


    public static void setTerritories(List<Territory> territories) {
        TERRITORIES = territories;
    }

}
