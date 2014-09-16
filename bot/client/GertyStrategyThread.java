package charlie.bot.client;

import charlie.actor.Courier;
import charlie.advisor.Advisor;
import charlie.card.Card;
import charlie.card.Hand;
import charlie.card.Hid;
import charlie.util.Play;

/**
 * Worker thread designed to imitate human behavior by taking a random amount
 * of time analyzing the basic strategy for Blackjack and returning the correct
 * play (hit, split, double down, or stay).
 * @author Christopher Lee
 */
public class GertyStrategyThread implements Runnable {

    private final Advisor advisor = new Advisor();
    private final Hid hid;
    private final Hand botHand;
    private final Card upCard;
    private final Courier courier;
    private Play botPlay;
    
    public GertyStrategyThread(Hand botHand, Card upCard, Hid hid, Courier courier) {
        this.botHand = botHand;
        this.upCard = upCard;
        this.hid = hid;
        this.courier = courier;
    }
    
    @Override
    public void run() {
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
