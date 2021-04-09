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
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * <h1>True or False Question Class</h1>
 * 
 * The TrueOrFalse class creates an object representing a true or false question 
 * and implements methods for dealing with such.
 * 
 * @author Varun Unnithan
 *
 */
public class TrueOrFalse extends MouseAdapter implements ActionListener{
		
	//----------------Instance variables----------------
	//Question variables
	/** The true or false question as a JSONObject */
	private JSONObject questionObject;
	/** The text of the question */
	private String question;
	/** The question's correct answer */
	private String answer;
	/** The user's answer to the question */
	private String userAnswer;
	/** Whether this question has been flagged for review or not */
	private boolean flagged;
	/** The question number of this question in the quiz */
	private int quizQuestionNumber;
		
	//Buttons and scene objects
	/** A container for components to represent a question */
	private JPanel trueFalsePanel, userPanel, questionPanel;
	/** The button to flag the question for review */
	private JToggleButton flagTool;
	/** The button to represent the answer being true */
	private JToggleButton trueButton;
	/** The button to represent the answer being false */
	private JToggleButton falseButton;
	/** The JLabel description of the toolbar's tools */
	private JLabel toolBarHelp;
	
	
	//------------------Constructors------------------
	/**
	* Default constructor for creating a random question
	* @throws IOException On input error
	* @throws ParseException On error while parsing the database
	* @throws FileNotFoundException On failure to find database file path
	*/
	public TrueOrFalse() throws IOException, ParseException, FileNotFoundException{
		
		//read and store an array of all multiple choice questions
		JSONObject database;
		try {
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/testQuestions.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
		}
		JSONArray fillInArray = (JSONArray) database.get("True or False");
		
		//picks random question from multiple choice questions
		questionObject = (JSONObject) fillInArray.get(new Random().nextInt(fillInArray.size()));
		question = (String) questionObject.get("question");
		answer = (String) questionObject.get("answer");
		flagged = false;
		
		userAnswer = "";
	
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
	 * Method to get the question answer
	 * @return The answer as a String
	 */
	public String getAnswer() {
		return answer;
	}
	
	
	/**
	 * Method to get the user's inputted answer
	 * @return The user's answer as a String
	 */
	public String getUserAnswer() {
		return userAnswer;
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
	 * Creates a JPanel which includes GUI components to add to a JFrame to ask a true or false question
	 * @return JPanel object to be added to the JFrame to pose a true or false question
	 * @param questionNumber The question's number within the quiz
	 * @param frame The JFrame upon which this JPanel will be added
	 */
	public JPanel createPanel(int questionNumber, JFrame frame) {
		
		//creates the JPanels for the user input, question, and the overall container
		userPanel = new UserPanel();
		questionPanel = new JPanel();
		trueFalsePanel = new JPanel();
		
		//setting up layout managers for the panels
		SpringLayout panelLayout = new SpringLayout();		
		trueFalsePanel.setLayout(panelLayout);	
		
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
		
		//sets up the true button
		trueButton = new JToggleButton();
		trueButton.setIcon(new ImageIcon("./Icons/True.png"));
		trueButton.setSelectedIcon(new ImageIcon("./Icons/True Selected.png"));
		trueButton.setRolloverIcon(new ImageIcon("./Icons/True Rollover.png"));
		trueButton.setOpaque(false);
		trueButton.setContentAreaFilled(false);
		trueButton.setBorderPainted(false);
		trueButton.setFocusable(false);
		trueButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		trueButton.addActionListener(this);
		
		//sets up the false button
		falseButton = new JToggleButton();
		falseButton.setIcon(new ImageIcon("./Icons/False.png"));
		falseButton.setSelectedIcon(new ImageIcon("./Icons/False Selected.png"));
		falseButton.setRolloverIcon(new ImageIcon("./Icons/False Rollover.png"));
		falseButton.setOpaque(false);
		falseButton.setContentAreaFilled(false);
		falseButton.setBorderPainted(false);
		falseButton.setFocusable(false);
		falseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		falseButton.addActionListener(this);
		
		//creates a ButtonGroup so either the ture or false button is always selected
		ButtonGroup trueOrFalseGroup = new ButtonGroup();
		trueOrFalseGroup.add(trueButton);
		trueOrFalseGroup.add(falseButton);
		
		//creates a dividing line for the true and false buttons
		JLabel separator = new JLabel();
		separator.setBorder(new LineBorder(new Color(25,25,25), 2, false));
		
		//creates the instruction text
		JLabel instructions = new JLabel("Select whether the above statement is true or false");
		instructions.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		instructions.setForeground(Color.LIGHT_GRAY);
		
		
		//adding all the components and subpanels to their respective panels
		userPanel.add(flagTool);
		userPanel.add(toolBarHelp);
		userPanel.add(trueButton);
		userPanel.add(falseButton);
		userPanel.add(separator);
		userPanel.add(instructions);
		questionPanel.add(questionText);
		
		
		//setting the positions of the flag tool and its description
		userLayout.putConstraint(SpringLayout.EAST, flagTool, 5, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, flagTool, -75, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, flagTool, 0, SpringLayout.VERTICAL_CENTER, userPanel);
		
		userLayout.putConstraint(SpringLayout.EAST, toolBarHelp, -50, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, toolBarHelp, -5, SpringLayout.SOUTH, userPanel);
		
		//setting the position of the true and false buttons
		userLayout.putConstraint(SpringLayout.EAST, trueButton, -30, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, trueButton, -280, SpringLayout.EAST, trueButton);
		userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, trueButton, -10, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, trueButton, -55, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, trueButton, 55, SpringLayout.VERTICAL_CENTER, userPanel);
		
		userLayout.putConstraint(SpringLayout.WEST, falseButton, 30, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.EAST, falseButton, 280, SpringLayout.WEST, falseButton);
		userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, falseButton, -10, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, falseButton, -55, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, falseButton, 55, SpringLayout.VERTICAL_CENTER, userPanel);
		
		//sets the position of the separator
		userLayout.putConstraint(SpringLayout.NORTH, separator, 0, SpringLayout.NORTH, falseButton);
		userLayout.putConstraint(SpringLayout.SOUTH, separator, 0, SpringLayout.SOUTH, falseButton);
		userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, separator, -3, SpringLayout.HORIZONTAL_CENTER, userPanel);
		
