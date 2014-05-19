package feature_extraction;

public class WindowFunctions {
	
	public static float generalizedHammingWindow(float alpha, int sampleIdx, int sampleLength) {
		return (float) (alpha - (1.0f - alpha) * Math.cos((2.0f * Math.PI * sampleIdx) / (sampleLength - 1)));
	}
	
	public static float hammingWindow(int sampleIdx, int sampleLength) {
		return generalizedHammingWindow(0.54f, sampleIdx, sampleLength);
	}
	
	// TODO: Make generic function for any window
	public static float[] applyWindowFunction(float[] samples) {
		float[] windowedSamples = new float[samples.length];
		
		for (int i = 0; i < samples.length; i++) {
			windowedSamples[i] = samples[i] * hammingWindow(i, samples.length);
		}
		return windowedSamples;
	}
}
