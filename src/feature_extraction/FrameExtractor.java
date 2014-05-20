package feature_extraction;

public class FrameExtractor {
	public static double[][] extractFrames(double[] samples, int frameLength, int stepLength) {
		int N = samples.length;
		double[][] framedSamples = new double[(N - frameLength) / stepLength + 1][frameLength];
		
		for (int i = 0, frameIdx = 0; i < N - frameLength; i += stepLength, frameIdx++) {
			for (int j = i; j < i + frameLength; j++) {
				framedSamples[frameIdx][j - i] = samples[j];
			}
		}		
		return framedSamples;
	}
}
