/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

import org.mmbase.bridge.*;

/**
 * Implementations of these SMS handler can perform action on receivel of an SMS message.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Handler.java,v 1.2 2007-11-12 15:54:53 michiel Exp $
 **/
public interface Handler {

    boolean handle(Cloud cloud, Receiver.SMS sms);



}
