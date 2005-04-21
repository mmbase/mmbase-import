/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

/**
 * @author keesj
 * @version $Id: StaticNavigation.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public class StaticNavigation extends AbstractNavigation {
    String id;
    String name;

    public StaticNavigation(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
