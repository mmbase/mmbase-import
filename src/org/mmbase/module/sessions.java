/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
 $Id: sessions.java,v 1.8 2000-04-21 11:14:55 wwwtech Exp $

 $Log: not supported by cvs2svn $
 Revision 1.7  2000/03/31 12:50:04  wwwtech
 Wilbert: Introduction of ParseException for method getList

 Revision 1.6  2000/03/30 13:11:26  wwwtech
 Rico: added license

 Revision 1.5  2000/03/29 10:05:00  wwwtech
 Rob: Licenses changed

 Revision 1.4  2000/03/24 11:16:15  wwwtech
 Rico: added test when saving the session values if the needed builders are loaded (properties,users)

 Revision 1.3  2000/03/09 16:21:29  wwwtech
 Rico: fixed bug in sessions, when it would try to get the hostname out of the request when used by calcPage when no request is available

 Revision 1.2  2000/02/24 13:28:54  wwwtech
 Rico: added addSetValue for adding of sets of values to a session set
 so you can post a set of values to a session variable, fixed several debug messages (added
 mostly)

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

/**
 *
 * @author Daniel Ockeloen
 *
 * @version $Id: sessions.java,v 1.8 2000-04-21 11:14:55 wwwtech Exp $
 */
public class sessions extends ProcessorModule implements sessionsInterface {

	private String classname = getClass().getName();
	private boolean debug=true;
	
	Hashtable sessions = new Hashtable();
    private MMBase mmbase;
	MMObjectBuilder props,users;

	public void init() {
    	mmbase=(MMBase)getModule("MMBASEROOT");
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}
	
	public sessionInfo getSession(scanpage sp,String wanted) {
		if (sessions!=null && wanted!=null) {
			sessionInfo session=(sessionInfo)sessions.get(wanted);
			if (session==null) {
				if (sp.req!=null) {
					session=new sessionInfo(sp.req.getRemoteHost(),wanted);
				} else {
					session=new sessionInfo("<unknown>",wanted);
				}
				sessions.put(wanted,session);
				// get all the propertie values of this node

				if (mmbase!=null) {
					try {
					loadProperties(session);
					} catch(Exception r) {}
					// set cookie check var
					// setVisitInfo(session);
				}

			} else {
				session.setValue("COOKIETEST","YES");
				//setVisitInfo(session);
			}
			return(session);	
		} 
		return(null);
	}

	public void forgetSession(String wanted) {
		if (sessions!=null) { 
			if(wanted!=null) {
				if (sessions.containsKey(wanted)) {
					sessions.remove(wanted);
					debug("forgetSession("+wanted+"): Who? Don't know 'm .. sorry!");
				} else debug("forgetSession("+wanted+"): WARNING: This key not found in session!");
			} else debug("forgetSession("+wanted+"): ERROR: wanted to forget a null!");
		} else debug("forgetSession("+wanted+"): ERROR: sessions is null!");
	}
	
	public String getValue(sessionInfo session,String wanted) {
		if (session!=null) {
			return(session.getValue(wanted));
		} else {
			debug("getValue("+wanted+"): ERROR: session is null!");
			return(null);
		}
	}

	public String setValue(sessionInfo session,String key,String value) {
		if (session!=null) {
			return(session.setValue(key,value));
		} else {
			debug("setValue("+key+","+value+"): ERROR: sessions is null!");
			return(null);
		}
	}

	public void addSetValues(sessionInfo session,String key,Vector values) {
		if (session!=null) {
			String str;
			for (Enumeration e=values.elements();e.hasMoreElements();) {
				str=(String)e.nextElement();
				session.addSetValue(key,str);
			}
		}
		else
			debug("addSetValues("+key+","+values+"): ERROR: sessions is null!");
	}

	public void addSetValue(sessionInfo session,String key,String value) {
		if (session!=null) {
			session.addSetValue(key,value);
		} else {
			debug("addSetValue("+key+","+value+"): ERROR: sessions is null!");
		}
	}
	
	public void setValueFromNode( sessionInfo session, MMObjectNode node ) {
		String key = node.getStringValue("key"); 
		String ptype = node.getStringValue("ptype");
		String value = node.getStringValue("value");
		if (ptype.equals("string")) {
			setValue(session, key, value);
		} else if (ptype.equals("vector")) {
			StringTokenizer tok = new StringTokenizer(value,",\n\r");
			while (tok.hasMoreTokens()) {
				addSetValue( session, key, tok.nextToken());	
			}
		} else {
			debug("setValueFromNode("+key+","+ptype+","+value+"): ERROR: ptype("+ptype+") neither 'string' nor 'vector'!");
		}
	}

