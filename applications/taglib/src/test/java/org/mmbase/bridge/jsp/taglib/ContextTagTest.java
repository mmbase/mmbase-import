/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import org.springframework.mock.web.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @version $Id: BasicBacking.java 36504 2009-06-30 12:39:45Z michiel $
 */

public  class ContextTagTest {


    @Test
    public void basic() throws Exception {
        PageContext pageContext = new MockPageContext();
        ContextTag tag = new ContextTag();
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.register("a", "A");
        try {
            tag.register("a", "B");
            fail("Should have thrown exception");
        } catch (JspTagException te) {
            // ok
        }
        assertEquals("A", pageContext.getAttribute("a"));
        tag.doAfterBody();
        tag.doEndTag();
    }


}
