package nl.eo.batchupload.gui.cellrenderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import nl.eo.batchupload.preview.ImageThumbnail;

public class ImageCellRenderer implements TableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
		ImageThumbnail t = (ImageThumbnail)value;
		t.setSize(t.getThumbnailSize());
		return t;
   }
}