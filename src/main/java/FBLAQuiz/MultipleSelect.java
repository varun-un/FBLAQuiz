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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * <h1>Multiple Select Question Class</h1>
 * 
 * The MultipleSelect class creates an object representing a multiple select question 
 * and implements methods for dealing with such.
 * 
 * @author Varun Unnithan
 *
 */
public class MultipleSelect extends MouseAdapter implements ActionListener{
	
	//----------------Instance variables----------------
	//Question variables
	/** The multiple select question as a JSONObject */
	private JSONObject questionObject;
	/** The text of the question */
	private String question;
	/** The List of all the question's answer choices */
	private ArrayList<String> choices; 
	/** The List of all correct answer choices */
	private ArrayList<String> answer;
	/** The List of the user's answers to the question */
	private ArrayList<String> userAnswer;
	/** Whether this question has been flagged for review or not */
	boolean flagged;
	/** The question number of this question in the quiz */
	private int quizQuestionNumber;
	
	//Buttons and scene objects
	/** A container for components to represent a question */
	private JPanel multipleSelectPanel, userPanel, questionPanel;
	/** A checkbox for one of the question's answer choices */
	private JCheckBox choiceA, choiceB, choiceC, choiceD, choiceE;
	/** The button to flag the question for review */
	private JToggleButton flagTool;
	/** The button to enable the selection of choices */
	private JToggleButton selectTool;
	/** The button to enable the elimination of certain choices */
	private JToggleButton eliminateTool;
	/** An array of all the multiple select buttons */
	private JCheckBox[] choiceButtons;
	/** The JLabel description of the toolbar's tools */
	private JLabel toolBarHelp;
	
	
	//------------------Constructors------------------
	/**
	* Default constructor for creating a random question
	* @throws IOException On input error
	* @throws ParseException On error while parsing the database
	* @throws FileNotFoundException On failure to find database file path
	*/
	public MultipleSelect() throws IOException, ParseException, FileNotFoundException{
		
		//read and store an array of all multiple select questions
		JSONObject database;
		try {
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/testQuestions.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
		}
		JSONArray multipleSelectArray = (JSONArray) database.get("Multiple Select");
		
		//picks random question from multiple select questions
		questionObject = (JSONObject) multipleSelectArray.get(new Random().nextInt(multipleSelectArray.size()));
		question = (String) questionObject.get("question");
		
		//converts JSONArray of choices to an ArrayList
		choices = new ArrayList<String>();
		JSONArray choicesJSON = (JSONArray) questionObject.get("choices");
		for (int i = 0; i < choicesJSON.size(); i++) {
			choices.add((String) choicesJSON.get(i));
		}
		Collections.shuffle(choices); //shuffles answer choices
		
		//converts JSONArray of answers to an ArrayList
		answer = new ArrayList<String>();
		JSONArray answersJSON = (JSONArray) questionObject.get("answer");
		for (int i = 0; i < answersJSON.size(); i++) {
			answer.add((String) answersJSON.get(i));
		}
		
		flagged = false;
		userAnswer = new ArrayList<String>();
	
	}
	
	
	//------------------Methods------------------
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
	 * Method to get the question answer
	 * @return An ArrayList of String objects, each being a correct answer
	 */
	public ArrayList<String> getAnswer() {
		return answer;
	}
	
	
	/**
	 * Method to get the user's inputted answer
	 * @return The user's answer as an ArrayList of Strings
	 */
	public ArrayList<String> getUserAnswer() {
		return userAnswer;
	}
	
	
	/**
	 * Updates the user answer, based off of the user input
	 */	
	private void updateUserInput() {
		
		//checks which multiple select button was clicked and records the user's input appropriately
		userAnswer.clear();
		for (JCheckBox multipleSelect : choiceButtons) {
					
			if (multipleSelect.isSelected()) {
				userAnswer.add(multipleSelect.getText());
			}
			else {
				userAnswer.remove(multipleSelect.getText());
			}
			
		}

	}
	
	
	/**
	 * Checks if the user has inputted an answer
	 * @return Boolean value of whether user has answered the question or not
	 */
	public boolean isAnswered() {
		return (userAnswer.size() != 0);
	}
	
	
	/**
	 * Checks if the user's choices is correct
	 * @return Boolean value of whether user was right
	 */
	public boolean isCorrect() {
		
		return userAnswer.containsAll(answer) && answer.containsAll(userAnswer);
	}
	
	
	/**
	 * Creates a JPanel which includes GUI components to add to a JFrame to ask a multiple select question
	 * @return JPanel object to be added to the JFrame to pose a multiple select question
	 * @param questionNumber The question's number within the quiz
	 * @param frame The JFrame upon which this JPanel will be added
	 */
	public JPanel createPanel(int questionNumber, JFrame frame) {
		
		//creates the JPanels for the user input, question, and the overall container
		userPanel = new UserPanel();
		questionPanel = new JPanel();
		multipleSelectPanel = new JPanel();
		
		//setting up layout managers for the panels
		SpringLayout panelLayout = new SpringLayout();		
		multipleSelectPanel.setLayout(panelLayout);	
		
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

		
		//create multiple select buttons
		choiceA = new JCheckBox();
		choiceB = new JCheckBox();
		choiceC = new JCheckBox();
		choiceD = new JCheckBox();
		choiceE = new JCheckBox();
		choiceButtons = new JCheckBox[]{choiceA, choiceB, choiceC, choiceD, choiceE};
		
		
		//sets up the four multiple select check box buttons by iterating through the array they are in	
		for (int i = 0; i < choiceButtons.length; i++) {
			//sets the text
			choiceButtons[i].setText(choices.get(i));
			choiceButtons[i].setFont(new Font("Tahoma", Font.PLAIN, 27));
			choiceButtons[i].setForeground(new Color(35,35,35));
			choiceButtons[i].setVerticalTextPosition(JCheckBox.CENTER);
			//sets up the button
			choiceButtons[i].setIcon(new ImageIcon("./Icons/CheckBox Icon Unselected.png"));
			choiceButtons[i].setSelectedIcon(new ImageIcon("./Icons/CheckBox Icon Selected.png"));
			choiceButtons[i].setDisabledIcon(new ImageIcon("./Icons/CheckBox Icon Disabled.png"));
			choiceButtons[i].setDisabledSelectedIcon(new ImageIcon("./Icons/CheckBox Icon Disabled.png"));
			choiceButtons[i].setRolloverIcon((new ImageIcon("./Icons/CheckBox Icon Rollover.png")));
			choiceButtons[i].setFocusable(false);
			choiceButtons[i].setContentAreaFilled(false);
			choiceButtons[i].setBorderPainted(false);
			choiceButtons[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			choiceButtons[i].addActionListener(this);
			choiceButtons[i].addMouseListener(this);
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
		JLabel instructions = new JLabel("Select ALL answer choices which apply");
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
		userPanel.add(choiceE);
		userPanel.add(toolBarHelp);
		userPanel.add(instructions);
		questionPanel.add(questionText);
		
		
		//setting the positions of the multiple select buttons
		userLayout.putConstraint(SpringLayout.SOUTH, choiceA, -27, SpringLayout.NORTH, choiceB);
		userLayout.putConstraint(SpringLayout.WEST, choiceA, 40, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, choiceB, -27, SpringLayout.NORTH, choiceC);
		userLayout.putConstraint(SpringLayout.WEST, choiceB, 40, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, choiceC, 0, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, choiceC, 40, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, choiceD, 27, SpringLayout.SOUTH, choiceC);
		userLayout.putConstraint(SpringLayout.WEST, choiceD, 40, SpringLayout.WEST, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, choiceE, 27, SpringLayout.SOUTH, choiceD);
		userLayout.putConstraint(SpringLayout.WEST, choiceE, 40, SpringLayout.WEST, userPanel);
		
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
		
	
		//adding the subpanels to the multiple select question panel container
		multipleSelectPanel.add(questionPanel);
		multipleSelectPanel.add(userPanel);
		
		//setting the position of the questionPanel and userPanel within the multipleSelectPanel
		panelLayout.putConstraint(SpringLayout.NORTH, questionPanel, 0, SpringLayout.NORTH, multipleSelectPanel);
		panelLayout.putConstraint(SpringLayout.EAST, questionPanel, 0, SpringLayout.EAST, multipleSelectPanel);
		panelLayout.putConstraint(SpringLayout.WEST, questionPanel, 0, SpringLayout.WEST, multipleSelectPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, questionPanel, (int)(-.125 * frame.getHeight()), SpringLayout.VERTICAL_CENTER, multipleSelectPanel);
		
		panelLayout.putConstraint(SpringLayout.NORTH, userPanel, 0, SpringLayout.SOUTH, questionPanel);
		panelLayout.putConstraint(SpringLayout.EAST, userPanel, 0, SpringLayout.EAST, multipleSelectPanel);
		panelLayout.putConstraint(SpringLayout.WEST, userPanel, 0, SpringLayout.WEST, multipleSelectPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, userPanel, 0, SpringLayout.SOUTH, multipleSelectPanel);
		
		
		return multipleSelectPanel;
	}

	
	//------------------Listeners------------------
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
		
		//changes the rollover icon of the choices when the eliminateTool is selected
		if (e.getSource() == eliminateTool) {
			for (JCheckBox checkbox: choiceButtons) {
				checkbox.setRolloverIcon(new ImageIcon("./Icons/CheckBox Icon Rollover Disabled.png"));
			}
		}
		
		//changes back the rollover icon of the choices when the selectTool is selected
		if (e.getSource() == selectTool) {
			for (JCheckBox checkbox: choiceButtons) {
				checkbox.setRolloverIcon(new ImageIcon("./Icons/CheckBox Icon Rollover.png"));
			}
		}
		
		updateUserInput();
		
	}


	/**
	 * {@inheritDoc}
	 */
	public void mousePressed(MouseEvent e) {
		
		//disables or enables a choice when the eliminateTool is selected
		if (eliminateTool.isSelected()) {
			
			//to iterate through all the multiple choice buttons
			for (JCheckBox multipleSelect : choiceButtons) {
				
				if (e.getComponent() == multipleSelect) {
					//enables or disables the choice based on whether or not it is currently enabled
					if (multipleSelect.isEnabled()) {
						multipleSelect.setSelected(false);
						multipleSelect.setEnabled(false);
						updateUserInput();
					}										
					else {
						multipleSelect.setEnabled(true);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
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
	public void mouseExited(MouseEvent e) {
		
		//when exiting a tool, hide the description
		if (e.getComponent() == flagTool || e.getComponent() == selectTool || e.getComponent() == eliminateTool) {
			toolBarHelp.setText("");
		}
	}
	
}
