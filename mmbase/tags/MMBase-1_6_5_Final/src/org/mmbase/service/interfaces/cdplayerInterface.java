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
 * @rename CdplayerInterface
 * @author Daniel Ockeloen
 * @version $Id: cdplayerInterface.java,v 1.6 2002-04-29 10:54:18 pierre Exp $
 */
public interface cdplayerInterface extends serviceInterface {
    /**
     * @javadoc
     */
    public String getVersion();
    /**
     * @javadoc
     */
    public int getTrack(int number,String filename);
    /**
     * @javadoc
     */
    public String getListCD();
    /**
     * @javadoc
     */
    public String getInfoCDtoString();
}
