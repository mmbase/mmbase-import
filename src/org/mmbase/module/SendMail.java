package org.mmbase.module;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.util.*;

/**
 * This module gives mail functionality 
 *
 * @author Rob Vermeulen
 * @version $Revision: 1.3 $ $Date: 2000-03-29 10:04:58 $
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
		if (!connect(mailhost,25)) return false;

	 	try {
			out.writeBytes("MAIL FROM:<"+from+">\n");
			out.flush();
            anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("250")!=0)  return false;
	
			out.writeBytes("RCPT TO:<"+to+">\n");
			out.flush();
            anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("250")!=0)  return false;
			
			out.writeBytes("DATA\n");
			out.flush();
            anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("354")!=0)  return false;
				
			out.writeBytes(data+"\n");
			out.writeBytes("\n.\n");
			out.flush();
            anwser = in.readLine();
			//debug(anwser);
        	if(anwser.indexOf("250")!=0)  return false;
			
			out.writeBytes("QUIT\n");
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
			temp+=headers.get(header)+"\n";
      	}	
		temp+="\n\n";
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
            out.writeBytes("VRFY "+name+"\n");
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
            out.writeBytes("EXPN "+name+"\n");
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
