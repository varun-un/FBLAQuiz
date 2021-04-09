package FBLAQuiz;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

/**
 * Main class from where to run the program
 * @author Varun Unnithan
 *
 */
public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		new QuizMenu();
	}

}
