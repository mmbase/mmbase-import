package org.mmbase.remote.builders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.remote.*;
import org.mmbase.service.interfaces.*;
import org.mmbase.service.implementations.*;


/**
 * @version $Revision: 1.3 $ $Date: 2000-03-29 09:48:10 $ 
 * @author Daniel Ockeloen
 */
public class dropboxes extends RemoteBuilder {

	private String _classname = getClass().getName();
	private boolean debug = RemoteBuilder.debug;
	private void debug( String msg ) { System.out.println( _classname +":"+ msg ); }

	private dropboxInterface impl;
	private String classname;
	StringTagger tagger;

	public void init(MMProtocolDriver con,String servicefile) {
		super.init(con,servicefile);
		if( debug ) debug("init("+con+","+servicefile+")");

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
		if(debug) debug("nodeRemoteChanged("+nodenr+","+buildername+","+ctype+")");
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeLocalChanged(String nodenr,String buildername,String ctype) {
		if(debug) debug("nodeLocalChanged("+nodenr+","+buildername+","+ctype+")");
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeChanged(String nodenr,String buildername,String ctype) {
		if(debug) debug("nodeChanged("+nodenr+","+buildername+","+ctype+")");
		// get the node
		getNode();
				
		String state=getStringValue("state");
		if(debug) debug("nodeChanged("+nodenr+","+buildername+","+ctype+"): got state("+state+")");
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

	void getConfig() {
		classname=(String)props.get("implementation");
		if( debug ) debug("getConfig(): impl("+classname+")");
		try {
			Class newclass=Class.forName(classname);
			impl = (dropboxInterface)newclass.newInstance();
			String tmp=(String)props.get("directory");
			impl.setDir(tmp);
			tmp=(String)props.get("command");
			impl.setCmd(tmp);
			tmp=(String)props.get("wwwpath");
			impl.setWWWPath(tmp);
		} catch (Exception f) {
			debug("getConfig(): ERROR: Can't load class("+classname+")");
		}
	}

}
