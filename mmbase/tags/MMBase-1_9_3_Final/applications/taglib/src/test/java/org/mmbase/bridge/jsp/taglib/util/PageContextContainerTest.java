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

public  class PageContextContainerTest {


    @Test
    public void basic() throws Exception {

        PageContext pageContext = new MockPageContext();

        PageContextContainer container = new PageContextContainer(pageContext);

        container.register("a", "A");
        assertEquals("A", container.get("a"));
        assertEquals("A", pageContext.getAttribute("a"));
        try {
            container.register("a", "AA");
            fail("Should throw exception");
        } catch (Exception e) {
            // ok
        }

        assertTrue(container.isRegistered("a"));

        container.reregister("a", "AA");

        assertEquals("AA", container.get("a"));
        assertEquals("AA", pageContext.getAttribute("a"));


    }

}
