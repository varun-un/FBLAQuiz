package FBLAQuiz;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * <h1>Matching Question Class</h1>
 * 
 * The Matching class creates an object representing a matching question 
 * and implements methods for dealing with such.
 * 
 * @author Varun Unnithan
 *
 */
public class Matching extends MouseAdapter implements ActionListener{
		
	//----------------Instance variables----------------
	//Question variables
	/** The true or false question as a JSONObject */
	private JSONObject questionObject;
	/** The text of the question */
	private String question;
	/** The first group of matching choices */
	private ArrayList<String> groupA;
	/** The second group of matching choices, with the correct matches having corresponding indices to groupA. 
	 * Must have the same size as groupA */
	private ArrayList<String> groupB;
	/** The user's answer to the question, with the chosen matches having corresponding indices to groupA. 
	 * Must have the same size as groupA
	 * All unanswered choices are set as null. */
	private ArrayList<String> userAnswerGroupB;
	/** Whether this question has been flagged for review or not */
	private boolean flagged;
	/** The question number of this question in the quiz */
	private int quizQuestionNumber;
		
	//Components and scene objects
	/** A container for components to represent a question */
	private JPanel matchingPanel, userPanel, questionPanel;
	/** A draggable line to match together items */
	private DraggableLine line1, line2, line3;
	/** Whether or not to check for the mouse's position to update the DraggableLine objects */
	private boolean checkMouse;
	/** The button to flag the question for review */
	private JToggleButton flagTool;
	/** The JLabel description of the toolbar's tools */
	private JLabel toolBarHelp;
	
	/** An ArrayList of all DraggableLine objects */
	private ArrayList<DraggableLine> connectingLines;
	/** An ArrayList of all the groupA matching boxes */
	private ArrayList<JLabel> groupAItems;
	/** An ArrayList of all the groupB matching boxes */
	private ArrayList<JLabel> groupBItems;
	
	
	
	
	//---------------Constructors-----------------
	/**
	* Default constructor for creating a random question
	* @throws IOException On input error
	* @throws ParseException On error while parsing the database
	* @throws FileNotFoundException On failure to find database file path
	*/
	public Matching() throws IOException, ParseException, FileNotFoundException{
		
		//read and store an array of all multiple choice questions
		JSONObject database;
		try {
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/testQuestions.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
		}
		JSONArray fillInArray = (JSONArray) database.get("Matching");
		
		//picks random question from multiple choice questions
		questionObject = (JSONObject) fillInArray.get(new Random().nextInt(fillInArray.size()));
		question = (String) questionObject.get("question");
		
		//converts JSONArray of groupA to an ArrayList
		groupA = new ArrayList<String>();
		JSONArray JSONgroupA = (JSONArray) questionObject.get("groupA");
		for (int i = 0; i < JSONgroupA.size(); i++) {
			groupA.add((String) JSONgroupA.get(i));
		}
		
		//converts JSONArray of groupB to an ArrayList
		groupB = new ArrayList<String>();
		JSONArray JSONgroupB = (JSONArray) questionObject.get("groupB");
		for (int i = 0; i < JSONgroupB.size(); i++) {
			groupB.add((String) JSONgroupB.get(i));
		}
		
		userAnswerGroupB = new ArrayList<String>();
		flagged = false;
	
	}
	
	
	
	//---------------Methods-------------------
	/**
	 * Method to get the question string
	 * @return The question as a String
	 */
	public String getQuestion() {
		return question;
	}
	
	
	/**
	 * Method to see if a question is flagged
	 * @return Boolean of if a question is flagged
	 */
	public boolean isFlagged() {
		return flagged;
	}
	
	
	/**
	 * Mathod to get this question's number
	 * @return The question number of this question in the quiz
	 */
	public int getQuizQuestionNumber() {
		return quizQuestionNumber;
	}
	
	
	/**
	 * Method to get the first group of items to match
	 * @return An ArrayList of strings, each representing an item to match
	 */
	public ArrayList<String> getGroupA() {
		return groupA;
	}
	
	
	/**
	 * Method to get the second group of items to match
	 * @return An ArrayList of strings, each representing an item to match
	 */
	public ArrayList<String> getGroupB() {
		return groupB;
	}
	
	
	/**
	 * Method to get the user's input for the matching order for groupB
	 * @return An ArrayList of strings, with the indices corresponding to the items of groupA
	 */
	public ArrayList<String> getUserAnswer() {
		updateUserInput();
		return userAnswerGroupB;
	}
	
