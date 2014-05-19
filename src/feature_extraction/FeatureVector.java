package feature_extraction;

import java.io.Serializable;

public class FeatureVector implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private float[][] mfccFeature;
	private float[][] featureVector;
	
	private int frameCount;
	private int featureCount;
	
	public FeatureVector() {}
	
	public float[][] getMfccFeature() {
		return mfccFeature;
	}
	
	public void setMfccFeature(float[][] mfccFeature) {
		this.mfccFeature = mfccFeature;
	}
	
	public float[][] getFeatureVector() {
		return featureVector;
	}
	
	public void setFeatureVector(float[][] featureVector) {
		this.featureVector = featureVector;
	}
	
	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}
	
	public int getFrameCount() {
		return frameCount;
	}
	
	public void setFeatureCount(int featureCount) {
		this.featureCount = featureCount;
	}
	
	public int getFeatureCount() {
		return featureCount;
	}
}
