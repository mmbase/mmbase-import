/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.*;

/**
 * @author keesj
 * @version $Id: Paths.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public class Paths extends Vector {
    public Path getPath(int index) {
        return (Path) get(index);
    }
}
