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
 * @rename DropboxInterface
 * @author Daniel Ockeloen
 * @version $Id: dropboxInterface.java,v 1.5 2002-04-29 10:54:19 pierre Exp $
 */
public interface dropboxInterface extends serviceInterface {
    /**
     * @javadoc
     */
    public String getVersion();
    /**
     * @javadoc
     */
    public void setCmd(String cmd);
    /**
     * @javadoc
     */
    public void setDir(String dir);
    /**
     * @javadoc
     */
    public void setWWWPath(String path);
    /**
     * @javadoc
     */
    public String doDir(String cmds);
}
