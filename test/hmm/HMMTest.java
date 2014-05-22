package hmm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import mediators.Mediator;

import org.junit.Test;

import feature_extraction.FeatureVector;
import feature_extraction.FrameExtractor;
import feature_extraction.MfccExtractor;
import vector_quantization.KDPoint;
import voice_activity_detection.EndPointDetection;
import wav_file.WavFile;
import wav_file.WavFileException;
import app.Main;

public class HMMTest {
	static final int ENTRY_COUNT = 256;
	
	@Test
	public void test2() throws Exception {
		HiddenMarkovModel hmm = new HiddenMarkovModel(6, ENTRY_COUNT);
		HiddenMarkovModel hmmPetar = new HiddenMarkovModel(6, ENTRY_COUNT);
		
		Scanner sc = new Scanner(new File("/home/nikola/train"));
		Scanner sc2 = new Scanner(new File("/home/nikola/train2"));
		
		int train[][] = new int[2][];
		int train2[][] = new int[2][];
		
		for (int i = 0; i < 2; i++ ){
			int length;
			
			if (i < 2) {
				length = sc.nextInt();
				train[i] = new int[length];
				for (int j = 0; j < length; j++) {
					train[i][j] = sc.nextInt();
				}
			}
			
			length = sc2.nextInt();
			train2[i] = new int[length];
			for (int j = 0; j < length; j++) {
				train2[i][j] = sc2.nextInt();
			}
		}
		sc.close();
		sc2.close();
		
		System.out.println(Arrays.toString(train[0]));
		System.out.println(Arrays.toString(train[1]));
		
		System.out.println(Arrays.toString(train2[0]));
		System.out.println(Arrays.toString(train2[1]));
		
		hmm.setTrainSeq(train);
		hmm.train();
		
		hmmPetar.setTrainSeq(train2);
		hmmPetar.train();
		
		Mediator mediator = new Mediator(Main.DATABASE_PATH);
		mediator.generateCodebook();
		
		WavFile wav = WavFile.openWavFile(new File("/home/nikola/SpeechRecognitionTrainingData/Nikola/5.wav"));
		int sampleRate = (int)wav.getSampleRate();
		double[] samples = new double[(int) wav.getNumFrames()];
		wav.readFrames(samples, samples.length);
		
		
		double[][] framedSamples = FrameExtractor.extractFrames(samples, 512, 160);
		MfccExtractor mfccExtractor = new MfccExtractor(framedSamples, (int) wav.getSampleRate());
		mfccExtractor.makeFeatureVector();
		FeatureVector fv = mfccExtractor.getFeatureVector();
		
		
		List<KDPoint> points = new ArrayList<>();
		for (int i = 0; i < fv.getFeatureVector().length; i++) {
			points.add(new KDPoint(fv.getFeatureVector()[i]));
			System.out.println(Arrays.toString(fv.getFeatureVector()[i]));
		}
		System.out.println(points.size());
		int quantized[] = mediator.getCodebook().quantize(points);
		System.out.println(Arrays.toString(quantized));
		
		double p = hmm.viterbi(quantized);
		double p2 = hmmPetar.viterbi(quantized);
		
		System.out.println(p);
		System.out.println(p2);
	}
}
