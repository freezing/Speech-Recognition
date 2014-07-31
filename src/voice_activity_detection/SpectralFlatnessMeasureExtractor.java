package voice_activity_detection;

import feature_extraction.Fft;
import feature_extraction.WindowFunctions;

public class SpectralFlatnessMeasureExtractor {
	
	public SpectralFlatnessMeasureExtractor() {
		
	}
	
	/**
	 * SFM = 10 Log_10(Gm/Am) = 10 (Log_10 (Gm) - Log_10(Am))
	 */
	public double extractSpectralFlatnessMeasure(double[] samples) {
		double[] windowedSamples = WindowFunctions.applyWindowFunction(samples);
		double[] spectrum = new Fft(windowedSamples.length).calculateFFTMagnitude(windowedSamples);		
		return getSFMFromSpectrum(spectrum);
	}

	public double getSFMFromSpectrum(double[] spectrum) {
		double Am = 0.0;
		double Gm = 0.0;
		
		for (double value : spectrum) {
			Am += value;
			Gm += Math.log10(value);
		}
		
		Gm /= spectrum.length;
		Am /= spectrum.length;
		Am = Math.log10(Am);
		
		return 10.0 * (Gm - Am);
	}
}
