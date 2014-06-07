package app;

import java.io.File;

import preprocessing.PreProcessor;
import voice_activity_detection.EndPointDetection;
import wav_file.WavFile;
import mediators.Mediator;

public class Main {
	public static final String DATABASE_PATH = "/home/nikola/SpeechRecognitionDatabase";
	
	public static void main(String[] args) throws Exception {		
		Mediator mediator = new Mediator(DATABASE_PATH);
				
		WavFile wav = WavFile.openWavFile(new File("/home/nikola/SpeechRecognitionTrainingData/Nikola/5.wav"));
		int sampleRate = (int)wav.getSampleRate();
		double[] samples = wav.readWholeFile();
		
		PreProcessor.normalizePCM(samples);
		for (int i = 0; i < samples.length; i++) {
		/*	samples[i] += 0.1;
			if (samples[i] > 1.0) samples[i] = 1.0;*/
		}
		
	/*	double[] tmp = mediator.getVoicedSamples(samples, (int)wav.getSampleRate());
		WavFile newWav = WavFile.newWavFile(new File("/home/nikola/test.wav"), 1, tmp.length, wav.getValidBits(), (int)wav.getSampleRate());
		newWav.writeFrames(tmp, tmp.length);
		newWav.close();*/
	//	mediator.retrainVad();
	//	mediator.saveVad();
	//	mediator.addWordAndTrainModel("Stolica", samples, sampleRate);
	//	mediator.saveCurrentHmmModels();
	//	mediator.generateCodebook();
	//	mediator.saveCodebook();
	//	mediator.retrainAllHmms();
	//	mediator.saveCurrentHmmModels();
		String word = mediator.recognizeSpeech(samples, (int) wav.getSampleRate());
		System.out.println(word);
		
		WavFile wavFile = WavFile.newWavFile(new File("/home/nikola/SpeechRecognitionTrainingData/Nikola/15.wav"), 1, 
				(int) wav.getNumFrames(), wav.getValidBits(), wav.getSampleRate());
		
		wavFile.writeFrames(samples, (int)wav.getNumFrames());
		wavFile.close();
	}
}
