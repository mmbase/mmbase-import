/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob;

import java.util.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.EditState;
import org.mmbase.util.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.*;

/**
 */
public class testBuilder extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(testBuilder.class);


    /**
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        log.info("executeFunction of test builder");
	log.info("node="+node);
	log.info("function="+function);
	log.info("args="+args);
	/*
        if ("mimetype".equals(function)) {
            return node.getStringValue("mimetype");
        }
	*/		
        return super.executeFunction(node, function, args);
    }

}
