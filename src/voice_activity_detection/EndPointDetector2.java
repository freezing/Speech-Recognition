package voice_activity_detection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import utility.Utils;

public class EndPointDetector2 {
	
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
	public List<Interval> detectEndPoints(double[] samples, int noiseWindowCount, int windowCount) {
		double SNR = Utils.getSNR(samples, noiseWindowCount * windowSamples);
		System.out.println("SNR = " + SNR);
		
		int totalWindowCount = samples.length / windowSamples;
		Threshold threshold = getThreshold(samples, noiseWindowCount);
		
		boolean[] vad = new boolean[totalWindowCount];
		
		List<Double> energyMeans = new LinkedList<>();
		for (int i = 0; i < windowCount - 1; i++) {
			double[] window = getSamples(samples, i * windowSamples, windowSamples);
			energyMeans.add(Utils.getEnergy(window));
		}
		
		for (int i = windowCount; i < totalWindowCount; i++) {
			double[] window = getSamples(samples, i * windowSamples, windowSamples);
			double energyMean = Utils.getEnergy(window);
			energyMeans.add(energyMean);
			energyMeans.remove(0);
			
			double W = Utils.getPower(energyMeans.toArray(new Double[energyMeans.size()]));
		//	System.out.println(W);
			if (W > threshold.getEnergy()) {
				vad[i] = true;
			}
			else {
				vad[i] = false;
			}
		}
		
		List<Interval> intervals = getIntervals(vad, windowCount);
		return intervals;
	}	
	
	private List<Interval> getIntervals(boolean[] vad, int windowCount) {
		List<Interval> intervals = new ArrayList<>();
		
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
							(s - windowCount / 2) * windowSamples, 
							(e - windowCount / 2) * windowSamples)
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
		double energyThr = 1.0 * energyMean + getA(energyStDev) * energyStDev;
		
		System.out.println("Energy mean = " + energyMean);
		System.out.println("Energy Standard Deviation = " + energyStDev);
		System.out.println("Energy Threshold = " + energyThr * SCALE);
		System.out.println("Coefficient A = " + getA(energyStDev));
		
		double zcrMean = Utils.getMean(zcr);
		double zcrStDev = Utils.getStDev(zcr, zcrMean);
		double zcrThr = zcrMean + 1.0 * zcrStDev;
		
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
