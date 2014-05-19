package app;

import hmm.LeftRightHmm;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;

public class Main {
	public static void main(String[] args) {
		OpdfIntegerFactory factory = new OpdfIntegerFactory(5);
		LeftRightHmm<ObservationInteger> hmm = new LeftRightHmm<ObservationInteger>(3, 2, factory);
		
		List<List<ObservationInteger>> train = new LinkedList<>();		
		
		train.add(new LinkedList<ObservationInteger>());
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(3));

		train.add(new LinkedList<ObservationInteger>());
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(3));
		
		train.add(new LinkedList<ObservationInteger>());
		train.get(train.size() - 1).add(new ObservationInteger(0));
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(2));

		train.add(new LinkedList<ObservationInteger>());
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(3));
		train.get(train.size() - 1).add(new ObservationInteger(3));
		
		train.add(new LinkedList<ObservationInteger>());
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(3));

		train.add(new LinkedList<ObservationInteger>());
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(3));
		train.get(train.size() - 1).add(new ObservationInteger(3));
		
		train.add(new LinkedList<ObservationInteger>());
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(1));
		train.get(train.size() - 1).add(new ObservationInteger(2));
		train.get(train.size() - 1).add(new ObservationInteger(3));
		
		BaumWelchLearner learner = new BaumWelchLearner();
		hmm = (LeftRightHmm<ObservationInteger>) learner.learn(hmm, train);
		
		List<ObservationInteger> oseq = new LinkedList<>();
		oseq.add(new ObservationInteger(1));
		oseq.add(new ObservationInteger(1));
		oseq.add(new ObservationInteger(1));
		oseq.add(new ObservationInteger(2));
		oseq.add(new ObservationInteger(2));
		oseq.add(new ObservationInteger(2));
		oseq.add(new ObservationInteger(2));
		oseq.add(new ObservationInteger(3));
		oseq.add(new ObservationInteger(3));
		oseq.add(new ObservationInteger(3));
		oseq.add(new ObservationInteger(3));
		
		File file = new File("/home/nikola");
		for (String s : file.list()) {
			System.out.println(s);
		}
		
		double p = hmm.lnProbability(oseq);
		System.out.println(p);
	}
}
