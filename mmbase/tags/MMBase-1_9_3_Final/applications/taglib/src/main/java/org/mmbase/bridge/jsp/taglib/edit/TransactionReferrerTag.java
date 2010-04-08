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

import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: CommitTag.java 35335 2009-05-21 08:14:41Z michiel $
 * @since MMBase-1.9.2
 */

public class TransactionReferrerTag extends CloudReferrerTag {
    private static final Logger log = Logging.getLoggerInstance(TransactionReferrerTag.class);

    private Attribute transaction = Attribute.NULL;
    private Attribute refresh     = Attribute.NULL;
    public void setTransaction(String t) throws JspTagException {
        transaction = getAttribute(t, true);
    }

    protected void doAction(Transaction t) {
    }

    /**
     * @since MMBase-1.9.2
     */
    public void setRefresh(String r) throws JspTagException {
        refresh = getAttribute(r, true);
    }


    /**
     * @since MMBase-1.9.2
     */
    protected TransactionTag findTransactionTag(boolean throwexception) throws JspTagException {
        return findParentTag(TransactionTag.class, transaction == Attribute.NULL ? null : transaction.getString(this), throwexception);
    }
    /**
     * @since MMBase-1.8.5
     */
    protected Transaction getTransactionVar() throws JspTagException {
        // find the parent transaction:
        TransactionTag tt = findTransactionTag(false);
        if (tt != null) {
            log.debug("Found transactiontag " + tt);
            return (Transaction) tt.getCloudVar();
        }
        Transaction t = (Transaction) pageContext.getAttribute(TransactionTag.KEY, TransactionTag.SCOPE);
        if (t != null) {
            log.debug("Found transaction " + t);
            return t;
        }
        throw new JspTagException("Could not find parent transaction provider");
    }


    @Override
    public int doStartTag() throws JspTagException{
        Transaction trans = getTransactionVar();
        log.debug("Found transaction " + trans);
        doAction(trans);
        if (refresh.getBoolean(this, false)) {
            findTransactionTag(true).refreshTransaction();
        }
        return SKIP_BODY;
    }
}
