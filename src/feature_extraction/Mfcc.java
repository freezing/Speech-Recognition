package feature_extraction;

public class Mfcc {
	
	private double[][] filterBank;
	
	private double sampleRate;
	private int frameLength;
	private int melFilterCount;
	
	public Mfcc(int frameLength, int sampleRate, int melFilterCount) {
		initialize(frameLength, sampleRate, melFilterCount);
	}

	private void initialize(int frameLength, int sampleRate, int melFilterCount) {
		this.sampleRate = sampleRate;
		this.frameLength = frameLength;
		this.melFilterCount = melFilterCount;
		
		initializeFilterBanks();
	}

	private void initializeFilterBanks() {
		final double lowerFreq = 300.0f;
		final double higherFreq = sampleRate / 2.0f;
		
		final double lowerMel = freqToMel(lowerFreq);
		final double higherMel = freqToMel(higherFreq);
		
		int[] f = new int[melFilterCount + 2];
		
		for (int i = 0; i < f.length; i++) {
			double h = melToFreq(lowerMel + i * (higherMel - lowerMel) / (melFilterCount + 1));
			f[i] = (int)Math.floor((frameLength + 1) * h / sampleRate);
		}
		
		filterBank = new double[melFilterCount][frameLength / 2];
		for (int i = 1; i <= melFilterCount; i++) {
			for (int k = 0; k < frameLength / 2; k++) {
				if (k < f[i - 1]) {
					filterBank[i - 1][k] = 0.0f;
				}
				else if (k <= f[i]) {
					filterBank[i - 1][k] = (double)(k - f[i - 1]) / (f[i] - f[i - 1]);
				}
				else if (k <= f[i + 1]) {
					filterBank[i - 1][k] = (double)(f[i + 1] - k) / (f[i + 1] - f[i]);
				}
				else {
					filterBank[i - 1][k] = 0.0f;
				}
			}
		}
	}
	
	
	public double[] calculateFeaturesForWindow(double[] samples, int sampleStart, int sampleEnd, int numberOfFeatures) {
		if (sampleStart < 0) {
			sampleStart = 0;
		}
		
		if (sampleEnd > samples.length) {
			sampleEnd = samples.length;
		}
		
		if (sampleStart >= sampleEnd) {
			return null;
		}
		
		double[] tmpSamples = new double[sampleEnd - sampleStart];
		for (int i = 0; i < tmpSamples.length; i++) {
			tmpSamples[i] = samples[i + sampleStart];
		}
		
		tmpSamples = Utils.preEmphasis(tmpSamples, 0.95f);
		tmpSamples = WindowFunctions.applyWindowFunction(tmpSamples);
		
		FFT fft = new FFT();
		fft.computeFFT(tmpSamples);
		double[] powerSpectrum = fft.getSpectrum(); 
		
		double[] logEnergies = calculateLogEnergies(powerSpectrum);
		double[] dct = DCT.performDCT(logEnergies);
		
		double[] features = new double[numberOfFeatures];
		for (int i = 1; i <= numberOfFeatures; i++) {
			features[i - 1] = dct[i];
		}
		return features;
		
	}

	public double[] calculateFeaturesForWindow(double[] samples) {
		return calculateFeaturesForWindow(samples, 0, samples.length, 12);
	}
	
	private double calculateFilterBankLogEnergy(double[] samples, int melIdx) {
		double res = 0.0f;
		for (int i = 0; i < samples.length / 2; i++) {
			res += samples[i] * filterBank[melIdx][i];
		}
		return (double)Math.log(res);
	}
	
	private double[] calculateLogEnergies(double[] powerSpectrum) {
		double[] logEnergies = new double[melFilterCount];
		for (int i = 0; i < melFilterCount; i++) {
			logEnergies[i] = calculateFilterBankLogEnergy(powerSpectrum, i);
		}
		return logEnergies;
	}
	
	private double freqToMel(double frequency) {
		return (double) (1125.0 * Math.log(1.0 + frequency / 700.0));
	}
	
	private double melToFreq(double mel) {
		double t = (double) Math.pow(Math.E, mel / 1125.0);
		return 700.0f * (t - 1.0f);
	}
}
