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

        ContextProvider provider = new MockContextProvider(pageContext);
        ContextCollector collector = new ContextCollector(context);

        collector.register("a", "A");
        assertEquals("A", collector.get("a"));
        assertEquals("A", pageContext.getAttribute("a"));

        collector.doAfterBody();

        collector.register("a", "B", false);

        assertEquals("B", collector.get("a"));
        assertEquals("B", pageContext.getAttribute("a"));


        collector.doAfterBody();

        collector.register("a", "C", false);

        assertEquals("C", collector.get("a"));
        assertEquals("C", pageContext.getAttribute("a"));

        collector.doAfterBody();
        collector.release(pageContext, provider.getContextContainer());

        assertEquals("C", pageContext.getAttribute("a"));


    }
    @Test
    public void mmb1702() {

    }

}
