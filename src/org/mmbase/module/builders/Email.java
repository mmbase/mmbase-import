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

	private EmailSendProbe sendprobe;


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
			// its a new object so lets check if we should mail
			//checkNewMailNode(number);
			MMObjectNode node=getNode(number);
			sendprobe.putTask(node);	
		}
		return(true);
	}

	/**
	* check the message object, now if state is 1 (oneshot)
	* it mails and removed itself from the cloud when done
	*/
	public void checkNewMailNode(MMObjectNode node) {

		// get the message object from the cloud
		// MMObjectNode node=getNode(number);	

		// obtain all the needed fields from the object
		int mailtype=node.getIntValue("mailtype");
		String subject=node.getStringValue("subject");
		String to=node.getStringValue("to");
		String from=node.getStringValue("from");
		String replyto=node.getStringValue("replyto");
		String body=node.getStringValue("body");

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

		// if its a oneshot mail then create mail and send it
		// at the end remove the object from the cloud
		if (mailtype==1) {
			// its one shot so lets mail and zap the node

			Mail mail=new Mail(to,from);
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
			if (mmb.getSendMail().sendMail(mail)==false) {
				System.out.println("Email -> mail failed");
			} else {
				System.out.println("Email -> mail send to : "+to);
			}
		
			// remove message from the cloud
			removeNode(node);
		} else if (mailtype==2) {
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
		System.out.println("GOT perform="+node);
		checkNewMailNode(node);
		return(true);
	}



}
