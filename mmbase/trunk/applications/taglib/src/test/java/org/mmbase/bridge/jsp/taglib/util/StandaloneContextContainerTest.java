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

public  class StandaloneContextContainerTest {


    @Test
    public void basic() throws Exception {

        PageContext pageContext1 = new MockPageContext();
        PageContextContainer parent1 = new PageContextContainer(pageContext1);

        PageContext pageContext2 = new MockPageContext();
        PageContextContainer parent2 = new PageContextContainer(pageContext2);

        StandaloneContextContainer container = new StandaloneContextContainer(pageContext1, "test", parent1);

        container.register("a", "A");
        assertEquals("A", container.get("a"));
        assertEquals("A", pageContext1.getAttribute("a"));
        try {
            container.register("a", "AA");
            fail("Should throw exception");
        } catch (Exception e) {
            // ok
        }

        assertTrue(container.isRegistered("a"));

        container.reregister("a", "AA");

        assertEquals("AA", container.get("a"));
        assertEquals("AA", pageContext1.getAttribute("a"));

        container.release(pageContext1, parent1);
        container.setParent(pageContext2, parent2);

        assertEquals("AA", container.get("a"));
        assertEquals("AA", pageContext1.getAttribute("a"));
        assertEquals("AA", pageContext2.getAttribute("a"));





    }

}
