/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * @author cjr@dds.nl
 */
public class Config extends ProcessorModule {

    private String classname = getClass().getName();
    private String configpath;

    /**
     * Implement a FilenameFilter for xml files
     */
    public class XMLFilenameFilter implements FilenameFilter {
	public boolean accept(File directory, String name) {
	    if (name.endsWith(".xml")) {
		return true;
	    } else {
		return false;
	    }
	}
    }

    public void init() {
	configpath = System.getProperty("mmbase.config");
	if (configpath.endsWith(File.separator)) {
	    configpath = configpath.substring(0,configpath.length()-1);
	}
    }
    
    public void reload() {
    }
    
    public void onload() {
    }
    
    public void unload() {
    }
    
    public void shutdown() {
    }
    
    
    /**
     * Config, a support module for servscan
     */
    public Config() {
    }
    
    /**
     * Generate a list of values from a command to the processor
     */
    public Vector  getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
	Vector v = new Vector();

    	String line = Strip.DoubleQuote(value,Strip.BOTH);
	StringTokenizer tok = new StringTokenizer(line,"-\n\r");

	String[] argv = new String[tok.countTokens()];
	for (int i=0; i<tok.countTokens(); i++) {
	    argv[i] = tok.nextToken();
	}

	String category = tagger.Value("category");

	try {
	    if (argv[0].equalsIgnoreCase("show")) {
		if (argv.length == 1) {
		    if (category == null || category.equals("")) {
			// Show main configuration file categories
			return listConfigDirectories(configpath);
		    } else {
			return listDirectory(configpath+File.separator+category);
		    }
		} else if (argv.length == 2) {
		    v.addElement("arg = "+argv[1]);
		} else if (argv.length == 3) {
		    v.addElement("arg = "+argv[1]+","+argv[2]);
		}
	    }
	    return v;
	} catch (IOException e) {
	    return null;
	}
    }

    /**
     * @param path Path to root of configuration files
     * @return Vector containing the names of the main configuration directories
     */
    public Vector listConfigDirectories(String path) {
	Vector v = new Vector();
	File dir = new File(path);
	String[] list = dir.list();
	for (int i=0;i<list.length;i++) {
	    File f = new File(configpath+File.separator+list[i]);
	    if (!list[i].equalsIgnoreCase("CVS") && f.isDirectory()) {
		v.addElement(list[i]);
	    }
	}
	return v;
    }

    /**
     * Retrieve all xml files in a directory
     * 
     * @param path Directory path
     * @return String array containing the names of the xml files in the directory
     *
     */
    protected Vector listDirectory(String path) throws IOException {
	File dir = new File(path);
	if (!dir.isDirectory()) {
	    throw new IOException("Path "+path+" is not a directory.\n");
	} else {
	    String[] dirlist = dir.list(new XMLFilenameFilter());
	    Vector v = new Vector();
	    for (int i=0;i<dirlist.length;i++) {
		v.addElement(dirlist[i].substring(0,dirlist[i].length()-4));
	    }
	    return v;
	}
    }
    
    /**
     * Execute the commands provided in the form values
     */
    public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
	debug("CMDS="+cmds);
	debug("VARS="+vars);
	return(false);
    }
    
    /**
     *	Handle a $MOD command
     */
    public String replace(scanpage sp, String cmds) {
	String[] dirlist;

	int level = 1;
	StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
	int count = tok.countTokens();
	String[] argv = new String[count];
	for (int i=0; i<count; i++) {
	    argv[i] = tok.nextToken();
	}
	
	if (argv.length != 3) {
	    return "$MOD-CONFIG should have three arguments, e.g. $MOD-CONFIG-show-builders-people";
	} else if (argv[0].equalsIgnoreCase("SHOW")) {
	    String dir = argv[1];
	    String filename = argv[2]+".xml";
	    String path = configpath+File.separator+dir+File.separator+filename;
	    
	    return prettyPrintXML(path);
	    
	}
	return "dummy";
    }
    
    public String getModuleInfo() {
	return("Analysis of mmbase configuration, cjr@dds.nl");
    }
    
    
    private void debug( String msg )
    {
	System.out.println( classname +":"+msg );
    }

    /*
     * @param out PrintWriter to http result
     * @param path Path to the builder file
     * @return Prettified version of the XML file as a string
     */
    protected String prettyPrintXML(String path) {
	XMLScreenWriter screen = new XMLScreenWriter(path);
	StringWriter out = new StringWriter();
	try {
	    screen.write(out);
	    return out.toString();
	} catch (IOException e) {
	    return "Config::prettyPrintXML("+path+"), IOException: "+e.getMessage();
	}
    }
}













