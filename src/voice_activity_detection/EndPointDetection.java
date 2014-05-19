package voice_activity_detection;

import java.io.File;

import wav_file.WavFile;

public class EndPointDetection {
	private float[] originalSamples;
	private float[] silenceRemovedSamples;
	
	private int sampleRate;
	private int noiseSamples;
	private int frameLength;
	
	public EndPointDetection(float[] originalSamples, int sampleRate) {
		this.originalSamples = originalSamples;
		this.sampleRate = sampleRate;
		
		frameLength = sampleRate / 1000;
		noiseSamples = frameLength * 200;
	}
	
	public float[] detectEndPoints() {
		boolean[] voiced = new boolean[originalSamples.length];
		
		float mx = 0.0f;
		for (float a : originalSamples) {
			mx = Math.max(mx, a);
		}
		
		System.out.println("MAX = " + mx);
		
		float sd = 0.0f;
		float mean = 0.0f;
		
		// Calculate mean
		for (int i = 0; i < noiseSamples; i++) {
			mean += originalSamples[i];
		}
		mean /= noiseSamples;
		
		System.out.println("Mean = " + mean);
		
		// Calculate standard deviation
		for (int i = 0; i < noiseSamples; i++) {
			sd += (originalSamples[i] - mean) * (originalSamples[i] - mean);
		}
		sd = (float)Math.sqrt(sd / noiseSamples);
		
		System.out.println("Standard Deviation = " + sd);
		
		// Check if 1D Mahalanobis distance function
		// |x-u|/s is greater than 2 or not
		for (int i = 0; i < originalSamples.length; i++) {
			float v = (Math.abs(originalSamples[i] - mean) / sd);
			if (v > 2) {
				voiced[i] = true;
			}
			else {
				voiced[i] = false;
			}
		}
		
		// Mark each frame to be voiced or unvoiced
		int frameCount = 0;
		int usefulFrameCount = 0;
		int countVoiced;
		int countUnvoiced;
		
		boolean voicedFrame[] = new boolean[originalSamples.length / frameLength];
		
		for (int i = 0; i < originalSamples.length - frameLength; i += frameLength) {
			countVoiced = 0;
			countUnvoiced = 0;
			
			for (int j = i; j < i + frameLength; j++) {
				if (voiced[j]) {
					countVoiced++;
				}
				else {
					countUnvoiced++;
				}
			}
			
			if (countVoiced > countUnvoiced) {
				usefulFrameCount++;
				voicedFrame[frameCount++] = true;
			}
			else {
				voicedFrame[frameCount++] = false;
			}
		}
		
		// Remove silence
		silenceRemovedSamples = new float[usefulFrameCount * frameLength];
		int k = 0;
		for (int i = 0; i < frameCount; i++) {
			if (voicedFrame[i]) {
				for (int j = i * frameLength; j < i * frameLength + frameLength; j++) {
					silenceRemovedSamples[k++] = originalSamples[j];
				}
			}
		}
		return silenceRemovedSamples;
	}
	
	public static void main(String[] args) {
		try {
			// Open the wav file specified as the first argument
			WavFile wavFile = WavFile.openWavFile(new File(args[0]));
			
			// Display information about the wav file
			wavFile.display();
			
			// Get the number of audio channels in the wav file
			int numChannels = wavFile.getNumChannels();
			
			// Create a buffer of 100 frames
			long total = wavFile.getFramesRemaining();
			double[] buffer = new double[(int) (total * numChannels)];
			float[] samples = new float[(int) (total)];
			
			int framesRead;
			
			do {
				framesRead = wavFile.readFrames(buffer, (int)total);
				for (int i = 0; i < framesRead * numChannels; i++) {
					samples[i] = (float) buffer[i];
				}
			} while (framesRead != 0);
			EndPointDetection endPointDetection = new EndPointDetection(samples, (int)wavFile.getSampleRate());
			float voice[] = endPointDetection.detectEndPoints();
			double doubleVoiced[] = new double[voice.length];
			for (int i = 0; i < voice.length; i++) {
				doubleVoiced[i] = voice[i];
			}

			WavFile newWavFile = WavFile.newWavFile(new File(args[1]), 1, voice.length, wavFile.getValidBits(), wavFile.getSampleRate());
			newWavFile.writeFrames(doubleVoiced, (int)total);
			
			// Close wav file
			wavFile.close();
			newWavFile.close();
			
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