	public void loadProperties(sessionInfo session) {	
		try {
			String sid;
			if (session!=null) {
				sid=session.getCookie();
				if (mmbase!=null) {
					props=mmbase.getMMObject("properties");
					Enumeration res=props.search("WHERE key='SID' AND value='"+sid+"'");
					if( debug ) debug("loadProperties(): got SID("+sid+")"); // WHERE key='SID' AND value='"+sid+"'");
					if (res.hasMoreElements()) {
						MMObjectNode snode = (MMObjectNode)res.nextElement();
						int id=snode.getIntValue("parent");
						setValue(session,"USERNUMBER",""+id);
						res=props.search("WHERE parent='"+id+"'");
						while (res.hasMoreElements()) {
							setValueFromNode( session, (MMObjectNode)res.nextElement() );
						}
					}
          		} else debug("loadProperties(): mmbase is null!");
            } else debug("loadProperties(): session is null!");
		} catch (Exception e) {
			//	e.printStackTrace();
		}
	}

	public String saveValue(sessionInfo session,String key) 
	{
		if (mmbase==null) 
		{
			debug("saveValue("+session+","+key+"): ERROR: mmbase is null!");
			return(null);
		}

		if (session!=null) 
		{
			if( key != null )
			{
				int id=-1;
				String sid=session.getCookie();
				MMObjectNode node=session.getNode();

				if (node==null) {

					// node not found
					// --------------

					props=mmbase.getMMObject("properties");
					users=mmbase.getMMObject("users");


					// does the sid have any properties?
					if (props==null || users==null) {
						debug("Can't Save: One of the needed builders is not loaded either users or properties");
					} else {
						Enumeration res=props.search("WHERE key='SID' AND value='"+sid+"'");
						if (res.hasMoreElements()) {
	
							// yes, is it a user?
							// ------------------
	
							// get the parent for this ID value
							MMObjectNode snode = (MMObjectNode)res.nextElement();
							id=snode.getIntValue("parent");
							node=users.getNode(id);
							if( node != null )
							{
								session.setNode(node);
							}
							else
							{
								debug("saveValue("+key+"): WARNING: node("+id+") for user("+sid+") not found in usersdb, maybe long-time-no-see and forgotten?!");
							}
	
						} else {
							// ----------------------------------------------------------------------
							// Server has given a cookie, but *now* we create a new user & properties
							// ----------------------------------------------------------------------
	
							debug("saveValue("+key+"): This is a new user("+sid+"), making database entry..");
		
							node = users.getNewNode ("system");
							if( node != null ) 
							{
								node.setValue ("description","created for SID = "+sid);
								id = users.insert ("system", node); 
								node.setValue("number",id);
		
								// hier
								// ----
								MMObjectNode snode = props.getNewNode ("system");
								snode.setValue ("parent",id);
								snode.setValue ("ptype","string");
								snode.setValue ("key","SID");
								snode.setValue ("value",sid);
								props.insert("system", snode); 
								session.setNode(node);
							}
							else
								debug("saveValue("+session+","+key+"): ERROR: No node("+id+") could be created for this user("+sid+")!");
						}
					}
				} else {
					id=node.getIntValue("number");	
				}

				// set value in the users node and save in database
				// ------------------------------------------------

				if (node!=null) {
					MMObjectNode pnode=node.getProperty(key);
					if (pnode!=null) {
						String value=session.getSetString(key);
						if (value==null) {
							value=session.getValue(key);
						}
						pnode.setValue("value",value);
						pnode.commit();
					} else {
						MMObjectNode snode = props.getNewNode ("system");
						String value=session.getSetString(key);
						if (value==null) {
							value=session.getValue(key);
							if( value==null )
							{
								debug("saveValue("+key+"): value("+value+") is null!");
								debug(" - values(" + session.values +")" );
								debug(" - setvalues(" + session.setvalues +")" );
							}
							snode.setValue ("ptype","string");
						} else {
							snode.setValue ("ptype","vector");
						}
						snode.setValue ("parent",id);
						snode.setValue ("key",key);
						snode.setValue ("value",value);
						int id2=props.insert("system", snode); 
						snode.setValue("number",id2);
						node.putProperty(snode);
					}

					return(null);
				}
				else
				{
					debug("saveValue("+session+","+key+"): ERROR: no node("+node+") found for user("+sid+")!");
					return(null);
				}
			} else {
				debug("saveValue("+session+","+key+"): ERROR: key is null!");
				return(null);
			}
		} else {
			debug("saveValue("+session+","+key+"): ERROR: session is null!");
			return(null);
		}
	}

	/**
	 * SimpleModule
	 */
	public sessions() {
	}

