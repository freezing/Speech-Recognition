package actions;

import gui.AddWordsFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.swing.AbstractAction;

import utility.Utils;
import mediators.Mediator;

public class TrainAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private boolean[] intervalsToUse;
	private List<AudioInputStream> audioInputStreams;
	
	public TrainAction(boolean[] intervalsToUse, List<AudioInputStream> audioInputStreams) {
		this.intervalsToUse = intervalsToUse;
		this.audioInputStreams = audioInputStreams;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		List<String> words = AddWordsFrame.getInstance().getWords();
		
		for (int i = 0; i < intervalsToUse.length; i++) {
			if (intervalsToUse[i]) {
				String word = words.get(i);
				Mediator.getInstance().addTrainingData(word, audioInputStreams.get(i));
			}
		}
		
		if (!Mediator.getInstance().hasCodebook() || AddWordsFrame.getInstance().isGenerateCodebookFlagSet()) {
			try {
				System.out.println("Generating codebook...");
				Mediator.getInstance().generateCodebook();
				Mediator.getInstance().saveCodebook();
				System.out.println("==========Codebook saved==========");
				
				System.out.println("Retraining all HMM Models...");
				try {
					Mediator.getInstance().retrainAllHmms();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("==========Retraining finished=========");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else {		
			for (int i = 0; i < intervalsToUse.length; i++) {
				if (intervalsToUse[i]) {
					int sampleRate = (int) audioInputStreams.get(i).getFormat().getSampleRate();
					double[] samples = Utils.getSamples(audioInputStreams.get(i));
					String word = words.get(i);
					
					try {
						System.out.println("Training: " + word);
						Mediator.getInstance().addWordAndTrainModel(word, samples, sampleRate);
						System.out.println("Training finished....");
						System.out.println("========================");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		System.out.println("Saving new HMM Models...");
		Mediator.getInstance().saveCurrentHmmModels();
		System.out.println("New HMM Models saved...");
	}

}
