package nl.eo.batchupload.gui;
import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import nl.eo.batchupload.gui.cellrenderer.ProcessBarCellRenderer;
import nl.eo.batchupload.table.UploadTable;
/**
 * 
 */
public class UploadQueueFrame extends JInternalFrame {
    private UploadTable uploadTable;

    public UploadQueueFrame() {
        super("Upload Queue", true, false, false, true);
        setLocation(100,10);
        setSize(500, 100);

        uploadTable = new UploadTable();

        JTable tabel = new JTable(uploadTable) {
            public TableCellRenderer getCellRenderer(int row, int column) {
	            if (column == 2) {
	                return (TableCellRenderer)(new ProcessBarCellRenderer());
	            }
	            return super.getCellRenderer(row, column);
            };
        };

        tabel.setPreferredScrollableViewportSize(new Dimension(500, 70));
        tabel.setRowHeight(tabel.getRowHeight() * 2);
        tabel.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabel);
        setContentPane(scrollPane);
    }
    
    public UploadTable getTable() {
    	return uploadTable;
    }
}