	public Vector getList(scanpage sp, StringTagger tagger, String cmd) throws ParseException {
		String val;
		sessionInfo tmps;

		if (cmd.charAt(0)=='"') cmd=cmd.substring(1,cmd.length()-1);
		if (cmd.equals("sessions")) {
			Vector results = new Vector();
			for (Enumeration e=sessions.keys();e.hasMoreElements();) {
				val = (String)e.nextElement();
				results.addElement(val);	
				tmps=(sessionInfo)sessions.get(val);
				results.addElement(tmps.getHostName());	
			}
			return(results);
		}
		if (cmd.equals("SESSION")) {
			Vector results=new Vector();
			String key;
			sessionInfo session=getSession(sp,sp.sname);
			for (Enumeration e=session.values.keys();e.hasMoreElements();) {
				key=(String)e.nextElement();
				results.addElement("VAR");
				results.addElement(key);
				results.addElement(session.getValue(key));
			}
			for (Enumeration e=session.setvalues.keys();e.hasMoreElements();) {
				key=(String)e.nextElement();
				results.addElement("SET");
				results.addElement(key);
				results.addElement(session.getSetString(key));
			}
			return(results);
		}

    	String line = Strip.DoubleQuote(cmd,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd2=tok.nextToken();	
			if (cmd2.equals("GETSET")) return(doGetSet(sp,tok));
		}
		return(null);
	}


	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			
			if (cmd.equals("CLEARSET")) 	return(doClearSet(sp,tok));
			if (cmd.equals("ADDSET")) 		return(doAddSet(sp,tok));
			if (cmd.equals("PUTSET")) 		return(doPutSet(sp,tok));
			if (cmd.equals("DELSET")) 		return(doDelSet(sp,tok));
			if (cmd.equals("CONTAINSSET")) 	return(getContainsSet(sp,tok));
			if (cmd.equals("SETSTRING")) 	return(getSetString(sp,tok));
			if (cmd.equals("SETCOUNT")) 	return(getSetCount(sp,tok));
			if (cmd.equals("AVGSET")) 		return(getAvgSet(sp,tok));
			
			debug("replace("+cmds+"): WARNING: Unknown command("+cmd+")!");
		}
		return( classname +"replace(): ERROR: No command defined");
	}

	public String doAddSet(scanpage sp, StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
				if (tok.hasMoreTokens()) {
					String value=tok.nextToken();
					session.addSetValue(key,value);
				}
			}
		}
		return("");
	} 


	public String doPutSet(scanpage sp , StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
				if (tok.hasMoreTokens()) {
					String value=tok.nextToken();
					session.putSetValue(key,value);
				}
			}
		}
		return("");
	} 

	public String doClearSet(scanpage sp, StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
					session.clearSet(key);
			}
		}
		return("");
	} 


	public String getAvgSet(scanpage sp,StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
				return(session.getAvgSet(key));
			}
		}
		return("");
	} 

	public String doDelSet(scanpage sp, StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
				if (tok.hasMoreTokens()) {
					String value=tok.nextToken();
					session.delSetValue(key,value);
				}
			}
		}
		return("");
	} 


	public String getContainsSet(scanpage sp, StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
				if (tok.hasMoreTokens()) {
					String value=tok.nextToken();
					return(session.containsSetValue(key,value));
				} 
			}
		} 
		return("NO");
	} 

	public Vector doGetSet(scanpage sp, StringTokenizer tok) {
		Vector results=new Vector();
		String line=getSetString(sp,tok);
		debug("doGetSet(): SESSION LINE="+line);
		if (line!=null) {
			StringTokenizer tok2 = new StringTokenizer(line,",\n\r");
			while (tok2.hasMoreTokens()) {
				results.addElement(tok2.nextToken());
			}
		}
		return(results);	
	}


	public String getSetString(scanpage sp, StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
				String tmp=session.getSetString(key);
				if (tmp!=null) return(tmp);
			}
		} 
		return("");
	} 

	public String getSetCount(scanpage sp, StringTokenizer tok) {
		sessionInfo session=getSession(sp,sp.sname);
		if (session!=null) {
			if (tok.hasMoreTokens()) {
				String key=tok.nextToken();
				String tmp=session.getSetCount(key);
				if (tmp!=null) return(tmp);
			}
		} 
		return("0");
	} 


	public int getSize() {
		return(sessions.size());
	}


	 public Hashtable state() {
		state.put("Sessions",""+getSize());
		return(state);
	 }


	void setVisitInfo(sessionInfo session) {
		String counter=session.getValue("SESSIONCOUNT");
		if (counter==null) {
			session.setValue("SESSIONCOUNT","1");
		} else {
		try {
			int icounter=Integer.parseInt(counter);
			session.setValue("SESSIONCOUNT",""+(icounter+1));
		} catch (Exception e) {
			session.setValue("SESSIONCOUNT","1");
		}
		int time=(int)(System.currentTimeMillis()/1000);
		int day=(time/(3600*24));
		String hday=session.getValue("LASTVISIT");
		if (hday==null || !hday.equals(""+day)) {
			session.setValue("LASTVISIT",""+day);
			saveValue(session,"LASTVISIT");
			String visits=session.getValue("VISITCOUNT");
			if (visits==null || visits.equals("")) {
				session.setValue("FIRSTVISIT",""+day);
				saveValue(session,"FIRSTVISIT");
				session.setValue("VISITCOUNT","1");
				saveValue(session,"VISITCOUNT");
			} else {
				try {
					int val=Integer.parseInt(visits);
					session.setValue("VISITCOUNT",""+(val+1));
					saveValue(session,"VISITCOUNT");
				} catch(Exception e) {
			}
			}
		}
		}
	}

	private void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}
		
}
