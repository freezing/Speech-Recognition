package model;

public class Recording {
	private double[] samples;
	private int sampleRate;
	
	public Recording(double[] samples, int sampleRate) {
		this.samples = samples;
		this.sampleRate = sampleRate;
	}
	
	public int getSampleRate() {
		return sampleRate;
	}
	
	public double[] getSamples() {
		return samples;
	}
}
