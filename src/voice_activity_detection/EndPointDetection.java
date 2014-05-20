package voice_activity_detection;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import feature_extraction.Energy;
import feature_extraction.FFT;
import feature_extraction.WindowFunctions;

public class EndPointDetection {
	public static final int DEFAULT_WINDOW_COUNT = 10;

	public static final int DEFAULT_E_PRIMARY_THRESHOLD = 4;
	public static final int DEFAULT_F_PRIMARY_THRESHOLD = 185;
	public static final int DEFAULT_SFM_PRIMARY_THRESHOLD = 5;

	private double[] originalSamples;
	private double[] currentSamples;
	private double[] silenceRemovedSamples;

	private int sampleRate;
	private int noiseSamples;
	private int frameLength;
	private int windowCount;
	private int mergeThreshold;
	private int spikeThreshold;

	private List<Interval> voiceActivityDetected;

	private double minimalNoiseThreshold;

	public EndPointDetection(double[] originalSamples, int sampleRate) {
		this.originalSamples = originalSamples;
		this.currentSamples = originalSamples;
		this.sampleRate = sampleRate;
		this.windowCount = DEFAULT_WINDOW_COUNT;

		frameLength = 512;// sampleRate / 100;
		noiseSamples = sampleRate * 200 / 1000;

		mergeThreshold = 10 * frameLength;
		spikeThreshold = 5 * frameLength;

		minimalNoiseThreshold = Energy.calculateMean(originalSamples);
		System.out
				.println("Minimal noise threhsold = " + minimalNoiseThreshold);
	}

	public EndPointDetection(double[] originalSamples, int sampleRate,
			int windowCount, int mergeThreshold, int spikeThreshold) {
		this.originalSamples = originalSamples;
		this.currentSamples = originalSamples;
		this.sampleRate = sampleRate;
		this.windowCount = windowCount;

		frameLength = sampleRate / 100;
		noiseSamples = sampleRate * 200 / 1000;
		minimalNoiseThreshold = Energy.calculateMean(originalSamples);

		mergeThreshold = 10 * frameLength;
		spikeThreshold = 5 * frameLength;
	}

	public List<Interval> test() {
		double[] noiseSamples = Arrays.copyOfRange(originalSamples, 0,
				20 * frameLength);
		double noiseThreshold = 1.0 * Energy.calculateMean(noiseSamples) + 2.0
				* Energy.calculateEnergyStandardDeviation(noiseSamples);
		System.out.println("Noise Threshold = " + noiseThreshold);

		boolean vad[] = new boolean[originalSamples.length / frameLength];
		for (int i = 20, s = 20 * frameLength; i < vad.length; i++, s += frameLength) {
			double[] samples = Arrays.copyOfRange(originalSamples, s, s
					+ frameLength);
			double E = Energy.calculateMean(samples);

			if (E > noiseThreshold) {
				vad[i] = true;
			} else {
				vad[i] = false;
			}
		}

		List<Interval> intervals = new LinkedList<>();
		int start = -1, end = -1;
		for (int i = 29; i < vad.length; i++) {
			if (vad[i] && start == -1) {
				start = i * frameLength;
			} else if (!vad[i] && start != -1) {
				end = i * frameLength;
				intervals.add(new Interval(start, end));

				start = end = -1;
			}
		}
		return smooth(intervals);
	}

