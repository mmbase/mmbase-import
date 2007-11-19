/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

import org.mmbase.bridge.*;

/**
 * Implementations of these SMS handlers can perform action on receival of an SMS message.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Handler.java,v 1.4 2007-11-19 12:03:12 michiel Exp $
 **/
public interface Handler {

    boolean handle(Cloud cloud, SMS sms);



}
