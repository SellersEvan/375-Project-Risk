package view;

import controller.Game;
import model.Map.MapLoader;
import model.Map.MapLoaderYAML;

import javax.swing.*;
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


        JComboBox<String> mapOptions = new JComboBox<>();
        for (String mapName : MapLoader.getMapFiles().keySet())
            mapOptions.addItem(mapName);
        JOptionPane.showMessageDialog(null, mapOptions,
                "Which map would you like to play?", JOptionPane.INFORMATION_MESSAGE);
        File mapFile = (File) MapLoader.getMapFiles().values().toArray()[mapOptions.getSelectedIndex()];
        MapLoader map = new MapLoaderYAML(mapFile);

        Game gameController = new Game(numberOfPlayers.getSelectedIndex() + 2, map);
        gameController.setLanguageBundle(bundleName);
        gameController.initWindow();
    }
}
