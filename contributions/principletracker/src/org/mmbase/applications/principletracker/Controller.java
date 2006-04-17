/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.principletracker.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;


/**
 * @author     Daniel Ockeloen
 * @created    April 12, 2006
 */
public class Controller {

    private static Logger log = Logging.getLoggerInstance(Controller.class);
    private static Cloud cloud;


    /**
     *Constructor for the Controller object
     */
    public Controller() {
        cloud = LocalContext.getCloudContext().getCloud("mmbase");
    }

    public String getNextPatchLevel(String version) {
	StringTokenizer tok = new StringTokenizer(version,".\n\r");
	if (tok.hasMoreTokens()) {
	    String mayor = tok.nextToken();
	    if (tok.hasMoreTokens()) {
		String minor = tok.nextToken();
	    	if (tok.hasMoreTokens()) {
			String patchlevel = tok.nextToken();
			try {
				int i = Integer.parseInt(patchlevel);
				return mayor+"."+minor+"."+(i+1);
			} catch(Exception e) {
			  // illegal patchlevel, set to .1
			     return (version+".1");
			}
		} else {
			return (version+".1");
		}
            } else {
		return (version+".0.1");
	    }
	} else {
		return ("1.0");
	}
    }

    public String isOlderVersion(String currentversion, String checkversion) {	
	int cu = getVersionValue(currentversion);
	int ch = getVersionValue(checkversion);
	if (cu==-1 || ch==-1) return "unknown";
	if (ch==cu) return "same";
	if (cu<ch) {
		return "newer";
	} else {
		return "older";
	}
    }


    public String isOlderPatchLevel(String currentversion, String checkversion) {	
	try {
	StringTokenizer tok = new StringTokenizer(currentversion,".\n\r");
	StringTokenizer tok2 = new StringTokenizer(checkversion,".\n\r");
	if (tok.hasMoreTokens() && tok2.hasMoreTokens()) {
	    if (tok.nextToken().equals(tok2.nextToken())) {
		if (tok.hasMoreTokens() && tok2.hasMoreTokens()) {
	    	    if (tok.nextToken().equals(tok2.nextToken())) {
			if (tok.hasMoreTokens() && tok2.hasMoreTokens()) {
				int cu = Integer.parseInt(tok.nextToken());
				int ch = Integer.parseInt(tok2.nextToken());
				if (ch==cu) return "same";
				if (cu<ch) {
					return "newer";
				} else {
					return "older";
				}
			}
		    }
		}
	    }	
	}
      } catch(Exception e) {
	// something went wrong return false;
      }
      return "invalid";
    }
	
    public int getVersionValue(String version) {
      // use a stupid trick that works until we get more than 9999 version
      // per type
      try {
	int value=0;
	StringTokenizer tok = new StringTokenizer(version,".\n\r");
	if (tok.hasMoreTokens()) {
	    value += Integer.parseInt(tok.nextToken())*100000000;
	    if (tok.hasMoreTokens()) {
	    	value += Integer.parseInt(tok.nextToken())*10000;
	    	if (tok.hasMoreTokens()) {
	    		value += Integer.parseInt(tok.nextToken());
		}
            } 
	}
	return value;
      } catch(Exception e) {
	// something went wrong return  -1
      }
      return -1;
    }

    public String getNextPrincipleNumber(String principleset) {
	    // since i didn't want to enforce int only numbers
	    // but to want to help and and guess the next number
	    // we need this kinda kludge 
	    // (seems i did demand Integer afteral, im leaving this in
	    //  since i might change it)
	    int current = 0;
            NodeManager principlesetmanager = cloud.getNodeManager("principlesets");
            NodeManager principlemanager = cloud.getNodeManager("principle");
            Query query = cloud.createQuery();
            Step step1 = query.addStep(principlesetmanager);
            RelationStep step2 = query.addRelationStep(principlemanager);
            StepField f1 = query.addField(step1, principlesetmanager.getField("number"));
            query.addField(step2.getNext(), principlemanager.getField("principlenumber"));

            query.setConstraint(query.createConstraint(f1, new Integer(principleset)));

            NodeIterator i = cloud.getList(query).nodeIterator();
            while (i.hasNext()) {
                Node node = i.nextNode();
		String value = node.getStringValue("principle.principlenumber");
		try {
			int parsed = Integer.parseInt(value);
			if (parsed > current) current = parsed;
		} catch(Exception e) {
		}
	    }
	return ""+(current+1);
    }

    
}

