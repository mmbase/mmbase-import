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

public  class ContextCollectorTest {


    @Test
    public void basic() throws Exception {
        PageContext pageContext = new MockPageContext();

        ContextTag context = new ContextTag();
        context.setPageContext(pageContext);
        context.doStartTag();
        assertNotNull(context.getContextContainer());

        context.getContextContainer().register("x", "X");


        {
            ContextCollector collector = new ContextCollector(context);

            collector.register("a", "A");
            assertEquals("A", collector.get("a"));
            assertEquals("A", pageContext.getAttribute("a"));

            try {
                collector.register("x", "Y");
                fail("Should already be registered");
            } catch (Exception e) {
            }

            collector.doAfterBody(true);

            collector.register("a", "B", false);

            assertEquals("B", collector.get("a"));
            assertEquals("B", pageContext.getAttribute("a"));


            collector.doAfterBody(true);

            collector.register("a", "C", false);

            assertEquals("C", collector.get("a"));
            assertEquals("C", pageContext.getAttribute("a"));

            collector.doAfterBody(false);
            collector.release(pageContext, context.getContextContainer());
        }

        assertEquals("C", context.getContextContainer().get("a"));
        assertEquals("C", pageContext.getAttribute("a"));

        assertEquals("X", context.getContextContainer().get("x"));
        assertEquals("X", pageContext.getAttribute("x"));


    }
    @Test
    public void mmb1702() {

    }

}
