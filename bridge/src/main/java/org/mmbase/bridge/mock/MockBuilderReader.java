/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import org.mmbase.util.xml.ParentBuilderReader;
import org.mmbase.bridge.util.NodeManagerDescription;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;

/**
 * This class can read builder XML's. For the moment it's main use is to parse to a Map of DataType's, which is used by {@link MockCloudContext} to create NodeManagers.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class MockBuilderReader extends ParentBuilderReader {


    final MockCloudContext  cloudContext;

    MockBuilderReader(InputSource s, MockCloudContext cc) {
        super(s);
        this.cloudContext = cc;
        if (getRootElement().getTagName().equals("builder")) {
            resolveInheritance();
        }
    }
    MockBuilderReader(Document d, MockCloudContext cc) {
        super(d);
        this.cloudContext = cc;
        if (getRootElement().getTagName().equals("builder")) {
            resolveInheritance();
        }
    }

    @Override
    protected NodeManagerDescription getNodeManagerDescription(String parentBuilder) {
        return cloudContext.nodeManagers.get(parentBuilder);
    }


}
