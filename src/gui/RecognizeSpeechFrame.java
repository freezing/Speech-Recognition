package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class RecognizeSpeechFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	
	private static RecognizeSpeechFrame instance = null;
	
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
		setTitle("Recognize Speech");
		setSize(200, 100);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(this);
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
}
