package model;

public abstract class WinCondition {
    Player player;

    public WinCondition(Player playerToCheck) {
        this.player = playerToCheck;
    }

    public String getObjective() {
        return "Win";
    }

    abstract boolean hasWon();
}
