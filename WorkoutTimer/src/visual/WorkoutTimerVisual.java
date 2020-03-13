package visual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import code.Circuit;
import code.Exercise;

public class WorkoutTimerVisual {

	
	private static boolean jarFile = true;
	
	
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int OVERALL_HEIGHT =(int) (screenSize.getHeight()/1.25);
	public static final int OVERALL_WIDTH = (int) (screenSize.getWidth()/2);
	private static JFrame startScreen;
	private static JFrame secondScreen;
	private static MainPanel mainPanel;
	private static TimerMainPanel timerMain;
	private static Timer timer;
	private static JLabel count = new JLabel("Exercise count: ");
	private static int secondsElapsed;
	private static int currentExerciseTime;
	private static Exercise currExercise;
	private static int currExerciseNum;
	private static JTextArea exercisesPane = new JTextArea(
			(new String("Exercises will appear here. \nIf you wish to remove an exercise, \nthe remove button will remove the last exercise. \nPreviously saved: " +  getSavedExercises())));
	private static Circuit workoutCircuit = new Circuit();
	public static JTextField time = new JTextField();
	public static JTextField name = new JTextField();
	public static JTextField pos = new JTextField();
	
	// setting up JMenu
	public static JMenu menu = new JMenu("File");
	public static JMenuBar menuBar = new JMenuBar();
	
	
	
