/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;
import java.util.*;
import java.io.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.*;

/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: ShrinksTag.java,v 1.1 2003-12-18 23:05:45 michiel Exp $
 */
public class ShrinksTag extends TreeReferrerTag implements ListProvider {


    private int size;
    private int index;
    private TreeProvider tree;

    public int size() {
        return size;
    }
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
        return new Integer(getIndex());
    }
    public void remove() {
    }

    private   ContextCollector  collector;

    // ContextProvider implementation

    public ContextContainer getContextContainer() throws JspTagException {
        return collector.getContextContainer();
    }



    public int doStartTag() throws JspException {
        collector = new ContextCollector(getContextProvider().getContextContainer());
        tree = findTreeProvider();
        size = tree.getDepth() - tree.getNextDepth();
        index = tree.getDepth();
        if (index > tree.getNextDepth()) {
            index--;
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspTagException {

        if (index > tree.getNextDepth()) {
            index--;
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
            return SKIP_BODY;
        }

    }


}

