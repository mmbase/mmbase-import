/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package	org.mmbase.module.irc.communication;

import	org.mmbase.module.irc.communication.*;
import	org.mmbase.module.irc.communication.irc.*;

/**
 * Class Communication
 *
 * @obsolete
 * @deprecated use IrcConnection instead
 * @author vpro
 * @version $Id: Communication.java,v 1.5 2003-01-07 11:49:31 kees Exp $
 */

public class Communication extends IrcConnection implements	CommunicationInterface {

    /**
     * @javadoc
     */
    public Communication(CommunicationUserInterface com) {
        super(com);
    }
}

