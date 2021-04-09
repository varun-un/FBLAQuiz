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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * <h1>Fill-In-The-Blank Question Class</h1>
 * 
 * The FillInBlank class creates an object representing a fill-in-the-blank question 
 * and implements methods for dealing with such.
 * 
 * @author Varun Unnithan
 *
 */
public class FillInBlank extends MouseAdapter implements ActionListener, CaretListener{
		
	//----------------Instance variables----------------
	//Question variables
	/** The fill-in-the-blank question as a JSONObject */
	private JSONObject questionObject;
	/** The text of the question */
	private String question;
	/** The question's correct answer */
	private String answer;
	/** The user's answer to the question */
	private String userAnswer;
	/** Whether this question has been flagged for review or not */
	boolean flagged;
	/** The question number of this question in the quiz */
	private int quizQuestionNumber;
		
	//Components and scene objects
	/** A container for components to represent a question */
	private JPanel fillInBlankPanel, userPanel, questionPanel;
	/** The textfield for the user's answer to be inputted */
	private JTextField userInput;
	/** The button to flag the question for review */
	private JToggleButton flagTool;
	/** The JLabel description of the toolbar's tools */
	private JLabel toolBarHelp;
	/** The JLabel to display a warning for invalid user inputs */
	private JLabel inputWarning;
	
	
	//------------------Constructors------------------
	/**
	* Default constructor for creating a random question
	* @throws IOException On input error
	* @throws ParseException On error while parsing the database
	* @throws FileNotFoundException On failure to find database file path
	*/
	public FillInBlank() throws IOException, ParseException, FileNotFoundException{
		
		//read and store an array of all multiple choice questions
		JSONObject database;
		try {
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/testQuestions.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			database = (JSONObject)(QuizMenu.parser.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
		}
		JSONArray fillInArray = (JSONArray) database.get("Fill-in Blank");
		
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
	 * Method to get the question answer
	 * @return The answer as a String
	 */
	public String getAnswer() {
		return answer;
	}
	
	
	/**
	 * Mathod to get this question's number
	 * @return The question number of this question in the quiz
	 */
	public int getQuizQuestionNumber() {
		return quizQuestionNumber;
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
	 * Checks if the user input is correct
	 * @return Boolean value of whether user was right or not
	 */
	public boolean isCorrect() {
		return userAnswer.toLowerCase().trim().equals(answer.toLowerCase());
	}
	
	
	/**
	 * Checks to see if the passed String is an integer
	 * @param input A String to check
	 * @return Boolean value of whether or not the String was an integer
	 */
	public static boolean isInt(String input) {
		
		if (input == null) {
	        return false;
	    }
	    try {
	        Integer.parseInt(input);
	    } 
	    catch (NumberFormatException exception) {
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Creates a JPanel which includes GUI components to add to a JFrame to ask a fill-in-the-blank question
	 * @return JPanel object to be added to the JFrame to pose a fill-in-the-blank question
	 * @param questionNumber The question's number within the quiz
	 * @param frame The JFrame upon which this JPanel will be added
	 */
	public JPanel createPanel(int questionNumber, JFrame frame) {
		
		//creates the JPanels for the user input, question, and the overall container
		userPanel = new UserPanel();
		questionPanel = new JPanel();
		fillInBlankPanel = new JPanel();
		
		//setting up layout managers for the panels
		SpringLayout panelLayout = new SpringLayout();		
		fillInBlankPanel.setLayout(panelLayout);	
		
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
		
		//sets up the textfield
		userInput = new JTextField();
		userInput.setOpaque(false);
		String title;
		if (isInt(answer)) {					//to place respective title on answer textbox
			title = "Enter a number";
		}										
		else {
			title = "Answer";
		}
		LineBorder lineBorder = new LineBorder(Color.LIGHT_GRAY, 5, true);
		TitledBorder titleBorder = new TitledBorder((Border)lineBorder, title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Consolas", Font.PLAIN, 15), Color.LIGHT_GRAY);
		userInput.setBorder(titleBorder);
		userInput.setFont(new Font("Consolas", Font.PLAIN, 30));
		userInput.setForeground(new Color(25,25,25));
		userInput.setHorizontalAlignment(JTextField.CENTER);
		userInput.setFocusable(true);
		userInput.addCaretListener(this);
		
		//sets up the invalid user input warning message
		inputWarning = new JLabel();
		inputWarning.setFont(new Font("Karla", Font.PLAIN, 15));
		inputWarning.setForeground(Color.lightGray);
		inputWarning.setText("");
		
		//creates the instruction text
		JLabel instructions = new JLabel("Type a valid answer in the textbox");
		instructions.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		instructions.setForeground(Color.LIGHT_GRAY);
		
		
		//adding all the components and subpanels to their respective panels
		userPanel.add(flagTool);
		userPanel.add(toolBarHelp);
		userPanel.add(userInput);
		userPanel.add(inputWarning);
		userPanel.add(instructions);
		questionPanel.add(questionText);
		
		
		//setting the positions of the flag tool and its description
		userLayout.putConstraint(SpringLayout.EAST, flagTool, 5, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, flagTool, -75, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, flagTool, 0, SpringLayout.VERTICAL_CENTER, userPanel);
		
		userLayout.putConstraint(SpringLayout.EAST, toolBarHelp, -50, SpringLayout.EAST, userPanel);
		userLayout.putConstraint(SpringLayout.SOUTH, toolBarHelp, -5, SpringLayout.SOUTH, userPanel);
		
		//setting the position of the user input text box
		userLayout.putConstraint(SpringLayout.VERTICAL_CENTER, userInput, 5, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, userInput, 0, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.NORTH, userInput, -30, SpringLayout.VERTICAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.WEST, userInput, -250, SpringLayout.HORIZONTAL_CENTER, userPanel);
		userLayout.putConstraint(SpringLayout.EAST, userInput, 250, SpringLayout.HORIZONTAL_CENTER, userPanel);
		
		//setting the position of the invalid input warning
		userLayout.putConstraint(SpringLayout.NORTH, inputWarning, 10, SpringLayout.SOUTH, userInput);
		userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, inputWarning, 0, SpringLayout.HORIZONTAL_CENTER, userPanel);
		
		//set the position of the instructions
		userLayout.putConstraint(SpringLayout.NORTH, instructions, 30, SpringLayout.NORTH, userPanel);
		userLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructions, 0, SpringLayout.HORIZONTAL_CENTER, userPanel);
				
		
		//setting the position of the question
		questionLayout.putConstraint(SpringLayout.EAST, questionText, -25, SpringLayout.EAST, questionPanel);
		questionLayout.putConstraint(SpringLayout.WEST, questionText, 25, SpringLayout.WEST, questionPanel);
		questionLayout.putConstraint(SpringLayout.VERTICAL_CENTER, questionText, 0, SpringLayout.VERTICAL_CENTER, questionPanel);
		
	
		//adding the subpanels to the multiple choice question panel container
		fillInBlankPanel.add(questionPanel);
		fillInBlankPanel.add(userPanel);
		
		//setting the position of the questionPanel and userPanel within the mcqPanel
		panelLayout.putConstraint(SpringLayout.NORTH, questionPanel, 0, SpringLayout.NORTH, fillInBlankPanel);
		panelLayout.putConstraint(SpringLayout.EAST, questionPanel, 0, SpringLayout.EAST, fillInBlankPanel);
		panelLayout.putConstraint(SpringLayout.WEST, questionPanel, 0, SpringLayout.WEST, fillInBlankPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, questionPanel, (int)(-.125 * frame.getHeight()), SpringLayout.VERTICAL_CENTER, fillInBlankPanel);
		
		panelLayout.putConstraint(SpringLayout.NORTH, userPanel, 0, SpringLayout.SOUTH, questionPanel);
		panelLayout.putConstraint(SpringLayout.EAST, userPanel, 0, SpringLayout.EAST, fillInBlankPanel);
		panelLayout.putConstraint(SpringLayout.WEST, userPanel, 0, SpringLayout.WEST, fillInBlankPanel);
		panelLayout.putConstraint(SpringLayout.SOUTH, userPanel, 0, SpringLayout.SOUTH, fillInBlankPanel);
		
		return fillInBlankPanel;
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
		
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void caretUpdate(CaretEvent e) {
		
		//whenever the textfield is typed into, check for allowed inputs
		if (userInput.getText().length() >= 1) {
			
			//gets the last character the user inputted
			final char lastChar = userInput.getText().toLowerCase().charAt(userInput.getText().length() - 1);	
			
			//creates anonymous inner class of Runnable to remove the last character of the user's input
			Runnable removeLastChar = new Runnable() {
				public void run() {
					
					try {
						String validInput = userInput.getText().substring(0, userInput.getText().length() - 1);
						userInput.setText(validInput);
					} 
					catch (IndexOutOfBoundsException e) {}
					
					//issues the warning of invalid input
					if (isInt(answer)) {
						inputWarning.setText("Please enter a valid number");
					}
					else {
						inputWarning.setText("Please enter a valid character");
					}
				}
			};
			
			//checks if the last character is a valid input for the question
			if (isInt(answer)) {
				
				//if the last character isn't a number
				if (!(Character.isDigit(lastChar))) {
					SwingUtilities.invokeLater(removeLastChar);		//removes last character
				}
				else {
					inputWarning.setText(""); 		//removes the warning
				}
			}
			
			else {
				
				//if the last character isn't a letter or a space
				if (!(Character.isLetter(lastChar) || Character.isSpaceChar(lastChar))) {
					SwingUtilities.invokeLater(removeLastChar);		//removes last character
				}
				else {
					inputWarning.setText(""); 		//removes the warning
				}
			
			}
		}
		
		userAnswer = userInput.getText(); 		//updates the userAnswer variable
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
