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
public class Posting {

    // logger
    static private Logger log = Logging.getLoggerInstance(Posting.class);

    private int id;
    private PostThread parent;
    private Node node;

    /**
     * Construct the posting
     *
     * @param node postingnode
     * @param parent postthread
     */
    public Posting(Node node, PostThread parent) {
        this.node = node;
        this.id = node.getNumber();
        this.parent = parent;
    }

    /**
     * Set the id of this postingnode
     * @param id posting id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * set the subject of the posting
     * @param subject
     */
    public void setSubject(String subject) {
        node.setValue("subject", subject);
    }

    /**
     * set the body of the posting
     * @param body
     */
    public void setBody(String body) {
	log.info("SETBODY");
        node.setStringValue("body", body);
	String parsed=node.getStringValue("body");
	node.setStringValue("cbody",parsed);
    }

    /**
     * set the date/time of the last time this posting was editted
     * @param time Date/time (Epoch)
     */
    public void setEditTime(int time) {
        node.setIntValue("edittime", time);
    }

    /**
     * get the date/time of the last time this posting was editted
     * @return Date/time (Epoch)
     */
    public int getEditTime() {
        return node.getIntValue("edittime");
    }

    /**
     * get the id of this posting
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * set the node of this posting
     * @param node posting
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * get the subject of this posting
     * @return subject of this posting
     */
    public String getSubject() {
        return node.getStringValue("subject");
    }

    /**
     * get the body of this posting
     * @return body of this posting
     */
    public String getBody() {
	log.info("GETBODY");
        return node.getStringValue("cbody");
    }

    /**
     * get the accountname/nick of the poster of this posting
     * @return accountname/nick of the poster
     */
    public String getPoster() {
        return node.getStringValue("c_poster");
    }

    /**
     * get the date/time (epoch) when this posting was posted
     * @return date/time (epoch)
     */
    public int getPostTime() {
        return node.getIntValue("createtime");
    }

    /**
     * Delete a posting and signal the parent postthread that the posting must be removed
     * @return allways <code>true</code>
     */
    public boolean remove() {
        node.delete(true);
        parent.childRemoved(this);
        return true;
    }

    /**
     * save the node to the cloud
     * @return allways <code>true</code>
     */
    public boolean save() {
        node.commit();
        return true;
    }
}
