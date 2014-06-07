package database.model;

import java.io.Serializable;

import voice_activity_detection.VoiceActivityDetector;

public class VadModel extends AbstractModel implements Serializable {
	public static final String VAD_MODEL_EXTENSION = ".vad";
	private static final long serialVersionUID = 1L;
	
	private VoiceActivityDetector vad;
	
	public VadModel() {
		super("vad");
	}
	
	public VadModel(VoiceActivityDetector vad, String name) {
		super(name);
		this.vad = vad;
	}

	@Override
	public String getExtension() {
		return VAD_MODEL_EXTENSION;
	}
	
	public VoiceActivityDetector getVAD() {
		return vad;
	}
	
}
