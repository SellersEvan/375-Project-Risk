package model;

public abstract class WinCondition {
    Player player;

    public WinCondition(Player playerToCheck) {
        this.player = playerToCheck;
    }

    abstract boolean hasWon();
}
