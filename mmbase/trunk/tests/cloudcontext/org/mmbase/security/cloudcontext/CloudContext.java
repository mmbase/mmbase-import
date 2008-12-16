/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.cloudcontext;

import org.mmbase.tests.BridgeTest;
import junit.framework.TestCase;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.Casting;
import org.w3c.dom.Document;
/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 */

public class CloudContext extends BridgeTest {

    public void setUp() throws Exception  {
        startMMBase();
    }

    public void testImplementation() {
        assertEquals(org.mmbase.security.implementation.cloudcontext.Authenticate.class, getCloud().getCloudContext().getAuthentication().getClass());
    }


}

