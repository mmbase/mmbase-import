package nl.eo.batchupload.gui.cellrenderer;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ProcessBarCellRenderer implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        JProgressBar j = new JProgressBar(0, 100);
        int val = (int)(100.0 * Double.parseDouble("" + value));
        j.setValue(val);
        j.setStringPainted(true);
        return j;
    }
}