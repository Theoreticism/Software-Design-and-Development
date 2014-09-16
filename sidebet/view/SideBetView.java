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

package charlie.sidebet.view;

import charlie.audio.Effect;
import charlie.audio.SoundFactory;
import charlie.card.Hid;
import charlie.plugin.ISideBetView;
import charlie.view.AHand.Outcome;
import charlie.view.AMoneyManager;

import charlie.view.sprite.ChipButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the side bet view
 * @author Ron Coleman, Ph.D.
 */
public class SideBetView implements ISideBetView {
    private final Logger LOG = LoggerFactory.getLogger(SideBetView.class);
    
    public final static int X = 400;
    public final static int Y = 200;
    public final static int DIAMETER = 50;
    public final static int PLACE_HOME_X = X-DIAMETER/2 + DIAMETER + 10;
    public final static int PLACE_HOME_Y = Y-DIAMETER/2 + DIAMETER / 4;
   
    protected Font font = new Font("Arial", Font.BOLD, 18);
    protected Font payoutInfo = new Font("Arial", Font.BOLD, 14);
    protected BasicStroke stroke = new BasicStroke(3);
    
    // See http://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html
    protected float dash1[] = {10.0f};
    protected BasicStroke dashed
            = new BasicStroke(3.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);   

    protected List<ChipButton> buttons;
    protected List<charlie.view.sprite.Chip> chips = new ArrayList<>();
    protected int amt = 0;
    protected int width = 0;
    protected AMoneyManager moneyManager;
    
    protected Image hundredChip = new ImageIcon("./images/chip-100-1.png").getImage();
    protected Image twentyFiveChip = new ImageIcon("./images/chip-25-1.png").getImage();
    protected Image fiveChip = new ImageIcon("./images/chip-5-1.png").getImage();
  
    protected Random random = new Random();
    protected Integer[] amounts = { 100, 25, 5 };
    protected Outcome outcome = Outcome.None;

    public SideBetView() {
        LOG.info("side bet view constructed");
    }
    
    /**
     * Sets the money manager.
     * @param moneyManager 
     */
    @Override
    public void setMoneyManager(AMoneyManager moneyManager) {
        this.moneyManager = moneyManager;
        this.buttons = moneyManager.getButtons();
    }
    
    /**
     * Registers a click for the side bet.
     * @param x X coordinate
     * @param y Y coordinate
     */
    @Override
    public void click(int x, int y) {
        int oldAmt = amt;
        
        // Test if any chip button has been pressed.
        for(int i=0; i < buttons.size(); i++) {
            ChipButton button = buttons.get(i);
            if(button.isReady() && button.isPressed(x, y)) {
                width = fiveChip.getWidth(null);
                int n = chips.size();
                int placeX = PLACE_HOME_X + n * width/3 + random.nextInt(10)-10;
                int placeY = PLACE_HOME_Y + random.nextInt(5)-5;

                charlie.view.sprite.Chip chip = new charlie.view.sprite.Chip(button.getImage(),placeX,placeY,amounts[i]);
                chips.add(chip);

                amt += button.getAmt();
                SoundFactory.play(Effect.CHIPS_IN);
                LOG.info("A. side bet amount "+button.getAmt()+" updated new amt = "+amt);
            } 
        }
        
        if(oldAmt == amt) {
            amt = 0;
            chips.clear();
            SoundFactory.play(Effect.CHIPS_OUT);
            LOG.info("B. side bet amount cleared");
        }
    }

    /**
     * Informs view the game is over and it's time to update the bankroll for the hand.
     * @param hid Hand id
     */
    @Override
    public void ending(Hid hid) {
        double bet = hid.getSideAmt();
        
        if(bet == 0)
            return;

        // Update outcome for "WIN" or "LOSE" rendering
        if(bet > 0)
            outcome = Outcome.Win;
        else
            outcome = Outcome.Lose;
        
        LOG.info("side bet outcome = "+bet);
        
        // Update the bankroll
        moneyManager.increase(bet);
        
        LOG.info("new bankroll = "+moneyManager.getBankroll());
    }

    /**
     * Informs view the game is starting
     */
    @Override
    public void starting() {
        // Reset outcome
        outcome = Outcome.None;
    }

    /**
     * Gets the side bet amount.
     * @return Bet amount
     */
    @Override
    public Integer getAmt() {
        return amt;
    }

    /**
     * Updates the view
     */
    @Override
    public void update() {
    }

    /**
     * Renders the view
     * @param g Graphics context
     */
    @Override
    public void render(Graphics2D g) {
        // Draw the at-stake place on the table
        g.setColor(Color.RED); 
        g.setStroke(dashed);
        g.drawOval(X-DIAMETER/2, Y-DIAMETER/2, DIAMETER, DIAMETER);
        
        // Draw the at-stake amount
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString(""+amt, X-5, Y+5);
        
        // Draw the betting payout information for SUPER 7
        g.setFont(payoutInfo);
        g.setColor(Color.BLACK);
        g.drawString("SUPER 7 pays 3:1", X+75, Y+100);
        
        // Draw the betting payout information for ROYAL MATCH
        g.setFont(payoutInfo);
        g.setColor(Color.BLACK);
        g.drawString("ROYAL MATCH pays 25:1", X+75, Y+115);
        
        // Draw the betting payout information for EXACTLY 13
        g.setFont(payoutInfo);
        g.setColor(Color.BLACK);
        g.drawString("EXACTLY 13 pays 1:1", X+75, Y+130);
        
        // Draws the randomized chip images to the right of the in-stake area
        for(int i=0; i < chips.size(); i++) {
            charlie.view.sprite.Chip chip = chips.get(i);
            chip.render(g);
        }
        
        // Draws highlighted "WIN" or "LOSE" over at-stake chips
        if (outcome == Outcome.Win) {
            
            // Paint win background
            FontMetrics fm = g.getFontMetrics(font);
            int w = fm.charsWidth("WIN!".toCharArray(), 0, "WIN!".length());
            int h = fm.getHeight();
            g.setColor(new Color(116,255,4));
            g.fillRoundRect(X-DIAMETER/2, (Y-DIAMETER/2)-h+5, w, h, 5, 5);
            
            // Paint win foreground
            g.setFont(font);
            g.setColor(Color.BLACK);
            g.drawString("WIN!", X-DIAMETER/2, Y-DIAMETER/2);
            
        } else if (outcome == Outcome.Lose) {
            
            // Paint lose background
            FontMetrics fm = g.getFontMetrics(font);
            int w = fm.charsWidth("LOSE!".toCharArray(), 0, "LOSE!".length());
            int h = fm.getHeight();
            g.setColor(new Color(250,58,5));
            g.fillRoundRect(X-DIAMETER/2, (Y-DIAMETER/2)-h+5, w, h, 5, 5);
            
            // Paint lose foreground
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString("LOSE!", X-DIAMETER/2, Y-DIAMETER/2);
            
        }
    }
}
