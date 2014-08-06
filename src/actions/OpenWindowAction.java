package actions;

import gui.MainFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

public class OpenWindowAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private JFrame window;
	
	public OpenWindowAction(JFrame window) {
		this.window = window;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		window.setVisible(true);
		MainFrame.getInstance().setVisible(false);
	}

}
