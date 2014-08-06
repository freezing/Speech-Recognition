package actions;

import gui.TrainFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mediators.Mediator;


public class RetrainAllHmmsAction extends AbstractAction  {
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String message = "This action will start generating codebook from the beggining using all training data "
					+ "that is in the database. It make take some time. Are you sure you want to retrain all HMMs?";
		    int answer = JOptionPane.showConfirmDialog(TrainFrame.getInstance(), message, 
		    		"Are you sure?", JOptionPane.YES_NO_OPTION);
		    if (answer == JOptionPane.YES_OPTION) {
		    	// User clicked YES.
		    	Mediator.getInstance().retrainAllHmms();		    	
		    }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
