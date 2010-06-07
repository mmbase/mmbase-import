/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import java.util.*;

/**
 * Mocks mm:import
 *
 * @version $Id$
 */

public  class Import {


    public static void tag(PageContext pageContext, ContextReferrerTag context, String key, String value) throws Exception {
        tag(pageContext, context, key, value, "string", false);
    }
    public static void tag(PageContext pageContext, ContextReferrerTag context, String key, String value, boolean reset) throws Exception {
        tag(pageContext, context, key, value, "string", reset);
    }
    public static void tag(PageContext pageContext, ContextReferrerTag context, String key, String value, String type) throws Exception {
        tag(pageContext, context, key, value, type, false);
    }
    public static void tag(PageContext pageContext, ContextReferrerTag context, String key, String value, String type, boolean reset) throws Exception {
        WriteTag i = new WriteTag();
        i.setPageContext(pageContext);
        i.setParent(context);
        i.setId(key);
        i.setVartype(type);
        i.setReset(reset);
        i.setWrite("false"); // getOut not supported in MockPageContext
        i.setValue(value);
        i.doStartTag();
        i.doEndTag();
    }
    public static void jspvar(PageContext pageContext, ContextReferrerTag context, String key, String value, String type, boolean reset) throws Exception {
        WriteTag i = new WriteTag();
        i.setPageContext(pageContext);
        i.setParent(context);
        i.setId(key);
        i.setJspvar(key);
        i.setVartype(type);
        i.setReset(reset);
        i.setWrite("false"); // getOut not supported in MockPageContext
        i.setValue(value);
        i.doStartTag();
        i.doEndTag();
    }


}
