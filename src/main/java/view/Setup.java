package view;

import controller.PlayerController;
import model.CardTrader;
import model.Map.World;
import model.Player;
import model.PlayerColor;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class Setup {

    private static final String   BUNDLE_PREFIX       = "messages_";
    private static final String[] SUPPORTED_LANGUAGES = new String[]{"en", "fr"};


    public static void setupProperties() {
        System.setProperty("sun.java2d.uiScale", "1");
    }


    public static ResourceBundle selectLanguage() {
        JComboBox<String> promptLanguage = new JComboBox<>();
        for (String language : SUPPORTED_LANGUAGES) {
            promptLanguage.addItem(language);
        }
        JOptionPane.showMessageDialog(null, promptLanguage,
                "Language Select", JOptionPane.INFORMATION_MESSAGE);
        String bundleID   = SUPPORTED_LANGUAGES[promptLanguage.getSelectedIndex()];
        String bundleFile = String.format("%s%s", BUNDLE_PREFIX, bundleID);
        return ResourceBundle.getBundle(bundleFile);
    }


    public static int selectNumberOfPlayers(ResourceBundle bundle) {
        String unitLabel    = bundle.getString("numberOfPlayersUnit");
        String promptLabel  = bundle.getString("numberOfPlayersPrompt");
        int    minPlayers   = PlayerController.MIN_PLAYERS;
        int    maxPlayers   = PlayerController.MAX_PLAYERS;
        JComboBox<String> promptPlayerCnt = new JComboBox<>();
        for (int i = minPlayers; i <= maxPlayers; i++) {
            promptPlayerCnt.addItem(String.format("%d %s", i, unitLabel));
        }
        JOptionPane.showMessageDialog(null, promptPlayerCnt,
                promptLabel, JOptionPane.INFORMATION_MESSAGE);
        return promptPlayerCnt.getSelectedIndex() + minPlayers;
    }


    public static List<Player> setupPlayers(ResourceBundle bundle, int numberOfPlayers) {
        ArrayList<Player> players    = new ArrayList<>();
        CardTrader        cardTrader = new CardTrader();
        Random            seed       = new Random();
        for (int i = 0; i < numberOfPlayers; i++) {
            String      name  = selectPlayerName(bundle);
            PlayerColor color = selectPlayerColor(bundle, getAvailableColors(players));
            players.add(new Player(color, name, seed, cardTrader));
        }
        return players;
    }


    private static String selectPlayerName(ResourceBundle bundle) {
        String promptLabel = bundle.getString("selectPlayerNamePrompt");
        JTextField promptName = new JTextField();
        promptName.setText("");
        JOptionPane.showMessageDialog(null, promptName,
                promptLabel, JOptionPane.INFORMATION_MESSAGE);
        return promptName.getText();
    }


    private static PlayerColor selectPlayerColor(ResourceBundle bundle, List<PlayerColor> colors) {
        String promptLabel = bundle.getString("selectPlayerColorPrompt");
        JComboBox<PlayerColor> promptColor = new JComboBox<>();
        for (PlayerColor color : colors) {
            promptColor.addItem(color);
        }
        promptColor.setSelectedIndex(0);
        JOptionPane.showMessageDialog(null, promptColor,
                promptLabel, JOptionPane.INFORMATION_MESSAGE);
        return promptColor.getItemAt(promptColor.getSelectedIndex());
    }


    private static List<PlayerColor> getAvailableColors(List<Player> players) {
        ArrayList<PlayerColor> colors = new ArrayList<>(Arrays.asList(PlayerColor.values()));
        for (Player player : players) {
            colors.remove(player.getColor());
        }
        return colors;
    }

    public static boolean selectSecretMissionMode(ResourceBundle bundle) {
        String promptLabel = bundle.getString("selectSecretMissionPrompt");
        JComboBox<String> options = new JComboBox<>();
        options.addItem("No");
        options.addItem("Yes");
        JOptionPane.showMessageDialog(null, options,
                promptLabel, JOptionPane.INFORMATION_MESSAGE);
        if (options.getSelectedIndex() == 0) {
            return false;
        }
        return true;
    }

    public static void displaySecretMissions(ResourceBundle bundle, List<Player> players) {
        String promptText = "The secret mission is World Domination. Pass the laptop to player "
                + players.get(0).getName() + " and continue.";
        JLabel label = new JLabel();
        label.setText(promptText);
        JOptionPane.showMessageDialog(null, promptText,
                "Secret Mission", JOptionPane.INFORMATION_MESSAGE);
        for (Player p : players) {
            promptText = "The mission for player " + p.getName() + ": " + p.displayWinConditions()
                    + ". Please pass to next player and continue.";
            label.setText(promptText);
            JOptionPane.showMessageDialog(null, promptText,
                    "Secret Mission", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public static World selectWorld(ResourceBundle bundle) {
        String promptLabel = bundle.getString("selectWorld");
        JComboBox<String> worldPrompt = new JComboBox<>();
        for (String worldName : World.getMapFiles().keySet())
            worldPrompt.addItem(worldName);
        JOptionPane.showMessageDialog(null, worldPrompt,
                promptLabel, JOptionPane.INFORMATION_MESSAGE);
        return new World((File) World.getMapFiles().values().toArray()[worldPrompt.getSelectedIndex()]);
    }

}
