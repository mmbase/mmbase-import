/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: g2encoders.java,v 1.8 2001-02-15 15:27:29 vpro Exp $

$Log: not supported by cvs2svn $
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
 * @version $Revision: 1.8 $ $Date: 2001-02-15 15:27:29 $
 */
public class g2encoders extends RemoteBuilder {
	private boolean debug = true;
	private g2encoderInterface impl;

	// Destination path where encoded files will be filed under.
	// Has to move to props file but at least the mkdir is done on the remote side.
	public final static String DST_PATH = "/data/audio/ra/";

	public void init(MMProtocolDriver con,String servicefile) {
		super.init(con,servicefile);
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
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeLocalChanged(String nodenr,String buildername,String ctype) {		
		nodeChanged(nodenr,buildername,ctype);
	}

	/**
	 * Gets node from mmbase and checks the node state reacts to state value.
	 * State value 'version' gets the version info, 'encode' starts encoding process. 
	 * @param nodenr a String with servicenode objectnr
	 * @param buildername the name of the service
	 * @param ctype the node changetype.
	 */
	public void nodeChanged(String nodenr,String buildername,String ctype) {		
		// All encoded files are filed under objectnr subdirectory.
		doMakeDir(); 
		// Gets the node by requesting it from the mmbase space through remotexml.
		getNode(); 
				
		String state=getStringValue("state");
		debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): state("+state+")");
		if (state.equals("version")) {
			doVersion();
		} else if (state.equals("encode")) {
			doEncode();
		}
	}

	/**
	 * Makes a directory file in which the encoded file will be placed.
	 * The directory name is the audio/videopart objectnr and the id value of the rawaudio/video
	 * object for the encoded file.
	 * @param subdir The directory filename.
	 */
	private void doMakeDir() {
		debug("doMakeDir: disabled");
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
		debug("doVersion(): commit done");
	}

	/**
	 * Sets the remotebuilder node to 'busy' and starts the Real encoding process, 
	 * when encoding finishes state is set to 'waiting'.
	 * Encoding result information is saved in the nodes info field.
	 */
	private void doEncode() {
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	

		if (impl!=null) {
			String cmds=getStringValue("info");
			if( debug ) debug("doEncode(): starting impl.doEncode("+cmds+")");
		    setValue("info",impl.doEncode(cmds));	
		} else {
			debug("doEncode(): ERROR: cannot encode! No implementation!");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	/**
	 * Loads the implemention for the g2encoders service using the properties.
	 */
	void getConfig() {
		String implClassName=(String)props.get("implementation");
		if( debug ) debug("getConfig(): loading("+implClassName+")");
		try {
			Class newclass=Class.forName(implClassName);
			impl = (g2encoderInterface)newclass.newInstance();
		} catch (Exception f) {
			debug("getConfig(): ERROR: Can't load class("+implClassName+")");
			f.printStackTrace();
		}
	}

}
