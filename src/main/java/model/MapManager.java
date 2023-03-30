package model;

import controller.GameSetup;

import java.util.Collections;
import java.util.List;

public class MapManager {
    private final List<Territory> territories = GameSetup.initTerritories();
    private static final MapManager INSTANCE = new MapManager();

    public static MapManager getInstance() {
        return INSTANCE;
    }

    public List<Territory> getTerritories() {
        return Collections.unmodifiableList(this.territories);
    }
}
