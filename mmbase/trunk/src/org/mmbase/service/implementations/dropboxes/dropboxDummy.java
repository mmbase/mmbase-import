/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.implementations.dropboxes;

import org.mmbase.service.interfaces.*;

/**
 * @javadoc
 * @rename  DropboxDummy
 * @author  vpro
 * @version  $Id: dropboxDummy.java,v 1.5 2002-04-29 10:54:17 pierre Exp $
 */
public class dropboxDummy implements dropboxInterface {

    public void startUp() {
    }

    public void shutDown() {
    }

    public String getVersion() {
        return "0";
    }

    public String doDir(String cmds) {
        return "";
    }

    public void setDir(String dir) {
    }

    public void setCmd(String cmd) {
    }

    public void setWWWPath(String wwwpath) {
    }
}

