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

public  class BasicBackingTest {



    /**
     * Tests a backing, using given pageContext.
     */
    static void basic(BasicBacking backing, PageContext pageContext) {

        // - put something in.   (a)
        // - overwrite it        (a->AA)
        // - put something else in (b)
        // - overwrite that too    (b->BB)
        // (the point is that one of those was already in the pageContext(.

        // - release.
        backing.put("a", "A");
        assertEquals("A", backing.get("a"));
        if (pageContext != null) {
            assertEquals("A", pageContext.getAttribute("a"));
        }

        backing.put("a", "AA");
        assertEquals("AA", backing.get("a"));
        if (pageContext != null) {
            assertEquals("AA", pageContext.getAttribute("a"));
        }

        backing.put("b", "B");

        assertEquals("B", backing.get("b"));
        if (pageContext != null) {
            assertEquals("B", pageContext.getAttribute("b"));
        }

        backing.put("b", "BB");

        assertEquals("BB", backing.get("b"));
        if (pageContext != null) {
            assertEquals("BB", pageContext.getAttribute("b"));
        }

        backing.release();


    }

    static void reset(BasicBacking backing, PageContext pageContext) {
        pageContext.setAttribute("a", "X");

        backing.put("a", "A", true);
        assertEquals("A", backing.get("a"));
        assertEquals("A", pageContext.getAttribute("a"));

        backing.put("a", "AA", true);
        assertEquals("AA", backing.get("a"));
        assertEquals("AA", pageContext.getAttribute("a"));

        backing.release();

        assertEquals("AA", pageContext.getAttribute("a"));

    }

    @Test
    public void mirrorPut() {
        PageContext pageContext = new MockPageContext();
        pageContext.setAttribute("a", "X");
        pageContext.setAttribute("b", "Y");

        BasicBacking backing = new BasicBacking(pageContext, false);

        backing.mirrorPut("a", "A", false);
        backing.mirrorPut("b", "B", true);
        backing.mirrorPut("c", "C", false);

        assertEquals("A", pageContext.getAttribute("a"));
        assertEquals("B", pageContext.getAttribute("b"));
        assertEquals("C", pageContext.getAttribute("c"));

        assertEquals("X",  backing.pageContextValues.get("a"));
        assertEquals("B",  backing.pageContextValues.get("b"));
        assertEquals(null, backing.pageContextValues.get("c"));

        backing.release();

        assertEquals("X", pageContext.getAttribute("a"));
        assertEquals("B", pageContext.getAttribute("b"));
        assertEquals("C", pageContext.getAttribute("c"));


    }

    @Test
    public void basic() {
        // put something on the pageContext
        // use a backing
        // check the pageContext

        PageContext pageContext = new MockPageContext();
        pageContext.setAttribute("a", "X");
        basic(new BasicBacking(pageContext, false), pageContext);
        assertEquals("BB", pageContext.getAttribute("b"));  // should have the last value set in the backing
        assertEquals("X", pageContext.getAttribute("a"));   // should have the original value

    }

    @Test
    public void reset() {
        // put something on the pageContext
        // use a backing, in which we _reset_ that value
        // check the pageContext

        PageContext pageContext = new MockPageContext();
        pageContext.setAttribute("a", "X");
        reset(new BasicBacking(pageContext, false), pageContext);
        assertEquals("AA", pageContext.getAttribute("a")); // Since the value was _reset_ it should not have the original value, but the last value set in the backing.
    }

    @Test
    public void basicCollector() throws Exception {
        PageContext pageContext = new MockPageContext();
        pageContext.setAttribute("a", "X");
        ContextProvider parent = new MockContextProvider(pageContext);
        ContextCollector collector = new ContextCollector(parent);

        basic(collector.createBacking(pageContext), pageContext);

        //
        assertEquals("BB", pageContext.getAttribute("b"));
        assertEquals("X",  pageContext.getAttribute("a"));

        // collectors are 'transparent' to parent
        assertEquals("BB", parent.getContextContainer().get("b")); // should have the last value set in the backing
        assertEquals("X",  parent.getContextContainer().get("a")); // should have the original value





    }

    @Test
    public void resetCollector() throws Exception {
        PageContext pageContext = new MockPageContext();
        pageContext.setAttribute("a", "X");
        ContextProvider parent = new MockContextProvider(pageContext);
        ContextCollector collector = new ContextCollector(parent);
        reset(collector.createBacking(pageContext), pageContext);
        assertEquals("AA",  parent.getContextContainer().get("a"));
        assertEquals("AA", pageContext.getAttribute("a"));

    }




}
