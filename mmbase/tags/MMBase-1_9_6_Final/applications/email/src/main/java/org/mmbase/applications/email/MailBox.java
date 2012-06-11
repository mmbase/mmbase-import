/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;
import java.util.ResourceBundle;

/**
 * At the moment just a container for some constants. This may change
 * @version $Id$
 */
public abstract class MailBox {
    public static enum Type {
        INBOX(0),
        SENT(1),
        DRAFTS(11),
        TRASH(2),
        PERSONAL(3);

        private final int db;
        private Type (int db) {
            this.db = db;
        }
        public int getValue() {
            return db;
        }
        public String getName(java.util.Locale locale) {
            return ResourceBundle.getBundle("org.mmbase.applications.email.resources.webmail", locale).getString(toString());
        }
    }

}

