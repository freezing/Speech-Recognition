package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import mediators.Mediator;
import threads.Capture;
import utility.Utils;
import voice_activity_detection.Interval;
import actions.ProcessWavFileAction;
import actions.RecognizeFromWavFileAction;
import actions.audiorecorder.PlayRecordingAction;
import actions.audiorecorder.RecognizeSpeechAction;
import actions.audiorecorder.StartRecordingRecognitionAction;
import actions.audiorecorder.StopRecordingRecognitionAction;

public class RecognizeSpeechFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;

	private static RecognizeSpeechFrame instance = null;
	
	private JButton recordButton;
	private JButton stopButton;
	private JButton processButton;
	private JButton processFileButton;
	
	private JButton trainButton;
	
	private JPanel wrapper;
	private JPanel wordsContainer;
	
	private JLabel infoLabel;
	
	private JCheckBox generateCodebookFlagCheckbox;
	
	private List<Interval> intervals;
	
	private Capture capture;

	private List<AudioInputStream> audioInputStreams;
	
	public static RecognizeSpeechFrame getInstance() {
		if (instance == null) {
			instance = new RecognizeSpeechFrame();
		}
		return instance;
	}
	
	private RecognizeSpeechFrame() {
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
		
		recordButton = new JButton(new StartRecordingRecognitionAction());
		recordButton.setText("Start recording");
		
		stopButton = new JButton(new StopRecordingRecognitionAction());
		stopButton.setText("Stop recording");
		
		processButton = new JButton(new RecognizeSpeechAction());
		processButton.setText("Recognize speech");
		
		processFileButton = new JButton(new RecognizeFromWavFileAction());
		processFileButton.setText("Recognize speech using WAV file");
		
		setInitialState();
		
		container.add(recordButton);		
		container.add(stopButton);
		container.add(processButton);
		container.add(processFileButton);
		
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
		MainFrame.getInstance().setVisible(true);
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

	public void showDetectedWords(List<Interval> intervals, List<AudioInputStream> audioInputStreams) {
		setInitialState();
		this.intervals = intervals;
		this.audioInputStreams = audioInputStreams;
		createComponentsForDetectedWords();
	}

	private void createComponentsForDetectedWords() {		
		infoLabel.setText("Voice Activity Detector has recognized " + intervals.size() + " words.");
		
		wordsContainer.removeAll();
		for (int i = 0; i < intervals.size(); i++) {
			JPanel wordContainer = new JPanel();
			wordContainer.setSize(this.getWidth(), 150);
			
			double[] samples = Utils.getSamples(audioInputStreams.get(i));
			String recognizedWord = "Message: ERROR";
			try {
				recognizedWord = Mediator.getInstance().recognizeSpeech(samples, (int)Utils.getAudioFormat().getSampleRate());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JLabel label = new JLabel(recognizedWord);
			
			wordContainer.add(label);

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
		this.invalidate();
	}

	public Capture newCapture() {
		capture = new Capture();
		return capture;
	}

	public Capture getCapture() {
		return capture;
	}

	public boolean isGenerateCodebookFlagSet() {
		return generateCodebookFlagCheckbox.isEnabled();
	}
}
