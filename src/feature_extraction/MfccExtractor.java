package feature_extraction;

public class MfccExtractor {
	private float[][] framedSamples;
	
	private int frameCount;	
	private int numCepstra = 12;
	
	private float[][] featureVectorValues;
	private float[][] mfccFeature;
	private float[][] deltaMfcc;
	private float[][] deltaDeltaMfcc;
	
	private float[] energy;
	private float[] deltaEnergy;
	private float[] deltaDeltaEnergy;
	
	private FeatureVector featureVector;
	private Mfcc mfcc;
	
	public MfccExtractor(float[][] framedSamples, int sampleRate, int frameLength) {
		this.framedSamples = framedSamples;
		this.frameCount = framedSamples.length;
		
		mfcc = new Mfcc(frameLength, sampleRate, numCepstra);
		featureVector = new FeatureVector();
		
		mfccFeature = new float[frameCount][numCepstra];
		deltaMfcc = new float[frameCount][numCepstra];
		deltaDeltaMfcc = new float[frameCount][numCepstra];
		
		energy = new float[frameCount];
		deltaEnergy = new float[frameCount];
		deltaDeltaEnergy = new float[frameCount];
		
		featureVectorValues = new float[frameCount][3 * (numCepstra + 1)];
	}
	
	public void makeFeatureVector() {
		makeMfccFeatures();
		normalizeCepstralFeatures();
		liftering();
		
		// Delta features
		deltaMfcc = Delta.calculateDelta2D(mfccFeature, 2);
		
		// Delta-Delta features
		deltaDeltaMfcc = Delta.calculateDelta2D(deltaMfcc, 1);
		
		// Energy
		energy = Energy.calculate(framedSamples);
		
		// Delta Energy
		deltaEnergy = Delta.calculateDelta1D(energy, 1);
		
		// Delta-Delta Energy
		deltaDeltaEnergy = Delta.calculateDelta1D(deltaEnergy, 1);
		
		for (int i = 0; i < framedSamples.length; i++) {
			for (int j = 0; j < numCepstra; j++) {
				featureVectorValues[i][j] = mfccFeature[i][j];
			}
			
			for (int j = numCepstra; j < 2 * numCepstra; j++) {
				featureVectorValues[i][j] = deltaMfcc[i][j - numCepstra];
			}
			
			for (int j = 2 * numCepstra; j < 3 * numCepstra; j++) {
				featureVectorValues[i][j] = deltaDeltaMfcc[i][j - 2 * numCepstra];
			}
			
			featureVectorValues[i][3 * numCepstra] = energy[i];
			featureVectorValues[i][3 * numCepstra + 1] = deltaEnergy[i];
			featureVectorValues[i][3 * numCepstra + 2] = deltaDeltaEnergy[i];
		}
		
		featureVector.setMfccFeature(mfccFeature);
		featureVector.setFeatureVector(featureVectorValues);
	}
	
	private void liftering() {
		// TODO: implement liftering
	}

	private void normalizeCepstralFeatures() {
		float sum;
		float mean;
		float mCeps[][] = new float[mfccFeature.length][mfccFeature[0].length];
		
		for (int i = 0; i < mCeps[0].length; i++) {
			// Calculate mean
			sum = 0.0f;
			
			// Add i-th coefficient of each frame
			for (int j = 0; j < frameCount; j++) {
				sum += mfccFeature[j][i];
			}
			
			mean = sum / frameCount;
			
			// Subtract
			for (int j = 0; j < frameCount; j++) {
				mCeps[j][i] = mfccFeature[j][i] - mean;
			}
		}
		
		mfccFeature = mCeps;
	}

	private void makeMfccFeatures() {
		for (int i = 0; i < frameCount; i++) {
			mfccFeature[i] = mfcc.calculateFeaturesForWindow(framedSamples[i]);
		}
	}
	
	public FeatureVector getFeatureVector() {
		return featureVector;
	}
}