	/**
	 * Stops the checking of the mouse position to update the DraggableLines
	 */
	public void stopMouseChecking() {
		checkMouse = false;
	}
	
	
	/**
	 * Updates the userAnswerGroupB ArrayList, based off of the user input
	 */	
	private void updateUserInput() {
		
		ArrayList<Point> destinations = line1.getDestinations();
		
		//sort the destinations ArrayList by the Point's y-value, descending
		Collections.sort(destinations, new Comparator<Point>() {

			public int compare(Point o1, Point o2) {
				//return 1 if o1 is higher than o2, -1 if o2 is higher, and 0 if they're equal
				return o1.getY() > o2.getY() ? 1 : (o1.getY() < o2.getY()) ? -1 : 0;
			}
		});
		
		
		ArrayList<String> userInput = new ArrayList<String>();
		
		//iterate through each DraggableLine and get its status
		for (int i = 0; i < connectingLines.size(); i++) {
			
			//if the DraggableLine is snapped, add the text to the ArrayList, else set that position to null
			if (connectingLines.get(i).isSnapped()) {
				
				//gets the text of the DraggableLine's connected JLabel
				String thisAnswer = groupBItems.get(destinations.indexOf((connectingLines.get(i).getDraggedPoint()))).getText();
				//remove the HTML tags from the String
				userInput.add(thisAnswer.substring(24, thisAnswer.length() - 13));
			}
			else {
				userInput.add(null);
			}
		}

		//update the userAnswerGroupB list
		userAnswerGroupB = userInput;

	}
	
	
	/**
	 * Checks if the user has inputted an answer
	 * @return Boolean value of whether user has answered the question or not
	 */
	public boolean isAnswered() {
		updateUserInput();
		
		//checks if all the items in the user answer array is null
		for (int i = 0; i < userAnswerGroupB.size(); i++) {
			if (userAnswerGroupB.get(i) != null) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Checks if the user's answer is correct
	 * @return Boolean value of whether user is correct or not
	 */
	public boolean isCorrect() {
		updateUserInput();
		return userAnswerGroupB.equals(groupB);
	}

	
	
	/**
	 * Creates a JPanel which includes GUI components to add to a JFrame to ask a matching question
	 * @return JPanel object to be added to the JFrame to pose a matching question
	 * @param questionNumber The question's number within the quiz
	 * @param frame The JFrame upon which this JPanel will be added
	 */
	public JPanel createPanel(int questionNumber, final JFrame frame) {
		
		//creates the JPanels for the user input, question, and the overall container
		userPanel = new UserPanel();
		questionPanel = new JPanel();
		matchingPanel = new JPanel();
		
		//setting up layout managers for the panels
		SpringLayout panelLayout = new SpringLayout();		
		matchingPanel.setLayout(panelLayout);	
		
		SpringLayout userLayout = new SpringLayout();		
		userPanel.setLayout(userLayout);
		
		SpringLayout questionLayout = new SpringLayout();		
		questionPanel.setLayout(questionLayout);
		questionPanel.setBackground(new Color(25,25,25));
		
		
		//sets up icon for flagTool button
		flagTool = new JToggleButton();
		
		flagTool.setIcon(new ImageIcon("./Icons/Flag Icon Unselected.png"));
		flagTool.setSelectedIcon(new ImageIcon("./Icons/Flag Icon Selected.png"));
		flagTool.setRolloverIcon(new ImageIcon("./Icons/Flag Icon Rollover.png"));
		flagTool.setOpaque(false);
		flagTool.setContentAreaFilled(false);
		flagTool.setBorderPainted(false);
		flagTool.setFocusable(false);
		flagTool.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		flagTool.addActionListener(this);
		flagTool.addMouseListener(this);
		
		
		//create a JLabel to display the question
		quizQuestionNumber = questionNumber;
		JLabel questionText = new JLabel();
		questionText.setText("<html>" + quizQuestionNumber + ") " + question + "</html>");	//html tags for text wrapping
		questionText.setFont(new Font("Trebuchet MS", Font.BOLD, 35));
		questionText.setForeground(Color.lightGray);
	
		//sets up the toolbar dscription JLabel
		toolBarHelp = new JLabel();
		toolBarHelp.setFont(new Font("Consolas", Font.PLAIN, 16));
		toolBarHelp.setForeground(Color.lightGray);
		
		
		//declares and initializes the JLabel items and sets them up into an Array
		JLabel groupA_1 = new JLabel(), groupA_2 = new JLabel(), groupA_3 = new JLabel();
		JLabel groupB_1 = new JLabel(), groupB_2 = new JLabel(), groupB_3 = new JLabel();
		groupAItems = new ArrayList<JLabel>(Arrays.asList(groupA_1, groupA_2, groupA_3));
		groupBItems = new ArrayList<JLabel>(Arrays.asList(groupB_1, groupB_2, groupB_3));
		
		//creates border for the matching boxes
		Border raisedBevelBorder = BorderFactory.createRaisedSoftBevelBorder();
		Border loweredBevelBorder = BorderFactory.createLoweredSoftBevelBorder();
		Border boxBorder = BorderFactory.createCompoundBorder(raisedBevelBorder, loweredBevelBorder);
		
		//sets up groupA JLabels
		for (int i = 0; i < groupAItems.size(); i++) {
			groupAItems.get(i).setText("<html><div align=center>" + groupA.get(i) + "</div></html>");
			groupAItems.get(i).setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
			groupAItems.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			groupAItems.get(i).setBackground(new Color(25,25,25));
			groupAItems.get(i).setForeground(Color.LIGHT_GRAY);
			groupAItems.get(i).setOpaque(true);
			groupAItems.get(i).setBorder(boxBorder);
		}
		
		//shuffled the groupB order
		ArrayList<String> shuffledGroupB = new ArrayList<String>(groupB);
		Collections.shuffle(shuffledGroupB);
		
		//sets up groupB Jlabels
		for (int i = 0; i < groupBItems.size(); i++) {
			groupBItems.get(i).setText("<html><div align=center>" + shuffledGroupB.get(i) + "</div></html>");
			groupBItems.get(i).setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
			groupBItems.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			groupBItems.get(i).setBackground(new Color(25,25,25));
			groupBItems.get(i).setForeground(Color.LIGHT_GRAY);
			groupBItems.get(i).setOpaque(true);
			groupBItems.get(i).setBorder(boxBorder);
		}
		
		
		//intializes all DraggableLine objects and adds them to an ArrayList
		line1 = new DraggableLine();
		line2 = new DraggableLine();
		line3 = new DraggableLine();
		connectingLines = new ArrayList<DraggableLine>();
		connectingLines.add(line1);
		connectingLines.add(line2);
		connectingLines.add(line3);
		
		//sets up the connecting DraggableLine objects
		final Runnable setUpLines = new Runnable() {
			
			public void run() {
				//pauses the Thread to wait for the layout manager to set up the component's positions
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for (int a = 0; a < groupAItems.size(); a++) {
					//gets the location of the middle right of the groupA boxes
					Point pivot = groupAItems.get(a).getLocation();
					pivot.translate(userPanel.getWidth() / 2 - 200, 30);
					
					//sets up the DraggableLine objects
					connectingLines.get(a).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					connectingLines.get(a).setFixedPoint(pivot);
					connectingLines.get(a).setColor(new Color(25,25,25));
					connectingLines.get(a).setThickness(10f);
					
					//adds each of the groupB boxes as a destination
					for (int b = 0; b < groupBItems.size(); b++) {
						//gets the location of the middle left of the groupB boxes
						Point destination = groupBItems.get(a).getLocation();
						destination.translate(0, 30);
						
						connectingLines.get(b).addDestination(destination);
					}
				}
				
				
			}
		};
		
		//creates a separate Thread to delay the DraggableLine set up until the rest of the components have been set up
		new Thread(setUpLines);
		
		//everytime the frame is resized, recalculate the positions of the DraggableLine objects
		userPanel.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		    	
		    	//re-set up the lines with its new positions
		    	Thread resizePanel = new Thread(setUpLines);
		    	resizePanel.start();
		    }
		});
		
		
		//creates the DraggableLine's pivot dots
		JLabel dotA1 = new JLabel(); 
		JLabel dotA2 = new JLabel(); 
		JLabel dotA3 = new JLabel();
		JLabel[] linePivotDots = {dotA1, dotA2, dotA3};
		for (int i = 0; i < linePivotDots.length; i++) {
			linePivotDots[i].setIcon(new ImageIcon("./Icons/Line Pivot Dot.png"));
			linePivotDots[i].setOpaque(false);
			linePivotDots[i].setBorder(null);
		}
		
