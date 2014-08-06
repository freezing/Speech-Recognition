package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import actions.OpenWindowAction;
import actions.RetrainAllHmmsAction;

public class TrainFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;

	private static TrainFrame instance = null;
	
	public static TrainFrame getInstance() {
		if (instance == null) {
			instance = new TrainFrame();
		}
		return instance;
	}
	
	private TrainFrame() {
		initialize();
	}
	
	private void initialize() {
		setTitle("Train Speech Recognizer");
		setSize(300, 150);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		
		createComponents();
	}
	
	private void createComponents() {
		JPanel container = new JPanel();
		add(container);
		
		JButton addWordsButton = new JButton(new OpenWindowAction(AddWordsFrame.getInstance()));
		addWordsButton.setText("Add word(s) to database");
		JButton retrainButton = new JButton(new RetrainAllHmmsAction());
		retrainButton.setText("Retrain all HMMs (new codebook)");
		
		container.add(addWordsButton);
		container.add(retrainButton);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		
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
}
