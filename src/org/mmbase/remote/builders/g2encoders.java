/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: g2encoders.java,v 1.14 2001-04-11 15:31:22 michiel Exp $

$Log: not supported by cvs2svn $
Revision 1.13  2001/03/15 16:15:58  vpro
Davzev: Removed setValue to print what remotebuilder is doing when busy working, cause it distored communication between mmbase and remotebuilders.

Revision 1.12  2001/03/14 16:42:02  vpro
Davzev: Added setValue to print what remotebuilder is doing when busy working.

Revision 1.11  2001/03/07 14:19:32  vpro
Davzev: Changed debug when state is busy in method nodeChanged

Revision 1.10  2001/02/20 17:45:17  vpro
Davzev: Added method comments, more debug and moved doMakeDir() sothat it only will be called when state is encode.

Revision 1.9  2001/02/19 10:10:11  vpro
Davzev: Changed wrong debug in doMakeDir.

Revision 1.8  2001/02/15 15:27:29  vpro
Davzev: Added, changed debug and moved the mkdir from local to remote side.

*/
package org.mmbase.remote.builders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.remote.*;
import org.mmbase.service.interfaces.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Daniel Ockeloen
 * @version $Revision: 1.14 $ $Date: 2001-04-11 15:31:22 $
 */
public class g2encoders extends RemoteBuilder {

    private static Logger log = Logging.getLoggerInstance(g2encoders.class.getName()); 

	private g2encoderInterface impl;
	// Destination path where encoded files will be filed under.
	// Has to move to props file but at least the mkdir is done on the remote side.
	public final static String DST_PATH = "/data/audio/ra/";

	/**
	 * Initializes itself, then gets the g2encoder config and resets its' when it isn't 'waiting'.
	 * @param con protocoldriver
	 * @param servicefile the servicefile that contains the config. 
	 */
	public void init(MMProtocolDriver con,String servicefile) {
		if (log.isDebugEnabled()) {
            log.debug("init: Initializing, getting config."); 
        }
		super.init(con,servicefile);
		// the node was loaded allready so check what the state was
		// and put us in ready/waiting state
		String state=getStringValue("state");
		getConfig();
		if (!state.equals("waiting")) {
			log.warn("init: State: "+state+"!=waiting, resetting it to waiting!"); 
			// maybe add 'what' happened code ? but for now
			// just put the service on waiting state
			setValue("state","waiting");
			commit();
		}
	}

	/**
	 * Called when the service node state was changed on another server, in turn this method calls
	 * nodeChanged to check and react to the new state.
	 * @param serviceRef a String with a reference to the service node who's state has been changed.
	 * @param builderName a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 */
	public void nodeRemoteChanged(String serviceRef,String buildername,String ctype) {		
		if (log.isDebugEnabled()) {
            log.debug("nodeRemoteChanged: Calling nodeChanged");
        }
		nodeChanged(serviceRef,buildername,ctype);
	}

	/**
	 * Called when node was changed on the local side, in turn this routine calls nodeChanged
	 * to check out and react to the new state.
	 * @param serviceRef a String with a reference to the service node who's state has been changed.
	 * @param builderName a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 */
	public void nodeLocalChanged(String serviceRef,String buildername,String ctype) {		
		if (log.isDebugEnabled()) {
            log.debug("nodeLocalChanged: Calling nodeChanged");
        }
		nodeChanged(serviceRef,buildername,ctype);
	}

