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

	private cdplayerInterface impl;
	private String classname;
	StringTagger tagger;

	public void init(MMProtocolDriver con,String servicefile) {
		super.init(con,servicefile);
		// the node was loaded allready so check what the state was
		// and put us in ready/waiting state
		String state=getStringValue("state");
		getConfig();
		System.out.println("STATE="+state);
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
		} else if (state.equals("record")) {
			doRecord();
		} else if (state.equals("getdir")) {
			doGetDir();
		} else if (state.equals("restart")) {
			doRestart();
		} else if (state.equals("version")) {
			doVersion();
		} else if (state.equals("claimed")) {
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
		    	System.out.println("getTrack("+tracknr+" /data/audio/wav/"+cdid+".wav");	
		    	boolean result=impl.getTrack(tracknr,"/data/audio/wav/"+cdid+".wav");	
				if (result) {
		   		 	setValue("info","result=ok");	
				} else {
					setValue("info","result=err reason=recordfailed");	
				}
			} catch(Exception e) {}
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
			impl = (cdplayerInterface)newclass.newInstance();
		} catch (Exception f) {
			System.out.println("cdplayer -> Can't load class : "+classname);
		}
	}

}
