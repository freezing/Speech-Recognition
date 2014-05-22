package database.model;

import hmm.HiddenMarkovModel;

public class HmmModel extends AbstractModel {
	public static final String HMM_EXTENSION = ".hmm";
	private static final long serialVersionUID = 1L;
	
	private HiddenMarkovModel hmm;
	
	public HmmModel(HiddenMarkovModel hmm, String name) {
		super(name);
		this.hmm = hmm;
	}

	@Override
	public String getExtension() {
		return HMM_EXTENSION;
	}
	
	public HiddenMarkovModel getHmm() {
		return hmm;
	}
	
	public void setHmm(HiddenMarkovModel hmm) {
		this.hmm = hmm;
	}

}
