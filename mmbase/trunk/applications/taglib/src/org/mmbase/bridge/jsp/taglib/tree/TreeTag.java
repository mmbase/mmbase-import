/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;


import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.*;

import java.io.IOException;
import java.util.Stack;

import org.mmbase.bridge.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.debug.TimerTag;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.containers.*;

import org.mmbase.util.logging.*;


/**
 * Implements mm:tree. Which works about like this
<pre>
 &lt;mm:relatednodescontainer type="object" searchdirs="destination"&gt;
  &lt;mm:tree type="object" searchdir="destination" maxdepth="8"&gt;
    &lt;mm:grow&gt;
      &lt;ul class="&lt;mm:depth" /&gt;"&gt;&lt;mm:onshrink&gt;&lt;/ul&gt;&lt;/mm:onshrink&gt;
    &lt;/mm:grow&gt;
    &lt;li&gt;&lt;mm:depth /&gt;: &lt;mm:nodeinfo type="guitype" /&gt;: &lt;mm:field name="number" /&gt; &lt;mm:function name="gui" escape="none" /&gt;

    &lt;mm:onshrink&gt;&lt;/li&gt;&lt;/mm:onshrink&gt;

    &lt;mm:shrink /&gt;

    &lt;mm:last&gt;SIZE : &lt;mm:size /&gt;&lt;/mm:last&gt;

  &lt;/mm:tree&gt;
&lt;/mm:relatednodescontainer&gt;
</pre>
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: TreeTag.java,v 1.14 2005-12-05 17:21:17 michiel Exp $
 */
public class TreeTag extends AbstractNodeProviderTag implements TreeProvider, QueryContainerReferrer  {
    private static final Logger log = Logging.getLoggerInstance(TreeTag.class);

    private TreeList     tree;
    private TreeIterator iterator;

    private Stack        shrinkStack;

    private int previousDepth = -1;
    private int depth = 0;
    private int nextDepth = 1;
    private int initialDepth;
    private int index;

    private Node nextNode;

    /**
     * Lists do implement ContextProvider
     */
    private   ContextCollector  collector;
    protected int timerHandle = -1;


    protected Attribute container   = Attribute.NULL;
    protected Attribute nodeManager = Attribute.NULL;
    protected Attribute role        = Attribute.NULL;
    protected Attribute searchDir   = Attribute.NULL;
    protected Attribute maxDepth    = Attribute.NULL;
    protected Attribute orderby     = Attribute.NULL;
    protected Attribute directions  = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setType(String n) throws JspTagException {
        nodeManager = getAttribute(n);
    }

    public void setRole(String r) throws JspTagException {
        role = getAttribute(r);
    }
    public void setSearchdir(String sd) throws JspTagException {
        searchDir = getAttribute(sd);
    }
    public void setMaxdepth(String md) throws JspTagException {
        maxDepth = getAttribute(md);
    }

    /**
     * @since MMBase 1.7.1
     */
    public void setOrderby(String o) throws JspTagException {
        orderby = getAttribute(o);
    }
    /**
     * @since MMBase 1.7.1
     */
    public void setDirections(String d) throws JspTagException {
        directions = getAttribute(d);
    }


    public Stack getShrinkStack() {
        return shrinkStack;
    }


    // ContextProvider implementation
    public ContextContainer getContextContainer() throws JspTagException {
        if (collector == null) return getContextProvider().getContextContainer(); // to make sure old-style implemntation work (which do not initialize container)
        return collector.getContextContainer();
    }


    public int size() {
        return tree.size();
    }
    public int getIndex() {
        return index;
    }

    public int getIndexOffset() {
        return 1;
    }
    public boolean isChanged() {
        return true;
    }
    
    public Object getCurrent() {
        return getNodeVar();
    }

    public void remove() {
        iterator.remove();
    }

