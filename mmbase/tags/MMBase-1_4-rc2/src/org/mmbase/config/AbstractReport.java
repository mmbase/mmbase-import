/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.config;

import java.lang.*;
import java.util.*;
import java.io.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Case Roole, cjr@dds.nl
 * 
 * $Id: AbstractReport.java,v 1.5 2001-07-16 10:08:07 jaco Exp $
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2001/07/09 12:30:02  jaco
 * jaco: Changed old method for retrieving mmbase.config and mmbase.htmlroot with new method.
 *
 * Revision 1.3  2001/04/10 11:02:07  michiel
 * michiel: new logging system
 *
 * Revision 1.2  2000/10/07 17:06:07  case
 * cjr: Added checking of mmbase JVM options and some minor bugfixes
 *
 */
public abstract class AbstractReport implements ReportInterface {
    protected String mode,encoding;
    protected Hashtable specialChars;
    protected String configpath;

    private static Logger log = Logging.getLoggerInstance(AbstractReport.class.getName()); 
    //protected String classname = getClass().getName();
    //protected boolean debug = false;

    // --- public methods ------------------------------------------
    public void init(String mode, String encoding) {
	this.mode = mode;
	this.encoding = encoding;
	if (encoding.equalsIgnoreCase("HTML")) {
	    specialChars = getHTMLChars();
	} else if (encoding.equalsIgnoreCase("TEXT")) {
	    specialChars = getTEXTChars();
	} else {
	    specialChars = getTEXTChars();
	}
	configpath = getMMBaseConfigPath();
    }
    
    public String report() {
	return "";
    }

    // --- protected utility methods --------------------------------
    /**
     * Replace a substring
     *
     * @param s String which is to be modified
     * @param sub Substring to be replaced (once!)
     * @param rep Replacement string
     *
     * @return <code>s</code> with substring <code>sub</code> replaced with <code>rep</code>
     */
    protected String stringReplace(String s, String sub, String rep) {
	String res;
	int n = s.indexOf(sub);
	if (n >= 0) {
	    return s.substring(0,n) + rep + s.substring(n+sub.length());
	} else {
	    return s;
	}
    }

    /**
     * Retrieve all xml files in a directory
     * 
     * @param path Directory path
     * @return String array containing the names of the xml files in the directory, without the extension
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
     * Read an XML file in which key/value pairs are represented as tag and content
     *
     * @param path Full path to XML file
     * 
     * @return Hashtable with the key/value pairs or an empty Hashtable if something went wrong.
     */
    protected Hashtable getPropertiesFromXML(String path) {
	XMLProperties xmlReader = new XMLProperties();
	SAXParser parser = new SAXParser();
	// Telling the parser it must not use some features
	// we're not using right now as dtd-validation and namespaces
	try {
	    parser.setFeature("http://xml.org/sax/features/validation",false);
	    parser.setFeature("http://xml.org/sax/features/namespaces",false);
	} catch (SAXNotSupportedException ex) {
	    log.debug("Config::databaseIsActive: failed because parser doesn't support feature");
	    ex.printStackTrace();
	}
	catch (SAXNotRecognizedException ex) {
	    log.debug("Config::databaseIsActive(): failed because parser didn't recognized feature");
	    ex.printStackTrace();
	}
	// create new ContentHandler and let the parser use it
	xmlReader = new XMLProperties();
	parser.setContentHandler(xmlReader);
	
	// get us a (normal) propertie reader
	Hashtable mods = null;
	
	// load the
	String filename=configpath+File.separator+path;
	// filename=filename.replace('/',(System.getProperty("file.separator")).charAt(0));
	// filename=filename.replace('\\',(System.getProperty("file.separator")).charAt(0));
	
	// check if there's a xml-configuration file
	try {
	    parser.parse(new InputSource(filename));
	    mods = xmlReader.getProperties();
	} catch (Exception e) {
	    log.error("Error reading xml properties file " + path + ". Returning empty hashtable.");
	    mods = new Hashtable();
	}
	return mods;
    }

    /**
     * @return String with '<' and '>' converted to respectively &lt; and &gt;
     */
    protected String htmlEntities(String s) {
        StringBuffer res = new StringBuffer();
        char c;
        for (int i=0;i<s.length();i++) {
            c = s.charAt(i);
            switch (c) {
            case '>':
                res.append("&gt;");
                break;
            case '<':
                res.append("&lt;");
                break;
            default:
                res.append(c);
            }
        }
        return res.toString();
    }

    protected String xmlErrorMessage(String path, XMLParseResult pr) {
	String eol = (String)specialChars.get("eol");
	LineNumberReader f = null;
	try {
	    f = new LineNumberReader(new FileReader(path));
	} catch (IOException canthappen) {
	}

	String res = "";
	Vector fatalList = pr.getFatalList();
	for (int j=0;j<fatalList.size();j++) {
	    ErrorStruct fatalerror = (ErrorStruct)fatalList.elementAt(j);
	    int lineno = fatalerror.getLineNumber();
	    int col = fatalerror.getColumnNumber();
	    String msg = fatalerror.getMessage();
	    
	    if (f != null) {
		try {
		    int i = f.getLineNumber();
		    while (f.ready() && i<lineno-1) {
			f.readLine();
			i++;
		    }
		    String line = f.readLine();
		    if (line != null) {
			if (encoding.equalsIgnoreCase("html")) {
			    line = htmlEntities(line);
			}
			res = res + "*** line "+lineno+": column "+col+": "+msg + eol;
			res = res + "*** " + line + eol;
		    }
		} catch (IOException e) {
		    res = res + "*** IOException reading line " + lineno + " in " + path + ":" + eol + e.getMessage();
		}
	    }
	}
	return res;
    }
    
    // --- private methods -------------------------------------------
    
    /**
     * @return Hashtable with some special characters represented for HTML
     */
    private Hashtable getHTMLChars() {
	Hashtable h = new Hashtable();
	h.put("amp","&amp;amp");
	h.put("eol","<br>\n");
	return h;
    }


    /**
     * @return Hashtable with some special characters represented for TEXT
     */
    private Hashtable getTEXTChars() {
	Hashtable h = new Hashtable();
	h.put("amp","&amp;");
	h.put("eol","\n");
	return h;
    }

    private String getMMBaseConfigPath() {
	return MMBaseContext.getConfigPath();
    }
}
