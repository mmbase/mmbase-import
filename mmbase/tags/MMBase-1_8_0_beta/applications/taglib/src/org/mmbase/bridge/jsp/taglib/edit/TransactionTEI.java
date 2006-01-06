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
 * @version $Id: TransactionTEI.java,v 1.4 2003-06-06 10:03:22 pierre Exp $ 
 */

public class TransactionTEI extends CloudTEI {
    protected String cloudType() {
        return "org.mmbase.bridge.Transaction";
    }

}
