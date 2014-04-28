package charlie.advisor;

import charlie.card.Card;
import charlie.card.Hand;
import charlie.plugin.IAdvisor;
import charlie.util.Play;
import java.util.HashMap;
import java.util.Map;

/**
 * Plugin for the Charlie program that initializes and runs the advisor 
 * according to the basic strategy for Blackjack. Allows the player to get
 * help from the advisor by checking the box for advice.
 * @author Christopher Lee
 */
public class CharliePlugins implements IAdvisor {

    Map<Pair<Integer,Integer>, Play> other = new HashMap<>();
    Map<Pair<Integer,Integer>, Play> ace_hand = new HashMap<>();
    Map<Pair<Integer,Integer>, Play> doubles = new HashMap<>();
    
    
    /*
     * CharliePlugins constructor, builds the ArrayLists and HashMaps 
     */
    public CharliePlugins() {
        otherInitialize();
        aceHandInitialize();
        doubleHandInitialize();
    }
    
    @Override
    public Play advise(Hand myHand, Card upCard) {
        Pair temp;
        if (myHand.size() < 3) {
            if (myHand.getCard(0).value() == myHand.getCard(1).value()) {
                temp = new Pair(myHand.getCard(0).value(), upCard.value());
                return doubles.get(temp);
            } else if (myHand.getCard(0).isAce() && !myHand.getCard(1).isAce()) {
                temp = new Pair(myHand.getCard(1).value(), upCard.value());
                return ace_hand.get(temp);
            } else if (myHand.getCard(1).isAce() && !myHand.getCard(0).isAce()) {
                temp = new Pair(myHand.getCard(0).value(), upCard.value());
                return ace_hand.get(temp);
            } else {
                temp = new Pair(myHand.getValue(), upCard.value());
                return other.get(temp);
            }
        } else {
            temp = new Pair(myHand.getValue(), upCard.value());
            return other.get(temp);
        }
    }
    
    /*
     * Fills the first hashMap (other) with recommended plays based on the
     * total value of the cards in the player's hand
     */
    public final void otherInitialize() {
        
         // Total = 17 or up
        for (int a = 17; a < 21; a++) {
            for (int j = 1; j < 11; j++) {
                other.put(Pair.create(a, j), Play.STAY);
            }
        }
        // Total = 13-16
        for (int b = 13; b < 17; b++) {
            for (int j = 1; j < 11; j++) {
                if (j >= 2 && j <= 6)
                    other.put(Pair.create(b, j), Play.STAY);
                else
                    other.put(Pair.create(b, j), Play.HIT);
            }
        }
        // Total = 5-8
        for (int c = 5; c < 9; c++) {
            for (int j = 1; j < 11; j++)
                other.put(Pair.create(c, j), Play.HIT);
        }
        // Total = 12, 11, 10
        for (int orig = 1; orig < 11; orig++) {
            if (orig >= 4 && orig <= 6)
                other.put(Pair.create(12, orig), Play.STAY);
            else
                other.put(Pair.create(12, orig), Play.HIT);
            if (orig == 1)
                other.put(Pair.create(11, orig), Play.HIT);
            else
                other.put(Pair.create(11, orig), Play.DOUBLE_DOWN);
            if (orig >= 2 && orig <= 9)
                other.put(Pair.create(10, orig), Play.DOUBLE_DOWN);
            else
                other.put(Pair.create(10, orig), Play.HIT);
            if (orig >= 3 && orig <= 6)
                other.put(Pair.create(9, orig), Play.DOUBLE_DOWN);
            else
                other.put(Pair.create(9, orig), Play.HIT);
        }
        
    }
    
    /*
     * Fills the second hashMap (ace_hand) with recommended plays based on the
     * value of the non-ace card in the player's hand
     */
    public final void aceHandInitialize() {
        
        //Ace + 2, 3, 4, 5, 6, 8, 9, or 10
        for (int a = 1; a < 11; a++) {
            ace_hand.put(Pair.create(10, a), Play.STAY);
            ace_hand.put(Pair.create(9, a), Play.STAY);
            ace_hand.put(Pair.create(8, a), Play.STAY);
            if (a >= 3 && a <= 6) 
                ace_hand.put(Pair.create(6, a), Play.DOUBLE_DOWN);
            else
                ace_hand.put(Pair.create(6, a), Play.HIT);
            if (a >= 4 && a <= 6) {
                ace_hand.put(Pair.create(5, a), Play.DOUBLE_DOWN);
                ace_hand.put(Pair.create(4, a), Play.DOUBLE_DOWN);
            } else {
                ace_hand.put(Pair.create(5, a), Play.HIT);
                ace_hand.put(Pair.create(4, a), Play.HIT);
            }
            if (a == 5 || a == 6) {
                ace_hand.put(Pair.create(3, a), Play.DOUBLE_DOWN);
                ace_hand.put(Pair.create(2, a), Play.DOUBLE_DOWN);
            } else {
                ace_hand.put(Pair.create(3, a), Play.HIT);
                ace_hand.put(Pair.create(2, a), Play.HIT);
            }
        }
        //Ace + 7
        for (int b = 1; b < 11; b++) {
            if (b >= 3 && b <= 6) 
                ace_hand.put(Pair.create(7, b), Play.DOUBLE_DOWN);
            else if (b == 2 || b == 7 || b == 8)
                ace_hand.put(Pair.create(7, b), Play.STAY);
            else
                ace_hand.put(Pair.create(7, b), Play.HIT);
        }
   
    }
    
    /*
     * Fills the third hashMap (doubles) with recommended plays based on one of
     * the cards in the player's hand, given the hand is a pair
     */
    public final void doubleHandInitialize() {
        
        //All Doubles
        for (int a = 1; a < 11; a++) {
            doubles.put(Pair.create(10, a), Play.SPLIT);
            doubles.put(Pair.create(8, a), Play.SPLIT);
            doubles.put(Pair.create(1, a), Play.STAY);
            if (a >= 2 && a <= 7) {
                doubles.put(Pair.create(7, a), Play.SPLIT);
                doubles.put(Pair.create(3, a), Play.SPLIT);
                doubles.put(Pair.create(2, a), Play.SPLIT);
            } else {
                doubles.put(Pair.create(7, a), Play.HIT);
                doubles.put(Pair.create(3, a), Play.HIT);
                doubles.put(Pair.create(2, a), Play.HIT);
            }
            if (a == 1 || a == 7 || a == 10)
                doubles.put(Pair.create(9, a), Play.STAY);
            else
                doubles.put(Pair.create(9, a), Play.SPLIT);
            if (a >= 2 && a <= 6) 
                doubles.put(Pair.create(6, a), Play.SPLIT);
            else
                doubles.put(Pair.create(6, a), Play.HIT);
            if (a >= 2 && a <= 9)
                doubles.put(Pair.create(5, a), Play.DOUBLE_DOWN);
            else
                doubles.put(Pair.create(5, a), Play.HIT);
            if (a == 5 || a == 6)
                doubles.put(Pair.create(4, a), Play.SPLIT);
            else
                doubles.put(Pair.create(4, a), Play.HIT);
        }
        
    }
    
}
