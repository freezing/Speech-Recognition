package database.model;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;

public class HmmModel extends AbstractModel {
	public static final String HMM_EXTENSION = ".hmm";
	private static final long serialVersionUID = 1L;
	
	private Hmm<ObservationInteger> hmm;
	
	public HmmModel(Hmm<ObservationInteger> hmm, String name) {
		super(name);
		this.hmm = hmm;
	}

	@Override
	public String getExtension() {
		return HMM_EXTENSION;
	}
	
	public Hmm<ObservationInteger> getHmm() {
		return hmm;
	}
	
	public void setHmm(Hmm<ObservationInteger> hmm) {
		this.hmm = hmm;
	}

}
