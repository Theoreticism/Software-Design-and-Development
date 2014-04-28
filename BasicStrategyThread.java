package charlie.bot.server;

import charlie.advisor.CharliePlugins;
import charlie.card.Card;
import charlie.card.Hand;
import charlie.dealer.Dealer;
import charlie.plugin.IBot;
import charlie.util.Play;

/**
 * Worker thread designed to imitate human behavior by taking a random amount
 * of time analyzing the basic strategy for Blackjack and returning the correct
 * play (hit, split, double down, or stay).
 * @author Christopher Lee
 */
public class BasicStrategyThread implements Runnable {
    
    private final CharliePlugins advisor = new CharliePlugins();
    private final Hand botHand;
    private final Card upCard;
    private final Dealer dealer;
    private final IBot bot;
    private Play botPlay;
    
    public BasicStrategyThread(Hand botHand, Card upCard, IBot bot, Dealer dealer) {
        this.botHand = botHand;
        this.upCard = upCard;
        this.dealer = dealer;
        this.bot = bot;
    }
    
    /**
     * Runs the worker thread after waiting between 2 and 5 seconds. Takes the 
     * cards in the bot's hand and the dealer's up card and analyzes them using 
     * the advisor.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(((int)(Math.random()*3) + 2)*1000);
            botPlay = advisor.advise(botHand, upCard);
            if (botPlay == Play.HIT)
                dealer.hit(bot, botHand.getHid());
            else if (botPlay == Play.DOUBLE_DOWN)
                dealer.doubleDown(bot, botHand.getHid());
            else
                dealer.stay(bot, botHand.getHid());
        } catch (InterruptedException e) {
            System.err.println("InterruptedException: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
}
