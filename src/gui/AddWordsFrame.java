package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import model.Recording;
import threads.Capture;
import voice_activity_detection.Interval;
import actions.CheckBoxWordAction;
import actions.TrainAction;
import actions.audiorecorder.PlayRecordingAction;
import actions.audiorecorder.ProcessRecordingAction;
import actions.audiorecorder.StartRecordingAction;
import actions.audiorecorder.StopRecordingAction;

public class AddWordsFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;

	private static AddWordsFrame instance = null;
	
	private JButton recordButton;
	private JButton stopButton;
	private JButton processButton;
	
	private JButton trainButton;
	
	private JPanel wrapper;
	private JPanel wordsContainer;
	
	private JLabel infoLabel;
	
	private JCheckBox generateCodebookFlagCheckbox;
	
	private Recording recording;
	private List<Interval> intervals;
	private boolean[] intervalsToUse;
	private List<JTextField> wordValues;
	
	private Capture capture;

	private List<AudioInputStream> audioInputStreams;
	
	public static AddWordsFrame getInstance() {
		if (instance == null) {
			instance = new AddWordsFrame();
		}
		return instance;
	}
	
	private AddWordsFrame() {
		initialize();
	}
	
	private void initialize() {
		setTitle("Add Word(s)");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		
		createComponents();
	}
	
	private void createComponents() {
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		add(wrapper);
		
		JPanel container = new JPanel();
		container.setSize((int)this.getSize().getWidth(), 150);
		wrapper.add(container);
		
		recordButton = new JButton(new StartRecordingAction());
		recordButton.setText("Start recording");
		
		stopButton = new JButton(new StopRecordingAction());
		stopButton.setText("Stop recording");
		
		processButton = new JButton(new ProcessRecordingAction());
		processButton.setText("Process recorded audio");
		
		setInitialState();
		
		container.add(recordButton);		
		container.add(stopButton);
		container.add(processButton);
		
		wordsContainer = new JPanel();
		wordsContainer.setSize((int) this.getSize().getWidth(), 600);
		
		infoLabel = new JLabel("");
		wrapper.add(infoLabel);
		wrapper.add(wordsContainer);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		TrainFrame.getInstance().setVisible(true);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setRecordingState() {
		recordButton.setEnabled(false);
		stopButton.setEnabled(true);
		processButton.setEnabled(false);
		
	}

	public void setProcessingState() {
		recordButton.setEnabled(false);
		stopButton.setEnabled(false);
		processButton.setEnabled(false);
	}

	public void setStopState() {
		recordButton.setEnabled(true);
		stopButton.setEnabled(false);
		processButton.setEnabled(true);
	}
	
	public void setInitialState() {
		recordButton.setEnabled(true);
		stopButton.setEnabled(false);
		processButton.setEnabled(false);
	}

	public void showEndPoints(List<Interval> intervals, List<AudioInputStream> audioInputStreams) {
		setInitialState();
		this.intervals = intervals;
		this.audioInputStreams = audioInputStreams;
		createComponentsForDetectedWords();
	}

	private void createComponentsForDetectedWords() {
		intervalsToUse = new boolean[intervals.size()];
		wordValues = new ArrayList<>();
		Arrays.fill(intervalsToUse, true);
		
		infoLabel.setText("Voice Activity Detector has recognized " + intervals.size() + " words.");
		
		wordsContainer.removeAll();
		for (int i = 0; i < intervals.size(); i++) {
			JPanel wordContainer = new JPanel();
			wordContainer.setSize(this.getWidth(), 150);
			
			JTextField value = new JTextField(7);
			wordValues.add(value);
			
			JCheckBox checkbox = new JCheckBox(new CheckBoxWordAction(intervalsToUse, i));
			checkbox.setSelected(true);
			checkbox.setText("Include word");
			//intervalsToUse.add(checkbox);
			
			wordContainer.add(value);
			wordContainer.add(checkbox);

			JButton playButton = new JButton(new PlayRecordingAction(audioInputStreams.get(i)));
			playButton.setText("Play detected word");
			wordContainer.add(playButton);
			
			wordsContainer.add(wordContainer);
			this.revalidate();
		}
		
		if (trainButton != null) {
			wrapper.remove(trainButton);
		}
		
		if (generateCodebookFlagCheckbox != null) {
			wrapper.remove(generateCodebookFlagCheckbox);
		}
		
		generateCodebookFlagCheckbox = new JCheckBox();
		generateCodebookFlagCheckbox.setText("Generate new codebook");
		generateCodebookFlagCheckbox.setEnabled(true);
		
		trainButton = new JButton(new TrainAction(intervalsToUse, audioInputStreams));
		trainButton.setText("Train");
		
		wrapper.add(generateCodebookFlagCheckbox);
		wrapper.add(trainButton);
		this.invalidate();
	}

	public Capture newCapture() {
		capture = new Capture();
		return capture;
	}

	public Capture getCapture() {
		return capture;
	}

	public List<String> getWords() {
		List<String> words = new ArrayList<>();
		
		for (int i = 0; i < wordValues.size(); i++) {
			words.add(wordValues.get(i).getText().trim().toLowerCase());
		}
		
		return words;
	}

	public boolean isGenerateCodebookFlagSet() {
		return generateCodebookFlagCheckbox.isEnabled();
	}
}
