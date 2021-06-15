package FBLAQuiz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * <h1>Quiz Menu Class</h1>
 * 
 * The Quiz Menu class creates the home screen for the program, along with implementing methods for the showing
 * of previous score reports and the database's questions.
 * 
 * @author Varun Unnithan
 *
 */
public class QuizMenu extends MouseAdapter implements ActionListener{

	/** Global parser used to parse the JSON databases */
	public static JSONParser PARSER = new JSONParser();
	
	//----------------Instance Variables----------
	/** The JFrame on which the program will display its GUI */
	private JFrame frame;
	/** The label to show the descriptions for each home menu button */
	private JLabel buttonDesc;
	/** The button to start the quiz */
	private JButton startButton;
	/** The button to view all score reports */
	private JButton scoresButton;
	/** The button to view the database's questions */
	private JButton questionsButton;
	/** The Quiz object to show if the button to start the quiz is clicked */
	private Quiz quiz;
	/** The panel to display the contents of the home screen */
	private GradientPanel homePanel;
	
	
	//-----------------Constructor---------------
	/**
	 * Creates a QuizMenu object, which sets up the home screen for the program
	 */
	public QuizMenu(){
		
		//create the JFrame for the program
		frame = new JFrame("FBLA Quiz");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				
				//when the window is closed, update the backup database for score reports
				try {
					FileWriter backupReportFile = new FileWriter("./JSONfiles/backup/scoreReports.json");
					backupReportFile.write(Files.readString(Path.of("./JSONfiles/scoreReports.json")));
					backupReportFile.close();
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}


				//exit the program
				System.exit(0);
			}
		});
		frame.setSize(900,750);
		
		//creates and displays the panel for the home screen onto the window
		showHomeScreen();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setIconImage((new ImageIcon("./Icons/FBLA Quiz Icon.png")).getImage());
		
		
	}
	
	
	//---------------Methods-------------
	/**
	 * Method to set the window to show the home menu screen
	 */
	public void showHomeScreen() {
		
		//clear the frame
		frame.getContentPane().removeAll();
		frame.repaint();
		frame.revalidate();
		
		//create and display home screen
		homePanel = new GradientPanel(1);
		frame.add(homePanel);
		frame.repaint();
		frame.revalidate();
		homePanel.animateGradient();
	}
	
	
	/**
	 * Displays the screen of all previous score report, called
	 * when the view scores button is clicked
	 * @throws IOException On input error
	 * @throws ParseException On error while parsing the database
	 * @throws FileNotFoundException On failure to find database file path
	 */
	@SuppressWarnings("serial")
	public void showScoresScreen() throws FileNotFoundException, IOException, ParseException {
		
		//clear the frame
		frame.getContentPane().removeAll();
		frame.repaint();
		frame.revalidate();
		
		//create the base panel
		GradientPanel listScoresPanel = new GradientPanel();
		SpringLayout scoresLayout = new SpringLayout();
		listScoresPanel.setLayout(scoresLayout);
		
		//create the label for the directions
		JLabel directions = new JLabel();
		directions.setText("<html><p style=\"text-align: center;\">Click on a previous quiz to see its specific score report.</p></html>");
		directions.setForeground(new Color(25,25,25));
		directions.setFont(new Font("Trebuchet MS", Font.ITALIC, 35));
		directions.setHorizontalAlignment(JLabel.CENTER);
		
		//Parse the score reports database to create the list of score reports
		JSONArray database;
		try {
			database = (JSONArray)(PARSER.parse(new FileReader("./JSONfiles/scoreReports.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			database = (JSONArray)(PARSER.parse(new FileReader("./JSONfiles/backup/scoreReports.json")));
		}
		int numberOfReports = database.size();
		
		//create the panel where the score reports will be displayed and sets its preferred size
		JPanel reportsPanel = new JPanel();
		reportsPanel.setPreferredSize(new Dimension(900, 70 + 58 * (numberOfReports - 1)));
		reportsPanel.setOpaque(false);
		SpringLayout scrollLayout = new SpringLayout();
		reportsPanel.setLayout(scrollLayout);
		
		
		//creates the label to display the description for the buttons
		JLabel scoreButtonsDesc = new JLabel();
		scoreButtonsDesc.setFont(new Font("Consolas", Font.PLAIN, 22));
		scoreButtonsDesc.setForeground(Color.LIGHT_GRAY);
		scoreButtonsDesc.setText("");
		scoreButtonsDesc.setHorizontalAlignment(JLabel.CENTER);
			
		//create the exit/return button
		JButton exitButton = new JButton();
		exitButton.setIcon(new ImageIcon("./Icons/Exit Icon Unselected.png"));
		exitButton.setPressedIcon(new ImageIcon("./Icons/Exit Icon Selected.png"));
		exitButton.setRolloverIcon(new ImageIcon("./Icons/Exit Icon Rollover.png"));
		exitButton.setOpaque(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setBorderPainted(false);
		exitButton.setFocusable(false);
		exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHomeScreen();
			}	
		});
		
		//adds the mouse listeners for when to display the button's label
		exitButton.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 */
			public void mouseEntered(MouseEvent e) {
				//when hovering over the exit button, display its description
				scoreButtonsDesc.setText("Return to the home screen");
			}
			/**
			 * {@inheritDoc}
			 */
			public void mouseExited(MouseEvent e) {
				//when exiting the button, hide the description
				scoreButtonsDesc.setText("");
			}
		});
		
		
		//an ArrayList for all of the report buttons
		ArrayList<JButton> buttonsList = new ArrayList<JButton>();
		
		//iterate through and display buttons for the score reports 
		for (int i = 0; i < numberOfReports; i++) {
			
			//gets the report's data
			JSONObject reportObj = (JSONObject) database.get(i);
			String dateAndTime = (String) reportObj.get("date");
			int score = (int) (long) reportObj.get("score");
			
			//sets up the report's button
			JButton reportButton = new JButton() {
				@Override
	            public void paintComponent(Graphics g)
	            {
					//paints the backgrund to write on top of it
	                super.paintComponent(g);
	                
	                Graphics2D g2 = (Graphics2D) g;
	                g2.setFont(new Font("Trebuchet MS", Font.PLAIN, 30));
	                g2.setColor(new Color(25,25,25));
	                //turns on anti-aliasing for the text
	    			RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
	    		            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    			g2.setRenderingHints(rh);
	                
	    			//sets the String to display, as the date and time + the score out of 5
	                String date = (dateAndTime.substring(0, 16)).trim() + " at " + ScoreReport.get12hrTime(dateAndTime);
	                g2.drawString(date, (int)(frame.getWidth() / 2.0 - 395), 40);
	                g2.drawString("Score: " + score + " / 5", (int)(frame.getWidth() / 2.0 + 130), 40);
	            }
			};
			//set up the rest of the button
			reportButton.setIcon(new ImageIcon("./Icons/Report Button.png"));
			reportButton.setPressedIcon(new ImageIcon("./Icons/Report Button Selected.png"));
			reportButton.setRolloverIcon(new ImageIcon("./Icons/Report Button Rollover.png"));
			reportButton.setContentAreaFilled(false);
			reportButton.setBorderPainted(false);
			reportButton.setFocusable(false);
			reportButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			//make the button show the specific score report when clicked
			reportButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					//clear the panel of other components
					listScoresPanel.removeAll();
					listScoresPanel.revalidate();
					listScoresPanel.repaint();
					
					//create the action to return to the screen of listing all score reports
					ActionListener returnAction = new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								showScoresScreen();
							} catch (IOException | ParseException e1) {}
						}
					};
					
					//creates a ScoreReport and a panel for it for the report that is clicked
					ScoreReport thisReport = new ScoreReport(reportObj);
					JPanel scorePanel = thisReport.createPanel(1000, listScoresPanel.getSize(), returnAction, 
							"Return to the previous page");
					
					//adds the score report panel to the overall panel
					listScoresPanel.stopAnimation();
					listScoresPanel.add(scorePanel);
					
					//set the score report panel position
					scoresLayout.putConstraint(SpringLayout.NORTH, scorePanel, 0, SpringLayout.NORTH, listScoresPanel);
					scoresLayout.putConstraint(SpringLayout.EAST, scorePanel, 0, SpringLayout.EAST, listScoresPanel);
					scoresLayout.putConstraint(SpringLayout.WEST, scorePanel, 0, SpringLayout.WEST, listScoresPanel);
					scoresLayout.putConstraint(SpringLayout.SOUTH, scorePanel, 0, SpringLayout.SOUTH, listScoresPanel);
				}
				
			});
			
			//add the button to the reports subpanel and the ArrayList
			reportsPanel.add(reportButton);
			buttonsList.add(reportButton);
			
			//set the position of the button
			//set the position dependent on if it is the first button or not
			if (i == 0) 
				scrollLayout.putConstraint(SpringLayout.NORTH, reportButton, 5, SpringLayout.NORTH, reportsPanel);
			else
				scrollLayout.putConstraint(SpringLayout.NORTH, reportButton, -2, SpringLayout.SOUTH, buttonsList.get(i-1));
			scrollLayout.putConstraint(SpringLayout.EAST, reportButton, -40, SpringLayout.EAST, reportsPanel);
			scrollLayout.putConstraint(SpringLayout.WEST, reportButton, 40, SpringLayout.WEST, reportsPanel);
			scrollLayout.putConstraint(SpringLayout.SOUTH, reportButton, 60, SpringLayout.NORTH, reportButton);
		}
		
		//sets up the scroll pane for the reports list
		JScrollPane scrollReports = new JScrollPane(reportsPanel) ;
		scrollReports.setOpaque(false);
		scrollReports.getViewport().setOpaque(false);
		scrollReports.setBorder(BorderFactory.createEmptyBorder());
		scrollReports.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  
		scrollReports.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//modifies the scroll bar itself, and sets it to the custom UI
		scrollReports.getVerticalScrollBar().setUI(new CustomScrollUI());
		((CustomScrollUI) scrollReports.getVerticalScrollBar().getUI()).fadeIn();
		scrollReports.getVerticalScrollBar().setOpaque(false);
		scrollReports.getVerticalScrollBar().setUnitIncrement(8);
		
		//create the button for viewing the graph
		JButton graphButton = new JButton();
		graphButton.setIcon(new ImageIcon("./Icons/Graph Icon.png"));
		graphButton.setPressedIcon(new ImageIcon("./Icons/Graph Icon Selected.png"));
		graphButton.setRolloverIcon(new ImageIcon("./Icons/Graph Icon Rollover.png"));
		graphButton.setOpaque(false);
		graphButton.setContentAreaFilled(false);
		graphButton.setBorderPainted(false);
		graphButton.setFocusable(false);
		graphButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		graphButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showScoresGraph();
				} catch (IOException | ParseException e1) {
					e1.printStackTrace();
				}
				
			}
		
		});
		
		//adds the mouse listeners for when to display the button's label
		graphButton.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 */
			public void mouseEntered(MouseEvent e) {
				//when hovering over the exit button, display its description
				scoreButtonsDesc.setText("View a graph of the past 20 scores");
			}
			/**
			 * {@inheritDoc}
			 */
			public void mouseExited(MouseEvent e) {
				//when exiting the button, hide the description
				scoreButtonsDesc.setText("");
			}
		});
		
		//create a label for the score count
		JLabel reportCount = new JLabel();
		reportCount.setFont(new Font("Trebuchet MS", Font.PLAIN, 22));
		reportCount.setForeground(new Color(25,25,25));
		reportCount.setText(numberOfReports + " reports");
		reportCount.setHorizontalAlignment(JLabel.LEFT);
		
		//add the components to the overall panel
		listScoresPanel.add(directions);
		listScoresPanel.add(graphButton);
		listScoresPanel.add(exitButton);
		listScoresPanel.add(scoreButtonsDesc);
		listScoresPanel.add(scrollReports);
		listScoresPanel.add(reportCount);
		
		
		//set the position of the directions label
		scoresLayout.putConstraint(SpringLayout.NORTH, directions, 60, SpringLayout.NORTH, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.EAST, directions, -200, SpringLayout.EAST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.WEST, directions, 200, SpringLayout.WEST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.SOUTH, directions, 80, SpringLayout.NORTH, directions);
		
		//set the position of the graph button
		scoresLayout.putConstraint(SpringLayout.NORTH, graphButton, 25, SpringLayout.NORTH, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.EAST, graphButton, -30, SpringLayout.EAST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.WEST, graphButton, 20, SpringLayout.EAST, directions);
		scoresLayout.putConstraint(SpringLayout.SOUTH, graphButton, 120, SpringLayout.NORTH, directions);
		
		//set the position of the exit button
		scoresLayout.putConstraint(SpringLayout.NORTH, exitButton, 32, SpringLayout.NORTH, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.WEST, exitButton, 30, SpringLayout.WEST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.EAST, exitButton, -20, SpringLayout.WEST, directions);
		scoresLayout.putConstraint(SpringLayout.SOUTH, exitButton, 127, SpringLayout.NORTH, directions);
		
		//set the position of the button's description text
		scoresLayout.putConstraint(SpringLayout.NORTH, scoreButtonsDesc, 200, SpringLayout.NORTH, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.WEST, scoreButtonsDesc, 30, SpringLayout.WEST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.EAST, scoreButtonsDesc, -30, SpringLayout.EAST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.SOUTH, scoreButtonsDesc, 25, SpringLayout.NORTH, scoreButtonsDesc);
		
		//set the position of the scroll panel for all the reports
		scoresLayout.putConstraint(SpringLayout.NORTH, scrollReports, 225, SpringLayout.NORTH, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.EAST, scrollReports, 0, SpringLayout.EAST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.WEST, scrollReports, 0, SpringLayout.WEST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.SOUTH, scrollReports, 0, SpringLayout.SOUTH, listScoresPanel);
		
		//set the position of the report count
		scoresLayout.putConstraint(SpringLayout.NORTH, reportCount, 200, SpringLayout.NORTH, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.EAST, reportCount, 140, SpringLayout.WEST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.WEST, reportCount, 25, SpringLayout.WEST, listScoresPanel);
		scoresLayout.putConstraint(SpringLayout.SOUTH, reportCount, 25, SpringLayout.NORTH, reportCount);
		
		
		frame.add(listScoresPanel);
		frame.repaint();
		frame.revalidate();
		listScoresPanel.animateGradient();
	}
	
	/**
	 * Creates a graph to display the scores of the recent score reports
	 * @throws IOException On input error
	 * @throws ParseException On error while parsing the database
	 * @throws FileNotFoundException On failure to find database file path
	 */
	public void showScoresGraph() throws FileNotFoundException, IOException, ParseException {
		
		//Parse the score reports database
		JSONArray database;
		try {
			database = (JSONArray)(PARSER.parse(new FileReader("./JSONfiles/scoreReports.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			database = (JSONArray)(PARSER.parse(new FileReader("./JSONfiles/backup/scoreReports.json")));
		}
		
		//get the scores for the data points
		ArrayList<Double> scores = new ArrayList<Double>();
        for (int i = 19; i >= 0; i--) {
        	JSONObject reportObj = (JSONObject) database.get(i);
			int score = (int) (long) reportObj.get("score");
			scores.add((double) score);
        }
        
        UIManager.put("Panel.background", new Color(0x1088FF));
        
        //create the labels for the x-axis
        ArrayList<String> xLabels = new ArrayList<String>();
        for (int i = 19; i >= 0; i--) {
        	JSONObject reportObj = (JSONObject) database.get(i);
        	String date = (String) reportObj.get("shortened date");
        	xLabels.add(date.substring(4,5) + "/" + date.substring(0,2));
        }
        
        //create and add the panel to a frame
        ScoreGraph graphPanel = new ScoreGraph(scores, xLabels);
        graphPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("Graph of Previous Scores");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(graphPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
	}
	
	/**
	 * Creates and displays the UI for showing the questions and answers from
	 * the database
	 */
	protected void showQuestionTypes() {
		
		//clear the frame
		frame.getContentPane().removeAll();
		frame.repaint();
		frame.revalidate();
		
		//create the base panel
		GradientPanel viewQsPanel = new GradientPanel();
		SpringLayout qTypesLayout = new SpringLayout();
		viewQsPanel.setLayout(qTypesLayout);
		
		//sets up the button to view the mcq questions
		JButton mcqButton = new JButton();
		mcqButton.setIcon(new ImageIcon("./Icons/Multiple Choice Button.png"));
		mcqButton.setPressedIcon(new ImageIcon("./Icons/Multiple Choice Button Selected.png"));
		mcqButton.setRolloverIcon(new ImageIcon("./Icons/Multiple Choice Button Rollover.png"));
		mcqButton.setOpaque(false);
		mcqButton.setContentAreaFilled(false);
		mcqButton.setBorderPainted(false);
		mcqButton.setFocusable(false);
		mcqButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mcqButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showQuestions(1);
			}
		});

		//sets up the button to view the multiple select questions
		JButton selectButton = new JButton();
		selectButton.setIcon(new ImageIcon("./Icons/Multiple Select Button.png"));
		selectButton.setPressedIcon(new ImageIcon("./Icons/Multiple Select Button Selected.png"));
		selectButton.setRolloverIcon(new ImageIcon("./Icons/Multiple Select Button Rollover.png"));
		selectButton.setOpaque(false);
		selectButton.setContentAreaFilled(false);
		selectButton.setBorderPainted(false);
		selectButton.setFocusable(false);
		selectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showQuestions(2);
			}
		});

		//sets up the button to view the true or false questions
		JButton tOrFButton = new JButton();
		tOrFButton.setIcon(new ImageIcon("./Icons/TorF Button.png"));
		tOrFButton.setPressedIcon(new ImageIcon("./Icons/TorF Button Selected.png"));
		tOrFButton.setRolloverIcon(new ImageIcon("./Icons/TorF Button Rollover.png"));
		tOrFButton.setOpaque(false);
		tOrFButton.setContentAreaFilled(false);
		tOrFButton.setBorderPainted(false);
		tOrFButton.setFocusable(false);
		tOrFButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		tOrFButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showQuestions(3);
			}
		});

		//sets up the button to view the fill in the blank questions
		JButton fillInButton = new JButton();
		fillInButton.setIcon(new ImageIcon("./Icons/Fill In Button.png"));
		fillInButton.setPressedIcon(new ImageIcon("./Icons/Fill In Button Selected.png"));
		fillInButton.setRolloverIcon(new ImageIcon("./Icons/Fill In Button Rollover.png"));
		fillInButton.setOpaque(false);
		fillInButton.setContentAreaFilled(false);
		fillInButton.setBorderPainted(false);
		fillInButton.setFocusable(false);
		fillInButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		fillInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showQuestions(4);
			}
		});

		//sets up the button to view the matching questions
		JButton matchingButton = new JButton();
		matchingButton.setIcon(new ImageIcon("./Icons/Matching Button.png"));
		matchingButton.setPressedIcon(new ImageIcon("./Icons/Matching Button Selected.png"));
		matchingButton.setRolloverIcon(new ImageIcon("./Icons/Matching Button Rollover.png"));
		matchingButton.setOpaque(false);
		matchingButton.setContentAreaFilled(false);
		matchingButton.setBorderPainted(false);
		matchingButton.setFocusable(false);
		matchingButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		matchingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showQuestions(5);
			}
		});
		
		//sets up the button to change the password
		JButton changePass = new JButton();
		changePass.setIcon(new ImageIcon("./Icons/Change Pass Button.png"));
		changePass.setPressedIcon(new ImageIcon("./Icons/Change Pass Button Selected.png"));
		changePass.setRolloverIcon(new ImageIcon("./Icons/Change Pass Button Rollover.png"));
		changePass.setOpaque(false);
		changePass.setContentAreaFilled(false);
		changePass.setBorderPainted(false);
		changePass.setFocusable(false);
		changePass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		changePass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				UIManager.put("OptionPane.background", new Color(25,25,25));
				UIManager.put("Panel.background", new Color(25,25,25));
				
				showPasswordPrompt("Please enter the new password", false);
			}
		});

		//sets up the label to provide instructions
		JLabel directions = new JLabel();
		directions.setText("<html><p style=\"text-align: center;\">Select the question type whose questions you want to "
				+ "view</p></html>");
		directions.setFont(new Font("Trebuchet MS", Font.BOLD + Font.ITALIC, 26));
		directions.setHorizontalAlignment(JLabel.CENTER);
		directions.setForeground(new Color(25,25,25));
		
		//creates the label to describe the exit button
		JLabel exitLabel = new JLabel();
		exitLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
		exitLabel.setForeground(Color.LIGHT_GRAY);
		exitLabel.setText("<html><p style=\"text-align: center;\">Return to the home screen</p></html>");
		exitLabel.setHorizontalAlignment(JLabel.CENTER);
		exitLabel.setVisible(false);
		
		//create the button to exit the question viewing panel
		JButton exitButton = new JButton();
		exitButton.setIcon(new ImageIcon("./Icons/Exit Icon Unselected.png"));
		exitButton.setPressedIcon(new ImageIcon("./Icons/Exit Icon Selected.png"));
		exitButton.setRolloverIcon(new ImageIcon("./Icons/Exit Icon Rollover.png"));
		exitButton.setOpaque(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setBorderPainted(false);
		exitButton.setFocusable(false);
		exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHomeScreen();
			}	
		});
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
		
		//add the components to the panel
		viewQsPanel.add(mcqButton);
		viewQsPanel.add(selectButton);
		viewQsPanel.add(tOrFButton);
		viewQsPanel.add(fillInButton);
		viewQsPanel.add(matchingButton);
		viewQsPanel.add(changePass);
		viewQsPanel.add(directions);
		viewQsPanel.add(exitButton);
		viewQsPanel.add(exitLabel);
		
		//set the position of each of the question buttons
		qTypesLayout.putConstraint(SpringLayout.NORTH, mcqButton, 110, SpringLayout.NORTH, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.EAST, mcqButton, -175, SpringLayout.EAST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.WEST, mcqButton, 175, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, mcqButton, 80, SpringLayout.NORTH, mcqButton);
		
		qTypesLayout.putConstraint(SpringLayout.NORTH, selectButton, 25, SpringLayout.SOUTH, mcqButton);
		qTypesLayout.putConstraint(SpringLayout.EAST, selectButton, -175, SpringLayout.EAST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.WEST, selectButton, 175, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, selectButton, 80, SpringLayout.NORTH, selectButton);
		
		qTypesLayout.putConstraint(SpringLayout.NORTH, tOrFButton, 25, SpringLayout.SOUTH, selectButton);
		qTypesLayout.putConstraint(SpringLayout.EAST, tOrFButton, -175, SpringLayout.EAST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.WEST, tOrFButton, 175, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, tOrFButton, 80, SpringLayout.NORTH, tOrFButton);

		qTypesLayout.putConstraint(SpringLayout.NORTH, fillInButton, 25, SpringLayout.SOUTH, tOrFButton);
		qTypesLayout.putConstraint(SpringLayout.EAST, fillInButton, -175, SpringLayout.EAST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.WEST, fillInButton, 175, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, fillInButton, 80, SpringLayout.NORTH, fillInButton);
		
		qTypesLayout.putConstraint(SpringLayout.NORTH, matchingButton, 25, SpringLayout.SOUTH, fillInButton);
		qTypesLayout.putConstraint(SpringLayout.EAST, matchingButton, -175, SpringLayout.EAST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.WEST, matchingButton, 175, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, matchingButton, 80, SpringLayout.NORTH, matchingButton);
		
		//set up the location of the button to change the password
		qTypesLayout.putConstraint(SpringLayout.EAST, changePass, 0, SpringLayout.EAST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.WEST, changePass, 0, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, changePass, 0, SpringLayout.SOUTH, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.NORTH, changePass, -60, SpringLayout.SOUTH, viewQsPanel);
		
		//set up the location of the label for instructions
		qTypesLayout.putConstraint(SpringLayout.EAST, directions, 0, SpringLayout.EAST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.WEST, directions, 0, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, directions, 0, SpringLayout.NORTH, mcqButton);
		qTypesLayout.putConstraint(SpringLayout.NORTH, directions, 0, SpringLayout.NORTH, viewQsPanel);
		
		//set up the location of the exit button
		qTypesLayout.putConstraint(SpringLayout.EAST, exitButton, -15, SpringLayout.WEST, matchingButton);
		qTypesLayout.putConstraint(SpringLayout.WEST, exitButton, 15, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, exitButton, -10, SpringLayout.NORTH, changePass);
		qTypesLayout.putConstraint(SpringLayout.NORTH, exitButton, -120, SpringLayout.SOUTH, exitButton);
		
		//set up the location of the exit button's label
		qTypesLayout.putConstraint(SpringLayout.EAST, exitLabel, -10, SpringLayout.WEST, matchingButton);
		qTypesLayout.putConstraint(SpringLayout.WEST, exitLabel, 15, SpringLayout.WEST, viewQsPanel);
		qTypesLayout.putConstraint(SpringLayout.SOUTH, exitLabel, 25, SpringLayout.NORTH, exitButton);
		qTypesLayout.putConstraint(SpringLayout.NORTH, exitLabel, -120, SpringLayout.SOUTH, exitLabel);
		
		//add panel to the frame
		frame.add(viewQsPanel);
		frame.repaint();
		frame.revalidate();
		viewQsPanel.animateGradient();
		
	}
	
	/**
	 * Shows all of the questions in the database for a given question type
	 * @param questionType An integer representing the type of question to display. The integer should be
	 * between 1-5, inclusive, with 1 being multiple choice, 2 is multiple select, 3 is true or false,
	 * 4 is fill in the blank, and 5 is matching.
	 */
	protected void showQuestions(int questionType) {
		
		//clear the frame
		frame.getContentPane().removeAll();
		frame.repaint();
		frame.revalidate();
	
		//create the base panel
		GradientPanel qPanel = new GradientPanel();
		SpringLayout qLayout = new SpringLayout();
		qPanel.setLayout(qLayout);
				
		//add button to return to previous page
		JButton returnButton = new JButton();
		returnButton.setIcon(new ImageIcon("./Icons/Return Page Button.png"));
		returnButton.setPressedIcon(new ImageIcon("./Icons/Return Page Button Selected.png"));
		returnButton.setRolloverIcon(new ImageIcon("./Icons/Return Page Button Rollover.png"));
		returnButton.setOpaque(false);
		returnButton.setContentAreaFilled(false);
		returnButton.setBorderPainted(false);
		returnButton.setFocusable(false);
		returnButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		returnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showQuestionTypes();
			}
		});
		
		
		//JPanel for questions
		JPanel questionsPanel = new JPanel();
		questionsPanel.setPreferredSize(new Dimension(900,3000));
		questionsPanel.setOpaque(false);
		SpringLayout questionsLayout = new SpringLayout();
		questionsPanel.setLayout(questionsLayout);
				
		
		//read and store the database
		JSONObject database = null;
		try {
			database = (JSONObject)(PARSER.parse(new FileReader("./JSONfiles/testQuestions.json")));
		} 
		catch (IOException | ParseException e) {
			//Use backup database if exception occurs
			try {
				database = (JSONObject)(PARSER.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
			} catch (IOException | ParseException e1) {}
		}
		
		//get the correct array of questions and set the panel size based on type
		JSONArray qArray = null;
		if (questionType == 1) {
			qArray = (JSONArray) database.get("mcq");
			questionsPanel.setPreferredSize(new Dimension(900,2800));
		}
		if (questionType == 2) {
			qArray = (JSONArray) database.get("Multiple Select");
			questionsPanel.setPreferredSize(new Dimension(900,2900));
		}
		if (questionType == 3) {
			qArray = (JSONArray) database.get("True or False");
			questionsPanel.setPreferredSize(new Dimension(900,1700));
		}
		if (questionType == 4) {
			qArray = (JSONArray) database.get("Fill-in Blank");
			questionsPanel.setPreferredSize(new Dimension(900,1700));
		}
		if (questionType == 5) {
			qArray = (JSONArray) database.get("Matching");
			questionsPanel.setPreferredSize(new Dimension(900,2500));
		}
		
		//ArrayList of all the labels for the questions
		ArrayList<JLabel> questionLabels = new ArrayList<JLabel>();
	
		for (int i = 0; i < qArray.size(); i++) {
			
			JSONObject questionObject = (JSONObject) qArray.get(i);
			
			//gets the text of the question and its number
			String questionText = "<html>" + (String) questionObject.get("question") + "</html>";
			
			//sets up the label for the question
			JLabel question = new JLabel();
			question.setText(questionText);
			question.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
			question.setForeground(new Color(25,25,25));
			question.setHorizontalAlignment(JLabel.LEFT);
			question.setOpaque(false);
			
			//adds all the labels to the ArrayList for them
			questionLabels.add(question);
			
			//adds the labels to the questionsPanel
			questionsPanel.add(question);
			
			//sets top position based on whether if it is the first question or not
			if (i == 0) {
				questionsLayout.putConstraint(SpringLayout.NORTH, question, 20, SpringLayout.NORTH, questionsPanel);
			}
			else {
				questionsLayout.putConstraint(SpringLayout.NORTH, question, 0, SpringLayout.SOUTH, questionLabels.get(i*3-2));
			}
			//sets the position of the question label
			questionsLayout.putConstraint(SpringLayout.EAST, question, -125, SpringLayout.EAST, questionsPanel);
			questionsLayout.putConstraint(SpringLayout.WEST, question, 25, SpringLayout.WEST, questionsPanel);
			questionsLayout.putConstraint(SpringLayout.SOUTH, question, questionText.length() > 75 ? 90 : 60, 
					SpringLayout.NORTH, question);			//set size based on the text's length
			
			
			//if the question type requires more than one line
			if (questionType == 1 || questionType == 2 || questionType == 5) {
				
				//get certain values and display different text if the question type is matching
				String dbKey1 = questionType == 5 ? "groupA" : "choices";
				String dbKey2 = questionType == 5 ? "groupB" : "answer";
				String group1Title = questionType == 5 ? "Group A" : "Choices";
				String group2Title = questionType == 5 ? "Group B" : "Correct Answer";
				
				//create ArrayList of all choices
				ArrayList<String> choicesArray = new ArrayList<String>();
				JSONArray choicesJSON = (JSONArray) questionObject.get(dbKey1);
				for (int i1 = 0; i1 < choicesJSON.size(); i1++) {
					choicesArray.add((String) choicesJSON.get(i1));
				}
				
				//gets and sets up the text of the user answer
				String choicesString = "<html><span style=\"text-decoration: underline;\"><strong>"
						+ group1Title + ":</strong></span>";
				
				for (int a = 0; a < choicesArray.size(); a++) {
					choicesString += "<br>" + choicesArray.get(a);
				}
				choicesString += "<html>";
				
				//gets and sets up the text of the correct answer
				String correctAnswerText = "<html><span style=\"text-decoration: underline;\"><strong>"
						+ group2Title + ":</strong></span>";
				
				//if multiple select, get an array of answers
				if (questionType == 2 || questionType == 5) {
					
					ArrayList<String> answer = new ArrayList<String>();
					JSONArray answersJSON = (JSONArray) questionObject.get(dbKey2);
					for (int i1 = 0; i1 < answersJSON.size(); i1++) {
						answer.add((String) answersJSON.get(i1));
					}
					
					//gets and formats each part of a correct answer
					for (int a = 0; a < answer.size(); a++) {
						correctAnswerText += "<br>" + answer.get(a);
					}	
					correctAnswerText += "<html>";
				}
				//if questionType is mcq have a single correct answer
				else {
					correctAnswerText += "<br>" + (String) questionObject.get("answer") + "<html>";
				}
				
				//sets up the label for the user answer
				JLabel choices = new JLabel();
				choices.setText(choicesString);
				choices.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
				choices.setForeground(Color.LIGHT_GRAY);
				choices.setHorizontalAlignment(JLabel.LEFT);
				choices.setVerticalAlignment(JLabel.TOP);
				choices.setOpaque(false);
				
				
				//sets up the label for the correct answer
				JLabel correctAnswer = new JLabel();
				correctAnswer.setText(correctAnswerText);
				correctAnswer.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
				correctAnswer.setForeground(Color.LIGHT_GRAY);
				correctAnswer.setHorizontalAlignment(JLabel.LEFT);
				correctAnswer.setVerticalAlignment(JLabel.TOP);
				correctAnswer.setOpaque(false);
				
				//adds all the labels to the ArrayList for them
				questionLabels.add(choices);
				questionLabels.add(correctAnswer);
				
				//adds the labels to the questionsPanel
				questionsPanel.add(choices);
				questionsPanel.add(correctAnswer);
				
				//sets the position of the choices label
				questionsLayout.putConstraint(SpringLayout.NORTH, choices, 20, SpringLayout.SOUTH, question);
				questionsLayout.putConstraint(SpringLayout.EAST, choices, 0, SpringLayout.HORIZONTAL_CENTER, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.WEST, choices, 40, SpringLayout.WEST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.SOUTH, choices, choicesString.length() > 200 ? 160 : 130, 
						SpringLayout.NORTH, choices);			//set size based on the text's length

				//sets the position of the correct answer label
				questionsLayout.putConstraint(SpringLayout.NORTH, correctAnswer, 20, SpringLayout.SOUTH, question);
				questionsLayout.putConstraint(SpringLayout.EAST, correctAnswer, -40, SpringLayout.EAST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.WEST, correctAnswer, 30, SpringLayout.HORIZONTAL_CENTER, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.SOUTH, correctAnswer, correctAnswerText.length() > 200 ? 160 : 120, 
						SpringLayout.NORTH, correctAnswer);			//set size based on the text's length
			}
			
			//if the question only requires one line
			else {
				
				//get the answer
				String correctAnswerText = "<html><p style=\"text-align: center;\"><span style=\"text-decoration: underline;\">"
						+ "<strong>Correct Answer:</strong></span>";
				correctAnswerText += " " + (String) questionObject.get("answer") + "</p><html>";
				
				//sets up the label for the correct answer
				JLabel correctAnswer = new JLabel();
				correctAnswer.setText(correctAnswerText);
				correctAnswer.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
				correctAnswer.setForeground(Color.LIGHT_GRAY);
				correctAnswer.setHorizontalAlignment(JLabel.CENTER);
				correctAnswer.setVerticalAlignment(JLabel.CENTER);
				correctAnswer.setOpaque(false);
				
				//update the ArrayList
				questionLabels.add(correctAnswer);
				questionLabels.add(null);
				
				//adds the label to the questionsPanel
				questionsPanel.add(correctAnswer);
				
				//sets the position of the correct answer label
				questionsLayout.putConstraint(SpringLayout.NORTH, correctAnswer, 20, SpringLayout.SOUTH, question);
				questionsLayout.putConstraint(SpringLayout.EAST, correctAnswer, -40, SpringLayout.EAST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.WEST, correctAnswer, 40, SpringLayout.WEST, questionsPanel);
				questionsLayout.putConstraint(SpringLayout.SOUTH, correctAnswer, 40, SpringLayout.NORTH, correctAnswer);
				
			}
			
		}
		

		//sets up the scroll pane for the questions
		JScrollPane scrollQuestions = new JScrollPane(questionsPanel) ;
		scrollQuestions.setOpaque(false);
		scrollQuestions.getViewport().setOpaque(false);
		scrollQuestions.setBorder(BorderFactory.createEmptyBorder());
		scrollQuestions.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  
		scrollQuestions.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//modifies the scroll bar itself, and sets it to the custom UI
		scrollQuestions.getVerticalScrollBar().setUI(new CustomScrollUI());
		scrollQuestions.getVerticalScrollBar().setOpaque(false);
		scrollQuestions.getVerticalScrollBar().setUnitIncrement(8);
		((CustomScrollUI) scrollQuestions.getVerticalScrollBar().getUI()).fadeIn();
		
		
		//add the components to the panel
		qPanel.add(returnButton);
		qPanel.add(scrollQuestions);
		
		//set up the location of the button to return
		qLayout.putConstraint(SpringLayout.EAST, returnButton, 0, SpringLayout.EAST, qPanel);
		qLayout.putConstraint(SpringLayout.WEST, returnButton, 0, SpringLayout.WEST, qPanel);
		qLayout.putConstraint(SpringLayout.NORTH, returnButton, 0, SpringLayout.NORTH, qPanel);
		qLayout.putConstraint(SpringLayout.SOUTH, returnButton, 60, SpringLayout.NORTH, qPanel);
		
		//set up location of questions panel
		qLayout.putConstraint(SpringLayout.EAST, scrollQuestions, 0, SpringLayout.EAST, qPanel);
		qLayout.putConstraint(SpringLayout.WEST, scrollQuestions, 0, SpringLayout.WEST, qPanel);
		qLayout.putConstraint(SpringLayout.SOUTH, scrollQuestions, 0, SpringLayout.SOUTH, qPanel);
		qLayout.putConstraint(SpringLayout.NORTH, scrollQuestions, 60, SpringLayout.NORTH, qPanel);
		
		
		//add panel to the frame
		frame.add(qPanel);
		frame.repaint();
		frame.revalidate();
				
	}
	
	/**
	 * Creates a dialog box to ask for the user to enter a password, either to verify the user or
	 * change the password
	 * @param message The message to display on the dialog box
	 * @param verifyPassword If this is true, it will verify this password with the correct one from the database.
	 * If this is false, the dialog box will act to change the password.
	 */
	protected void showPasswordPrompt(String message, boolean verifyPassword) {
		
		//read the password from the database
		JSONObject database = null;
		try {
			database = (JSONObject)(PARSER.parse(new FileReader("./JSONfiles/testQuestions.json")));
		} 
		catch (IOException | ParseException e1) {
			//Use backup database if exception occurs
			try {
				database = (JSONObject)(PARSER.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
			} catch (IOException | ParseException e2) {}
		}
		String correctPass = (String) database.get("password");
		
		JFrame passFrame = new JFrame("Enter the password");
		passFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//panel to place components on
		@SuppressWarnings("serial")
		JPanel passPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				
				//prioritize render quality
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				super.paintComponent(g);
			}
		};
		passPanel.setPreferredSize(new Dimension(350,150));
		passPanel.setBackground(new Color(25,25,25));
		SpringLayout passwordLayout = new SpringLayout();
		passPanel.setLayout(passwordLayout);
		
		//label to explain password field
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
		passwordLabel.setForeground(Color.LIGHT_GRAY);
		passwordLabel.setVerticalAlignment(JLabel.CENTER);
		
		//create teh field to enter the password
		JPasswordField passwordField = new JPasswordField();
		passwordField.setBackground(new Color(25,25,25));
		passwordField.setForeground(Color.LIGHT_GRAY);
		passwordField.setCaretColor(Color.LIGHT_GRAY);
		passwordField.setBorder(new LineBorder(Color.LIGHT_GRAY, 2, true));
		passwordField.setFont(new Font("Consolas", Font.PLAIN, 16));
		char defaultDot = passwordField.getEchoChar();
		
		//create a button to be able to view the password
		JToggleButton eyeButton = new JToggleButton();
		eyeButton.setIcon(new ImageIcon("./Icons/Eye Icon.png"));
		eyeButton.setSelectedIcon(new ImageIcon("./Icons/Eye Icon Selected.png"));
		eyeButton.setRolloverIcon(new ImageIcon("./Icons/Eye Icon Rollover.png"));
		eyeButton.setOpaque(false);
		eyeButton.setContentAreaFilled(false);
		eyeButton.setBorderPainted(false);
		eyeButton.setFocusable(false);
		eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		//make it so when the eye button is clicked, the password is revealed
		eyeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//depending on whether the button is selected or not, display it as a dot or letters
				if (((JToggleButton) (e.getSource())).isSelected()) {
					passwordField.setEchoChar((char) 0);
		        } else {
		        	passwordField.setEchoChar(defaultDot);
		        }
			}
		});
		
		//create the label for the introductory text
		JLabel messageLabel = new JLabel();
		messageLabel.setText("<html><p style=\"text-align: center;\">" + message + "</p></html>");
		messageLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 18));
		messageLabel.setForeground(Color.LIGHT_GRAY);
		
		//create the label for the warning text if the password is wrong
		JLabel incorrectPassLabel = new JLabel();
		incorrectPassLabel.setText("The password is incorrect. Please try again");
		incorrectPassLabel.setHorizontalAlignment(JLabel.CENTER);
		incorrectPassLabel.setFont(new Font("Karla", Font.PLAIN, 13));
		incorrectPassLabel.setForeground(Color.red);
		incorrectPassLabel.setVisible(false);
		
		//create the button to submit the password
		JButton enterButton = new JButton("Enter");
		enterButton.setFocusable(false);
		enterButton.setFocusPainted(false);
		enterButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		enterButton.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//if it is meant to vrify the password
				if (verifyPassword) {
					
					//if the password is right, move on, otherwise display the incorrect text
					if (String.valueOf(passwordField.getPassword()).equals(correctPass)) {
						showQuestionTypes();
						passFrame.dispose();
					}
					else {
						incorrectPassLabel.setVisible(true);
						passwordField.setText("");
					}
				}
				//if it is meant to update the password
				else {
					
					//read the database
					JSONObject database = null;
					try {
						database = (JSONObject)(PARSER.parse(new FileReader("./JSONfiles/testQuestions.json")));
					} 
					catch (IOException | ParseException e1) {
						//Use backup database if exception occurs
						try {
							database = (JSONObject)(PARSER.parse(new FileReader("./JSONfiles/backup/testQuestions.json")));
						} catch (IOException | ParseException e2) {}
					}
					
					//update the database with the password
					database.put("password", (String)(String.valueOf(passwordField.getPassword())));
					
					//write to output file
					try (Writer out = new FileWriter("./JSONfiles/testQuestions.json")) {
					    out.write(database.toJSONString());
					    incorrectPassLabel.setText("Password changed successfully");
					    incorrectPassLabel.setForeground(Color.green);
					    incorrectPassLabel.setVisible(true);
					} catch (IOException e1) {}
				}
			}
		});
		
		//add the components to the frame
		passPanel.add(passwordLabel);
		passPanel.add(passwordField);
		passPanel.add(eyeButton);
		passPanel.add(enterButton);
		passPanel.add(messageLabel);
		passPanel.add(incorrectPassLabel);
		
		//set the position of the password label and field
		passwordLayout.putConstraint(SpringLayout.NORTH, passwordLabel, 75, SpringLayout.NORTH, passPanel);
		passwordLayout.putConstraint(SpringLayout.EAST, passwordLabel, 125, SpringLayout.WEST, passPanel);
		passwordLayout.putConstraint(SpringLayout.WEST, passwordLabel, 10, SpringLayout.WEST, passPanel);
		passwordLayout.putConstraint(SpringLayout.SOUTH, passwordLabel, -40, SpringLayout.SOUTH, passPanel);
		
		passwordLayout.putConstraint(SpringLayout.NORTH, passwordField, 75, SpringLayout.NORTH, passPanel);
		passwordLayout.putConstraint(SpringLayout.EAST, passwordField, -50, SpringLayout.EAST, passPanel);
		passwordLayout.putConstraint(SpringLayout.WEST, passwordField, 110, SpringLayout.WEST, passPanel);
		passwordLayout.putConstraint(SpringLayout.SOUTH, passwordField, -45, SpringLayout.SOUTH, passPanel);
		
		//sets the position of the button to view the password
		passwordLayout.putConstraint(SpringLayout.NORTH, eyeButton, 75, SpringLayout.NORTH, passPanel);
		passwordLayout.putConstraint(SpringLayout.EAST, eyeButton, -5, SpringLayout.EAST, passPanel);
		passwordLayout.putConstraint(SpringLayout.WEST, eyeButton, 8, SpringLayout.EAST, passwordField);
		passwordLayout.putConstraint(SpringLayout.SOUTH, eyeButton, -40, SpringLayout.SOUTH, passPanel);
		
		//sets the position of the button to view the password
		passwordLayout.putConstraint(SpringLayout.SOUTH, enterButton, -10, SpringLayout.SOUTH, passPanel);
		passwordLayout.putConstraint(SpringLayout.EAST, enterButton, -140, SpringLayout.EAST, passPanel);
		passwordLayout.putConstraint(SpringLayout.WEST, enterButton, 140, SpringLayout.WEST, passPanel);
		passwordLayout.putConstraint(SpringLayout.NORTH, enterButton, -35, SpringLayout.SOUTH, passPanel);
		
		//set the position of the intro and warning messages
		passwordLayout.putConstraint(SpringLayout.SOUTH, messageLabel, -20, SpringLayout.NORTH, passwordField);
		passwordLayout.putConstraint(SpringLayout.EAST, messageLabel, -10, SpringLayout.EAST, passPanel);
		passwordLayout.putConstraint(SpringLayout.WEST, messageLabel, 10, SpringLayout.WEST, passPanel);
		passwordLayout.putConstraint(SpringLayout.NORTH, messageLabel, 6, SpringLayout.NORTH, passPanel);
		
		passwordLayout.putConstraint(SpringLayout.SOUTH, incorrectPassLabel, -5, SpringLayout.NORTH, passwordField);
		passwordLayout.putConstraint(SpringLayout.EAST, incorrectPassLabel, -10, SpringLayout.EAST, passPanel);
		passwordLayout.putConstraint(SpringLayout.WEST, incorrectPassLabel, 10, SpringLayout.WEST, passPanel);
		passwordLayout.putConstraint(SpringLayout.NORTH, incorrectPassLabel, -3, SpringLayout.SOUTH, messageLabel);
		
		
		//finishes the frame
		passFrame.add(passPanel);
		passFrame.pack();
		passFrame.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
		passFrame.setLocationRelativeTo(null);
		passFrame.setVisible(true);
		
	}
	
	//---------------Listeners--------------
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == startButton) {
			
			//create the quiz object
			try {
				quiz = new Quiz(frame, this);
			} catch (IOException | ParseException e1) {
				e1.printStackTrace();
			}
			
			//create the submit button for the dialog box
			JButton viewButton = new JButton("Start Quiz");
			viewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					homePanel.stopAnimation();
					frame.getContentPane().remove(homePanel);
					frame.repaint();
					frame.revalidate();
					
					frame.add(quiz);
					frame.repaint();
					frame.revalidate();
					quiz.startTimer();
					
					//close the dialog box
					JOptionPane.getRootFrame().dispose();
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
			dialogText.setText("<html><p style=\"text-align: center;\">Are you sure you want to start the quiz?</p>"
					+ "<p style=\"text-align: center;\">You cannot go back once you have started.</p></html>");
			
			//set the background color of dialog box
			UIManager.put("OptionPane.background", new Color(25,25,25));		
			UIManager.put("Panel.background", new Color(25,25,25));
			
			//create the dialog box
			Object[] buttons = {viewButton , closeButton};
			JOptionPane.showOptionDialog(null, dialogText, "Start Quiz",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, null);
			
		}
		
		//if the view scores button is clicked, show the scores screen
		if (e.getSource() == scoresButton) {
			
			try {
				showScoresScreen();
			} catch (IOException | ParseException e1) {
				e1.printStackTrace();
			}
		}
		
		//if the view questions button is clicked, request the password
		if (e.getSource() == questionsButton) {
			
			String message = "Please enter the administrative password to view the questions";
			
			//create the password box
			showPasswordPrompt(message, true);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void mouseEntered(MouseEvent e) {
		
		//when hovering over a button, display its description
		if (e.getSource() == startButton)
			buttonDesc.setText("Start the 5-question quiz");
		if (e.getSource() == scoresButton)
			buttonDesc.setText("View the score reports of previous quizzes");
		if (e.getSource() == questionsButton)
			buttonDesc.setText("View the database of all questions and answers");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void mouseExited(MouseEvent e) {
		//when exiting a button, hide the description
		buttonDesc.setText("");
	}
	
	
	//------------------Inner Class-----------
	/**
	 * A custom JPanel which has a gradient background which can be animated
	 * @author Varun Unnithan
	 */
	@SuppressWarnings("serial")
	private class GradientPanel extends JPanel {
		
		/** The point around which to center the panel's gradient */
		Point bluePoint, cyanPoint;
		/** The timer to animate the gradient of the panel */
		Timer rotateGradient;
		/** One of the colors of the gradient */
		Color darkColor, lighterColor;
		
		/**
		 * Creates a default GradientPanel object, without any components
		 */
		public GradientPanel() {
			cyanPoint = new Point(900, 750);
			bluePoint = new Point(10, 70);
			
			darkColor = new Color(0x0000AA);
			lighterColor = new Color(0x1088FF);
		}
		
		/**
		 * Creates a GradientPanel object, included with all the home menu's components
		 * @param any Passing any object or type causes the GradientPanel to act as a start menu
		 */
		public GradientPanel(Object any) {
			cyanPoint = new Point(900, 750);
			bluePoint = new Point(10, 70);
			
			darkColor = new Color(0x00007F);
			lighterColor = new Color(0x10AAFF);
			
			//create the JPanel which would hold the home screen
			SpringLayout homeLayout = new SpringLayout();
			this.setLayout(homeLayout);
			
			//sets up a label to show the "FBLA" part of the title
			JLabel fblaLabel = new JLabel();
			fblaLabel.setText("FBLA");
			fblaLabel.setFont(new Font("Trebuchet MS", Font.ITALIC + Font.BOLD, 100));
			fblaLabel.setForeground(new Color(25,25,25));
			
			//sets up a label to show the "quiz" part of the title
			JLabel quizLabel = new JLabel();
			quizLabel.setText("Quiz");
			quizLabel.setFont(new Font("Trebuchet MS", Font.ITALIC + Font.BOLD, 92));
			quizLabel.setForeground(new Color(25,25,25));
			
			//sets up the label description of the buttons
			buttonDesc = new JLabel();
			buttonDesc.setText("");
			buttonDesc.setFont(new Font("Consolas", Font.PLAIN, 20));
			buttonDesc.setForeground(Color.LIGHT_GRAY);
			buttonDesc.setHorizontalAlignment(JLabel.CENTER);
			
			//sets up the button to start the quiz
			startButton = new JButton();
			startButton.setIcon(new ImageIcon("./Icons/Start Quiz Button.png"));
			startButton.setPressedIcon(new ImageIcon("./Icons/Start Quiz Button Selected.png"));
			startButton.setRolloverIcon(new ImageIcon("./Icons/Start Quiz Button Rollover.png"));
			startButton.setOpaque(false);
			startButton.setContentAreaFilled(false);
			startButton.setBorderPainted(false);
			startButton.setFocusable(false);
			startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			startButton.addMouseListener(QuizMenu.this);
			startButton.addActionListener(QuizMenu.this);
			
			//sets up the button to view the scores
			scoresButton = new JButton();
			scoresButton.setIcon(new ImageIcon("./Icons/View Scores Button.png"));
			scoresButton.setPressedIcon(new ImageIcon("./Icons/View Scores Button Selected.png"));
			scoresButton.setRolloverIcon(new ImageIcon("./Icons/View Scores Button Rollover.png"));
			scoresButton.setOpaque(false);
			scoresButton.setContentAreaFilled(false);
			scoresButton.setBorderPainted(false);
			scoresButton.setFocusable(false);
			scoresButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			scoresButton.addMouseListener(QuizMenu.this);
			scoresButton.addActionListener(QuizMenu.this);
			
			//sets up the button to view the scores
			questionsButton = new JButton();
			questionsButton.setIcon(new ImageIcon("./Icons/View Questions Button.png"));
			questionsButton.setPressedIcon(new ImageIcon("./Icons/View Questions Button Selected.png"));
			questionsButton.setRolloverIcon(new ImageIcon("./Icons/View Questions Button Rollover.png"));
			questionsButton.setOpaque(false);
			questionsButton.setContentAreaFilled(false);
			questionsButton.setBorderPainted(false);
			questionsButton.setFocusable(false);
			questionsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			questionsButton.addMouseListener(QuizMenu.this);
			questionsButton.addActionListener(QuizMenu.this);
			
			
			//adds the components to the panel
			this.add(fblaLabel);
			this.add(quizLabel);
			this.add(startButton);
			this.add(scoresButton);
			this.add(questionsButton);
			this.add(buttonDesc);
			
			
			//sets the position of the labels for the title on the panel
			homeLayout.putConstraint(SpringLayout.NORTH, fblaLabel, 25, SpringLayout.NORTH, this);
			homeLayout.putConstraint(SpringLayout.EAST, fblaLabel, 90, SpringLayout.HORIZONTAL_CENTER, this);
			homeLayout.putConstraint(SpringLayout.WEST, fblaLabel, -275, SpringLayout.EAST, fblaLabel);
			homeLayout.putConstraint(SpringLayout.SOUTH, fblaLabel, 120, SpringLayout.NORTH, fblaLabel);
			
			homeLayout.putConstraint(SpringLayout.NORTH, quizLabel, -35, SpringLayout.SOUTH, fblaLabel);
			homeLayout.putConstraint(SpringLayout.EAST, quizLabel, -150, SpringLayout.EAST, this);
			homeLayout.putConstraint(SpringLayout.WEST, quizLabel, -100, SpringLayout.EAST, fblaLabel);
			homeLayout.putConstraint(SpringLayout.SOUTH, quizLabel, 120, SpringLayout.NORTH, quizLabel);
			
			//sets the position of the start quiz button on the panel
			homeLayout.putConstraint(SpringLayout.NORTH, startButton, -65, SpringLayout.VERTICAL_CENTER, this);
			homeLayout.putConstraint(SpringLayout.EAST, startButton, -175, SpringLayout.EAST, this);
			homeLayout.putConstraint(SpringLayout.WEST, startButton, 175, SpringLayout.WEST, this);
			homeLayout.putConstraint(SpringLayout.SOUTH, startButton, 15, SpringLayout.VERTICAL_CENTER, this);
			
			//sets the position of the view scores button on the panel
			homeLayout.putConstraint(SpringLayout.NORTH, scoresButton, 35, SpringLayout.SOUTH, startButton);
			homeLayout.putConstraint(SpringLayout.EAST, scoresButton, -175, SpringLayout.EAST, this);
			homeLayout.putConstraint(SpringLayout.WEST, scoresButton, 175, SpringLayout.WEST, this);
			homeLayout.putConstraint(SpringLayout.SOUTH, scoresButton, 80, SpringLayout.NORTH, scoresButton);
			
			//sets the position of the view questions button on the panel
			homeLayout.putConstraint(SpringLayout.NORTH, questionsButton, 35, SpringLayout.SOUTH, scoresButton);
			homeLayout.putConstraint(SpringLayout.EAST, questionsButton, -175, SpringLayout.EAST, this);
			homeLayout.putConstraint(SpringLayout.WEST, questionsButton, 175, SpringLayout.WEST, this);
			homeLayout.putConstraint(SpringLayout.SOUTH, questionsButton, 80, SpringLayout.NORTH, questionsButton);
			
			//sets the position of the button description label
			homeLayout.putConstraint(SpringLayout.SOUTH, buttonDesc, -10, SpringLayout.SOUTH, this);
			homeLayout.putConstraint(SpringLayout.NORTH, buttonDesc, -50, SpringLayout.SOUTH, buttonDesc);
			homeLayout.putConstraint(SpringLayout.EAST, buttonDesc, -175, SpringLayout.EAST, this);
			homeLayout.putConstraint(SpringLayout.WEST, buttonDesc, 175, SpringLayout.WEST, this);
			
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
	        //casts Graphics object to new Graphics2D object
			Graphics2D g2d = (Graphics2D) g;		
			//sets priority render quality
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); 	
	        
			//creates a gradient for the panel, with the colors being defined at the points
	        GradientPaint gp = new GradientPaint(bluePoint.x, bluePoint.y, darkColor, cyanPoint.x, cyanPoint.y, lighterColor);
	        g2d.setPaint(gp);
	        g2d.fillRect(0, 0, getWidth(), getHeight());
		}
		
		
		/**
		 * Animates the gradient of the panel to rotate
		 */
		public void animateGradient() {
			
			//rotate the gradient every 16 milliseconds
			rotateGradient = new Timer(16, new ActionListener() {

				int dy = 0;
				int dx = -10;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					//translate the points by the given amount
					bluePoint.translate(dx, dy);
					cyanPoint.translate(-1 * dx, -1 * dy);
					
					//set the amounts based on which side of the panel the point is "moving" upon and if it's out of bounds
					if ((bluePoint.x <= 0) && (dx < 0)) {
						dx = 0;
						dy = -10;
					}
					else if ((bluePoint.y <= 0) && (dy < 0)) {
						dx = 10;
						dy = 0;
					}
					else if ((bluePoint.x >= getWidth()) && (dx > 0)) {
						dx = 0;
						dy = 10;
					}
					else if ((bluePoint.y >= getHeight()) && (dy > 0)) {
						dx = -10;
						dy = 0;
						
					}
					
					//repaint the panel with the new graident points
					repaint();
					
				}
			});
			rotateGradient.start();

		}
		
		/**
		 * Stop the rotating gradient animation
		 */
		public void stopAnimation() {
			rotateGradient.stop();
		}
		
	}

	
	/**
	 * Creates a panel which contains a line graph for the scores
	 * @author Varun Unnithan
	 *
	 */
	@SuppressWarnings("serial")
	private class ScoreGraph extends JPanel {

		/** The size, in pixels, of the graph's right and top borders */
	    private int border = 25;
	    /** The size, in pixels, of the graph's left and bottom borders */
	    private int labelBorder = 50;
	    /** An ArrayList of all of the scores to plot, as a value from 0-5. These values should have 
	     * corresponding indices to that of the xLabels ArrayList and the list have a size of 20 */
	    private ArrayList<Double> scores;
	    /** An ArrayList of the x-axis labels, from left to right. These values should have corresponding
	     * indices to that of the xLabels ArrayList and the list have a size of 20 */
	    private ArrayList<String> xLabels;

	    /**
	     * Creates a ScoreGraph object
	     * @param scores An ArrayList of all data points (as a value of 0-5, inclusive) to plot. 
	     * This ArrayList should be 20 items long
	     * @param xLabels The labels of the x-axis, from left to right. This ArrayList should 
	     * be 20 items long
	     */
	    public ScoreGraph(ArrayList<Double> scores, ArrayList<String> xLabels) {
	        this.scores = scores;
	        this.xLabels = xLabels;
	        
	        //sets up the panel's layout manager
	        SpringLayout graphLayout = new SpringLayout();
	        this.setLayout(graphLayout);
	        
	        //creates the title for the x-axis
	        JLabel xTitle = new JLabel();
	        xTitle.setText("Date");
	        xTitle.setHorizontalAlignment(JLabel.CENTER);
	        xTitle.setFont(new Font("Consolas", Font.BOLD, 30));
	        xTitle.setForeground(new Color(25,25,25));
	        
	        //creates the title for the y-axis
	        JLabel yTitle = new JLabel() {
	        	@Override
	        	public void paintComponent(Graphics g) {
	        		
	        		//to rotate the jlabel
	        		Graphics2D g2 = (Graphics2D) g;
	        		g2.rotate(-1.5707, getX() + getWidth()/2, getY() + getHeight()/2);
	        		super.paintComponent(g);
	        	}
	        };
	        yTitle.setText("Score");
	        yTitle.setHorizontalAlignment(JLabel.CENTER);
	        yTitle.setVerticalAlignment(JLabel.CENTER);
	        yTitle.setFont(new Font("Consolas", Font.BOLD, 30));
	        yTitle.setForeground(new Color(25,25,25));
	        
	        //adds the components to the panel
	        this.add(yTitle);
	        this.add(xTitle);
	        
	        //set their positions
	        graphLayout.putConstraint(SpringLayout.SOUTH, xTitle, -10, SpringLayout.SOUTH, this);
	        graphLayout.putConstraint(SpringLayout.EAST, xTitle, -10, SpringLayout.EAST, this);
	        graphLayout.putConstraint(SpringLayout.WEST, xTitle, 60, SpringLayout.WEST, this);
	        graphLayout.putConstraint(SpringLayout.NORTH, xTitle, -30, SpringLayout.SOUTH, xTitle);
	        
	        //set their positions
	        graphLayout.putConstraint(SpringLayout.NORTH, yTitle, 10, SpringLayout.NORTH, this);
	        graphLayout.putConstraint(SpringLayout.EAST, yTitle, 90, SpringLayout.WEST, this);
	        graphLayout.putConstraint(SpringLayout.WEST, yTitle, 5, SpringLayout.WEST, this);
	        graphLayout.putConstraint(SpringLayout.SOUTH, yTitle, -85, SpringLayout.SOUTH, this);
	        
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	    	
	    	//create the graphics2d and set anti-aliasing on
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        //gets the sclae for the x and y
	        double xScale = ((double) getWidth() - 2 * border - labelBorder) / 19;
	        double yScale = ((double) getHeight() - 2 * border - labelBorder) / 5;

	        //creates an ArrayList of the points to plot
	        ArrayList<Point> graphPoints = new ArrayList<>();
	        for (int i = 0; i < scores.size(); i++) {
	            int x1 = (int) (i * xScale + border + labelBorder);
	            int y1 = (int) ((5 - scores.get(i)) * yScale + border);
	            graphPoints.add(new Point(x1, y1));
	        }

	        //draw graph background
	        g2.setColor(new Color(200,200,200));
	        g2.fillRect(border + labelBorder, border, getWidth() - (2 * border) - labelBorder, getHeight() - 2 * border - labelBorder);
	        g2.setColor(Color.BLACK);

	        //set the font
	        g2.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
	        
	        //for each y-axis value, draw its line and value
	        for (int i = 0; i < 6; i++) {
	        	
	        	//get the values for the cordinates
	            int x0 = border + labelBorder;
	            int yCord = getHeight() - ((i * (getHeight() - border * 2 - labelBorder)) / 5 + border + labelBorder);
	           
	            if (scores.size() > 0) {
	            	
	            	//draw the grid line
	                g2.setColor(new Color(220, 220, 220));
	                g2.drawLine(border + labelBorder + 1, yCord, getWidth() - border, yCord);
	                g2.setColor(Color.BLACK);
	                
	                //label the y-axis
	                String yLabel = i + "";
	                FontMetrics metrics = g2.getFontMetrics();
	                int labelWidth = metrics.stringWidth(yLabel);
	                g2.drawString(yLabel.equals("0") ? "" : yLabel, x0 - labelWidth - 5, yCord + (metrics.getHeight() / 2) - 3);
	            }
	        }

	        //set the font
	        g2.setFont(new Font("Trebuchet MS", Font.PLAIN, 11));
	        
	        //do the same for the x-axis
	        for (int i = 0; i < scores.size(); i++) {
	            if (scores.size() > 1) {
	                int xCord = i * (getWidth() - border * 2 - labelBorder) / 19 + border + labelBorder;
	                int yCord = getHeight() - border - labelBorder;
	                
	                if (xLabels.get(i) != null) {
	                	
	                	//draw grid lines
	                    g2.setColor(new Color(220, 220, 220));
	                    g2.drawLine(xCord, getHeight() - border - labelBorder - 1, xCord, border);
	                    g2.setColor(Color.BLACK);
	                    
	                    //draw the x-axis label
	                    String xLabel = xLabels.get(i);
	                    FontMetrics metrics = g2.getFontMetrics();
	                    int labelWidth = metrics.stringWidth(xLabel);
	                    g2.drawString(xLabel, xCord - labelWidth / 2, yCord + metrics.getHeight() + 3);
	                }
	                //draws the x-axis hatch marks
	                g2.drawLine(xCord, yCord + 4, xCord, yCord - 4);
	            }
	        }

	        //draw the actual axis for x and y
	        g2.drawLine(border + labelBorder, getHeight() - border - labelBorder, border + labelBorder, border);
	        g2.drawLine(border + labelBorder, getHeight() - border - labelBorder, getWidth() - border, getHeight() - border - labelBorder);

	        //actually draw the lines for each point in the ArrayList
	        g2.setColor(new Color(20, 52, 230));
	        g2.setStroke(new BasicStroke(2f));
	        for (int i = 0; i < graphPoints.size() - 1; i++) {
	            int x1 = graphPoints.get(i).x;
	            int y1 = graphPoints.get(i).y;
	            int x2 = graphPoints.get(i + 1).x;
	            int y2 = graphPoints.get(i + 1).y;
	            g2.drawLine(x1, y1, x2, y2);
	        }

	        
	    }
	}
}
