package feature_extraction;

public class Delta {
	
	public static float[][] calculateDelta2D(float[][] data, int regressionWindowLength) {
		final int M = regressionWindowLength;
		
		int featureCount = data[0].length;
		int frameCount = data.length;
		
		float mSquareSum = 0.0f;
		for (int i = -M; i < M; i++) {
			mSquareSum += Math.pow(i, 2);
		}
		
		float delta[][] = new float[frameCount][featureCount];
		for (int i = 0; i < featureCount; i++) {
			for (int k = 0; k < M; k++) {
				delta[k][i] = data[k][i];
			}
			
			for (int k = frameCount - M; k < frameCount; k++) {
				delta[k][i] = data[k][i];
			}
			
			for (int j = M; j < frameCount; j++) {
				float sumDataMulM = 0.0f;
				for (int m = -M; m <= M; m++) {
					sumDataMulM += m * data[m + j][i];
				}
				delta[j][i] = sumDataMulM / mSquareSum;
			}
		}
		return delta;
	}
	
	public static float[] calculateDelta1D(float[] data, int regressionWindowLength) {
		final int M = regressionWindowLength;
		
		int frameCount = data.length;
		
		float mSquareSum = 0.0f;
		for (int i = -M; i < M; i++) {
			mSquareSum += Math.pow(i, 2);
		}
		
		float[] delta = new float[frameCount];
		for (int k = 0; k < M; k++) {
			delta[k] = data[k];
		}
		
		for (int k = frameCount - M; k < frameCount; k++) {
			delta[k] = data[k];
		}
		
		for (int j = M; j < frameCount - M; j++) {
			float sumDataMulM = 0.0f;
			for (int m = -M; m <= M; m++) {
				sumDataMulM += m * data[m + j];
			}
			delta[j] = sumDataMulM / mSquareSum;
		}
		
		return delta;
	}
}
