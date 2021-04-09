package FBLAQuiz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JPanel;


/**
 * <h1> User Panel Class</h1>
 * 
 * The UserPanel class creates and decorates a JPanel object for use 
 * as the bottom section for each of the question classes.
 * 
 * @author Varun Unnithan
 *
 */
@SuppressWarnings("serial")
public class UserPanel extends JPanel{
	
	

	/**
	 * Overriden method for painting the background of UserPanel object
	 * @param g the Graphics object to protect
	 */
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		//setting width and height inside the paintComponent method updates it each time window is resized
		int width = getWidth();			
        int height = getHeight();
		
        //casts Graphics object to new Graphics2D object
		Graphics2D g2d = (Graphics2D) g;		
		//sets priority render quality
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); 	
        
		//creates a gradient for bottom section of panel
        GradientPaint gp = new GradientPaint(0, 0, new Color(0x0055FF), width, height,new Color(0x0000CF));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
        
        //gets default Stroke
      	Stroke defaultStroke = g2d.getStroke();
      		
      	//draws the line to seperate the question and its user options
        g2d.setStroke(new BasicStroke(30));
        g2d.setPaint(Color.lightGray);
        g2d.drawLine(0, 0, width, 0);
      		
        //resets the Stroke to its default
        g2d.setStroke(defaultStroke);
        
	}

	
}
