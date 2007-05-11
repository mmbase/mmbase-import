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
 * @version $Id: TreeProvider.java,v 1.2 2003-12-24 00:31:39 michiel Exp $
 */
public interface TreeProvider extends NodeProvider, ListProvider, DepthProvider {

    public int getPreviousDepth();

    public int getNextDepth();

    public Stack getShrinkStack();

}

