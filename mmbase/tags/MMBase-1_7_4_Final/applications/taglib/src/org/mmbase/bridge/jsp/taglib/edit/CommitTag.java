/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.Transaction;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This tag can be used inside a transaction tag, to commit it. It also
 * serves as a baseclass for e.g. CancelTag
 *
 * @author Michiel Meeuwissen 
 * @version $Id: CommitTag.java,v 1.16 2003-08-27 21:33:39 michiel Exp $
 */

public class CommitTag extends CloudReferrerTag { 
    // perhaps it would be nicer to extend CloudReferrer to TransactionReferrer first.

    private static final Logger log = Logging.getLoggerInstance(CommitTag.class.getName());

    private Attribute transaction = Attribute.NULL;
    public void setTransaction(String t) throws JspTagException {
        transaction = getAttribute(t);
    }

    protected void doAction(Transaction t) {
        t.commit();
    }

    public int doStartTag() throws JspTagException{
        // find the parent transaction:        
        TransactionTag tt = (TransactionTag)  findParentTag(TransactionTag.class, (String) transaction.getValue(this), true);
        Transaction trans = (Transaction) tt.getCloudVar();
        doAction(trans);
        /*
          why should we do this??
        if (tt.getId() != null) {
            getContextProvider().getContextContainer().unRegister(tt.getId());
        }
        */
        return SKIP_BODY;    
    }    
}
