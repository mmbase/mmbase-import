/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.bridge.jsp.taglib.*;

import javax.servlet.jsp.PageContext;
import org.springframework.mock.web.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;



/**
 * @version $Id$
 */

public  class CollectorBackingTest {



    protected CollectorBacking getInstance(PageContext pageContext) throws Exception {
        ContextTag context = new ContextTag();
        context.setPageContext(pageContext);
        context.doStartTag();
        context.setId("TEST");


        return new CollectorBacking(pageContext, context.getContextContainer());
    }



    @Test
    public void basic() throws Exception {
        PageContext pageContext = new MockPageContext();

        CollectorBacking backing = getInstance(pageContext);

        backing.put("a", "A");
        assertEquals("A", pageContext.getAttribute("a"));
        //backing.doAfterBody();

        backing.put("b", "B");
        assertEquals("B", pageContext.getAttribute("b"));

        backing.put("a", "AA");
        assertEquals("AA", pageContext.getAttribute("a"));

        backing.pullPageContext(pageContext);


        assertEquals("AA", pageContext.getAttribute("a"));
        assertEquals("B", backing.parent.getObject("b"));


    }

    @Test
    public  void nested() {
    }

    //@Test
    public void reset() throws Exception {
        PageContext pageContext = new MockPageContext();
        pageContext.setAttribute("a", "X");
        ContextProvider parent = new MockContextProvider(pageContext);
        ContextCollector collector = new ContextCollector(parent);
        //reset(collector.createBacking(pageContext), pageContext);
        assertEquals("AA",  parent.getContextContainer().get("a"));
        assertEquals("AA",  pageContext.getAttribute("a"));

    }




}
