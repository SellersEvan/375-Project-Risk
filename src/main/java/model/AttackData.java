package model;

import model.Map.Territory;

public class AttackData {
    private Territory defender;
    private Territory attacker;
    private int[] defenderDice;
    private int[] attackerDice;
    public AttackData(Territory attacker, Territory defender,int attackerDice, int defenderDice){
        this.attacker = attacker;
        this.defender = defender;
        this.attackerDice = attacker.getOccupant().rollDice(attackerDice);
        this.defenderDice = defender.getOccupant().rollDice(defenderDice);
    }
    public AttackData(Territory attacker, Territory defender,int[] attackerDice, int[] defenderDice){
        this.attacker = attacker;
        this.defender = defender;
        this.attackerDice = attackerDice;
        this.defenderDice = defenderDice;
    }
    public Territory getAttacker() {
        return attacker;
    }
    public Territory getDefender() {
        return defender;
    }
    public int[] getAttackerDice() {
        return attackerDice;
    }
    public int[] getDefenderDice() {
        return defenderDice;
    }
}
