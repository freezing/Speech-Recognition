package voice_activity_detection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import utility.Utils;

public class EndPointDetector2 {
	// false means return intervals in samples
	private static boolean TIME = true;
	private static final double SCALE = 50.0;
	
	// window length in milliseconds
	private int windowLength;
	
	private int sampleRate;
	
	private int windowSamples;
	
	public EndPointDetector2(int windowLength, int sampleRate) {
		this.sampleRate = sampleRate;
		this.windowLength = windowLength;
		this.windowSamples = windowLength * sampleRate / 1000;
	}
	
	/**
	 * 
	 * @param samples
	 * @param noiseWindowCount
	 * @param windowCount
	 * @return VAD intervals in sample units
	 */
	public List<Interval> detectEndPoints(double[] samples, int noiseWindowCount, int windowCount, int zcrExpandLength,
			int spikeLength, int mergeLength) {
		double SNR = Utils.getSNR(samples, noiseWindowCount * windowSamples);
		System.out.println("SNR = " + SNR);
		
		int totalWindowCount = samples.length / windowSamples;
		Threshold threshold = getThreshold(samples, noiseWindowCount);
		
		boolean[] vad = new boolean[totalWindowCount];
		
		List<Double> energies = new LinkedList<>();
		for (int i = 0; i < windowCount - 1; i++) {
			double[] window = getSamples(samples, i * windowSamples, windowSamples);
			energies.add(Utils.getEnergy(window));
		}
		
		for (int i = windowCount; i < totalWindowCount; i++) {
			double[] window = getSamples(samples, i * windowSamples, windowSamples);
			double energy = Utils.getEnergy(window);
			energies.add(energy);
			energies.remove(0);
			
			double W = Utils.getPower(energies.toArray(new Double[energies.size()]));

			if (W > threshold.getEnergy()) {
				vad[i] = true;
			}
			else {
				vad[i] = false;
			}
		}

		System.out.println("========= ENERGY ===========");
		List<Interval> intervals = getIntervals(vad, windowCount, TIME);
		for (Interval i : intervals) {
			System.out.println(i);
		}
		System.out.println("===============================");
		
		int mergeFrames = mergeLength / windowLength;
		smoothUp(vad, mergeFrames);
		
		System.out.println("========= SMOOTH UP ===========");
		intervals = getIntervals(vad, windowCount, TIME);
		for (Interval i : intervals) {
			System.out.println(i);
		}
		System.out.println("===============================");
		
		int spikeFrames = spikeLength / windowLength;
		smoothDown(vad, spikeFrames);
		
		System.out.println("========= SMOOTH DOWN ===========");
		intervals = getIntervals(vad, windowCount, TIME);
		for (Interval i : intervals) {
			System.out.println(i);
		}
		System.out.println("===============================");
		
		int zcrExpandFrames = zcrExpandLength / windowLength;
		expandByZcr(samples, windowLength, zcrExpandFrames, vad, threshold);
		
		System.out.println("========= ZCR ===========");
		intervals = getIntervals(vad, windowCount, TIME);
		for (Interval i : intervals) {
			System.out.println(i);
		}
		System.out.println("===============================");
		List<Interval> ii = new ArrayList<>();
		ii.add(new Interval(0, samples.length));
		// TODO: RETURN REAL INTERVALS
	//	return ii;
		return getIntervals(vad, windowCount, false);
	}	
	
	private void expandByZcr(double[] samples, int windowLength, int zcrExpandFrames, boolean[] vad, Threshold threshold) {
		for (int frameId = 1; frameId < vad.length - 1; frameId++) {
			// Expand left
			if (vad[frameId] && !vad[frameId - 1]) {
				int bound = Math.max(0, frameId - zcrExpandFrames);
				// frameId * windowLength gives beginning of the window (frameId) in milliseconds
				int frameSamples = windowLength * sampleRate / 1000;
				int start = frameId * frameSamples;
				for (int j = frameId - 1; j >= bound; j--) {
					start -= frameSamples;
					double[] currentWindow = Utils.getSubArray(samples, start, frameSamples);
					double zcr = Utils.getZcr(currentWindow);
					if (zcr < threshold.getZcr()) {
						break;
					}
					vad[j] = true;
				}
			}
			
			// Expand right
			if (vad[frameId] && !vad[frameId + 1]) {
				int bound = Math.min(vad.length, frameId + zcrExpandFrames);
				// frameId * windowLength gives beginning of the window (frameId) in milliseconds
				int frameSamples = windowLength * sampleRate / 1000;
				int start = frameId * frameSamples;
				for (int j = frameId + 1; j < bound; j++) {
					start += frameSamples;
					double[] currentWindow = Utils.getSubArray(samples, start, frameSamples);
					double zcr = Utils.getZcr(currentWindow);
					System.out.println("Current ZCR = " + zcr);
					if (zcr < threshold.getZcr()) {
						break;
					}
					vad[j] = true;
				}
			}
		}
	}

