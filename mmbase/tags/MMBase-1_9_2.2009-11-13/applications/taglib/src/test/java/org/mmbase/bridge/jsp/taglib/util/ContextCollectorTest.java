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
 * @version $Id: BasicBacking.java 36504 2009-06-30 12:39:45Z michiel $
 */

public  class ContextCollectorTest {


    @Test
    public void basic() throws Exception {
        PageContext pageContext = new MockPageContext();
        ContextCollector collector = new ContextCollector(new MockContextProvider(pageContext));

        collector.put("a", "A");
        assertEquals("A", collector.get("a"));
        assertEquals("A", pageContext.getAttribute("a"));


    }

}
