package FBLAQuiz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.Timer;


/**
 * <h1>Draggable Line Class</h1>
 * 
 * The DraggableLine class creates a component which is a line with one end
 * fixed, and the other being draggable by the mouse.
 * 
 * @author Varun Unnithan
 * 
 */
@SuppressWarnings("serial")
public class DraggableLine extends JComponent{


	//------------Instance Variables------------
	/** The Point representation of the line's pivot point */
	private Point fixedPoint;
	/** The Point representation of the line's detached/draggable point */
	private Point draggedPoint;
	/** The List of all possible destinations for the line to snap to */
	private ArrayList<Point> destinationPoints;
	/** The color of the line */
	private Color lineColor;
	/** The thickness of the line in pixels */
	private float thickness;
	/** Whether the line has snapped to a destination point or not */
	private boolean hasSnapped;
	/** Whether the component is currently selected or not */
	private boolean isSelected;
	
	
	/**
	 * Constructs a default DraggableLine object which starts at (0, 0) with a black line color and 10 pixel thickness
	 */
	public DraggableLine() {
		
		//sets the coordinates for the line's two points
		fixedPoint = new Point(0,0);
		draggedPoint = new Point(0,0);
		
		//instantiate other instance variables
		lineColor = Color.black;
		thickness = 10f;
		destinationPoints = new ArrayList<Point>();
		hasSnapped = false;
		
		//adds necessary mouse listeners
		this.addMouseListener(new MouseListener());
		this.addMouseMotionListener(new DragListener());
	}
	
	
	/**
	 * Constructs a DraggableLine object which starts at (x, y) with color and thickness attributes
	 * @param x The x-coordinate of the starting position
	 * @param y The y-coordinate of the starting position
	 * @param color The color of the line
	 * @param thickness The thickness of the line in pixels
	 */
	public DraggableLine(int x, int y, Color color, float thickness) {
		
		//sets the coordinates for the line's two points
		fixedPoint = new Point(x, y);
		draggedPoint = new Point(x, y);
		
		//instantiate other instance variables
		lineColor = color;
		this.thickness = thickness;
		destinationPoints = new ArrayList<Point>();
		hasSnapped = false;
		
		//adds necessary mouse listeners
		this.addMouseListener(new MouseListener());
		this.addMouseMotionListener(new DragListener());
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		//draws the line
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(lineColor);
		g2d.draw(new Line2D.Double(fixedPoint.getX(), fixedPoint.getY(), draggedPoint.getX(), draggedPoint.getY()));
		
	}
	
	@Override
	public boolean contains(int x, int y) {
		
		Point parameter = new Point(x,y);
		return (fixedPoint.distance(parameter) <= 25) || (draggedPoint.distance(parameter) <= 25);
	}
	
	/**
	 * Method to get the line's pivot point
	 * @return Point representation of the line's pivot point
	 */
	public Point getFixedPoint() {
		return fixedPoint;
	}
	
	
	/**
	 * Method to get the line's detached point
	 * @return Point representation of the line's draggable point
	 */
	public Point getDraggedPoint() {
		return draggedPoint;
	}
	
	
	/**
	 * Method to get the line's color
	 * @return The color of the line
	 */
	public Color getColor() {
		return lineColor;
	}
	
	
	/**
	 * Method to get the line's thickness
	 * @return The line's thickness as a float amount of pixels
	 */
	public float getThickness() {
		return thickness;
	}
	
	
	/**
	 * Method to get all the destinations to which the line will snap to
	 * @return ArrayList of Points of destinations for the line
	 */
	public ArrayList<Point> getDestinations() {
		return destinationPoints;
	}
	
	
	/**
	 * Method to see if the line has snapped to a destination point
	 * @return Boolean value of whether the line is currently snapped to a destination point or not
	 */
	public boolean isSnapped() {
		return hasSnapped;
	}
	
	
	/**
	 * Changes the line's pivot point
	 * @param pivot The Point to which the line's pivot should be set 
	 */
	public void setFixedPoint(Point pivot) {
		fixedPoint = pivot;
		draggedPoint = pivot;
		repaint();
	}
	
	
	/**
	 * Changes the line's detached point
	 * @param dragged The Point to which the line's detached end should be set
	 */
	public void setDraggedPoint(Point dragged) {
		draggedPoint = dragged;
		repaint();
	}
	
	
	/**
	 * Method to set the line's color
	 * @param color The color of the line
	 */
	public void setColor(Color color) {
		lineColor = color;
		repaint();
	}
	
	
	/**
	 * Method to set the line's thickness
	 * @param thickness The number of pixels wide the line is, as a float
	 */
	public void setThickness(float thickness) {
		this.thickness = thickness;
		repaint();
	}
	
	
	/**
	 * Replaces the current list of destination points with a new one
	 * @param destinations An ArrayList of Point objects to be used as the line's destiantion points
	 */
	public void setDestinations(ArrayList<Point> destinations) {
		destinationPoints = destinations;
	}
	
	
	/**
	 * Convenience method to add a destination
	 * @param destination A Point to add to the current ArrayList of destinations
	 */
	public void addDestination(Point destination) {
		destinationPoints.add(destination);
	}
	
	
	/**
	 * Convenience method to remove a destination
	 * @param destination A Point to remove from the current ArrayList of destinations
	 */
	public void removeDestination(Point destination) {
		destinationPoints.remove(destination);
	}
	
	
	/**
	 * Inner class for when the mouse drags the line
	 * @see MouseMotionAdapter
	 */
	private class DragListener extends MouseMotionAdapter{
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			
			//if the line is clicked, make the detached end follow the mouse
			if (isSelected) {
				
				draggedPoint = e.getPoint();			
			
				//check to see if there is a nearby destination point
				for (Point destination: destinationPoints) {
					
					if (draggedPoint.distance(destination) <= 25) {
						draggedPoint = destination;
						hasSnapped = true;
						break;
					}
					else {
						hasSnapped = false;
					}
				}
			}
			
			repaint();
			
		}	
	}
	
	
	/**
	 * Inner class for when the mouse is clicked or released
	 * @see MouseAdapter
	 */
	private class MouseListener extends MouseAdapter {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			
			//if the line isn't snapped to a point, return it to the start
			if (!hasSnapped) {
				
				//calculate the step for the line retraction animation
				final int dx = (int)((fixedPoint.getX() - draggedPoint.getX())/15);
				final int dy = (int)((fixedPoint.getY() - draggedPoint.getY())/15);
				final Point initialPoint = draggedPoint;
				
				//every 1 millisecond, retract the line by one step
				Timer timer = new Timer(1, new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						
						setDraggedPoint(new Point((int)(draggedPoint.getX() + dx), (int)(draggedPoint.getY() + dy)));
						
						//if the line is finished retracting, end the timer
						if ((int)(initialPoint.getX() + (15 * dx)) == draggedPoint.getX()) {
							((Timer)(e.getSource())).stop();
							setDraggedPoint(fixedPoint);
						}
					}
		
				});
				
				timer.setInitialDelay(0);
				timer.start();
				
			}
			
		}
		
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			
			//check if the mouse was clicked on, or near, the line's initial point
			if ((e.getPoint().distance(fixedPoint) <= 25) || (hasSnapped && e.getPoint().distance(draggedPoint) <= 30)) {
				isSelected = true;
			}
			else {
				isSelected = false;
			}
		}
	}
	
}


