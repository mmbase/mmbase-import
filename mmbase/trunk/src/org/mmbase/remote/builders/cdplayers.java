/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: cdplayers.java,v 1.11 2001-04-11 15:31:22 michiel Exp $

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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @version $Revision: 1.11 $ $Date: 2001-04-11 15:31:22 $
 * @author Daniel Ockeloen
 */
public class cdplayers extends RemoteBuilder {

    private static Logger log = Logging.getLoggerInstance(cdplayers.class.getName()); 

	private cdplayerInterface impl;

	public void init(MMProtocolDriver con,String servicefile) {
		super.init(con,servicefile);
		// the node was loaded allready so check what the state was
		// and put us in ready/waiting state
		String state=getStringValue("state");
		getConfig();
		log.info("init(): state("+state+")");
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
		if (log.isDebugEnabled()) {
            log.debug("nodeRemoteChanged("+serviceRef+","+builderName+","+ctype+"): Calling nodeChanged");
        }
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
		if (log.isDebugEnabled()) {
            log.debug("nodeLocalChanged("+serviceRef+","+builderName+","+ctype+"): Calling nodeChanged");
        }
		nodeChanged(serviceRef,builderName,ctype);
	}

	/**
	 * Check to see what the status of the service node has become and react to it.
	 * @param serviceRef a String with a reference to the service node who's state has been changed.
	 * @param builderName a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 */
	public void nodeChanged(String nodenr,String buildername,String ctype) {		
		log.info("nodeChanged("+nodenr+","+buildername+","+ctype+") Getting "+buildername+" node "+nodenr+" to find out what to do.");
		// gets the node using a request.
		getNode();
				
		String state=getStringValue("state");
		log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): Node state is: "+state);
		if (state.equals("version")) {
			log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): doVersion()");
			doVersion();
		} else if (state.equals("record")) {
			log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): doRecord()");
			doRecord();
		} else if (state.equals("getdir")) {
			log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): doGetDir()");
			doGetDir();
		} else if (state.equals("restart")) {
			log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): doRestart()");
			doRestart();
		} else if (state.equals("version")) {
			log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): doVersion()");
			doVersion();
		} else if (state.equals("claimed")) {
			log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): setClaimed()");
			setClaimed();
		} else if (state.equals("busy")) {
			log.info("nodeChanged("+nodenr+","+buildername+","+ctype+"): "+buildername+" RemoteBuilder named "+getStringValue("name")+" is "+state);
		} else {
			log.error("nodeChanged("+nodenr+","+buildername+","+ctype+"): unknown state: "+state+", ignoring it");
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
			log.error(error);
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
            log.error("doGetDir(): no implementation!");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	private void doRecord() {
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	

		if (impl!=null) {
			String cmds=getStringValue("info");
			if (log.isDebugEnabled()) {
                log.debug ("doRecord(): cmds("+cmds+")");
            }
			StringTagger tagger=new StringTagger(cmds);

			try {
				String tmp=tagger.Value("tracknr");
				if (log.isDebugEnabled()) {
                    log.debug ("doRecord(): track("+tmp+")");
                }
				int tracknr=Integer.parseInt(tmp);

				tmp=tagger.Value("id");
				if (log.isDebugEnabled()) {
                    log.debug ("doRecord(): id("+tmp+")");
                }
				int cdid=Integer.parseInt(tmp);

		    	log.info("doRecord(): getTrack("+tracknr+" /data/audio/wav/"+cdid+".wav");	
		    	boolean result=impl.getTrack(tracknr,"/data/audio/wav/"+cdid+".wav");	
				if (result) {
		   		 	setValue("info","result=ok");	
				} else {
					log.error("doRecord(): result("+result+")");
					setValue("info","result=err reason=recordfailed");	
				}
			} catch(Exception e){
				log.error("doRecord(): Got exception: " + e);
				log.error(Logging.stackTrace(e));
			}
		} else {
			log.error("doRecord(): no implementation!");
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
		log.info("getConfig(): loading("+implClassName+")");
		try {
			Class newclass=Class.forName(implClassName);
			impl = (cdplayerInterface)newclass.newInstance();
		} catch (Exception f) {
			log.error("getConfig(): Can't load class("+implClassName+")");
			log.error(Logging.stackTrace(f));
		}
	}
}
