package feature_extraction;

public class WindowFunctions {
	
	public static double generalizedHammingWindow(double alpha, int sampleIdx, int sampleLength) {
		return (double) (alpha - (1.0f - alpha) * Math.cos((2.0f * Math.PI * sampleIdx) / (sampleLength - 1)));
	}
	
	public static double hammingWindow(int sampleIdx, int sampleLength) {
		return generalizedHammingWindow(0.54f, sampleIdx, sampleLength);
	}
	
	// TODO: Make generic function for any window
	public static double[] applyWindowFunction(double[] samples) {
		double[] windowedSamples = new double[samples.length];
		
		for (int i = 0; i < samples.length; i++) {
			windowedSamples[i] = samples[i] * hammingWindow(i, samples.length);
		}
		return windowedSamples;
	}
}
