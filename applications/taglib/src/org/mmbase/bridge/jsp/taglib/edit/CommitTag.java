/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.Transaction;

/**
* This tag can be used inside a transaction tag, to commit it.
*
* @author Michiel Meeuwissen 
**/

public class CommitTag extends CloudReferrerTag { 
    // perhaps it would be nicer to extend CloudReferrer to TransactionReferrer first.

    void setTransaction(String t) {
        setCloud(t);
    }

    public int doStartTag() throws JspTagException{
        // find the parent transaction:
        Transaction trans = (Transaction) getCloudProviderVar();
        trans.commit();
        return SKIP_BODY;    
    }    
}
