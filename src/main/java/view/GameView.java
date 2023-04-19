package view;

import controller.Game;

import model.Map.World;
import model.Map.Territory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class GameView {
    private JFrame frame;
    private JPanel boardContainer;
    private JLabel board;
    private JLabel phase;
    private JLabel player;
    private Container pane;
    private PhaseButton phaseButton;
    private CardButton cardButton;
    private ResourceBundle messages;

    public GameView(Game game, World world) {
        this.messages = game.getBundle();
        frame = new JFrame(messages.getString("Risk"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        pane = frame.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        boardContainer = new JPanel();
        boardContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.createBoard(world.getBackground());
        int width = world.getWidth();
        int height = world.getHeight();
        this.addButtons(world.getTerritories(), width, height, game);
        boardContainer.add(board);

        phase = new JLabel(" ");
        phase.setFont(new Font("Serif", Font.PLAIN, 30));
        phase.setAlignmentX(Component.CENTER_ALIGNMENT);
        player = new JLabel(" ");
        player.setFont(new Font("Serif", Font.PLAIN, 30));
        player.setAlignmentX(Component.CENTER_ALIGNMENT);
        phaseButton = new PhaseButton(messages.getString("endPhaseMessage"), game);
        phaseButton.setFont(new Font("Serif", Font.PLAIN, 30));
        phaseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardButton = new CardButton(messages.getString("cardButton"), game);
        cardButton.setFont(new Font("Serif", Font.PLAIN, 30));
        cardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel middle = new JPanel();
        middle.add(boardContainer);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
        buttons.add(cardButton);
        buttons.add(Box.createRigidArea(new Dimension(0, 10)));
        buttons.add(phaseButton);

        middle.add(buttons);

        pane.add(phase);
        pane.add(player);
        pane.add(middle);

        frame.setVisible(true);
    }

    public void setPhase(String text) {
        phase.setText(messages.getString("currentPhaseMessage") + " " + messages.getString(text));
        phase.paintImmediately(phase.getVisibleRect());
    }

    public void setPlayer(String player, int armies) {

        this.player.setText(messages.getString("currentPlayerMessage") + " " + player
                + "    " + messages.getString("currentArmiesMessage") + " " + armies);
        frame.repaint();
    }


    private void createBoard(BufferedImage background) {
        try {
            ImageIcon icon = new ImageIcon(background);
            board = new JLabel(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addButtons(List<Territory> territories, int width, int height, Game game) {
        for (Territory territory : territories) {
            JButton button = new TerritoryButton(messages.getString("armies") + " 0", territory, game);
            int buttonWidth  = (int) (width * territory.getPosX());
            int buttonHeight = (int) (height * territory.getPosY());
            button.setBounds(buttonWidth, buttonHeight,  100, 20);
            board.add(button);
        }
        frame.setVisible(true);
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
                messages.getString("numberOfDiceMessage") + " " + player, JOptionPane.INFORMATION_MESSAGE);
        return comboBox.getSelectedIndex() + 1;
    }

    public void displayRolls(int[] attackRolls, int[] defendRolls) {
        String attack = Arrays.toString(attackRolls);
        String defend = Arrays.toString(defendRolls);
        JOptionPane.showMessageDialog(null, messages.getString("attackerRolledMessage") + " " + attack + "\n"
                + messages.getString("defenderRolledMessage") + " " + defend);
    }

    public void openCardTradeDisplay() {
        cardButton.tradeCardDialog(true);
    }
}
