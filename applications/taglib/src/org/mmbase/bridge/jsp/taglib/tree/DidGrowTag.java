/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: DidGrowTag.java,v 1.1 2003-12-19 12:55:09 michiel Exp $
 */
public class DidGrowTag extends WillShrinkTag { 

    protected void findStartEndAndDirection() {
        startIndex = tree.getPreviousDepth();
        endIndex   = tree.getDepth();
        direction  = +1;
    }


}

