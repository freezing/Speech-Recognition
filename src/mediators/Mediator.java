package mediators;

import hmm.HiddenMarkovModel;
import hmm.LeftRightHmm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import preprocessing.PreProcessor;
import vector_quantization.Codebook;
import vector_quantization.KDPoint;
import wav_file.WavFile;
import wav_file.WavFileException;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import database.Database;
import database.model.CodebookModel;
import database.model.HmmModel;
import database.model.TrainingSetFiles;
import feature_extraction.FeatureVector;
import feature_extraction.FrameExtractor;
import feature_extraction.MFCC;
import feature_extraction.MfccExtractor;

public class Mediator {
	public static final int FRAME_LENGTH = 512;
	public static final int STEP_LENGTH = 160;
	
	public static final int HMM_STATES = 6;
	public static final int HMM_DELTA = 2;

	private Database database;
	private List<HmmModel> hmmModels;
	private Map<String, Integer> hmmNameToIndex;
	private CodebookModel codebookModel;

	public Mediator(String databasePath) {
		database = new Database(databasePath);
		hmmModels = database.loadHmmModels();
		initializeHmmNameToIndex();
		codebookModel = (CodebookModel) database.load(CodebookModel.class,
				"codebook", CodebookModel.CODEBOOK_MODEL_EXTENSION);
	}

	private void initializeHmmNameToIndex() {
		hmmNameToIndex = new HashMap<>();
		for (int i = 0; i < hmmModels.size(); i++) {
			hmmNameToIndex.put(hmmModels.get(i).getName(), i);
		}
	}
	
	private HmmModel getHmmByName(String name) {
		return hmmModels.get(hmmNameToIndex.get(name));
	}

	public void generateCodebook() throws Exception {
		TrainingSetFiles trainingSetFiles = database.loadTrainingSet();
		Map<String, File[]> trainingSets = trainingSetFiles.getTrainingSets();

		List<KDPoint> points = new LinkedList<>();
		for (Entry<String, File[]> entry : trainingSets.entrySet()) {
			for (File file : entry.getValue()) {
				FeatureVector featureVector = extractFeatureVector(file);
				for (double[] p : featureVector.getFeatureVector()) {
					points.add(new KDPoint(p));
				}
			}
		}

		Codebook codebook = new Codebook(points, Codebook.DEFAULT_SIZE);
		codebook.initialize();
		codebookModel = new CodebookModel(codebook, "codebook");
	}

	private FeatureVector extractFeatureVector(File file) throws IOException,
			WavFileException {
		WavFile wav = WavFile.openWavFile(file);
		double[] samples = wav.readWholeFile();
		PreProcessor.normalizePCM(samples);
		return extractFeatureVector(samples, (int) wav.getSampleRate());
	}

	private FeatureVector extractFeatureVector(double[] samples, int sampleRate) {
		double[][] framedSamples = FrameExtractor.extractFrames(samples, FRAME_LENGTH, STEP_LENGTH);

	//	MfccExtractor mfccExtractor = new MfccExtractor(framedSamples,
	//		sampleRate);
	//	mfccExtractor.makeFeatureVector();
	//	return mfccExtractor.getFeatureVector();
		FeatureVector fv = new FeatureVector();
		double featureVector[][] = new double[framedSamples.length][];
		MFCC mfcc = new MFCC(12, sampleRate, 24, 512, true, 22, false);
		for (int i = 0; i < framedSamples.length; i++) {
			featureVector[i] = mfcc.getParameters(framedSamples[i]);
		}
		fv.setFeatureVector(featureVector);
		return fv;
	}

	public void saveCodebook() {
		database.save(codebookModel);
	}
	
	public void addWordAndTrainModel(String word, double[] samples, int sampleRate) throws Exception {
		if (hmmNameToIndex.get(word) == null) {
			HiddenMarkovModel hmm = new HiddenMarkovModel(HMM_STATES, codebookModel.getCodebook().getSize());
			HmmModel hmmModel = new HmmModel(hmm, word);
			hmmModels.add(hmmModel);
			hmmNameToIndex.put(word, hmmModels.size() - 1);
		}
		
		HmmModel hmmModel = getHmmByName(word);
		FeatureVector featureVector = extractFeatureVector(samples, sampleRate);
		int trainSeq[] = codebookModel.getCodebook().quantize(featureVector.toKDPointList());
		hmmModel.getHmm().setTrainSeq(trainSeq);
		hmmModel.getHmm().train();
	}

	/**
	 * NOTE: Codebook should already be initialized before calling this method.
	 * @throws Exception 
	 */
	public void retrainAllHmms() throws Exception {
		TrainingSetFiles trainingSetFiles = database.loadTrainingSet();
		Map<String, File[]> trainingSets = trainingSetFiles.getTrainingSets();

		hmmModels = new LinkedList<HmmModel>();
		for (Entry<String, File[]> entry : trainingSets.entrySet()) {			
			int[][] trainingSet = new int[entry.getValue().length][];
			int m = 0;
			
			for (File file : entry.getValue()) {
				FeatureVector featureVector = extractFeatureVector(file);
				List<KDPoint> points = featureVector.toKDPointList();
				
				int[] quantized = codebookModel.getCodebook().quantize(points);
				trainingSet[m++] = quantized;
			}

			HiddenMarkovModel hmm = new HiddenMarkovModel(HMM_STATES, codebookModel.getCodebook().getSize());
			hmm.setTrainSeq(trainingSet);
			hmm.train();
			HmmModel hmmModel = new HmmModel(hmm, entry.getKey());			
			hmmModels.add(hmmModel);
		}
	}
	
	public String recognizeSpeech(double[] samples, int sampleRate) throws Exception {
		double[][] framedSamples = FrameExtractor.extractFrames(samples, FRAME_LENGTH, STEP_LENGTH);
	//	MfccExtractor mfccExtractor = new MfccExtractor(framedSamples, sampleRate);
	//	mfccExtractor.makeFeatureVector();
	//	FeatureVector featureVector = mfccExtractor.getFeatureVector();
		FeatureVector fv = new FeatureVector();
		double featureVector[][] = new double[framedSamples.length][];
		MFCC mfcc = new MFCC(12, sampleRate, 24, 512, true, 22, false);
		for (int i = 0; i < framedSamples.length; i++) {
			featureVector[i] = mfcc.getParameters(framedSamples[i]);
		}
		fv.setFeatureVector(featureVector);
		
		
		int[] quantized = codebookModel.getCodebook().quantize(fv.toKDPointList());
		
		String recognizedWord = null;
		double probability = Double.NEGATIVE_INFINITY;
		
		for (HmmModel hmmModel : hmmModels) {
			double tmpProbability = hmmModel.getHmm().viterbi(quantized);
			System.out.println(hmmModel.getName() + "   " + tmpProbability);
			if (tmpProbability > probability) {
				probability = tmpProbability;
				recognizedWord = hmmModel.getName();
			}
		}		
		return recognizedWord;
	}

	private List<ObservationInteger> makeObservationList(int[] quantized) {
		List<ObservationInteger> observationList = new LinkedList<>();
		for (int i = 0; i < quantized.length; i++) {
			observationList.add(new ObservationInteger(quantized[i]));
		}
		return observationList;
	}

	public void saveCurrentHmmModels() {
		for (HmmModel hmmModel : hmmModels) {
			database.save(hmmModel);
		}
	}
	
	public Codebook getCodebook() {
		return codebookModel.getCodebook();
	}
}
