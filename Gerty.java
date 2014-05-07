package charlie.bot.client;

import charlie.actor.Courier;
import charlie.advisor.Advisor;
import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Seat;
import charlie.plugin.IGerty;
import charlie.util.Play;
import charlie.view.AMoneyManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.HashMap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Gerty bot implementation of IPlayer similar to IBot that plays Charlie, 
 * except in the seat YOU, rather than the seat LEFT or seat RIGHT. Human player
 * input is disabled while the Gerty bot is active.
 * @author Chris
 */
public class Gerty implements IGerty {
    
    protected final int minBet = 5;
    protected final int X = 10;
    protected final int Y = 275;
    protected final double VARIANCE = 1.30347889;
    protected double advantage;
    protected double fraction;
    protected double bankroll;
    protected double betTotal;
    protected double mean;
    protected double maxBet;
    protected int count;
    protected int blackjacks;
    protected int charlies;
    protected int wins;
    protected int breaks;
    protected int loses;
    protected int shoeSize;
    protected int gameCount;
    protected boolean turn;
    protected Font stats = new Font("Arial", Font.BOLD, 14);
    protected HashMap<Hid, Hand> hands = new HashMap<>();
    protected Courier courier;
    protected AMoneyManager moneyManager;
    protected Hand botHand;
    protected Card upCard;
    protected Hid gertyHid;

    /**
     * Constructor for value initialization if necessary
     */
    public Gerty() {
        
    }
    
    /**
     * Called when each new Blackjack game begins with the Gerty bot in place.
     * Varies bets according to Kelly's criterion and card counting practices.
     */
    @Override
    public void go() {
        gameCount++;
        
        moneyManager.clearBet();
        
        //Advantage approxmations
        if (count >= 5)
            advantage = 0.05;
        else if (count == 4)
            advantage = 0.04;
        else if (count == 3)
            advantage = 0.03;
        else if (count == 2)
            advantage = 0.02;
        else if (count == 1)
            advantage = 0.01;
        else 
            advantage = 0;
        
        fraction = advantage / VARIANCE;
        
        bankroll = moneyManager.getBankroll();
        
        //Minimum bet, main bets only, no side bets
        int tempBet;
        if (fraction <= 0) {
            tempBet = minBet;
            moneyManager.upBet(tempBet);
        } else {
            tempBet = (int)((fraction * bankroll) - ((fraction * bankroll)%5));
            for (int i = 0; i < tempBet; i = i + 5) {
                moneyManager.upBet(minBet);
            }
        }
        gertyHid = courier.bet(tempBet, 0);
        if (tempBet > maxBet)
            maxBet = tempBet;
        betTotal += tempBet;
        mean = betTotal / gameCount
                ;
    }

    /**
     * Sets the courier that Gerty is communicating with.
     * @param courier The courier being used.
     */
    @Override
    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    /**
     * Sets the money manager that Gerty is using to update bets on the GUI.
     * @param moneyManager The money manager being used.
     */
    @Override
    public void setMoneyManager(AMoneyManager moneyManager) {
        this.moneyManager = moneyManager;
    }

    /**
     * Updates game screen and prepares for rendering.
     */
    @Override
    public void update() {
    }

    /**
     * Renders the game screen.
     * @param g Graphics context.
     */
    @Override
    public void render(Graphics2D g) {
        g.setFont(stats);
        g.setColor(Color.BLACK);
        g.drawString("Counting System: Omega II", X, Y);
        g.drawString("Running count: " + count, X, Y+15);
        g.drawString("Blackjacks: " + blackjacks + " | Charlies: " + charlies, X, Y+30);
        g.drawString("Wins: " + wins + " | Breaks: " + breaks + " | Loses: " + loses, X, Y+45);
        g.drawString("Max bet amount: " + maxBet, X, Y+60);
        g.drawString("Mean bet amount: " + mean, X, Y+75);
        g.drawString("Games: " + gameCount, X, Y+90);
        g.drawString("Shoe size: " + shoeSize, X, Y+105);
    }

