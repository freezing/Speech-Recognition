package preprocessing;

public class PreProcessor {
	public static void normalizePCM(double[] originalSignal) {
		double max = originalSignal[0];
		for (int i = 1; i < originalSignal.length; i++) {
			if (max < Math.abs(originalSignal[i])) {
				max = Math.abs(originalSignal[i]);
			}
		}

		for (int i = 0; i < originalSignal.length; i++) {
			originalSignal[i] = originalSignal[i] * max;
		}
	}
}
