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
import org.mmbase.util.logging.*;

/**
 * This module provides mail functionality
 *
 * @author Rob Vermeulen
 */
public class SendMail extends Module implements SendMailInterface {
    private static Logger log = Logging.getLoggerInstance(SendMail.class.getName());
   	private DataInputStream in = null;
   	private DataOutputStream out = null;
	private Socket connect = null;
	private String mailhost = "";

	public void reload() {
       	mailhost=getInitParameter("mailhost");
	}

	public void unload() { }
	
	public void onload() { }
	
	public void shutdown() { }
	
	public void init() {
       	mailhost=getInitParameter("mailhost");
		log.debug("Module SendMail started (mailhost="+mailhost+")");
	}

	/** 
	 * Connect to the mailhost
	 */
	private boolean connect(String host, int port) { 
		log.service("SendMail connected to host="+host+", port="+port+")");
		String result="";

        try {
            connect=new Socket(host,port);
		} catch (Exception e) { 
			log.error("SendMail cannot connect to host="+host+", port="+port+")."+e);
			return false;
		}
        try {
           out=new DataOutputStream(connect.getOutputStream());
        } catch (IOException e) { 	
			log.error("Sendmail cannot get outputstream." +e);
			return false;}
        try {
           in=new DataInputStream(connect.getInputStream());
        } catch (IOException e) { 
			log.error("SendMail cannot get inputstream."+e);
			return false;}
		try {
        		result = in.readLine();
		} catch (Exception e) { 
			log.error("SendMail cannot read response."+e);
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
		log.service("SendMail sending mail to "+to);
		String anwser="";

		/** Connect to mail-host **/	
		if (!connect(mailhost,25)) {
			log.error("SendMail cannot connect to mailhost host="+mailhost+", from"+from+", to"+to);
			return false;
		}

	 	try {
			out.writeBytes("MAIL FROM:<"+from+">\r\n");
			out.flush();
            		anwser = in.readLine();
            		log.debug("SendMail "+getStateInfo(anwser)+" based on anwser="+anwser);
		
        		if (anwser.indexOf("250")!=0) {
            			log.error("SendMail error sending "+from+","+to+", error nr : "+getStateInfo(anwser)+" based on anwser="+anwser);
				return(false);
			}

			StringTokenizer tok = new StringTokenizer(to,",\n\r");
			while (tok.hasMoreTokens()) {
				String tmp=tok.nextToken();
				out.writeBytes("RCPT TO:<"+tmp+">\r\n");
			}
			out.flush();
            		anwser = in.readLine();
            		log.debug("SendMail "+getStateInfo(anwser)+" based on anwser="+anwser);
        		if (anwser.indexOf("250")!=0) {
            			log.error("SendMail error sending "+from+","+to+", error nr : "+getStateInfo(anwser)+" based on anwser="+anwser);
				 return false;
			}
			
			out.writeBytes("DATA\r\n");
			out.flush();
            		anwser = in.readLine();
            		log.debug("SendMail "+getStateInfo(anwser)+" based on anwser="+anwser);
        		if (anwser.indexOf("354")!=0)  {
            			log.error("SendMail error sending "+from+","+to+", error nr : "+getStateInfo(anwser)+" based on anwser="+anwser);
				 return false;
			}
				
			out.writeBytes(data+"\r\n");
			out.writeBytes("\r\n.\r\n");
			out.flush();
            		anwser = in.readLine();
            		log.debug("SendMail "+getStateInfo(anwser)+" based on anwser="+anwser);
        		if (anwser.indexOf("250")!=0) {
            			log.error("SendMail error sending "+from+","+to+", error nr : "+getStateInfo(anwser)+" based on anwser="+anwser);
				 return false;
			}
			
			out.writeBytes("QUIT\r\n");
			out.flush();
			in.close();
        } catch (Exception e) {
            log.error("SendMail error sending "+from+","+to+","+data.length()+") "+e);
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
 
        } catch (Exception e) {
            log.error("Sendmail verify error on: "+name+". "+e);
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
        } catch (Exception e) {
            log.error("Sendmail expand error on:"+name+". "+e);
            return new Vector();
        }
        return ret;
	}

	public String getModuleInfo() {
		return("Sends mail using a mailhost, Rob Vermeulen");
	}

        public String getStateInfo(String line) {
		try {
			int error=Integer.parseInt(line.substring(0,3));
               		switch(error) {
                    		case 500: return("user error, 500 ,Syntax error command unrecognized");
                    		case 501: return("user error, 501 ,Syntax error in parameters or agruments");
                    		case 502: return("user error, 502 ,Command not implemented");
                    		case 503: return("user error, 503 ,Bad sequence of commands");
                    		case 504: return("user error, 504 ,Syntax error command unrecognized");
                    		case 211: return("status, 211 ,System status, or system help reply");
                    		case 214: return("status, 214 ,Help message");
                    		case 220: return("success, 220 ,Service ready");
                    		case 221: return("success, 221 ,Service closing transmission channel");
                    		case 250: return("success, 250 ,Requested mail action okey, completed");
                    		case 251: return("success, 251 ,User not local will forward to ");
                    		case 354: return("success, 354,Start mail input; end with <CRLF><CRLF>");
                    		case 421: return("system error, 421,Service not available, closing transmition channel");
                    		case 450: return("system error, 450,Requested mail action not taken: mailbox unavailable");
                    		case 551: return("system error, 551,User not local, please try forward");
                    		case 452: return("system error, 452,Request not taken: insufficient system storage");
                    		case 552: return("system error, 552,Request action aborted: exceeded storage allocation");
                    		case 553: return("system error, 553,Request action not taken: mailbox name not allowed");
                    		case 554: return("system error, 554,Transaction failed");
                	}
		} catch(Exception e) {
			return("parse error in getStateInfo on : "+line);
		}
		return("");
	}
}
