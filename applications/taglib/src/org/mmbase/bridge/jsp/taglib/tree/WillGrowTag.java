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
 * @version $Id: WillGrowTag.java,v 1.1 2003-12-19 12:55:10 michiel Exp $
 */
public class WillGrowTag extends WillShrinkTag { 

    protected void findStartEndAndDirection() {
        startIndex = tree.getDepth();
        endIndex   = tree.getNextDepth();
        direction  = +1;
    }


}

