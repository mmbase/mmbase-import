/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.gui.html.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @version 10 Dec 2000
 *
 * this is a dummy/hack to be replaced by a full Email
 * object system now being developed at submarine.nl. It
 * now just does very basic mail to allow the new users 
 * system to use it.
 */
public class Email extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(Email.class.getName());
	private EmailSendProbe sendprobe;
	public final static int STATE_UNKNOWN=-1;
	public final static int STATE_WAITING=0;
	public final static int STATE_DELIVERED=1;
	public final static int STATE_FAILED=2;
	public final static int STATE_SPAMGARDE=3;

	public final static int TYPE_ONESHOT=1;
	public final static int TYPE_REPEATMAIL=2;

	public boolean init() {
		super.init ();
		sendprobe=new EmailSendProbe(this);
		return (true);
	}

	/**
	* this message has changed so lets see if we need to do
	* something.
	*/
	public boolean nodeChanged(String number,String builder,String ctype) {
		// check the type of change on the object
		if (ctype.equals("n")) {
		}
		// its a new object so lets check if we should mail
		// not sure if we can signal it on changes too. Since
		// that means we will 'recheck' them alot
		MMObjectNode node=getNode(number);
		sendprobe.putTask(node);	
		return(true);
	}

	/**
	* check the message object, now if state is 1 (oneshot)
	* it mails and removed itself from the cloud when done
	*/
	public void checkOneShotMail(MMObjectNode node) {

		int mailstatus=node.getIntValue("mailstatus");
		if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {

			mailstatus=sendMailNode(node);
			if (mailstatus==STATE_DELIVERED) {
				removeNode(node);
			} else {
				// set the changes back to the database
				node.commit();
			}
		} 
		
	}


	/**
	* check the message object, now if state is 1 (oneshot)
	* it mails and removed itself from the cloud when done
	*/
	public void checkRepeatMail(MMObjectNode node) {
		int mailstatus=node.getIntValue("mailstatus");
		if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {

			mailstatus=sendMailNode(node);
			if (mailstatus==STATE_DELIVERED) {
				int mailtime=node.getIntValue("mailtime");
				int repeattime=node.getIntValue("repeattime");

				int nowtime=(int)(System.currentTimeMillis()/1000);
				int proposedtime=mailtime+repeattime;
				
				// if we allready passed the time or repeattime
				// is less then 60 seconds we ignore as a spam
				// garde
				if (repeattime>59 && nowtime<proposedtime) {
					node.setValue("mailtime",proposedtime);
					node.setValue("mailstatus",STATE_WAITING);
				} else {
					node.setValue("mailstatus",STATE_SPAMGARDE);
				}
			}
			// set the changes back to the database
			node.commit();
		} 
	}


	/**
	*/
	public int sendMailNode(MMObjectNode node) {
			String subject=getSubject(node);
			String to=getTo(node);
			String from=node.getStringValue("from");
			String replyto=node.getStringValue("replyto");
			//String body=node.getStringValue("body");
			String body=getBody(node);
	
			// now if a url is defined it overrides the body
			// it will then send the webpage defined by the
			// url as the body !! (only  local url's are
			// now supported that use html or shtml
			String url=node.getStringValue("url");
			if (url!=null && !url.equals("")) {
				// get the page
				String tmpbody=getPage(url);
				if (tmpbody!=null) body=tmpbody;
			}
	

			StringTokenizer tok = new StringTokenizer(to,",\n\r");
			while (tok.hasMoreTokens()) {
				String to_one=tok.nextToken();
				Mail mail=new Mail(to_one,from);
				mail.setSubject(subject);
				mail.setDate();
				if (replyto!=null && !replyto.equals("")) mail.setReplyTo(replyto); 
				mail.setText(body);
	
				// little trick if it seems valid html page set
				// the headers for html mail
				if (body.indexOf("<HTML>")!=-1 && body.indexOf("</HTML>")!=-1) {
					mail.setHeader("Mime-Version","1.0");
					mail.setHeader("Content-Type","text/html; charset=\"ISO-8859-1\"");
				}
				
				// send the message to the user defined
				if (to==null || mmb.getSendMail().sendMail(mail)==false) {
					log.debug("Email -> mail failed");
					node.setValue("mailstatus",STATE_FAILED);
				} else {
					node.setValue("mailstatus",STATE_DELIVERED);
				}
			}
			node.setValue("mailedtime",(int)(System.currentTimeMillis()/1000));
			return(node.getIntValue("mailstatus"));
	}


	String getTo(MMObjectNode node) {
		String to=node.getStringValue("to");
		if (to==null || to.equals("")) {
			to=getPeoplesEmail(node);
			String tmp=getUsersEmail(node);	
			if (tmp!=null) {
				if (to==null || to.equals("")) {
					to=tmp;
				} else {
					to+=","+tmp;
				}
			}
		}
		log.debug("TO="+to);
		return(to);
	}

	String getPeoplesEmail(MMObjectNode node) {
		// try and find related people
		String to=null;
		Vector rels=node.getRelatedNodes("people");
		if (rels!=null) {
			Enumeration enum=rels.elements();
       			while (enum.hasMoreElements()) {
				MMObjectNode pnode=(MMObjectNode)enum.nextElement();
				String email=pnode.getStringValue("email");
				if (to==null || to.equals("")) {
					to=email;
				} else {
					to+=","+email;
				}
			}
		}
		return(to);
	}


	String getUsersEmail(MMObjectNode node) {
		// try and find related users
		String to=null;
		Vector rels=node.getRelatedNodes("users");
		if (rels!=null) {
			Enumeration enum=rels.elements();
       			while (enum.hasMoreElements()) {
				MMObjectNode pnode=(MMObjectNode)enum.nextElement();
				String email=pnode.getStringValue("email");
				if (to==null || to.equals("")) {
					to=email;
				} else {
					to+=","+email;
				}
			}
		}
		return(to);
	}

	String getSubject(MMObjectNode node) {
		String subject=node.getStringValue("subject");
		if (subject!=null) {
			if (subject.startsWith("/")) {
				// we need a url=".." in tcp !
				String pagesubject=getPage(subject);
				if (pagesubject!=null) {
					return(pagesubject);
				} else {
					return("subject page 404 : "+subject);
				}
			} else {
				return(subject);
			}
		} else {
			return(""); // should we make the subject empty on defailt ?
		}
	}


	String getBody(MMObjectNode node) {
		String body=node.getStringValue("body");
		if (body!=null) {
			if (body.startsWith("/")) {
				// we need a url=".." in tcp !
				String pagebody=getPage(body);
				if (pagebody!=null) {
					return(pagebody);
				} else {
					return("body page 404 : "+body);
				}
			} else {
				return(body);
			}
		} else {
			return(""); // should we make the body empty on defailt ?
		}
	}

	/**
	* check the message object, now if state is 1 (oneshot)
	* it mails and removed itself from the cloud when done
	*/
	public void checkMailNode(MMObjectNode node) {

		// get the message object from the cloud
		// MMObjectNode node=getNode(number);	

		// obtain all the needed fields from the object
		int mailtype=node.getIntValue("mailtype");
		switch(mailtype) {
			case TYPE_ONESHOT :
				checkOneShotMail(node);	
				break;
			case TYPE_REPEATMAIL:
				checkRepeatMail(node);	
				break;
		}
	}


	/**
	* local change reroute to nodeChange
	*/
	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}


	/**
	* remote change reroute to nodeChange
	*/
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}
	

	/**
	* getPage, using the scanparser (remember this is a hack to be
	* replaced by a new version soon).
	*/
  	public String getPage(String url) { 
		// get the scanparser
		scanparser m=(scanparser)mmb.getBaseModule("SCANPARSER");
		if (m!=null) {
			// found module, create a empty
			// context since we don't have a user
			scanpage sp=new scanpage();
			// get the page and return it
			return(m.calcPage(url,sp,0));
		}
		return(null);
	}

	public boolean performTask(MMObjectNode node) {
		checkMailNode(node);
		return(true);
	}



}