	public List<Interval> detectVoiceActivityIntervals() {
		voiceActivityDetected = new LinkedList<>();

		int frameCount = originalSamples.length / frameLength;
		FFT fft = new FFT();
		SpectralFlatnessMeasureExtractor sfmExtractor = new SpectralFlatnessMeasureExtractor();

		double min_E = Double.MAX_VALUE;
		double min_F = Double.MAX_VALUE;
		double min_SFM = Double.MAX_VALUE;
		System.out.println("FL = " + frameLength);
		int silenceCount = 0;

		for (int i = 0; i < frameCount; i++) {
			double[] frameSamples = Arrays.copyOfRange(originalSamples, i
					* frameLength, (i + 1) * frameLength);
			double[] windowedSamples = WindowFunctions
					.applyWindowFunction(frameSamples);
			fft.computeFFT(windowedSamples);
			double[] spectrum = fft.getSpectrum();

			double E = Energy.calculate(frameSamples);
			double F = getMostDominantFrequency(spectrum);
			double SFM = sfmExtractor.getSFMFromSpectrum(spectrum);

			if (i < 30) {
				min_E = Math.min(min_E, E);
				min_F = Math.min(min_F, F);
				min_SFM = Math.min(min_SFM, SFM);
			} else {
				double threshold_E = DEFAULT_E_PRIMARY_THRESHOLD
						* Math.log(min_E);
				double threshold_F = DEFAULT_F_PRIMARY_THRESHOLD;
				double threshold_SFM = DEFAULT_SFM_PRIMARY_THRESHOLD;

				int counter = 0;
				if (E - min_E >= threshold_E)
					counter++;
				if (F - min_F >= threshold_F)
					counter++;
				if (SFM - min_SFM >= threshold_SFM)
					counter++;

				if (counter > 1) {
					Interval interval = new Interval(i * frameLength, (i + 1)
							* frameLength);
					voiceActivityDetected.add(interval);
				} else {
					silenceCount++;
					min_E = ((silenceCount * min_E) + E) / (silenceCount + 1);
				}
			}
		}
		return smooth(voiceActivityDetected);
	}

	private double getMostDominantFrequency(double[] spectrum) {
		int idx = 0;
		for (int i = 0; i < spectrum.length; i++) {
			if (spectrum[i] > spectrum[idx]) {
				idx = i;
			}
		}
		return idx * sampleRate / frameLength;
	}

	public List<Interval> detectVoiceByEnergy() {
		voiceActivityDetected = new LinkedList<>();

		double noiseEnergyThreshold = calculateNoiseEnergyThreshold();
		int lastVoiceDetected = Integer.MIN_VALUE;
		System.out.println("Noise = " + noiseEnergyThreshold);

		int start = -1;
		int end = -1;

		double currentEnergy = Energy.calculate(Arrays.copyOf(originalSamples,
				frameLength * (windowCount)));
		for (int loopNumber = 0, i = frameLength * (windowCount); i < originalSamples.length
				- frameLength; i += frameLength, loopNumber++) {
			double prevWindowEnergyLevel = Energy.calculate(Arrays.copyOfRange(
					originalSamples, i - frameLength * windowCount, i
							- frameLength * windowCount + frameLength));
			double nextWindowEnergyLevel = Energy.calculate(Arrays.copyOfRange(
					originalSamples, i, i + frameLength));
			currentEnergy += nextWindowEnergyLevel - prevWindowEnergyLevel;

			double averageEnergy = currentEnergy / (frameLength * windowCount);

			if (averageEnergy > noiseEnergyThreshold) {
				// if previous window was unvoiced
				if (loopNumber - 1 != lastVoiceDetected) {
					// mark new start point
					start = (i - (windowCount - 1) * frameLength) + windowCount
							* frameLength / 2;
				}
				// update time of last voice detection
				lastVoiceDetected = loopNumber;
			} else {
				// if no voice activity is detected
				if (loopNumber - 1 == lastVoiceDetected) {
					// if last window was voiced

					// mark new endpoint
					end = (i - (windowCount - 1) * frameLength) + windowCount
							* frameLength / 2;

					// add new (start, end) pair to the list
					voiceActivityDetected.add(new Interval(start, end));
				}
			}
		}
		return voiceActivityDetected;
	}

	public List<double[]> detectVoice() {
		List<Interval> intervals = detectVoiceByEnergy();
		intervals = smooth(intervals);
		return getVoicedSamples(intervals);
	}

	private List<double[]> getVoicedSamples(List<Interval> intervals) {
		List<double[]> voicedSamples = new LinkedList<>();
		for (Interval interval : intervals) {
			double[] samples = new double[interval.getLength()];
			for (int i = 0; i < samples.length; i++) {
				samples[i] = originalSamples[i + interval.getStart()];
			}
			voicedSamples.add(samples);
		}
		return voicedSamples;
	}

	private List<Interval> smooth(List<Interval> intervals) {
		List<Interval> smoothed = smoothUp(intervals);
		return smoothDown(smoothed);
	}

	private List<Interval> smoothUp(List<Interval> intervals) {
		List<Interval> smoothed = new LinkedList<>();
		if (intervals.size() == 0) {
			return smoothed;
		}

		smoothed.add(intervals.get(0).clone());
		Interval prev = smoothed.get(0);

		for (int i = 1; i < intervals.size(); i++) {
			Interval current = intervals.get(i);

			if (prev.getEnd() + mergeThreshold >= current.getStart()) {
				prev.setEnd(current.getEnd());
			} else {
				prev = intervals.get(i).clone();
				smoothed.add(prev);
			}
		}
		return smoothed;
	}

