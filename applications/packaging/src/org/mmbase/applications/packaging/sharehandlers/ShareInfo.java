/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers;

import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.applications.packaging.ShareManager;

/**
 * @author     Daniel Ockeloen
 */
public class ShareInfo {

    // even if we have a share we have active boolean to allow
    // people to turn of a share without loosing all its setup(s).
    // this works the same way as windows shares.
    private boolean active;

    private HashMap<String, Object> users = new HashMap<String, Object>();

    private HashMap<String, Object> groups = new HashMap<String, Object>();

    /**
     * Constructor for the ShareInfo object
     */
    public ShareInfo() { }


    /**
     *  Gets the active attribute of the ShareInfo object
     *
     * @return    The active value
     */
    public boolean isActive() {
        return active;
    }


    /**
     *  Sets the active attribute of the ShareInfo object
     *
     * @param  wantedstate  The new active value
     */
    public void setActive(boolean wantedstate) {
        active = wantedstate;
    }


    /**
     *  Adds a feature to the User attribute of the ShareInfo object
     *
     * @param  name  The feature to be added to the User attribute
     * @return       Description of the Return Value
     */
    public boolean addUser(String name) {
        ShareUser su = ShareManager.getShareUser(name);
        if (su != null) {
            users.put(name, su);
            return true;
        }
        return false;
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean removeUser(String name) {
        users.remove(name);
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean removeGroup(String name) {
        groups.remove(name);
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  user  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean containsUser(String user) {
        if (users.containsKey(user)) {
            return true;
        }
        return false;
    }



    /**
     *  Description of the Method
     *
     * @param  group  Description of the Parameter
     * @return        Description of the Return Value
     */
    public boolean containsGroup(String group) {
        if (groups.containsKey(group)) {
            return true;
        }
        return false;
    }


    /**
     *  Adds a feature to the Group attribute of the ShareInfo object
     *
     * @param  name  The feature to be added to the Group attribute
     * @return       Description of the Return Value
     */
    public boolean addGroup(String name) {
        ShareGroup sg = ShareManager.getShareGroup(name);
        if (sg != null) {
            groups.put(name, sg);
            return true;
        }
        return false;
    }


    /**
     *  Gets the shareUsers attribute of the ShareInfo object
     *
     * @return    The shareUsers value
     */
    public Iterator<Object> getShareUsers() {
        return users.values().iterator();
    }


    /**
     *  Description of the Method
     *
     * @param  user      Description of the Parameter
     * @param  password  Description of the Parameter
     * @param  method    Description of the Parameter
     * @param  host      Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean sharedForUser(String user, String password, String method, String host) {
        ShareUser su = (ShareUser) users.get(user);
        if (su != null && password.equals(su.getPassword())) {
            return true;
        }
        return false;
    }


    /**
     *  Gets the shareGroups attribute of the ShareInfo object
     *
     * @return    The shareGroups value
     */
    public Iterator<Object> getShareGroups() {
        return groups.values().iterator();
    }
}

