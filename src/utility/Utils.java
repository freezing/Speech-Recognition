package utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import threads.Capture;
import voice_activity_detection.Interval;

public class Utils {
	public static double getEnergy(double[] samples) {
		double e = 0.0;
		for (double s : samples) {
			e += s * s;
		}
		return e;
	}
	
	public static double getEnergy(Double[] samples) {
		double e = 0.0;
		for (double s : samples) {
			e += s * s;
		}
		return e;
	}
	
	public static double getPower(double[] samples) {
		return getEnergy(samples) / samples.length;
	}
	
	public static double getPower(Double[] samples) {
		return getEnergy(samples) / samples.length;
	}
	
	public static double getZcr(double[] samples) {
		double zcr = 0.0;
		for (int i = 1; i < samples.length; i++) {
			zcr += (sgn(samples[i]) - sgn(samples[i - 1])) / 2.0;
		}
		return zcr / samples.length;
	}
	
	public static double getWindowValue(double[] samples, double scale) {
		double P = getPower(samples);
		double Z = getZcr(samples);
		return P * (1 - Z) * scale;
	}
	
	public static int sgn(double a) {
		if (a < 0.0) {
			return -1;
		}
		else if (a > 0.0) {
			return +1;
		}
		else {
			return 0;
		}
	}

	public static double getMean(double[] w) {
		double mean = 0.0;
		for (double a : w) {
			mean += a;
		}
		return mean / w.length;
	}
	
	public static double getVariance(double[] a, double mean) {
		double var = 0.0;
		for (double s : a) {
			var += (s - mean) * (s - mean);
		}
		return var / a.length;
	}
	
	public static double getVariance(double[] a) {
		double mean = getMean(a);
		return getVariance(a, mean);
	}
	
	public static double getStDev(double[] a, double mean) {
		return Math.sqrt(getVariance(a, mean));
	}

	public static double getSNR(double[] samples, int noiseLength) {
		double[] noise = getSubArray(samples, 0, noiseLength);
		double[] signal = getSubArray(samples, noiseLength, samples.length - noiseLength);
		
		double varN = getVariance(noise);
		double varS = getVariance(signal);
		return varS / varN;
	}

	public static double[] getSubArray(double[] a, int start, int length) {
		double[] tmp = new double[length];
		for (int i = start; i < start + length; i++) {
			tmp[i - start] = a[i];
		}
		return tmp;
	}

	private static AudioFormat audioFormatInstance = null;
	public static AudioFormat getAudioFormat() {
		if (audioFormatInstance == null) {
			audioFormatInstance = new AudioFormat(22050, 16, 1, true, true);
		}
		return audioFormatInstance;
	}

	public static AudioInputStream saveWavFile(String path, byte[] audioBytes, Interval interval, AudioFormat format) {
		File file = new File(path);
		
		AudioInputStream audioInputStreamSlice = getAudioInputStreamSlice(audioBytes, interval, format);
		
		if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, audioInputStreamSlice)) {
			try {
				AudioSystem.write(audioInputStreamSlice, AudioFileFormat.Type.WAVE, file);
				return audioInputStreamSlice;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}

	public static AudioInputStream getAudioInputStreamSlice(byte[] audioBytes,
			Interval interval, AudioFormat format) {
		int frameSize = format.getFrameSize();
		
		if (frameSize == 2 && format.isBigEndian()) {
			byte[] slice = new byte[interval.getLength() * 2];
			for (int i = interval.getStart(); i < interval.getEnd(); i++) {
				int idx = i - interval.getStart();
				slice[2 * idx] = audioBytes[2 * i];
				slice[2 * idx + 1] = audioBytes[2 * i + 1];
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(slice);
			int frameSizeInBytes = format.getFrameSize();
			return new AudioInputStream(bais, format, slice.length
					/ frameSizeInBytes);
		}
		else {
			return null;
		}
	}

	public static double[] getSamples(AudioInputStream audioInputStream) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AudioFormat format = audioInputStream.getFormat();
		
		int frameSizeInBytes = format.getFrameSize();
		long bufferLengthInBytes = audioInputStream.getFrameLength() * frameSizeInBytes;
		byte[] data = new byte[(int)bufferLengthInBytes];
		int numBytesRead;

		try {
			audioInputStream.reset();
			while ((numBytesRead = audioInputStream.read(data, 0, (int) bufferLengthInBytes)) != -1) {
				out.write(data, 0, numBytesRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] audioBytes = out.toByteArray();
		return extractSamples(audioBytes, format);
	}
	
	public static double[] extractSamples(byte[] audioBytes, AudioFormat format) {
		// convert
		double[] audioData = null;
		if (format.getSampleSizeInBits() == 16) {
			int nlengthInSamples = audioBytes.length / 2;
			audioData = new double[nlengthInSamples];
			if (format.isBigEndian()) {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is MSB (high order) */
					int MSB = audioBytes[2 * i];
					/* Second byte is LSB (low order) */
					int LSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			} else {
				for (int i = 0; i < nlengthInSamples; i++) {
					/* First byte is LSB (low order) */
					int LSB = audioBytes[2 * i];
					/* Second byte is MSB (high order) */
					int MSB = audioBytes[2 * i + 1];
					audioData[i] = MSB << 8 | (255 & LSB);
				}
			}
		} else if (format.getSampleSizeInBits() == 8) {
			int nlengthInSamples = audioBytes.length;
			audioData = new double[nlengthInSamples];
			if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i];
				}
			} else {
				for (int i = 0; i < audioBytes.length; i++) {
					audioData[i] = audioBytes[i] - 128;
				}
			}
		}
		
		for (int i = 0; i < audioData.length; i++) {
			audioData[i] /= Short.MAX_VALUE;
		}
		return audioData;
	}
}
