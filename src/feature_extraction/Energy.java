package feature_extraction;

public class Energy {
	public static double[] calculate(double[][] framedSamples) {
		double[] energy = new double[framedSamples.length];
		for (int i = 0; i < framedSamples.length; i++) {
			energy[i] = (double)Math.log(calculate(framedSamples[i]));
		}
		return energy;
	}
	
	public static double calculate(double[] samples) {
		double sum = 0.0;
		for (int i = 0; i < samples.length; i++) {
			sum += Math.abs(samples[i]);//samples[i] * samples[i];
		}
		return sum;
	}
	
	public static double calculateMean(double[] samples) {
		return calculate(samples) / samples.length;
	}
	
	public static double calculateStandardDeviation(double[] samples, double mean) {
		double variance = 0.0;
		for (double sample : samples) {
			double difference = sample - mean;
			variance += difference * difference;
		}
		variance /= samples.length;
		return Math.sqrt(variance);
	}
	
	public static double calculateEnergyStandardDeviation(double[] samples) {
		double mean = calculateMean(samples);
		return calculateStandardDeviation(samples, mean);
	}
}
