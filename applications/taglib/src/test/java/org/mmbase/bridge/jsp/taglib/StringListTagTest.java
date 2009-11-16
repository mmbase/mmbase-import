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


        String fooValue = "bar";
        Import.tag(pageContext, context, "list", "A,B,C", "list");
        Import.tag(pageContext, context, "foo", fooValue);


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

        System.out.println("" + context.getContextContainer().getClass() + " " + context.getContextContainer());
        System.out.println("" + tag.getContextContainer().getClass() + " " + tag.getContextContainer());
        while (it == 2) {
            tag.doInitBody();

            assertEquals(fooValue, "" + context.getObject("foo")); // Should not give exception

            fooValue = "bla" + (++index);
            Import.tag(pageContext, tag, "foo", fooValue, true);

            assertEquals("bla" + index, pageContext.getAttribute("foo"));
            assertEquals("bla" + index, context.getObject("foo"));
            it = tag.doAfterBody();
        }
        tag.doEndTag();

        assertEquals("bla3", pageContext.getAttribute("foo"));
        assertEquals("bla3", context.getObject("foo"));  // MMB-1702



        context.doEndTag();

    }

    @Test
    public void nested() throws Exception  {
        final PageContext pageContext = new MockPageContext();
        ContextTag context = new ContextTag();
        context.setPageContext(pageContext);
        context.doStartTag();
        context.setId("TEST");
        Import.tag(pageContext, context, "list", "A,B,C", "list");

        StringListTag tag1 = new StringListTag();
        tag1.setPageContext(pageContext);
        tag1.setParent(context);
        tag1.setId("tag1");
        tag1.setReferid("list");


        tag1.doStartTag();


        tag1.doInitBody();
        for (int i = 0; i < 3; i++) {
            StringListTag tag2 = new StringListTag();
            tag2.setPageContext(pageContext);
            tag2.setParent(tag1);
            tag2.setId("tag2");
            tag2.setReferid("list");
            tag2.doStartTag();
            Import.tag(pageContext, tag1, "aaa", "AAA" + i);
            tag2.doInitBody();
            for (int j = 0; j < 3; j++) {
                Import.tag(pageContext, tag2, "bbb", "BBB" + i + "" + j);
                tag2.doAfterBody();
            }
            tag2.doEndTag();
            tag2.release();

            tag1.doAfterBody();
        }
        tag1.doEndTag();

        assertEquals("AAA2" ,  pageContext.getAttribute("aaa"));
        assertEquals("BBB22" , pageContext.getAttribute("bbb"));

        assertEquals("AAA2" ,  context.getObject("aaa"));
        assertEquals("BBB22" , context.getObject("bbb"));

        context.doEndTag();

        tag1.release();
        context.release();


    }


}
