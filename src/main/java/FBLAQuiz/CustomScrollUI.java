package FBLAQuiz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * 
 * <h1>Custom Scroll UI Class</h1>
 * 
 * A custom scroll bar UI for a simple scrollbar that can
 * fade in
 * 
 * @author Varun Unnithan
 * 
 */
public class CustomScrollUI extends BasicScrollBarUI {
	
	/** The alpha level of the scrollbar, as an int between 0 and 255, inclusive */
	private int alpha;

	/**
	 * {@inheritDoc}
	 */
    public void installUI(JComponent c)   {
        super.installUI(c);
    }
	
    /**
     * Overriden to paint a transparent track
     */
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}

    /**
     * Paints the thumb as a simple rectangle with rounded corners
     */
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

    	Graphics2D g2d = (Graphics2D) g;
    	
    	//draws a new, simple, rectangular scrollbar, with given alpha level
    	g2d.setColor(new Color(25,25,25, alpha));
    	g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 8, 8);
    	
    	g2d.dispose();
    }
    
    /**
     * Gets rid of the decrease button
     */
    @Override
    protected JButton createDecreaseButton(int orientation) {
    	
    	//removes the scroll down button
    	JButton falseButton = new JButton();
    	falseButton.setPreferredSize(new Dimension(0,0));
        return falseButton;
    }

    /**
     * Gets rid of the increase button
     */
    @Override
    protected JButton createIncreaseButton(int orientation) {

    	//removes the scroll up button
    	JButton falseButton = new JButton();
    	falseButton.setPreferredSize(new Dimension(0,0));
        return falseButton;
    }
    
    
    /**
     * Fades in the scrollbar from transparent to fully opaque
     */
    public void fadeIn() {
    	
    	//fire an event to change the alpha
		Timer fadeTimer = new Timer(35, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//increase the alpha value
				alpha += 50;
				
				//stop the Timer when the alpha equals 1
				if (alpha >= 255) {
					alpha = 255;
					scrollbar.repaint();
					((Timer)(e.getSource())).stop();
				}
				
				scrollbar.repaint();
			}
			
		});
		fadeTimer.start();
    }
}
