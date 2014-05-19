package feature_extraction;

public class Mfcc {
	
	private float[][] filterBank;
	
	private float sampleRate;
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
		final float lowerFreq = 300.0f;
		final float higherFreq = sampleRate / 2.0f;
		
		final float lowerMel = freqToMel(lowerFreq);
		final float higherMel = freqToMel(higherFreq);
		
		int[] f = new int[melFilterCount + 2];
		
		for (int i = 0; i < f.length; i++) {
			float h = melToFreq(lowerMel + i * (higherMel - lowerMel) / (melFilterCount + 1));
			f[i] = (int)Math.floor((frameLength + 1) * h / sampleRate);
		}
		
		filterBank = new float[melFilterCount][frameLength / 2];
		for (int i = 1; i <= melFilterCount; i++) {
			for (int k = 0; k < frameLength / 2; k++) {
				if (k < f[i - 1]) {
					filterBank[i - 1][k] = 0.0f;
				}
				else if (k <= f[i]) {
					filterBank[i - 1][k] = (float)(k - f[i - 1]) / (f[i] - f[i - 1]);
				}
				else if (k <= f[i + 1]) {
					filterBank[i - 1][k] = (float)(f[i + 1] - k) / (f[i + 1] - f[i]);
				}
				else {
					filterBank[i - 1][k] = 0.0f;
				}
			}
		}
	}
	
	
	public float[] calculateFeaturesForWindow(float[] samples, int sampleStart, int sampleEnd, int numberOfFeatures) {
		if (sampleStart < 0) {
			sampleStart = 0;
		}
		
		if (sampleEnd > samples.length) {
			sampleEnd = samples.length;
		}
		
		if (sampleStart >= sampleEnd) {
			return null;
		}
		
		float[] tmpSamples = new float[sampleEnd - sampleStart];
		for (int i = 0; i < tmpSamples.length; i++) {
			tmpSamples[i] = samples[i + sampleStart];
		}
		
		tmpSamples = Utils.preEmphasis(tmpSamples, 0.95f);
		tmpSamples = WindowFunctions.applyWindowFunction(tmpSamples);
		
		FFT fft = new FFT();
		fft.computeFFT(tmpSamples);
		float[] powerSpectrum = fft.getSpectrum(); 
		
		float[] logEnergies = calculateLogEnergies(powerSpectrum);
		float[] dct = DCT.performDCT(logEnergies);
		
		float[] features = new float[numberOfFeatures];
		for (int i = 1; i <= numberOfFeatures; i++) {
			features[i - 1] = dct[i];
		}
		return features;
		
	}

	public float[] calculateFeaturesForWindow(float[] samples) {
		return calculateFeaturesForWindow(samples, 0, samples.length, 12);
	}
	
	private float calculateFilterBankLogEnergy(float[] samples, int melIdx) {
		float res = 0.0f;
		for (int i = 0; i < samples.length / 2; i++) {
			res += samples[i] * filterBank[melIdx][i];
		}
		return (float)Math.log(res);
	}
	
	private float[] calculateLogEnergies(float[] powerSpectrum) {
		float[] logEnergies = new float[melFilterCount];
		for (int i = 0; i < melFilterCount; i++) {
			logEnergies[i] = calculateFilterBankLogEnergy(powerSpectrum, i);
		}
		return logEnergies;
	}
	
	private float freqToMel(float frequency) {
		return (float) (1125.0 * Math.log(1.0 + frequency / 700.0));
	}
	
	private float melToFreq(float mel) {
		float t = (float) Math.pow(Math.E, mel / 1125.0);
		return 700.0f * (t - 1.0f);
	}
}
