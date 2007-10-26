/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

import org.mmbase.bridge.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: Handler.java,v 1.1 2007-10-26 15:34:36 michiel Exp $
 **/
public interface Handler {

    boolean handle(Cloud cloud, Receiver.SMS sms);



}
