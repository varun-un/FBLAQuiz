package FBLAQuiz;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;



/**
 * <h1>Score Report Class</h1>
 * 
 * The Score Report Class creates a score report for a Quiz object, which 
 * is essentially a collection of questionReports for each of the Quiz's
 * questions.
 * This class records and writes each quiz's data to a database, along with
 * implementing methods to display a report for the Quiz.
 * 
 * A ScoreReport object is meant to be created only upon the completion
 * of a Quiz.
 * 
 * @author Varun Unnithan
 *
 */
public class ScoreReport {

	//-------------------Instance Variables---------------
	/** An ArrayList of questionReports for each of the Quiz's questions */
	private ArrayList<QuestionReport> questionReports;
	/** An value between 0 and 5 that represents the number of questions answered correctly in the Quiz */
	private int score;
	/** The amount of time taken on the quiz, measured from its start to its submission, in seconds. */
	private int quizDuration;
	/** The date and time of the completion of the quiz.
	 * 	This is written in RFC-1123 date and time format. */
	private String dateAndTime;
	
	
	//---------------Constructors-------------------
	/**
	 * Creates a ScoreReport object to represent the results of a given Quiz
	 * @param quiz The Quiz for which to create a report
	 */
	public ScoreReport (Quiz quiz) {
	
		// gets and sets up the questions and their question reports
		questionReports = new ArrayList<QuestionReport>();
		ArrayList<Question> questions = quiz.getQuestions();
		
		for (int i = 0; i < questions.size(); i++) {
			
			//check which question type each question is and create the appropriate question report
			if (questions.get(i) instanceof MCQ) {
				questionReports.add(new QuestionReport((MCQ)(questions.get(i))));
			}
			else if (questions.get(i) instanceof MultipleSelect) {
				questionReports.add(new QuestionReport((MultipleSelect)(questions.get(i))));
			}
			else if (questions.get(i) instanceof FillInBlank) {
				questionReports.add(new QuestionReport((FillInBlank)(questions.get(i))));
			}
			else if (questions.get(i) instanceof Matching) {
				questionReports.add(new QuestionReport((Matching)(questions.get(i))));
			}
			else{
				questionReports.add(new QuestionReport((TrueOrFalse)(questions.get(i))));
			}
		}
		
		//record the number of correct questions
		score = 0;
		for (int i = 0; i < questionReports.size(); i++) {
			if (questionReports.get(i).isCorrect()) {
				score++;
			}
		}
		
		quizDuration = quiz.getQuizDuration();
		dateAndTime = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		
	}
	
	
	/**
	 * Creates a ScoreReport object from a given JSONObject. The passed JSONObject 
	 * should be a member of the JSONArray that makes up the scoreReport database.
	 * @param DBscoreReport The JSONObject for which to create a report. 
	 */
	public ScoreReport(JSONObject DBscoreReport) {
		
		dateAndTime = (String) DBscoreReport.get("date");
		score = (int) (long) DBscoreReport.get("score");
		quizDuration = (int) (long) DBscoreReport.get("time");
		
		JSONArray questionReportsArray = (JSONArray) DBscoreReport.get("questions");
		
		questionReports = new ArrayList<QuestionReport>();
		
		for (int i = 0; i < questionReportsArray.size(); i++) {
			questionReports.add(new QuestionReport((JSONObject) (questionReportsArray.get(i))));
		}
	}
	
	
	//---------------Methods--------------
	/**
	 * Formats the quiz's duration into a readable String
	 * @return the Quiz's duration in a formatted String (in minute(s) and second(s))
	 */
	public String getFormattedDuration() {
		
		//sets up the minutes
		String durationString = (quizDuration / 60 == 0) ? "" : (quizDuration/60 == 1) ? quizDuration / 60 + " minute" : quizDuration / 60 + " minutes";
		durationString += (quizDuration % 60 != 0) && (quizDuration / 60 != 0) ? " and " : "";
		//sets up the seconds
		durationString += (quizDuration % 60 == 0) ? "": (quizDuration % 60 == 1) ? quizDuration % 60 + " second" : quizDuration % 60 + " seconds";
	
		return durationString;
	}
	
	
	/**
	 * Formats a given date and time into a 12-hour clock format
	 * @param dateAndTime The String for the date and time, written in RFC-1123 date and time format
	 * @return The formatted String, with the date cut out, in the format of time + PM/AM
	 */
	public static String get12hrTime(String dateAndTime) {
		
		String timeString;
		
		//gets a String representation of the time which this report's quiz was taken
		if (dateAndTime.substring(15, 16).equals(" "))
			timeString = dateAndTime.substring(16,21);
		else
			timeString = dateAndTime.substring(17,22);
		
		//converts the timeString from that of a 24-hour clock to a 12-hour clock time
        Date militaryDate;
		try {
			SimpleDateFormat militaryFormat = new SimpleDateFormat("HH:mm");
	        SimpleDateFormat twelveHrFormat = new SimpleDateFormat("hh:mm a");
	        militaryDate = militaryFormat.parse(timeString);
			timeString = twelveHrFormat.format(militaryDate);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		//return the formtted String for time
		return timeString;
		
	}
	
	/**
	 * Updates the score report JSON database with this score report by adding it
	 * @throws IOException On input error or error when writing the desired files
	 */
	@SuppressWarnings("unchecked")
	public void updateDB() throws IOException {
		
		//retreive the current database from the file
		JSONArray database;
		try {
			database = (JSONArray) QuizMenu.PARSER.parse(new FileReader("./JSONfiles/scoreReports.json"));
		} 
		//use the backup database if unavailable
		catch (IOException | ParseException e) {
			try {
				database = (JSONArray) QuizMenu.PARSER.parse(new FileReader("./JSONfiles/backup/scoreReports.json"));
			} 
			//if backup database isn't available too, proceed as if the database were empty
			catch (IOException | ParseException e1) {
				database = new JSONArray();
			}
		}

		
		//create a JSONArray to represent the question reports
		JSONArray questionReportsArray = new JSONArray();
		for (int i = 0; i < questionReports.size(); i++) {
			questionReportsArray.add(questionReports.get(i).toJSON());
		}
		
		//create the JSONObject for this score report
		JSONObject scoreReport = new JSONObject();
		scoreReport.put("date", dateAndTime);
		scoreReport.put("shortened date", (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date()));
		scoreReport.put("score", score);
		scoreReport.put("time", quizDuration);
		scoreReport.put("questions", questionReportsArray);
		
		//adds the score report JSONObject to the database
		database.add(0, scoreReport);
		
		//write the modified database onto the file
		FileWriter scoreReportFile = new FileWriter("./JSONfiles/scoreReports.json");
		scoreReportFile.write(database.toJSONString());
		scoreReportFile.flush();
		scoreReportFile.close();
	}
	
	
	/**
	 * Creates and saves a printable PDF file from this score report
	 * @param pdfName The name of the PDF file to be created
	 * @throws IOException If the file could not be written or there is an error releasing resources
	 */
	public void createPDF(String pdfName) throws IOException {
		
		//creates the PDF document and a page within the PDF to write onto
		PDDocument reportPDF = new PDDocument();
		PDPage reportPage = new PDPage();
		reportPDF.addPage(reportPage);
		
		PDPageContentStream contentStream = new PDPageContentStream(reportPDF, reportPage);
		contentStream.beginText();
		
		//setting the position for the line 
		contentStream.newLineAtOffset(50, 725);
		
		//writes the title
		contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUCBD.ttf")), 32);
		contentStream.showText("FBLA Quiz Results");
		contentStream.endText();
		
		//draws the dividing line between the title and the report itself
		contentStream.moveTo(0, 715);
		contentStream.lineTo(500, 715);
		contentStream.setStrokingColor(new Color(25,25,25));
		contentStream.setLineCapStyle(1);
		contentStream.setLineWidth(8);
		contentStream.stroke();

		
		contentStream.beginText();
		contentStream.setLeading(40f);
		
		//writes the quiz's date and time
		contentStream.newLineAtOffset(50, 690);
		contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/MavenPro-Regular.ttf")), 16);
		contentStream.showText("Taken on " + dateAndTime.substring(0, 16).trim() + " at " + get12hrTime(dateAndTime));
		
		//writes the quiz's score
		contentStream.newLine();
		contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUCBD.ttf")), 16);
		contentStream.showText("Score: ");
		contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUC.ttf")), 16);
		contentStream.showText(score + " / " + questionReports.size() + "                        ");
		
		//writes the quiz's duration
		contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUCBD.ttf")), 16);
		contentStream.showText("Time Spent: ");
		contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUC.ttf")), 16);
		contentStream.showText(getFormattedDuration());
		contentStream.endText();
		
		int questionYCord = 590;
		
		//For each of the quiz's questions, write its report
		for (int i = 0; i < 5; i++) {
			
			contentStream.beginText();
			contentStream.newLineAtOffset(50, questionYCord);
			
			//write the question, with the bounding box based off of the page's size
		    float questionWidth = reportPage.getMediaBox().getWidth() - 2 * 80;
			wrapText(contentStream, questionReports.get(i).getQuestionNumber() + ") " + questionReports.get(i).getQuestion(), 
					PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUCBD.ttf")), 13, questionWidth);
			
			contentStream.endText();
			
			
			//display the correct/incorrect icon
			if (questionReports.get(i).isCorrect()) {
				
				PDImageXObject correctIcon = PDImageXObject.createFromFile("./Icons/Correct Icon Scaled.png", reportPDF);
				contentStream.drawImage(correctIcon, 510, questionYCord - 26);
			}
			else {
				
				PDImageXObject correctIcon = PDImageXObject.createFromFile("./Icons/Incorrect Icon Scaled.png", reportPDF);
				contentStream.drawImage(correctIcon, 510, questionYCord - 26);
			}
			
			
			//display user answer
			contentStream.beginText();
			questionYCord -= questionReports.get(i).getQuestion().length() / 64 * 15 + 25;
			contentStream.newLineAtOffset(60, questionYCord);
			
			contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUC.ttf")), (float) 12);
			contentStream.showText("User Answer:");
			contentStream.newLine();
			
		    String userAnswer = questionReports.get(i).getUserAnswer().get(0);
		    
		    //check if unanswered
		    if ((questionReports.get(i).getUserAnswer().size() == 1) && (questionReports.get(i).getUserAnswer().get(0).equals(""))) {
				userAnswer = "Unanswered";
			}
		    
		    //write the user answer
		    for (int c = 0; c < questionReports.get(i).getUserAnswer().size(); c++) {
		    	
		    	wrapText(contentStream, userAnswer, 
						PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUC.ttf")), (float) 11, 250);
		    	contentStream.setLeading(3);
		    	contentStream.newLine();
		    	
		    	userAnswer = questionReports.get(i).getUserAnswer().get(c + 1 >= questionReports.get(i).getUserAnswer().size() ? 0 : c+1);
		    	
		    }
		    contentStream.endText();
		    
		    
		    //display correct answer
		    contentStream.beginText();
			contentStream.newLineAtOffset(335, questionYCord);
			
			contentStream.setFont(PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUC.ttf")), (float) 12);
			contentStream.showText("Correct Answer:");
			contentStream.setLeading(11.5*1.5);
			contentStream.newLine();
			
			//count the number of lines the correct answer will take up
			int lineCount = 0;
		    
			//write the correct answer
		    for (int c = 0; c < questionReports.get(i).getCorrectAnswer().size(); c++) {
		    	
		    	lineCount += wrapText(contentStream, questionReports.get(i).getCorrectAnswer().get(c), 
						PDType0Font.load(reportPDF, new File("./src/Fonts/TREBUC.ttf")), (float) 11, 250);
		    	contentStream.setLeading(3);
		    	contentStream.newLine();
		    	
		    }
		    contentStream.endText();
		   
		    //draw the underline for "correct answer" subtitle
		    contentStream.moveTo(335, questionYCord - 2);
			contentStream.lineTo(420, questionYCord - 2);
			contentStream.setStrokingColor(new Color(25,25,25));
			contentStream.setLineWidth(1);
			contentStream.stroke();
			
			//draw the underline for "user answer" subtitle
		    contentStream.moveTo(60, questionYCord - 2);
			contentStream.lineTo(132, questionYCord - 2);
			contentStream.stroke();
		    
			//go to next line
		    questionYCord -= lineCount * 14 + 50;
		}
		
		//close the content stream and save the PDF with the given name
		contentStream.close();
		reportPDF.save("./Score Reports/" + pdfName + ".pdf");
		reportPDF.close();
		
	}
	
	
	
	/**
	 * Helper method to write text onto a PDF from a content stream with the text wrapped around the edges of the page
	 * @param contentStream The PDPageContentStream from which to write through
	 * @param text The text that is to be written and wrapped
	 * @param pdfFont The font for the text that is to be wrapped
	 * @param fontSize The font size for the text
	 * @param wrapWidth The width of the bounding box by which the text will be wrapped
	 * @return The number of lines the text has been formatted into
	 * @throws IOException On error when writing to the stream
	 */
	private int wrapText(PDPageContentStream contentStream, String text, PDFont pdfFont, float fontSize, float wrapWidth) throws IOException {
		
		contentStream.setLeading(fontSize * 1.5);
		int lineCount = 0;
 
	    ArrayList<String> lines = new ArrayList<String>();
	    int lastSpace = -1;
	    while (text.length() > 0)
	    {
	        int spaceIndex = text.indexOf(' ', lastSpace + 1);
	        if (spaceIndex < 0)
	            spaceIndex = text.length();
	        String subString = text.substring(0, spaceIndex);
	        float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
	        if (size > wrapWidth)
	        {
	            if (lastSpace < 0)
	                lastSpace = spaceIndex;
	            subString = text.substring(0, lastSpace);
	            lines.add(subString);
	            text = text.substring(lastSpace).trim();
	            lastSpace = -1;
	        }
	        else if (spaceIndex == text.length())
	        {
	            lines.add(text);
	            text = "";
	        }
	        else
	        {
	            lastSpace = spaceIndex;
	        }
	        
	    }
	    
	    contentStream.setFont(pdfFont, fontSize);
        for (String line: lines)
        {
            contentStream.showText(line);
            contentStream.newLine();
            lineCount++;
        }
        return lineCount;
	}
	
	
	/**
	 * Creates a JPanel to display a ScoreReport's contents
	 * @param delay The amount of milliseconds to wait before displaying this panel
	 * @param panelSize The size of the parent panel or frame
	 * @param exitAction The Action to be taken upon the clicking of the exit button
	 * @return the JPanel The JPanel that is created that displays this score report
	 */
	public JPanel createPanel(int delay, Dimension panelSize, ActionListener exitAction, String exitMessage) {
		
		return (new ScorePanel(delay, panelSize, exitAction, exitMessage));
	}
	
	
	
	//----------------Inner Classes---------------
	/**
	 * A custom JPanel made specifically for displaying a ScoreReport's details
	 * @author Varun Unnithan
	 */
	@SuppressWarnings("serial")
	public class ScorePanel extends JPanel implements ActionListener{

		//-------------Instance Variables--------
		/** The label to display the title of the page */
		FadingLabel title;
		/** The custom labels to display the ScoreReport's metadata */
		FadingLabel dateLabel, durationLabel, scoreLabel;
		/** Timers to delay the displaying of this panel's contents */
		Timer showPanelDelay, metadataDelay, lineDelay, questionsDelay;
		/** The width, in pixels, of the ScorePanel's dividing line */
		int lineWidth;
		/** The layout manager fo the ScorePanel's contents */
		SpringLayout scoreLayout, questionsLayout;
		/** The container for all of the results data regarding specific questions */
		JPanel questionsPanel;
		/** An ArrayList of the FadingLabel objects in the questionPanel */
		ArrayList<FadingLabel> questionLabels, questionIcons;
		/** The ScrollPane that allows for the scrolling through of questions */
		JScrollPane scrollQuestions;
		/** The button to export the results to a file */
		FadingButton exportButton;
		/** The button to exit the resulst screen, with its specific action being passed in */
		FadingButton exitButton;
		

		
		
		//--------------Constructor-------------
		/**
		 * Creates a ScorePanel object to display this ScoreReport's data
		 * @param delay The amount of milliseconds to wait before displaying this panel
		 * @param panelSize The size of the parent panel or frame
		 * @param exitAction The Action to be taken upon the clicking of the exit button
		 * @param exitMessage The message to display or the description of the exit button
		 */
		public ScorePanel(int delay, Dimension panelSize, ActionListener exitAction, String exitMessage) {
			
			lineWidth = 0;
			
			//sets up this JPanel
			this.setOpaque(false);
			scoreLayout = new SpringLayout();
			this.setLayout(scoreLayout);
			
			//sets up the timer to delay the start of the panel's creation
			showPanelDelay = new Timer(delay, this);
			showPanelDelay.start();
			showPanelDelay.setRepeats(false);
			
			//sets up the page's title
			title = new FadingLabel();
			title.setText("Results");
			title.setFont(new Font("Trebuchet MS", Font.BOLD, 60));
			title.setForeground(new Color(25,25,25));
			title.setHorizontalAlignment(JLabel.CENTER);
			title.setOpaque(false);
			
			//sets up the label for this report's date
			dateLabel = new FadingLabel();
			dateLabel.setText((dateAndTime.substring(0, 16)).trim() + " at " + get12hrTime(dateAndTime));
			dateLabel.setFont(new Font("Consolas", Font.PLAIN, 23));
			dateLabel.setForeground(new Color(25,25,25));
			dateLabel.setHorizontalAlignment(JLabel.CENTER);
			dateLabel.setOpaque(false);
			
			
			//sets up the label for the quiz's quration
			durationLabel = new FadingLabel();
			durationLabel.setText("<html><strong>Time Spent: </strong>" + getFormattedDuration() + "</html>");
			durationLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 28));
			durationLabel.setForeground(new Color(25,25,25));
			durationLabel.setHorizontalAlignment(JLabel.CENTER);
			durationLabel.setOpaque(false);
			
			//sets up the label for the quiz's score
			scoreLabel = new FadingLabel();
			scoreLabel.setText("<html><strong>Score: </strong>" + score + " / " + questionReports.size() + "</html>");
			scoreLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 28));
			scoreLabel.setForeground(new Color(25,25,25));
			scoreLabel.setHorizontalAlignment(JLabel.CENTER);
			scoreLabel.setOpaque(false);
			
			
			//sets up the label description for the exit button
			JLabel exitLabel = new JLabel();
			exitLabel.setText("<html>" + exitMessage + "</html>");
			exitLabel.setFont(new Font("Consolas", Font.PLAIN, 15));
			exitLabel.setVerticalTextPosition(JLabel.CENTER);
			exitLabel.setForeground(Color.LIGHT_GRAY);
			exitLabel.setOpaque(false);
			exitLabel.setVisible(false);
			
			
			//sets up the exit button
			exitButton = new FadingButton();
			exitButton.setIcon(new ImageIcon("./Icons/Exit Icon Unselected.png"));
			exitButton.setPressedIcon(new ImageIcon("./Icons/Exit Icon Selected.png"));
			exitButton.setRolloverIcon(new ImageIcon("./Icons/Exit Icon Rollover.png"));
			exitButton.setOpaque(false);
			exitButton.setContentAreaFilled(false);
			exitButton.setBorderPainted(false);
			exitButton.setFocusable(false);
			exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			exitButton.addActionListener(exitAction);
			
			//adds the mouse listeners for when to display the button's label
			exitButton.addMouseListener(new MouseAdapter() {
				/**
				 * {@inheritDoc}
				 */
				public void mouseEntered(MouseEvent e) {
					//when hovering over the exit button, display its description
					exitLabel.setVisible(true);
				}
				/**
				 * {@inheritDoc}
				 */
				public void mouseExited(MouseEvent e) {
					//when exiting the button, hide the description
					exitLabel.setVisible(false);
				}
			});
			
			
			//sets up the label description for the export button
			JTextPane exportLabel = new JTextPane();
			exportLabel.setText("Export this\npage to a\nprintable\nPDF file");
			exportLabel.setFont(new Font("Consolas", Font.PLAIN, 15));
			exportLabel.setForeground(Color.LIGHT_GRAY);
			exportLabel.setOpaque(false);
			exportLabel.setVisible(false);
			exportLabel.setEditable(false);
			//sets up the formatting of the label
			StyledDocument textDoc = exportLabel.getStyledDocument();
			SimpleAttributeSet right = new SimpleAttributeSet();
			StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
			textDoc.setParagraphAttributes(0, textDoc.getLength(), right, false);
			
			//sets up the export button
			exportButton = new FadingButton();
			exportButton.setIcon(new ImageIcon("./Icons/Export Icon Unselected.png"));
			exportButton.setPressedIcon(new ImageIcon("./Icons/Export Icon Selected.png"));
			exportButton.setRolloverIcon(new ImageIcon("./Icons/Export Icon Rollover.png"));
			exportButton.setOpaque(false);
			exportButton.setContentAreaFilled(false);
			exportButton.setBorderPainted(false);
			exportButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			exportButton.setFocusable(false);
			
			//adds the mouse listeners for when to display the button's label
			exportButton.addMouseListener(new MouseAdapter() {
				/**
				 * {@inheritDoc}
				 */
				public void mouseEntered(MouseEvent e) {
					//when hovering over the export button, display its description
					exportLabel.setVisible(true);
				}
				/**
				 * {@inheritDoc}
				 */
				public void mouseExited(MouseEvent e) {
					//when exiting the button, hide the description
					exportLabel.setVisible(false);
				}
			});

			//adds the action listener to create a printable document when clicked
			exportButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					//creates the pdf title as month date, time Report		(i.e. Feb 15, 5:18 PM Report)
					String pdfName = dateAndTime.substring(7, 11).trim() + " " + dateAndTime.substring(5,7).trim() + ", " + get12hrTime(dateAndTime) + " Report";
					
					//replaces any colons with an acceptable unicode character to avid file name errors
					final String PDFName = pdfName.replace(":", "\ua789");
					
					try {
						createPDF(PDFName);
						
						//if the PDF is produced, show the success dialog box
						
						//create the submit button for the dialog box
						JButton viewButton = new JButton("View Now");
						viewButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								//close the dialog box
								JOptionPane.getRootFrame().dispose();

								//opens the PDF
								if (Desktop.isDesktopSupported()) {
									
								    try {
								        File myFile = new File("./Score Reports/" + PDFName + ".pdf");
								        Desktop.getDesktop().open(myFile);
								    } catch (IOException ex) {}
								}
							}
						});
						viewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						viewButton.setFocusable(false);
						viewButton.setFocusPainted(false);
						
						
						//create the cancel button for the dialog box
						JButton closeButton = new JButton("Close");
						closeButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								JOptionPane.getRootFrame().dispose();	
							}
						});
						closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						closeButton.setFocusable(false);
						closeButton.setFocusPainted(false);
						
						
						//create the dialog box's text's JLabel
						JLabel dialogText = new JLabel();
						dialogText.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
						dialogText.setForeground(Color.LIGHT_GRAY);
						dialogText.setText("The PDF has successfully been made");
						
						//create the dialog box
						Object[] buttons = {viewButton , closeButton};
						JOptionPane.showOptionDialog(null, dialogText, "PDF Report",
								JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, null);
						
					} 
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			//set the background color of dialog box
			UIManager.put("OptionPane.background", new Color(25,25,25));		
			UIManager.put("Panel.background", new Color(25,25,25));
			
			//creates the JPanel to display the questions, with a set preferred size
			questionsPanel = new JPanel(){

			    @Override
			    public Dimension getPreferredSize() {
			    
			    	//returns the average height of a questionsPanel with 5 questions
			        return new Dimension((int) panelSize.getWidth(), 1180);
			     }
			};
			questionsPanel.setOpaque(false);
			questionsLayout = new SpringLayout();
			questionsPanel.setLayout(questionsLayout);
			//hide the panel until its time to show
			questionsPanel.setVisible(false);
			
			
			questionLabels = new ArrayList<FadingLabel>();
			questionIcons = new ArrayList<FadingLabel>();
			
			for (int i = 0; i < questionReports.size(); i++) {
				
				//gets the text of the question and its number
				String questionText = "<html>" + questionReports.get(i).getQuestionNumber() + ") " + questionReports.get(i).getQuestion()
						+ "</html>";
				
				//sets up the label for the question
				FadingLabel question = new FadingLabel();
				question.setText(questionText);
				question.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
				question.setForeground(new Color(25,25,25));
				question.setHorizontalAlignment(JLabel.LEFT);
				question.setOpaque(false);
				
				
				//gets and sets up the text of the user answer
				String userAnswerText = "<html><span style=\"text-decoration: underline;\"><strong>"
						+ "User Answer:</strong></span>";
				
				//if there is no user response, display "unanswered"
				if ((questionReports.get(i).getUserAnswer().size() == 1) && (questionReports.get(i).getUserAnswer().get(0).equals(""))) {
					userAnswerText += "<br>Unanswered";
				}
				//if there is a user response, display such
				else {
					for (int a = 0; a < questionReports.get(i).getUserAnswer().size(); a++) {
						userAnswerText += "<br>" + questionReports.get(i).getUserAnswer().get(a);
					}
				}
				userAnswerText += "<html>";
				
				
				//sets up the label for the user answer
				FadingLabel userAnswer = new FadingLabel();
				userAnswer.setText(userAnswerText);
				userAnswer.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
				userAnswer.setForeground(Color.LIGHT_GRAY);
				userAnswer.setHorizontalAlignment(JLabel.LEFT);
				userAnswer.setVerticalAlignment(JLabel.TOP);
				userAnswer.setOpaque(false);
				
				
				//gets and sets up the text of the correct answer
				String correctAnswerText = "<html><span style=\"text-decoration: underline;\"><strong>"
						+ "Correct Answer:</strong></span>";
				
				//gets and formats each part of a correct answer
				for (int a = 0; a < questionReports.get(i).getCorrectAnswer().size(); a++) {
					correctAnswerText += "<br>" + questionReports.get(i).getCorrectAnswer().get(a);
				}	
				correctAnswerText += "<html>";
				
				
				//sets up the label for the correct answer
				FadingLabel correctAnswer = new FadingLabel();
				correctAnswer.setText(correctAnswerText);
				correctAnswer.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
				correctAnswer.setForeground(Color.LIGHT_GRAY);
				correctAnswer.setHorizontalAlignment(JLabel.LEFT);
				correctAnswer.setVerticalAlignment(JLabel.TOP);
				correctAnswer.setOpaque(false);
				
				
				//sets up the label for the correct indicator icon
				FadingLabel isCorrect = new FadingLabel();
				if (questionReports.get(i).isCorrect())
					isCorrect.setIcon(new ImageIcon("./Icons/Correct Icon.png"));
				else
					isCorrect.setIcon(new ImageIcon("./Icons/Incorrect Icon.png"));
				
				
				//adds all the labels to the ArrayList for them
				questionLabels.add(question);
				questionLabels.add(userAnswer);
				questionLabels.add(correctAnswer);
				questionIcons.add(isCorrect);
				
				//adds the labels to the questionsPanel
				questionsPanel.add(question);
				questionsPanel.add(userAnswer);
				questionsPanel.add(correctAnswer);
				questionsPanel.add(isCorrect);
				
				
				//sets top position based on whether if it is the first question or not
				if (questionReports.get(i).getQuestionNumber() == 1) {
					questionsLayout.putConstraint(SpringLayout.NORTH, question, 20, SpringLayout.NORTH, questionsPanel);
				}
				else {
					questionsLayout.putConstraint(SpringLayout.NORTH, question, 0, SpringLayout.SOUTH, questionLabels.get(i*3-1));
				}
				//sets the position of the question label
				questionsLayout.putConstraint(SpringLayout.EAST, question, -125, SpringLayout.EAST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.WEST, question, 25, SpringLayout.WEST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.SOUTH, question, questionText.length() > 75 ? 90 : 60, 
						SpringLayout.NORTH, question);			//set size based on the text's length
				
				//sets the position of the user answer label
				questionsLayout.putConstraint(SpringLayout.NORTH, userAnswer, 20, SpringLayout.SOUTH, question);
				questionsLayout.putConstraint(SpringLayout.EAST, userAnswer, -55, SpringLayout.HORIZONTAL_CENTER, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.WEST, userAnswer, 40, SpringLayout.WEST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.SOUTH, userAnswer, userAnswerText.length() > 200 ? 160 : 120, 
						SpringLayout.NORTH, userAnswer);			//set size based on the text's length
				
				//sets the position of the correct answer label
				questionsLayout.putConstraint(SpringLayout.NORTH, correctAnswer, 20, SpringLayout.SOUTH, question);
				questionsLayout.putConstraint(SpringLayout.EAST, correctAnswer, -120, SpringLayout.EAST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.WEST, correctAnswer, -30, SpringLayout.HORIZONTAL_CENTER, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.SOUTH, correctAnswer, correctAnswerText.length() > 250 ? 160 : 120, 
						SpringLayout.NORTH, correctAnswer);			//set size based on the text's length
				
				//sets the position of the correct answer label
				questionsLayout.putConstraint(SpringLayout.NORTH, isCorrect, 25, SpringLayout.NORTH, question);
				questionsLayout.putConstraint(SpringLayout.EAST, isCorrect, 40, SpringLayout.EAST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.WEST, isCorrect, -120, SpringLayout.EAST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.SOUTH, isCorrect, 80, SpringLayout.NORTH, isCorrect);
			}
			

			//sets up the scroll pane for the questions
			scrollQuestions = new JScrollPane(questionsPanel) ;
			scrollQuestions.setOpaque(false);
			scrollQuestions.getViewport().setOpaque(false);
			scrollQuestions.setBorder(BorderFactory.createEmptyBorder());
			scrollQuestions.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  
			scrollQuestions.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			//modifies the scroll bar itself, and sets it to the custom UI
			scrollQuestions.getVerticalScrollBar().setUI(new CustomScrollUI());
			scrollQuestions.getVerticalScrollBar().setOpaque(false);
			scrollQuestions.getVerticalScrollBar().setUnitIncrement(8);
		
			
			//adds the components to this ScorePanel
			this.add(title);
			this.add(dateLabel);
			this.add(durationLabel);
			this.add(scoreLabel);
			this.add(exportButton);
			this.add(exportLabel);
			this.add(exitButton);
			this.add(exitLabel);
			this.add(scrollQuestions);
			
			
			//sets the position of the title
			scoreLayout.putConstraint(SpringLayout.NORTH, title, 20, SpringLayout.NORTH, this);
			scoreLayout.putConstraint(SpringLayout.EAST, title, -300, SpringLayout.EAST, this);
			scoreLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, title, 0, SpringLayout.HORIZONTAL_CENTER, this);
			scoreLayout.putConstraint(SpringLayout.WEST, title, 300, SpringLayout.WEST, this);
			scoreLayout.putConstraint(SpringLayout.SOUTH, title, 70, SpringLayout.NORTH, this);
			
			//sets the position of the report's date
			scoreLayout.putConstraint(SpringLayout.NORTH, dateLabel, 15, SpringLayout.SOUTH, title);
			scoreLayout.putConstraint(SpringLayout.EAST, dateLabel, -100, SpringLayout.EAST, this);
			scoreLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, dateLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);
			scoreLayout.putConstraint(SpringLayout.WEST, dateLabel, 100, SpringLayout.WEST, this);
			scoreLayout.putConstraint(SpringLayout.SOUTH, dateLabel, 30, SpringLayout.NORTH, dateLabel);
			
			//sets the position of the Quiz's duration label
			scoreLayout.putConstraint(SpringLayout.NORTH, durationLabel, -175, SpringLayout.VERTICAL_CENTER, this);
			scoreLayout.putConstraint(SpringLayout.EAST, durationLabel, 60, SpringLayout.HORIZONTAL_CENTER, this);
			scoreLayout.putConstraint(SpringLayout.WEST, durationLabel, 10, SpringLayout.WEST, this);
			scoreLayout.putConstraint(SpringLayout.SOUTH, durationLabel, 30, SpringLayout.NORTH, durationLabel);
			
			//sets the position of the report's score label
			scoreLayout.putConstraint(SpringLayout.NORTH, scoreLabel, -175, SpringLayout.VERTICAL_CENTER, this);
			scoreLayout.putConstraint(SpringLayout.EAST, scoreLabel, 0, SpringLayout.EAST, this);
			scoreLayout.putConstraint(SpringLayout.WEST, scoreLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);
			scoreLayout.putConstraint(SpringLayout.SOUTH, scoreLabel, 30, SpringLayout.NORTH, scoreLabel);
			
			//sets the position of the export button
			scoreLayout.putConstraint(SpringLayout.NORTH, exportButton, 25, SpringLayout.NORTH, this);
			scoreLayout.putConstraint(SpringLayout.EAST, exportButton, -25, SpringLayout.EAST, this);
			scoreLayout.putConstraint(SpringLayout.WEST, exportButton, -120, SpringLayout.EAST, exportButton);
			scoreLayout.putConstraint(SpringLayout.SOUTH, exportButton, 120, SpringLayout.NORTH, exportButton);
			
			//sets the position of the export button's description
			scoreLayout.putConstraint(SpringLayout.NORTH, exportLabel, 30, SpringLayout.NORTH, exportButton);
			scoreLayout.putConstraint(SpringLayout.EAST, exportLabel, 0, SpringLayout.WEST, exportButton);
			scoreLayout.putConstraint(SpringLayout.WEST, exportLabel, 50, SpringLayout.EAST, title);
			scoreLayout.putConstraint(SpringLayout.SOUTH, exportLabel, 0, SpringLayout.SOUTH, exportButton);
			
			//sets the position of the exit button
			scoreLayout.putConstraint(SpringLayout.NORTH, exitButton, 40, SpringLayout.NORTH, this);
			scoreLayout.putConstraint(SpringLayout.WEST, exitButton, 25, SpringLayout.WEST, this);
			scoreLayout.putConstraint(SpringLayout.EAST, exitButton, 120, SpringLayout.WEST, exitButton);
			scoreLayout.putConstraint(SpringLayout.SOUTH, exitButton, 120, SpringLayout.NORTH, exitButton);
			
			//sets the position of the exit button's description
			scoreLayout.putConstraint(SpringLayout.NORTH, exitLabel, 0, SpringLayout.NORTH, exitButton);
			scoreLayout.putConstraint(SpringLayout.WEST, exitLabel, 5, SpringLayout.EAST, exitButton);
			scoreLayout.putConstraint(SpringLayout.EAST, exitLabel, -60, SpringLayout.WEST, title);
			scoreLayout.putConstraint(SpringLayout.SOUTH, exitLabel, -10, SpringLayout.SOUTH, exitButton);
			
			//sets the position of the panel with the questions
			scoreLayout.putConstraint(SpringLayout.NORTH, scrollQuestions, 34, SpringLayout.SOUTH, scoreLabel);
			scoreLayout.putConstraint(SpringLayout.EAST, scrollQuestions, 0, SpringLayout.EAST, this);
			scoreLayout.putConstraint(SpringLayout.WEST, scrollQuestions, 0, SpringLayout.WEST, this);
			scoreLayout.putConstraint(SpringLayout.SOUTH, scrollQuestions, 0, SpringLayout.SOUTH, this);
		}
		
		
		//--------------Methods-----------
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//triggered to start the program
			if (e.getSource() == showPanelDelay) {
				title.fadeIn();
				
				//sets up the next Timer to trigger the displaying of the metadata in 650 milliseconds
				metadataDelay = new Timer(650, ScorePanel.this);
				metadataDelay.start();
				metadataDelay.setRepeats(false);
			}
			
			//triggered when the second phase, the displaying of the metadata, is performed
			if (e.getSource() == metadataDelay) {
				dateLabel.fadeIn();
				durationLabel.fadeIn();
				scoreLabel.fadeIn();
				
				//fades in the buttons
				exportButton.fadeIn();
				exitButton.fadeIn();
				
				//sets up the next Timer to trigger the displaying of the dividing line in 600 milliseconds
				lineDelay = new Timer(800, ScorePanel.this);
				lineDelay.start();
				lineDelay.setRepeats(false);
				
				//sets up the next Timer to trigger the displaying of the questions' panel in 1250 milliseconds
				questionsDelay = new Timer(1500, ScorePanel.this);
				questionsDelay.start();
				questionsDelay.setRepeats(false);
			}
			
			//triggered when the third phase, the dividing line being shown, is performed
			if (e.getSource() == lineDelay)
				animateLine();
			
			//triggered when the fourth phase, the displaying of the questions, is performed
			if (e.getSource() == questionsDelay) {
				
				//show the questionsPanel
				questionsPanel.setVisible(true);
				
				//fades in the question JLabels first
				for (int i = 0; i < questionLabels.size(); i++) {
					questionLabels.get(i).fadeIn();
				}
				
				//then fades in the rest after a 750 milliseconds delay
				Timer delayIconFade = new Timer(750, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						
						//fades in each of the correct/incorrect icons
						for (int i = 0; i < questionIcons.size(); i++) {
							questionIcons.get(i).fadeIn();
						}
						//fades in the scrollbar
						((CustomScrollUI) scrollQuestions.getVerticalScrollBar().getUI()).fadeIn();
						
					}
					
				});
				delayIconFade.start();
				delayIconFade.setRepeats(false);
			}
		}
		
		
		@Override
		public void paintComponent(Graphics g) {
			
			Graphics2D g2 = (Graphics2D) g;
			
			//prioritizes render quality
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); 
			
			//to paint the dividing line
			g2.setColor(new Color(25,25,25));
			g2.fillRect(0, this.getHeight() / 2 - 125, lineWidth, 15);
			
			//paints the rest of the background transparent afterwards
			super.paintComponent(g);
		}
		
		
		/**
		 * Animates the dividing line between the report's metadata and the question data
		 */
		public void animateLine() {
			
			//triggers a chage in the line's position every 16 milliseconds
			Timer animateLine = new Timer(30, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					//change the line's width by 50 pixels
					lineWidth += 50;
					repaint();
					
					//if the line is the width of the window, stop changing its width
					if (lineWidth >= ScorePanel.this.getWidth()) {
						((Timer)(e.getSource())).stop();
					}
				}
			});
			animateLine.start();
			animateLine.setCoalesce(true);
			
		}
		
	}
	
	
	
	/**
	 * A custom JLabel whose alpha level (opacity) can be changed, so as to allow for animations such as fading in
	 * @author Varun Unnithan
	 *
	 */
	@SuppressWarnings("serial")
	public class FadingLabel extends JLabel {
		
		/** The current alpha level of the label, between 0f and 1f */
		private float alpha;
		
		/**
		 * Changes the alpha value of the JLabel
		 * @param alpha The new alpha value to set for the JLabel
		 */
		public void setAlpha(float alpha) {
			
			//updates the alpha value
			firePropertyChange("alpha", this.alpha, alpha);
			this.alpha = alpha;
			repaint();
		}
		
		/**
		 * Gets the current alpha level for the JLabel
		 * @return The current alpha value as a float
		 */
		public float getAlpha() {
			return alpha;
		}
		
		
		@Override
		public void paint(Graphics g) {
			
			Graphics2D g2d = (Graphics2D) g;
			
			//makes entire paint chain use this alpha value
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g2d);
            g2d.dispose();
		}
		
		
		/**
		 * If the JLabel is transparent, it will make the JLabel fade to full opacity
		 */
		public void fadeIn() {
			
			//fire an event to change the alpha every 16 milliseconds
			Timer fadeTimer = new Timer(16, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					//increase the alpha value
					setAlpha(alpha + .2f);
					
					//stop the Timer when the alpha equals 1
					if (alpha >= 1f) {
						setAlpha(1f);
						((Timer)(e.getSource())).stop();
					}
				}
				
			});
			fadeTimer.start();
			fadeTimer.setCoalesce(true);
		}
	}
	
	
	/**
	 * A custom button whose alpha level (opacity) can be changed, so as to allow for animations such as fading in
	 * @author Varun Unnithan
	 *
	 */
	@SuppressWarnings("serial")
	public class FadingButton extends JButton {
		
		/** The current alpha level of the label, between 0f and 1f */
		private float alpha;
		
		/**
		 * Changes the alpha value of the button
		 * @param alpha The new alpha value to set for the button
		 */
		public void setAlpha(float alpha) {
			
			//updates the alpha value
			firePropertyChange("alpha", this.alpha, alpha);
			this.alpha = alpha;
			repaint();
		}
		
		/**
		 * Gets the current alpha level for the button
		 * @return The current alpha value as a float
		 */
		public float getAlpha() {
			return alpha;
		}
		
		
		@Override
		public void paintComponent(Graphics g) {
			
			Graphics2D g2d = (Graphics2D) g;
			
			//makes entire paint chain use this alpha value
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2d);
            g2d.dispose();
		}
		
		
		/**
		 * If the button is transparent, it will make the button fade to full opacity
		 */
		public void fadeIn() {
			
			//fire an event to change the alpha every 16 milliseconds
			Timer fadeTimer = new Timer(16, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					//increase the alpha value
					setAlpha(alpha + .2f);
					
					//stop the Timer when the alpha equals 1
					if (alpha >= 1f) {
						setAlpha(1f);
						((Timer)(e.getSource())).stop();
					}
				}
				
			});
			fadeTimer.start();
			fadeTimer.setCoalesce(true);
		}
	}
	
}
	

