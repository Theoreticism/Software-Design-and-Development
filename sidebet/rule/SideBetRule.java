/*
 Copyright (c) 2014 Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package charlie.sidebet.rule;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.plugin.ISideBetRule;
import charlie.view.AHand.Outcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the side bet rule for Super 7.
 * @author Ron Coleman
 */
public class SideBetRule implements ISideBetRule {
    private final Logger LOG = LoggerFactory.getLogger(SideBetRule.class);
    
    private final Double PAYOFF_SUPER7 = 3.0;
    private final Double PAYOFF_ROYAL_MATCH = 25.0;
    private final Double PAYOFF_EXACTLY13 = 1.0;
    
    protected static Outcome outcome = Outcome.None;

    /**     
     * Apply rule to the hand and return the payout if the rule matches
     * and the negative bet if the rule does not match.
     * @param hand Hand to analyze.
     * @return 
     */
    @Override
    public double apply(Hand hand) {

        
        Double bet = hand.getHid().getSideAmt();
        LOG.info("side bet amount = "+bet);
        
        if(bet == 0)
            return 0.0;
        
        LOG.info("side bet rule applying hand = "+hand);
        
        Card card = hand.getCard(0);
        Card card2 = hand.getCard(1);
 
        if(card.getRank() == 7) {
            LOG.info("side bet SUPER 7 matches");
            outcome = Outcome.Win;
            return bet * PAYOFF_SUPER7;
        } 
        if((card.getRank() == 12 && card2.getRank() == 13 && card.getSuit() == card2.getSuit())||
                (card.getRank() == 13 && card2.getRank() == 12 && card.getSuit() == card2.getSuit())) {
            LOG.info("side bet ROYAL MATCH matches");
            outcome = Outcome.Win;
            return bet * PAYOFF_ROYAL_MATCH;
        }
        if(hand.getValue() == 13) {
            LOG.info("side bet EXACTLY 13 matches");
            outcome = Outcome.Win;
            return bet * PAYOFF_EXACTLY13;
        }
        
        LOG.info("side bet rule no match");
        outcome = Outcome.Lose;
        return -bet;
    }
    
    public static Outcome getOutcome() {
        return outcome;
    }
}
