package nl.eo.batchupload.table;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;
import nl.eo.batchupload.upload.UploadImage;


public class ImageTable extends AbstractTableModel {
	String[] columnNames = {" ", "Image", "Titel", "Omschrijving"};
	ArrayList data = new ArrayList();

	public ImageTable() {

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
		UploadImage i = (UploadImage)data.get(row);
		if (col == -1) {
			return i;
		} else if (col == 0) {
			return new Boolean(i.isEnabled());
		} else if (col == 1) {
			return i.getThumbnail();
		} else if (col == 2) {
			return i.getTitle();
		} else if (col == 3) {
			return i.getDescription();
		} else {
			return null;
		}
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
    	if (col == 0)
    		return true;
    	if (col == 1) 
    		return false;
    	
    	UploadImage img = (UploadImage)data.get(row);
    	return img.isEnabled();
    }

    public void setValueAt(Object value, int row, int col) {
    	UploadImage img = (UploadImage)data.get(row);
    	data.remove(row);
    	
    	if (col == 0) {
    		img.setEnabled(((Boolean)value).booleanValue());
    		fireTableDataChanged() ;
    	}
		if (col == 2) {
			Hashtable replaces = new Hashtable();
			replaces.put("n", 					//volgnummer
				"" + (row + 1));
			replaces.put("b",						//volledige bestandsnaam
			 	img.getFilename()); 		
			replaces.put("s", 					//bestandsnaam zonder extensie
				img.getFilename().substring(0, img.getFilename().lastIndexOf("."))); 
			replaces.put("%", 					//%
				"%"); 				
			String replaced = expandString((String)value, replaces);
			img.setTitle(replaced);
		}
		if (col == 3) {
			Hashtable replaces = new Hashtable();
			replaces.put("n",						//volgnummer
				"" + (row + 1));
			replaces.put("b", 					//volledige bestandsnaam
				img.getFilename()); 				
			replaces.put("s", 					//bestandsnaam zonder extensie
				img.getFilename().substring(0, img.getFilename().lastIndexOf("."))); 	
			replaces.put("%", 					//%
				"%"); 									
			String replaced = expandString((String)value, replaces);
			img.setDescription(replaced);
		}
		data.add(row, img);
        fireTableCellUpdated(row, col);
    }
    
    public int addRow(UploadImage img) {
    	data.add(img);
    	fireTableDataChanged();
    	img.getThumbnail().setParentTable(this);
    	return data.size() - 1;
    }
    
    public UploadImage getRow(int rownumber) {
    	return (UploadImage)data.get(rownumber);
    }
    
    public ArrayList getAllData() {
        return data;
    }
    
    public void removeAll() {
        data = new ArrayList();
    }
    
    public void setRow(int rownumber, UploadImage newimg) {
		data.set(rownumber, newimg);
		fireTableDataChanged();
    }
    
    
	private String expandString(String source, Hashtable properties) {
        int lastindex = 0;
        int index = source.indexOf("%", lastindex);
        StringBuffer result = new StringBuffer();
        while (index != -1 && source.length() > (index + 1)) {
            result.append(source.substring(lastindex, index));
            String nextChar = source.substring(index + 1, index + 2);
            if (properties.containsKey(nextChar)) {
                result.append(properties.get(nextChar));
                lastindex = index + 2;
            } else {
                result.append(source.substring(index, index + 2));
                lastindex = index + 2;
            }
            index = source.indexOf("%", lastindex);
        }
        result.append(source.substring(lastindex));
        return result.toString();
	}
}	
