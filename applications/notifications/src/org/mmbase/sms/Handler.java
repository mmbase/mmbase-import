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
 * @version $Id$
 **/
public interface Handler {


    /**
     * Handle an SMS.
     * @return true if the SMS was understood and could be handled. False otherwise.
     */
    boolean handle(Cloud cloud, SMS sms);



}
