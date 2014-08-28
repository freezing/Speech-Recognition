package actions.audiorecorder;

import gui.AddWordsFrame;
import gui.RecognizeSpeechFrame;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.swing.AbstractAction;

import utility.Utils;
import voice_activity_detection.EndPointDetector;
import voice_activity_detection.EndPointDetector2;
import voice_activity_detection.Interval;

public class RecognizeSpeechAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		AudioInputStream audioInputStream = RecognizeSpeechFrame.getInstance().getCapture().getAudioInputStream();
		AudioFormat format = audioInputStream.getFormat();
		
		double[] samplesAIS = Utils.getSamples(audioInputStream);

		List<Interval> intervals = new ArrayList<>();
		List<AudioInputStream> audioInputStreams = new ArrayList<>();
		intervals = new EndPointDetector2(20, (int)format.getSampleRate())
				.detectEndPoints(samplesAIS, 10, 10, 250, 300, 180);
		
		for (int i = 0; i < intervals.size(); i++) {
			AudioInputStream slice = Utils.getAudioInputStreamSlice(
					RecognizeSpeechFrame.getInstance().getCapture().getAudioBytes(),
					intervals.get(i), Utils.getAudioFormat());
			audioInputStreams.add(slice);
		}
		RecognizeSpeechFrame.getInstance().showDetectedWords(intervals, audioInputStreams);
	}

}
