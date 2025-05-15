import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Collections;
import java.util.Objects;

public class Blackjack {

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

    private ArrayList<Card> deck;

    //dealer stuff
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //player stuff
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //window
    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110; //1:1.4 ratio
    int cardHeight = 154;

    int playerscore = 1000;
    int dealerscore = 1000;
    JFrame frame = new JFrame("Blackjack - 6:5");
    Boolean dealercardhidden = true;
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            JTextField textField = new JTextField(); // Creating text field
            textField.setBounds(50, 40, 200, 30);
            frame.add(textField);
            try {
                //draw hidden card
                if (dealercardhidden) {
                    Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                    g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);
                } else {
                    Card card = hiddenCard;
                    Image faceuphiddencard = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(faceuphiddencard, 20, 20, cardWidth, cardHeight, null);
                }
                //draw dealer's hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg,cardWidth + 25 + (cardWidth + 5)*i,20,cardWidth,cardHeight,null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //draw player hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(Objects.requireNonNull(getClass().getResource(card.getImagePath()))).getImage();
                    g.drawImage(cardImg, 25 + cardWidth * i, 526 - cardHeight, cardWidth, cardHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!stayButton.isEnabled()){ // Prints message depending on result
                System.out.println("STAY: ");
                System.out.println("DEALER: " + dealerSum);
                System.out.println("PLAYER: "+playerSum);

                String message = "";

                boolean playerblackjack = playerHand.size() == 2 && playerSum == 21;
                boolean dealerblackjack = hiddenCard.getValue() + dealerHand.get(0).getValue() == 21 &&
                        (hiddenCard.isAce() ||dealerHand.get(0).isAce());

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
                g.setFont(new Font("Arial",Font.PLAIN,30));
                g.setColor(Color.white);
                g.drawString(message,220, (boardHeight/2) - 30);

            }
            g.setFont(new Font("Arial",Font.PLAIN,20));
            g.setColor(Color.white);
            g.drawString("Playerscore: " + String.valueOf(playerscore),25, (boardHeight/2) + 15);
            g.drawString("Dealerscore: " + String.valueOf(dealerscore),25, (boardHeight/2) - 7);
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
        frame.add(buttonPanel, BorderLayout.SOUTH);
        newgamebutton.setFocusable(false);
        buttonPanel.add(newgamebutton);


        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Boolean playerbust = false;

                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                gamePanel.repaint();
                while (playerSum > 21 && playerAceCount > 0) {
                    playerSum -= 10;
                    playerAceCount--;
                }
                if (playerSum > 21 && playerAceCount == 0) {
                    System.out.println(card + " - Playersum: " + playerSum);
                    System.out.println("You bust");
                    playerbust = true;
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                } else {
                    System.out.println(card + " - Playersum: " + playerSum);
                }

                gamePanel.repaint();
            }
        });
        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dealercardhidden = false;

                gamePanel.repaint();
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) { // Hits until > 17
                    Card card = deck.removeLast();
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                    System.out.println("Dealer Card added" + card);
                    System.out.println("Dealer Hand Value" + dealerSum);
                    gamePanel.repaint();
                    while (dealerSum > 21 && dealerAceCount > 0) { //Reduces count if ace
                        dealerSum -= 10;
                        dealerAceCount--;
                    }
                }
            }
        }); // <- Fixed bracketing here
        newgamebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                dealercardhidden = true;
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
                startGame();
                gamePanel.repaint();

            }
        });
    }

    public void startGame () {
        //deck
        buildDeck();
        shuffleDeck();

        //dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.removeLast();
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.removeLast();
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER HAND");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);


        //player stuff
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;
        for (int i = 0; i < 2; i++) {
            card = deck.removeLast();
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("PLAYER HAND: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }

    public void buildDeck () {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] suits = {"C", "D", "H", "S"};

        // use some loop structure to make a deck of cards
        // 1) make Card object with (value, suit)
        // 2) add Card object to deck
        // do this for all value suit combos
        for (String value : values) {
            for (String suit : suits) {
                Card card = new Card(value, suit);
                deck.add(card);
            }
        }

        System.out.println("BUILD DECK: ");
        System.out.println(deck);
    }

    public void shuffleDeck () {
        Collections.shuffle(deck);
        System.out.println("AFTER SHUFFLE");
        System.out.println(deck);
    }
}
