/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;
import org.mmbase.core.event.*;
import org.mmbase.module.core.MMBase;

/**
 * An SMS wrapped into an event.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SMSEvent.java,v 1.2 2007-12-10 09:57:41 michiel Exp $
 */
public class  SMSEvent extends Event {

    private SMS sms;
    private boolean immediate;
    public SMSEvent(SMS sms, boolean i) {
        super();
        this.sms = sms;
        this.immediate = i;
    }

    public SMS getSMS() {
        return sms;
    }
    public boolean isImmediate() {
        return immediate;
    }

    public String toString() {
        return super.toString() + ":" + sms.toString();
    }
}
