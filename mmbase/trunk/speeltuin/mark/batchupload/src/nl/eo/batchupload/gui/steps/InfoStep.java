package nl.eo.batchupload.gui.steps;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.eo.batchupload.gui.StepsFrame;

/**
 *
 */
public class InfoStep extends StepFrame {
	JTextField tfTitle = new JTextField(15);
	JTextField tfDescription = new JTextField(15);
	//StepsFrame parent = null;
	
	public InfoStep(StepsFrame parent) {
        super(parent);
		JPanel p = new JPanel();
	//	this.parent = parent;

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        p.setLayout(layout);

        // Algemene gridbag constraints
        c.fill = GridBagConstraints.HORIZONTAL; 
        c.anchor = GridBagConstraints.NORTH;

        c.weightx = 0.1; c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        JLabel lblHeader = new JLabel("Geef standaardwaarden voor plaatjes");
        layout.setConstraints(lblHeader, c);
        p.add(lblHeader);

        c.weightx = 0.1; c.gridx = 0; c.gridy = 2; c.gridwidth = 1;
        JLabel lblTitle = new JLabel("Titel:");
        layout.setConstraints(lblTitle, c);
        p.add(lblTitle);

        c.weightx = 0.1; c.gridx = 0; c.gridy = 4; c.gridwidth = 1;
        JLabel lblDescription = new JLabel("Omschrijving:");
        layout.setConstraints(lblDescription, c);
        p.add(lblDescription);

        c.weightx = 1; c.gridx = 1; c.gridy = 2;
        layout.setConstraints(tfTitle, c);
        p.add(tfTitle);

        c.weightx = 1; c.gridx = 1; c.gridy = 4;
        layout.setConstraints(tfDescription, c);
        p.add(tfDescription);
        
        
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(5,1));
        jp.setBorder(BorderFactory.createTitledBorder("Variabelen"));
        jp.add(new JLabel("%n: volgnummer"));
        jp.add(new JLabel("%b: volledige bestandsnaam"));
        jp.add(new JLabel("%s: bestandsnaam zonder extensie"));
		jp.add(new JLabel("%r: resolutie (HxB) van het plaatje"));
		jp.add(new JLabel("%%, het '%' teken"));
                
        setLayout(new  BorderLayout());
        add(p, BorderLayout.NORTH);
		add(jp, BorderLayout.EAST);
        add(new NavigationPane(this), BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent e) {
		if ("Next".equals(e.getActionCommand())) {
			parent.setTitleTemplate(tfTitle.getText());
			parent.setDescriptionTemplate(tfDescription.getText());
		}
        if ("Cancel".equals(e.getActionCommand())) {
            parent.dispose();
            return;
        }
        parent.actionPerformed(e);
	}
}
