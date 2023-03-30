package controller;

import model.Player;
import model.PlayerColor;

import java.util.ArrayList;

public class PlayerController {
    protected ArrayList<Player> playerArray;
    protected int numberOfPlayers;
    protected int currentPlayer;

    public PlayerController(int numberOfPlayers, ArrayList<Player> playerArray){
        this.numberOfPlayers = numberOfPlayers;
        this.playerArray = playerArray;
    }

    public PlayerColor getPlayerColor(){
        return getCurrentPlayer().getColor();
    }

    public int getNumberOfPlayers(){
        return this.numberOfPlayers;
    }

    public void setCurrentPlayer(int player){
        this.currentPlayer = player;
    }

    public Player getCurrentPlayer(){
        return playerArray.get(currentPlayer);
    }

    public void removeDefeatedPlayer(int playerNum){
        Player currentPlayerObject = playerArray.get(currentPlayer);
        if (playerNum < 0 || playerNum > numberOfPlayers - 1) {
            throw new IllegalArgumentException("player number must be between "
                    + "0 and the number of players minus one");
        }
        playerArray.remove(playerNum);
        numberOfPlayers--;
        currentPlayer = playerArray.indexOf(currentPlayerObject);
    }

    public void nextPlayer(){
        if(++currentPlayer == playerArray.size()){
            currentPlayer = 0;
        }
    }

    public int getCurrentPlayerArmiesAvailable(){
        return playerArray.get(currentPlayer).getArmiesAvailable();
    }

    public int getIndexOfPlayer(Player defender){
        return playerArray.indexOf(defender);
    }


}
