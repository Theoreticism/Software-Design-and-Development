package charlie.bot.server;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.dealer.Dealer;
import charlie.dealer.Seat;
import charlie.plugin.IBot;
import java.util.HashMap;
import java.util.List;

/**
 * Charlie bot plugin that uses the IBot interface to play Charlie alongside
 * the real player.
 * @author Christopher Lee
 */
public class B9 implements IBot {

    private Hand botHand;
    protected Dealer dealer;
    protected Hid dealerHid;
    protected Hid botHid;
    protected Card upCard;
    protected Seat seat;
    protected boolean turn = false;
    protected HashMap<Hid, Hand> hands = new HashMap<>();
    private int runningCount;
    private int trueCount;
    private int decksInShoe;

    /**
     * Constructor. Initializes the running and true counts to zero for possible
     * implementation of card counting strategies.
     */
    public B9() {
        runningCount = 0;
        trueCount = 0;
    }
    
    /**
     * Called by the dealer to acquire the Hid to direct subsequent methods.
     * @return The current hand of the B9 bot.
     */
    @Override
    public Hand getHand() {
        return botHand;
    }

    /**
     * Saves the dealer for basic strategy calls and comparison of hands via
     * Hand IDs (Hid).
     * @param dealer Dealer the B9 bot is using.
     */
    @Override
    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    /**
     * Called when the B9 bot is assigned to a seat. Creates a new Hid 
     * with the seat as the parameter and creates an empty Hand from the Hid.
     * @param seat The seat assigned to B9 by the dealer.
     */
    @Override
    public void sit(Seat seat) {
        this.seat = seat;
        botHid = new Hid(seat);
        botHand = new Hand(botHid);
    }

    /**
     * Gathers the dealer's hand id from the list of hand ids provided for 
     * strategy purposes. Also determines the decks in shoe from the shoe size.
     * @param hids List of hand ids in the current game of Blackjack.
     * @param shoeSize Size of the shoe at the beginning of the game.
     */
    @Override
    public void startGame(List<Hid> hids, int shoeSize) {
        for (int i = 0; i < hids.size(); i++) {
            Hand tempHand = new Hand(hids.get(i));
            hands.put(hids.get(i), tempHand);
            if (hids.get(i).getSeat() == Seat.DEALER)
                dealerHid = hids.get(i);
        }
        decksInShoe = shoeSize / 52;
    }
    
    /**
     * Nothing for the B9 to do at the end of the game yet.
     * @param shoeSize Size of the shoe at the end of the game.
     */
    @Override
    public void endGame(int shoeSize) {

    }

    /**
     * This is invoked every time the dealer deals a card.
     * @param hid Specifies the hid of the hand receiving the dealt card.
     * @param card The card being dealt.
     * @param values The values of the hand, literal and soft.
     */
    @Override
    public void deal(Hid hid, Card card, int[] values) {
        if (card == null)
            return;
        
        Hand tempHand = hands.get(hid);
        
        if (tempHand == null) {
            tempHand = new Hand(hid);
            hands.put(hid, tempHand);
        }
        
        tempHand.hit(card);
        
        if (hid == dealerHid)
            upCard = card;
         
        if (hid.getSeat() != seat || !turn) {
            turn = false;
            return;
        }
        
        this.play(hid);
    }

    /**
     * Insurance method stub.
     */
    @Override
    public void insure() {
        
    }
    
    /**
     * Bust method stub.
     * @param hid Hand id of the bot.
     */
    @Override
    public void bust(Hid hid) {
        
    }

    /**
     * Win method stub.
     * @param hid Hand id of the bot.
     */
    @Override
    public void win(Hid hid) {

    }

    /**
     * Blackjack method stub.
     * @param hid Hand id of the bot.
     */
    @Override
    public void blackjack(Hid hid) {

    }

    /**
     * Charlie method stub.
     * @param hid Hand id of the bot.
     */
    @Override
    public void charlie(Hid hid) {

    }

    /**
     * Lose method stub.
     * @param hid Hand id of the bot.
     */
    @Override
    public void lose(Hid hid) {
        
    }

    /**
     * Push method stub.
     * @param hid Hand id of the bot.
     */
    @Override
    public void push(Hid hid) {

    }

    /**
     * Resets the running count and true count on shuffle.
     */
    @Override
    public void shuffling() {
        runningCount = 0;
        trueCount = 0;
    }

    /**
     * Spawns a basic strategy analysis worker thread comparing the B9 
     * bot's hand versus the dealer's hand and returns the appropriate play.
     * @param hid Hand id
     */
    @Override
    public void play(Hid hid) {
        if (hid.getSeat() == seat) {
            turn = true;
            (new Thread(new BasicStrategyThread(botHand, upCard, this, dealer))).start();
        }
    }
    
}
