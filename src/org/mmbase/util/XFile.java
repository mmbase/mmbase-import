/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.io.*;


/**
 *
 * @author David V van Zeventer
 * @version: 13 nov 1998
 */

/*
 * class XFile : Will retrieve the file properties from a File object and stores them as fields.
 *		 Eg: the files' modification time will be stored as a moddate long variabele. 
 */
public class XFile{

	private File file=null;
	private String filepath=null;
	private long modtime=0;

	public XFile(File f){
		filepath = f.getPath();		//Get filepath.
		modtime = f.lastModified();	//Get modificationtime.	
	}

	public XFile(String filepath){
		file = new File(filepath);	//Create fileobject.
		this.filepath = file.getPath();	//Get filepath.
		modtime = file.lastModified();	//Get modificationtime.	
	}
		
		
	public String getFilePath(){	//Method for retrieving filepath value.
		return filepath;
	}
	public long getModTime(){	//Method for retrieveing modtime value.
		return modtime;
	}

}
