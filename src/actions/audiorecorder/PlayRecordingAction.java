package actions.audiorecorder;

import java.awt.event.ActionEvent;

import javax.sound.sampled.AudioInputStream;
import javax.swing.AbstractAction;

import threads.Player;

public class PlayRecordingAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	AudioInputStream audioInputStream;
	
	public PlayRecordingAction(AudioInputStream audioInputStream) {
		this.audioInputStream = audioInputStream;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new Player(audioInputStream).start();
	}
}
