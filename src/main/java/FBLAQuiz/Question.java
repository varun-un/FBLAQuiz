package FBLAQuiz;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * An interface to define the shared methods of the different question 
 * types and classes.
 * 
 * @author Varun Unnithan
 *
 */
public interface Question {
	
	/**
	 * Gets the question String for this question
	 * @return The question as a String
	 */
	String getQuestion();
	
	/**
	 * Checks to see if a question is flagged
	 * @return {@code true} if a question is flagged
	 */
	boolean isFlagged();
	
	/**
	 * Get's this question's number within the quiz
	 * @return The question number of this question in the quiz
	 */
	int getQuizQuestionNumber();
	
	/**
	 * Checks to see if a question is answered
	 * @return {@code true} if a question is answered
	 */
	boolean isAnswered();
	
	/**
	 * Checks to see if the user's answer matches the correct answer 
	 * @return {@code true} if the user correctly answered the question
	 */
	boolean isCorrect();
	
	/**
	 * Creates a JPanel which includes UI components to add to the JFrame in order to ask the current question
	 * @return JPanel object to be added to the JFrame which includes this question
	 * @param questionNumber The question's number within the quiz
	 * @param frame The JFrame upon which this JPanel will be added
	 */
	JPanel createPanel(int questionNumber, JFrame frame);
}
