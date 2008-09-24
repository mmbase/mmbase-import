/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.smtp;
import java.util.*;
import javax.mail.*;

/**

 * @version $Id: ChainedMailHandler.java,v 1.4 2007-11-09 18:26:23 michiel Exp $
 */
public class  ChainedMailHandler implements MailHandler {

    List<MailHandler> chain = new ArrayList<MailHandler>();

    ChainedMailHandler(MailHandler... mh) {
        for (MailHandler m : mh) {
            chain.add(m);
        }
    }

    public MessageStatus handleMessage(Message message) {
        for (MailHandler m : chain) {
            MessageStatus status = m.handleMessage(message);
            if (status != MessageStatus.IGNORED) return status;
        }
        return MessageStatus.IGNORED;
    }
    public MailBoxStatus addMailbox(String user, String domain) {
        MailBoxStatus status = MailBoxStatus.UNDEFINED;
        for (MailHandler m : chain) {
            status = m.addMailbox(user, domain);
            if (status == MailBoxStatus.OK) return status;
        }
        return status;
    }
    public void clearMailboxes() {
        for (MailHandler m : chain) {
            m.clearMailboxes();
        }
    }
    public int size() {
        int result = 0;
        for (MailHandler m : chain) {
            result += m.size();
        }
        return result;
    }

    public String toString() {
        return "ChainedMailHandler" + chain;
    }

}

