/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Log: not supported by cvs2svn $
*/
package org.mmbase.remote;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;


/**
 * @version $Revision: 1.6 $ $Date: 2000-11-22 14:22:25 $  
 * @author Daniel Ockeloen
 */
public class RemoteBuilder {

    public	static 	boolean debug       = true;
    private 		String  classname   = getClass().getName();
    private 		void 	debug( String msg ) { System.out.println( classname +":"+ msg ); }

	private MMProtocolDriver con;
	private Hashtable values=new Hashtable();
	private String buildername;
	private String nodename;
	public Hashtable props = null; 

	int lease=-1;

	public void init(MMProtocolDriver con,String servicefile) {
		this.con=con;
		debug("init("+con+","+servicefile+")");

		ExtendedProperties Reader=new ExtendedProperties();
		props = Reader.readProperties(servicefile);
		buildername=(String)props.get("buildername");
		nodename=(String)props.get("nodename");

		con.addListener(buildername,nodename,this);

		getNode();
	}

	public synchronized void getNode() {
		con.getNode(nodename,buildername);
		if (con.getProtocol().equals("multicast")) {
			try {
				wait(8000);
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
		if( debug ) debug("nodeRemoteChanged("+nodename+","+buildername+","+ctype+")");
	}

	public void nodeLocalChanged(String nodename,String buildername,String ctype) {		
		if( debug ) debug("nodeLocalChanged("+nodename+","+buildername+","+ctype+")");
	}



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

	public void commit() {
		con.commitNode(nodename,buildername,toXML());
	}


	public String toXML() {
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
		return(body);
	}


	public boolean maintainance() {
		if (lease!=-1 && getStringValue("state").equals("claimed")) {
			if (lease<1) {
				setValue("state","waiting");			
				setValue("info","");
				commit();
				System.out.println("C=0 released !");
			} else {
				System.out.println("C="+lease);
				lease--;
			}
		} else {
			lease=-1;
		}

		getNode();
		String state=getStringValue("state");
		// System.out.println("state ("+nodename+")="+state);
		// does the server think im down ?
		if (state.equals("down")) {
			setValue("state","waiting");
			commit();
		}
		return(true);		
	}


	public void setClaimed() {
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
