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
 * handler for email one shot

 * @author Daniel Ockeloen
 */
public class EmailHandlerOneShot extends EmailHandler {

    // logger
    private static Logger log = Logging.getLoggerInstance(EmailHandlerOneShot.class.getName());

    /**
    * mail, direct mail action wanted
    * mail the node out and wait until its
    * finished.
    */
    public static void mail(MMObjectNode node) {
	log.debug("email handler oneshot mail() called");
	sendMailNode(node);
    }

    /**
    * startmail, create a background handler
    * that starts mailing the node. Return
    * directly (while mailing is still going on).
    */
    public static void startmail(MMObjectNode node) {
	log.debug("email handler oneshot startmail() called");
	// start backgroundhandler with the email node
	EmailBackgroundHandler mailer=new EmailBackgroundHandler(node);
    }
}
