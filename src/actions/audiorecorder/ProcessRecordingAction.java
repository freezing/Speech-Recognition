package actions.audiorecorder;

import gui.AddWordsFrame;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.AbstractAction;

import model.Recording;
import utility.Utils;
import voice_activity_detection.EndPointDetector2;
import voice_activity_detection.Interval;

public class ProcessRecordingAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private Recording recording;
	
	public ProcessRecordingAction() {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		this.recording = AddWordsFrame.getInstance().getCapture().extractSamples(Utils.getAudioFormat());
		AudioInputStream audioInputStream = AddWordsFrame.getInstance().getCapture().getAudioInputStream();
		AudioFormat format = audioInputStream.getFormat();
		
		double[] samplesAIS = Utils.getSamples(audioInputStream);

		List<Interval> intervals = new ArrayList<>();
		List<AudioInputStream> audioInputStreams = new ArrayList<>();
		intervals = new EndPointDetector2(20, (int)format.getSampleRate())
			.detectEndPoints(samplesAIS, 10, 10, 250, 300, 180);
		
		for (int i = 0; i < intervals.size(); i++) {
		/*	AudioInputStream slice = Utils.saveWavFile("D:\\tmp\\" + i + ".wav", 
					AddWordsFrame.getInstance().getCapture().getAudioBytes(), intervals.get(i),
					Utils.getAudioFormat());*/
			AudioInputStream slice = Utils.getAudioInputStreamSlice(
					AddWordsFrame.getInstance().getCapture().getAudioBytes(),
					intervals.get(i), Utils.getAudioFormat());
			audioInputStreams.add(slice);
		}
		AddWordsFrame.getInstance().showEndPoints(intervals, audioInputStreams);
	}
	
	/**
     * Save the double array as a sound file (using .wav or .au format).
     */
    public static void save(String filename, double[] input) {

        // assumes 44,100 samples per second
        // use 16-bit audio, mono, signed PCM, little Endian
        AudioFormat format = new AudioFormat(22050, 16, 1, true, false);
        byte[] data = new byte[2 * input.length];
        for (int i = 0; i < input.length; i++) {
            int temp = (short) (input[i] * Short.MAX_VALUE);
            data[2*i + 0] = (byte) temp;
            data[2*i + 1] = (byte) (temp >> 8);
        }

        // now save the file
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream ais = new AudioInputStream(bais, format, input.length);
            if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
            }
            else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
            }
            else {
                throw new RuntimeException("File format not supported: " + filename);
            }
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