		//creates the DraggableLine's destinations' dots
		JLabel dotB1 = new JLabel(); 
		JLabel dotB2 = new JLabel(); 
		JLabel dotB3 = new JLabel();
		JLabel[] lineDestinationDots = {dotB1, dotB2, dotB3};
		for (int i = 0; i < lineDestinationDots.length; i++) {
			lineDestinationDots[i].setIcon(new ImageIcon("./Icons/Line Destination Dot.png"));
			lineDestinationDots[i].setOpaque(false);
			lineDestinationDots[i].setBorder(null);
		}
		
		//creates the instruction text
		JLabel instructions = new JLabel("Drag the dots to connect the left-hand side boxes to the right-hand side boxes");
		instructions.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		instructions.setForeground(Color.LIGHT_GRAY);
		
		//adding all the components and subpanels to their respective panels
		userPanel.add(flagTool);
		userPanel.add(toolBarHelp);
		userPanel.add(groupA_1);
		userPanel.add(groupA_2);
		userPanel.add(groupA_3);
		userPanel.add(groupB_1);
		userPanel.add(groupB_2);
		userPanel.add(groupB_3);
		userPanel.add(line1);
		userPanel.add(line2);
		userPanel.add(line3);
		userPanel.add(dotB1);
		userPanel.add(dotB2);
		userPanel.add(dotB3);
		userPanel.add(dotA1);
		userPanel.add(dotA2);
		userPanel.add(dotA3);
		userPanel.add(instructions);
		questionPanel.add(questionText);
		
		
		//setting the positions of the flag tool and its description
		userLayout.putConstraint(SpringLayout.EAST, flagTool, 5, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, flagTool, -75, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, flagTool, 0, SpringLayout.VERTICAL_CENTER, userPanel);
		
