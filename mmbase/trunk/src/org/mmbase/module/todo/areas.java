/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.servlet.http.*;

import org.mmbase.util.*;
//import vpro.james.coremodules.users.*;

/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 */
public class areas extends ProcessorModule implements areasInterface {

	Hashtable values;
	Hashtable states;
	Vector delayed;
	UsersInterface userinfo;

	public void init() {
		userinfo=(UsersInterface)getModule("users");
 		values = null;	
 		states = null;	
 		delayed = new Vector();	
	}

	public void reload() {
		init();
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}
	

	public areas() {
	}

	void addNonTemps() {
		String key,val;

		userinfo.addUser("other");	
		values = new Hashtable();
		states = new Hashtable();
		String statevars=userinfo.getServletProperty("servscan","other","AREA_VARS_STATES",1);
		if (statevars==null) return;
		StringTagger tagger = new StringTagger(statevars);
		for (Enumeration e=tagger.keys();e.hasMoreElements();) {
			key = (String) e.nextElement();
			val = tagger.Value(key);
			states.put(key,val);	
			if (val.equals("save") || val.equals("savedirect")) {
				val=userinfo.getServletProperty("servscan","other","AREA_VAR_"+key,1);
				if (key!=null && val!=null) {
					values.put(key,val);
				}
			}
		}		
	}

	public String setState(String name,String state) {
		if (states==null) addNonTemps();
		return((String)states.put(name,state));
	}

	public String getState(String name) {
		if (states==null) addNonTemps();
		return((String)states.get(name));
	}

	public String getValue(String name) {
		if (states==null) addNonTemps();
		String val=(String)values.get(name);
		return(val);
	}

	public String setValue(String name,String value) {
		if (states==null) addNonTemps();
		String state=(String)states.get(name);
		if (state!=null) {
			if (state.equals("save") && !delayed.contains(name)) delayed.addElement(name);
			if (state.equals("savedirect")) {
				userinfo.setServletProperty("servscan","other","AREA_VAR_"+name,value,1);
			}
		}
		return((String)values.put(name,value));
	}


	public Vector getList(HttpServletRequest req, StringTagger tagger, String cmd) {
		String val,type;

		if (states==null) addNonTemps();
		if (cmd.charAt(0)=='"') cmd=cmd.substring(1,cmd.length()-1);
		if (cmd.equals("vars")) {
			Vector results=new Vector();
			for (Enumeration e=values.keys();e.hasMoreElements();) {
				val = (String) e.nextElement();
				results.addElement(val);
				results.addElement((String)values.get(val));
				type = (String)states.get(val);
				if (type==null) {
					results.addElement("temp");
				} else {
					results.addElement(type);
				}
			}
			tagger.setValue("ITEMS","3");
			return(results);
		} else if (cmd.equals("types")) {
			Vector results=new Vector();
			results.addElement("temp");
			results.addElement("savedirect");
			results.addElement("save");
			results.addElement("multi");
			return(results);
		} else if (cmd.equals("areas")) {
			Vector results=new Vector();
			results.addElement("www");
			results.addElement("film");
			return(results);
		}
		return(null);
	}

	public boolean process(HttpServletRequest req, Hashtable cmds,Hashtable vars,HttpPost poster) {
		String cmd,key,var,state;

		for (Enumeration e=cmds.keys();e.hasMoreElements();) {
			cmd=(String)e.nextElement();
			if (cmd.equals("CHANGEVAR")) {
				key=(String)cmds.get(cmd);
				var=(String)vars.get(key);
				values.put(key,var);
				state=(String)vars.get("TYPE");
				if (state!=null) {
					if (state.equals("temp")) {
					 	if (states.contains(key)) states.remove(key);
					} else {
					 		states.put(key,state);
							if (state.equals("save") && !delayed.contains(key)) delayed.addElement(key);
							if (state.equals("savedirect")) {
								userinfo.setServletProperty("servscan","other","AREA_VAR_"+key,var,1);
							}
					}
				}
			} else if (cmd.equals("DELVAR")) {
				key=(String)cmds.get(cmd);
				values.remove(key);
			} else if (cmd.equals("NEWNAME")) {
				key=(String)cmds.get(cmd);
				var=(String)vars.get("NEWVAL");
				values.put(key,var);
				//syncToStore(
			}
		}
		saveAreaState();
		return(true);
	}

	void saveAreaState() {
		String key,var;
		String line="";
		for (Enumeration e=states.keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			var=(String)states.get(key);
			if (!line.equals("")) line+=" ";
			line+=key+"="+var;
		}
		System.out.println("Area Setting property "+line);
		System.out.println("Area res "+userinfo.setServletProperty("servscan","other","AREA_VARS_STATES",line,1));
	}

	public void maintainance() {
		String val;

		while (delayed.size()>0) {
			//System.out.println("areas-> sync"+delayed.elementAt(0));
			val=(String)delayed.elementAt(0);
			userinfo.setServletProperty("servscan","other","AREA_VAR_"+val,(String)values.get(val),1);
			delayed.removeElementAt(0);
		}
	}
}
