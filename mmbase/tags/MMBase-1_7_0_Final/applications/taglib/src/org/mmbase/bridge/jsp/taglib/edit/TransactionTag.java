/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Transaction;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.CloudProvider;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * Creates a new Transaction.
 *
 * @author Michiel Meeuwissen
 * @version $Id: TransactionTag.java,v 1.19 2003-08-27 21:33:40 michiel Exp $ 
 */

public class TransactionTag extends CloudReferrerTag implements CloudProvider {

    private static final Logger log = Logging.getLoggerInstance(TransactionTag.class.getName());
    private Transaction transaction;
    private Attribute commit = Attribute.NULL;
    private Attribute name   = Attribute.NULL;
    private String jspvar = null;

    public void setCommitonclose(String c) throws JspTagException {
        commit = getAttribute(c);
    }

    public Cloud getCloudVar() throws JspTagException {
        return transaction;
    }

    public void setName(String s) throws JspTagException {
        name = getAttribute(s);
    }

    public void setJspvar(String jv) {
        jspvar = jv;
    }

    /**
     *  Creates the transaction.
     */
    public int doStartTag() throws JspTagException{
        log.debug("value of commit: " + commit);
        transaction = null;
        if (getId() != null) { // look it up from session
            log.debug("looking up transaction in context");
            try {
                transaction = (Transaction) getObject(getId());
                log.debug("found " + transaction);
            } catch (JspTagException e) { }
        }
        if (transaction == null) { // not found in context
            if (name == Attribute.NULL) {
                throw new JspTagException("Did not find transaction in context, and no name for transaction supplied");
            }
            transaction = findCloudProvider().getCloudVar().getTransaction(name.getString(this));
            if (getId() != null) { // put it in context
                log.debug("putting transaction in context");
                getContextProvider().getContextContainer().register(getId(), transaction);
            }
        }
        if (jspvar != null) {
            pageContext.setAttribute(jspvar, transaction);
        }
        return EVAL_BODY_BUFFERED;
    }


    public int doEndTag() throws JspTagException {
        if (commit.getBoolean(this, true)) {
            ((Transaction) getCloudVar()).commit();
            if (getId() != null) {
                getContextProvider().getContextContainer().unRegister(getId());
            }
        }
        return EVAL_PAGE;
    }
    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new JspTagException(ioe.toString());
            }
        }
        return SKIP_BODY;
    }

}

