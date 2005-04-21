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

/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 */
public class id extends ProcessorModule {

	Vector delayed;
	UsersInterface userinfo;
	areasInterface areas;
	Hashtable states=null;

	public void init() {
		userinfo=(UsersInterface)getModule("users");
		areas=(areasInterface)getModule("AREA");
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
	
	public id() {
	}

	public String setState(String userName,String name,String state) {
		if (states==null) getStoredStates();
		//return((String)states.put(name,state));
		return(null);
	}

	public String getState(String userName,String name) {
		if (states==null) getStoredStates();
		return(null);
	}

	public String getValue(String userName,String name) {
		if (states==null) getStoredStates();
		String state=(String)states.get(name);
		if (state!=null && (state.equals("save") || state.equals("savedirect"))) {
			return(userinfo.getServletProperty("servscan",userName,name,1));
		} else {
			return(userinfo.getServletProperty("servscan",userName,name,0));
		}
	}

	public String setValue(String userName,String name,String value) {
		if (states==null) getStoredStates();
		String state=(String)states.get(name);
		if (state!=null && (state.equals("save") || state.equals("savedirect"))) {
			userinfo.setServletProperty("servscan",userName,name,value,1);
		} else {
			userinfo.setServletProperty("servscan",userName,name,value,0);
		}
		return(null);
	}


	public Vector getList(scanpage sp, StringTagger tagger, String cmd) {
		String val,type,var;
		Hashtable tmp;

		HttpServletRequest req=sp.req;

		if (states==null) getStoredStates();
		if (cmd.charAt(0)=='"') cmd=cmd.substring(1,cmd.length()-1);
		if (cmd.equals("vars")) {
			Vector results=new Vector();
			tmp=userinfo.getServletProperties("servscan",req.getRemoteUser(),0);
			for (Enumeration e=tmp.keys();e.hasMoreElements();) {
				val = (String) e.nextElement();
				results.addElement(val);
				results.addElement((String)tmp.get(val));
				results.addElement("temp");
			}

			for (Enumeration e=states.keys();e.hasMoreElements();) {
				val = (String) e.nextElement();
				results.addElement(val);
				type = (String)states.get(val);
				if (type.equals("save") || type.equals("savedirect")) {
					var=userinfo.getServletProperty("servscan",req.getRemoteUser(),val,1);
					if (var!=null) {
						results.addElement(userinfo.getServletProperty("servscan",req.getRemoteUser(),val,1));
					} else {
						results.addElement("");
					}
				} else {
					results.addElement("");
				}
				results.addElement(type);
			}

			return(results);
		} else if (cmd.equals("types")) {
			Vector results=new Vector();
			results.addElement("temp");
			results.addElement("savedirect");
			results.addElement("save");
			results.addElement("multi");
			return(results);
		}
		return(null);
	}

	public boolean process(scanpage sp,Hashtable cmds,Hashtable vars) {
		
		HttpServletRequest req=sp.req;
		HttpPost poster=sp.poster;

		String cmd,key,var,type,oldtype;
		String userName=req.getRemoteUser();

		for (Enumeration e=cmds.keys();e.hasMoreElements();) {
			cmd=(String)e.nextElement();
			if (cmd.equals("CHANGEVAR")) {
				key=(String)cmds.get(cmd);
				var=(String)vars.get(key);
				type=(String)vars.get("TYPE");
				//System.out.println("key="+key+" var="+var+" type="+type);
				if (type!=null) {
					if (type.equals("save") || type.equals("savedirect")) {
						userinfo.delServletProperty("servscan",userName,key,0);
						userinfo.setServletProperty("servscan",userName,key,var,1);
					 	states.put(key,type);
					} else {
						if (states.containsKey(key)) states.remove(key);
						userinfo.delServletProperty("servscan",userName,key,1);
						userinfo.setServletProperty("servscan",userName,key,var,0);
					}
				} else {
					if (states.containsKey(key)) states.remove(key);
				}
			} else if (cmd.equals("DELVAR")) {
				key=(String)cmds.get(cmd);
				//values.remove(key);
			} else if (cmd.equals("NEWNAME")) {
				key=(String)cmds.get(cmd);
				var=(String)vars.get("NEWVAL");
				if (key!=null && !key.equals("") && var!=null) {
					userinfo.setServletProperty("servscan",userName,key,var,0);
				}
				states.put(key,"temp");
			}
		}
		saveIDstate();
		return(true);
	}

	void  saveIDstate() {
		String key,var;
		String line="";
		for (Enumeration e=states.keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			var=(String)states.get(key);
			if (!line.equals("")) line+=" ";
			line+=key+"="+var;
		}
		areas.setValue("ID_VARS_STATES",line);
		//System.out.println("ID->"+line);
	}


	void getStoredStates() {
		String key,val;

		states = new Hashtable();

		String statevars=areas.getValue("ID_VARS_STATES");
		if (statevars==null) return;
		StringTagger tagger = new StringTagger(statevars);
		for (Enumeration e=tagger.keys();e.hasMoreElements();) {
			key = (String) e.nextElement();
			val = tagger.Value(key);
			states.put(key,val);	
			//System.out.println("ID->"+key+"="+val);
			if (val.equals("save") || val.equals("savedirect")) {
			}
		}		
	}


	public void maintainance() {
		if (delayed.size()>0) {
			//System.out.println("id-> sync"+delayed.elementAt(0));
			delayed.removeElementAt(0);
		}
	}
}
