/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * New Email builder, 
 *
 * rewrite of the email system that became too complex to handle
 * focus on the new one is different. code is now split per mail
 * type to allow for easer debug and better control over the 'simple'
 * mail action. The delayed and repeat mail will be handled with
 * the upcomming crontab builder
 *
 * @author Daniel Ockeloen
 */
public class EmailBuilder extends MMObjectBuilder {

    // logger
    private static Logger log = Logging.getLoggerInstance(EmailBuilder.class.getName());


    // defined values for state ( node field "mailstatus" )
    public final static int STATE_UNKNOWN=-1; // unknown
    public final static int STATE_WAITING=0; // waiting
    public final static int STATE_DELIVERED=1; // delivered
    public final static int STATE_FAILED=2; // failed
    public final static int STATE_SPAMGARDE=3; // spam filter hit, not mailed
    public final static int STATE_QUEUED=4; // queued


    // defined values for state ( node field "mailtype" )
    public final static int TYPE_ONESHOT=1; // Email will be sent and removed after sending.
    public final static int TYPE_REPEATMAIL=2; // Email will be sent and scheduled after sending for a next time
    public final static int TYPE_ONESHOTKEEP=3; // Email will be sent and will not be removed.

    // number of emails send sofar since startup
    private int numberofmailsend=0;

    // reference to the sendmail module
    private static SendMailInterface sendmail;

    // reference to the expire handler
    private static EmailExpireHandler expirehandler;

    /**
     * init
     */
    public boolean init() {
        super.init ();

	// get the sendmail module
	sendmail=mmb.getSendMail();
	
	// start the email nodes expire handler, deletes
	// oneshot email nodes after the defined expiretime 
	// check every defined sleeptime
	expirehandler=new EmailExpireHandler(this,60,30*60);

        return true;
    }

 
    /**
    * override the function call to receive the functions called from
    * the outside world (mostly from the taglibs)
    */
    protected Object executeFunction(MMObjectNode node, String function, List arguments) {
	log.debug("function="+function);

	// function setType(type) called, normally not used
	if (function.equals("setType") || function.equals("settype") ) {
		setType(node,arguments);
	}

	// function mail(type) called
	if (function.equals("mail")) {

            // check if we have arguments ifso call setType()
	    if (arguments.size()>0) setType(node,arguments);

	    // get the mailtype so we can call the correct handler/method
            int mailtype=node.getIntValue("mailtype");
            switch(mailtype) {
                case TYPE_ONESHOT :
			EmailHandlerOneShot.mail(node);
			break;
                case TYPE_ONESHOTKEEP :
			EmailHandlerOneShotKeep.mail(node);
			break;
	    }
	}

	// function mail(type) called (starts a background thread)
	if (function.equals("startmail")) {

            // check if we have arguments ifso call setType()
	    if (arguments.size()>0) setType(node,arguments);

	    // get the mailtype so we can call the correct handler/method
            int mailtype=node.getIntValue("mailtype");
            switch(mailtype) {
             	case TYPE_ONESHOT :
			EmailHandlerOneShot.startmail(node);
			break;
             	case TYPE_ONESHOTKEEP :
			EmailHandlerOneShotKeep.startmail(node);
                	break;
	    }
	}
	return(null);
    }


    /**
    * return the sendmail module
    */
    public static SendMailInterface getSendMail() {
    	return(sendmail);
    }
	
    /**
    * set the mailtype based on the first argument in the list
    */
    private static void setType(MMObjectNode node,List arguments) {
	String type=(String)arguments.get(0);
	if (type.equals("oneshot")) node.setValue("mailtype",1);
	if (type.equals("oneshotkeep")) node.setValue("mailtype",3);
    }


    /**
    * return all the mailed nodes older than time given
    */
    public Enumeration getMailedOlderThen(int expiretime) {
	// calc search time based on expire time
	int time=(int)(System.currentTimeMillis()/1000)-expiretime;

	// query database for the nodes
	Enumeration e=search("mailedtime=S"+time+" and mailstatus='1' and mailtype='1'");
	
	// return the nodes
	return e;
    }	
}
