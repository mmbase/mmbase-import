/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/

package org.mmbase.module;

import java.util.*;
import java.io.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.EditState;
import org.mmbase.util.*;


/**
 * the Upload module stores files that are uploaded.
 * At this time files van only be stored in memory.
 * nog wat uitleg over hoe je de .shtml maakt
 */
public class Upload extends ProcessorModule {
    private String classname = getClass().getName();
    private boolean debug = false;
	private void debug(String s) {
		System.out.println(s);
	}

	private Hashtable FilesInMemory = new Hashtable();

	public Upload() {}
	public void onload(){}
	public void init() {}

    public boolean process(scanpage sp, Hashtable cmds, Hashtable vars) {
        if (debug) {
            debug("CMDS="+cmds);
            debug("VARS="+vars);
        }

		// Get the place where to store the file
		// Currently implemented places are: mem://
		// Other possible places to implement: file://
		String filename = (String)cmds.get("file");

		// Store in memory
		if(filename.indexOf("mem://")!=-1) {
			if(debug) debug("saving file at "+filename);

			// Create file object in memory		
			FileInfo fi = new FileInfo();
			try {
   		     	fi.bytes= sp.poster.getPostParameterBytes("file");
       		    fi.name = sp.poster.getPostParameter("file_name");
            	fi.type = sp.poster.getPostParameter("file_type");
            	fi.size = sp.poster.getPostParameter("file_size");
			} catch (Exception e) {
				debug(e.toString());
			}
			FilesInMemory.put(filename,fi);
			if(debug) debug("save in memory: "+fi.toString());
		}
		return true;
	}

	/**
	 * deletes an uploaded file.
	 * @param filename the name of the file, e.g. mem://filename
	 */
	public void deleteFile(String filename) {
		// Is file located in memory?
		if(filename.indexOf("mem://")!=-1) {
			if(FilesInMemory.containsKey(filename)) {
				FilesInMemory.remove(filename);
			}
		}
	}

	/** 
	 * gets the bytearray of an uploaded file.
	 * @param filename the name of the file, e.g. mem://filename
	 */
	public byte[] getFile(String filename) {
		// Is file located in memory?
		if(filename.indexOf("mem://")!=-1) {
			if(debug) debug("Upload -> received "+filename);
			if(FilesInMemory.containsKey(filename)) {
				if(debug) debug("Upload -> Contains "+filename);
				FileInfo fi = (FileInfo)FilesInMemory.get(filename);
				return fi.bytes;
			}
		}
		return null;
	}

	/*
	 * a class to store an uploaded file into memory
	 */	
	class FileInfo {
  		byte[] bytes= null;
        String name = null;
        String type = null;
        String size = null;

		public String toString () {
			return "name="+name+" type="+type+" size="+size;
		}
	}
}
