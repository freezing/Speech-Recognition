package feature_extraction;

public class Energy {
	public static float[] calculate(float[][] framedSamples) {
		float[] energy = new float[framedSamples.length];
		for (int i = 0; i < framedSamples.length; i++) {
			energy[i] = (float)Math.log(calculate(framedSamples[i]));
		}
		return energy;
	}
	
	public static float calculate(float[] samples) {
		float sum = 0.0f;
		for (int i = 0; i < samples.length; i++) {
			sum += samples[i] * samples[i];
		}
		return sum;
	}
}
