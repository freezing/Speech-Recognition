package app;

import gui.MainFrame;

public class Main {
	
	public static void main(String[] args) throws Exception {
		MainFrame app = MainFrame.getInstance();
		app.setVisible(true);
	//	Mediator mediator = new Mediator(DATABASE_PATH);
				
/*		
 * WavFile wav = WavFile.openWavFile(new File("e:\\BigData\\SpeechRecognition\\tmp\\"
				+ "non-noised\\"
				+ "nikola0.wav"));
		int sampleRate = (int)wav.getSampleRate();
		double[] samples = wav.readWholeFile();
		
		PreProcessor.normalizePCM(samples);
		
		EndPointDetector2 endPointDetector = new EndPointDetector2(20, sampleRate);
		List<Interval> intervals = endPointDetector.detectEndPoints(samples, 10, 10);
		for (Interval i : intervals) {
			System.out.println(i);
		}*/
		
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
	/*	String word = mediator.recognizeSpeech(samples, (int) wav.getSampleRate());
		System.out.println(word);
		
		WavFile wavFile = WavFile.newWavFile(new File("/home/nikola/SpeechRecognitionTrainingData/Nikola/15.wav"), 1, 
				(int) wav.getNumFrames(), wav.getValidBits(), wav.getSampleRate());
		
		wavFile.writeFrames(samples, (int)wav.getNumFrames());
		wavFile.close();*/
	}
}
