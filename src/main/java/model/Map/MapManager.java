package model.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapManager {
    private static List<Territory> territories = new ArrayList<>();


    public static List<Territory> getTerritories() {
        return Collections.unmodifiableList(territories);
    }


    public static void setTerritories(List<Territory> territories) {
        MapManager.territories = territories;
    }

}
