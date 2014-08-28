package actions;

import gui.AddWordsFrame;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import utility.Utils;
import voice_activity_detection.EndPointDetector2;
import voice_activity_detection.Interval;
import wav_file.WavFile;
import wav_file.WavFileException;

public class ProcessWavFileAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	public ProcessWavFileAction() {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File file = null;
		JFileChooser chooser = new JFileChooser("E:\\BigData\\SpeechRecognition\\words_non_trimed\\");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Audio files", "wav");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(AddWordsFrame.getInstance());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       System.out.println("You chose to open this file: " +
		            chooser.getSelectedFile().getName());
		       file = chooser.getSelectedFile();
		    }
		    else {
		    	System.out.println("File not opened: " + file);
		    	return;
		    }
		
		AudioFileFormat audioFileFormat;
		try {
			audioFileFormat = AudioSystem.getAudioFileFormat(file);
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		AudioFormat format = audioFileFormat.getFormat();
		System.out.println(format);
		AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		byte[] audioBytes = new byte[format.getFrameSize() * audioFileFormat.getFrameLength()];
		try {
			int bytesRead = audioInputStream.read(audioBytes, 0, audioBytes.length);
			System.out.println("Bytes read = " + bytesRead);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		audioInputStream = new AudioInputStream(bais, format, audioFileFormat.getFrameLength());
		double[] samplesAIS = Utils.getSamples(audioInputStream);

		double[] samples;
		WavFile wavFile = null;
		try {
			wavFile = WavFile.openWavFile(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (WavFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		List<Interval> intervals = new ArrayList<>();
		List<AudioInputStream> audioInputStreams = new ArrayList<>();
		intervals = new EndPointDetector2(20, (int)format.getSampleRate())
			.detectEndPoints(samplesAIS, 10, 10, 250, 300, 180);
		
		for (int i = 0; i < intervals.size(); i++) {
			AudioInputStream slice = Utils.getAudioInputStreamSlice(
					audioBytes,
					intervals.get(i), format);
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
