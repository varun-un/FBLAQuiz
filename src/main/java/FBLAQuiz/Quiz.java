package FBLAQuiz;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.json.simple.parser.ParseException;




/**
 * <h1>Quiz Class</h1>
 * 
 * The Quiz class creates a JPanel which contains each of the five question types
 * and provides a user interface to answer them.
 * 
 * @author Varun Unnithan
 * 
 */
@SuppressWarnings("serial")
public class Quiz extends JPanel implements ActionListener{
	
	
	//-------------Instance Variables-----------
	/** A random multiple choice question */
	private MCQ multipleChoice;
	/** A random fill-in-the-blank question */
	private FillInBlank fillInBlank;
	/** A random matching question */
	private Matching matching;
	/** A random multiple select question */
	private MultipleSelect multipleSelect;
	/** A random true or false question */
	private TrueOrFalse trueOrFalse;
	
	private ArrayList<Question> questions;

	/** The panel on which the question object's panels will be placed */
	private JPanel questionPanel;
	/** The layout manager for rotating through the question's panels */
	private CardLayout questionCards;
	/** The button to move onto the next question or submit the quiz */
	private JButton nextButton;
	/** The button to move backwards to the previous question */
	private JButton backButton;
	/** A label that is updated every second to show the quiz's duration */
	private JLabel timeLabel;
	/** A progressbar to indicate the user's progress in the quiz */
	private JProgressBar progressBar;
	/** A Swing Timer to keep track of the quiz's duration and update the timeLabel */
	private Timer time;
	/** The current question number of the question whose panel the user is on */
	private int currentQuestionNumber;
	/** The current duration of the quiz, in seconds */
	private int quizDuration;
	/** The layout manager for the entire quiz frame */
	private SpringLayout quizLayout; 
	/** The QuizMenu object from which this Quiz was called */
	private QuizMenu currentMenu;
	
	/**
	 * Creates a quiz object with 5 random questions and a score report at the end
	 * @param frame The JFrame this Quiz is being displayed on
	 * @param quizMenu The home menu from which this Quiz is being called from
	 * @throws IOException On input error
	 * @throws ParseException On error while parsing the database
	 * @throws FileNotFoundException On failure to find database or other file path
	 */
	public Quiz(JFrame frame, QuizMenu quizMenu) throws FileNotFoundException, IOException, ParseException {
		
		
		//sets up the overall JPanel for the Quiz object
		quizLayout = new SpringLayout();
		this.setLayout(quizLayout);
		this.setBackground(new Color(25,25,25));
				
		
		//create the questionPanel and its CardLayout
		questionPanel = new JPanel();
		questionCards = new CardLayout();
		questionPanel.setLayout(questionCards);
		
		//create the five different question objects
		MCQ multipleChoice = new MCQ();
		FillInBlank fillInBlank = new FillInBlank();
		Matching matching = new Matching();
		MultipleSelect multipleSelect = new MultipleSelect();
		TrueOrFalse trueOrFalse = new TrueOrFalse();
		
		//creates ArrayList for the questions and shuffle them
		questions = new ArrayList<Question>(Arrays.asList(multipleChoice, fillInBlank, matching, multipleSelect,
				trueOrFalse));
		Collections.shuffle(questions);
		
		//creates the panels for each of the questions and adds it to the card panel
		for (int i = 0; i < questions.size(); i++) {
			questionPanel.add(questions.get(i).createPanel(i + 1, frame));
		}
		currentQuestionNumber = 1;
		
		
		//sets up the next and back buttons
		nextButton = new JButton();
		nextButton.setIcon(new ImageIcon("./Icons/Next Button.png"));
		nextButton.setPressedIcon(new ImageIcon("./Icons/Next Button Pressed.png"));
		nextButton.setRolloverIcon(new ImageIcon("./Icons/Next Button Rollover.png"));
		nextButton.setOpaque(false);
		nextButton.setContentAreaFilled(false);
		nextButton.setBorderPainted(false);
		nextButton.setFocusable(false);
		nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		nextButton.addActionListener(this);
		
		backButton = new JButton();
		backButton.setIcon(new ImageIcon("./Icons/Back Button.png"));
		backButton.setPressedIcon(new ImageIcon("./Icons/Back Button Pressed.png"));
		backButton.setRolloverIcon(new ImageIcon("./Icons/Back Button Rollover.png"));
		backButton.setOpaque(false);
		backButton.setContentAreaFilled(false);
		backButton.setBorderPainted(false);
		backButton.setFocusable(false);
		backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		backButton.addActionListener(this);
		backButton.setVisible(false);
		
		
		//creates the progress bar
		progressBar = new JProgressBar(0, 200);
		progressBar.setValue(0);
		progressBar.setUI(new BasicProgressBarUI());
		
		
		//creates the border for the progress bar
		LineBorder lineBorder = new LineBorder(Color.LIGHT_GRAY, 6, true) {

			/**
			 * {@inheritDoc}
			 * 
			 * This implementation makes the border's corners more round
			 */
			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		        if ((this.thickness > 0) && (g instanceof Graphics2D)) {
		            Graphics2D g2d = (Graphics2D) g;

		            Color oldColor = g2d.getColor();
		            g2d.setColor(this.lineColor);

		            Shape outer;
		            Shape inner;

		            int offs = this.thickness;
		            int size = offs + offs;
		            if (this.roundedCorners) {
		                float arc = 2f * offs;		//modified curve arc size
		                outer = new RoundRectangle2D.Float(x, y, width, height, arc * 2f, arc* 2f);		
		                inner = new RoundRectangle2D.Float(x + offs, y + offs, width - size, height - size, arc, arc);
		            }
		            else {
		                outer = new Rectangle2D.Float(x, y, width, height);
		                inner = new Rectangle2D.Float(x + offs, y + offs, width - size, height - size);
		            }
		            Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
		            path.append(outer, false);
		            path.append(inner, false);
		            g2d.fill(path);
		            g2d.setColor(oldColor);
		        }
		    }
		};
		
