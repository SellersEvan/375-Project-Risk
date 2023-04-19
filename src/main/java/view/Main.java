package view;

import controller.Game;
import model.CardTrader;
import model.Player;
import model.PlayerColor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import model.Map.World;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1");
        JComboBox<String> selectLanguage = new JComboBox<>();
        selectLanguage.addItem("en");
        selectLanguage.addItem("fr");
        JOptionPane.showMessageDialog(null, selectLanguage,
                "Language", JOptionPane.INFORMATION_MESSAGE);
        String bundleName = "messages_" + selectLanguage.getSelectedItem().toString();

        JComboBox<String> numberOfPlayers = new JComboBox<>();
        for (int i = 2; i < 7; i++) {
            numberOfPlayers.addItem(String.valueOf(i));
        }
        JOptionPane.showMessageDialog(null, numberOfPlayers,
                "Number of Players", JOptionPane.INFORMATION_MESSAGE);
        ArrayList<Player> players = fillPlayerArray(numberOfPlayers.getSelectedIndex() + 2);
        JComboBox<String> mapOptions = new JComboBox<>();
        for (String mapName : World.getMapFiles().keySet())
            mapOptions.addItem(mapName);
        JOptionPane.showMessageDialog(null, mapOptions,
                "Which map would you like to play?", JOptionPane.INFORMATION_MESSAGE);
        File mapFile = (File) World.getMapFiles().values().toArray()[mapOptions.getSelectedIndex()];
        World map = new World(mapFile);

        Game gameController = new Game(numberOfPlayers.getSelectedIndex() + 2, map, players);
        gameController.setLanguageBundle(bundleName);
        gameController.initWindow();
    }
    private static ArrayList<Player> fillPlayerArray(int numberOfPlayers) {
        PlayerColor[] playerColors = PlayerColor.values();
        ArrayList<Player> playerArray = new ArrayList<>();
        Random random = new Random();
        CardTrader cardTrader = new CardTrader();
        List<PlayerColor> availableColors = new ArrayList<PlayerColor>();
        availableColors.addAll(Arrays.asList(playerColors));
        for (int i = 0; i < numberOfPlayers; i++) {
            String playerName = getString();
            PlayerColor playerColor = getColor(availableColors);
            Player p = new Player(playerColor, playerName, random, cardTrader);
            availableColors.remove(playerColor);
            playerArray.add(p);
        }
        return playerArray;
    }
    private static String getString() {
        JTextField nameInput = new JTextField();
        nameInput.setText("");
        JOptionPane.showMessageDialog(null, nameInput,
                "What is your name, commander?", JOptionPane.INFORMATION_MESSAGE);
        return nameInput.getText();
    }
    private static PlayerColor getColor(List<PlayerColor> opt) {
        JComboBox<PlayerColor> playerColors = new JComboBox<>();
        for (int count = 0; count < opt.size(); count++) {
            playerColors.addItem(opt.get(count));
        }
        playerColors.setSelectedIndex(0);
        JOptionPane.showMessageDialog(null, playerColors,
                "Select your color.", JOptionPane.INFORMATION_MESSAGE);
        return playerColors.getItemAt(playerColors.getSelectedIndex());
    }

}
