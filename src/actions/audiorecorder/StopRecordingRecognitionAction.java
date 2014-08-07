package actions.audiorecorder;

import gui.RecognizeSpeechFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class StopRecordingRecognitionAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		RecognizeSpeechFrame.getInstance().getCapture().stop();
		RecognizeSpeechFrame.getInstance().setStopState();
	}
}
