/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.remote.builders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.remote.*;
import org.mmbase.service.interfaces.*;
import org.mmbase.service.implementations.*;


/**
 * @version  3 Okt 1999
 * @author Daniel Ockeloen
 */
public class cdplayers extends RemoteBuilder {

	private String _classname = getClass().getName();
	private boolean _debug = true;
	private void debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	private cdplayerInterface impl;
	private String classname;
	StringTagger tagger;

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

	public void nodeRemoteChanged(String nodenr,String buildername,String ctype) {		
		if( _debug ) debug("nodeRemoteChanged("+nodenr+","+buildername+","+ctype+")");
		 nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeLocalChanged(String nodenr,String buildername,String ctype) {		
		if( _debug ) debug("nodeLocalChanged("+nodenr+","+buildername+","+ctype+")");
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeChanged(String nodenr,String buildername,String ctype) {		
		debug("nodeChanged("+nodenr+","+buildername+","+ctype+")");
		// get the node
		getNode();
				
		String state=getStringValue("state");
		debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): got state("+state+")");
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
			debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): doClaimed()");
			setClaimed();
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
			debug("doVersion(): ERROR: no implementation!");
			setValue("info","result=err reason=nocode");	
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

	private void doRecord() {
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	

		if (impl!=null) {
			String cmds=getStringValue("info");
			StringTagger tagger=new StringTagger(cmds);
			try {
				String tmp=tagger.Value("tracknr");
				int tracknr=Integer.parseInt(tmp);
				tmp=tagger.Value("id");
				int cdid=Integer.parseInt(tmp);
		    	debug("doRecord(): getTrack("+tracknr+" /data/audio/wav/"+cdid+".wav");	
		    	boolean result=impl.getTrack(tracknr,"/data/audio/wav/"+cdid+".wav");	
				if (result) {
		   		 	setValue("info","result=ok");	
				} else {
					debug("doRecord(): ERROR: result("+result+")");
					setValue("info","result=err reason=recordfailed");	
				}
			} catch(Exception e){
				debug("doRecord(): ERROR: Got exception: " + e);
			}
		} else {
			debug("doRecord(): ERROR: no implementation!");
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	void getConfig() {
		classname=(String)props.get("implementation");
		debug("getConfig(): loading("+classname+")");
		try {
			Class newclass=Class.forName(classname);
			impl = (cdplayerInterface)newclass.newInstance();
		} catch (Exception f) {
			debug("getConfig(): ERROR: Can't load class("+classname+")");
		}
	}

}
