/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.SendMailInterface;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: BugReports.java,v 1.4 2002-11-21 13:39:43 pierre Exp $
 */
public class BugReports extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(BugReports.class.getName());

	private int starttime;

    /**
     * @javadoc
     */
	public BugReports() {
		starttime=(int)(System.currentTimeMillis()/1000);
	}

    /**
     * @javadoc
     */
	private boolean nodeChanged(String machine,String number,String builder,String ctype) {
		int nowtime=(int)(System.currentTimeMillis()/1000);
		if ((nowtime-starttime)>30) {
			log.debug("BugReport ="+number+" "+ctype);
			if (ctype.equals("c") && builder.equals("bugreports")) changedReport(number);
		}
		return(true);
	}

    /**
     * @javadoc
     */
	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
	        super.nodeLocalChanged(machine,number,builder,ctype);
		return(nodeChanged(machine, number, builder, ctype));
	}

    /**
     * @javadoc
     */
	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
       		super.nodeRemoteChanged(machine,number,builder,ctype);
		return(nodeChanged(machine, number, builder, ctype));
	}

    /**
     * @javadoc
     */
	private void changedReport(String number) {
		MMObjectNode node=getNode(number);


		String body=" You are getting this email because you either are the maintainer \n";
		body+=" submitter or have shown interest in the following bugreport \n\n";
		body+=" Bug #"+node.getIntValue("number")+" ("+node.getStringValue("issue")+")\n\n";
		body+=" Last bug in Time log : \n\n";
		Enumeration t=node.getRelatedNodes("mmevents").elements();
		String lastlog=null;
		while (t.hasMoreElements()) {
			MMObjectNode n3=(MMObjectNode)t.nextElement();
			String name=n3.getStringValue("name");
			int start=n3.getIntValue("start");
			lastlog="\t"+DateSupport.date2date(start)+" "+name+"\n";
		}
		if (lastlog!=null) body+=lastlog;

        SendMailInterface sendmail=mmb.getSendMail();
        if (sendmail==null) {
            log.warn("sendmail module not active, cannot send email");
        } else {
            // send the email
            Enumeration e=node.getRelatedNodes("people").elements();
            while (e.hasMoreElements()) {
                MMObjectNode n2=(MMObjectNode)e.nextElement();
                String firstname=n2.getStringValue("firstname");
                String lastname=n2.getStringValue("lastname");
                String email=n2.getStringValue("email");
                log.debug("EMAIL="+email);
                Mail mail=new Mail(email,"bugs@mmbase.org");
                mail.setSubject("Mail from BugTracker");
                mail.setDate();
                mail.setReplyTo("bugs@mmbase.org"); // should be from
                mail.setText(body);
                if (mmb.getSendMail().sendMail(mail)==false) {
                    log.error("sending email failed");
                }
            }
		}
	}
}
