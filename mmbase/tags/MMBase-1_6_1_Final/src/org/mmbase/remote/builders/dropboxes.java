/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remote.builders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.remote.*;
import org.mmbase.service.interfaces.*;

//import org.mmbase.util.logging.Logger;
//import org.mmbase.util.logging.Logging;

/**
 * @rename Dropboxes
  * @version $Revision: 1.9 $ $Date: 2001-12-14 09:33:33 $ 
 * @author Daniel Ockeloen
 */
public class dropboxes extends RemoteBuilder { 
    //Logging removed automaticly by Michiel, and replace with __-methods
    private static String __classname = dropboxes.class.getName();


    boolean __debug = false;
    private static void __debug(String s) { System.out.println(__classname + ":" + s); }
    //private static Logger log = Logging.getLoggerInstance(dropboxes.class.getName()); 

	private dropboxInterface impl;
	StringTagger tagger;

	public void init(MMProtocolDriver con,String servicefile) {
		super.init(con,servicefile);
		/*log.info*/__debug("init("+con+","+servicefile+")");

		// the node was loaded allready so check what the state was
		// and put us in ready/waiting state
		String state=getStringValue("state");
		getConfig();
		if (!state.equals("waiting")) {
			// maybe add 'what' happened code ? but for now
			// just put the service on waiting state
			setValue("state","waiting");
			commit();
		}
	}

	public void nodeRemoteChanged(String nodenr,String buildername,String ctype) {
		if (__debug) {
            /*log.debug*/__debug("nodeRemoteChanged("+nodenr+","+buildername+","+ctype+")");
        }
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeLocalChanged(String nodenr,String buildername,String ctype) {
		if (__debug) {
            /*log.debug*/__debug("nodeLocalChanged("+nodenr+","+buildername+","+ctype+")");
        }
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeChanged(String nodenr,String buildername,String ctype) {
		if (__debug) {
            /*log.debug*/__debug("nodeChanged("+nodenr+","+buildername+","+ctype+")");
        }
		// get the node
		getNode();
				
		String state=getStringValue("state");
		if (__debug) {
            /*log.debug*/__debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): got state("+state+")");
        }
		if (state.equals("version")) {
			doVersion();
		} else if (state.equals("restart")) {
			doRestart();
		} else if (state.equals("dir")) {
			doDir();
		}
	}


	private void doRestart() {
		System.exit(0);
	}

	private void doVersion() {
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	
		if (impl!=null) {
			setValue("info",impl.getVersion());	
		} else {
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	private void doDir() {
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	

		if (impl!=null) {
			String cmds=getStringValue("info");
		    setValue("info",impl.doDir(cmds));	
		} else {
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	/**
	 * Loads the implemention for the dropboxes service using the properties.
	 */
	void getConfig() {
		String implClassName=(String)props.get("implementation");
		if (__debug) {
            /*log.debug*/__debug("getConfig(): impl("+implClassName+")");
        }
		try {
			Class newclass=Class.forName(implClassName);
			impl = (dropboxInterface)newclass.newInstance();
			String tmp=(String)props.get("directory");
			impl.setDir(tmp);
			tmp=(String)props.get("command");
			impl.setCmd(tmp);
			tmp=(String)props.get("wwwpath");
			impl.setWWWPath(tmp);
		} catch (Exception f) {
			/*log.error*/__debug("getConfig(): Can't load class("+implClassName+")");
		}
	}

}
