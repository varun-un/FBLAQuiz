package FBLAQuiz;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * <h1>Question Report Class</h1>
 * 
 * The Question Report Class creates a report that represents
 * a question from a Quiz that was asked, and the answers to it.
 * 
 * This object is intended to be created for a question only once it
 * has been answered.
 * 
 * @author Varun Unnithan
 *
 */
public class QuestionReport implements Comparable<QuestionReport>{

	//-----------Instance Variables-----------
	/** The question's number within the quiz */
	private int questionNumber;
	/** Whether the question was answered correctly or not */
	private boolean isCorrect;
	/** The question itself */
	private String question;
	/** The correct answer to the question in a String format. If multiple parts are 
	 * needed for a correct answer, each is stored as an element in the ArrayList */
	private ArrayList<String> correctAnswer;
	/** The user's answer to the question in a String format. If multiple parts are 
	 * needed to represent the answer, each is stored as an element in the ArrayList */
	private ArrayList<String> userAnswer;

	
	
	//-----------Constructors-------------
	/**
	 * Creates a Question Report object for a multiple choice question
	 * @param mcq The multiple choice question for which to generate a report
	 */
	public QuestionReport(MCQ mcq) {
		
		//gets the question's information and saves it
		questionNumber = mcq.getQuizQuestionNumber();
		isCorrect = mcq.isCorrect();
		question = mcq.getQuestion();
		
		correctAnswer = new ArrayList<String>();
		correctAnswer.add(mcq.getAnswer());
		userAnswer = new ArrayList<String>();
		userAnswer.add(mcq.getUserAnswer());
	}
	
	
	/**
	 * Creates a Question Report object for a fill-in-the-blank question
	 * @param fillInBlank The fill-in-the-blank question for which to generate a report
	 */
	public QuestionReport(FillInBlank fillInBlank) {
		
		//gets the question's information and saves it
		questionNumber = fillInBlank.getQuizQuestionNumber();
		isCorrect = fillInBlank.isCorrect();
		question = fillInBlank.getQuestion();
		
		correctAnswer = new ArrayList<String>();
		correctAnswer.add(fillInBlank.getAnswer());
		userAnswer = new ArrayList<String>();
		userAnswer.add(fillInBlank.getUserAnswer());
	}
	
	
	/**
	 * Creates a Question Report object for a matching question
	 * @param matching The matching question for which to generate a report
	 */
	public QuestionReport(Matching matching) {
		
		//gets the question's information and saves it
		questionNumber = matching.getQuizQuestionNumber();
		isCorrect = matching.isCorrect();
		question = matching.getQuestion();
		
		correctAnswer = new ArrayList<String>();
		userAnswer = new ArrayList<String>();

		//for each matching pair, add a string to the ArrayList representing the two boxes that were to be matched
		for (int i = 0; i < matching.getGroupA().size(); i++) {
			
			if (matching.isAnswered())
				userAnswer.add(matching.getGroupA().get(i) + " : " + matching.getUserAnswer().get(i));
			else 
				userAnswer.add(matching.getGroupA().get(i) + " : No answer");
			
			correctAnswer.add(matching.getGroupA().get(i) + " : " + matching.getGroupB().get(i));
		}
		
	}
	
					
	/**
	 * Creates a Question Report object for a multiple select question
	 * @param multipleSelect The multiple select question for which to generate a report
	 */
	public QuestionReport(MultipleSelect multipleSelect) {
		
		//gets the question's information and saves it
		questionNumber = multipleSelect.getQuizQuestionNumber();
		isCorrect = multipleSelect.isCorrect();
		question = multipleSelect.getQuestion();
		
		correctAnswer = multipleSelect.getAnswer();
		
		//if there is no user answer, reflect such
		if ((multipleSelect.getUserAnswer() == null) || (multipleSelect.getUserAnswer().isEmpty())) {
			userAnswer = new ArrayList<String>();
			userAnswer.add("");
		}
		else {
			userAnswer = multipleSelect.getUserAnswer();
		}
	}
	
	
	/**
	 * Creates a Question Report object for a true or false question
	 * @param trueOrFalse The true or false question for which to generate a report
	 */
	public QuestionReport(TrueOrFalse trueOrFalse) {
		
		//gets the question's information and saves it
		questionNumber = trueOrFalse.getQuizQuestionNumber();
		isCorrect = trueOrFalse.isCorrect();
		question = trueOrFalse.getQuestion();
		
		correctAnswer = new ArrayList<String>();
		correctAnswer.add(trueOrFalse.getAnswer());
		userAnswer = new ArrayList<String>();
		userAnswer.add(trueOrFalse.getUserAnswer());
	}
	
	
	/**
	 * Creates a Question Report object based off of a JSONObject that was stored in a database.
	 * This method is typically implemented within the ScoreReport class.
	 * @param DBquestionReport The JSONObject from which to generate a Question Report
	 */
	public QuestionReport(JSONObject DBquestionReport) {
		
		questionNumber = (int) (long) DBquestionReport.get("number");
		question = (String) DBquestionReport.get("question");
		isCorrect = ((String) DBquestionReport.get("correct")).equals("true") ? true : false;
		
		correctAnswer = new ArrayList<String>();
		userAnswer = new ArrayList<String>();
		
		JSONArray answerArray = (JSONArray) DBquestionReport.get("answer");
		for (int i = 0; i < answerArray.size(); i++) {
			correctAnswer.add((String) answerArray.get(i));
		}
		
		JSONArray userAnswerArray = (JSONArray) DBquestionReport.get("user answer");
		for (int i = 0; i < userAnswerArray.size(); i++) {
			userAnswer.add((String) userAnswerArray.get(i));
		}
	}


