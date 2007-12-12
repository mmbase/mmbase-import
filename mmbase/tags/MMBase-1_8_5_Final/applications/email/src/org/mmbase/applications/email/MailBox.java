/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

/**
 * At the moment just a container for some constants. This may change
 * @version $Id: MailBox.java,v 1.3 2007-10-24 13:40:23 michiel Exp $
 */
public abstract class MailBox {
    public static final int INBOX      = 0;
    public static final int SENT       = 1;
    public static final int DRAFTS     = 11;
    public static final int TRASH      = 2;
    public static final int PERSONAL   = 3;
}