		userLayout.putConstraint(SpringLayout.EAST, toolBarHelp, -50, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, toolBarHelp, -5, SpringLayout.SOUTH, userPanel);
		
		
		//setting the position of the groupA JLabel boxes
		userLayout.putConstraint(SpringLayout.EAST, groupA_1, -100, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, groupA_1, 100, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, groupA_1, -20, SpringLayout.NORTH, groupA_2);
		userLayout.putConstraint(SpringLayout.NORTH, groupA_1, -90, SpringLayout.SOUTH, groupA_1);
		
		userLayout.putConstraint(SpringLayout.EAST, groupA_2, -100, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, groupA_2, 100, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, groupA_2, 60, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, groupA_2, -30, SpringLayout.VERTICAL_CENTER, userPanel);
		
		userLayout.putConstraint(SpringLayout.EAST, groupA_3, -100, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, groupA_3, 100, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, groupA_3, 20, SpringLayout.SOUTH, groupA_2);
		userLayout.putConstraint(SpringLayout.SOUTH, groupA_3, 90, SpringLayout.NORTH, groupA_3);
		
		
		//setting the position of the groupB JLabel boxes
		userLayout.putConstraint(SpringLayout.WEST, groupB_1, 100, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.EAST, groupB_1, -100, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, groupB_1, -20, SpringLayout.NORTH, groupB_2);
		userLayout.putConstraint(SpringLayout.NORTH, groupB_1, -90, SpringLayout.SOUTH, groupB_1);
		
		userLayout.putConstraint(SpringLayout.WEST, groupB_2, 100, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.EAST, groupB_2, -100, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, groupB_2, 60, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, groupB_2, -30, SpringLayout.VERTICAL_CENTER, userPanel);
				
