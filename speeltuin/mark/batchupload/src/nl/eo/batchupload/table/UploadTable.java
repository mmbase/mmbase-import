package nl.eo.batchupload.table;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 */
public class UploadTable extends AbstractTableModel {
    String[] columnNames = {"Gebruiker", "Huidige file", "Totale voortgang", "Status"};
    ArrayList data = new ArrayList();

    public UploadTable() {

    }
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Object[] listdata =(Object[]) data.get(row);
        return listdata[col];        
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        Object[] listdata =(Object[]) data.get(row);
        listdata[col] = value;
        data.remove(row);
        data.add(row, listdata);
        fireTableCellUpdated(row, col);
    }
    
    private int addRow(String user, String filename, Double status, String currentstatus) {
        Object[] listdata = {user, filename, status, currentstatus};
        data.add(listdata);
        fireTableDataChanged();
        return data.size() - 1;
    }
    
    public int addRow() {
        return addRow("", "", new Double(0.0), "");
    }
}


