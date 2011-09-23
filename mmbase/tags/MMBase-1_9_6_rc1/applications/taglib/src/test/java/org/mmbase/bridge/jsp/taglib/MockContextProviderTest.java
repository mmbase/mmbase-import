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
import org.springframework.mock.web.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**

 * @version $Id$
 */

public  class MockContextProviderTest {




    @Test
    public void basic() {
        PageContext pageContext = new MockPageContext();
        ContextProvider provider = new MockContextProvider(pageContext);


    }



}
