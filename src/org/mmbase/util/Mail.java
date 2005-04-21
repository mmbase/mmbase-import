/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import java.lang.*;
import java.util.*;

/**
 * This mail-object gives persons the functionality to create mail 
 * and send it with the SendMail-module.
 *
 * @author Rob Vermeulen
 * @version 31 December 1996
 */
public class Mail {
	public String to = "";
	public String from = "";
	public String text = "";
	public Hashtable headers = new Hashtable(); 

	public Mail(String to, String from) {
		this.to=to;
		this.from=from;
	}

	public void setText(String text) {
		this.text=text;
	}

	/**
	 * Sets the subject of the mail
	 */
	public void setSubject(String subject) {
		headers.put("Subject",subject);
	}

	/**
	 * Sets the time of the mail
	 */ 
	public void setDate() {
		Date d=new Date();
		headers.put("Date",RFC1123.makeDate(d));
	}

	/** 
	 * Sets given time to the mail
	 */
	public void setDate(String date) {
		headers.put("Date",date);
	}

	/** 
	 * tells the mail from who the mail is comming
	 */
	public void setFrom(String from) {
		headers.put("From",from);
	}

	/**
	 * tells the mail for who the mail is
	 */
	public void setTo(String to) { 
		headers.put("To",to);
	}

	/** 
	 * sends the message to all persons mentioned in the CC list. 
	 * Recipients of the message can see the names of the other recipients.
	 */ 
	public void setCc(String cc) {
		headers.put("CC",cc);
	}

	/** 
	 * sends the message to all persons mentioned in the BCC list. 
	 * Recipients of the message cannot see the names of the other recipients.
	 */ 
	public void setBcc(String bcc) {
		headers.put("BCC",bcc);
	}
	public void setComment(String comment) {
		headers.put("Comment",comment);
	}
	
	public void setReplyTo(String reply) {
		headers.put("Reply-to",reply);
	}

	public String setHeader(String header,String value) {
		return((String)headers.put(header,value));
	}

	public String getHeader(String header) {
		return((String)headers.get(header));
	}

	public String toString() {
		return("Mail -> Headers : "+headers+"\nText :\n"+text+"\n-");
	}
}
	
