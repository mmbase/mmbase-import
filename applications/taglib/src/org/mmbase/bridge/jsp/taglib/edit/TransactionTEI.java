/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;
import org.mmbase.bridge.jsp.taglib.CloudTEI;

/**
* The TEI class belonging to the TransactionTag
*
* @author Michiel Meeuwissen
**/
public class TransactionTEI extends CloudTEI {
    public TransactionTEI() { 
        super(); 
    }
    protected String defaultCloudVar() {
        return TransactionTag.DEFAULT_TRANS_JSPVAR;
    }
    protected String cloudType() {
        return "org.mmbase.bridge.Transaction";
    }

}
