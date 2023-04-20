package model;

import model.Map.MapManager;

public class WorldDominationWin extends WinCondition {

    public WorldDominationWin(Player playerToCheck) {
        super(playerToCheck);
    }
    @Override
    public boolean hasWon() {
        return super.player.getOccupiedTerritories().size() == MapManager.getInstance().getTerritories().size();
    }
}
