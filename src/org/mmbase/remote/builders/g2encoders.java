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
public class g2encoders extends RemoteBuilder {

	private g2encoderInterface impl;
	private String classname;
	StringTagger tagger;

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

	public void nodeChanged(String nodenr,String buildername,String ctype) {		
		// get the node
		getNode();
				
		String state=getStringValue("state");
		System.out.println("STATE="+state);
		if (state.equals("version")) {
			doVersion();
		} else if (state.equals("encode")) {
			doEncode();
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
		System.out.println("COOOOMMMIT done");
	}

	private void doEncode() {
		setValue("state","busy");
		commit();
	
		// set the version we got from the encoder	

		if (impl!=null) {
			String cmds=getStringValue("info");
		    setValue("info",impl.doEncode(cmds));	
		} else {
			setValue("info","result=err reason=nocode");	
		}

		// signal that we are done
		setValue("state","waiting");
		commit();
	}

	void getConfig() {
		classname=(String)props.get("implementation");
		System.out.println("impl="+classname);
		try {
			Class newclass=Class.forName(classname);
			impl = (g2encoderInterface)newclass.newInstance();
		} catch (Exception f) {
			System.out.println("g2encoder -> Can't load class : "+classname);
		}
	}

}