		//set the position of the instructions
		userLayout.putConstraint(SpringLayout.NORTH, instructions, 30, SpringLayout.NORTH, userPanel);
		userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructions, 0, SpringLayout.HORIZONTAL_CENTER, userPanel);
		
		
		//setting the position of the question
		questionLayout.putConstraint(SpringLayout.EAST, questionText, -25, SpringLayout.EAST, questionPanel);
		questionLayout.putConstraint(SpringLayout.WEST, questionText, 25, SpringLayout.WEST, questionPanel);
		questionLayout.putConstraint(SpringLayout.VERTICAL_CENTER, questionText, 0, SpringLayout.VERTICAL_CENTER, questionPanel);
		
	
		//adding the subpanels to the multiple choice question panel container
		trueFalsePanel.add(questionPanel);
		trueFalsePanel.add(userPanel);
		
		//setting the position of the questionPanel and userPanel within the mcqPanel
		panelLayout.putConstraint(SpringLayout.NORTH, questionPanel, 0, SpringLayout.NORTH, trueFalsePanel);
		panelLayout.putConstraint(SpringLayout.EAST, questionPanel, 0, SpringLayout.EAST, trueFalsePanel);
		panelLayout.putConstraint(SpringLayout.WEST, questionPanel, 0, SpringLayout.WEST, trueFalsePanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, questionPanel, (int)(-.125 * frame.getHeight()), SpringLayout.VERTICAL_CENTER, trueFalsePanel);
		
		panelLayout.putConstraint(SpringLayout.NORTH, userPanel, 0, SpringLayout.SOUTH, questionPanel);
		panelLayout.putConstraint(SpringLayout.EAST, userPanel, 0, SpringLayout.EAST, trueFalsePanel);
		panelLayout.putConstraint(SpringLayout.WEST, userPanel, 0, SpringLayout.WEST, trueFalsePanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, userPanel, 0, SpringLayout.SOUTH, trueFalsePanel);
		
		return trueFalsePanel;
	}
	
	
	//------------------Listeners------------------
	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e) {
		
		//sets user input to button that is clicked
		if (e.getSource() == trueButton) {
			userAnswer = "true";
		}
		if (e.getSource() == falseButton) {
			userAnswer = "false";
		}
		
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
