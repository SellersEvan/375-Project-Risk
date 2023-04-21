package model.Map;

import java.util.Collections;
import java.util.List;

public class MapManager {
    private List<Territory> territories;
    private static final MapManager INSTANCE = new MapManager();

    public static MapManager getInstance() {
        return INSTANCE;
    }

    public List<Territory> getTerritories() {
        return Collections.unmodifiableList(this.territories);
    }

    public void setTerritories(List<Territory> territories) {
        this.territories = territories;
    }

}
