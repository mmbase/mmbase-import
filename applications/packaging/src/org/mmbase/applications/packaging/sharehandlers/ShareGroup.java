/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.packaging.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author     Daniel Ockeloen
 * @created    July 20, 2004
 */
public class ShareGroup {

    // logger
    private static Logger log = Logging.getLoggerInstance(ShareGroup.class);
    private String name;

    private HashMap members = new HashMap();


    /**
     *Constructor for the ShareGroup object
     *
     * @param  name  Description of the Parameter
     */
    public ShareGroup(String name) {
        this.name = name;
    }


    /**
     *  Sets the name attribute of the ShareGroup object
     *
     * @param  name  The new name value
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Gets the name attribute of the ShareGroup object
     *
     * @return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Adds a feature to the Member attribute of the ShareGroup object
     *
     * @param  name  The feature to be added to the Member attribute
     * @return       Description of the Return Value
     */
    public boolean addMember(String name) {
        ShareUser su = ShareManager.getShareUser(name);
        if (su != null) {
            members.put(name, su);
        }
        return false;
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean removeMember(String name) {
        members.remove(name);
        return false;
    }


    /**
     *  Gets the members attribute of the ShareGroup object
     *
     * @return    The members value
     */
    public Iterator getMembers() {
        return members.values().iterator();
    }
}

