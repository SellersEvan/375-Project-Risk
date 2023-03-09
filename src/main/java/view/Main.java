package view;

import controller.Game;

import javax.swing.*;

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

        Game gameController = new Game(numberOfPlayers.getSelectedIndex() + 2);
        gameController.setLanguageBundle(bundleName);
        gameController.initWindow();
    }
}
