/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: g2encoders.java,v 1.10 2001-02-20 17:45:17 vpro Exp $

$Log: not supported by cvs2svn $
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


/**
 * @author Daniel Ockeloen
 * @version $Revision: 1.10 $ $Date: 2001-02-20 17:45:17 $
 */
public class g2encoders extends RemoteBuilder {
	private boolean debug = true;
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
		if (debug) debug("init: Initializing, getting config."); 
		super.init(con,servicefile);
		// the node was loaded allready so check what the state was
		// and put us in ready/waiting state
		String state=getStringValue("state");
		getConfig();
		if (!state.equals("waiting")) {
			debug("init: WARNING: State: "+state+"!=waiting, resetting it to waiting!"); 
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
		if (debug) debug("nodeRemoteChanged: Calling nodeChanged");
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
		if (debug) debug("nodeLocalChanged: Calling nodeChanged");
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
		if (debug) debug("nodeChanged("+serviceRef+","+buildername+","+ctype+"): Getting node from mmbase.");
		// Gets the node by requesting it from the mmbase space through remotexml.
		getNode(); 
				
		String state=getStringValue("state");
		debug("nodeChanged("+serviceRef+","+buildername+","+ctype+"): state("+state+")");
		if (state.equals("version")) {
			doVersion();
		} else if (state.equals("encode")) {
			// All encoded files are filed under objectnr subdirectory.
			debug("nodeChanged("+serviceRef+","+buildername+","+ctype+"): state:"+state+", first make subdir");
			doMakeDir(); 
			// Start the encoding process.
			doEncode();
		} else {
			debug("nodeChanged("+serviceRef+","+buildername+","+ctype+"): ERROR, Unknown state:"+state);
		}
	}

	/**
	 * Makes a directory file in which the encoded file will be placed.
	 * The directory name is the audio/videopart objectnr and the id value of the rawaudio/video
	 * object for the encoded file.
	 * @param subdir The directory filename.
	 */
	private void doMakeDir() {
		debug("doMakeDir: Getting subdir tag from info field.");
		String subdir=null;
		StringTagger cmdTagger = new StringTagger(getStringValue("info"));
		if (cmdTagger.containsKey("subdir"))
			subdir = (String) cmdTagger.Value("subdir");
		else
			debug("doMakeDir: ERROR no 'subdir' key in info field.");

		File file = new File(DST_PATH+"/"+subdir);
		try {
			if (file.mkdir())
				if (debug) debug("doMakeDir: Directory "+subdir+" created in "+DST_PATH);
		} catch (Exception f) {
			debug("doMakeDir: ERROR making directory "+subdir+" in "+DST_PATH);
			f.printStackTrace();
		}
	}

	/**
	 * Gets the version information from the g2encoder.
	 */
	private void doVersion() {
		if (debug) debug("doVersion: Setting state to busy and get the version info.");
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
		if (debug) debug("doVersion: Setting state to waiting and return.");
		setValue("state","waiting");
		commit();
	}

	/**
	 * Sets the remotebuilder node to 'busy' and starts the Real encoding process, 
	 * when encoding finishes state is set to 'waiting'.
	 * Encoding result information is saved in the nodes info field.
	 */
	private void doEncode() {
		if (debug) debug("doEncode: Setting state to busy and start encoding.");
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	

		if (impl!=null) {
			String cmds=getStringValue("info");
			if (debug) debug("doEncode(): starting impl.doEncode("+cmds+")");
		    setValue("info",impl.doEncode(cmds));	
		} else {
			debug("doEncode: ERROR, implementation reference is null, filling info with error info.");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		if (debug) debug("doEncode: Setting state to waiting and return.");
		setValue("state","waiting");
		commit();
	}

	/**
	 * Loads the implemention for the g2encoders service using the properties.
	 */
	void getConfig() {
		String implClassName=(String)props.get("implementation");
		if (debug) debug("getConfig(): loading("+implClassName+")");
		try {
			Class newclass=Class.forName(implClassName);
			impl = (g2encoderInterface)newclass.newInstance();
		} catch (Exception f) {
			debug("getConfig(): ERROR: Can't load class("+implClassName+")");
			f.printStackTrace();
		}
	}

}
