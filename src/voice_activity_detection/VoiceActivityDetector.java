package voice_activity_detection;

import feature_extraction.FeatureVector;
import hmm.HiddenMarkovModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import vector_quantization.Codebook;
import vector_quantization.KDPoint;
import database.model.CodebookModel;
import database.model.HmmModel;

public class VoiceActivityDetector implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final double THRESHOLD = 0;
	
	private HmmModel noiseHmmModel;
	private HmmModel voiceHmmModel;
	private CodebookModel codebookModel;
	
	public VoiceActivityDetector() {
		
	}
	
	public VoiceActivityDetector(CodebookModel codebookModel, HmmModel noiseHmmMode, HmmModel voiceHmmModel) {
		this.codebookModel = codebookModel;
		this.noiseHmmModel = noiseHmmMode;
		this.voiceHmmModel = voiceHmmModel;
	}
	
	public HiddenMarkovModel getNoiseHmm() {
		return noiseHmmModel.getHmm();
	}
	
	public HiddenMarkovModel getVoiceHmm() {
		return voiceHmmModel.getHmm();
	}
	
	public Codebook getCodebook() {
		return codebookModel.getCodebook();
	}
	
	public void setCodebook(CodebookModel codebookModel) {
		this.codebookModel = codebookModel;
	}
	
	public boolean isVoiced(int[] quantized) throws Exception {	
		double noiseProbability = getNoiseHmm().viterbi(quantized);
		double voiceProbability = getVoiceHmm().viterbi(quantized);
		return voiceProbability - noiseProbability > THRESHOLD;
	}

}
