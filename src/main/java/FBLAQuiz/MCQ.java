package FBLAQuiz;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * <h1>Multiple Choice Question Class</h1>
 * 
 * The MCQ class creates an object representing a multiple choice question 
 * and implements methods for dealing with such.
 * 
 * @author Varun Unnithan
 *
 */
public class MCQ extends MouseAdapter implements ActionListener{
	
	//----------------Instance variables----------------
	//Question variables
	/** The multiple choice question as a JSONObject */
	private JSONObject questionObject;
	/** The text of the question */
	private String question;
	/** The question's correct answer */
	private String answer;
	/** The user's answer to the question */
	private String userAnswer;
	/** A List of all the question's answer choices */
	private ArrayList<String> choices;
	/** Whether this question has been flagged for review or not */
	private boolean flagged;
	/** The question number of this question in the quiz */
	private int quizQuestionNumber;
	
	//Buttons and scene objects
	/** A container for components to represent a question */
	private JPanel mcqPanel, userPanel, questionPanel;
	/** A button for one of the question's answer choices */
	private JRadioButton choiceA, choiceB, choiceC, choiceD;
	/** The button to flag the question for review */
	private JToggleButton flagTool;
	/** The button to enable the selection of choices */
	private JToggleButton selectTool;
	/** The button to enable the elimination of certain choices */
	private JToggleButton eliminateTool;
	/** An array of all the multiple choice buttons */
	private JRadioButton[] choiceButtons;
	/** The JLabel description of the toolbar's tools */
	private JLabel toolBarHelp;
	/** The ButtonGroup of which the multiple choice buttons are a part of */
	private ButtonGroup choiceGroup;
	
	
	//-----------------------Constructor--------------
	/**
	* Default constructor for creating a multiple choice question, pulled from
	* the database
	* 
	* @throws IOException On input error
	* @throws ParseException On error while parsing the database
	* @throws FileNotFoundException On failure to find database file path
	*/
	public MCQ() throws FileNotFoundException, IOException, ParseException {
		
		//parse the database and save it to a JSONObject
		JSONObject database;
		try {
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/testQuestions.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
		}
		//get the array for all multiple choice questions from the database
		JSONArray mcqArray = (JSONArray) database.get("mcq");
		
		//picks random question from multiple choice questions
		questionObject = (JSONObject) mcqArray.get(new Random().nextInt(mcqArray.size()));
		
		//initializes variables using this question
		question = (String) questionObject.get("question");
		answer = (String) questionObject.get("answer");
		
		//converts JSONArray of choices to an ArrayList
		choices = new ArrayList<String>();
		JSONArray choicesJSON = (JSONArray) questionObject.get("choices");
		for (int i = 0; i < choicesJSON.size(); i++) {
			choices.add((String) choicesJSON.get(i));
		}
		Collections.shuffle(choices); //shuffles answer choices
		
		userAnswer = "";
		flagged = false;
		
	}
	
	
	
