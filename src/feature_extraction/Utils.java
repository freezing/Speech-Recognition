package feature_extraction;

public class Utils {
	public static float[] preEmphasis(float[] samples, float alpha) {
		float[] s = new float[samples.length];
		
		s[0] = 0.0f;
		for (int i = 1; i < s.length; i++) {
			s[i] = samples[i] - alpha * samples[i - 1];
		}
		return s;
	}
}
