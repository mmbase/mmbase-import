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
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

public class LinkChecker extends ProcessorModule implements Runnable {

	private String classname = getClass().getName();

    Thread kicker = null;
	MMBase mmbase;
	MMObjectBuilder urls;
	MMObjectBuilder jumpers;
	SendMail sendmail;

	public void init() {
		super.init();
	    mmbase=(MMBase)getModule("MMBASEROOT");
		urls=(MMObjectBuilder)mmbase.getMMObject("urls");
		jumpers=(MMObjectBuilder)mmbase.getMMObject("jumpers");
		sendmail=(SendMail)getModule("sendmail");
		start();
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}

	public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
		return(null);
	}

	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		System.out.println("CMDS="+cmds);
		System.out.println("VARS="+vars);
		return(false);
	}

	public String replace(scanpage sp, String cmds) {
		return "";
	}

	public String getModuleInfo() {
		return("This module checks all urls, Rob Vermeulen");
	}

	public void maintainance() {
	}
	
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"LinkChecker");
            kicker.start();
        }
    }

    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker.suspend();
        kicker.stop();
        kicker = null;
    }

    public void run () {
		// Wait till all builders are loaded.
		try { Thread.sleep(10000); } catch (Exception wait) { }
        String from=getInitParameter("from");
        String to=getInitParameter("to");

		
		String data="";

        try {
			// Get the urls and Jumper builders.
			if(urls==null) {
   				urls=(MMObjectBuilder)mmbase.getMMObject("urls");
    		}
			if(jumpers==null) {
   				jumpers=(MMObjectBuilder)mmbase.getMMObject("jumpers");
    		}
			if(sendmail==null) {
				sendmail=(SendMail)getModule("sendmail");
			}

			// Get all urls.
         	Enumeration e = urls.search("");
    		while (e.hasMoreElements()) {
            	MMObjectNode url = (MMObjectNode)e.nextElement();
            	String number = ""+url.getValue("number");
            	String theUrl = ""+url.getValue("url");
				// Check if an url is correct.
				if(!checkUrl(theUrl)) {
					//System.out.println("LinkChecker -> Error in url "+theUrl +" (objectnumber="+number+")");
					data+="Error in url "+theUrl +" (objectnumber="+number+")\n";
				}
    		}
			// Get all jumpers.
         	e = jumpers.search("");
    		while (e.hasMoreElements()) {
            	MMObjectNode jumper = (MMObjectNode)e.nextElement();
            	String number = ""+jumper.getValue("number");
            	String theUrl = ""+jumper.getValue("url");
				// Check if an jumper is correct.
				if(!checkUrl(theUrl)) {
					//System.out.println("LinkChecker -> Error in jumper "+theUrl +" (objectnumber="+number+")");
					data+="Error in jumper "+theUrl +" (objectnumber="+number+")\n";
				}
    		}

			// Send Email
			sendmail.sendMail(from,to,data);
        } catch (Exception e) {
            System.out.println("LinkChecker -> Error in Run()");
            e.printStackTrace();
        }
    }

	/**
	 * Checks if an url exists.
	 * @param the url to check
	 * @return false if the url does not exist
	 * @return true if the url exists
	 */
	public boolean checkUrl(String url) {
	    URL urlToCheck;
		URLConnection uc;
		String header;

        try {
            urlToCheck = new URL(url);
			uc = urlToCheck.openConnection();
			header = uc.getHeaderField(0);	
			if(header.indexOf("404")!=-1) return false;
        } catch (Exception e) {
			// The hostname is incorrect, or the url does not contain the prefix http://
			return false;
        }
		// I don't know if the url is wrong, so lets say it's correct.
		return true;
	}
}