    /**
     * Determines a hashmap of the hands in the game from the list of hand ids
     * provided for strategy purposes. Also determines the decks in shoe from
     * the shoe size.
     * @param hids List of hand ids in the current game of Blackjack.
     * @param shoeSize Size of the shoe at the beginning of each game.
     */
    @Override
    public void startGame(List<Hid> hids, int shoeSize) {
        this.shoeSize = shoeSize;
        for (int i = 0; i < hids.size(); i++) {
            Hand tempHand = new Hand(hids.get(i));
            hands.put(hids.get(i), tempHand);
        }
    }

    /**
     * Handles actions performed at the end of each Blackjack game.
     * @param shoeSize Size of the shoe at the end of the game.
     */
    @Override
    public void endGame(int shoeSize) {
        this.shoeSize = shoeSize;
    }

    /**
     * Invoked every time dealer deals a card.
     * @param hid Specifies the hid of the hand receiving the dealt card.
     * @param card The card being dealt.
     * @param values The values of the hand, literal and soft.
     */
    @Override
    public void deal(Hid hid, Card card, int[] values) {
        if (card == null)
            return;
        
        if (card.getRank() == 10 || card.isFace())
            count = count - 2;
        else if (card.getRank() == 9)
            count = count - 1;
        else if (card.getRank() == 2 || card.getRank() == 3 || card.getRank() == 7)
            count = count + 1;
        else if (card.getRank() == 4 || card.getRank() == 5 || card.getRank() == 6)
            count = count + 2;
        else
            count = count + 0;
        
        Hand tempHand = hands.get(hid);
        
        if (tempHand == null) {
            tempHand = new Hand(hid);
            hands.put(hid, tempHand);
        }
        
        tempHand.hit(card);
        
        if (hid.getSeat() == Seat.DEALER)
            upCard = card;
         
        if (hid.getSeat() != Seat.YOU || !turn) {
            turn = false;
            return;
        }
        
        this.play(hid);
    }

    /**
     * Handles insurance options. Method stub.
     */
    @Override
    public void insure() {
        
    }

    /**
     * Handles hand actions for bust conditions.
     * @param hid Specified hand id.
     */
    @Override
    public void bust(Hid hid) {
        loses++;
    }

    /**
     * Handles hand actions for win conditions.
     * @param hid Specified hand id.
     */
    @Override
    public void win(Hid hid) {
        wins++;
    }

    /**
     * Handles hand actions for Blackjack conditions.
     * @param hid Specified hand id.
     */
    @Override
    public void blackjack(Hid hid) {
        blackjacks++;
    }

    /**
     * Handles hand actions for Charlie conditions.
     * @param hid Specified hand id.
     */
    @Override
    public void charlie(Hid hid) {
        charlies++;
    }

    /**
     * Handles hand actions for lose conditions.
     * @param hid Specified hand id.
     */
    @Override
    public void lose(Hid hid) {
        loses++;
    }

    /**
     * Handles hand actions for push conditions.
     * @param hid Specified hand id.
     */
    @Override
    public void push(Hid hid) {
        breaks++;
    }

    /**
     * Resets the running count on shuffle.
     */
    @Override
    public void shuffling() {
        count = 0;
    }

    /**
     * Spawns a basic strategy analysis worker thread comparing the Gerty bot's
     * hand versus the dealer hand's visible cards and returns the appropriate
     * play.
     * @param hid Specified hand id.
     */
    @Override
    public void play(Hid hid) {
        if (hid.getSeat() == Seat.YOU) {
            turn = true;
            botHand = hands.get(hid);
            Advisor advisor = new Advisor();
            Play botPlay;
            
            try {
                Thread.sleep(((int)(Math.random()*3) + 2) * 1000);
            } catch (InterruptedException e) {
                System.err.println("InterruptedException: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            if (!(botHand.isBlackjack() || botHand.isBroke() ||
                    botHand.isCharlie() || botHand.getValue() == 21)) {
                botPlay = advisor.advise(botHand, upCard);
                if (botPlay == Play.HIT)
                    courier.hit(hid);
                else if (botPlay == Play.DOUBLE_DOWN)

                    courier.dubble(hid);
                else
                    courier.stay(hid);
            }
        }
    }
    
}
