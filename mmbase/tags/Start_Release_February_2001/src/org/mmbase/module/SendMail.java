/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.util.*;

/**
 * This module gives mail functionality 
 *
 * @author Rob Vermeulen
 * @version $Revision: 1.6 $ $Date: 2001-02-05 15:02:44 $
 */
public class SendMail extends Module implements SendMailInterface {
	private String 		 classname 	= getClass().getName();
	private boolean		 debug 		= false;
   	private DataInputStream  in		= null;
   	private DataOutputStream out		= null;
	private Socket 		 connect	= null;
	private String		 mailhost 	= "";

	private void		 debug(String msg){ System.out.println(classname+":"+msg); }

	public void reload() {
        	mailhost=getInitParameter("mailhost");
	}

	public void unload() {
	}
	
	public void onload() {
	}
	
	public void shutdown() {
	}
	
	public void init() {
        	mailhost=getInitParameter("mailhost");
		if( debug ) debug("init(): SendMail -> mailhost "+mailhost);
	}

	/** 
	 * Connect to the mailhost
	 */
	private boolean connect(String host, int port) { 
		String result="";

        try {
            connect=new Socket(host,port);
		} catch (Exception e) { 
			debug("connect("+host+","+port+"): ERROR: while connecting: "+e);
			return false;
		}
        try {
           out=new DataOutputStream(connect.getOutputStream());
        } catch (IOException e) { 	
			debug("connect("+host+","+port+"): ERROR: while getting outputstream: "+e);
			return false;}
        try {
           in=new DataInputStream(connect.getInputStream());
        } catch (IOException e) { 
			debug("connect("+host+","+port+"): ERROR: while getting inputstream: "+e);
			return false;}
		try {
        		result = in.readLine();
		} catch (Exception e) { 
			debug("connect("+host+","+port+"): ERROR: while reading response:"+e);
			return false;
		}
		/** Is anwser 220 **/
		if(result.indexOf("220")!=0)  return false; 
		return true;
	}

	/** 
  	 * Send mail
	 */	
	public synchronized boolean sendMail(String from, String to, String data) {
		String anwser="";

		/** Connect to mail-host **/	
		if (!connect(mailhost,25)) {
			debug("sendMail(): ERROR: from("+from+"), to("+to+"), data("+data+"): Cannot connect to mailhost("+mailhost+")!");
			return false;
		}

	 	try {
			out.writeBytes("MAIL FROM:<"+from+">\r\n");
			out.flush();
            anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("250")!=0)  return false;

			StringTokenizer tok = new StringTokenizer(to,",\n\r");
			while (tok.hasMoreTokens()) {
				String tmp=tok.nextToken();
				out.writeBytes("RCPT TO:<"+tmp+">\r\n");
			}
			out.flush();
            		anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("250")!=0)  return false;
			
			out.writeBytes("DATA\r\n");
			out.flush();
            anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("354")!=0)  return false;
				
			out.writeBytes(data+"\r\n");
			out.writeBytes("\r\n.\r\n");
			out.flush();
            anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("250")!=0)  return false;
			
			out.writeBytes("QUIT\r\n");
			out.flush();
			in.close();
        }   catch (Exception e) {
            debug("sendMail("+from+","+to+","+data.length()+"): ERROR: "+e);
            return false;
        }
		return true;
	}

	/** 
	 * Send mail with headers 
	 */
	public boolean sendMail(String from, String to, String data, Hashtable headers) {
		String header="";
		String temp="";
   	
		for (Enumeration t=headers.keys();t.hasMoreElements();) {
        	header=(String)t.nextElement();
			temp+=header+": ";
			temp+=headers.get(header)+"\r\n";
      	}	
		temp+="\r\n\r\n";
		data=temp+data;
		return sendMail(from,to,data);
	}

	/**
	 * Send mail
	 */	
	public boolean sendMail(Mail mail) {
		return sendMail(mail.from, mail.to, mail.text, mail.headers);
	}

	/**
	 * checks the e-mail address
	 */ 
	public String verify(String name) {
	  String anwser="";
 
        /** Connect to mail-host **/
        if (!connect(mailhost,25)) return "Error";
 
        try {
            out.writeBytes("VRFY "+name+"\r\n");
            out.flush();
            anwser = in.readLine();
            if(anwser.indexOf("250")!=0)  return "Error";
 
        }   catch (Exception e) {
            debug("verify("+name+"): ERROR: "+e);
            return "Error";
        }
		anwser=anwser.substring(4);
        return anwser;
	}

	/**
	 * gives all the members of a mailinglist 
	 */	
	public Vector expand(String name) {
	   	String anwser="";
		Vector ret = new Vector();
 
        /** Connect to mail-host **/
        if (!connect(mailhost,25)) return ret;
 
        try {
            out.writeBytes("EXPN "+name+"\r\n");
            out.flush();
			while (true) {	
            	anwser = in.readLine();
            	if(anwser.indexOf("250")==0) {
					ret.addElement(anwser.substring(4));
				}
				if(anwser.indexOf("-")!=3) break;
			}
        }   catch (Exception e) {
            debug("expand("+name+"): ERROR: "+e);
            return new Vector();
        }
        return ret;
	}

	public String getModuleInfo() {
		return("Sends mail using a mailhost, Rob Vermeulen");
	}
}
