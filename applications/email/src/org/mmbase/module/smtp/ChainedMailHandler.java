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

 * @version $Id: ChainedMailHandler.java,v 1.1 2007-10-11 17:47:50 michiel Exp $
 */
public class  ChainedMailHandler implements MailHandler {

    List<MailHandler> chain = new ArrayList<MailHandler>();

    ChainedMailHandler(MailHandler... mh) {
        for (MailHandler m : mh) {
            chain.add(m);
        }
    }

    public boolean handleMessage(Message message) {
        for (MailHandler m : chain) {
            if (m.handleMessage(message)) return true;
        }
        return false;
    }
    public boolean addMailbox(String user) {
        for (MailHandler m : chain) {
            if (m.addMailbox(user)) return true;
        }
        return false;
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

}

