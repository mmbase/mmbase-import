
package nl.eo.batchupload.gui.cellrenderer;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {
	JTextArea textArea;

	public TextAreaCellEditor() {
	    textArea = new JTextArea() {
            protected void processKeyEvent(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER  && arg0.getID() == KeyEvent.KEY_TYPED) {
					insert("\n", getCaretPosition());
				}

				if (arg0.getKeyCode() == KeyEvent.VK_LEFT && arg0.getID() == KeyEvent.KEY_TYPED) {
					setCaretPosition(Math.max(getCaretPosition() -1, 0));
				}

				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT && arg0.getID() == KeyEvent.KEY_TYPED) {
					setCaretPosition(Math.min(getCaretPosition() +1, getText().length()));
				}

                super.processKeyEvent(arg0);
            }
	    };
	    textArea.setLineWrap(true);
	    textArea.setWrapStyleWord(true);
	}

	public Object getCellEditorValue() {
		return textArea.getText();
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	    if (value == null || value.toString() == null)
      		textArea.setText("");
    	else
      		textArea.setText(value.toString());
		
		if (!table.isCellEditable(row, column))
			textArea.setEnabled(false);
		else 
			textArea.setEnabled(true);
			
    	JScrollPane scrollPane = new JScrollPane();
   		scrollPane.getViewport().add(textArea, null);
    	return scrollPane;
 	}

	public boolean isCellEditable(EventObject anEvent) {
		if (!textArea.isEnabled())
			return false;
				
	    if (anEvent instanceof MouseEvent) {
    		return ((MouseEvent)anEvent).getClickCount() >= 2;
   		} 
   		return true;
  	}

	public boolean shouldSelectCell(EventObject anEvent) {
    	return true;
  	}
}
