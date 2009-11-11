/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import org.springframework.mock.web.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @version $Id$
 */

public  class StringListTagTest {


    @Test
    public void basic() throws Exception {
        final PageContext pageContext = new MockPageContext();


        ContextTag context = new ContextTag();
        context.setPageContext(pageContext);
        context.doStartTag();
        context.setId("TEST");
        {
            WriteTag i = new WriteTag();
            i.setPageContext(pageContext);
            i.setParent(context);
            i.setId("list");
            i.setVartype("list");
            i.setWrite("false"); // getOut not supported in MockPageContext
            i.setValue("A,B,C");
            i.doStartTag();
            i.doEndTag();
        }
        {
            WriteTag i = new WriteTag();
            i.setPageContext(pageContext);
            i.setParent(context);
            i.setId("foo");
            i.setWrite("false"); // getOut not supported in MockPageContext
            i.setValue("bar");
            i.doStartTag();
            i.doEndTag();
        }



        List<String> list = (List<String>) pageContext.getAttribute("list");
        assertEquals(3, list.size());
        assertEquals("A", list.get(0));



        StringListTag tag = new StringListTag();
        tag.setParent(context);
        tag.setReferid("list");
        tag.setPageContext(pageContext);

        int it = tag.doStartTag();
        tag.setBodyContent(null);

        int index = 0;

        while (it == 2) {
            tag.doInitBody();

            {
                WriteTag i = new WriteTag();
                i.setPageContext(pageContext);
                i.setParent(tag);
                assertEquals(tag, i.getContextProvider());
                i.setId("foo");
                i.setWrite("false");
                i.setValue("bla" + (++index));
                i.setReset(true);
                i.doStartTag();
                i.doAfterBody();
                i.doEndTag();
            }

            assertEquals("bla" + index, pageContext.getAttribute("foo"));
            assertEquals("bla" + index, context.getObject("foo"));
            it = tag.doAfterBody();
            assertEquals(null, pageContext.getAttribute("foo"));
        }
        tag.doEndTag();

        assertEquals("bla3", pageContext.getAttribute("foo"));
        assertEquals("bla3", context.getObject("foo"));  // MMB-1702



        context.doEndTag();

    }


}
