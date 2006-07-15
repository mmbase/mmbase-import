/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.config;

import java.util.Hashtable;

/**
 * Class Test
 * 
 * @javadoc
 */
public class Test {
    public static void main(String[] argv) {
	String eol = "\n";
	String res = "";

	String[] reportKeys = new String[]{
	    "jvmoptions",
	    "java",
	    "database",
	    "builders",
	    "languages"
	};
	Hashtable reportClasses = new Hashtable();
	reportClasses.put("jvmoptions","org.mmbase.config.JVMOptionsReport");
	reportClasses.put("java","org.mmbase.config.JavaReport");
	reportClasses.put("database","org.mmbase.config.DatabaseReport");
	reportClasses.put("builders","org.mmbase.config.BuilderReport");
	reportClasses.put("languages","org.mmbase.config.LanguagesReport");

	
	System.out.println("MMBASE CONFIGURATION REPORT:\n------\n");
	for (int i=0;i<reportKeys.length;i++) {
	    try {
		Class c = Class.forName((String)reportClasses.get(reportKeys[i]));
		ReportInterface r = (ReportInterface)c.newInstance();
		r.init("error","text");
		res = res + "=== " + r.label() + " ===" + eol + r.report() + eol;
	    } catch (Exception ignore) {
		System.out.println("+++");
		ignore.printStackTrace(System.out);
		System.out.println("+++");
		res = res + "ERROR: failed to load " + reportClasses.get(reportKeys[i]) + ": " + ignore.getMessage() + eol;
	    }
	}
	System.out.println(res);
    }
}
