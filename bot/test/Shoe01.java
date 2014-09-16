package charlie.bot.test;

import charlie.card.Shoe;
import java.util.Random;

/**
 * Implements a test shoe of a random seed (non-zero) as directed by the 
 * assignment requirements.
 * @author Chris
 */
public class Shoe01 extends Shoe {
    
    @Override
    public void init() {
        
        super.ran = new Random(42);
        
        super.numDecks = 1;
        
        super.load();
        
        super.shuffle();
        
    }
    
}
