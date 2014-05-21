package app;

import java.io.File;

import wav_file.WavFile;
import mediators.Mediator;

public class Main {
	public static final String DATABASE_PATH = "/home/nikola/SpeechRecognitionDatabase";
	
	public static void main(String[] args) throws Exception {
	/*	String rootpath = "/home/nikola/SpeechRecognitionTrainingData/Petar/";
		String fileId = "2";
		String filepath = rootpath + fileId + ".wav";
		WavFile wav = WavFile.openWavFile(new File(filepath));

		double[] buffer = new double[(int) (wav.getNumChannels() * wav
				.getNumFrames())];
		int readFrames = wav.readFrames(buffer, (int) wav.getNumFrames());
		wav.display();
		
		System.out.println(Utils.timeToSamples(550, (int)wav.getSampleRate()));

		FileWriter writer = new FileWriter(new File("/home/nikola/log.txt"));
		for (int i = 0; i < readFrames; i++) {
			if (buffer[i] > 1e-3) {
				writer.write(i + "     " + buffer[i] + "\n");
			}
			else {
				writer.write(i + "     0.000000" + "\n");
			}
		}
		writer.close();

		PreProcessor.normalizePCM(buffer);
		EndPointDetection epd = new EndPointDetection(buffer,
				(int) wav.getSampleRate());
		List<Interval> words = epd.test();
		for (Interval interval : words) {
			System.out.println(interval);
			System.out.println(Utils.samplesToTime(interval.getStart(), (int)wav.getSampleRate()));
			System.out.println(Utils.samplesToTime(interval.getEnd(), (int)wav.getSampleRate()));
		}

		WavFile newWav = WavFile.newWavFile(new File(rootpath + fileId
				+ "-endpoints.wav"), wav.getNumChannels(), words.get(0).length,
				wav.getValidBits(), wav.getSampleRate());
		
		newWav.writeFrames(words.get(0), words.get(0).length);
		
		newWav.close();
		wav.close();
*/
		WavFile wav = WavFile.openWavFile(new File("/home/nikola/SpeechRecognitionDatabase/TrainingSet/Nikola/0.wav"));
		int sampleRate = (int)wav.getSampleRate();
		double[] samples = new double[(int) wav.getNumFrames()];
		wav.readFrames(samples, samples.length);
		
		Mediator mediator = new Mediator(DATABASE_PATH);
		mediator.generateCodebook();
		mediator.saveCodebook();
		mediator.retrainAllHmms();
		mediator.saveCurrentHmmModels();
		mediator.recognizeSpeech(samples, (int) wav.getSampleRate());
	}
}
