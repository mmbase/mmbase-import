/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import java.util.HashMap;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Transaction;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
*
* Creates a new Transaction.
*
* @author Michiel Meeuwissen
*
**/
public class TransactionTag extends SequenceTag implements CloudProvider {

    private static Logger log = Logging.getLoggerInstance(TransactionTag.class.getName());
    private Transaction transaction;
      
    private static boolean commit = true;
    public void setCommitOnClose(boolean c) {
        commit = c;
    }
    
    public Cloud getCloudVar() throws JspTagException {
        return transaction;
    }  

    /* if you don't extend from Sequence:
    public void  registerNode(String id, Node n) {
    }
    
    public Node getNode(String id) throws JspTagException {
        throw new JspTagException("Cannot get Nodes directly from Transaction (use a group tag)");
    }
    */

    /**
    *  Creates the transaction.
    */
    public int doStartTag() throws JspTagException{
        transaction = findCloudProvider().getCloudVar().getTransaction(getId());
        return EVAL_BODY_TAG;
    }
  

    public int doAfterBody() throws JspTagException {
        if (commit) {
            ((Transaction) getCloudVar()).commit();
        }
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());            
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

}

