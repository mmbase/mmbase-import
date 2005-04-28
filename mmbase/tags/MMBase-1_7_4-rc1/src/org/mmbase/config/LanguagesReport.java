/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.config;

import java.util.*;
import java.io.*;

/**
 * Report can be generated by SCAN, see: /mmadmin/config/report.shtml
 * or from the commandline:
 * java -Dmmbase.config=myconfigpath org.mmbase.config.Test
 *
 * @author Case Roole, cjr@dds.nl
 * @version $Id: LanguagesReport.java,v 1.4 2003-03-10 11:50:11 pierre Exp $
 */
public class LanguagesReport extends AbstractReport {
    
    // --- public methods ---------------------------------------
    public String label() {
	return "Localization/Languages";
    }
    /**
     * @return String with localization (languages) configuration
     */
    public String report() {
	String res = "";
	String eol = (String)specialChars.get("eol");

	if (configpath == null) {
	    res = res + "mmbase.config option is not set - can't find configuration files" + eol;
	} else {

	    if (hasLegacyMMLanguage()) {
		res = res + "*** warning: using legacy mmlanguage.xml file" + eol +
		    "!!! On removing "+configpath+File.separator+"modules"+File.separator+"mmlanguage.xml" + eol +
		    "!!! the language support in "+configpath+File.separator+"modules"+File.separator+"languages will be used" + eol;
	    } else {
		Vector v = getSupportedLanguages();
		if (v.size() == 0) {
		    res = res + "*** No language files found in "+languagePath()+eol;
		} else {
		    Hashtable parseList = new Hashtable();
		    boolean allOk = true;
		    
		    for (int i=0; i<v.size(); i++) {
			String isolang = (String)v.elementAt(i);
			XMLParseResult pr = new XMLParseResult(languagePath()+File.separator+isolang+".xml");
			parseList.put(isolang,pr);
			if (pr.getResultList().size() > 0) {
			    allOk = false;
			}
		    }
		    
		    if (allOk) {
			res = res + "Supported languages: ";
		    } else {
			res = res + "Found language files for: ";
		    }
		    for (int i=0; i < v.size(); i++) {
			String isolang = (String)v.elementAt(i);
			res = res + v.elementAt(i);
			if (i<v.size()-1) {
			    res = res + ", ";
			}
		    }
		    res = res + eol;
		    
		    if (!allOk) {
			for (int i=0; i < v.size(); i++) {
			    String isolang = (String)v.elementAt(i);
			    XMLParseResult pr = (XMLParseResult)parseList.get(isolang);
			    String path = languagePath() + File.separator + isolang + ".xml";
			    if (pr.getResultList().size() > 0) {
				res = res + "*** XML error for " + path + eol;
				res = res + xmlErrorMessage(path,pr) + eol;   
			    }
			}
			
		    }
		}
	    }
	}
	return res;
    }

    // --- private methods ---------------------------------------
    private String languagePath() {
	return configpath + File.separator + "modules" + File.separator + "languages";
    }

    private boolean hasLegacyMMLanguage() {
	File f = new File(configpath+File.separator+"modules"+File.separator+"mmlanguage.xml");
	return f.exists();
    }

    private Vector getSupportedLanguages() {
	Vector v;
	try {
	    v = listDirectory(configpath+File.separator+"modules"+File.separator+"languages");
	} catch (IOException e) {
	    v = new Vector();
	}
	return v;
    }

}
