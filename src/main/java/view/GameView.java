package view;

import controller.Game;
import model.Map.World;
import model.Map.Territory;
import view.components.CardButton;
import view.components.PhaseButton;
import view.components.PlayerFlag;
import view.components.TerritoryButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class GameView {


    private JPanel boardContainer;
    private JLabel board;

    private final ResourceBundle bundle;
    private final JFrame frame;
    private final ArrayList<TerritoryButton> territories;
    private JLabel labelDetails;
    private JLabel labelPlayer;
    private PhaseButton actionEndPhase;
    private CardButton actionCardView;


    public GameView(Game game, World world) {
        this.territories = new ArrayList<>();
        this.bundle = game.getBundle();
        frame = new JFrame(bundle.getString("Risk"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        Container pane = frame.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(buildRibbon(game));
        pane.add(buildBoard(game, world));

        frame.setVisible(true);
    }


    private JPanel buildRibbon(Game game) {
        JPanel ribbon = new JPanel();
        ribbon.setBackground(new Color(20, 20, 20));
        ribbon.setLayout(new BoxLayout(ribbon, BoxLayout.X_AXIS));
        ribbon.setBorder(new EmptyBorder(0, 20, 0, 20));

        this.actionEndPhase = new PhaseButton(bundle.getString("endPhaseMessage"), game);
        this.actionCardView = new CardButton(bundle.getString("cardButton"), game);

        this.labelPlayer = new JLabel("Mr. Green");
        this.labelPlayer.setFont(new Font("SansSerif", Font.BOLD, 24));
        this.labelPlayer.setForeground(Color.WHITE);
        this.labelDetails = new JLabel("Selection Phase");
        this.labelDetails.setFont(new Font("SansSerif", Font.PLAIN, 16));
        this.labelDetails.setForeground(Color.GRAY);
        JPanel details = new JPanel();
        details.setBackground(new Color(20, 20, 20));
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(this.labelPlayer);
        details.add(this.labelDetails);

        ribbon.add(new PlayerFlag(game));
        ribbon.add(Box.createHorizontalStrut(10));
        ribbon.add(details);
        ribbon.add(Box.createHorizontalGlue());
        ribbon.add(actionCardView);
        ribbon.add(Box.createHorizontalStrut(10));
        ribbon.add(actionEndPhase);

        return ribbon;
    }


    private Container buildBoard(Game game, World world) {
        this.boardContainer = new JPanel();
        this.boardContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.boardContainer.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.boardContainer.setBackground(new Color(30, 30, 30));
        this.setBoardBackground(world.getBackground());
        int width = world.getWidth();
        int height = world.getHeight();
        this.addButtons(world.getTerritories(), width, height, game);
        boardContainer.add(board);
        return this.boardContainer;
    }


    private void setBoardBackground(BufferedImage background) {
        try {
            ImageIcon icon = new ImageIcon(background);
            board = new JLabel(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setDetails(String player, int armies, String phase) {
        this.labelPlayer.setText(player);
        this.labelDetails.setText(String.format("%d %s  |  %s", armies,
                bundle.getString("armies"), bundle.getString(phase)));
        this.labelDetails.paintImmediately(this.labelDetails.getVisibleRect());
        this.labelPlayer.paintImmediately(this.labelPlayer.getVisibleRect());
        this.frame.repaint();
    }


    private void addButtons(List<Territory> territories, int width, int height, Game game) {
        for (Territory territory : territories) {
            TerritoryButton button = new TerritoryButton(territory, game);
            int buttonWidth  = (int) (width * territory.getPosX());
            int buttonHeight = (int) (height * territory.getPosY());
            button.setBounds(buttonWidth, buttonHeight,  100, 35);
            board.add(button);
            this.territories.add(button);
        }
    }


    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }


    public int getNumber(String message) {
        String res = JOptionPane.showInputDialog(null, message);
        try {
            return Integer.parseInt(res);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public int getNumberOfDice(int armies, String player, boolean attacking) {
        JComboBox<String> comboBox = new JComboBox<>();
        if (attacking) {
            for (int i = 1; i < armies && i < 4; i++) {
                comboBox.addItem(String.valueOf(i));
            }
        } else {
            for (int i = 1; i <= armies && i < 3; i++) {
                comboBox.addItem(String.valueOf(i));
            }
        }

        JOptionPane.showMessageDialog(null, comboBox,
                bundle.getString("numberOfDiceMessage") + " " + player, JOptionPane.INFORMATION_MESSAGE);
        return comboBox.getSelectedIndex() + 1;
    }


    public void displayRolls(int[] attackRolls, int[] defendRolls) {
        String attack = Arrays.toString(attackRolls);
        String defend = Arrays.toString(defendRolls);
        JOptionPane.showMessageDialog(null, bundle.getString("attackerRolledMessage") + " " + attack + "\n"
                + bundle.getString("defenderRolledMessage") + " " + defend);
    }


    public void openCardTradeDisplay() {
        this.actionCardView.tradeCardDialog(true);
    }


    public void updateTerritoryButtons() {
        for (TerritoryButton territoryButton : this.territories) {
            territoryButton.updateDisplay();
        }
    }


}
