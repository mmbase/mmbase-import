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
import org.mmbase.module.core.*;

/**
 *
 * @author Daniel Ockeloen
 */
public class sessions extends ProcessorModule implements sessionsInterface {

	private String classname = getClass().getName();
	
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
				session=new sessionInfo(sp.req.getRemoteHost(),wanted);
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
		if (sessions!=null && wanted!=null) {
			if (sessions.containsKey(wanted)) {
				sessions.remove(wanted);
				System.out.println("Sessions -> Forget user :"+wanted);
			}
		}
	}
	
	public String getValue(sessionInfo session,String wanted) {
		if (session!=null) {
			return(session.getValue(wanted));
		} else {
			return(null);
		}
	}

	public String setValue(sessionInfo session,String key,String value) {
		if (session!=null) {
			return(session.setValue(key,value));
		} else {
			return(null);
		}
	}


	public void addSetValue(sessionInfo session,String key,String value) {
		if (session!=null) {
			session.addSetValue(key,value);
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
		}
	}

	public void loadProperties(sessionInfo session) {	
		try {
		if (session!=null) {
			if (mmbase!=null) {
				props=mmbase.getMMObject("properties");
				String sid=session.getCookie();
				Enumeration res=props.search("WHERE key='SID' AND value='"+sid+"'");
				System.out.println("GETPROP -> WHERE key='SID' AND value='"+sid+"'");
				if (res.hasMoreElements()) {
					MMObjectNode snode = (MMObjectNode)res.nextElement();
					int id=snode.getIntValue("parent");
					setValue(session,"USERNUMBER",""+id);
					res=props.search("WHERE parent='"+id+"'");
					while (res.hasMoreElements()) {
						setValueFromNode( session, (MMObjectNode)res.nextElement() );
					}
				}
			}
		}
		} catch (Exception e) {
		//	e.printStackTrace();
		}
	}

	public String saveValue(sessionInfo session,String key) {
		if (session!=null) {
			int id=-1;
			String sid=session.getCookie();
			MMObjectNode node=session.getNode();
			if (node==null) {
				if (mmbase==null) return(null);
				props=mmbase.getMMObject("properties");
				users=mmbase.getMMObject("users");


				Enumeration res=props.search("WHERE key='SID' AND value='"+sid+"'");
				if (res.hasMoreElements()) {
					// get the parent for this ID value
					MMObjectNode snode = (MMObjectNode)res.nextElement();
					id=snode.getIntValue("parent");
					node=users.getNode(id);
					session.setNode(node);
				} else {
					// so no SID found so no parent lets create them both
					System.out.println("sessions-> WANNE CREATE A NEW USER");

					node = users.getNewNode ("system");
					node.setValue ("description","created for SID = "+sid);
					id = users.insert ("system", node); 
					node.setValue("number",id);
					// hier
					MMObjectNode snode = props.getNewNode ("system");
					snode.setValue ("parent",id);
					snode.setValue ("ptype","string");
					snode.setValue ("key","SID");
					snode.setValue ("value",sid);
					props.insert("system", snode); 
					session.setNode(node);
				}
			} else {
				id=node.getIntValue("number");	
			}
			// set value in the users node and save in database
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
			}
			return(null);
		} else {
			return(null);
		}
	}

	/**
	 * SimpleModule
	 */
	public sessions() {
	}

	public Vector getList(scanpage sp, StringTagger tagger, String cmd) {
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
			if (cmd.equals("CLEARSET")) return(doClearSet(sp,tok));
			if (cmd.equals("ADDSET")) return(doAddSet(sp,tok));
			if (cmd.equals("PUTSET")) return(doPutSet(sp,tok));
			if (cmd.equals("DELSET")) return(doDelSet(sp,tok));
			if (cmd.equals("CONTAINSSET")) return(getContainsSet(sp,tok));
			if (cmd.equals("SETSTRING")) return(getSetString(sp,tok));
			if (cmd.equals("AVGSET")) return(getAvgSet(sp,tok));
		}
		return("No command defined");
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
		System.out.println("SESSION LINE="+line);
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
