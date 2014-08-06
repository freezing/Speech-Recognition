package actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class CheckBoxWordAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	boolean[] intervalsToUse;
	int idx;
	
	public CheckBoxWordAction(boolean[] intervalsToUse, int idx) {
		this.intervalsToUse = intervalsToUse;
		this.idx = idx;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		intervalsToUse[idx] = !intervalsToUse[idx];
	}

}
