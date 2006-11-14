/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import org.mmbase.bridge.jsp.taglib.*;

import java.util.Stack;

/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: TreeProvider.java,v 1.3 2006-11-14 22:53:49 michiel Exp $
 */
public interface TreeProvider extends NodeProvider, ListProvider, DepthProvider {

    public int getPreviousDepth();

    public int getNextDepth();

    public Stack<ShrinkTag.Entry> getShrinkStack();

}

