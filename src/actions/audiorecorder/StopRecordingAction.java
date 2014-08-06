package actions.audiorecorder;

import gui.AddWordsFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class StopRecordingAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		AddWordsFrame.getInstance().getCapture().stop();
		AddWordsFrame.getInstance().setStopState();
	}
}
