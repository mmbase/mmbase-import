/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;

import org.mmbase.module.corebuilders.*;

/**
*/
public class XMLApplicationWriter  {

    public static boolean writeXMLFile(XMLApplicationReader app,String targetpath) {
	System.out.println("STARTED XML WRITER ON : "+app);

	// again this is a stupid class generating the xml file
	// the second part called the extractor is kind of neat
	// but very in early beta
	String name=app.getApplicationName();
	String maintainer=app.getApplicationMaintainer();
	int version=app.getApplicationVersion();
	boolean deploy=app.getApplicationAutoDeploy();
	
	String body="<application name=\""+name+"\" maintainer=\""+maintainer+"\" version=\""+version+"\" auto-deploy=\""+deploy+"\">\n";
	// status
	body+="\t<neededbuilderlist>\n";
	body+=getNeededBuilders(app);
	body+="\t</neededbuilderlist>\n\n";

	System.out.println("BODY="+body);
	return(true);
    }

    static String getNeededBuilders(XMLApplicationReader app) {
	String body="";
	Vector builders=app.getNeededBuilders();
	System.out.println("builders="+builders.size());
	for (Enumeration e=builders.elements();e.hasMoreElements();) {
		Hashtable bset=(Hashtable)e.nextElement();
		String name=(String)bset.get("name");
		String maintainer=(String)bset.get("maintainer");
		String version=(String)bset.get("version");
		body+="\t\t<builder maintainer=\""+maintainer+"\" version=\""+version+"\">"+name+"</builder>\n";
	}
	return(body);	
    }


	static boolean saveFile(String filename,String value) {
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.writeBytes(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}
}
