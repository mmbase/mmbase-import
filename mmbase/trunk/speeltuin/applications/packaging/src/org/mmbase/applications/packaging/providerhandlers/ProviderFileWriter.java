/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.providerhandlers;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 */
public class ProviderFileWriter  {

    private static Logger log = Logging.getLoggerInstance(ProviderFileWriter.class.getName());

    public static boolean write() {
        String body =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE providers PUBLIC \"-//MMBase/DTD providers config 1.0//EN\" \"http://www.mmbase.org/dtd/providers_1_0.dtd\">\n";
        body+="<providers>\n";

        body+=writeProviders();

        body+="</providers>\n";

        String filename = MMBaseContext.getConfigPath()+File.separator+"packaging"+File.separator+"providers.xml";
        saveFile(filename,body);
	return true;
    }

    private static String writeProviders() {
	String result="";
	Enumeration e=ProviderManager.getProviders();
	while (e.hasMoreElements()) {
		ProviderInterface p=(ProviderInterface)e.nextElement();
		String method=p.getMethod();
		result+="\t<provider name=\""+p.getName()+"\" maintainer=\""+p.getMaintainer()+"\" ";
		result+="method=\""+method+"\" ";
		result+=">\n";
		if (method.equals("http")) {
			HttpProvider p2=(HttpProvider)p;
			result+="\t\t<path>"+p2.getPath()+"</path>\n";
			result+="\t\t<account>"+p2.getAccount()+"</account>\n";
			result+="\t\t<password>"+p2.getPassword()+"</password>\n";
			result+="\t\t<description>"+p2.getDescription()+"</description>\n";
		} else if (method.equals("disk")) {
			result+="\t\t<path>"+p.getPath()+"</path>\n";
			result+="\t\t<description>"+p.getDescription()+"</description>\n";
		}
		
		result+="\t</provider>\n";
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
