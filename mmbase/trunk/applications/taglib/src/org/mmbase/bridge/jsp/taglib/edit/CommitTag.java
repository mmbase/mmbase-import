/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.CloudProvider;
import org.mmbase.bridge.Transaction;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* This tag can be used inside a transaction tag, to commit it. It also
* serves as a baseclass for e.g. CancelTag
*
* @author Michiel Meeuwissen 
**/

public class CommitTag extends CloudReferrerTag { 
    // perhaps it would be nicer to extend CloudReferrer to TransactionReferrer first.

    private static Logger log = Logging.getLoggerInstance(CommitTag.class.getName());

    void setTransaction(String t) {
        setCloud(t);
    }

    protected void doAction(Transaction t) {
        t.commit();
    }
    protected String actionName() {
        return "commit";
    }

    public int doStartTag() throws JspTagException{
        // find the parent transaction:        
        Class transactionClass;
        try {
            transactionClass = Class.forName("org.mmbase.bridge.jsp.taglib.edit.TransactionTag");
        } catch (java.lang.ClassNotFoundException e) {
            throw new JspTagException ("Could not find TransactionTag class");  
        }
        TransactionTag tt = (TransactionTag) findAncestorWithClass((Tag)this, transactionClass); 

        if (tt == null) {
            log.warn("No transaction tag found, no " + actionName() + " could be done");
        } else {
            Transaction trans = (Transaction) tt.getCloudVar();
            doAction(trans);
            if (tt.getId() != null) {
                tt.unRegister(tt.getId());
            }
        }
        return SKIP_BODY;    
    }    
}
