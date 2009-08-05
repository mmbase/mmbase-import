                                                                       package nl.eo.batchupload.gui.steps;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
public class NavigationPane extends JPanel implements ActionListener {
	ActionListener parent;
	
	public NavigationPane(ActionListener parent) {
		this.parent = parent;
		
		setLayout(new GridLayout(1,5));
		add(new JLabel(""));
		add(new JLabel(""));
			
  		JButton backButton = new JButton ("<- Back");
		backButton.addActionListener(this);
		backButton.setActionCommand("Back");
        add(backButton);
     
     	JButton nextButton = new JButton ("Next ->");
		nextButton.addActionListener(this);
		nextButton.setActionCommand("Next");
        add(nextButton);
        
        JButton cancelButton = new JButton ("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("Cancel");
        add(cancelButton);
	}
    
    public void actionPerformed(ActionEvent arg0) {
        JButton callingbutton = (JButton)arg0.getSource();
        // TODO: werkt niet?
        callingbutton.setEnabled(false);
        parent.actionPerformed(arg0);
        callingbutton.setEnabled(true);
    }
} 
