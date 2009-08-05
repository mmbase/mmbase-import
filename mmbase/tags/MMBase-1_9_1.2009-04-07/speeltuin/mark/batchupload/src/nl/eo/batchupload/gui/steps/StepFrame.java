package nl.eo.batchupload.gui.steps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import nl.eo.batchupload.gui.StepsFrame;

public abstract class StepFrame extends JPanel implements ActionListener {
    NavigationPane navigationPane = new NavigationPane(this);
    StepsFrame parent;

    public StepFrame(StepsFrame parent) {
        this.parent = parent;        
    }

    public StepFrame(StepsFrame parent, boolean b) {
        super(b);   
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand().equals("Cancel")) {
            parent.dispose();
            return;
        }
        parent.actionPerformed(arg0);
    }
}
