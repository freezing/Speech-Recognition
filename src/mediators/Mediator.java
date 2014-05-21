package mediators;

import hmm.LeftRightHmm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import vector_quantization.Codebook;
import vector_quantization.KDPoint;
import wav_file.WavFile;
import wav_file.WavFileException;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import database.Database;
import database.model.CodebookModel;
import database.model.HmmModel;
import database.model.TrainingSetFiles;
import feature_extraction.FeatureVector;
import feature_extraction.FrameExtractor;
import feature_extraction.MfccExtractor;

public class Mediator {
	public static final int FRAME_LENGTH = 512;
	public static final int STEP_LENGTH = 160;
	
	public static final int HMM_STATES = 6;
	public static final int HMM_DELTA = 2;

	private Database database;
	private List<HmmModel> hmmModels;
	private CodebookModel codebookModel;

	public Mediator(String databasePath) {
		database = new Database(databasePath);
		hmmModels = database.loadHmmModels();
		codebookModel = (CodebookModel) database.load(CodebookModel.class,
				"codebook", CodebookModel.CODEBOOK_MODEL_EXTENSION);
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
		return extractFeatureVector(samples, (int) wav.getSampleRate());
	}

	private FeatureVector extractFeatureVector(double[] samples, int sampleRate) {
		double[][] framedSamples = FrameExtractor.extractFrames(samples, FRAME_LENGTH, STEP_LENGTH);

		MfccExtractor mfccExtractor = new MfccExtractor(framedSamples,
				sampleRate);
		mfccExtractor.makeFeatureVector();
		return mfccExtractor.getFeatureVector();
	}

	public void saveCodebook() {
		database.save(codebookModel);
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
			OpdfIntegerFactory opdfFactory = new OpdfIntegerFactory(
					codebookModel.getCodebook().getSize());
			Hmm<ObservationInteger> hmm = new LeftRightHmm<>(HMM_STATES,
					HMM_DELTA, opdfFactory);
			
			List<List<ObservationInteger>> trainingSet = new LinkedList<>();
			
			for (File file : entry.getValue()) {
				FeatureVector featureVector = extractFeatureVector(file);
				List<KDPoint> points = featureVector.toKDPointList();
				
				int[] quantized = codebookModel.getCodebook().quantize(points);
				if (entry.getKey().equals("Petar")) {
					System.out.println(quantized.length);
					for (int k : quantized) {
						System.out.println(k);
					}
					System.out.println();
					System.out.println();
				}
				List<ObservationInteger> observationList = makeObservationList(quantized);
				trainingSet.add(observationList);
			}
			BaumWelchLearner learner = new BaumWelchLearner();
			hmm = (LeftRightHmm<ObservationInteger>) learner.learn(hmm, trainingSet);
			
			HmmModel hmmModel = new HmmModel(hmm, entry.getKey());			
			hmmModels.add(hmmModel);
		}
	}
	
	public String recognizeSpeech(double[] samples, int sampleRate) throws Exception {
		double[][] framedSamples = FrameExtractor.extractFrames(samples, FRAME_LENGTH, STEP_LENGTH);
		MfccExtractor mfccExtractor = new MfccExtractor(framedSamples, sampleRate);
		mfccExtractor.makeFeatureVector();
		
		FeatureVector featureVector = mfccExtractor.getFeatureVector();
		int[] quantized = codebookModel.getCodebook().quantize(featureVector.toKDPointList());
		List<ObservationInteger> oseq = makeObservationList(quantized);
		
		System.out.println("Search sequence:");
		for (int k : quantized) {
			System.out.println(k);
		}
		
		String recognizedWord = null;
		double probability = Double.MIN_VALUE;
		
		for (HmmModel hmmModel : hmmModels) {
			double tmpProbability = hmmModel.getHmm().lnProbability(oseq);
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
