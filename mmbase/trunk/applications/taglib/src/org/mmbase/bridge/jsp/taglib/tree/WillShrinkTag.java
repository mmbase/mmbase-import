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
import org.mmbase.bridge.jsp.taglib.Writer;
import org.mmbase.bridge.jsp.taglib.util.*;

/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: WillShrinkTag.java,v 1.1 2003-12-19 12:55:10 michiel Exp $
 */
public class WillShrinkTag extends TreeReferrerTag implements ListProvider, Writer {

    private Attribute inverse = Attribute.NULL;


    public void setInverse(String i) throws JspTagException {
        inverse = getAttribute(i);
    }

    protected int startIndex;
    protected int index;
    protected int endIndex;
    protected int direction;

    protected TreeProvider tree;

    public int size() {
        return Math.abs(endIndex - startIndex);
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
        return new Integer(index);
    }
    public void remove() {
    }

    private   ContextCollector  collector;

    // ContextProvider implementation

    public ContextContainer getContextContainer() throws JspTagException {
        return collector.getContextContainer();
    }


    protected void findStartEndAndDirection() {
        startIndex = tree.getDepth();
        endIndex   = tree.getNextDepth();
        direction  = -1;
    }


    public int doStartTag() throws JspException {
        collector = new ContextCollector(getContextProvider().getContextContainer());
        tree = findTreeProvider();

        findStartEndAndDirection();
        index     = startIndex;

        if (inverse.getBoolean(this, false)) {
            boolean b = direction * index >= direction * endIndex;
            if (b) {
                endIndex = startIndex + direction;
            } else {
                endIndex = startIndex;
            }
        }

        if (direction * index < direction * endIndex) {
            index += direction;
            doInitBody();
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    public void doInitBody() throws JspTagException {
        helper.setValue("" + index);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
    }

    public int doAfterBody() throws JspException {
        
        helper.doAfterBody();
        collector.doAfterBody();

        if (direction * index < direction * endIndex) {
            index += direction;
            doInitBody();
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

