package controller;

import model.Map.Territory;

import java.util.List;

public class TerritoryController {
    protected List<Territory> territories;
    private Territory selectedTerritory;

    public TerritoryController(List<Territory> territories) {
        this.territories = territories;
        selectedTerritory = null;
    }

    public List<Territory> getTerritories() {
        return this.territories;
    }

    public Territory getSelectedTerritory() {
        return selectedTerritory;
    }

    public void setSelectedTerritory(Territory selectedTerritory) {
        this.selectedTerritory = selectedTerritory;
    }

    public boolean checkIfAllClaimed() {
        for (Territory territory : territories) {
            if (!territory.hasOccupant())
                return true;
        }
        return false;
    }
}