	//---------------Methods------------------
	/**
	 * Gets the question's number within the quiz
	 * @return the question number
	 */
	public int getQuestionNumber() {
		return questionNumber;
	}


	/**
	 * Indicates if the user responded correctly or not
	 * @return whether the user was correct or not
	 */
	public boolean isCorrect() {
		return isCorrect;
	}


	/**
	 * Gets the question that is being asked
	 * @return the question's text
	 */
	public String getQuestion() {
		return question;
	}


	/**
	 * Gets the correct answer to the question
	 * Each element in the ArrayList is a different part of the answer
	 * @return the correct answer to the question
	 */
	public ArrayList<String> getCorrectAnswer() {
		return correctAnswer;
	}


	/**
	 * Gets the user's answer to the question
	 * Each element in the ArrayList is a different part of the answer
	 * @return the user's answer to the question
	 */
	public ArrayList<String> getUserAnswer() {
		return userAnswer;
	}


	/**
	 * Compares the questionNumber fields of two QuestionReport objects and 
	 * returns an int representing their relationship
	 * @return -1 or 1 if this questionReport's question number is less than or greater than, respectively, that of the specified object.
	 * @param questionReport the questionReport object to compare to
	 */
	@Override
	public int compareTo(QuestionReport questionReport) {
		//return -1 if the question number is less and 1 if it is greater
		return this.questionNumber < questionReport.getQuestionNumber() ? -1 : this.questionNumber > questionReport.getQuestionNumber() ? 1 : 0;
	}
	
	
	/**
	 * Creates a JSONObject that represents the necessary information of this
	 * question report to be added to a database to store.
	 * @return The JSONObject that represents this question report
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		
		//set up the JSONObject with the necessary information
		JSONObject questionDetails = new JSONObject();
		questionDetails.put("number", questionNumber);
		questionDetails.put("question", question);
		
		//converts the answer ArrayList to a JSONArray to be stored
		JSONArray answerArray = new JSONArray();
		for (int i = 0; i < correctAnswer.size(); i++) {
			answerArray.add(correctAnswer.get(i));
		}
		questionDetails.put("answer", answerArray);
		
		//converts the user answer ArrayList to a JSONArray to be stored
		JSONArray userAnswerArray = new JSONArray();
		for (int i = 0; i < userAnswer.size(); i++) {
			userAnswerArray.add(userAnswer.get(i));
		}
		questionDetails.put("user answer", userAnswerArray);
		
		questionDetails.put("correct", "" + isCorrect);
		
		return questionDetails;
		
	}
	
}