		userLayout.putConstraint(SpringLayout.WEST, groupB_3, 100, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.EAST, groupB_3, -100, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, groupB_3, 20, SpringLayout.SOUTH, groupB_2);
		userLayout.putConstraint(SpringLayout.SOUTH, groupB_3, 90, SpringLayout.NORTH, groupB_3);
		
		
		//setting the constraints of the DraggableLine objects
		for (DraggableLine line: connectingLines) {
			userLayout.putConstraint(SpringLayout.WEST, line, 0, SpringLayout.WEST, userPanel);
			userLayout.putConstraint(SpringLayout.EAST, line, 0, SpringLayout.EAST, userPanel);
			userLayout.putConstraint(SpringLayout.SOUTH, line, 0, SpringLayout.SOUTH, userPanel);
			userLayout.putConstraint(SpringLayout.NORTH, line, 16, SpringLayout.NORTH, userPanel);
		}
		
		
		//setting the position of the destination's dots
		for (int i = 0; i < groupAItems.size(); i++) {
			userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, linePivotDots[i], 0, SpringLayout.EAST, groupAItems.get(i));
			userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, linePivotDots[i], 1, SpringLayout.VERTICAL_CENTER, groupAItems.get(i));
		}
		
		//setting the position of the destination's dots
		for (int i = 0; i < groupBItems.size(); i++) {
			userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lineDestinationDots[i], 0, SpringLayout.WEST, groupBItems.get(i));
			userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, lineDestinationDots[i], 1, SpringLayout.VERTICAL_CENTER, groupBItems.get(i));
		}
		
		
		//set the position of the instructions
		userLayout.putConstraint(SpringLayout.NORTH, instructions, 30, SpringLayout.NORTH, userPanel);
		userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructions, 0, SpringLayout.HORIZONTAL_CENTER, userPanel);
		
		
		//setting the position of the question
		questionLayout.putConstraint(SpringLayout.EAST, questionText, -25, SpringLayout.EAST, questionPanel);
		questionLayout.putConstraint(SpringLayout.WEST, questionText, 25, SpringLayout.WEST, questionPanel);
		questionLayout.putConstraint(SpringLayout.VERTICAL_CENTER, questionText, 0, SpringLayout.VERTICAL_CENTER, questionPanel);
		
		
		//adding the subpanels to the multiple choice question panel container
		matchingPanel.add(questionPanel);
		matchingPanel.add(userPanel);
		
		//setting the position of the questionPanel and userPanel within the mcqPanel
		panelLayout.putConstraint(SpringLayout.NORTH, questionPanel, 0, SpringLayout.NORTH, matchingPanel);
		panelLayout.putConstraint(SpringLayout.EAST, questionPanel, 0, SpringLayout.EAST, matchingPanel);
		panelLayout.putConstraint(SpringLayout.WEST, questionPanel, 0, SpringLayout.WEST, matchingPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, questionPanel, (int)(-.125 * frame.getHeight()), SpringLayout.VERTICAL_CENTER, matchingPanel);
		
		panelLayout.putConstraint(SpringLayout.NORTH, userPanel, 0, SpringLayout.SOUTH, questionPanel);
		panelLayout.putConstraint(SpringLayout.EAST, userPanel, 0, SpringLayout.EAST, matchingPanel);
		panelLayout.putConstraint(SpringLayout.WEST, userPanel, 0, SpringLayout.WEST, matchingPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, userPanel, 0, SpringLayout.SOUTH, matchingPanel);
	
		
		
		//Runnable object to constantly check which DraggableLine is to be clicked
		Runnable getMouseNearLine = new Runnable() {
			public void run() {
				
				try {
					Thread.sleep(200);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				checkMouse = true;
					   
				//while the checkMouse is enabled, check for which line the mouse is near
			    while (checkMouse) {
			    	
			    	//get the mouse position relative to userPanel
					Point mousePos = MouseInfo.getPointerInfo().getLocation();
					SwingUtilities.convertPointFromScreen(mousePos, userPanel);
					    	
					//if the mouse is near a DraggableLine, set that line's Z order to be the highest
					if ((mousePos.distance(line1.getFixedPoint()) <= 25) || (mousePos.distance(line1.getDraggedPoint()) <= 20)) {
					    userPanel.setComponentZOrder(line1, 8);
					    userPanel.setComponentZOrder(line2, 9);
					    userPanel.setComponentZOrder(line3, 10);
					}
					
					else if ((mousePos.distance(line2.getFixedPoint()) <= 25) || (mousePos.distance(line2.getDraggedPoint()) <= 20)) {
						userPanel.setComponentZOrder(line2, 8);
						userPanel.setComponentZOrder(line1, 9);
						userPanel.setComponentZOrder(line3, 10);
					}
					
					else if ((mousePos.distance(line3.getFixedPoint()) <= 25) || (mousePos.distance(line3.getDraggedPoint()) <= 20)) {
					  	userPanel.setComponentZOrder(line3, 8);
					 	userPanel.setComponentZOrder(line1, 9);
					 	userPanel.setComponentZOrder(line2, 10);
					}
			  	}
			}
		};
			    
		//create a new Thread to run the mouse detection
		Thread mouseDetector = new Thread(getMouseNearLine);
		mouseDetector.start();
		
		
		return matchingPanel;
		
	}
	
	
	
	//----------------Listeners---------------
	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e) {
		
		//sets boolean variable flagged when the flagTool button is clicked
		if (e.getSource() == flagTool) {
			if(flagTool.isSelected()) {
				flagged = true;
			}
			else {
				flagged = false;
			}
		}
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void mouseEntered(MouseEvent e) {
		
		//when hovering over the flag tool, display its description
		if (e.getComponent() == flagTool) {
			toolBarHelp.setText("Flag this question for later review");
		}
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void mouseExited(MouseEvent e) {
		
		//when exiting the tool, hide the description
		if (e.getComponent() == flagTool) {
			toolBarHelp.setText("");
		}
	}

	
	
}
