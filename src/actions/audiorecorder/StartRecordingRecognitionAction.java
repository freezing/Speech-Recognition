package actions.audiorecorder;

import gui.RecognizeSpeechFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class StartRecordingRecognitionAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		RecognizeSpeechFrame.getInstance().setRecordingState();
		RecognizeSpeechFrame.getInstance().newCapture().start();
	}
}
