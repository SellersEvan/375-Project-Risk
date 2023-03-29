package model;

import controller.GameSetup;

import java.util.List;

public class MapManager {
    private static final List<Territory> TERRITORIES = GameSetup.initTerritories();

    public static List<Territory> getTerritories() {
        return TERRITORIES;
    }
}
