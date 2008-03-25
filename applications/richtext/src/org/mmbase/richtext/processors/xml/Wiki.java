/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.richtext.processors.xml;

import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.*;
import org.w3c.dom.*;
import javax.xml.transform.dom.*;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * Set-processing for an `mmxf' field. This is the counterpart and inverse of {@link MmxfGetString}, for more
 * information see the javadoc of that class.
 * @author Michiel Meeuwissen
 * @version $Id: Wiki.java,v 1.1 2008-03-25 18:00:14 michiel Exp $
 */

class Wiki {
    private static final Logger log = Logging.getLoggerInstance(Wiki.class);
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    Document parse(Node editedNode, Document source) {

        // TODO reolve anchors. Allow to use nodenumber as anchor.

        return source;
    }


}
