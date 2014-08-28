package gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import actions.OpenWindowAction;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private static MainFrame instance = null;
	
	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}

	private MainFrame() {
		initialize();
	}
	
	private void initialize() {
		// Set default values for the main frame
		setTitle("Speech Recognition using HMM - Nikola Stojiljkovic");
		setSize(300, 80);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		createComponents();
	}

	private void createComponents() {
		JPanel container = new JPanel();
		add(container);
		
		JButton recognizeSpeechButton = new JButton(new OpenWindowAction(RecognizeSpeechFrame.getInstance()));
		recognizeSpeechButton.setText("Recognize Speech");
		JButton trainButton = new JButton(new OpenWindowAction(AddWordsFrame.getInstance()));
		trainButton.setText("Train");
		
		container.add(recognizeSpeechButton);
		container.add(trainButton);
	}
	
}
