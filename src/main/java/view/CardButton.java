package view;

import controller.Game;
import controller.Phase;
import model.Card;
import model.CardTrader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;

public class CardButton extends JButton {
    private Game gameController;
    private CardTrader cardTrader;
    private ResourceBundle messages;

    public CardButton(String text, Game game) {
        super(text);
        this.gameController = game;
        this.cardTrader = new CardTrader(new Random(), null);
        messages = game.getLanguageBundle();
        setActionListener();
    }

    private void setActionListener() {
        this.addActionListener(e -> {
            if (gameController.getCurrentPhase() == Phase.tradeCards) {
                tradeCardDialog(true);
            } else {
                tradeCardDialog(false);
            }
        });
    }

    void tradeCardDialog(boolean trading) {
        JDialog dialog = new JDialog();
        dialog.setTitle(messages.getString("cards"));
        dialog.setFont(new Font("Serif", Font.PLAIN, 30));
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));
        JPanel cards = new JPanel();
        cards.setLayout(new FlowLayout());


        System.out.println(gameController.getCurrentPlayer().getCards().size());
        Set<Card> playerCards = gameController.getCurrentPlayer().getCards();
        ArrayList<CardPanel> cardPanels = new ArrayList<>();

        for (Card card: playerCards) {
            CardPanel cardPanel = new CardPanel(card);
            cards.add(cardPanel);
            cardPanels.add(cardPanel);
        }
        dialog.add(cards);

        if (trading) {
            JButton turnIn = new JButton(messages.getString("turnIn"));
            turnIn.addActionListener(event -> {
                Set<Card> selectedCards = new HashSet<>();
                for (CardPanel cardPanel: cardPanels) {
                    if (cardPanel.checkBox.isSelected()) {
                        selectedCards.add(cardPanel.card);
                    }
                }
                dialog.dispose();
                if (gameController.getCurrentPlayer().tradeInCards(selectedCards)) {
                    gameController.updateGameView();
                    JOptionPane.showMessageDialog(null, messages.getString("tradeCardGainMessage") + " "
                            + gameController.getCurrentPlayer().getArmiesAvailable()
                            + " " + messages.getString("tradeCardArmiesMessage"));
                } else {
                    JOptionPane.showMessageDialog(null, messages.getString("invalid"));
                }
            });
            turnIn.setAlignmentX(Component.CENTER_ALIGNMENT);
            dialog.add(turnIn);
        }

        dialog.pack();
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private class CardPanel extends JPanel {
        JCheckBox checkBox;
        Card card;

        CardPanel(Card card) {
            this.card = card;
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            JLabel territory = new JLabel(messages.getString("territory"));
            territory.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel cardTerritory = new JLabel(card.getPicturedTerritory().getName());
            cardTerritory.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel type = new JLabel(messages.getString("type"));
            type.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel cardType = new JLabel(messages.getString(card.getSymbol().toString()));
            cardType.setAlignmentX(Component.CENTER_ALIGNMENT);

            checkBox = new JCheckBox();
            checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(territory);
            add(cardTerritory);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(type);
            add(cardType);
            add(checkBox);

            setBorder(new LineBorder(Color.BLACK));
        }
    }
}
