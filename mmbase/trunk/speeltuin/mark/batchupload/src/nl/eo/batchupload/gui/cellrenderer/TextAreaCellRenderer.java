package nl.eo.batchupload.gui.cellrenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class TextAreaCellRenderer implements TableCellRenderer {
	private JTextArea textArea = new JTextArea();
	
	public TextAreaCellRenderer() {
	   	textArea.setLineWrap(true);
	   	textArea.setWrapStyleWord(true);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null || value.toString() == null)
	   		textArea.setText("");
	   	else
     		textArea.setText(value.toString());

		if (table.isCellEditable(row, column))
			textArea.setBackground(Color.white);
		else
			textArea.setBackground(Color.gray);
				
    	return textArea;
  	} 
}