	// these appear in TimerMainPanel (screen 2)
	private static JLabel exerciseLbl = new JLabel("", SwingConstants.CENTER);
	private static JLabel timeLeftLbl = new JLabel("0", SwingConstants.CENTER);
	private static JMenuItem saveItem = new JMenuItem("Save",KeyEvent.VK_T);
	private static JMenuItem openItem = new JMenuItem("Open",KeyEvent.VK_T);

	
	
	
	// set up the sounds
	private static boolean alreadyPlayingAudio;
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setUpAndDrawGui();
			}

		});
	}
	
	
	
	private static boolean readFile(String fileName) {
		File file = new File("");
		String path = file.getAbsolutePath() + "/src/saved_exercises/" + fileName;
		System.out.println(path);
		if (jarFile) {
			File jarFile1 = null;
			try {
				jarFile1 = new File(WorkoutTimerVisual.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			} catch (URISyntaxException e) {
				
			}
			file = new File(jarFile1.getParentFile().getAbsolutePath());
			path = file.getPath() + "/saved_exercises/" + fileName;
			//exercisesPane.setText(path); used to see path when loaded
		}


		file = new File(path);
		exercisesPane.setText(file.getPath());
		Circuit circuit = new Circuit();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file.getPath()));
			String line = reader.readLine();
			String line2 = reader.readLine();
			int counter = 1;
			while (line != null && line != "") {
				int length = 0;
				String name = "";
				name = line;
				try {
					length = Integer.parseInt(line2);
				} catch (Exception e) {
					length = 0;
				}

				circuit.addExercise(new Exercise(name, length, counter));
				line = reader.readLine();
				line2 = reader.readLine();

			}

		} catch (IOException e) {
			return false;
		}
		workoutCircuit = circuit;
		try {
			reader.close();
		} catch (IOException e) {
			return false;
		}


		return true;
	}
	
	private static String getSavedExercises() { // doesn't work for jar file
		String retString = "";
		File file;
		String path;
		file = new File("");
		if (jarFile) {
			File jarFile1 = null;
			try {
				jarFile1 = new File(WorkoutTimerVisual.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			} catch (URISyntaxException e) {
				
			}
			file = new File(jarFile1.getParentFile().getAbsolutePath());
			path = file.getPath() + "/saved_exercises";
		} else {
			path = file.getAbsolutePath() + "/src/saved_exercises";			
		}
		
		file = new File(path);
		File[] directoryListing = file.listFiles();
		try {
			int counter = 1;
			for (File f : directoryListing) {
				if (counter++ >= 3) {
					retString += "\n";
				}
				retString += f.getName() + ", ";
			}
		} catch(NullPointerException e) {
			return "";
		}

		return retString.substring(0, retString.length()-2);
	}
	
	public static void playAudio(String nameOfAudioFile) {
		File audioFile = null;
		String path;
		if (jarFile) {
			File jarFile1 = null;
			try {
				jarFile1 = new File(WorkoutTimerVisual.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			} catch (URISyntaxException e) {
				
			}
			audioFile = new File(jarFile1.getParentFile().getAbsolutePath());
			path = audioFile.getPath() + "/audio/";
			audioFile = new File(path);
			
		} else {
			try {
				File currPath = new File("");
				audioFile = new File(currPath.getAbsolutePath() + "/src/audio/" + nameOfAudioFile);
			} catch (Exception e) {

			}
		}
		try {
			InputStream sound = new FileInputStream(audioFile);
			AudioInputStream soundStream = AudioSystem.getAudioInputStream(audioFile);
			Clip clip;
			clip = AudioSystem.getClip();
			clip.start();
			clip.wait(3000);
			clip.close();
			alreadyPlayingAudio = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static class MainPanel extends JPanel {
		public MainPanel() {
			this.setLayout(new GridLayout(4,1));
			
			
			saveItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_S, ActionEvent.CTRL_MASK));
				saveItem.getAccessibleContext().setAccessibleDescription(
					"Save the currrent workout");
			openItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_O, ActionEvent.CTRL_MASK));
				saveItem.getAccessibleContext().setAccessibleDescription(
					"Open an old workout");
			menu.add(openItem);
			menu.add(saveItem);
			
			saveItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new File("");
					String path;
					if (jarFile) {
						File jarFile1 = null;
						try {
							jarFile1 = new File(WorkoutTimerVisual.class.getProtectionDomain().getCodeSource().getLocation().toURI());
						} catch (URISyntaxException uri) {
							
						}
						file = new File(jarFile1.getParentFile().getAbsolutePath());
						path = file.getPath() + "/saved_exercises";
						file = new File(path);
					} else {
						file = new File("");
						path = file.getAbsolutePath();
						file = new File(path + "/src/saved_exercises");
					}
					if (!file.isDirectory()) {
						file.mkdir();
					}
					if (!(workoutCircuit.getTotalTime() >= 0)) {
						JOptionPane.showMessageDialog(mainPanel, "A circuit with zero time cannot be saved.");
					}
					String txt = new String();
					for (Exercise ex : workoutCircuit.getCircuit()) {
						txt+= ex.textString() + "\n";
					}
					String file_name = JOptionPane.showInputDialog("What do you want to name your file?");
					BufferedWriter bufferedWriter = null;
					File myFile = null;
					if (jarFile) {
						File jarFile1 = null;
						try {
							jarFile1 = new File(WorkoutTimerVisual.class.getProtectionDomain().getCodeSource().getLocation().toURI());
						} catch (URISyntaxException uri) {
							
						}
						file = new File(jarFile1.getParentFile().getAbsolutePath());
						path = file.getPath() + "/saved_exercises/" + file_name;
						myFile = new File(path);
					} else {
						file = new File(path+"/src/saved_exercises/" + file_name);
					}
					try {
						// check if file exist, otherwise create the file before writing
						if (!myFile.exists()) {
							myFile.createNewFile();
						}
						Writer writer = new FileWriter(myFile);
						bufferedWriter = new BufferedWriter(writer);
						bufferedWriter.write(txt);
					} catch (IOException io) {
						io.printStackTrace();
					} finally{
						try{
							if(bufferedWriter != null) bufferedWriter.close();
						} catch(Exception ex){

						}
					}
					
					JOptionPane.showMessageDialog(mainPanel, "Save completed succesfully.");

					}
					
			});
			
			openItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String file_name1 = JOptionPane.showInputDialog("Please enter the name of the workout that you previously saved");
					int save;
					if (workoutCircuit.getTotalTime() <= 0) {
						save = JOptionPane.NO_OPTION;
					} else {
						save = JOptionPane.showConfirmDialog(mainPanel, "Would you like to save the current circuit?");
					}
					if (save == JOptionPane.YES_OPTION) {
						File file = new File("");
						String path = file.getAbsolutePath();
						file = new File(path + "/src/saved_exercises");
						if (jarFile) {
							file = new File("/saved_exercises");
						}
						
						if (!file.isDirectory()) {
							file.mkdir();
						}
						if (!(workoutCircuit.getTotalTime() >= 0)) {
							JOptionPane.showMessageDialog(mainPanel, "A circuit with zero time cannot be saved.");
						}
						
						String txt = new String();
						for (Exercise ex : workoutCircuit.getCircuit()) {
							txt+= ex.textString() + "\n";
						}
						String file_name = JOptionPane.showInputDialog("What do you want to name your file?");
						BufferedWriter bufferedWriter = null;
						try {
							
							File myFile = new File(path+"/src/saved_exercises/" + file_name);
							if (jarFile) {
								myFile = new File("/saved_exercises/" + file_name);
							}
							
							// check if file exist, otherwise create the file before writing
							if (!myFile.exists()) {
								myFile.createNewFile();
							}
							Writer writer = new FileWriter(myFile);
							bufferedWriter = new BufferedWriter(writer);
							bufferedWriter.write(txt);
						} catch (IOException io) {
							io.printStackTrace();
						} finally{
							try{
								if(bufferedWriter != null) bufferedWriter.close();
							} catch(Exception ex){

							}
						}
						JOptionPane.showMessageDialog(mainPanel, "Save completed succesfully.");
					} 
					if (readFile(file_name1)) {
						JOptionPane.showMessageDialog(mainPanel, file_name1 + " was successfully loaded.");
					} else {
						JOptionPane.showMessageDialog(mainPanel, "Error!" + file_name1 + " was unsuccessfully loaded.");
					}
					exercisesPane.setText(workoutCircuit.toString());
					count.setText("Exercise count: " + (workoutCircuit.getCircuit().size()));
					repaint();
				}
				
				
			});
			
		}
	}
	
	public static class LblPanel extends JPanel{
		public LblPanel() {
			this.setLayout(new GridLayout(3,1));
			this.add(new JLabel("Exercise name"));
			this.add(new JLabel("Length in seconds"));
			this.add(new JLabel("Position (if not in order)"));
		}
	}
	
	public static class MiddlePanel extends JPanel{
		public MiddlePanel() {
			this.setLayout(new GridLayout(1,2));
		}
	}

	public static class ButtonPanel extends JPanel{
		
		JButton addAnother = new JButton("Add");
		JButton goBtn = new JButton("Go!");
		JButton removeBtn = new JButton("Remove");
		public ButtonPanel() {
			this.setLayout(new GridLayout(1,3));
			this.add(addAnother);
			this.add(removeBtn);
			this.add(goBtn);
			
			addAnother.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					revalidate();
					
					int time1;
					String name1;
					int pos1;
					try {
						time1 = Integer.parseInt(time.getText());
					} catch (Exception e1) {
						time1 = 0;
					}
					try {
						pos1 = Integer.parseInt(pos.getText());
					} catch (Exception e1) {
						pos1 = 0;
					}
					count.setText("Exercise count: " + (workoutCircuit.getCircuit().size() + 1));
					name1 = name.getText();
					workoutCircuit.addExercise(new Exercise(name1, time1, pos1));
					workoutCircuit.sortList();
					time.setText("");
					pos.setText("");
					name.setText("");
					exercisesPane.setText(workoutCircuit.toString());
					repaint();
					
					
					
					
				}
				
			});
			
			timer = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					timeLeftLbl.setText(""+(currentExerciseTime - (++secondsElapsed)));
					repaint();
					revalidate();
					if (currentExerciseTime - secondsElapsed <= 0) {
						
						playAudio("258193__kodack__beep-beep.wav");
						
						
						timer.stop();
						timer.stop();
						currentExerciseTime = currExercise.getLength();
						timer.stop();
						secondsElapsed = 0;
						currExerciseNum++;
						if (currExerciseNum >= workoutCircuit.getCircuit().size() ) {
							int xCoordinate = secondScreen.getX();
							int yCoordinate = secondScreen.getY();
							startScreen.setLocation(xCoordinate, yCoordinate);
							secondScreen.setVisible(false);
							startScreen.setVisible(true);
							timer.stop();
							return;
						}
						currExercise = workoutCircuit.getCircuit().get(currExerciseNum);
						currentExerciseTime = currExercise.getLength();
						timeLeftLbl.setText(""+currentExerciseTime);
						int numberOfLetters = currExercise.getName().length();
						int fontSize = (int) (0.0087*Math.pow(numberOfLetters,2)-1.5829*numberOfLetters+88.8314);
						exerciseLbl.setFont(new Font("Arial", Font.BOLD, fontSize));						
						exerciseLbl.setText(currExercise.getName());
						timer.stop();
						timer.restart();
					} else if (currentExerciseTime - secondsElapsed <= 3) {
						if (currExerciseNum >= workoutCircuit.getCircuit().size()-1 && !alreadyPlayingAudio) {
							playAudio("17216__meatball4u__countdown.wav");
							alreadyPlayingAudio = true;
						}

					}

				}
				

			});
			
			goBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (workoutCircuit.getCircuit().size() <= 0) {
						JOptionPane.showMessageDialog(mainPanel, "Error, please enter in an exercise to start.");
						return;
					}
					if (workoutCircuit.getTotalTime() <= 0) {
						JOptionPane.showMessageDialog(mainPanel, "Error, total circuit time is zero.");
						return;
					}
					int xCoordinate = startScreen.getX();
					int yCoordinate = startScreen.getY();
					secondScreen.setLocation(xCoordinate, yCoordinate);
					startScreen.setVisible(false);
					secondScreen.setVisible(true);
					currExerciseNum = 0;
					currExercise = workoutCircuit.getCircuit().get(currExerciseNum);
					currentExerciseTime = currExercise.getLength();
					secondsElapsed = 0;
					timeLeftLbl.setText(""+currentExerciseTime);
					int numberOfLetters = currExercise.getName().length();
					int fontSize = (int) (0.0087*Math.pow(numberOfLetters,2)-1.5829*numberOfLetters+88.8314);
					exerciseLbl.setFont(new Font("Arial", Font.BOLD, fontSize));
					exerciseLbl.setText(currExercise.getName());

					timer.start();

				}

			});

			
			removeBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (workoutCircuit.getCircuit().size() <= 0) {
						JOptionPane.showMessageDialog(mainPanel, "Error, exercise list is empty.");
						return;
					}
					workoutCircuit.removeLastExercise();
					exercisesPane.setText(workoutCircuit.toString());
					count.setText("Exercise count: " + (workoutCircuit.getCircuit().size()));
				}
			});
			


		}

		
		
		
		
		
		
		
		
	}
	
	public static class StartPanelUpper extends JPanel {
		
		
		
		public JLabel welcomeLbl = new JLabel("Welcome, please enter in your circuit, then press go!");
		
		public StartPanelUpper() {
			this.setLayout(new FlowLayout());
			
			count.setText("Exercise count: 0");
			this.add(count);
			this.add(welcomeLbl);
			int textSize1 = (int)(0.0325*OVERALL_WIDTH);
			welcomeLbl.setFont(new Font("Arial", Font.BOLD, textSize1));
			int textSize2 = (int)(0.0875*OVERALL_WIDTH);
			count.setFont(new Font("Arial", Font.BOLD, textSize2));
			
				
			
			
			
		}
		
		
		
	}
	
	public static class QueryPanel extends JPanel {


		public QueryPanel() {
			this.setLayout(new GridLayout(3,1));

	
			this.add(name);
			this.add(time);
			this.add(pos);
		
		}
	}

	
	
	
	
	public static class TimerMainPanel extends JPanel {
		
		JButton nextBtn = new JButton("Next");
		JButton prevBtn = new JButton("Prev");
		JButton stopBtn = new JButton("Stop");
		public TimerMainPanel() {
			this.setLayout(new BorderLayout());
			this.add(nextBtn, BorderLayout.EAST);
			this.add(prevBtn, BorderLayout.WEST);
			this.add(stopBtn, BorderLayout.NORTH);
			this.add(timeLeftLbl, BorderLayout.CENTER);
			this.add(exerciseLbl, BorderLayout.SOUTH);

			timeLeftLbl.setFont(new Font("Arial", Font.BOLD, 40));
			exerciseLbl.setFont(new Font("Arial", Font.BOLD, 75));
			
			
			
			stopBtn.addActionListener(new ActionListener() {
				@Override 
				public void actionPerformed(ActionEvent e) {
					int xCoordinate = secondScreen.getX();
					int yCoordinate = secondScreen.getY();
					startScreen.setLocation(xCoordinate, yCoordinate);
					secondScreen.setVisible(false);
					startScreen.setVisible(true);
					timer.stop();
					timer.stop();
				
				}
				
			});
			
			nextBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					timer.stop();
					secondsElapsed = 0;
					currExerciseNum++;
					if (currExerciseNum >= workoutCircuit.getCircuit().size() ) {
						int xCoordinate = secondScreen.getX();
						int yCoordinate = secondScreen.getY();
						startScreen.setLocation(xCoordinate, yCoordinate);
						secondScreen.setVisible(false);
						startScreen.setVisible(true);
						timer.stop();
						return;
					}
					currExercise = workoutCircuit.getCircuit().get(currExerciseNum);
					currentExerciseTime = currExercise.getLength();
					timeLeftLbl.setText(""+currentExerciseTime);
					int numberOfLetters = currExercise.getName().length();
					int fontSize = (int) (0.0087*Math.pow(numberOfLetters,2)-1.5829*numberOfLetters+88.8314);
					exerciseLbl.setFont(new Font("Arial", Font.BOLD, fontSize));
					exerciseLbl.setText(currExercise.getName());
					timer.stop();
					timer.restart();
					
				}
				
				
			});
			
			

			prevBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (currExerciseNum <= 0) {
						currExerciseNum = 0;
						secondsElapsed = 0;
						timeLeftLbl.setText(""+(currentExerciseTime - secondsElapsed));
						return;
					}
					timer.stop();
					secondsElapsed = 0;
					currExerciseNum--;
					if (currExerciseNum >= workoutCircuit.getCircuit().size() ) {
						int xCoordinate = secondScreen.getX();
						int yCoordinate = secondScreen.getY();
						startScreen.setLocation(xCoordinate, yCoordinate);
						secondScreen.setVisible(false);
						startScreen.setVisible(true);
						timer.stop();
						return;
					}
					currExercise = workoutCircuit.getCircuit().get(currExerciseNum);
					currentExerciseTime = currExercise.getLength();
					timeLeftLbl.setText(""+(currentExerciseTime - secondsElapsed));
					int numberOfLetters = currExercise.getName().length();
					int fontSize = (int) (0.0087*Math.pow(numberOfLetters,2)-1.5829*numberOfLetters+88.8314);
					exerciseLbl.setFont(new Font("Arial", Font.BOLD, fontSize));
					exerciseLbl.setText(currExercise.getName());
					timer.stop();
					timer.restart();
					
				}
				
				
			});
			
			
		}
		
		
	}
	
	
	
	
	public static void setUpAndDrawGui() {

		startScreen = new JFrame();
		startScreen.setResizable(false);
		startScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startScreen.setSize(OVERALL_WIDTH, OVERALL_HEIGHT);
		secondScreen = new JFrame();
		secondScreen.setSize(OVERALL_WIDTH, OVERALL_HEIGHT);
		secondScreen.setResizable(false);
		secondScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startScreen.setLocationRelativeTo(null);
		menuBar.add(menu);
	
		
		
		startScreen.setJMenuBar(menuBar);
		exercisesPane.setFont(new Font("Arial", Font.PLAIN, 15));
		MainPanel main = new MainPanel();
		mainPanel = main;
		main.add(new StartPanelUpper());
		MiddlePanel mid = new MiddlePanel();
		mid.add(new LblPanel());
		mid.add(new QueryPanel());
		main.add(mid);
		main.add(new ButtonPanel());
		main.add(exercisesPane);
		
		timerMain = new TimerMainPanel();
		secondScreen.add(timerMain);
		timerMain.setVisible(true);
		secondScreen.setVisible(false);
		
		startScreen.setContentPane(main);
		startScreen.setVisible(true);
	

	}

	
}