		//sets up rest of the progress bar
		progressBar.setForeground(new Color(0x0000FF));
		progressBar.setBackground(new Color(25,25,25));
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Dialog.bold", Font.PLAIN, 20));
		progressBar.setBorder(lineBorder);
		
		
		//creates the JPanel for navigation tools
		JPanel navigationPanel = new JQualityPanel();
		navigationPanel.setBackground(new Color(25,25,25));
		SpringLayout navLayout = new SpringLayout();
		navigationPanel.setLayout(navLayout);
		
		timeLabel = new JLabel("00:00");
		timeLabel.setFont(new Font("Consolas", Font.BOLD, 18));
		timeLabel.setForeground(Color.LIGHT_GRAY);
		
		
		//adds the components to the navigation panel
		navigationPanel.add(nextButton);
		navigationPanel.add(backButton);
		navigationPanel.add(progressBar);
		navigationPanel.add(timeLabel);
		
		
		//sets the position of the next and back buttons
		navLayout.putConstraint(SpringLayout.SOUTH, backButton, 65, SpringLayout.NORTH, navigationPanel);
		navLayout.putConstraint(SpringLayout.WEST, backButton, 15, SpringLayout.WEST, navigationPanel);
		navLayout.putConstraint(SpringLayout.EAST, backButton, 130, SpringLayout.WEST, navigationPanel);
		navLayout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, navigationPanel);
		
		navLayout.putConstraint(SpringLayout.SOUTH, nextButton, 65, SpringLayout.NORTH, navigationPanel);
		navLayout.putConstraint(SpringLayout.WEST, nextButton, -130, SpringLayout.EAST, navigationPanel);
		navLayout.putConstraint(SpringLayout.EAST, nextButton, -15, SpringLayout.EAST, navigationPanel);
		navLayout.putConstraint(SpringLayout.NORTH, nextButton, 5, SpringLayout.NORTH, navigationPanel);
		
		//sets the position of the progress bar
		navLayout.putConstraint(SpringLayout.SOUTH, progressBar, 65, SpringLayout.NORTH, navigationPanel);
		navLayout.putConstraint(SpringLayout.WEST, progressBar, 25, SpringLayout.EAST, backButton);
		navLayout.putConstraint(SpringLayout.EAST, progressBar, -25, SpringLayout.WEST, nextButton);
		navLayout.putConstraint(SpringLayout.NORTH, progressBar, 10, SpringLayout.NORTH, navigationPanel);
		
		//sets the position of the JLabel for the timer
		navLayout.putConstraint(SpringLayout.NORTH, timeLabel, 8, SpringLayout.SOUTH, progressBar);
		navLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, timeLabel, 0, SpringLayout.HORIZONTAL_CENTER, navigationPanel);
		navLayout.putConstraint(SpringLayout.SOUTH, timeLabel, 0, SpringLayout.SOUTH, navigationPanel);
		
		
		//adds the subpanels and components to the overall container
		this.add(navigationPanel);
		this.add(questionPanel);
		
		
		//sets the position of the questionPanel
		quizLayout.putConstraint(SpringLayout.SOUTH, navigationPanel, 90, SpringLayout.NORTH, this);
		quizLayout.putConstraint(SpringLayout.WEST, navigationPanel, 0, SpringLayout.WEST, this);
		quizLayout.putConstraint(SpringLayout.EAST, navigationPanel, 0, SpringLayout.EAST, this);
		quizLayout.putConstraint(SpringLayout.NORTH, navigationPanel, 0, SpringLayout.NORTH, this);
		
		quizLayout.putConstraint(SpringLayout.SOUTH, questionPanel, 0, SpringLayout.SOUTH, this);
		quizLayout.putConstraint(SpringLayout.WEST, questionPanel, 0, SpringLayout.WEST, this);
		quizLayout.putConstraint(SpringLayout.EAST, questionPanel, 0, SpringLayout.EAST, this);
		quizLayout.putConstraint(SpringLayout.NORTH, questionPanel, 90, SpringLayout.NORTH, this);
		
		currentMenu = quizMenu;
		
	}


	//-----------------Methods--------------
	/**
	 * Gets the Quiz's question objects
	 * @return An ArrayList of the Quiz's questions, in order
	 */
	public ArrayList<Question> getQuestions() {
		return questions;
	}
	
	
	/**
	 * Gets the current question number which the user is on
	 * @return the current question number
	 */
	public int getCurrentQuestionNumber() {
		return currentQuestionNumber;
	}


	/**
	 * Gets the length of time taken on a quiz between starting and submitting
	 * @return the quiz's time in seconds
	 */
	public int getQuizDuration() {
		return quizDuration;
	}


	/**
	 * Starts the timer of the quiz
	 */
	public void startTimer() {
		quizDuration = 0;
		
		//creates a timer that'll fire every 1 second
		time = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//update quiz duration and the visible timer
				quizDuration++;
				String timeString = String.format("%02d:%02d", quizDuration / 60, quizDuration % 60);
				timeLabel.setText(timeString);
				
			}
			
		});	
		time.start();
	}
	
	
	/**
	 * Stops the timer of the quiz
	 */
	public void stopTimer() {
		time.stop();
	}
	
	
	/**
	 * Updates the progress bar to reflect the number of questions answered
	 */
	public void updateProgressBar() {
		
		Runnable updateBar = new Runnable() {

			@Override
			public void run() {
				
				//find how many questions have already been answered
				int numberAnswered = 0;
				for (int i = 0; i < questions.size(); i++) {
					if (questions.get(i).isAnswered()) {
						numberAnswered++;
					}
				}
				
				//calculate by how much the bar needs to change
				int steps = 40 * numberAnswered - progressBar.getValue();
				
				//decrease or increase the bar respectively
				if (steps > 0) {
					while (steps != 0) {
						progressBar.setValue(progressBar.getValue() + 1);
						steps --;
						
						//slow down in order to show animation
						try {
							Thread.sleep(7);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if (steps < 0) {
					while (steps != 0) {
						progressBar.setValue(progressBar.getValue() - 1);
						steps ++;
						
						//slow down in order to show animation
						try {
							Thread.sleep(7);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}	
			}
		};
		
		Thread updateBarThread = new Thread(updateBar);
		updateBarThread.start();
		
	}
	
	
	
	/**
	 * Displays the results of the quiz once it is finished
	 */
	private void displayResults() {
		
		//ends the Quiz
		this.removeAll();
		this.revalidate();
		this.repaint();
		stopTimer();
		
		
		//creates and stores a ScoreReport for this Quiz in the database
		ScoreReport report = new ScoreReport(this);
		try {
			report.updateDB();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//creates JPanel to display the results and adds it to the Quiz frame
		ResultsPanel resultsPanel = new ResultsPanel(this.getHeight());
		//sets the result panel's layout
		SpringLayout resultsLayout = new SpringLayout();
		resultsPanel.setLayout(resultsLayout);
		
		this.add(resultsPanel);
		
		//sets the position of the results panel
		quizLayout.putConstraint(SpringLayout.NORTH, resultsPanel, 0, SpringLayout.NORTH, this);
		quizLayout.putConstraint(SpringLayout.EAST, resultsPanel, 0, SpringLayout.EAST, this);
		quizLayout.putConstraint(SpringLayout.WEST, resultsPanel, 0, SpringLayout.WEST, this);
		quizLayout.putConstraint(SpringLayout.SOUTH, resultsPanel, 0, SpringLayout.SOUTH, this);
		
		
		
		//delay the animation start
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		//animate the line sliding up
		Runnable animateLine = new Runnable() {

			@Override
			public void run() {
				
				double change = 1;
				
				//while the line had to move, move it by a changing amount (quadratic motion)
				while (resultsPanel.getLineY() > 0) {
					
					resultsPanel.setLineY(resultsPanel.getLineY() - ((int) change));
					change += .2;
					
					//pause the animation for visual appeal
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	
				
				
			}

		};
		Thread animateResults = new Thread(animateLine);
		animateResults.start();
		
		//instantiate the action for when the exit button would be clicked
		ActionListener exitAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentMenu.showHomeScreen();
			}
			
		};
		
		//create and show the results panel
		JPanel scorePanel = report.createPanel(1000, this.getSize(), exitAction, "Return to the home page");
		
		resultsPanel.add(scorePanel);
		
		//set the results panel position
		resultsLayout.putConstraint(SpringLayout.NORTH, scorePanel, 0, SpringLayout.NORTH, resultsPanel);
		resultsLayout.putConstraint(SpringLayout.EAST, scorePanel, 0, SpringLayout.EAST, resultsPanel);
		resultsLayout.putConstraint(SpringLayout.WEST, scorePanel, 0, SpringLayout.WEST, resultsPanel);
		resultsLayout.putConstraint(SpringLayout.SOUTH, scorePanel, 0, SpringLayout.SOUTH, resultsPanel);
		
		
	}
	
	
	//--------------Listeners-------------
	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e) {
		
		//go to the next or previous question based on which button is clicked
		if (e.getSource() == nextButton) {
			
			if (currentQuestionNumber != 5) {
				questionCards.next(questionPanel);
				currentQuestionNumber++;
				updateProgressBar();
			}
			
			//if it is question 5 when the next button is clicked, run the submission confirmation dialog box
			else {
				
				//create the submit button for the dialog box
				JButton submit = new JButton("Submit");
				submit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JOptionPane.getRootFrame().dispose();
						displayResults();
					}
				});
				submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				submit.setFocusable(false);
				submit.setFocusPainted(false);
				
				//create the cancel button for the dialog box
				JButton cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						//close the popup when clicked
						JOptionPane.getRootFrame().dispose();	
					}
				});
				cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				cancel.setFocusable(false);
				cancel.setFocusPainted(false);
				
				//ArrayLists for the flagged and unanswered questions
				ArrayList<Integer> flaggedQuestions = new ArrayList<Integer>();
				ArrayList<Integer> blankQuestions = new ArrayList<Integer>();
				
				//checks to see which questions are flagged or unanswered
				for (int i = 0; i < questions.size(); i++) {
					if (questions.get(i).isFlagged()) {
						flaggedQuestions.add(questions.get(i).getQuizQuestionNumber());
					}
					if (questions.get(i).isAnswered()) {
						blankQuestions.add(questions.get(i).getQuizQuestionNumber());
					}
				}
				
				
				String numberFlaggedUnanswered = "";
				
				//sets up the confirmation text based on how many unanswered/flagged questions there are
				String numberFlagged = "", numberBlank = "";
				if (flaggedQuestions.size() != 0 || blankQuestions.size() != 0) {
					if (flaggedQuestions.size() > 0) {
						numberFlagged = flaggedQuestions.size() + " flagged question" + (flaggedQuestions.size() != 1 ? "s " : " ");
					}
					if (blankQuestions.size() > 0) {
						numberBlank = blankQuestions.size() + " unanswered question" + (blankQuestions.size() != 1 ? "s" : "");
					}
					if (flaggedQuestions.size() > 0 && blankQuestions.size() > 0) {
						numberFlagged += "and ";
					}
					
					//sets up the text with HTML tags for formatting
					numberFlaggedUnanswered = "<p style=\"text-align: center;\">You have " + numberFlagged 
							+ numberBlank + "</p>\r\n<ul>\r\n";
				}
				
				
				//sets up the text with HTML tags for formatting
				String submitWarning = "<html><p style=\"text-align: center;\"><strong>Are you sure you want to submit?</strong></p>\r\n"
						+ numberFlaggedUnanswered;
				
				//Show which specific questions are flagged/unanswered
				Collections.sort(flaggedQuestions);
				Collections.sort(blankQuestions);
				for (int i = 0; i < flaggedQuestions.size(); i++) {
					submitWarning += "<li style=\"text-align: left;\">Question " + flaggedQuestions.get(i) + " - Flagged</li>\r\n";
				}
				for (int i = 0; i < blankQuestions.size(); i++) {
					submitWarning += "<li style=\"text-align: left;\">Question " + blankQuestions.get(i) + " - Unanswered</li>\r\n";
				}
				
				submitWarning += "</ul></html>";
				
				
				//create the dialog box's text's JLabel
				JLabel dialogText = new JLabel();
				dialogText.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
				dialogText.setForeground(Color.LIGHT_GRAY);
				dialogText.setText(submitWarning);
				
				//create the dialog box
				Object[] buttons = {submit , cancel};
				JOptionPane.showOptionDialog(null, dialogText,"Confirm Submission",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, null);
				
			}
		}
		
		if (e.getSource() == backButton) {
			questionCards.previous(questionPanel);
			currentQuestionNumber--;
			updateProgressBar();
		}
		
		//display the back button only when not on question 1
		if (currentQuestionNumber > 1) {
			backButton.setVisible(true);
		}
		else {
			backButton.setVisible(false);
		}
		
		//changes the next button to a submit button if on the final question
		if (currentQuestionNumber == 5) {
			nextButton.setIcon(new ImageIcon("./Icons/Submit Button.png"));
			nextButton.setPressedIcon(new ImageIcon("./Icons/Submit Button Pressed.png"));
			nextButton.setRolloverIcon(new ImageIcon("./Icons/Submit Button Rollover.png"));
		}
		else {
			nextButton.setIcon(new ImageIcon("./Icons/Next Button.png"));
			nextButton.setPressedIcon(new ImageIcon("./Icons/Next Button Pressed.png"));
			nextButton.setRolloverIcon(new ImageIcon("./Icons/Next Button Rollover.png"));
		}
		
	}
	
	
	//-----------Inner Classes--------------
	/**
	 * A JPanel with higher rendering quality
	 * @see JPanel
	 */
	private class JQualityPanel extends JPanel {

		@Override
		public void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
			Graphics2D g2d = (Graphics2D) g;		
			//sets priority render quality
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
		}
		
	}
	

	/**
	 * A JPanel with a custom background for displaying the quiz's results
	 */
	private class ResultsPanel extends JPanel {
		
		/** The y-coordinate of the top of the panel's dividing line for the gradient */
		int lineYCord;
		/** The height of the parent container in pixels */
		int panelHeight;
		
		
		/**
		 * Constructs a ResultsPanel object
		 * @param height The height of the parent container in pixels
		 */
		ResultsPanel(int height) {
			panelHeight = height;
			//find where the dividing line was drawn for the question panels
			lineYCord = (int)(90 + ((panelHeight - 90) / 2.0) + (-.125 * (panelHeight - 37)));	//add 37 to account for window bar at top
		}
		
		
		/**
		 * Gets the current y-coordinate of the panel's line
		 * @return The current y-coordinate
		 */
		public int getLineY() {
			return lineYCord;
		}
		
		
		/**
		 * Changes the y-coordinate of the line
		 * @param newYCord The new y-coordinate to set the line to
		 */
		public void setLineY(int newYCord) {
			lineYCord = newYCord;
			repaint();
		}
		
		
		@Override
		public void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
			//setting width and height inside the paintComponent method updates it each time window is resized
			int width = getWidth();			
	        int height = getHeight();
			
			//casts Graphics object to new Graphics2D object
			Graphics2D g2d = (Graphics2D) g;		
			//sets priority render quality
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); 	
	        
			//creates a gradient for bottom section of panel
	        GradientPaint gp = new GradientPaint(0, 0, new Color(0x0000BF), width, height, new Color(0x0055FF));
	        g2d.setPaint(gp);
	        g2d.fillRect(0, lineYCord, width, panelHeight - lineYCord);
	        
	        //gets default Stroke
	      	Stroke defaultStroke = g2d.getStroke();
	      		
	      	//draws the sperating line, in the same location as the question panels
	        g2d.setStroke(new BasicStroke(15));
	        g2d.setPaint(Color.lightGray);
	        g2d.drawLine(0, lineYCord, width, lineYCord);
	      		
	        //resets the Stroke to its default
	        g2d.setStroke(defaultStroke);
	        
		}
		
	}

	
	
	
}
