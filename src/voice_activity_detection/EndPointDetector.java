package voice_activity_detection;

import java.util.ArrayList;
import java.util.List;

import utility.Utils;


public class EndPointDetector {
	private static final double SCALE_FACTOR = 1000.0;
	
	private int sampleRate;
	private int windowSize;
	private int windowSamples;
	
	/**
	 * 
	 * @param sampleRate
	 * @param windowSize - window size in milliseconds
	 */
	public EndPointDetector(int sampleRate, int windowSize) {
		this.sampleRate = sampleRate;
		this.windowSize = windowSize;
		this.windowSamples = sampleRate * windowSize / 1000;
	}
	
	public List<Interval> getEndPoints(double[] samples, int noiseWindowCount) {
		int windowCount = samples.length / windowSamples;
		
		// Get noise samples
		double[] noiseSamples = getSamples(samples, 0, windowSamples * noiseWindowCount);
		
		// Calculate threshold
		double threshold = calculateThreshold(noiseSamples, noiseWindowCount);
		
		boolean[] vad = new boolean[windowCount];
		for (int i = 0; i < windowCount; i++) {
			double[] currentSamples = getSamples(samples, i * windowCount, windowSamples);
			double W = Utils.getWindowValue(currentSamples, SCALE_FACTOR);
			if (W >= threshold) {
				vad[i] = true;
			}
			else {
				vad[i] = false;
			}
		}
		
		List<Interval> intervals = new ArrayList<>();
		
		int s = -1, e = -1;
		for (int i = noiseWindowCount; i < vad.length; i++) {
			if (vad[i]) {
				if (s == -1) {
					s = i;
				}
			}
			else {
				if (s != -1) {
					e = i;
					intervals.add(new Interval(s * windowSize, (e + 1) * windowSize));
					s = e = -1;
				}
			}
		}
		
		return intervals;
	}

	private double[] getSamples(double[] samples, int start, int length) {
		double[] tmp = new double[length];
		for (int i = start; i < start + length; i++) {
			tmp[i - start] = samples[i];
		}
		return tmp;
	}

	private double calculateThreshold(double[] noiseSamples, int noiseWindowCount) {
		double[] W = new double[noiseWindowCount];
		for (int i = 0; i < noiseWindowCount; i++) {
			double[] samples = getSamples(noiseSamples, i * windowSamples, windowSamples);
			W[i] = Utils.getWindowValue(samples, SCALE_FACTOR);
		}
		
		double mean = Utils.getMean(W);
		double variance = Utils.getVariance(W, mean);
		
		double a = getA(variance);
		return mean + a * variance / 7.0;
	}

	private double getA(double variance) {
		return 0.3 * Math.pow(variance, -0.92);
	}
}
