package feature_extraction;

public class DCT {

	public static float[] performDCT(float samples[]) {
		float[] res = new float[samples.length];
		
		for (int i = 1; i <= res.length; i++) {
			res[i - 1] = 0.0f;
			for (int j = 1; j <= res.length; j++) {
				res[i - 1] += samples[j - 1] * Math.cos(Math.PI * (i - 1) / res.length * (j - 0.5));
			}
		}
		return res;
	}
}
