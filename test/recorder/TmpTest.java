package recorder;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import wav_file.WavFile;
import wav_file.WavFileException;

public class TmpTest {
	
	@Test
	public void test() throws IOException, WavFileException {
		WavFile wav = WavFile.openWavFile(new File("e:\\SpeechRecognitionDatabase\\TrainingSet\\three\\0.wav"));
		double[] samples = wav.readWholeFile();
		System.out.println("Samples length = " + samples.length);
		for (int i = 0; i < 50; i++) {
			System.out.println(samples[i]);
		}
	}
}
