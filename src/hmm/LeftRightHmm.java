package hmm;

import java.util.Arrays;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;

/**
 * 
 * Also known as Baskin HMM. The rules of transition in a left right HMM is:
 * <ol>
 * <li><code>a(i, j) = 0</code> if <code>j < i</code></li>
 * <li><code>a(i, j) = 0</code> if <code>j > i + DELTA</code></li>
 * </ol>
 **/
public class LeftRightHmm<O extends Observation> extends Hmm<O> {

	private static final long serialVersionUID = -7558110664963322390L;

	protected int delta;

	public LeftRightHmm(int delta, double[] pi, double[][] a, List<? extends Opdf<O>> opdfs) {
		super(pi, a, opdfs);
		this.delta = delta;
		refineTransitionRules();
	}

	public LeftRightHmm(int nbStates, int delta, OpdfFactory<? extends Opdf<O>> opdfFactory) {
		super(nbStates, opdfFactory);
		this.delta = delta;
		refineTransitionRules();
	}

	public LeftRightHmm(int nbStates, int delta, List<? extends Opdf<O>> opdfs) {
		super(nbStates);
		setOpdfs(opdfs);
		this.delta = delta;
		initDefaultPis(nbStates);
		refineTransitionRules();
	}

	public LeftRightHmm(int nbStates, int delta) {
		super(nbStates);
		this.delta = delta;
		refineTransitionRules();
	}

	public LeftRightHmm(Hmm<O> hmm, int delta) {
		this(hmm.nbStates(), delta);
		for (int i = 0; i < nbStates(); ++i) {
			setPi(i, hmm.getPi(i));
			setOpdf(i, hmm.getOpdf(i));
			for (int j = 0; j < nbStates(); ++j) {
				setAij(i, j, hmm.getAij(i, j));
			}
		}
		refineTransitionRules();
	}

	public Integer getDelta() {
		return delta;
	}

	@Override
	public void setAij(int i, int j, double value) {
		if (j < i) {
			super.setAij(i, j, 0);
			return;
		}
		if (j > i + delta) {
			super.setAij(i, j, 0);
			return;
		}
		super.setAij(i, j, value);
	}

	public void setOpdfs(Opdf<O>[] opdfs) {
		for (int i = 0; i < nbStates(); ++i) {
			setOpdf(i, opdfs[i]);
		}
	}

	public void setOpdfs(List<? extends Opdf<O>> opdfs) {
		setOpdfs(opdfs.toArray(new Opdf[opdfs.size()]));
	}

	public void setPis(double[] pis) {
		for (int i = 0; i < nbStates(); ++i) {
			setPi(i, pis[i]);
		}
	}

	public void setPis(List<Double> pis) {
		int i = 0;
		for (Double pi : pis) {
			setPi(i++, pi);
		}
	}

	protected void refineTransitionRules() {
		for (int i = 0; i < nbStates(); ++i) {
			for (int j = 0; j < nbStates(); ++j) {
				this.setAij(i, j, this.getAij(i, j));
			}
		}
	}

	protected void initDefaultPis(int nbStates) {
		double[] pis = new double[nbStates];
	//	Arrays.fill(pis, 1. / nbStates);
		pis[0] = 1.0;
		setPis(pis);
	}

	@Override
	public Hmm<O> clone() throws CloneNotSupportedException {
		LeftRightHmm<O> hmm = new LeftRightHmm<O>(nbStates(), delta);
		for (int i = 0; i < nbStates(); i++) {
			hmm.setPi(i, this.getPi(i));
			hmm.setOpdf(i, this.getOpdf(i).clone());
			for (int j = 0; j < nbStates(); ++j) {
				hmm.setAij(i, j, this.getAij(i, j));
			}
		}
		return hmm;
	}

}
