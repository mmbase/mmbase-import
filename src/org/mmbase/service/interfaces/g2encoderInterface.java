/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.interfaces;

import org.mmbase.service.*;

/**
 * @javadoc
 * @rename G2encoderInterface
 * @author Daniel Ockeloen
 * @version $Id: g2encoderInterface.java,v 1.6 2002-04-29 10:54:19 pierre Exp $
 */
public interface g2encoderInterface extends serviceInterface {
    /**
     * @javadoc
     */
    public String getVersion();
    /**
     * @javadoc
     */
    public int doEncode(String cmds);
}
