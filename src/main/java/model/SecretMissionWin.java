package model;

import model.Map.Continent;
import model.Map.Territory;

import java.util.*;

public class SecretMissionWin extends WinCondition {

    private final Set<Continent> secretTargets;
    public SecretMissionWin(Player playerToCheck, List<Continent> mapContinents, Random random) {
        super(playerToCheck);
        this.secretTargets = new HashSet<>();
        this.generateSecretMission(mapContinents, random);
    }

    @Override
    boolean hasWon() {
        for (Continent continent : this.secretTargets) {
            for (Territory territory : continent.getComposingTerritories()) {
                if (!this.player.getOccupiedTerritories().contains(territory)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void generateSecretMission(List<Continent> targetContinents, Random random) {
        while (this.secretTargets.size() < 2) {
            this.secretTargets.add(targetContinents.get(random.nextInt(targetContinents.size())));
        }
    }

    public Set<Continent> getSecretTargets() {
        return Collections.unmodifiableSet(this.secretTargets);
    }
}