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
    private int messagecount=0;
    private int messagenewcount=0;
    private int messageunreadcount=0;

    /**
     * Constructor
     * @param node mailbox
     * @param parent poster
     */
    public Mailbox(Node node, Poster parent) {
        this.parent = parent;
        this.node = node;
        this.id = node.getNumber();
	readStats();
    }

    /**
     * get the name of the mailbox
     * @return name of the mailbox
     */
    public String getName() {
        return node.getStringValue("name");
    }


    /**
     * get message count of this mailbox
     * @return number of messages in this mailbox
     */
    public int getMessageCount() {
        return messagecount;
    }


    /**
     * get message count unread of this mailbox
     * @return number of unread messages in this mailbox
     */
    public int getMessageUnreadCount() {
        return messageunreadcount;
    }


    /**
     * get message count unread of this mailbox
     * @return number of unread messages in this mailbox
     */
    public int getMessageNewCount() {
        return messagenewcount;
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
	    node.deleteRelations();
            node.delete();
            return true;
        } catch (Exception e) {
	    e.printStackTrace();
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

  public void signalMailboxChange() {
	readStats();
	parent.mailboxChanged(this);
  }


   public void readStats() {
	messagecount = 0;
	messagenewcount = 0;
	messageunreadcount = 0;
	if (node!=null) {
		NodeIterator i=node.getRelatedNodes("forumprivatemessage").nodeIterator();
		while (i.hasNext()) {
			Node node=i.nextNode();
			messagecount = messagecount + 1;
			int viewstate =  node.getIntValue("viewstate");
			if ( viewstate == 0 ) {
				messageunreadcount = messageunreadcount + 1;
			}
			//log.info ("p="+parent.getLastSessionEnd()+" c="+node.getIntValue("createtime"));
			if (parent.getLastSessionEnd() < node.getIntValue("createtime")) messagenewcount =  messagenewcount + 1;
		}
	}
   }
}
