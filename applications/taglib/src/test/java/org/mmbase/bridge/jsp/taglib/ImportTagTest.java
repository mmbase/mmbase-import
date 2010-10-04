/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.*;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.http.*;
import org.springframework.mock.web.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @version $Id$
 */

public  class ImportTagTest {


    @Test
    public void basic() throws Exception {
        PageContext pageContext = new MockPageContext();
        BodyContent body = new MockBodyContent("A&B", (HttpServletResponse) pageContext.getResponse());
        ContextTag context = new ContextTag();
        context.setPageContext(pageContext);
        context.doStartTag();


        ImportTag i = new ImportTag();
        i.setPageContext(pageContext);
        i.setBodyContent(body);
        i.setParent(context);
        i.setId("a");
        i.doStartTag();
        i.doEndTag();
        assertEquals("A&B", pageContext.getAttribute("a"));

        context.doAfterBody();
        context.doEndTag();

    }



}
