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
 * handler for email one shot and keep

 * @author Daniel Ockeloen
 */
public class EmailHandlerOneShotKeep extends EmailHandler {

    // logger
    private static Logger log = Logging.getLoggerInstance(EmailHandlerOneShotKeep.class.getName());

    /**
    * mail, direct mail action wanted
    * mail the node out and wait until its
    * finished.
    */
    public static void mail(MMObjectNode node) {
	log.info("email handler oneshotkeep mail() called");
	sendMailNode(node);
    }

    /**
    * startmail, create a background handler
    * that starts mailing the node. Return
    * directly (while mailing is still going on).
    */
    public static void startmail(MMObjectNode node) {
	log.info("email handler oneshotkeep startmail() called");
	EmailBackgroundHandler mailer=new EmailBackgroundHandler(node);
    }
}