	private void smoothDown(boolean[] vad, int spikeFrames) {
		int count = 0;
		for (int i = 0; i < vad.length; i++) {
			if (vad[i]) {
				count++;
			}
			else if (count < spikeFrames) {
				for (int j = i - count; j < i; j++) {
					vad[j] = false;
				}
				count = 0;
			}
		}
	}

	private void smoothUp(boolean[] vad, int mergeFrames) {
		int last = -1;
		for (int i = 1; i < vad.length - 1; i++) {
			// If it is beginning of the interval
			if (vad[i] && !vad[i - 1]) {
				// If this is not the first interval
				// and distance to previous interval is less than mergeFrames
				if (last != -1 && i - last < mergeFrames) {
					for (int j = last; j < i; j++) {
						vad[j] = true;
					}
				}
			}
			
			// If it is last frame in interval, update last speech window index
			if (vad[i] && !vad[i + 1]) {
				last = i;
			}
		}
	}

	private List<Interval> getIntervals(boolean[] vad, int windowCount, boolean milliseconds) {
		List<Interval> intervals = new ArrayList<>();
		
		boolean tmp = vad[vad.length - 1];
		vad[vad.length - 1] = false;
		
		int time = windowSamples;
		if (milliseconds) {
			time = windowLength;
		}
		
		int s = -1, e = -1;
		for (int i = 0; i < vad.length; i++) {
			if (vad[i]) {
				if (s == -1) {
					s = i;
				}
			}
			else {
				if (s != -1) {
					e = i;
					intervals.add(new Interval(
							(s - windowCount / 2) * time, 
							(e - windowCount / 2) * time)
					);
					s = e = -1;
				}
			}
		}
		
		if (s != -1) {
			e = vad.length;
			intervals.add(new Interval(
					(s - windowCount / 2) * windowLength, 
					(e - windowCount / 2) * windowLength)
			);
		}
		
		vad[vad.length - 1] = tmp;
		return intervals;
	}

	private Threshold getThreshold(double[] samples, int noiseWindowCount) {
		double[] noiseSamples = getSamples(samples, 0, noiseWindowCount * windowLength * sampleRate / 1000);
		double[] e = new double[noiseWindowCount];
		double[] zcr = new double[noiseWindowCount];
		
		for (int i = 0; i < noiseWindowCount; i++) {
			double[] noise = getSamples(noiseSamples, i * windowSamples, windowSamples);
			e[i] = Utils.getPower(noise);
			zcr[i] = Utils.getZcr(noise);
		}
		
		double energyMean = Utils.getMean(e);
		double energyStDev = Utils.getStDev(e, energyMean);
		double energyThr = 1.5 * energyMean + /* TODO: CHECK getA(energyStDev)*/ 3.0 * energyStDev;
		
		System.out.println("Energy mean = " + energyMean);
		System.out.println("Energy Standard Deviation = " + energyStDev);
		System.out.println("Energy Threshold = " + energyThr * SCALE);
		System.out.println("Coefficient A = " + getA(energyStDev));
		
		double zcrMean = Utils.getMean(zcr);
		double zcrStDev = Utils.getStDev(zcr, zcrMean);
		double zcrThr = zcrMean + 1.0 * zcrStDev;
		
		System.out.println();
		System.out.println("ZCR Mean = " + zcrMean);
		System.out.println("ZCR Standard Deviation = " + zcrStDev);
		System.out.println("ZCR Threshold = " + zcrThr);
		System.out.println();
		
		return new Threshold(energyThr, zcrThr);
	}
	
	private double getA(double variance) {
		return 0.3 * Math.pow(variance, -0.92);
	}

	private double[] getSamples(double[] samples, int start, int length) {
		double[] tmp = new double[length];
		for (int i = start; i < start + length; i++) {
			tmp[i - start] = samples[i];
		}
		return tmp;
	}
	
	private static class Threshold {
		private double energy;
		private double zcr;
		
		public Threshold(double energy, double zcr) {
			this.energy = energy;
			this.zcr = zcr;
		}
		
		public double getEnergy() {
			return energy * SCALE;
		}
		
		public double getZcr() {
			return zcr;
		}
	}

}