	//----------------------Methods-----------------
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
	 * Method to get the question choices
	 * @return An ArrayList of String objects, each being an answer choice
	 */
	public ArrayList<String> getChoices() {
		return choices;
	}
	
	
	/**
	 * Method to get the question's correct answer
	 * @return The answer as a String
	 */
	public String getAnswer() {
		return answer;
	}
	
	
	/**
	 * Method to get the user's inputted answer
	 * @return The user answer as a String
	 */
	public String getUserAnswer() {
		return userAnswer;
	}
	
	
	/**
	 * Updates the user answer, based off of the user input
	 */	
	private void updateUserInput() {
		
		//checks which multiple choice button was clicked and records the user's input appropriately
		for (JRadioButton multipleChoice : choiceButtons) {
					
			if (multipleChoice.isSelected()) {
				userAnswer = multipleChoice.getText();
				return;
			}
		}
		userAnswer = "";

	}
	
	
	/**
	 * Checks if the user has inputted an answer
	 * @return Boolean value of whether user has answered the question or not
	 */
	public boolean isAnswered() {
		return !userAnswer.equals("");
	}
	
	
	/**
	 * Checks if the user choice is correct
	 * @return Boolean value of whether user was right or not
	 */
	public boolean isCorrect() {
		return userAnswer.equals(answer);
	}
	
	
	/**
	 * Creates a JPanel which includes GUI components to add to a JFrame to ask a multiple choice question
	 * @return JPanel object to be added to the JFrame to pose a multiple choice question
	 * @param questionNumber The question's number within the quiz
	 * @param frame The JFrame upon which this JPanel will be added
	 */
	public JPanel createPanel(int questionNumber, JFrame frame) {
		
		//creates the JPanels for the user input, question, and the overall container
		userPanel = new UserPanel();
		questionPanel = new JPanel();
		mcqPanel = new JPanel();
		
		//setting up layout managers for the panels
		SpringLayout panelLayout = new SpringLayout();		
		mcqPanel.setLayout(panelLayout);	
		
		SpringLayout userLayout = new SpringLayout();		
		userPanel.setLayout(userLayout);
		
		SpringLayout questionLayout = new SpringLayout();		
		questionPanel.setLayout(questionLayout);
		questionPanel.setBackground(new Color(25,25,25));
		
		
		//creating and adding the toolbar buttons
		flagTool = new JToggleButton();
		selectTool = new JToggleButton();
		eliminateTool = new JToggleButton();
		
		//sets up icon for flagTool button
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
		
		//sets up icon for selectTool button
		selectTool.setIcon(new ImageIcon("./Icons/Select Icon Unselected.png"));
		selectTool.setSelectedIcon(new ImageIcon("./Icons/Select Icon Selected.png"));
		selectTool.setRolloverIcon(new ImageIcon("./Icons/Select Icon Rollover.png"));
		selectTool.setOpaque(false);
		selectTool.setContentAreaFilled(false);
		selectTool.setBorderPainted(false);
		selectTool.setFocusable(false);
		selectTool.setSelected(true);
		selectTool.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		selectTool.addActionListener(this);
		selectTool.addMouseListener(this);
		
		//sets up icon for eliminateTool button
		eliminateTool.setIcon(new ImageIcon("./Icons/Eliminate Icon Unselected.png"));
		eliminateTool.setSelectedIcon(new ImageIcon("./Icons/Eliminate Icon Selected.png"));
		eliminateTool.setRolloverIcon(new ImageIcon("./Icons/Eliminate Icon Rollover.png"));
		eliminateTool.setOpaque(false);
		eliminateTool.setContentAreaFilled(false);
		eliminateTool.setBorderPainted(false);
		eliminateTool.setFocusable(false);
		eliminateTool.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		eliminateTool.addActionListener(this);
		eliminateTool.addMouseListener(this);
		
		//creates a ButtonGroup so either the selectTool or eliminateTool is always selected
		ButtonGroup selectionGroup = new ButtonGroup();
		selectionGroup.add(selectTool);
		selectionGroup.add(eliminateTool);
		
		
		//create multiple choice buttons
		choiceA = new JRadioButton();
		choiceB = new JRadioButton();
		choiceC = new JRadioButton();
		choiceD = new JRadioButton();
		choiceButtons = new JRadioButton[]{choiceA, choiceB, choiceC, choiceD};
		
		//creates a ButtonGroup for the choice buttons
		choiceGroup = new ButtonGroup();
		
		//sets up the four multiple choice radio buttons by iterating through the array they are in	
		for (int i = 0; i < choiceButtons.length; i++) {
			//sets the text
			choiceButtons[i].setText(choices.get(i));
			choiceButtons[i].setFont(new Font("Tahoma", Font.PLAIN, 27));
			choiceButtons[i].setForeground(new Color(35,35,35));
			choiceButtons[i].setVerticalTextPosition(JRadioButton.CENTER);
			//sets up the button
			choiceButtons[i].setIcon(new ImageIcon("./Icons/RadioButton Icon Unselected.png"));
			choiceButtons[i].setSelectedIcon(new ImageIcon("./Icons/RadioButton Icon Selected.png"));
			choiceButtons[i].setDisabledIcon(new ImageIcon("./Icons/RadioButton Icon Disabled.png"));
			choiceButtons[i].setDisabledSelectedIcon(new ImageIcon("./Icons/RadioButton Icon Disabled.png"));
			choiceButtons[i].setRolloverIcon((new ImageIcon("./Icons/RadioButton Icon Rollover.png")));
			choiceButtons[i].setFocusable(false);
			choiceButtons[i].setContentAreaFilled(false);
			choiceButtons[i].setBorderPainted(false);
			choiceButtons[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			choiceButtons[i].addActionListener(this);
			choiceButtons[i].addMouseListener(this);
			choiceGroup.add(choiceButtons[i]);
		}
		
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
		
		
		//creates the instruction text
		JLabel instructions = new JLabel("Select the correct answer choice");
		instructions.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		instructions.setForeground(Color.LIGHT_GRAY);
		
		
		//adding all the components to their respective panels
		userPanel.add(flagTool);
		userPanel.add(selectTool);
		userPanel.add(eliminateTool);
		userPanel.add(choiceA);
		userPanel.add(choiceB);
		userPanel.add(choiceC);
		userPanel.add(choiceD);
		userPanel.add(toolBarHelp);
		userPanel.add(instructions);
		questionPanel.add(questionText);
		
		
		//setting the positions of the multiple choice buttons
		userLayout.putConstraint(SpringLayout.SOUTH, choiceA, -40, SpringLayout.NORTH, choiceB);
		userLayout.putConstraint(SpringLayout.WEST, choiceA, 40, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, choiceB, -5, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, choiceB, 40, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, choiceC, 35, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, choiceC, 40, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, choiceD, 40, SpringLayout.SOUTH, choiceC);
		userLayout.putConstraint(SpringLayout.WEST, choiceD, 40, SpringLayout.WEST, userPanel);
		
		//setting the positions of the toolbar buttons and their description
		userLayout.putConstraint(SpringLayout.EAST, flagTool, 5, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, flagTool, -75, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, flagTool, -10, SpringLayout.NORTH, selectTool);
		
		userLayout.putConstraint(SpringLayout.EAST, selectTool, 5, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, selectTool, -75, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, selectTool, 10, SpringLayout.VERTICAL_CENTER, userPanel);
		
		userLayout.putConstraint(SpringLayout.EAST, eliminateTool, 5, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, eliminateTool, -75, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, eliminateTool, 10, SpringLayout.SOUTH, selectTool);
		
		userLayout.putConstraint(SpringLayout.EAST, toolBarHelp, -50, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, toolBarHelp, -5, SpringLayout.SOUTH, userPanel);
		
		//set the position of the instructions
		userLayout.putConstraint(SpringLayout.NORTH, instructions, 30, SpringLayout.NORTH, userPanel);
		userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructions, 0, SpringLayout.HORIZONTAL_CENTER, userPanel);
		
		
		//setting the position of the question
		questionLayout.putConstraint(SpringLayout.EAST, questionText, -25, SpringLayout.EAST, questionPanel);
		questionLayout.putConstraint(SpringLayout.WEST, questionText, 25, SpringLayout.WEST, questionPanel);
		questionLayout.putConstraint(SpringLayout.VERTICAL_CENTER, questionText, 0, SpringLayout.VERTICAL_CENTER, questionPanel);
		
	
		//adding the subpanels to the multiple choice question panel container
		mcqPanel.add(questionPanel);
		mcqPanel.add(userPanel);
		
		//setting the position of the questionPanel and userPanel within the mcqPanel
		panelLayout.putConstraint(SpringLayout.NORTH, questionPanel, 0, SpringLayout.NORTH, mcqPanel);
		panelLayout.putConstraint(SpringLayout.EAST, questionPanel, 0, SpringLayout.EAST, mcqPanel);
		panelLayout.putConstraint(SpringLayout.WEST, questionPanel, 0, SpringLayout.WEST, mcqPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, questionPanel, (int)(-.125 * frame.getHeight()), SpringLayout.VERTICAL_CENTER, mcqPanel);
		
		panelLayout.putConstraint(SpringLayout.NORTH, userPanel, 0, SpringLayout.SOUTH, questionPanel);
		panelLayout.putConstraint(SpringLayout.EAST, userPanel, 0, SpringLayout.EAST, mcqPanel);
		panelLayout.putConstraint(SpringLayout.WEST, userPanel, 0, SpringLayout.WEST, mcqPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, userPanel, 0, SpringLayout.SOUTH, mcqPanel);
		
		
		return mcqPanel;
	}

	
	
