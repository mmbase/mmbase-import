/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;
import org.mmbase.bridge.jsp.taglib.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.jsp.taglib.util.*;

/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: AbstractTreeReferrerListTag.java,v 1.1 2003-12-24 00:31:37 michiel Exp $
 */
abstract class AbstractTreeReferrerListTag extends TreeReferrerTag implements ListProvider, DepthProvider {

    protected int depth; 
    protected int index;
    protected TreeProvider tree;


    public int getIndex() {
        return index;
    }
    public int getIndexOffset() {
        return 0;
    }
    public boolean isChanged() {
        return true;
    }
    public Object getCurrent() {
        return new Integer(depth);
    }
    public void remove() {
        // not supported
    }

    public int getDepth() {
        return depth;
    }

    protected   ContextCollector  collector;

    // ContextProvider implementation

    public ContextContainer getContextContainer() throws JspTagException {
        return collector.getContextContainer();
    }


    protected final void doStartTagHelper() throws JspTagException {
        collector = new ContextCollector(getContextProvider().getContextContainer());
        tree = findTreeProvider();
        index = 0;
    }




}

