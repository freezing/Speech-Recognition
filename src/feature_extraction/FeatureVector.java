package feature_extraction;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import vector_quantization.KDPoint;

public class FeatureVector implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private double[][] mfccFeature;
	private double[][] featureVector;
	
	private int frameCount;
	private int featureCount;
	
	public FeatureVector() {}
	
	public double[][] getMfccFeature() {
		return mfccFeature;
	}
	
	public void setMfccFeature(double[][] mfccFeature) {
		this.mfccFeature = mfccFeature;
	}
	
	public double[][] getFeatureVector() {
		return featureVector;
	}
	
	public void setFeatureVector(double[][] featureVector) {
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

	public List<KDPoint> toKDPointList() {
		List<KDPoint> list = new LinkedList<>();
		
		for (int i = 0; i < featureVector.length; i++) {
			list.add(new KDPoint(featureVector[i]));
		}
		
		return list;
	}
}
