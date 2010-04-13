/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import javax.servlet.*;

/**
 * This can be added to your web.xml as a Listener to bootstrap the mock bridge.
 <pre>
  &lt;listener&gt;
    &lt;listener-class&gt;org.mmbase.bridge.mock.MockSetup&lt;/listener-class&gt;
  &lt;/listener&gt;
  </pre>
 * This arranges the MockCloudContext to be (minimalisticly) set up, so that a mock bridge is available. E.g. to test taglib or so.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class MockSetup implements ServletContextListener {

    @Override
    public void	contextDestroyed(ServletContextEvent sce) {
        MockCloudContext.getInstance().clear();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            MockCloudContext.getInstance().addCore();
        } catch (java.io.IOException ioe) {
            sce.getServletContext().log(ioe.getMessage(), ioe);
        }
    }

}

