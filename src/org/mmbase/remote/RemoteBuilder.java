/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Log: not supported by cvs2svn $
Revision 1.9  2000/11/29 13:29:03  vpro
davzev: Changed debug info

Revision 1.8  2000/11/28 16:44:38  vpro
davzev: Added some method comments and debug to figure out what goes on...

Revision 1.7  2000/11/27 12:33:11  vpro
davzev: Changed debug and classname var to public

Revision 1.6  2000/11/22 14:22:25  vpro
davzev: Added cvs logging comments.

*/
package org.mmbase.remote;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;


/**
 * @version $Revision: 1.10 $ $Date: 2000-12-20 16:31:45 $  
 * @author Daniel Ockeloen
 */
public class RemoteBuilder {

    public boolean debug = true;
    public String classname = getClass().getName();
    public void debug(String msg) {System.out.println(classname +":"+ msg);}

	private MMProtocolDriver con;
	private Hashtable values=new Hashtable();
	private String buildername;
	private String nodename;
	public Hashtable props = null; 

	int lease=-1;


	public void init(MMProtocolDriver con,String servicefile) {
		this.con=con;
		if (debug) debug("init("+con+","+servicefile+"): Reading props from servicefile in props hashtable.");
		ExtendedProperties Reader=new ExtendedProperties();
		props = Reader.readProperties(servicefile);
		buildername=(String)props.get("buildername");
		nodename=(String)props.get("nodename");

		
		if (debug) debug("init("+con+","+servicefile+"): Calling con.addListener("+buildername+","+nodename+","+this+")");
		con.addListener(buildername,nodename,this);
		if (debug) debug("init("+con+","+servicefile+"): Calling getNode()");
		getNode();
	}

	/**
	 * Calls con.getNode with nodename and buildername, and waits 8 seconds
	 */
	public synchronized void getNode() {
		int MULTICASTWAIT=8000;
		debug("getNode(): Calling con.getNode("+nodename+","+buildername+")");
		con.getNode(nodename,buildername);
		if (con.getProtocol().equals("multicast")) {
			try {
				wait(MULTICASTWAIT);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when a remote node changes.
	 * The following notify node changed types are are possible:
	 * d: node deleted
	 * c: node changed
	 * n: new node
	 * f: node field changed
	 * r: node relation changed
	 * x: some xml notify?
	 * 
	 * @param nodename
	 * @param buildername
	 * @ctype the node changetype.
	 */
	public void nodeRemoteChanged(String nodename,String buildername,String ctype) {		
		if( debug ) debug("nodeRemoteChanged("+nodename+","+buildername+","+ctype+"): Do nothing");
	}

	public void nodeLocalChanged(String nodename,String buildername,String ctype) {		
		if( debug ) debug("nodeLocalChanged("+nodename+","+buildername+","+ctype+"): Do noting");
	}

	/**
	 * XML Parses the body parameter and saving the xml info as a hashtable as key = value.
	 * @param body a String with information in xml format.
	 */
	public synchronized void gotXMLValues(String body) {
		StringTokenizer tok = new StringTokenizer(body,"\n\r");
		String xmlline=tok.nextToken();
		String docline=tok.nextToken();
	
		String builderline=tok.nextToken();
		String endtoken="</"+builderline.substring(1);

		String nodedata=body.substring(body.indexOf(builderline)+builderline.length());
		nodedata=nodedata.substring(0,nodedata.indexOf(endtoken));

		int bpos=nodedata.indexOf("<");
		while (bpos!=-1) {
			String key=nodedata.substring(bpos+1);
			key=key.substring(0,key.indexOf(">"));
			String begintoken="<"+key+">";
			endtoken="</"+key+">";
			
			String value=nodedata.substring(nodedata.indexOf(begintoken)+begintoken.length());
			value=value.substring(0,value.indexOf(endtoken));

			if (debug) debug("gotXMLValues: Storing field in hashtable as key:"+key+", value:"+value);
			values.put(key,value);

			nodedata=nodedata.substring(nodedata.indexOf(endtoken)+endtoken.length());
			bpos=nodedata.indexOf("<");
		}
		notify();
	}
	
	public String getStringValue(String key) {
		return((String)values.get(key));
	}

	public int getIntValue(String key) {
		try {
			return(Integer.parseInt(getStringValue(key)));
		} catch(Exception e) {
			return(-1);
		}
	}

	public void setValue(String key, String value) {
		values.put(key,value);
	}

	public void setValue(String key, int value) {
		values.put(key,""+value);
	}

	/**
	 * Gets the node in XML value Calls con.commitNode with it. 
	 */
	public void commit() {
		if (debug) debug("commit(): Calling con.commitNode("+nodename+","+buildername+","+toXML());
		con.commitNode(nodename,buildername,toXML());
	}

	/**
	 * Gets the node contents from hashtable and return it in XML format.
	 * @return a String with XML contents.
	 */
	public String toXML() {
		if (debug) debug("toXML(): Converting node(=hashtable) to XML.");
		String body="<?xml version=\"1.0\"?>\n";
		// body+="<!DOCTYPE mmnode."+buildername+" SYSTEM \"http://openbox.vpro.nl/mmnode/"+buildername+".dtd\">\n";
		body+="<!DOCTYPE mmnode."+buildername+" SYSTEM \"http://openbox.vpro.nl/mmnode/"+buildername+".dtd\">\n";
		body+="<"+buildername+">\n";
		for (Enumeration e=values.keys();e.hasMoreElements();) {
			String key=(String)e.nextElement();	
				String value=getStringValue(key);
				body+="<"+key+">"+value+"</"+key+">\n";
		}
		body+="</"+buildername+">\n";
		if (debug) debug("toXML(): Returning body: "+body);
		return(body);
	}

	public boolean maintainance() {
		if (debug) debug("maintenance: lease="+lease+", state="+getStringValue("state"));
		if (lease!=-1 && getStringValue("state").equals("claimed")) {
			if (lease<1) {
				if (debug) debug("maintenance: Setting state to waiting and emptying info field.");
				setValue("state","waiting");			
				setValue("info","");
				commit();
				System.out.println("C=0 released !");
			} else {
				if (debug) debug("maintenance: lease="+lease+", decrementing lease value");
				System.out.println("C="+lease);
				lease--;
			}
		} else {
			if (debug) debug("maintenance: Setting lease to -1");
			lease=-1;
		}

		if (debug) debug("maintenance: Calling getNode() and checking state");
		getNode();
		String state=getStringValue("state");
		// System.out.println("state ("+nodename+")="+state);
		// does the server think im down ?
		if (state.equals("down")) {
			if (debug) debug("maintenance: State is down, resetting to waiting");
			setValue("state","waiting");
			commit();
		}
		return(true);		
	}

	public void setClaimed() {
		if (debug) debug("maintenance: Getting lease value from info field");
		String cmds=getStringValue("info");
		StringTagger tagger=new StringTagger(cmds);
		String tmp=tagger.Value("lease");
		if (tmp!=null) {
			try {
				lease=Integer.parseInt(tmp);	
			} catch(Exception e) {}
		}
	}
}
