package threads;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import utility.Utils;

/**
 * Reads data from the input channel and writes to the output stream
 */
public class Capture implements Runnable {

	TargetDataLine line;
	Thread thread;
	byte[] audioBytes;
	AudioInputStream audioInputStream;

	public void start() {
		thread = new Thread(this);
		thread.setName("Capture");
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	public void run() {
		double duration = 0;
		audioInputStream = null;

		// define the required attributes for our line,
		// and make sure a compatible line is supported.

		AudioFormat format = Utils.getAudioFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info)) {
			System.err.println("Line supported info error: " + info);
			return;
		}

		// get and open the target data line for capture.

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
		} catch (LineUnavailableException ex) {
			System.err.println("Unable to open the line: " + ex);
			return;
		} catch (SecurityException ex) {
			System.err.println(ex.toString());
			// JavaSound.showInfoDialog();
			return;
		} catch (Exception ex) {
			System.err.println(ex.toString());
			return;
		}

		// play back the captured audio data
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int frameSizeInBytes = format.getFrameSize();
		int bufferLengthInFrames = line.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead;

		line.start();

		while (thread != null) {
			if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
				break;
			}
			out.write(data, 0, numBytesRead);
		}

		// we reached the end of the stream. stop and close the line.
		line.stop();
		line.close();
		line = null;

		// stop and close the output stream
		try {
			out.flush();
			out.close();
		} catch (IOException ex) {
			System.err.println("Error on inputstream");
		}

		// load bytes into the audio input stream for playback

		audioBytes = out.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		audioInputStream = new AudioInputStream(bais, format, audioBytes.length
				/ frameSizeInBytes);

		long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format
				.getFrameRate());
		duration = milliseconds / 1000.0;

		try {
			audioInputStream.reset();
		} catch (Exception ex) {
			System.err.println("Eor in reseting inputStream");
		}
	}
	
	public byte[] getAudioBytes() {
		return audioBytes;
	}

	public AudioInputStream extractSamples(AudioFormat format) {
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
	//	return new Recording(audioData, (int)format.getSampleRate());
		return audioInputStream;
	}

	public AudioInputStream getAudioInputStream() {
		return audioInputStream;
	}
}