/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: cdplayers.java,v 1.12 2001-04-19 14:02:21 vpro Exp $

$Log: not supported by cvs2svn $
Revision 1.10  2001/03/15 16:15:58  vpro
Davzev: Removed setValue to print what remotebuilder is doing when busy working, cause it distored communication between mmbase and remotebuilders.

Revision 1.9  2001/03/14 16:42:02  vpro
Davzev: Added setValue to print what remotebuilder is doing when busy working.

Revision 1.8  2001/03/07 14:19:32  vpro
Davzev: Changed debug when state is busy in method nodeChanged

Revision 1.7  2001/01/24 13:42:30  vpro
davzev: Removed StringTagger classvariable (not used) added printStackTrace to catches.

Revision 1.6  2001/01/05 14:12:33  vpro
Davzev: Changed var classname in getConfig() to implClassName cause our debug also uses a var name classname.

Revision 1.5  2000/11/27 12:35:09  vpro
davzev: Removed debug method now uses super, added comments

*/
package org.mmbase.remote.builders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.remote.*;
import org.mmbase.service.interfaces.*;


/**
 * @version $Revision: 1.12 $ $Date: 2001-04-19 14:02:21 $
 * @author Daniel Ockeloen
 */
public class cdplayers extends RemoteBuilder {

	private cdplayerInterface impl;
 	private boolean debug = true;

	// Destination path where ripped files will be filed under.
	public final static String DST_PATH = "/data/audio/wav";

	public void init(MMProtocolDriver con,String servicefile) {
		super.init(con,servicefile);
		// the node was loaded allready so check what the state was
		// and put us in ready/waiting state
		String state=getStringValue("state");
		getConfig();
		debug("init(): state("+state+")");
		if (!state.equals("waiting")) {

			// maybe add 'what' happened code ? but for now
			// just put the service on waiting state

			setValue("state","waiting");
			commit();
		}
	}

	/**
	 * Called when the service node state was changed on another server, in turn this method calls
	 * nodeChanged to check out and react to  the new state.
	 * @param serviceRef a String with a reference to the service node who's state has been changed.
	 * @param builderName a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 */
	public void nodeRemoteChanged(String serviceRef,String builderName,String ctype) {		
		if( debug ) debug("nodeRemoteChanged("+serviceRef+","+builderName+","+ctype+"): Calling nodeChanged");
		nodeChanged(serviceRef,builderName,ctype);
	}

	/**
	 * Called when node was changed on the local side, in turn this routine calls nodeChanged
	 * to check out and react to the new state.
	 * @param serviceRef a String with a reference to the service node who's state has been changed.
	 * @param builderName a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 */
	public void nodeLocalChanged(String serviceRef,String builderName,String ctype) {		
		// I don't think this method is used though? says davzev
		if (debug) debug("nodeLocalChanged("+serviceRef+","+builderName+","+ctype+"): Calling nodeChanged");
		nodeChanged(serviceRef,builderName,ctype);
	}

	/**
	 * Check to see what the status of the service node has become and react to it.
	 * @param serviceRef a String with a reference to the service node who's state has been changed.
	 * @param builderName a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 */
	public void nodeChanged(String nodenr,String buildername,String ctype) {		
		debug("nodeChanged("+nodenr+","+buildername+","+ctype+") Getting "+buildername+" node "+nodenr+" to find out what to do.");
		// gets the node using a request.
		getNode();
				
		String state=getStringValue("state");
		debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): Node state is: "+state);
		if (state.equals("version")) {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): doVersion()");
			doVersion();
		} else if (state.equals("record")) {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): doRecord()");
			doRecord();
		} else if (state.equals("getdir")) {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): doGetDir()");
			doGetDir();
		} else if (state.equals("restart")) {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): doRestart()");
			doRestart();
		} else if (state.equals("version")) {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): doVersion()");
			doVersion();
		} else if (state.equals("claimed")) {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): setClaimed()");
			setClaimed();
		} else if (state.equals("busy") || state.equals("waiting")) {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): "+buildername+" RemoteBuilder named "+getStringValue("name")+" is "+state);
		} else {
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): ERROR unknown state: "+state+", ignoring it");
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
			String error = "doVersion(): ERROR: no implementation-code for cdplayers installed, look in ./mmbase/service for it!";
			debug( error );
			setValue("info",error);	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	private void doGetDir() {
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	
		if (impl!=null) {
			setValue("info",impl.getInfoCDtoString());	
		} else {
			debug("doGetDir(): ERROR: no implementation!");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

        /**
         * Sets the remotebuilder node to 'busy' and starts the Ripping process,
         * when ripping finishes, the process exitvalue and related audiopartnr is
	 * saved in the cdplayers.info field as result=exitvalue id=#audiopartnr
	 * After that the cdplayres state is set to 'waiting'.
	 * If the implementation cannot be found "result=err reason=nocode" is saved in info field,
	 * however this is an old error message which is ignored by the new error handling.
         */
	private void doRecord() {
		 if (debug) debug("doEncode: Setting state to busy and start ripping.");
		setValue("state","busy");
		commit();
	
		// Do the cdrip and set encode exit value together with audiopartnr encoded in info field.
		if (impl!=null) {
			String cmds=getStringValue("info");
			if (debug) debug("doRecord(): Getting tracknr & id from info field:"+cmds);
			int tracknr=-1;
			StringTagger infoTagger=new StringTagger(cmds);
			if (infoTagger.containsKey("tracknr")) {
				try {
					tracknr = Integer.parseInt((String)infoTagger.Value("tracknr"));
				} catch (NumberFormatException nfe)  {
					debug("doRecord(): ERROR: tracknr is not an int.");
					nfe.printStackTrace();
				}
				if (debug) debug ("doRecord(): Found 'tracknr' tag, value:"+tracknr);
			} else
				debug("doRecord: ERROR no 'tracknr' key in info field.");
			String id=null;	
			if (infoTagger.containsKey("id")) {
				id=(String)infoTagger.Value("id");
				if (debug) debug ("doRecord(): Found 'id' tag, value:"+id);
			} else
				debug("doRecord: ERROR no 'id' key in info field.");

			debug("doRecord(): Calling impl.getTrack("+tracknr+","+DST_PATH+"/"+id+".wav");	
			int exit=impl.getTrack(tracknr,DST_PATH+"/"+id+".wav");	
			setValue("info","result="+exit+" id="+id);
		} else {
			debug("doRecord(): ERROR: no implementation!");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	/**
	 * Loads the implemention for the cdplayers service using the properties.
	 */
	void getConfig() {
		String implClassName=(String)props.get("implementation");
		debug("getConfig(): loading("+implClassName+")");
		try {
			Class newclass=Class.forName(implClassName);
			impl = (cdplayerInterface)newclass.newInstance();
		} catch (Exception f) {
			debug("getConfig(): ERROR: Can't load class("+implClassName+")");
			f.printStackTrace();
		}
	}
}
