/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;
import org.springframework.mock.web.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;



/**
 * @version $Id: BasicBacking.java 36504 2009-06-30 12:39:45Z michiel $
 */

public  class BasicBackingTest {


    @Test
    public void basic() {
        PageContext pageContext = new MockPageContext();
        BasicBacking backing = new BasicBacking(pageContext, false);
        backing.put("a", "A");
        assertEquals("A", backing.get("a"));
        assertEquals("A", pageContext.getAttribute("a"));

        backing.release();

        assertEquals(null, pageContext.getAttribute("a"));

    }

}