    public int getPreviousDepth() {
        return previousDepth;
    }
    public int getDepth() {
        return depth;
    }
    public int getNextDepth() {
        return nextDepth;
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        log.debug("starttag");
        shrinkStack = new Stack();
        collector = new ContextCollector(getContextProvider());

        // serve parent timer tag:
        TagSupport t = findParentTag(TimerTag.class, null, false);
        if (t != null) {
            timerHandle = ((TimerTag)t).startTimer(getId(), getClass().getName());
        }

        if (getReferid() != null) {
            Object o =  getObject(getReferid());
            if (! (o instanceof TreeList)) {
                throw new JspTagException("Context variable " + getReferid() + " is not a TreeList");
            }
            if (nodeManager != Attribute.NULL) {
                throw new JspTagException("'type' attribute does not make sense with 'referid' attribute");
            }
            if (role != Attribute.NULL) {
                throw new JspTagException("'role' attribute does not make sense with 'referid' attribute");
            }
            if (searchDir!= Attribute.NULL) {
                throw new JspTagException("'searchdir' attribute does not make sense with 'referid' attribute");
            }

            if (getReferid().equals(getId())) { // in such a case, don't whine
                getContextProvider().getContextContainer().unRegister(getId());
            }
            tree = (TreeList) o;
        } else {
            tree = null;
            if (parentNodeId == Attribute.NULL) {
                TreeContainerTag c = (TreeContainerTag) findParentTag(TreeContainerTag.class, (String) container.getValue(this), false);
                if (c != null) {
                    tree = c.getTree();
                    if (! "".equals(maxDepth.getString(this)) && tree instanceof GrowingTreeList) {
                        ((GrowingTreeList)  tree).setMaxDepth(maxDepth.getInt(this, 5));
                    }
                }
            }
            if (tree == null) {
                NodeQuery query = TreeContainerTag.getStartQuery(this, container, parentNodeId);
                tree = new GrowingTreeList(query, maxDepth.getInt(this, 5),
                                           getCloudVar().getNodeManager(nodeManager.getString(this)), 
                                           role.getString(this), 
                                           searchDir.getString(this));
                Query template = ((GrowingTreeList) tree).getTemplate();
                if (orderby != Attribute.NULL) {
                    Queries.addSortOrders(template, (String) orderby.getValue(this), (String) directions.getValue(this));
                }
            }

        }
        iterator = tree.treeIterator();
        
        // returnList is known, now we can serve parent formatter tag
        FormatterTag f = (FormatterTag) findParentTag(FormatterTag.class, null, false);
        if (f != null && f.wantXML()) {
            f.getGenerator().add(tree);
        }


        if (iterator.hasNext()) {
            index = 0;
            previousDepth = iterator.currentDepth();            
            initialDepth  = previousDepth;
            Node node     = iterator.nextNode();            
            setNodeVar(node);
            fillVars();
            depth         = iterator.currentDepth();

            if (iterator.hasNext()) {
                nextNode  = iterator.nextNode();
                nextDepth = iterator.currentDepth();
                log.debug("hasnext " + nextDepth);
            } else {
                nextNode  = null;
                nextDepth = initialDepth;
                log.debug("no next " + nextDepth);
                    
            }
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }


    }
    public void doInitBody() throws JspTagException {
        log.debug("initbody");
    }


    public int doAfterBody() throws JspTagException {
        log.debug("afterbody");
        super.doAfterBody();
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }
        
        collector.doAfterBody();

        if (nextNode != null) {
            log.debug("using next-node");
            setNodeVar(nextNode);
            fillVars();
            previousDepth = depth;
            depth         = nextDepth;
            
            if (iterator.hasNext()) {
                nextNode  = iterator.nextNode();
                nextDepth = iterator.currentDepth();
                log.debug("has next " + nextDepth);
            } else {
                nextNode  = null;
                nextDepth = initialDepth;
                log.debug("no next " + nextDepth);
            }
            index ++;
            return EVAL_BODY_AGAIN;
        } else {
            log.debug("writing body");
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

    public int doEndTag() throws JspTagException {
        log.debug("endtag");
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), tree);
        }
        TagSupport t = findParentTag(TimerTag.class, null, false);
        if (t != null) {
            ((TimerTag)t).haltTimer(timerHandle);
        }
        // dereference for gc
        tree = null;
        iterator = null;
        shrinkStack = null;
        nextNode = null;
        collector = null;
        return super.doEndTag();
    }

    public javax.servlet.jsp.jstl.core.LoopTagStatus getLoopStatus() {
        return new ListProviderLoopTagStatus(this);
    }




}

