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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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
	public final static int STATE_UNDERWAY=4;

	public final static int TYPE_ONESHOT=1;
	public final static int TYPE_REPEATMAIL=2;
	public final static int TYPE_ONESHOTKEEP=3;
	private Vector dubcheck=new Vector(100);

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
		if (ctype.equals("d")) {
			return(true);
		}

		// its a object so lets check if we should mail
		// not sure if we can signal it on changes too. Since
		// that means we will 'recheck' them alot
		MMObjectNode node=getNode(number);


		// if they tell us they are busy we will trust them
		// and ignore in a positive way.
		int mailstatus=node.getIntValue("mailstatus");
		if (mailstatus==STATE_UNDERWAY) {
			return(true);
		}

		// if status is unknown or waiting lets mail or schedule
		// the task for mailing
		if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {
			sendprobe.putTask(node);	
		} else {
			checkMailNode(node);
		}
		return(true);
	}

	/**
	* check the message object, now if state is 1 (oneshot)
	* it mails and removed itself from the cloud when done
	*/
	public void checkOneShotMail(MMObjectNode node) {
		// just to make sure lets recheck state
		int mailstatus=node.getIntValue("mailstatus");
		if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {
			// so lets send this node as email
			sendMailNode(node);
		}

		// if mail is delivered then remove it
		if (mailstatus==STATE_DELIVERED) {
				removeNode(node);
		} 
		
	}


	/**
	* check the message object, now if state is 1 (oneshot)
	*/
	public void checkOneShotKeepMail(MMObjectNode node) {
		// just to make sure lets recheck state
		int mailstatus=node.getIntValue("mailstatus");
		if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {
			// so lets send this node as email
			sendMailNode(node);
		}
	}


	/**
	* check the message object, now if state is 1 (oneshot)
	* it mails and removed itself from the cloud when done
	*/
	public void checkRepeatMail(MMObjectNode node) {
		// just to make sure lets recheck state
		int mailstatus=node.getIntValue("mailstatus");
		if (mailstatus==STATE_UNKNOWN || mailstatus==STATE_WAITING) {
			// so lets send this node as email
			mailstatus=sendMailNode(node);
		// is mail delivered ifso update it for next mail event
		} else if (mailstatus==STATE_DELIVERED) {

			// get the mailtime, so we can calc the new
			// mailtime.
			int mailtime=node.getIntValue("mailtime");

			// what is the repeat time of the message
			int repeattime=node.getIntValue("repeattime");

			// calc the new proposed time
			int proposedtime=mailtime+repeattime;

			// get the current time to make sure we are not 
			// allready passed the new proposed time
			int nowtime=(int)(System.currentTimeMillis()/1000);
			
			// if we allready passed the time or repeattime
			// is less then 60 seconds we ignore as a spam
			// guard
			if (repeattime>59 && nowtime<proposedtime) {
				// set the new proposed time
				node.setValue("mailtime",proposedtime);
				// signal that we are ready again
				node.setValue("mailstatus",STATE_WAITING);
				// commit to the cloud
				node.commit();
			} else {
				// spam guard triggered
				node.setValue("mailstatus",STATE_SPAMGARDE);
				// commit to the cloud
				node.commit();
			}
		} 
	}


	/**
	*/
	public int sendMailNode(MMObjectNode node) {
			String ckey=""+node.getIntValue("number");
			if (dubcheck.contains(ckey)) {
				System.out.println("DUB KILLED");
				return(-1);
			} else {
				dubcheck.addElement(ckey);
				if (dubcheck.size()>100) {
					dubcheck.removeElementAt(99);
				}
			}


			// first set Node underway
			node.setValue("mailstatus",STATE_UNDERWAY);

			// commit to the cloud so the others in
			// the cluster know we are busy
			node.commit();

			String subject=getSubject(node);
			String to=getTo(node);
			String from=node.getStringValue("from");
			String replyto=node.getStringValue("replyto");
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
			
				if (body.indexOf("<DONTMAIL>")==-1) {	
					// send the message to the user defined
					if (to==null || mmb.getSendMail().sendMail(mail)==false) {
						log.debug("Email -> mail failed");
						node.setValue("mailstatus",STATE_FAILED);
					} else {
						log.debug("Email -> mail send");
						node.setValue("mailstatus",STATE_DELIVERED);
					}
				} else { 
					log.debug("Don't mail tag found");
				}
			}
			node.setValue("mailedtime",(int)(System.currentTimeMillis()/1000));
			node.commit();

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
		//System.out.println("TO="+to);
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
			case TYPE_ONESHOTKEEP :
				checkOneShotKeepMail(node);	
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
