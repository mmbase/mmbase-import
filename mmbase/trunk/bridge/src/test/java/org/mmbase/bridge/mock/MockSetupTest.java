/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import javax.servlet.*;
import org.junit.*;
import org.springframework.mock.web.*;
import org.springframework.core.io.*;
import static org.junit.Assert.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class MockSetupTest  {

    @Test
    public void contextInitialized() {
        MockSetup setup = new MockSetup();
        ServletContext sx = new MockServletContext("/src/test/files/",  new FileSystemResourceLoader()) {
                @Override
                public ServletContext getContext(String uriPath) {
                    return this;
                }
            };

        ServletContextEvent sce = new ServletContextEvent(sx);
        setup.contextInitialized(sce);

    }
}