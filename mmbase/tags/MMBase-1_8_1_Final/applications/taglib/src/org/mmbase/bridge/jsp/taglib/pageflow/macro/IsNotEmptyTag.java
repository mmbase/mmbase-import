/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow.macro;

/**
 * Checks if the parent writer is not empty.
 * 
 * @author Michiel Meeuwissen 
 * @version $Id: IsNotEmptyTag.java,v 1.3 2003-06-06 10:03:30 pierre Exp $
 */

public class IsNotEmptyTag extends IsEmptyTag  {
    protected boolean doCompare(Comparable compare1, Comparable compare2) {
        return ! super.doCompare(compare1, compare2);
    }
}