	//-----------------Listeners---------------
	/**
	 * {@inheritDoc}
	 */
	@Override
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
		
		//changes the rollover icon of the choices when the eliminateTool is selected
		if (e.getSource() == eliminateTool) {
			choiceA.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover Disabled.png"));
			choiceB.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover Disabled.png"));
			choiceC.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover Disabled.png"));
			choiceD.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover Disabled.png"));
		}
		
		//changes back the rollover icon of the choices when the selectTool is selected
		if (e.getSource() == selectTool) {
			choiceA.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover.png"));
			choiceB.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover.png"));
			choiceC.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover.png"));
			choiceD.setRolloverIcon(new ImageIcon("./Icons/RadioButton Icon Rollover.png"));
		}
		
		updateUserInput();
		
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		
		//disables or enables a choice when the eliminateTool is selected
		if (eliminateTool.isSelected()) {
			
			//to iterate through all the multiple choice buttons
			for (JRadioButton multipleChoice : choiceButtons) {
				
				if (e.getComponent() == multipleChoice) {
					
					//enables or disables the choice based on whether or not it is currently enabled
					if (multipleChoice.isEnabled()) {
						
						if (multipleChoice.isSelected()) {
							choiceGroup.clearSelection();
						}
						multipleChoice.setEnabled(false);
						updateUserInput();
					}		
					
					else {
						multipleChoice.setEnabled(true);
					}
				}
			}
		}
		
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		
		//when hovering over a tool, display their description
		if (e.getComponent() == flagTool) {
			toolBarHelp.setText("Flag this question for later review");
		}
		if (e.getComponent() == selectTool) {
			toolBarHelp.setText("Used to select the answer");
		}
		if (e.getComponent() == eliminateTool) {
			toolBarHelp.setText("Used to eliminate certain choices");
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		
		//when exiting a tool, hide the description
		if (e.getComponent() == flagTool || e.getComponent() == selectTool || e.getComponent() == eliminateTool) {
			toolBarHelp.setText("");
		}
	}
	
}
