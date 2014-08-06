package speechrecognizer;

import java.io.File;
import java.util.List;

import mediators.Mediator;

import org.junit.Test;

import utility.Utils;
import voice_activity_detection.EndPointDetector2;
import voice_activity_detection.Interval;
import wav_file.WavFile;

public class SpeechRecognizerTest {
	
	@Test
	public void test() throws Exception {
		String filepath = "D:\\tmp\\0.wav";
		WavFile wavFile = WavFile.openWavFile(new File(filepath));
		
		double[] samples = wavFile.readWholeFile();
		int sampleRate = (int)wavFile.getSampleRate();
		
		EndPointDetector2 vad = new EndPointDetector2(20, sampleRate);
		List<Interval> intervals = vad.detectEndPoints(samples, 10, 10);
		
		int k = 0;
		for (Interval i : intervals) {
			double[] tmp = Utils.getSubArray(samples, i.getStart(), i.getLength());
			String word = Mediator.getInstance().recognizeSpeech(tmp, sampleRate);
			System.out.println("Interval(" + k++ + ") = " + word);
		}
	}
}
