/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;


/**
 * @author Daniel Ockeloen
 */
public class MMLanguage extends ProcessorModule {

	MMBase mmb=null;
	String languagePrefix;

	public void init() {
		mmb=(MMBase)getModule("MMBASEROOT");		
		languagePrefix=mmb.getLanguage();
	}


	/**
	 */
	public MMLanguage() {
	}

	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("GET")) { 
				if (tok.hasMoreTokens()) {
					return(getFromCoreEnglish(tok.nextToken()));
				} else {
					return("missing core term");
				}
			}
		}
		return("No command defined");
	}


	private String getFromCoreEnglish(String term) {
		String translated=getInitParameter(languagePrefix+"_"+term);
		if (translated==null || translated.equals("")) {
			System.out.println("MMLangauge -> could not convert : "+term+" into : "+languagePrefix);
			return(term);
		} else {
			return(translated);
		}
	}
}
