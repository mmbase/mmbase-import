/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 */
public class Mailbox {

    // logger
    static private Logger log = Logging.getLoggerInstance(Mailbox.class);

    private Node node;
    private Poster parent;
    private int id;

    /**
     * Constructor
     * @param node mailbox
     * @param parent poster
     */
    public Mailbox(Node node, Poster parent) {
        this.parent = parent;
        this.node = node;
        this.id = node.getNumber();
    }

    /**
     * get the name of the mailbox
     * @return name of the mailbox
     */
    public String getName() {
        return node.getStringValue("name");
    }

    /**
     * get the MMBase objectnumber of the mailbox
     * @return MMBase objectnumber of the mailbox
     */
    public int getId() {
        return node.getNumber();
    }

    /**
     * remove the mailbox
     * @return <code>true</code> if the remove succeeded, <code>false</false> if it threw an exception.
     */
    public boolean remove() {
        try {
            node.delete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * get the mailbox-node
     * @return mailbox
     */
    public Node getNode() {
        return node;
    }
}
