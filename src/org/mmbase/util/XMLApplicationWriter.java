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

    public static boolean writeXMLFile(String applicationname,String targetpath) {
	System.out.println("STARTED XML WRITER ON : "+applicationname);
	String body="<application>\n";

	// status
	body+="<!-- <status>\n";
	body+="\twhat is the status of this builder options : active or inactive\n";
	body+="-->\n";
	body+="<status>active</status>\n\n";

	System.out.println("BODY="+body);
	return(true);
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
