/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.mmbase.applications.email.SendMail;

/**

 * @version $Id: MailHandler.java,v 1.6 2007-10-11 17:47:50 michiel Exp $
 */
public interface  MailHandler {

    boolean handleMessage(Message message);
    boolean addMailbox(String user);
    void clearMailboxes();
    int size();

}

class MailHandlerFactory {
    static MailHandler instance;

    public static MailHandler getInstance() {
        return instance;
    }
}
