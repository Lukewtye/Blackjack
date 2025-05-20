import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import javax.swing.*;

public class Blackjack {
    private ArrayList<Card> deck;

    private class Card {
        private String value;
        private String suit;

        public Card(String value, String suit) {
            this.value = value;
            this.suit = suit;
        }

        @Override
        public String toString() {
            return this.value + "-" + this.suit;
        }

        public int getValue() {
            if ("KQJ".contains(value)) {
                return 10;
            } else if (value.equals("A")) {
                return 11;
            }
            return Integer.parseInt(this.value);
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110;
    int cardHeight = 154;

    int playerscore = 1000;
    int dealerscore = 1000;

    JFrame frame = new JFrame("Blackjack - 6:5");
    Boolean dealercardhidden = true;
    Boolean initwagerplaced = false;

    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            while (initwagerplaced = false) {
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource(card.getImagePath()))).getImage();
                    g.drawImage(hiddenCardImg, 25 + cardWidth * i, 526 - cardHeight, cardWidth, cardHeight, null);
                }
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(hiddenCardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }
            }
            try {
                if (dealercardhidden) {
                    Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                    g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);
                } else {
                    Card card = hiddenCard;
                    Image faceuphiddencard = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(faceuphiddencard, 20, 20, cardWidth, cardHeight, null);
                }
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource(card.getImagePath()))).getImage();
                    g.drawImage(cardImg, 25 + cardWidth * i, 526 - cardHeight, cardWidth, cardHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!stayButton.isEnabled()) {
                String message = "";
                boolean playerblackjack = playerHand.size() == 2 && playerSum == 21;
                boolean dealerblackjack = hiddenCard.getValue() + dealerHand.get(0).getValue() == 21 &&
                        (hiddenCard.isAce() || dealerHand.get(0).isAce());

                if (playerblackjack && dealerblackjack) {
                    message = "Push - BJ";
                } else if (playerblackjack) {
                    message = "Player Wins - BJ";
                    dealerscore -= 50;
                    playerscore += 50;
                } else if (dealerblackjack) {
                    message = "Dealer Wins - BJ";
                    playerscore -= 50;
                    dealerscore += 50;
                } else if (playerSum > 21) {
                    message = "Dealer Wins";
                    playerscore -= 50;
                    dealerscore += 50;
                } else if (dealerSum > 21) {
                    message = "Player Wins";
                    dealerscore -= 50;
                    playerscore += 50;
                } else if (playerSum > dealerSum) {
                    message = "Player Wins";
                    dealerscore -= 50;
                    playerscore += 50;
                } else if (playerSum == dealerSum) {
                    message = "Push";
                } else {
                    message = "Dealer Wins";
                    playerscore -= 50;
                    dealerscore += 50;
                }
                g.setFont(new Font("Arial", Font.PLAIN, 30));
                g.setColor(Color.white);
                g.drawString(message, 220, (boardHeight / 2) - 30);
            }
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.white);
            g.drawString("Playerscore: " + playerscore, 25, (boardHeight / 2) + 15);
            g.drawString("Dealerscore: " + dealerscore, 25, (boardHeight / 2) - 7);

        }
    };

    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    JButton newgamebutton = new JButton("New Game");

    public Blackjack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        newgamebutton.setFocusable(false);
        buttonPanel.add(newgamebutton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        String initwager = JOptionPane.showInputDialog(null, "Enter your initial wager");
        if (initwager != null) {
            initwagerplaced = true;
        }
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                while (playerSum > 21 && playerAceCount > 0) {
                    playerSum -= 10;
                    playerAceCount--;
                }
                if (playerSum > 21 && playerAceCount == 0) {
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dealercardhidden = false;
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                    while (dealerSum > 21 && dealerAceCount > 0) {
                        dealerSum -= 10;
                        dealerAceCount--;
                    }
                }
                gamePanel.repaint();
            }
        });

        newgamebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dealercardhidden = true;
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
                startGame();
                gamePanel.repaint();
            }
        });
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }

    public void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] suits = {"C", "D", "H", "S"};

        for (String value : values) {
            for (String suit : suits) {
                Card card = new Card(value, suit);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public static void main(String[] args) {
        new Blackjack();
    }
}
