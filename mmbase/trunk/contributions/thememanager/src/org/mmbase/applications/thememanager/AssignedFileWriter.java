/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.thememanager;

import java.io.*;
import java.util.*;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 */
public class AssignedFileWriter  {

    private static Logger log = Logging.getLoggerInstance(AssignedFileWriter.class);

    public static boolean write() {
        String body =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE assigned PUBLIC \"-// MMBase - assigned //EN\" \"http://www.mmbase.org/dtd/assigned_1_0.dtd\">\n";
        body+="<assigned>\n";

        body+=writeAssigns();

        body+="</assigned>\n";

        String filename = MMBaseContext.getConfigPath()+File.separator+"thememanager"+File.separator+"assigned.xml";
        saveFile(filename,body);
	return true;
    }

    private static String writeAssigns() {
	String result="";
	HashMap m=ThemeManager.getAssigned();
        Iterator keys=m.keySet().iterator();
        while (keys.hasNext()) {
              String k=(String)keys.next();
              String v=(String)m.get(k);
      	      result+="\t\t<assign id=\""+k+"\" theme=\""+v+"\" ";
	      result+=" />\n";
	}
	return result;
    }

    static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return true;
    }

}
