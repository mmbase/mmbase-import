package nl.eo.batchupload.gui.steps;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.text.*;

import nl.eo.batchupload.gui.StepsFrame;

/**
 *
 */
public class ImportStep extends StepFrame {
	
	public ImportStep(StepsFrame parent) {
        super(parent);
	}
	
	private void redraw() {
		this.removeAll();
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder("Overzicht"));
        p.setLayout(new BorderLayout());        
        JTextPane jtp = new JTextPane();
		p.add(jtp, BorderLayout.CENTER);

		jtp.setEditable(false);

  		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style regular = jtp.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		Style s = jtp.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

   		s = jtp.addStyle("bold", regular);
		StyleConstants.setBold(s, true);

		s = jtp.addStyle("centered", regular);
		StyleConstants.setItalic(s, true);
		StyleConstants.setBold(s, true);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

		Document d = jtp.getDocument();
		
        try {
			d.insertString(d.getLength(), "Totaal aantal te importeren plaatjes: ", jtp.getStyle("regular"));
			d.insertString(d.getLength(), parent.getNumberOfImages() + "\n\n", jtp.getStyle("bold"));
			d.insertString(d.getLength(), "Locatie van de plaatjes: ", jtp.getStyle("regular"));
			d.insertString(d.getLength(), parent.getImagepath() + "\n\n", jtp.getStyle("bold"));
        	
			String newimg = new String();
			Object relateAction = parent.getClientProperty("relateaction");

            if (relateAction == null) {
                newimg = "NULL";
            } else if ("new".equals(relateAction.toString())) {
                newimg = "nieuwe ";
            } else if ("existing".equals(relateAction.toString())) {
                newimg = "bestaande ";
            } else {
				newimg = "NULL";
            }
            
            if (newimg.equals("NULL")) {
                d.insertString(d.getLength(), "Plaatjes worden na import NIET gerelateerd", jtp.getStyle("regular"));
            } else {
              	d.insertString(d.getLength(), "Plaatjes worden na import gerelateerd aan het " + newimg + "object met titel:\n", jtp.getStyle("regular"));
			    d.insertString(d.getLength(), (String)parent.getClientProperty("relate-title"), jtp.getStyle("centered"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        add(p, BorderLayout.NORTH);
        add(new NavigationPane(this), BorderLayout.SOUTH);
	}
	
	/**
     * @see javax.swing.JComponent#setVisible(boolean)
     */
    public void setVisible(boolean arg0) {
		if (!isVisible() && arg0) {
			redraw();
		}
        super.setVisible(arg0);
    }

	
}
