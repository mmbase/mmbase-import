/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import org.mmbase.util.functions.Parameters;
import org.mmbase.util.StringSplitter;

import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Framework functions for use in XSLT.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class Functions {
    private static final Logger log = Logging.getLoggerInstance(Functions.class);

    private Functions() {
        // this class has no instances
    }



    public static String url(Object request, String page, String block, String fw) throws FrameworkException {
        Framework framework = Framework.getInstance();
        Parameters frameworkParameters = framework.createParameters();
        frameworkParameters.setAutoCasting(true);

        if (request != null) {
            frameworkParameters.setIfDefined("request", request);
        }

        if (fw != null && ! "".equals(fw)) {
            for (Map.Entry<String, String> e : StringSplitter.map(fw).entrySet()) {
                frameworkParameters.set(e.getKey(), e.getValue());
            }
        }

        Map<String, String> params = StringSplitter.map(block);

        return framework.getUrl(page, params, frameworkParameters, false);
    }


}