	/**
	 * Gets node from mmbase and checks the node state reacts to state value.
	 * State value 'version' gets the version info, 'encode' starts encoding process. 
	 * @param serviceRef a String with a reference to the service node who's state has been changed.
	 * @param buildername the name of the service
	 * @param ctype the node changetype.
	 */
	public void nodeChanged(String serviceRef,String buildername,String ctype) {		
		if (log.isDebugEnabled()) {
            log.debug("nodeChanged("+serviceRef+","+buildername+","+ctype+") Getting "+buildername+" node "+serviceRef+" to find out what to do.");
        }
		// Gets the node by requesting it from the mmbase space through remotexml.
		getNode(); 
				
		String state=getStringValue("state");
		log.info("nodeChanged("+serviceRef+","+buildername+","+ctype+"): state("+state+")");
		if (state.equals("version")) {
			log.info("nodeChanged("+serviceRef+","+buildername+","+ctype+"): doVersion()");
			doVersion();
		} else if (state.equals("encode")) {
			// All encoded files are filed under objectnr subdirectory.
			log.info("nodeChanged("+serviceRef+","+buildername+","+ctype+"): state:"+state+", first make subdir");
			doMakeDir(); 
			// Start the encoding process.
			doEncode();
		} else if (state.equals("busy")) {
			log.info("nodeChanged("+serviceRef+","+buildername+","+ctype+"): "+buildername+" RemoteBuilder named "+getStringValue("name")+" is "+state);
		} else {
			log.error("nodeChanged("+serviceRef+","+buildername+","+ctype+"): unknown state: "+state+", ignoring it");
		}
	}

	/**
	 * Makes a directory file in which the encoded file will be placed.
	 * The directory name is the audio/videopart objectnr and the id value of the rawaudio/video
	 * object for the encoded file.
	 * @param subdir The directory filename.
	 */
	private void doMakeDir() {
        if (log.isDebugEnabled()) {
            log.debug("doMakeDir: Getting subdir tag from info field.");
        }
		String subdir=null;
		StringTagger cmdTagger = new StringTagger(getStringValue("info"));
		if (cmdTagger.containsKey("subdir")) {
			subdir = (String) cmdTagger.Value("subdir");
		} else {
			log.error("doMakeDir: no 'subdir' key in info field.");
        }

		File file = new File(DST_PATH+"/"+subdir);
		try {
			if (file.mkdir())
				if (log.isDebugEnabled()) {
                    log.debug("doMakeDir: Directory "+subdir+" created in "+DST_PATH);
                }
		} catch (Exception f) {
		    log.error("doMakeDir: making directory "+subdir+" in "+DST_PATH);
			log.error(Logging.stackTrace(f));
		}
	}

	/**
	 * Gets the version information from the g2encoder.
	 */
	private void doVersion() {
		if (log.isDebugEnabled()) {
            log.debug("doVersion: Setting state to busy and get the version info.");
        }
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	
		if (impl!=null) {
			setValue("info",impl.getVersion());	
		} else {
			debug("doVersion: ERROR, implementation reference is null, filling info with error info.");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		if (log.isDebugEnabled()) {
            log.debug("doVersion: Setting state to waiting and return.");
        }
		setValue("state","waiting");
		commit();
	}

	/**
	 * Sets the remotebuilder node to 'busy' and starts the Real encoding process, 
	 * when encoding finishes state is set to 'waiting'.
	 * Encoding result information is saved in the nodes info field.
	 */
	private void doEncode() {
        if (log.isDebugEnabled()) {
            log.debug("doEncode: Setting state to busy and start encoding.");
        }
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	

		if (impl!=null) {
			String cmds=getStringValue("info");
            if (log.isDebugEnabled()) {
                log.debug("doEncode(): starting impl.doEncode("+cmds+")");
            }
		    setValue("info",impl.doEncode(cmds));	
		} else {
			log.error("doEncode implementation reference is null, filling info with error info.");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
        if (log.isDebugEnabled()) {
            log.debug("doEncode: Setting state to waiting and return.");
        }
		setValue("state","waiting");
		commit();
	}

	/**
	 * Loads the implemention for the g2encoders service using the properties.
	 */
	void getConfig() {
		String implClassName=(String)props.get("implementation");
        if (log.isDebugEnabled()) {
            log.debug("getConfig(): loading("+implClassName+")");
        }
		try {
			Class newclass=Class.forName(implClassName);
			impl = (g2encoderInterface)newclass.newInstance();
		} catch (Exception f) {
			log.error("getConfig(): Can't load class("+implClassName+")");
			log.error(Logging.stackTrace(f));
		}
	}

}
