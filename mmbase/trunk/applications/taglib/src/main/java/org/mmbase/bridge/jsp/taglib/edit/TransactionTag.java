/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import java.io.IOException;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Transaction;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.CloudProvider;
import org.mmbase.bridge.jsp.taglib.CloudTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * Creates a new Transaction.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class TransactionTag extends CloudReferrerTag implements CloudProvider {

    private static final Logger log = Logging.getLoggerInstance(TransactionTag.class);
    public static final String KEY = "org.mmbase.transaction";
    public static final int SCOPE = PageContext.REQUEST_SCOPE;


    protected Transaction transaction;
    protected Attribute commit = Attribute.NULL;
    protected Attribute name   = Attribute.NULL;
    protected String jspvar = null;

    protected Object prevCloud;
    protected Object prevTransaction;

    public void setCommitonclose(String c) throws JspTagException {
        commit = getAttribute(c);
    }

    /**
     * @since 1.7.1 this method shadows/implements the methods below.
     *
     * @see org.mmbase.bridge.jsp.taglib.CloudReferrerTag#getCloudVar()
     * @see org.mmbase.bridge.jsp.taglib.CloudProvider#getCloudVar()
     */
    @Override
    public Cloud getCloudVar() throws JspTagException {
        return transaction;
    }

    public void setName(String s) throws JspTagException {
        name = getAttribute(s);
    }

    public void setJspvar(String jv) {
        jspvar = jv;
    }

    protected String getName() throws JspTagException {
        return (String) name.getValue(this);
    }

    /**
     *  Creates the transaction.
     */
    public int doStartTag() throws JspTagException{
        if (log.isDebugEnabled()) {
            log.debug("value of commit: " + commit);
        }
        transaction = null;
        boolean foundThis = false;
        if (getId() != null) { // look it up from session
            log.debug("looking up transaction in context");
            try {
                Object o = getObject(getId());
                if (o == this) {
                    foundThis = true;
                } else if (o instanceof Transaction) {
                    transaction = (Transaction) getObject(getId());
                } else if (o instanceof TransactionTag) {
                    transaction = ((TransactionTag)o).transaction;
                } else {
                    throw new JspTagException("The object with id " + getId() + " is not a transaction, but " + (o == null ? "NULL" : "a " + o.getClass()));
                }
                if (log.isDebugEnabled()) {
                    log.debug("found " + transaction);
                }
            } catch (JspTagException e) { }
        }
        if (transaction == null) { // not found in context
            String n = getName();
            if (name == null) {
                throw new JspTagException("Did not find transaction in context, and no name for transaction supplied");
            }
            transaction = super.getCloudVar().getTransaction(n);
            if (getId() != null && ! foundThis) { // put it in context
                log.debug("putting transaction in context");
                getContextProvider().getContextContainer().register(getId(), transaction);
            }
        }
        prevCloud = pageContext.getAttribute(CloudTag.KEY, CloudTag.SCOPE);
        prevTransaction = pageContext.getAttribute(TransactionTag.KEY, TransactionTag.SCOPE);
        if (prevCloud != null) {
            log.debug("Found previous cloud " + prevCloud);
       }
        pageContext.setAttribute(CloudTag.KEY, transaction, CloudTag. SCOPE);
        pageContext.setAttribute(TransactionTag.KEY, transaction, TransactionTag. SCOPE);

        if (jspvar != null) {
            pageContext.setAttribute(jspvar, transaction);
        }
        return EVAL_BODY;
    }

    protected boolean getDefaultCommit() {
        return true;
    }

    public int doEndTag() throws JspTagException {
        if (commit.getBoolean(this, getDefaultCommit())) {
            transaction.commit();
            if (getId() != null) {
                getContextProvider().getContextContainer().unRegister(getId());
            }
        }
        pageContext.setAttribute(CloudTag.KEY, prevCloud, CloudTag.SCOPE);
        pageContext.setAttribute(TransactionTag.KEY, prevTransaction, TransactionTag.SCOPE);
        transaction = null;
        prevCloud = null;
        prevTransaction = null;
        return super.doEndTag();
    }
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new JspTagException(ioe.toString());
                }
            }
        }
        return SKIP_BODY;
    }

}