	private List<Interval> smoothDown(List<Interval> intervals) {
		List<Interval> smoothed = new LinkedList<>();
		for (Interval interval : intervals) {
			if (interval.getLength() >= spikeThreshold) {
				smoothed.add(interval);
			}
		}
		return smoothed;
	}

	public double[] getNextWord() {

		// Detect VAD by energy
		boolean voiced[] = new boolean[currentSamples.length / frameLength];

		double noiseEnergyThreshold = calculateNoiseEnergyThreshold();
		System.out.println("Noise Energy Threshold = " + noiseEnergyThreshold);
		int usefulFrameCount = 0;

		int firstFrame = -1;
		int lastFrame = -1;

		for (int i = 0, frameIdx = 0; i < currentSamples.length - frameLength; i += frameLength, frameIdx++) {
			double frameEnergy = Energy.calculate(Arrays.copyOfRange(
					currentSamples, i, i + frameLength));
			if (frameEnergy > noiseEnergyThreshold) {
				voiced[frameIdx] = true;
				if (firstFrame == -1) {
					firstFrame = frameIdx;
				}

				lastFrame = frameIdx;
				usefulFrameCount++;
			} else {
				voiced[frameIdx] = false;
			}
		}
		double[] noiseRemoved = new double[(lastFrame - firstFrame + 1)
				* frameLength];
		for (int i = firstFrame * frameLength; i < (lastFrame + 1)
				* frameLength; i++) {
			noiseRemoved[i - firstFrame * frameLength] = currentSamples[i];
		}
		return noiseRemoved;
	}

	public double calculateNoiseEnergyThreshold() {
		double mean = Energy.calculateMean(currentSamples);
		double stDev = Energy.calculateStandardDeviation(currentSamples, mean);
		return minimalNoiseThreshold + mean + 1.0 * stDev;
	}

	public double[] detectEndPoints() {
		boolean[] voiced = new boolean[originalSamples.length];

		double mx = 0.0;
		for (double a : originalSamples) {
			mx = Math.max(mx, a);
		}

		double sd = 0.0f;
		double mean = 0.0f;

		// Calculate mean
		for (int i = 0; i < noiseSamples; i++) {
			mean += originalSamples[i];
		}
		mean /= noiseSamples;

		System.out.println("Mean = " + mean);

		// Calculate standard deviation
		for (int i = 0; i < noiseSamples; i++) {
			sd += (originalSamples[i] - mean) * (originalSamples[i] - mean);
		}
		sd = (double) Math.sqrt(sd / noiseSamples);

		System.out.println("Standard Deviation = " + sd);

		// Check if 1D Mahalanobis distance function
		// |x-u|/s is greater than 2 or not
		for (int i = 0; i < originalSamples.length; i++) {
			double v = (Math.abs(originalSamples[i] - mean) / sd);
			if (v > 2) {
				voiced[i] = true;
			} else {
				voiced[i] = false;
			}
		}

		// Mark each frame to be voiced or unvoiced
		int frameCount = 0;
		int usefulFrameCount = 0;
		int countVoiced;
		int countUnvoiced;

		boolean voicedFrame[] = new boolean[originalSamples.length
				/ frameLength];

		for (int i = 0; i < originalSamples.length - frameLength; i += frameLength) {
			countVoiced = 0;
			countUnvoiced = 0;

			for (int j = i; j < i + frameLength; j++) {
				if (voiced[j]) {
					countVoiced++;
				} else {
					countUnvoiced++;
				}
			}

			if (countVoiced > countUnvoiced) {
				usefulFrameCount++;
				voicedFrame[frameCount++] = true;
			} else {
				voicedFrame[frameCount++] = false;
			}
		}

		// Remove silence
		silenceRemovedSamples = new double[usefulFrameCount * frameLength];
		int k = 0;
		for (int i = 0; i < frameCount; i++) {
			if (voicedFrame[i]) {
				for (int j = i * frameLength; j < i * frameLength + frameLength; j++) {
					silenceRemovedSamples[k++] = originalSamples[j];
				}
			}
		}
		return silenceRemovedSamples;
	}

}
