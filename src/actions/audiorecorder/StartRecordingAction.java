package actions.audiorecorder;

import gui.AddWordsFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class StartRecordingAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	public StartRecordingAction() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AddWordsFrame.getInstance().setRecordingState();
		AddWordsFrame.getInstance().newCapture().start();
	}

}
