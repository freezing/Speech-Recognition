package feature_extraction;

public class Utils {
	public static double[] preEmphasis(double[] samples, double alpha) {
		double[] s = new double[samples.length];
		
		s[0] = 0.0f;
		for (int i = 1; i < s.length; i++) {
			s[i] = samples[i] - alpha * samples[i - 1];
		}
		return s;
	}
	
	public static int timeToSamples(int time, int sampleRate) {
		return time * sampleRate / 1000;
	}
	
	public static int samplesToTime(int samples, int sampleRate) {
		return samples * 1000 / sampleRate;
	}
}
