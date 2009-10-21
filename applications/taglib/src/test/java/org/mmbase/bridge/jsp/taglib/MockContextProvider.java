/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;

import java.util.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import javax.servlet.jsp.PageContext;

/**
 * @version $Id: BasicBacking.java 36504 2009-06-30 12:39:45Z michiel $
 */

public  class MockContextProvider implements ContextProvider {

    private final PageContext pageContext;
    private final ContextContainer container;

    public MockContextProvider(PageContext pageContext) {
        this.pageContext = pageContext;
        container = new StandaloneContextContainer("MOCK", new HashMap<String, Object>(), true);
    }

    public ContextContainer getContextContainer()  {
        return container;
    }

    public PageContext    getPageContext() {
        return pageContext;
    }

    public String getId() {
        return "MOCK";
    }


}
